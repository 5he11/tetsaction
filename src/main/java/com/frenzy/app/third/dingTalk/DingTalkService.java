package com.frenzy.app.third.dingTalk;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;

@Service
@Slf4j
public class DingTalkService {



    public static String secret= "SECe20632f99fd157a0361bd7c3c770171973c6741528515961c3cdeb8c601fa161"; //您的token
    public static String url= "https://oapi.dingtalk.com/robot/send?access_token=41d93f1696ec2cc2a23af74fc00be33680b994741d466d969a98c322b4ea00a5";


    public void send(String title){
        try{
            Long timestamp = System.currentTimeMillis();

            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
            System.out.println(sign);

            DingTalkReqVo reqVo = new DingTalkReqVo();
            reqVo.setMsgtype("text");
            DingTalkReqVo.DataDTO dataDTO = new DingTalkReqVo.DataDTO();
            dataDTO.setContent(title);
            reqVo.setText(dataDTO);

            url = url + "&timestamp="+timestamp+"&sign="+sign;

            String response = HttpUtil.post(url,JSON.toJSONString(reqVo));
            log.info(response);
        }catch (Exception e){
            log.error(e.getMessage());
        }



    }


    public static void sendStatic(String title){
        try{
            Long timestamp = System.currentTimeMillis();

            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
            System.out.println(sign);

            DingTalkReqVo reqVo = new DingTalkReqVo();
            reqVo.setMsgtype("text");
            DingTalkReqVo.DataDTO dataDTO = new DingTalkReqVo.DataDTO();
            dataDTO.setContent("通知："+title);
            reqVo.setText(dataDTO);

            url = url + "&timestamp="+timestamp+"&sign="+sign;

            String response = HttpUtil.post(url,JSON.toJSONString(reqVo));
            log.info(response);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }


}
