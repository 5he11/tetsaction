package com.frenzy.app.service;

import com.alibaba.fastjson.JSON;
import com.frenzy.FrenzyConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

@Component
@Slf4j
public class TestService {


    @Autowired
    private FrenzyConfig frenzyConfig;

    public String actionHello(){
        log.info("用户请求【柄图详情】接口");
        log.debug("用户请求【柄图详情】接口");
        log.error("用户请求【柄图详情】接口");

        HashMap<String,String> map = new HashMap<>();
        map.put("SysUrl=",frenzyConfig.getSysUrl());
        String res = "Hello, World!"+ JSON.toJSONString(map);
        return res;
    }
}
