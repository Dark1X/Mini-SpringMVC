package com.special.minispring.util;

/**
 * Created by Special on 2018/8/7 13:57
 */
public class StringUtils {

    public static String defaultBeanName(String className) {
        char[] chars = className.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }

}
