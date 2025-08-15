package com.frenzy.sso.service;


import com.frenzy.core.service.AbstractCosmosService;
import com.frenzy.sso.cache.FzCacheSsoUser;
import org.springframework.stereotype.Service;

/**
 * @author yf
 * @since 2024-11-20
 */
@Service
public class FzCacheSsoUserService extends AbstractCosmosService<FzCacheSsoUser> {

    // 构造函数中传递 FzCacheSsoUser 的类类型
    public FzCacheSsoUserService() {
        super(FzCacheSsoUser.class);
    }

}
