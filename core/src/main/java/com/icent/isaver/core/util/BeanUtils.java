package com.icent.isaver.core.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class BeanUtils {
    public BeanUtils() {
    }

    public static void convertMapToBean(Map map, Object bean) throws InvocationTargetException, IllegalAccessException {
        if(map != null && bean != null) {
            org.apache.commons.beanutils.BeanUtils.populate(bean, map);
        }

    }

    public static <T> T convertMapToBean(Map map, Class<T> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T bean = null;
        if(map != null) {
            bean = clazz.newInstance();
            org.apache.commons.beanutils.BeanUtils.populate(bean, map);
        }

        return bean;
    }
}
