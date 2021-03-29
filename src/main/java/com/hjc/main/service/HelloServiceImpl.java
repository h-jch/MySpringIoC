package com.hjc.main.service;

import com.hjc.springframework.annotation.Scope;
import com.hjc.springframework.annotation.Service;
import com.hjc.springframework.annotation.Value;

/**
 * @author kei
 */
@Service(name = "helloService")
@Scope("prototype")
public class HelloServiceImpl implements HelloService {

    @Value("Hello, world")
    private String msg;

    @Override
    public void say() {
        System.out.println(msg);
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
