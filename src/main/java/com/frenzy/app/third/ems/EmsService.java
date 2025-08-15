package com.frenzy.app.third.ems;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.frenzy.app.third.ems.entity.GetBarCodePo;
import com.frenzy.app.third.ems.entity.PrintOrderPo;
import com.frenzy.app.third.pushPlus.PushPlusResVo;
import com.frenzy.core.config.UploadConfig;
import com.frenzy.core.thirdParty.qiniu.UploadDemo;
import com.frenzy.core.utils.yftools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.frenzy.core.config.UploadConfig.Upload_Folder_name;

@Service
@Slf4j
public class EmsService {


    public String apiCancelOrder(String orderNo, String waybillNo){
        String apiCode = "020006";
        String params = "{\n" +
                "  \"logisticsOrderNo\": \""+orderNo+"\",\n" +
                "  \"cancelReason\": \"1\",\n" +
                "  \"waybillNo\": \""+waybillNo+"\"\n" +
                "}";


        System.out.printf("请求参数：%s\n", params);
        String content = params+EmsConfig.secret;
        String encryptStr = CryptoThirdSM4Tools.sm4Encrypt(content, EmsConfig.secret);
        String ret = this.send(apiCode,encryptStr);
        return ret;
    }



    public String apiGetBarCode(String logisticsOrderId, String rcountry){
        String apiCode = "110002";
        String params = "{\"order\":[{\"ecCompanyId\":\""+EmsConfig.senderNo+"\"," +
                "\"eventTime\":\""+yftools.nowStrCn()+"\"," +
                "\"whCode\":\""+EmsConfig.whCode+"\"," +
                "\"logisticsOrderId\": \""+logisticsOrderId+"\"," +
                "\"tradeId\":\""+yftools.getRandNum(8)+"\"," +
                "\"logisticsCompany\":\"POST\"," +
                "\"logisticsBiz\":\"001\"," +
                "\"mailType\":\"TEST\"," +
                "\"faceType\":\"1\"," +
                "\"rcountry\":\""+rcountry+"\"}]}";


        System.out.printf("请求参数：%s\n", params);
        String content = params+EmsConfig.secret;
        String encryptStr = CryptoThirdSM4Tools.sm4Encrypt(content, EmsConfig.secret);
        String ret = this.send(apiCode,encryptStr);

        GetBarCodePo getBarCodePo = JSON.parseObject(ret,GetBarCodePo.class);
        return getBarCodePo.getRetBody().get(0).getBarCode();
    }



