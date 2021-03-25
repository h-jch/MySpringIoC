package com.hjc.springframework.context;

import com.hjc.springframework.factory.BeanFactory;

/**
 * @author kei
 */
public abstract class AbstractApplicationContext implements ApplicationContext {

    /**
     * 组合方式
     * 使用BeanFactory，而不是继承或实现BeanFactory
     * 内部是一个map，存放组件名和组件信息，组件信息内有组件实例
     */
    BeanFactory beanFactory;

    @Override
    public Object getBean(Class clazz) throws Exception {
        return beanFactory.getBean(clazz);
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        return beanFactory.getBean(beanName);
    }
}
