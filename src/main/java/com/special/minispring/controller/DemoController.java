package com.special.minispring.controller;

import com.special.minispring.annotation.Autowired;
import com.special.minispring.annotation.Controller;
import com.special.minispring.annotation.RequestMapping;
import com.special.minispring.annotation.RequestParam;
import com.special.minispring.service.DemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Special on 2018/7/26 15:30
 */
@Controller
public class DemoController {

    @Autowired
    DemoService demoService;

    @RequestMapping("/")
    public String index() {
        return "test success!";
    }

    @RequestMapping("/add")
    public void add(HttpServletRequest req, HttpServletResponse resp,
                    @RequestParam("name") String name) {
        demoService.add(name);
    }
}
