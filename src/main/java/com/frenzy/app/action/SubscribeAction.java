package com.frenzy.app.action;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.frenzy.app.domain.ThirdApiLog;
import com.frenzy.app.service.TestService;
import com.frenzy.app.service.ThirdApiLogService;
import com.frenzy.app.third.dingTalk.DingTalkService;
import com.frenzy.app.third.ems.*;
import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.thirdParty.rabbitMq.EnumRabbitMethod;
import com.frenzy.core.thirdParty.rabbitMq.RabbitService;
import com.frenzy.core.utils.yftools;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class SubscribeAction {


    @Autowired
    public ThirdApiLogService thirdApiLogService;
    @Autowired
    public DingTalkService dingTalkService;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private EmsService emsService;


    @ApiOperation(value = "printOrder")
    @FunctionName("printOrder")
    public Object printOrder(
            @HttpTrigger(name = "httpRequest", route = "printOrder", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id, // 绑定路径参数
            final ExecutionContext context) throws IOException {
        Long startTime = System.currentTimeMillis();
        context.getLogger().info("User request <actionHello> API start.");
        String pdfUrl = emsService.apiGetPrintOrder(id);
        rabbitService.sendMsg(EnumRabbitMethod.RabbitMethod_ems_print_order.getValue(), "orders", pdfUrl);
        context.getLogger().info("User request <actionHello> API succeed,use time "+(System.currentTimeMillis() - startTime)+"ms.");
        return AjaxResult.success(request, "","");


    }





    @ApiOperation(value = "sendmsg")
    @FunctionName("sendmsg")
    public Object sendmsg(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS, route = "sendmsg") HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        Long startTime = System.currentTimeMillis();
        context.getLogger().info("User request <actionHello> API start.");
        rabbitService.sendMsg(EnumRabbitMethod.RabbitMethod_ems_print_order.getValue(), "orders", "https://cimg.rongsheng.c8b.com.cn/f/20250813/pdf_8579776.pdf");
        context.getLogger().info("User request <actionHello> API succeed,use time "+(System.currentTimeMillis() - startTime)+"ms.");
        return AjaxResult.success(request, "","");
    }



    @ApiOperation(value = "subscribePost")
    @FunctionName("subscribePost")
    public Object subscribePost(
            @HttpTrigger(name = "httpRequest", route = "subscribePost", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SubscribePush>> request,
            final ExecutionContext context) {
        long startTime = System.currentTimeMillis();
        log.info("User request <actionHello> API start.");

        SubscribePush subscribePush = request.getBody().get();

//        String key = "QkZWQnlSWGYyU0pTZno1NQ==";
        String key = EmsConfig.secret;
        String encryptStr = CryptoThirdSM4Tools.sm4Decrypt(subscribePush.getLogitcsInterface(), key);
        SubscribePushDecrypt subscribePushDecrypt = JSON.parseObject(encryptStr,SubscribePushDecrypt.class);

        ThirdApiLog thirdApiLog = new ThirdApiLog();
        thirdApiLog.setContent(JSON.toJSONString(subscribePush));
        thirdApiLog.setOpCode(subscribePushDecrypt.getOpCode());
        thirdApiLog.setOrderNo(subscribePushDecrypt.getOrderNo());
        thirdApiLog.setOpTime(subscribePushDecrypt.getOpTime());
        thirdApiLog.setOpName(subscribePushDecrypt.getOpName());
        thirdApiLog.setOpDesc(subscribePushDecrypt.getOpDesc());
        thirdApiLog.setOperatorNo(subscribePushDecrypt.getOperatorNo());
        thirdApiLog.setOpCode(subscribePushDecrypt.getOpCode());
        thirdApiLog.setOpOrgName(subscribePushDecrypt.getOpOrgName());
        thirdApiLog.setOpOrgProvName(subscribePushDecrypt.getOpOrgProvName());
        thirdApiLog.setOperatorName(subscribePushDecrypt.getOperatorName());
        thirdApiLog.setOpOrgCity(subscribePushDecrypt.getOpOrgCity());
        thirdApiLog.setWaybillNo(subscribePushDecrypt.getWaybillNo());
        thirdApiLog.setCreateTime(yftools.nowStrCn());
        thirdApiLog.setNewValue2();
        thirdApiLogService.save(thirdApiLog);

        dingTalkService.send(subscribePushDecrypt.getOpDesc());

        log.info("User request <actionHello> API succeed,use time {}ms.", System.currentTimeMillis() - startTime);

        Map<String,Object> map = new HashMap<>();
        map.put("serialNo",subscribePush.getSerialNo());
        map.put("code", "00000");//00000-表示成功S00001-表示推送失败
        map.put("codeMessage","");
        map.put("senderNo",EmsConfig.senderNo);

//        return AjaxResult.success(request, "",res);
        return map;
    }




}
