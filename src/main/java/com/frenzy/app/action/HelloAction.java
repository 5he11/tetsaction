package com.frenzy.app.action;

import com.frenzy.app.service.TestService;
import com.frenzy.core.entity.AjaxResult;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class HelloAction {

    @Autowired
    public TestService testService;



    @ApiOperation(value = "actionHello")
    @FunctionName("actionHello")
    public Object actionHello(
            @HttpTrigger(name = "httpRequest", route = "actionHello", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        long startTime = System.currentTimeMillis();
        log.info("User request <actionHello> API start.");
        String res = testService.actionHello();
        log.info("User request <actionHello> API succeed,use time {}ms.", System.currentTimeMillis() - startTime);
        return AjaxResult.success(request, "",res);
    }


    @ApiOperation(value = "actionHello1")
    @FunctionName("actionHello1")
    public Object actionHello1(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS, route = "actionHello1") HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        Long startTime = System.currentTimeMillis();
        context.getLogger().info("User request <actionHello> API start.");
        String res = testService.actionHello();
        context.getLogger().info("User request <actionHello> API succeed,use time "+(System.currentTimeMillis() - startTime)+"ms.");
        return AjaxResult.success(request, "",res);
    }

}
