package com.special.minispring.servlet;

import com.special.minispring.annotation.Autowired;
import com.special.minispring.annotation.Controller;
import com.special.minispring.annotation.RequestMapping;
import com.special.minispring.annotation.Service;
import com.special.minispring.component.Handler;
import com.special.minispring.util.ClassUtils;
import com.special.minispring.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Special on 2018/7/24 15:15
 */
public class MiniDispatchServlet extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(MiniDispatchServlet.class);

    private Properties contextConfig = new Properties();

    private List<String> classNames = new ArrayList<>();

    private Map<String, Object> beanInstances = new ConcurrentHashMap<>();

//    private Map<String, Method> handlerMapping = new ConcurrentHashMap<String, Method>();
    private List<Handler> handlerMapping = new ArrayList<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /**
         * 请求转发
         */
        try {
            doDispath(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 1.加载配置文件
     * 2.扫描出所有相关联的类
     * 3.初始化IOC容器, 注册bean
     * 4.依赖注入
     * 5.初始化了HandlerMapping
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {

        doLoadConfig(getServletConfig().getInitParameter("contextConfigLocation"));

        doScanner(contextConfig.getProperty("scanPackage"));

        doInstance();

        doAutowired();

        initHandlerMapping();

    }

    /**
     * 请求分发
     * @param req
     * @param resp
     * @throws Exception
     */
    private void doDispath(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        Handler handler = getHandler(req);
        PrintWriter printWriter = resp.getWriter();
        if(handler == null) {
            /**
             * 如果没有匹配上，返回404
             */
            printWriter.write("404 not found!");
            return;
        }
        Class<?>[] paramTypes = handler.getParamTypes();
        Object[] paramValues = new Object[paramTypes.length];
        Map<String, String[]> params = req.getParameterMap();
        for(Map.Entry<String, String[]> entry : params.entrySet()) {
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]", "");
            if(handler.getParamIndexMapping().containsKey(entry.getKey())) {
                int index = handler.getParamIndexMapping().get(entry.getKey());
                /**
                 * 仅支持八大基本类型和String的转换
                 * TODO:任意类型的转换
                 */
                paramValues[index] = ClassUtils.convertPrimitiveTypeOrString(paramTypes[index], value);
            }
        }
        /**
         * 对request和response处理
         */
        int reqIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getName());
        paramValues[reqIndex] = req;
        int respIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getName());
        paramValues[respIndex] = resp;

        handler.getMethod().invoke(handler.getController(), paramValues);
    }

    /**
     * 对于给定的请求url，匹配对应的处理器
     * 类似Spring中映射器模块
     * @param req
     * @return
     */
    private Handler getHandler(HttpServletRequest req) {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for(Handler handler : handlerMapping) {
            Matcher matcher = handler.getPattern().matcher(url);
            if(matcher.matches()) {
                return handler;
            }
        }
        return null;
    }

    /**
     * 初始化映射器
     */
    private void initHandlerMapping() {
        for(Map.Entry<String, Object> entry : beanInstances.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if(clazz.isAnnotationPresent(Controller.class)) {
                String baseUrl = "";
                if(clazz.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                Method[] methods = clazz.getDeclaredMethods();
                for(Method method : methods) {
                    if(method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        String url = (baseUrl + requestMapping.value()).replaceAll("/+", "/");
                        Pattern pattern = Pattern.compile(url);
                        handlerMapping.add(new Handler(pattern, entry.getValue(), method));
                        logger.info(url + " : " + method.getName());
                    }
                }
            }
        }
    }

    /**
     * 依赖注入
     */
    private void doAutowired() {
        if(!beanInstances.isEmpty()) {
            for(Map.Entry<String, Object> entry : beanInstances.entrySet()) {
                /**
                 * 1.依赖注入
                 * 2.给@Autowired标识的域进行属性注入
                 */
                Field[] fields = entry.getValue().getClass().getDeclaredFields();
                for(Field field : fields) {
                    if(field.isAnnotationPresent(Autowired.class)) {
                        Autowired autowired = field.getAnnotation(Autowired.class);
                        String beanName = autowired.value();
                        if("".equals(beanName.trim())) {
                            field.getName();
                            beanName = field.getName();
                        }
                        /**
                         * 利用反射机制访问私有域
                         */
                        field.setAccessible(true);
                        try {
                            field.set(entry.getValue(), beanInstances.get(beanName));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 向容器注册bean
     */
    private void doInstance() {
        if (!classNames.isEmpty()) {
            try {
                for (String className : classNames) {
                    Class<?> clazz = Class.forName(className);
                    //不是所有的类都要初始化，延迟加载
                    String beanName = null;
                    if (clazz.isAnnotationPresent(Controller.class)) {
                        /**
                         * 1.默认首字母小写
                         * 2.自定义beanName, 自定义优先
                         * 3.如果是接口，将实现类对象注入到IO
                         * TODO:为接口注入实现类会出现二义性，如何解决
                         */
                        beanName = StringUtils.defaultBeanName(clazz.getSimpleName());
                        beanInstances.put(beanName, clazz.newInstance());
                        logger.info(beanName + " : " + beanInstances.get(beanName));
                    }else if(clazz.isAnnotationPresent(Service.class)) {
                        beanName = clazz.getAnnotation(Service.class).value();
                        if("".equals(beanName.trim())) {
                            beanName = StringUtils.defaultBeanName(clazz.getSimpleName());
                        }
                        Object instance = clazz.newInstance();
                        beanInstances.put(beanName, instance);
                        logger.info(beanName + " : " + instance);
                        Class<?>[] interfaces = clazz.getInterfaces();
                        for(Class<?> item : interfaces) {
                            beanInstances.put(item.getName(), instance);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 扫描出给定基础包下的所有的类
     * @param packageName
     */
    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File baseScanDir = new File(url.getFile());
        for(File classFile : baseScanDir.listFiles()) {
            if(classFile.isDirectory()) {
                doScanner(packageName + "." + classFile.getName());
            } else {
                String str = packageName + "." + classFile.getName();
                String className = str.replace(".class", "");
                logger.debug(className);
                classNames.add(className);
            }
        }
    }

    /**
     * 加载配置文件
     * 此处硬编码为Properties
     * TODO:灵活的支持各种配置文件
     * @param location
     */
    private void doLoadConfig(String location) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
