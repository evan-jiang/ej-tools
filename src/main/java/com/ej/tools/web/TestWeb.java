package com.ej.tools.web;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestWeb {

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test(String params){
        System.out.println(params);
        return params;
    }
}
