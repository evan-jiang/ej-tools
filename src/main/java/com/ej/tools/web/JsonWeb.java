package com.ej.tools.web;

import com.alibaba.fastjson.JSON;
import com.ej.tools.dto.StringResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/json")
@Slf4j
public class JsonWeb {

    @RequestMapping(value = "/format",method = RequestMethod.POST)
    @ResponseBody()
    public Object format(String params){
        try {
            return new StringResult().genSuccess(JSON.toJSONString(JSON.parse(params),true));
        } catch (Exception e) {
            log.error("JSON格式化异常",e);
            return new StringResult().genFail(e.getMessage());
        }
    }
}
