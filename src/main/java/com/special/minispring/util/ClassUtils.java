package com.special.minispring.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Special on 2018/8/8 10:22
 */
public class ClassUtils {

    /**
     * 将值类型数据转换为八大基础类型或者String类型
     * @param targetType
     * @param value
     * @param <T>
     * @return
     */
    public static <T> T convertPrimitiveTypeOrString(Class<T> targetType, String value) {
        if(targetType == String.class) {
            return (T) value;
        }
        try {
            Constructor<T> constructor = targetType.getConstructor(String.class);
            //调用指定参数的构造器产生对象实例
            return constructor.newInstance(value);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
