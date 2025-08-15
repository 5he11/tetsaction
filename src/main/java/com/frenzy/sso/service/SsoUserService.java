package com.frenzy.sso.service;


import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.frenzy.core.service.AbstractCosmosService;
import com.frenzy.core.constant.UserConstants;
import com.frenzy.core.enums.EnumYesNoType;
import com.frenzy.core.exception.ServiceException;
import com.frenzy.core.utils.StringUtils;
import com.frenzy.core.utils.YFLocalDateTimeUtil;
import com.frenzy.sso.domain.SsoDept;
import com.frenzy.sso.domain.SsoPost;
import com.frenzy.sso.domain.SsoRole;
import com.frenzy.sso.domain.SsoUser;
import com.frenzy.sso.mapper.SsoUserMapper;
import com.microsoft.azure.functions.HttpRequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yf
 * @since 2024-11-20
 */
@Service
public class SsoUserService extends AbstractCosmosService<SsoUser> {


    public SsoUserService() {
        super(SsoUser.class);
    }

    @Autowired
    public TokenService tokenService;
    @Autowired
    public SsoDeptService deptService;
    @Autowired
    public SsoRoleService roleService;
    @Autowired
    public SsoPostService postService;
    @Autowired
    public SsoUserMapper userMapper;

    public SsoUser getUserByUserName(String userName){
        String sql = "SELECT TOP 1 * FROM c where c.userName = '"+userName+"' and c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        SsoUser ssoUser = this.getOne(sql);
        return ssoUser;
    }

