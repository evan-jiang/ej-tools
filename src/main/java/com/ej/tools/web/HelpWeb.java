package com.ej.tools.web;

import com.alibaba.fastjson.JSON;
import com.ej.tools.dto.StringResult;
import com.ej.tools.utils.EjUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/help")
@Slf4j
public class HelpWeb {

    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list() {
        return new StringResult().genSuccess(JSON.toJSONString(EjUtils.HELP_CACHE, true));
    }

    public static void main(String[] args) {
        Object list = new HelpWeb().list();

        System.out.println(JSON.toJSONString(list, true));
    }
}

