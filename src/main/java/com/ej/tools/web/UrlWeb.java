package com.ej.tools.web;

import com.ej.tools.dto.StringResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/url")
@Slf4j
public class UrlWeb {

    @RequestMapping(value = "/encode",method = RequestMethod.POST)
    @ResponseBody
    public Object encode(String params){
        try {
            return new StringResult().genSuccess(java.net.URLEncoder.encode(params, "UTF-8"));
        } catch (Exception e) {
            log.error("URL编码异常",e);
            return new StringResult().genFail(e.getMessage());
        }
    }

    @RequestMapping(value = "/decode",method = RequestMethod.POST)
    @ResponseBody
    public Object decode(String params){
        try {
            return new StringResult().genSuccess(java.net.URLDecoder.decode(params, "UTF-8"));
        } catch (Exception e) {
            log.error("URL解码异常",e);
            return new StringResult().genFail(e.getMessage());
        }
    }
}
