package com.ej.tools.web;

import com.alibaba.fastjson.JSON;
import com.ej.tools.annotation.ToolsMethod;
import com.ej.tools.annotation.ToolsParams;
import com.ej.tools.constants.EJConstants;
import com.ej.tools.dto.StringResult;
import com.ej.tools.utils.EjUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/help")
@Slf4j
public class HelpWeb {

    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list() {
        List<Method> methods = Arrays.asList(EjUtils.class.getDeclaredMethods());

        List<Map<String, Object>> result = methods.stream()
                .filter(method -> method.getAnnotation(ToolsMethod.class) != null)
                .map(method -> {
                    ToolsMethod toolsMethod = method.getAnnotation(ToolsMethod.class);
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("name", toolsMethod.value());
                    String temp = "%s %s.%s(%s)";
                    String returnType = method.getReturnType().getName();
                    String methodName = method.getName();
                    List<String> show = Arrays.asList(method.getParameters()).stream()
                            .map(parameter -> parameter.getType().getTypeName() + " " + parameter.getName())
                            .collect(Collectors.toList());
                    map.put("method", String.format(temp, returnType, EJConstants.EJUTILS,methodName, String.join(",", show)));
                    final Map<String,Object> info = new LinkedHashMap();
                    Arrays.asList(method.getParameters()).stream().forEach(parameter -> {
                        ToolsParams toolsParams = parameter.getAnnotation(ToolsParams.class);
                        info.put(parameter.getName(),toolsParams.value());
                    });
                    map.put("paramsInfo",info);
                    return map;
                }).collect(Collectors.toList());
        return new StringResult().genSuccess(JSON.toJSONString(result, true));
    }

    public static void main(String[] args) {
        Object list = new HelpWeb().list();

        System.out.println(JSON.toJSONString(list, true));
    }
}

