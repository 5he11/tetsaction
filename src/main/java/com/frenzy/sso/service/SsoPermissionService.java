package com.frenzy.sso.service;

import cn.hutool.core.util.StrUtil;
import com.frenzy.core.enums.EnumYesNoType;
import com.frenzy.sso.domain.SsoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * 登录校验方法
 * 
 * @author yf
 */
@Component
public class SsoPermissionService
{

    @Autowired
    private SsoRoleService ssoRoleService;
    @Autowired
    private SsoMenuService ssoMenuService;


    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SsoUser user)
    {
        Set<String> perms = new HashSet<String>();
        // 管理员拥有所有权限
        if (StrUtil.equals(user.getFlagAdmin(),EnumYesNoType.YES.getValue()))
        {
            perms.add("*:*:*");
        }
        else
        {
            perms.addAll(ssoMenuService.selectMenuPermsByUserId(user));
        }
        return perms;
    }


    /**
     * 获取角色数据权限
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SsoUser user)
    {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (StrUtil.equals(user.getFlagAdmin(),EnumYesNoType.YES.getValue()))
        {
            roles.add("admin");
        }
        else
        {
            roles.addAll(ssoRoleService.selectRolePermissionByUserId(user.getId()));
        }
        return roles;
    }

}
