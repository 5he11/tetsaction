package com.frenzy.sso.mapper;

import com.frenzy.core.service.AbstractCosmosService;
import com.frenzy.sso.domain.SsoRole;
import org.springframework.stereotype.Component;


/**
 * 登录校验方法
 * 
 * @author yf
 */
@Component
public class SsoRoleMapper extends AbstractCosmosService<SsoRole>
{

    public SsoRoleMapper() {
        super(SsoRole.class);
    }




}
