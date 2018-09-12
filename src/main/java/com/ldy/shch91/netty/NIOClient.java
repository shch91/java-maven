package com.ldy.shch91.netty;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

@Component
public class NIOClient {

    private static final Logger logger = LoggerFactory.getLogger(NIOServer.class);

    private  String host="127.0.0.1";

    private  int port=8888;

    private int BLOCK = 4096;

    private ByteBuffer sendBuffer=ByteBuffer.allocate(BLOCK);

    private ByteBuffer receiveBuffer=ByteBuffer.allocate(BLOCK);

    private Selector selector;

    public NIOClient() throws IOException{
        SocketChannel channel=SocketChannel.open();
        channel.configureBlocking(false);

        selector=Selector.open();
        channel.register(selector, SelectionKey.OP_CONNECT);
        channel.connect(new InetSocketAddress(host, port));
    }

    /**
     * 连接服务端，并进行请求
     * @throws IOException
     */
    public void connect()throws IOException{
        while(true){
            selector.select();
            handleKey(selector.selectedKeys());
            selector.selectedKeys().clear();
        }
    }

    /**
     * 对select到的key进行处理
     * @param selectionKeys
     * @throws IOException
     */
    public void handleKey(Set<SelectionKey> selectionKeys) throws IOException {
        Iterator<SelectionKey> itr=selectionKeys.iterator();
        while(itr.hasNext()){
            SelectionKey selectionKey=itr.next();
            SocketChannel client=(SocketChannel)selectionKey.channel();

            if(selectionKey.isConnectable()){
                logger.info("client connect");
                if(client.isConnectionPending()){
                    client.finishConnect();
                    logger.info("完成连接!");

                    //首次写数据
                    sendBuffer.clear();
                    sendBuffer.put("Hello,Server".getBytes());
                    sendBuffer.flip();
                    client.write(sendBuffer);
                }
                client.register(selector,SelectionKey.OP_READ);
            }else if(selectionKey.isReadable()){
                receiveBuffer.clear();

                //读取数据
                int count=client.read(receiveBuffer);
                if(count>0){
                    String receiveText=new String(receiveBuffer.array(),0,count);
                    logger.info("客户端接受服务端数据--:"+receiveText);
                    client.register(selector,SelectionKey.OP_WRITE);
                }
            }
        }
        selectionKeys.clear();
    }

    public void write(String content) throws IOException {
        out:while(true){
            selector.select();
            Set<SelectionKey> selectionKeys=selector.selectedKeys();
            Iterator<SelectionKey> itr=selectionKeys.iterator();
            while(itr.hasNext()){
                SelectionKey selectionKey=itr.next();
                if(selectionKey.isWritable()){
                    itr.remove();
                    ByteBuffer sendBuffer=ByteBuffer.allocate(BLOCK);
                    sendBuffer.put(content.getBytes());
                    sendBuffer.flip();
                    SocketChannel client=(SocketChannel)selectionKey.channel();
                    //将缓冲区各标志位复位，因为向里面put了数据，标志被改变，想从中读取数据发向服务端，就要复位
                    sendBuffer.flip();
                    client.write(sendBuffer);
                    logger.info("客户端向服务端发送数据 --:"+content);

                    break out;
                }
            }
        }
    }

    @PostConstruct
    public void start() throws IOException {
        final NIOClient client=new NIOClient();
        new Thread(new Runnable() {
            public void run() {
                try {
                    client.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        client.write("test1testtest");
        try {
            Thread.sleep(100000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("dd");
        //client.write("test2testtest");

    }
}