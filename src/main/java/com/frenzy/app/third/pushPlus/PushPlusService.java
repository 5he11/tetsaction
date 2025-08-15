package com.frenzy.app.third.pushPlus;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PushPlusService {

    //自己写的redis操作类
//    @Autowired
//    private RedisCache redisService;

    public static String token= "ce0b4989d2334784ad07a47864102789"; //您的token
    public static String url= "https://www.pushplus.plus/send/";


    public void send(String title, String content){
        //redis的key，可以自己随便命名
        String redisKey= "pushplus:canSend";
        //读取redis里面的值，是否为1，不为1的才能请求pushplus接口
//        Integer limit = redisService.getCacheObject(redisKey)!= null ? (Integer) redisService.getCacheObject(redisKey):0;
//        if(limit!=1){
//            String title= "标题";  //消息的标题
//            String content= "内容<br/><img src='http://www.pushplus.plus/doc/img/push.png' />";  //消息的内容

            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            map.put("title",title);
            map.put("content",content);

            //服务器发送POST请求，接收响应内容
            String response = HttpUtil.post(url,map);
            log.info(response);
            //把返回的字符串结果变成对象
            PushPlusResVo resultT = JSONUtil.toBean(response,PushPlusResVo.class);

            //判断返回码是否为900（用户账号使用受限），如果是就修改redis对象，下次请求不在发送
//            if(resultT.getCode()==900){
//                //使用redis缓存做全局判断，设置到第二天凌点自动失效
//                redisService.setCacheObject(redisKey,1, getSecondsNextEarlyMorning(), TimeUnit.SECONDS);
//            }
//        }
    }


    public static void sendStatic(String title, String content){
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            map.put("title",title);
            map.put("content",content);
            //服务器发送POST请求，接收响应内容
            String response = HttpUtil.post(url,map);
            log.info(JSON.toJSONString(response));
    }

    /**
     * 获取现在距离下一个早上的时间戳
     * @return 返回值单位为[s:秒]
     */
    public static Integer getSecondsNextEarlyMorning() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Long longTime = (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
        return longTime.intValue();
    }


}
