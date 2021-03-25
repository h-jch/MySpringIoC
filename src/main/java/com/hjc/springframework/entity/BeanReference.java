package com.hjc.springframework.entity;

/**
 * bean属性为引用的类型定义
 *
 * @author kei
 */
public class BeanReference {

    private String name;
    private Object bean;

    public BeanReference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }
}
