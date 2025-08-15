package com.frenzy.app.service;

import java.util.List;
import com.frenzy.app.domain.ThirdApiLog;
import com.frenzy.core.service.AbstractCosmosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 三方api记录Service接口
 * 
 * @author yannkeynes
 * @date 2025-08-12 16:08:14
 */
@Slf4j
@Component
public class ThirdApiLogService extends AbstractCosmosService<ThirdApiLog>
{
    public ThirdApiLogService() {
        super(ThirdApiLog.class);
    }
}
