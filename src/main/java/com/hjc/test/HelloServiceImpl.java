package com.hjc.test;

/**
 * @author kei
 */
public class HelloServiceImpl implements HelloService {

    private String msg;

    @Override
    public void say() {
        System.out.println(msg);
    }
}
