package com.special.minispring.service;

import com.special.minispring.annotation.Service;

/**
 * Created by Special on 2018/7/26 15:41
 */
@Service
public class DemoService {

    public void add(String name) {
        System.out.println("添加成功");
    }

    public String get(String name) {
        System.out.println("get " + name);
        return "I am " + name;
    }
}
