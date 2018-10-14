package com.special.minispring.controller;

import com.special.minispring.annotation.Controller;
import com.special.minispring.annotation.RequestMapping;
import com.special.minispring.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Create by Special on 2018/4/2 13:56
 */
@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

//    @RequestMapping("/")
//    public String index(HttpSession session){
//        logger.info("Visit Index");
//        return "Hello Word! " + session.getAttribute("msg")
//                + "<br> Say: " + touTiaoService.say();
//    }
//
//    @RequestMapping(value = {"/profile/{groupId}/{userId}"})
//    public String profile(@PathVariable("groupId") String groupId,
//                          @PathVariable("userId") int userId,
//                          @RequestParam(value = "key", defaultValue = "nowcoder") String key,
//                          @RequestParam(value = "type", defaultValue = "1") int type){
//        return String.format("GID{%s}, UID{%d}, THPE{%d}, KEY{%s}", groupId, userId, type, key);
//    }
//
//    @RequestMapping(value = {"/ftl"})
//    public String news(Model model){
//        model.addAttribute("name", "范扬");
//        // asList的形参是多变量形参，故可以直接传入多值
//        List<String> colors = Arrays.asList("RED", "GREEN", "BLUE");
//        Map<String, String> map = new HashMap<>();
//        for(int i = 0; i < 3; i++){
//            map.put(String.valueOf(i), String.valueOf(i * i));
//        }
//        model.addAttribute("colors", colors);
//        model.addAttribute("map", map);
//        model.addAttribute("user", new User("Yang"));
//        return "news";
//    }

    @RequestMapping("/request")
    public void request(HttpServletRequest request,
                          HttpServletResponse response){
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name + " : " + request.getHeader(name) + "<br>");
        }
        sb.append("getMethod: " + request.getMethod() + "<br>");
        sb.append("getPathInfo: " + request.getPathInfo() + "<br>");
        sb.append("getQueryString: " + request.getQueryString() + "<br>");
        sb.append("getRequestURI: " + request.getRequestURI() + "<br>");
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //CookieValue注解表明这个形参与HTTP中Cookie绑定，并且它会从Cookie读取该字段的值
    @RequestMapping("/response")
    public String response(String nowcoderId,
                           @RequestParam(value = "key") String key,
                           @RequestParam(value = "value") String value,
                           HttpServletResponse response){
        response.addCookie(new Cookie(key, value));
        response.addHeader(key, value);
        return "NowCoderId From Cookie: " + nowcoderId;
    }

//    @RequestMapping("/redirect/{code}")
//    public RedirectView redirectView(@PathVariable("code") int code){
//        RedirectView red = new RedirectView("/", true);
//        //默认为302 临时性转移
//        if(code == 301){
//            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
//        }
//        return red;
//    }
//
//    /**
//     * 原生的临时跳转方式
//     * @return
//     */
//    @RequestMapping("/origin/redirect")
//    public String redirect(HttpSession session){
//        // redirect 为原生的跳转方式，方式为临时转移，且不可变
//        session.setAttribute("msg", "Jump from redirect");
//        return "redirect:/";
//    }
//
//    @RequestMapping("/admin")
//    @ResponseBody
//    public String admin(@RequestParam(value = "key", required = false) String key){
//        if("admin".equals(key)){
//            return "Hello Admin";
//        }
//        throw new IllegalArgumentException("Key 错误");
//    }
//
//    @ExceptionHandler
//    @ResponseBody
//    public String error(Exception e){
//        return "error: " + e.getMessage();
//    }
}
