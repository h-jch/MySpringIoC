package com.hjc.springframework.entity;

/**
 * Bean的定义信息
 *
 * @author kei
 */
public class BeanDefinition {

    /**
     * 组件实例
     */
    private Object bean;

    /**
     * 组件类型
     */
    private Class beanClass;

    /**
     * 组件类型名
     */
    private String beanClassName;

    /**
     * 组件是否是单例
     */
    private boolean singleton;

    /**
     * 组件的属性信息
     */
    private PropertyValues propertyValues;

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    /**
     * 对BeanClassName赋值，并初始化对应的class，没找到相应的class则捕获异常
     * @param beanClassName
     */
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
        try {
            this.beanClass = Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    /**
     * 如果组件没有属性，就返回一个空的PropertyValues，防止空指针异常
     * @return
     */
    public PropertyValues getPropertyValues() {
        if (propertyValues == null) {
            propertyValues = new PropertyValues();
        }
        return propertyValues;
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }
}
