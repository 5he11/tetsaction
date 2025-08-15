package com.frenzy.sso.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.frenzy.core.service.AbstractCosmosService;
import com.frenzy.core.config.CosmosConfig;
import com.frenzy.core.constant.UserConstants;
import com.frenzy.core.exception.ServiceException;
import com.frenzy.core.utils.StringUtils;
import com.frenzy.core.utils.YFLocalDateTimeUtil;
import com.frenzy.sso.domain.SsoPost;
import com.frenzy.sso.domain.SsoUser;
import com.frenzy.sso.mapper.SsoUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * 登录校验方法
 * 
 * @author yf
 */
@Component
public class SsoPostService extends AbstractCosmosService<SsoPost>
{

    public SsoPostService() {
        super(SsoPost.class);
    }


    @Autowired
    private SsoUserMapper userMapper;

    /**
     * 查询岗位信息集合
     *
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    public List<SsoPost> selectPostList(SsoPost post)
    {
        String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        if (StrUtil.isNotEmpty(post.getPostCode())){
            sql = sql + "AND c.postCode like concat('%', '"+post.getPostCode()+"', '%')";
        }
        if (StrUtil.isNotEmpty(post.getPostName())){
            sql = sql + "AND c.postName like concat('%', '"+post.getPostName()+"', '%')";
        }
        if (StrUtil.isNotEmpty(post.getStatus())){
            sql = sql + "AND c.status  = '"+post.getStatus()+"' ";
        }
        return this.list(sql);
    }

    /**
     * 查询所有岗位
     *
     * @return 岗位列表
     */
    public List<SsoPost> selectPostAll()
    {
        return this.list("select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'");
    }

    /**
     * 通过岗位ID查询岗位信息
     *
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    public SsoPost selectPostById(String postId)
    {
        return this.getById(postId);
    }

    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    public List<SsoPost> selectPostListByUserId(String userId)
    {
        SsoUser ssoUser = userMapper.getById(userId);
        if (CollectionUtil.isNotEmpty(ssoUser.getRoleIds())){
            String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
            sql = sql + "AND c.id in "+this.getListStringInSql(ssoUser.getPostIds());
            return this.list(sql);
        }
        return new ArrayList<>();
    }

    /**
     * 校验岗位名称是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    public String checkPostNameUnique(SsoPost post)
    {
        String postId = StringUtils.isNull(post.getId()) ? "-1" : post.getId();
        SsoPost info = this.getOne("select TOP 1 * from c where c.postName = '"+post.getPostName()+"' c.partitionKey = '"+this.getPartitionKeyFromItem()+"'");
        if (StringUtils.isNotNull(info) && !StrUtil.equals(info.getId(),postId))
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验岗位编码是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    
    public String checkPostCodeUnique(SsoPost post)
    {
        String postId = StringUtils.isNull(post.getId()) ? "-1" : post.getId();
        SsoPost info = this.getOne("select TOP 1 * from c where c.postCode = '"+post.getPostCode()+"' c.partitionKey = '"+this.getPartitionKeyFromItem()+"'");
        if (StringUtils.isNotNull(info) && !StrUtil.equals(info.getId(),postId))
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     * @return 结果
     */
    public int countUserPostById(String postId)
    {
        SsoUser ssoUser = userMapper.getOne("select TOP 1 * from c where ARRAY_CONTAINS(c.postIds, '"+postId+"') and c.partitionKey = '"+userMapper.getPartitionKeyFromItem()+"'");
        if (ssoUser != null){
            return 1;
        }
        return 0;
    }

    /**
     * 删除岗位信息
     *
     * @param postId 岗位ID
     * @return 结果
     */
    public void deletePostById(String postId)
    {
        this.delete(postId);
    }

    /**
     * 批量删除岗位信息
     *
     * @param postIds 需要删除的岗位ID
     * @return 结果
     * @throws Exception 异常
     */
    public void deletePostByIds(String[] postIds)
    {
        for (String postId : postIds)
        {
            SsoPost post = this.selectPostById(postId);
            if (countUserPostById(postId) > 0)
            {
                throw new ServiceException(String.format("%1$s已分配,不能删除", post.getPostName()));
            }
        }
        for (String postId : postIds)
        {
            this.delete(postId);
        }
    }

    /**
     * 新增保存岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    public SsoPost insertPost(SsoPost post)
    {
        post.setKeyName(post.getKeyName());
        post.setId(UUID.fastUUID().toString());
        post.setCreateTime(YFLocalDateTimeUtil.now());
        post.setPartitionKey(CosmosConfig.getPartitionKeyPrefix() + post.getKeyName());
        return this.save(post);
    }

    /**
     * 修改保存岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    public void updatePost(SsoPost post)
    {
        post.setUpdateTime(LocalDateTimeUtil.now().toString());
        this.update(post);
    }

}
