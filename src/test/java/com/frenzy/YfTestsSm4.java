package com.frenzy;


import com.frenzy.app.third.ems.CryptoThirdSM4Tools;
import com.frenzy.app.third.ems.EmsConfig;
import com.frenzy.app.third.ems.EmsService;
import com.frenzy.core.thirdParty.rabbitMq.EnumRabbitMethod;
import com.frenzy.core.thirdParty.rabbitMq.RabbitService;
import com.frenzy.core.utils.yftools;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;


@SpringBootTest
@Slf4j
public class YfTestsSm4 {

    @Autowired
    private EmsService emsService;
    @Autowired
    private RabbitService rabbitService;

    @Test
    public void sendMsg(){
        rabbitService.sendMsg(EnumRabbitMethod.RabbitMethod_test.getValue(), "orders", yftools.getOrder("A"));
    }



    @Test
    public void apiCreateOrder(){
        String encryptStr = emsService.apiCreateOrder("LZ292339827CN", "", "Wen Han", "1050", "+64276116666", "NZ", "Auckland", "Auckland", "Remuera", "3/15 Ely Avenue", "741810", "手套", "gloves", "手套", "gloves", "1", "1", "40", "741810");
        System.out.printf("请求后后数据：%s\n", encryptStr);
//        {"serialNo":"6331a8f6-567f-4359-9d60-9f6be79ddaf2","retCode":"00000","retMsg":"成功交运订单","retBody":"{\"success\":\"true\",\"waybillNo\":\"LZ292346788CN\"}","retDate":"","success":true}
    }


    @Test
    public void apiCancelOrder(){
        String encryptStr = emsService.apiCancelOrder("LZ292339827CN", "LZ292346788CN");
        System.out.printf("请求后后数据：%s\n", encryptStr);
    }

    @Test
    public void apiGetBarCode(){
        String encryptStr = emsService.apiGetBarCode(yftools.getOrder("1"), "NZ");
        System.out.printf("请求后后数据：%s\n", encryptStr);
    }

    @Test
    public void apiGetPrintOrder() throws IOException {
        String encryptStr = emsService.apiGetPrintOrder("LZ292346788CN");
        System.out.printf("请求后后数据：%s\n", encryptStr);
//        https://cimg.rongsheng.c8b.com.cn/f/20250812/pdf_2816443.pdf
//        https://cimg.rongsheng.c8b.com.cn/f/20250813/pdf_8579776.pdf
    }

