package com.hjc.springframework.reader;

/**
 * @author kei
 */
public interface BeanDefinitionReader {

    /**
     * 从location出读取Bean的配置信息
     * @param location
     * @throws Exception
     */
    void loadBeanDefinitions(String location) throws Exception;
}