    public String apiGetPrintOrder(String waybillNo) throws IOException {
        String apiCode = "120001";
        String params = "{\"ecCompanyId\": \""+EmsConfig.senderNo+"\"," +
                "  \"ak\": \""+EmsConfig.secret+"\"," +
                "  \"barCode\": \""+waybillNo+"\"," +
                "  \"version\": \"2\"," +
                "  \"pageType\": \"RM\"}";
        String content = params+EmsConfig.secret;
        String encryptStr = CryptoThirdSM4Tools.sm4Encrypt(content, EmsConfig.secret);
        String ret = this.send(apiCode,encryptStr);
        PrintOrderPo printOrderPo = JSON.parseObject(ret,PrintOrderPo.class);

        String ffile = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_FORMATTER);
        String url = UploadDemo.main(printOrderPo.getRetBody().getData(), Upload_Folder_name+"/"+ffile+"/pdf_" + yftools.getRandNum(6) + ".pdf");//UuidUtil.get32UUID()
        JSONObject jb=JSONObject.parseObject(url);
        url=jb.getString("key");
        return UploadConfig.domain + url;
    }




    public String apiCreateOrder(String orderNo, String batchNo, String receiverName, String receiverPostCode, String receiverPhone, String receiverNation, String receiverProvince, String receiverCity, String receiverCounty, String receiverAddress, String cargoNo, String cargoNameCn, String cargoNameEn, String cargoTypeCn, String cargoTypeEn, String cargoNum, String cargoPrice, String cargoWeight, String cargoHSCode){
        String apiCode = "110001";
        String params = "{\n" +
                "  \"created_time\": \""+yftools.nowStrCn()+"\",\n" +
                "  \"sender_no\": \""+EmsConfig.senderNo+"\",\n" +
                "  \"wh_code\": \""+EmsConfig.whCode+"\",\n" +
                "  \"mailType\": \"OTHER\",\n" +
                "  \"logistics_order_no\": \""+orderNo+"\",\n" +
                "  \"batch_no\": \""+batchNo+"\",\n" +//批次号
                "  \"biz_product_no\": \"001\",\n" +
                "  \"weight\": \""+cargoWeight+"\",\n" +
                "  \"volume\": \"\",\n" +
                "  \"length\": \"\",\n" +
                "  \"width\": \"\",\n" +
                "  \"height\": \"\",\n" +
                "  \"postage_total\": \"1\",\n" +
                "  \"postage_currency\": \"USD\",\n" +
                "  \"contents_total_weight\": \""+cargoWeight+"\",\n" +
                "  \"contents_total_value\": \""+cargoPrice+"\",\n" +
                "  \"transfer_type\": \"HK\",\n" +
                "  \"battery_flag\": \"0\",\n" +
                "  \"pickup_notes\": \"\",\n" +
                "  \"insurance_flag\": \"1\",\n" +
                "  \"insurance_amount\": \"\",\n" +
                "  \"undelivery_option\": \"1\",\n" +
                "  \"back_addr\": \"\",\n" +
                "  \"back_way\": \"1\",\n" +
                "  \"declare_curr_code\": \"USD\",\n" +
                "  \"valuable_flag\": \"0\",\n" +
                "  \"declare_source\": \"1\",\n" +
                "  \"declare_type\": \"1\",\n" +
                "  \"printcode\": \"1\",\n" +
                "  \"barcode\": \"\",\n" +
                "  \"forecastshut\": \"0\",\n" +
                "  \"mail_sign\": \"2\",\n" +
                "  \"mail_flag\": \"0\",\n" +
                "  \"s_tax_id\": \"\",\n" +
                "  \"tax_id\": \"\",\n" +
                "  \"prepayment_of_vat\": \"\",\n" +
                "  \"pickup_flag\": \"\",\n" +
                "  \"sender\": {\n" +
                "    \"name\": \""+EmsConfig.senderName+"\",\n" +
                "    \"company\": \""+EmsConfig.senderCompany+"\",\n" +
                "    \"post_code\": \""+EmsConfig.senderPostCode+"\",\n" +
                "    \"phone\": \""+EmsConfig.senderPhone+"\",\n" +
                "    \"mobile\": \""+EmsConfig.senderPhone+"\",\n" +
                "    \"email\": \""+EmsConfig.senderEmail+"\",\n" +
                "    \"id_type\": \"1\",\n" +
                "    \"id_no\": \""+EmsConfig.senderIdCardNo+"\",\n" +
                "    \"nation\": \"CN\",\n" +
                "    \"province\": \""+EmsConfig.senderProvince+"\",\n" +
                "    \"city\": \""+EmsConfig.senderCity+"\",\n" +
                "    \"county\": \""+EmsConfig.senderCountry+"\",\n" +
                "    \"address\": \""+EmsConfig.senderAddress+"\",\n" +
                "    \"gis\": \"\",\n" +
                "    \"linker\": \""+EmsConfig.senderLinker+"\"\n" +
                "  },\n" +
                "  \"receiver\": {\n" +
                "    \"name\": \""+receiverName+"\",\n" +
                "    \"company\": \"\",\n" +
                "    \"post_code\": \""+receiverPostCode+"\",\n" +
                "    \"phone\": \""+receiverPhone+"\",\n" +
                "    \"mobile\": \""+receiverPhone+"\",\n" +
                "    \"email\": \"\",\n" +
                "    \"id_type\": \"1\",\n" +
                "    \"id_no\": \"\",\n" +
                "    \"nation\": \""+receiverNation+"\",\n" +
                "    \"province\": \""+receiverProvince+"\",\n" +
                "    \"city\": \""+receiverCity+"\",\n" +
                "    \"county\": \""+receiverCounty+"\",\n" +
                "    \"address\": \""+receiverAddress+"\",\n" +
                "    \"gis\": \"\",\n" +
                "    \"linker\": \""+receiverName+"\"\n" +
                "  },\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"cargo_no\": \""+cargoNo+"\",\n" +
                "      \"cargo_name\": \""+cargoNameCn+"\",\n" +
                "      \"cargo_name_en\": \""+cargoNameEn+"\",\n" +
                "      \"cargo_type_name\": \""+cargoTypeCn+"\",\n" +
                "      \"cargo_type_name_en\": \""+cargoTypeEn+"\",\n" +
                "      \"cargo_origin_name\": \"CN\",\n" +
                "      \"cargo_link\": \"\",\n" +
                "      \"cargo_quantity\": "+cargoNum+",\n" +
                "      \"cargo_value\": "+cargoPrice+",\n" +
                "      \"cost\": "+cargoPrice+",\n" +
                "      \"cargo_currency\": \"USD\",\n" +
                "      \"cargo_weight\": "+cargoWeight+",\n" +
                "      \"cargo_description\": \"1\",\n" +
                "      \"cargo_serial\": \""+cargoHSCode+"\",\n" +
                "      \"unit\": \"个\",\n" +
                "      \"intemsize\": \"\",\n" +
                "      \"carogo_weight\": \""+cargoWeight+"\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String content = params+EmsConfig.secret;
        String encryptStr = CryptoThirdSM4Tools.sm4Encrypt(content, EmsConfig.secret);
        String ret = this.send(apiCode,encryptStr);
        return ret;

    }

    public String apiTrack(String waybillNo){
        String apiCode = "040001";
        String params = "{\"waybillNo\":\""+waybillNo+"\",\"direction\":\"0\"}";
        String content = params+EmsConfig.secret;
        String encryptStr = CryptoThirdSM4Tools.sm4Encrypt(content, EmsConfig.secret);
        String ret = this.send(apiCode,encryptStr);
        return ret;

    }


    public String apiTrackSubscribe(String waybillNo){
        String apiCode = "040001";
        String params = "{\"waybillNo\":\""+waybillNo+"\",\"direction\":\"0\"}";
        String content = params+EmsConfig.secret;
        String encryptStr = CryptoThirdSM4Tools.sm4Encrypt(content, EmsConfig.secret);
        String ret = this.send(apiCode,encryptStr);
        return ret;

    }

    public String send(String apiCode, String logitcsInterface){
            Map<String,Object> map = new HashMap<>();
            map.put("apiCode",apiCode);
            map.put("senderNo",EmsConfig.senderNo);
            map.put("authorization",EmsConfig.authorization);
            map.put("msgType","0");//0-json 1-xml 默认为json
            map.put("timeStamp", yftools.nowStrCn());
            map.put("logitcsInterface",logitcsInterface);

            //服务器发送POST请求，接收响应内容
            String response = HttpUtil.post(EmsConfig.API_URL,map);
            log.info(response);
            return response;
            //把返回的字符串结果变成对象
//            PushPlusResVo resultT = JSONUtil.toBean(response,PushPlusResVo.class);

            //判断返回码是否为900（用户账号使用受限），如果是就修改redis对象，下次请求不在发送
//            if(resultT.getCode()==900){
//                //使用redis缓存做全局判断，设置到第二天凌点自动失效
//                redisService.setCacheObject(redisKey,1, getSecondsNextEarlyMorning(), TimeUnit.SECONDS);
//            }
//        }
    }


//    public static void sendStatic(String title, String content){
//            Map<String,Object> map = new HashMap<>();
//            map.put("token",token);
//            map.put("title",title);
//            map.put("content",content);
//            //服务器发送POST请求，接收响应内容
//            String response = HttpUtil.post(url,map);
//            log.info(JSON.toJSONString(response));
//    }
//
//    /**
//     * 获取现在距离下一个早上的时间戳
//     * @return 返回值单位为[s:秒]
//     */
//    public static Integer getSecondsNextEarlyMorning() {
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DAY_OF_YEAR, 1);
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        Long longTime = (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
//        return longTime.intValue();
//    }


}