    @Test
    public void init1(){
        String encryptStr = emsService.apiTrack("LZ290346175CN");
        System.out.printf("请求后后数据：%s\n", encryptStr);

//        {"serialNo":"ee60deb4-dfdb-4c98-a8a3-65846bdaf850","retCode":"00000","retMsg":"查询结果正常返回！","retBody":"{\"responseItems\":[{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-08 17:36:24\",\"opOrgProvName\":\"上海市\",\"opOrgCity\":\"上海市\",\"opOrgCode\":\"20131505\",\"opOrgName\":\"上海市浦东新区康桥揽投部\",\"opCode\":\"203\",\"opName\":\"收寄计费信息\",\"opDesc\":\"中国邮政 已收取邮件\",\"productName\":\"e邮宝\"},{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-08 17:42:05\",\"opOrgProvName\":\"上海市\",\"opOrgCity\":\"上海市\",\"opOrgCode\":\"20131505\",\"opOrgName\":\"上海市浦东新区康桥揽投部\",\"opCode\":\"303\",\"opName\":\"揽投配发\",\"opDesc\":\"邮件已在【上海市浦东新区康桥揽投部】完成分拣，准备发出\",\"productName\":\"e邮宝\"},{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-08 18:24:53\",\"opOrgProvName\":\"上海市\",\"opOrgCity\":\"上海市\",\"opOrgCode\":\"20131505\",\"opOrgName\":\"上海市浦东新区康桥揽投部\",\"opCode\":\"305\",\"opName\":\"揽投发运/封车\",\"opDesc\":\"邮件离开【上海市浦东新区康桥揽投部】，正在发往【上海国际】\",\"productName\":\"e邮宝\"},{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-09 05:11:15\",\"opOrgProvName\":\"上海市\",\"opOrgCity\":\"上海市\",\"opOrgCode\":\"20000414\",\"opOrgName\":\"上海王港邮件处理中心\",\"opCode\":\"954\",\"opName\":\"邮件到达处理中心\",\"opDesc\":\"邮件到达【上海王港邮件处理中心】\",\"productName\":\"e邮宝\"},{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-09 05:11:16\",\"opOrgProvName\":\"上海市\",\"opOrgCity\":\"上海市\",\"opOrgCode\":\"20000414\",\"opOrgName\":\"上海王港邮件处理中心\",\"opCode\":\"989\",\"opName\":\"离开处理中心\",\"opDesc\":\"邮件离开【上海王港邮件处理中心】，正在发往下一站\",\"productName\":\"e邮宝\"},{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-09 07:36:44\",\"opOrgProvName\":\"上海市\",\"opOrgCity\":\"上海市\",\"opOrgCode\":\"20120300\",\"opOrgName\":\"上海市国际互换局\",\"opCode\":\"954\",\"opName\":\"邮件到达处理中心\",\"opDesc\":\"邮件到达【上海市国际互换局】\",\"productName\":\"e邮宝\"},{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-10 14:53:56\",\"opOrgProvName\":\"上海市\",\"opOrgCity\":\"上海市\",\"opOrgCode\":\"20120300\",\"opOrgName\":\"上海市国际互换局\",\"opCode\":\"EXA\",\"opName\":\"送交出口海关\",\"opDesc\":\"送交出口海关\",\"productName\":\"e邮宝\"},{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-10 15:14:49\",\"opOrgProvName\":\"上海市\",\"opOrgCity\":\"上海市\",\"opOrgCode\":\"20120300\",\"opOrgName\":\"上海市国际互换局\",\"opCode\":\"EXC\",\"opName\":\"出口海关/放行\",\"opDesc\":\"出口海关/放行\",\"productName\":\"e邮宝\"},{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-10 15:15:19\",\"opOrgProvName\":\"上海市\",\"opOrgCity\":\"上海市\",\"opOrgCode\":\"20120300\",\"opOrgName\":\"上海市国际互换局\",\"opCode\":\"423\",\"opName\":\"出口邮件直封封发\",\"opDesc\":\"【上海市国际互换局】已出口直封\",\"productName\":\"e邮宝\"},{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-10 19:04:19\",\"opOrgProvName\":\"上海市\",\"opOrgCity\":\"上海市\",\"opOrgCode\":\"20120300\",\"opOrgName\":\"上海市国际互换局\",\"opCode\":\"989\",\"opName\":\"离开处理中心\",\"opDesc\":\"邮件离开【上海市国际互换局】，正在发往下一站\",\"productName\":\"e邮宝\"},{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-11 01:34:59\",\"opOrgProvName\":\"上海市\",\"opOrgCity\":\"上海市\",\"opOrgCode\":\"20000062\",\"opOrgName\":\"上海市国际交换站\",\"opCode\":\"404\",\"opName\":\"交航扫描\",\"opDesc\":\"已交承运商运输\",\"productName\":\"e邮宝\"},{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-11 09:16:00\",\"opOrgCity\":\"\",\"opOrgCode\":\"3\",\"opCode\":\"500\",\"opName\":\"航空公司接收\",\"opDesc\":\"航空公司接收\",\"productName\":\"e邮宝\"},{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-11 13:27:00\",\"opOrgCity\":\"\",\"opOrgCode\":\"CX\",\"opCode\":\"457\",\"opName\":\"航空公司启运\",\"opDesc\":\"航空公司启运\",\"productName\":\"e邮宝\"},{\"traceNo\":\"LZ290346175CN\",\"waybillNo\":\"LZ290346175CN\",\"opTime\":\"2025-08-11 18:24:00\",\"opOrgCity\":\"\",\"opOrgCode\":\"CX\",\"opCode\":\"505\",\"opName\":\"飞机到达进港\",\"opDesc\":\"飞机进港，飞机进港（到达中转地）\",\"productName\":\"e邮宝\"}]}","retDate":"2025-08-12 18:26:06","success":true}

    }




    @Test
    public void init(){
        String params = "{\"language\":\"zh-CN\",\"orderId\":\"QIAO-20200618-004\"}";
        String key = "TvaBgrhE46sft3nZlfe7xw==";
        String content = params+key;
        String encryptStr = CryptoThirdSM4Tools.sm4Encrypt(content, key);
        System.out.printf("加密后数据：%s\n", encryptStr);
    }



    @Test
    public void sm4Decrypt(){
        String params = "|$4|kF9KM/Iuq04gYlfM8qjxCO3+dLFPovkCiEPsFRG85WLSu/eR/nuAccmh7ra1kAzUz1nPWcupVeKZAAr4I5oFaSeyBDjGt7mECFGdy7NfqLfLJXiEDiU92H6D8xdp2Vgw4ju8N1vYJLuYLck2jAaCLFyy0uPfTZjhFEWteNoY0cv/dNQxMwR936U4hMbrf5bBL351/5kQoNePLJoKkLQwI6O3Z60o64/jySoKg6cehfnM4vwEbiRlxvNA1WJk0Q0VB4lFnIoPgOyFkxOEB3JkoIq2kc3exCrp40bcD7no1OfT/3s0/aZUUAUS4UwNAEWrn9FmJsuHXDwKY+doqXkCX83K8U867PRDfTUJvQ70Jx8/yYGjTalkF3kDOrYGn+GtHLBEvQlKRbznE2/EEOqKHwbWIsUoxM1mP/LS9v9sQ1j/3TsEJSj3dpDpn52CXqXdVJCM1r88WVIS9oeI9AxthSC/b0CbeKSxe7P/E0Zf39Rd3o5OoDMLbD+wyN37Xx7w";
        String key = "QkZWQnlSWGYyU0pTZno1NQ==";
        String content = params;
        String encryptStr = CryptoThirdSM4Tools.sm4Decrypt(content, key);
        System.out.printf("解密后数据：%s\n", encryptStr);
    }






}
