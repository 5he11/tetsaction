//package com.frenzy.core.thirdParty.rabbitMq;
//
//import com.alibaba.fastjson.JSON;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitHandler;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//@RabbitListener(queues = MqConst.Queue)//监听的队列名称 TestDirectQueue
//public class DirectReceiver {
//
//    @RabbitHandler
//    public void process(RabbitVo rabbitVo) {
//        System.out.println("DirectReceiver消费者收到消息  : " + JSON.toJSONString(rabbitVo));
//
//
//
//
//      if (rabbitVo.getMethod().equals(EnumRabbitMethod.RabbitMethod_test.getValue())){
//            try {
//                Thread.sleep(10000);
//                System.out.println("RabbitMethod_test ok : " + rabbitVo.getRelatedId());
//            }catch (Exception e){
//                log.error(e.getMessage());
//            }
//        }
//    }
//
//}
