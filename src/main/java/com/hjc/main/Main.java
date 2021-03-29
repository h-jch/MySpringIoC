package com.hjc.main;

import com.hjc.springframework.context.ApplicationContext;
import com.hjc.springframework.context.ClassPathXmlApplicationContext;
import com.hjc.main.service.HelloService;
import com.hjc.main.service.WrappedService;

/**
 * @author kei
 */
public class Main {

    public static void testXml() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
        WrappedService wrappedService = (WrappedService) context.getBean("wrappedService");
        wrappedService.say();
        HelloService helloService1 = (HelloService) context.getBean("helloService");
        HelloService helloService2 = (HelloService) context.getBean("helloService");
        System.out.println(helloService1 == helloService2);
    }

    public static void testAnnotation() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("application-annotation.xml");
        WrappedService wrappedService = (WrappedService) context.getBean("wrappedService");
        wrappedService.say();
        HelloService helloService1 = (HelloService) context.getBean("helloService");
        HelloService helloService2 = (HelloService) context.getBean("helloService");
        System.out.println(helloService1 == helloService2);
    }

    public static void main(String[] args) throws Exception {
//        testXml();
        testAnnotation();
    }
}
