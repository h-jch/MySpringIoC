package com.hjc.springframework.context;

/**
 * 应用程序上下文接口
 *
 * @author kei
 */
public interface ApplicationContext {

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
}
