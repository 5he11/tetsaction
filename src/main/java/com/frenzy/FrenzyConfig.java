package com.frenzy;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zip
 * @since 2022/3/4 15:51
 */
@Component
@Data
@ConfigurationProperties(prefix = "app")
public class FrenzyConfig {

    public String sysUrl;
    public static boolean addressEnabled;
    public boolean captchaOn;
    public String captchaType;

}
