package com.hjc.springframework.factory;

import com.hjc.springframework.entity.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kei
 */
public abstract class AbstractBeanFactory implements BeanFactory {

    /**
     * 容器本身，存储bean组件名和BeanDefinition
     * key = 组件名
     * value = bean的定义信息
     */
    public Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /**
     * 通过类型获取组件
     * @param clazz
     * @return
     * @throws Exception
     */
    @Override
    public Object getBean(Class clazz) throws Exception {
        BeanDefinition beanDefinition = null;
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            Class beanClass = entry.getValue().getBeanClass();
            // clazz是beanClass 或者 clazz是beanClass的父类或者接口
            if (clazz == beanClass || clazz.isAssignableFrom(beanClass)) {
                beanDefinition = entry.getValue();
            }
        }
        if (beanDefinition == null) {
            return null;
        }
        // 组件是多例模式 或者 还未创建
        if (!beanDefinition.isSingleton() || beanDefinition.getBean() == null) {
            return doCreateBean(beanDefinition);
        } else {
            // 已经创建过，直接返回
            return beanDefinition.getBean();
        }
    }

    /**
     * 通过组件名获取组件
     * @param beanName
     * @return
     * @throws Exception
     */
    @Override
    public Object getBean(String beanName) throws Exception {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            return null;
        }
        // 组件是多例模式 或者 还没创建
        if (!beanDefinition.isSingleton() || beanDefinition.getBean() == null) {
            return doCreateBean(beanDefinition);
        } else {
            // 已经创建过，直接返回
            return beanDefinition.getBean();
        }
    }

    /**
     * 对bean的定义信息进行注册，将bean的定义信息放入map中
     * @param name
     * @param beanDefinition
     * @throws Exception
     */
    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
        beanDefinitionMap.put(name, beanDefinition);
    }

    /**
     * 对容器内的Bean统一创建
     * @throws Exception
     */
    public void populateBeans() throws Exception {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            doCreateBean(entry.getValue());
        }
    }

    /**
     * 真正创建Bean实例的方法，留给子类实现
     * @param beanDefinition
     * @return
     * @throws Exception
     */
    public abstract Object doCreateBean(BeanDefinition beanDefinition) throws Exception;
}
