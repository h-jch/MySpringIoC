package com.hjc.test;

import com.hjc.springframework.context.ApplicationContext;
import com.hjc.springframework.context.ClassPathXmlApplicationContext;

/**
 * @author kei
 */
public class Main {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
        HelloController helloController = (HelloController) context.getBean("helloController");
        helloController.say();
        HelloService helloService1 = (HelloService) context.getBean("helloService");
        HelloService helloService2 = (HelloService) context.getBean("helloService");
        System.out.println(helloService1 == helloService2);
    }
}
