package com.hjc.springframework.factory;

import com.hjc.springframework.entity.BeanDefinition;
import com.hjc.springframework.entity.BeanReference;
import com.hjc.springframework.entity.PropertyValue;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 可以自动注入属性的BeanFactory
 *
 * @author kei
 */
public class AutowiredCapableBeanFactory extends AbstractBeanFactory {

    /**
     * 根据BeanDefinition创建bean实例
     * @param beanDefinition
     * @return
     * @throws Exception
     */
    @Override
    public Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
        // 如果单例bean且已经实例化了，直接返回bean
        if (beanDefinition.isSingleton() && beanDefinition.getBean() != null) {
            return beanDefinition.getBean();
        }
        // 创建新的空实例
        Object bean = beanDefinition.getBeanClass().newInstance();
        // 放入对应的BeanDefinition中
        if (beanDefinition.isSingleton()) {
            beanDefinition.setBean(bean);
        }
        // 对bean对象进行属性赋值
        applyPropertyValues(bean, beanDefinition);
        return bean;
    }

    /**
     * 对空的Bean对象进行属性赋值，递归赋值
     * @param bean
     * @param beanDefinition
     * @throws Exception
     */
    private void applyPropertyValues(Object bean, BeanDefinition beanDefinition) throws Exception {
        List<PropertyValue> propertyValueList = beanDefinition.getPropertyValues().getPropertyValues();
        for (PropertyValue propertyValue : propertyValueList) {
            // 获取类的属性名
            Field field = bean.getClass().getDeclaredField(propertyValue.getName());
            // 获取相应的属性值
            Object value = propertyValue.getValue();
            // 如果属性值为引用，先实例化被引用的bean
            if (value instanceof BeanReference) {
                BeanReference beanReference = (BeanReference) value;
                // 优先按照自定义名称进行匹配
                BeanDefinition refBeanDefinition = beanDefinitionMap.get(beanReference.getName());

                if (refBeanDefinition != null) {
                    if (refBeanDefinition.isSingleton() || refBeanDefinition.getBean() == null) {
                        value = doCreateBean(refBeanDefinition);
                    } else {
                        value = refBeanDefinition.getBean();
                    }
                } else {
                    Class clazz = Class.forName(beanReference.getName());
                    for (BeanDefinition definition : beanDefinitionMap.values()) {
                        if(clazz.isAssignableFrom(definition.getBeanClass())) {
                            if(!definition.isSingleton() || definition.getBean() == null) {
                                value = doCreateBean(definition);
                            } else {
                                value = definition.getBean();
                            }
                        }
                    }
                }
            }
            if (value == null) {
                throw new RuntimeException("无法注入");
            }
            field.setAccessible(true);
            field.set(bean, value);
        }
    }
}
