package com.special.minispring.controller;

import com.special.minispring.annotation.Autowired;
import com.special.minispring.annotation.Controller;
import com.special.minispring.annotation.RequestMapping;
import com.special.minispring.annotation.RequestParam;
import com.special.minispring.service.DemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Special on 2018/7/26 15:30
 */
@Controller
public class DemoController {

    @Autowired
    DemoService demoService;

    @RequestMapping("/")
    public void index(HttpServletRequest req, HttpServletResponse resp) {
        try {
            PrintWriter printWriter = resp.getWriter();
            printWriter.write("test successsing!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/add")
    public void add(HttpServletRequest req, HttpServletResponse resp,
                    @RequestParam("name") String name) {
        String result = demoService.get(name);
        try {
            PrintWriter printWriter = resp.getWriter();
            printWriter.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
