package com.special.minispring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
        Proxy.newProxyInstance(Test.class.getClassLoader(), str.getClass().getInterfaces(), new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                method.invoke(proxy, args);
                return proxy;
            }
        });
    }
}
