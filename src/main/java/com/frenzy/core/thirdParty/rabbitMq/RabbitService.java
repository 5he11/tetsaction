package com.frenzy.core.thirdParty.rabbitMq;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class RabbitService {

    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    public void sendMsg(String method, String relatedForm,String relatedId){
        RabbitVo rabbitVo = new RabbitVo();
        rabbitVo.setMethod(method);
        rabbitVo.setRelatedForm(relatedForm);
        rabbitVo.setRelatedId(relatedId);
        rabbitVo.setRelatedForm2("");
        rabbitVo.setRelatedId2("");
        rabbitVo.setMessageId(String.valueOf(UUID.randomUUID()));
        rabbitVo.setCreateTime(LocalDateTimeUtil.now());
        rabbitTemplate.convertAndSend(MqConst.Exchange, MqConst.Routing, JSON.toJSONString(rabbitVo));


//        Map<String,Object> map=new HashMap<>();
//        map.put("method",method);
//        map.put("relatedForm",relatedForm);
//        map.put("relatedId",relatedId);
//        map.put("relatedForm2","");
//        map.put("relatedId2","");
//        map.put("messageId",String.valueOf(UUID.randomUUID()));
////        map.put("messageData",messageData);
//        map.put("createTime",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
//        rabbitTemplate.convertAndSend(MqConst.Exchange, MqConst.Routing, map);
    }



}
