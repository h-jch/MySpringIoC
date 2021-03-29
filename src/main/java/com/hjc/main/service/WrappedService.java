package com.hjc.main.service;

import com.hjc.springframework.annotation.Autowired;
import com.hjc.springframework.annotation.Component;

/**
 * @author kei
 */
@Component(name = "wrappedService")
public class WrappedService {

    @Autowired
    private HelloService helloService;

    public void say() {
        helloService.say();
    }
}
