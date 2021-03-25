package com.hjc.springframework.reader;

import com.hjc.springframework.entity.BeanDefinition;
import com.hjc.springframework.io.ResourceLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kei
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    /**
     * 读取xml文件后暂存BeanDefinition信息
     */
    private Map<String, BeanDefinition> registry;

    private ResourceLoader resourceLoader;

    public AbstractBeanDefinitionReader(ResourceLoader resourceLoader) {
        this.registry = new HashMap<>();
        this.resourceLoader = resourceLoader;
    }

    public Map<String, BeanDefinition> getRegistry() {
        return registry;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
