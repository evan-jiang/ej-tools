package com.ej.tools.web;

import com.alibaba.fastjson.JSON;
import com.ej.tools.dto.StringResult;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groovy")
@Slf4j
public class GroovyWeb {

    private static final String PARAMS = "params";
    private static final String SCRIPT = "script";

    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    @ResponseBody
    public Object execute(String params, String script) {
        Object obj = null;
        try {
            obj = JSON.parseObject(params);
        } catch (Exception e) {
            obj = params;
        }
        try {
            Binding groovyBinding = new Binding();
            groovyBinding.setVariable(PARAMS, obj);
            GroovyShell groovyShell = new GroovyShell(groovyBinding);
            Script spt = groovyShell.parse(script);
            Object object = spt.run();
            return new StringResult().genSuccess((object instanceof String || object instanceof Number) ? object.toString() : JSON.toJSONString(object, true));
        } catch (Exception e) {
            log.error("GROOVY执行异常", e);
            return new StringResult().genFail(e.getMessage());
        }
    }
}
