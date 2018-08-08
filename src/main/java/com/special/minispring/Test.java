package com.special.minispring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * Created by Special on 2018/8/7 15:54
 */
public class Test {

    public static void main(String[] args) {
        try {
            Class clazz = Class.forName("com.special.minispring.annotation.Autowired");
            System.out.println(clazz.getName());
            System.out.println(clazz.getSimpleName());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String str = "2223class";
        String result = str.replace(".class", "");
        System.out.println(result);
        String[] strs = new String[]{"222"};
        System.out.println(Arrays.toString(strs));
    }
}
