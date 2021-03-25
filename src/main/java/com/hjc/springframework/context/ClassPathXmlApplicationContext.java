package com.hjc.springframework.context;

import com.hjc.springframework.entity.BeanDefinition;
import com.hjc.springframework.factory.AbstractBeanFactory;
import com.hjc.springframework.factory.AutowiredCapableBeanFactory;
import com.hjc.springframework.io.ResourceLoader;
import com.hjc.springframework.reader.XmlBeanDefinitionReader;

import java.util.Map;

/**
 * 容器读取xml配置文件生成bean
 *
 * @author kei
 */
public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

    private final Object startupShutdownMonitor = new Object();
    private String location;

    public ClassPathXmlApplicationContext(String location) throws Exception {
        super();
        this.location = location;
        refresh();
    }

    public void refresh() throws Exception {
        synchronized (startupShutdownMonitor) {
            AbstractBeanFactory beanFactory = obtainBeanFactory();
            prepareBeanFactory(beanFactory);
            // 将AbstractApplicationContext内的beanFactory指向新创建的BeanFactory，表示IOC容器创建完成
            this.beanFactory = beanFactory;
        }
    }

    /**
     * 准备BeanFactory，利用BeanFactory内的BeanDefinitionMap，对每个BeanDefinition进行处理，生成实例
     * @param beanFactory
     * @throws Exception
     */
    private void prepareBeanFactory(AbstractBeanFactory beanFactory) throws Exception {
        beanFactory.populateBeans();
    }

    /**
     * 内部使用AutowiredCapableBeanFactory
     * 新建一个AutowiredCapableBeanFactory，对这个BeanFactory内的BeanDefinitionMap进行填充
     * @return
     * @throws Exception
     */
    private AbstractBeanFactory obtainBeanFactory() throws Exception {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        // 加载完xml文件后，生成一个Map暂存BeanDefinition信息
        beanDefinitionReader.loadBeanDefinitions(location);
        // 使用AutowiredCapableBeanFactory
        AbstractBeanFactory beanFactory = new AutowiredCapableBeanFactory();
        // 将暂存的BeanDefinition信息放入BeanFactory的BeanDefinitionMap中
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitionReader.getRegistry().entrySet()) {
            beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }
        return beanFactory;
    }
}
