package com.special.minispring.component;

import com.special.minispring.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Special on 2018/8/7 16:44
 */
public class Handler {

    private Object controller;
    private Method method;
    private Pattern pattern;
    private Class<?>[] paramTypes;
    private Map<String, Integer> paramIndexMapping;

    public Handler(Pattern pattern, Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;

        paramIndexMapping = new HashMap<>();
        putParamIndexMapping(method);
    }

    public void putParamIndexMapping(Method method) {
        Annotation[][] params = method.getParameterAnnotations();
        for(int i = 0; i < params.length; i++) {
            for(Annotation annotation : params[i]) {
                if(annotation instanceof RequestParam) {
                    String requestParamName = ((RequestParam) annotation).value();
                    if(!"".equals(requestParamName.trim())) {
                        paramIndexMapping.put(requestParamName, i);
                    }
                }
            }
        }
        paramTypes = method.getParameterTypes();
        for(int i = 0; i < params.length; i++) {
            Class<?> type = paramTypes[i];
            if(type == HttpServletRequest.class ||
                    type == HttpServletResponse.class) {
                paramIndexMapping.put(type.getName(), i);
            }
        }
     }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Map<String, Integer> getParamIndexMapping() {
        return paramIndexMapping;
    }

    public void setParamIndexMapping(Map<String, Integer> paramIndexMapping) {
        this.paramIndexMapping = paramIndexMapping;
    }
}
