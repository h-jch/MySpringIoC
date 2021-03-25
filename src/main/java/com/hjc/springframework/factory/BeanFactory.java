package com.hjc.springframework.factory;

import com.hjc.springframework.entity.BeanDefinition;

/**
 * Bean工厂接口
 *
 * @author kei
 */
public interface BeanFactory {

    /**
     * 通过类型获取组件
     * @param clazz
     * @return
     * @throws Exception
     */
    Object getBean(Class clazz) throws Exception;

    /**
     * 通过组件名获取组件
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;

    /**
     * 注册bean的定义
     * @param name
     * @param beanDefinition
     * @throws Exception
     */
    void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception;
}
