package com.frenzy.core.thirdParty.rabbitMq;

import org.springframework.stereotype.Component;

/**
 * @author zip
 * @since 2022/3/4 15:51
 */
@Component
public class MqConst {

    public static final String Queue = "TestDirectQueue";
    public static final String Exchange = "TestDirectExchange";
    public static final String Routing = "TestDirectRouting";
    public static final String lonelyDirectExchange = "lonelyDirectExchange";


}
