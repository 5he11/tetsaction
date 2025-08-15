package com.frenzy.sso.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.frenzy.core.service.AbstractCosmosService;
import com.frenzy.core.config.CosmosConfig;
import com.frenzy.core.constant.UserConstants;
import com.frenzy.core.enums.EnumYesNoType;
import com.frenzy.core.exception.ServiceException;
import com.frenzy.core.utils.StringUtils;
import com.frenzy.core.utils.YFLocalDateTimeUtil;
import com.frenzy.sso.domain.SsoRole;
import com.frenzy.sso.mapper.SsoUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.frenzy.sso.domain.SsoUser;
import java.util.*;


/**
 * 登录校验方法
 * 
 * @author yf
 */
@Component
public class SsoRoleService extends AbstractCosmosService<SsoRole>
{

    public SsoRoleService() {
        super(SsoRole.class);
    }


    @Autowired
    private SsoUserMapper userMapper;


    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<SsoRole> selectRoleList(SsoRole role)
    {
        String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        if (StrUtil.isNotEmpty(role.getId())){
            sql = sql + "AND c.id  = '"+role.getId()+"' ";
        }
        if (StrUtil.isNotEmpty(role.getRoleName())){
            sql = sql + "AND c.roleName like concat('%', '"+role.getRoleName()+"', '%')";
        }
        if (StrUtil.isNotEmpty(role.getRoleKey())){
            sql = sql + "AND c.roleKey like concat('%', '"+role.getRoleKey()+"', '%')";
        }
        if (StrUtil.isNotEmpty(role.getStatus())){
            sql = sql + "AND c.status  = '"+role.getStatus()+"' ";
        }
        return this.list(sql);
    }

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<SsoRole> selectRolesByUserId(String userId)
    {
        List<SsoRole> userRoles = this.selectRoleListByUserId(userId);
        List<SsoRole> roles = selectRoleAll();
        for (SsoRole role : roles)
        {
            for (SsoRole userRole : userRoles)
            {
                if (StrUtil.equals(role.getId(),userRole.getId()))
                {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectRolePermissionByUserId(String userId)
    {
        List<SsoRole> perms = this.selectRoleListByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SsoRole perm : perms)
        {
            if (StringUtils.isNotNull(perm))
            {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    public List<SsoRole> selectRoleAll()
    {
        String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        return this.list(sql);
    }

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    public List<SsoRole> selectRoleListByUserId(String userId)
    {
        SsoUser ssoUser = userMapper.getById(userId);
        if (CollectionUtil.isNotEmpty(ssoUser.getRoleIds())){
            String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
            sql = sql + "AND c.id in "+this.getListStringInSql(ssoUser.getRoleIds());
            return this.list(sql);
        }
        return new ArrayList<>();
    }

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    public SsoRole selectRoleById(String roleId)
    {
        return this.getById(roleId);
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    public String checkRoleNameUnique(SsoRole role)
    {
        SsoRole info = this.checkRoleNameUnique(role.getRoleName());
        if (StringUtils.isNotNull(info) && !StrUtil.equals(info.getId(),role.getId()))
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    public String checkRoleKeyUnique(SsoRole role)
    {
        SsoRole info = this.checkRoleKeyUnique(role.getRoleKey());
        if (StringUtils.isNotNull(info) && !StrUtil.equals(info.getId(),role.getId()))
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }


    public SsoRole checkRoleNameUnique(String roleName)
    {
        String sql = "select TOP 1 * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        sql = sql + "AND c.roleName like concat('%', '"+roleName+"', '%')";
        return this.getOne(sql);
    }

    public SsoRole checkRoleKeyUnique(String roleKey)
    {
        String sql = "select TOP 1 * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        sql = sql + "AND c.roleKey like concat('%', '"+roleKey+"', '%')";
        return this.getOne(sql);
    }
    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    public void checkRoleAllowed(SsoRole role)
    {
        if (StringUtils.isNotNull(role.getId()) && StrUtil.equals(role.getAdminFlag(), EnumYesNoType.YES.getValue()))
        {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    public void checkRoleDataScope(String roleId,SsoUser ssoUser)
    {
        if (!StrUtil.equals(ssoUser.getFlagAdmin(),EnumYesNoType.YES.getValue())){
            SsoRole role = new SsoRole();
            role.setId(roleId);
            List<SsoRole> roles = this.selectRoleList(role);
            if (StringUtils.isEmpty(roles))
            {
                throw new ServiceException("没有权限访问角色数据！");
            }
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    public int countUserRoleByRoleId(String roleId)
    {
        return userMapper.getUserByRoleId(roleId).size();
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public void insertRole(SsoRole role)
    {

        role.setKeyName(role.getKeyName());
        role.setId(UUID.fastUUID().toString());
        role.setCreateTime(YFLocalDateTimeUtil.now());
        role.setPartitionKey(CosmosConfig.getPartitionKeyPrefix() + role.getKeyName());
        this.save(role);
    }

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public void updateRole(SsoRole role)
    {
        // 修改角色信息
        role.setUpdateTime(LocalDateTimeUtil.now().toString());
        this.update(role);
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    public void updateRoleStatus(SsoRole role)
    {
        role.setUpdateTime(LocalDateTimeUtil.now().toString());
        this.update(role);
    }

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public void authDataScope(SsoRole role)
    {
        // 修改角色信息
        role.setUpdateTime(LocalDateTimeUtil.now().toString());
        this.update(role);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    public void insertRoleMenu(SsoRole role)
    {
        role.setUpdateTime(LocalDateTimeUtil.now().toString());
        this.update(role);
    }

    /**
     * 新增角色部门信息(数据权限)
     *
     * @param role 角色对象
     */
    public void insertRoleDept(SsoRole role)
    {
        role.setUpdateTime(LocalDateTimeUtil.now().toString());
        this.update(role);
    }

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    public void deleteRoleById(String roleId)
    {
        this.delete(roleId);
    }

    /**
     * 批量删除角色信息
     *
     * @param roleId 需要删除的角色ID
     * @return 结果
     */
    public void deleteRoleByIds(String roleId)
    {
//        for (String roleId : roleIds)
//        {
            checkRoleAllowed(this.getById(roleId));
            SsoRole role = this.getById(roleId);
            if (countUserRoleByRoleId(roleId) > 0)
            {
                throw new ServiceException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
            this.delete(roleId);
//        }
    }

    /**
     * 取消授权用户角色
     *
     * @param roleId 用户和角色关联信息
     * @return 结果
     */
    public void deleteAuthUser(String roleId, String userId)
    {
        SsoUser ssoUser = userMapper.getById(userId);
        if (ssoUser.getRoleIds().contains(roleId)){
            ssoUser.getRoleIds().remove(roleId);
            userMapper.updateUser(ssoUser);
        }
    }

    /**
     * 批量取消授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    public void deleteAuthUsers(String roleId, String[] userIds)
    {
        for (String userId : userIds)
        {
            SsoUser ssoUser = userMapper.getById(userId);
            if (ssoUser.getRoleIds().contains(roleId)){
                ssoUser.getRoleIds().remove(roleId);
                userMapper.updateUser(ssoUser);
            }
        }
    }

    /**
     * 批量选择授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    public void insertAuthUsers(String roleId, String[] userIds)
    {
        // 新增用户与角色管理
        for (String userId : userIds)
        {
            SsoUser ssoUser = userMapper.getById(userId);
            if (!ssoUser.getRoleIds().contains(roleId)){
                ssoUser.getRoleIds().add(roleId);
                userMapper.updateUser(ssoUser);
            }
        }
    }



}
