package com.hjc.springframework.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * bean的所有属性名和属性值
 *
 * @author kei
 */
public class PropertyValues {

    private final List<PropertyValue> propertyValueList = new ArrayList<>();

    public PropertyValues() {}

    public void addPropertyValue(PropertyValue propertyValue) {
        propertyValueList.add(propertyValue);
    }

    public List<PropertyValue> getPropertyValues() {
        return propertyValueList;
    }
}