    public <T> SsoUser getLoginUser(HttpRequestMessage<Optional<T>> request){
        return tokenService.getLoginUser(request).getUser();
    }


    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SsoUser> selectUserList(SsoUser user)
    {
        String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        if (StrUtil.isNotEmpty(user.getId())){
            sql = sql + "AND c.id = '"+user.getId()+"' ";
        }

        if (StrUtil.isNotEmpty(user.getUserName())){
            sql = sql + " AND c.userName like concat('%', '"+user.getUserName()+"', '%')";
        }
        if (StrUtil.isNotEmpty(user.getPhonenumber())){
            sql = sql + " AND c.phonenumber like concat('%', '"+user.getPhonenumber()+"', '%')";
        }

        if (StrUtil.isNotEmpty(user.getDeptId())){
            List<SsoDept> deptList = deptService.selectChildrenDeptById(user.getDeptId());
            List<String> ids = deptList.stream().map(SsoDept::getId).toList();
            if (CollectionUtils.isEmpty(ids)){
                sql = sql + " AND (c.deptId = '"+user.getDeptId()+"')";
            }else{
                sql = sql + " AND (c.deptId = '"+user.getDeptId()+"' or c.deptId in "+getListStringInSql(ids)+")";
            }

        }

        return this.list(sql);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SsoUser> selectAllocatedList(SsoUser user)
    {
        String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        sql = sql + " and ARRAY_LENGTH(c.roleIds) > 0";
        if (StrUtil.isNotEmpty(user.getUserName())){
            sql = sql + "AND c.userName like concat('%', '"+user.getUserName()+"', '%')";
        }
        if (StrUtil.isNotEmpty(user.getPhonenumber())){
            sql = sql + "AND c.phonenumber like concat('%', '"+user.getPhonenumber()+"', '%')";
        }
        return this.list(sql);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SsoUser> selectUnallocatedList(SsoUser user)
    {
        String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        sql = sql + " and ARRAY_LENGTH(c.roleIds) = 0";
        if (StrUtil.isNotEmpty(user.getUserName())){
            sql = sql + "AND c.userName like concat('%', '"+user.getUserName()+"', '%')";
        }
        if (StrUtil.isNotEmpty(user.getPhonenumber())){
            sql = sql + "AND c.phonenumber like concat('%', '"+user.getPhonenumber()+"', '%')";
        }
        return this.list(sql);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    public SsoUser selectUserByUserName(String userName)
    {
        String sql = "select TOP 1 * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        sql = sql + "AND c.userName like concat('%', '"+userName+"', '%')";
        return this.getOne(sql);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public SsoUser selectUserById(String userId)
    {
        return this.getById(userId);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userId 用户名
     * @return 结果
     */
    public String selectUserRoleGroup(String userId)
    {
        List<SsoRole> list = roleService.selectRoleListByUserId(userId);
        if (CollectionUtils.isEmpty(list))
        {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SsoRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userId 用户名
     * @return 结果
     */
    public String selectUserPostGroup(String userId)
    {
        List<SsoPost> list = postService.selectPostListByUserId(userId);
        if (CollectionUtils.isEmpty(list))
        {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SsoPost::getPostName).collect(Collectors.joining(","));
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    public String checkUserNameUnique(String userName)
    {
        String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        sql = sql + "AND c.userName = '"+userName+"' ";
        List<SsoUser> userList = this.list(sql);
        if (!CollectionUtils.isEmpty(userList))
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    public String checkPhoneUnique(SsoUser user)
    {
        String sql = "select TOP 1 * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        sql = sql + "AND c.phonenumber = '"+user.getPhonenumber()+"' ";
        SsoUser info = this.getOne(sql);
        if (StringUtils.isNotNull(info) && !StrUtil.equals(info.getId(), user.getId()))
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    public String checkEmailUnique(SsoUser user)
    {
        String sql = "select TOP 1 * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        sql = sql + "AND c.email = '"+user.getEmail()+"' ";
        SsoUser info = this.getOne(sql);
        if (StringUtils.isNotNull(info) && !StrUtil.equals(info.getId(), user.getId()))
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    public void checkUserAllowed(SsoUser user)
    {
        if (StringUtils.isNotNull(user.getId()) && StrUtil.equals(user.getFlagAdmin(), EnumYesNoType.YES.getValue()))
        {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    public void checkUserDataScope(String userId,SsoUser ssoUser)
    {
        if (!StrUtil.equals(ssoUser.getFlagAdmin(),EnumYesNoType.YES.getValue()))
        {
            SsoUser user = new SsoUser();
            user.setId(userId);
            List<SsoUser> users = this.selectUserList(user);
            if (StringUtils.isEmpty(users))
            {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }

    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public void insertUser(SsoUser user)
    {
        user.setKeyName(user.getKeyName());
        user.setId(UUID.fastUUID().toString());
        user.setCreateTime(YFLocalDateTimeUtil.now());
        this.save(user);
    }

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public void registerUser(SsoUser user)
    {
        user.setKeyName(user.getKeyName());
        user.setId(UUID.fastUUID().toString());
        user.setCreateTime(YFLocalDateTimeUtil.now());
        this.save(user);
    }



    /**
     * 用户授权角色
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    public void insertUserAuth(String userId, String[] roleIds)
    {
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    public void updateUserStatus(SsoUser user)
    {
        user.setUpdateTime(LocalDateTimeUtil.now().toString());
        this.update(user);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public void updateUserProfile(SsoUser user)
    {
        user.setUpdateTime(LocalDateTimeUtil.now().toString());
        this.update(user);
    }

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    public void updateUserAvatar(String userName, String avatar)
    {
        SsoUser ssoUser = this.getUserByUserName(userName);
        ssoUser.setAvatar(avatar);
        ssoUser.setUpdateTime(LocalDateTimeUtil.now().toString());
        this.update(ssoUser);
    }

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    public void resetPwd(SsoUser user)
    {
        user.setUpdateTime(LocalDateTimeUtil.now().toString());
        this.update(user);
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    public void resetUserPwd(String userName, String password)
    {
        SsoUser ssoUser = this.getUserByUserName(userName);
        ssoUser.setPassword(password);
        ssoUser.setUpdateTime(LocalDateTimeUtil.now().toString());
        this.update(ssoUser);
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SsoUser user)
    {
        List<String> roles = user.getRoleIds();
        if (StringUtils.isNotNull(roles))
        {
            // 新增用户与角色管理
            for (String roleId : roles)
            {
                if (!user.getRoleIds().contains(roleId)){
                    user.getRoleIds().add(roleId);
                }
            }
            userMapper.updateUser(user);
        }
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(SsoUser user)
    {
        List<String> posts = user.getPostIds();
        if (StringUtils.isNotNull(posts))
        {
            // 新增用户与岗位管理
            for (String postId : posts)
            {
                if (!user.getPostIds().contains(postId)){
                    user.getPostIds().add(postId);
                }
            }
            userMapper.updateUser(user);
        }
    }

    /**
     * 新增用户角色信息
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(String userId, String[] roleIds)
    {
        if (StringUtils.isNotNull(roleIds))
        {
            SsoUser ssoUser = this.getById(userId);
            // 新增用户与角色管理
            for (String roleId : roleIds)
            {
                if (!ssoUser.getRoleIds().contains(roleId)){
                    ssoUser.getRoleIds().add(roleId);
                }
            }
            userMapper.updateUser(ssoUser);
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    public void deleteUserById(String userId)
    {
        this.delete(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    public void deleteUserByIds(String[] userIds)
    {
        for (String userId : userIds)
        {
            checkUserAllowed(this.getById(userId));
            this.delete(userId);
        }
    }


}
