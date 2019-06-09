/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package shch91.service.impl;

import com.alibaba.dubbo.rpc.RpcContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shch91.inter.DemoService;
import shch91.request.User;

import java.util.HashSet;
import java.util.Set;


@Slf4j
@Component("demoService")
public class DemoServiceImpl implements DemoService {

    @Override
    public User sayHello(String name) {

        log.info("Hello " + name + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());

        User u=new User();
        u.setName("shch91");
        u.setAge(678);
        u.setAddress("fsfdsafas");
        return  u;
    }

    @Override
    public Set<Integer> getSetInteger() {
        Set<Integer> result=new HashSet<Integer>();
        result.add(1);
        result.add(34);
        return  result;

    }

}
