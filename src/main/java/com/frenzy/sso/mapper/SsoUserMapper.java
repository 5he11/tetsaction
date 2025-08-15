package com.frenzy.sso.mapper;


import cn.hutool.core.date.LocalDateTimeUtil;
import com.frenzy.core.service.AbstractCosmosService;
import com.frenzy.sso.domain.SsoUser;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yf
 * @since 2024-11-20
 */
@Component
public class SsoUserMapper extends AbstractCosmosService<SsoUser> {


    public SsoUserMapper() {
        super(SsoUser.class);
    }

    /**
     * 查询部门全部用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    public List<SsoUser> getDeptUserList(String deptId)
    {
        return this.list("select * from c where c.deptId = '"+deptId+"' and c.partitionKey = '"+this.getPartitionKeyFromItem()+"'");
    }


    /**
     * 通过角色ID查询该角色得用户列表
     *
     * @param roleId 角色ID
     * @return 结果
     */
    public List<SsoUser> getUserByRoleId(String roleId)
    {
        return this.list("select * from c where ARRAY_CONTAINS(c.roleIds, '"+roleId+"') and c.partitionKey = '"+this.getPartitionKeyFromItem()+"'");
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public void updateUser(SsoUser user)
    {
        user.setUpdateTime(LocalDateTimeUtil.now().toString());
        this.update(user);
    }

}
