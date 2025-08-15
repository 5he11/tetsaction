package com.frenzy.app.third.lottery;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LotteryService {


    //https://www.apihz.cn/api/caipiaoshuangseqiu.html
    public static String url= "http://101.35.2.25/api/caipiao/shuangseqiu.php?id=10004998&key=2e257c80a136e7523661b1c4a55fab1c";


    public LotteryResVo send(){
        //服务器发送POST请求，接收响应内容
        String response = HttpUtil.get(url);
        log.info(response);
        //把返回的字符串结果变成对象
        LotteryResVo resultT = JSONUtil.toBean(response,LotteryResVo.class);

        return resultT;
    }






}
