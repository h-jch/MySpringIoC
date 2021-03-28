package com.hjc.test;

import com.hjc.springframework.annotation.Autowired;
import com.hjc.springframework.annotation.Controller;

/**
 * @author kei
 */
@Controller(name = "helloController")
public class HelloController {

    @Autowired
    private HelloService helloService;

    public void say() {
        helloService.say();
    }
}
