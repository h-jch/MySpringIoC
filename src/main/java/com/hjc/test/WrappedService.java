package com.hjc.test;

import com.hjc.springframework.annotation.Autowired;
import com.hjc.springframework.annotation.Component;

/**
 * @author kei
 */
@Component()
public class WrappedService {

    @Autowired
    private HelloService helloService;

    public void say() {
        helloService.say();
    }
}
