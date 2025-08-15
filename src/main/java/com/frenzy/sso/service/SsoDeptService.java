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
import com.frenzy.core.utils.yftools;
import com.frenzy.sso.domain.SsoDept;
import com.frenzy.sso.domain.SsoRole;
import com.frenzy.sso.domain.SsoUser;
import com.frenzy.sso.entity.TreeSelect;
import com.frenzy.sso.entity.po.SsoLoginUser;
import com.frenzy.sso.mapper.SsoRoleMapper;
import com.frenzy.sso.mapper.SsoUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 登录校验方法
 * 
 * @author yf
 */
@Component
public class SsoDeptService extends AbstractCosmosService<SsoDept>
{

    public SsoDeptService() {
        super(SsoDept.class);
    }


    @Autowired
    private SsoUserMapper userMapper;
    @Autowired
    private SsoRoleMapper roleMapper;

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<SsoDept> selectDeptList(SsoDept dept)
    {
        String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        if (StrUtil.isNotEmpty(dept.getId())){
            sql = sql + "AND c.id  = '"+dept.getId()+"' ";
        }
        if (StrUtil.isNotEmpty(dept.getParentId())){
            sql = sql + "AND c.parentId  = '"+dept.getParentId()+"' ";
        }
        if (StrUtil.isNotEmpty(dept.getDeptName())){
            sql = sql + "AND c.deptName like concat('%', '"+dept.getDeptName()+"', '%')";
        }
        if (StrUtil.isNotEmpty(dept.getStatus())){
            sql = sql + "AND c.status  = '"+dept.getStatus()+"' ";
        }
        return this.list(sql);
    }

    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    public List<SsoDept> buildDeptTree(List<SsoDept> depts)
    {
        List<SsoDept> returnList = new ArrayList<SsoDept>();
        List<String> tempList = new ArrayList<String>();
        for (SsoDept dept : depts)
        {
            tempList.add(dept.getId());
        }
        for (Iterator<SsoDept> iterator = depts.iterator(); iterator.hasNext();)
        {
            SsoDept dept = (SsoDept) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId()))
            {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildDeptTreeSelect(List<SsoDept> depts)
    {
        List<SsoDept> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    public List<SsoDept> selectDeptListByRoleId(String roleId)
    {
        SsoRole role = roleMapper.getById(roleId);
        String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        sql = sql + " AND c.id in "+getListStringInSql(role.getDeptIds());
        return this.list(sql);
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    public SsoDept selectDeptById(String deptId)
    {
        return this.getById(deptId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    public int selectNormalChildrenDeptById(String deptId)
    {
        List<SsoDept> list = this.list("select * from c where c.status = 0 CONTAINS(',' + c.ancestors + ',', ',"+deptId+",') and c.partitionKey = '"+this.getPartitionKeyFromItem()+"'");
        return list.size();
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public boolean hasChildByDeptId(String deptId)
    {
        List<SsoDept> list = this.list("select * from c where c.parentId = '"+deptId+"' and c.partitionKey = '"+this.getPartitionKeyFromItem()+"'");
        return list.size() > 0;
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkDeptExistUser(String deptId)
    {
        List<SsoUser> list = userMapper.getDeptUserList(deptId);
        return list.size() > 0;
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    public String checkDeptNameUnique(SsoDept dept)
    {
        SsoDept info = this.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        if (StringUtils.isNotNull(info) && !StrUtil.equals(info.getId(), dept.getId()))
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    public SsoDept checkDeptNameUnique(String deptName, String parentId)
    {
        String sql = "select TOP 1 * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        sql = sql + "AND c.deptName like concat('%', '"+deptName+"', '%')";
        sql = sql + "AND c.parentId = '"+parentId+"'";
        return this.getOne(sql);
    }

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    public void checkDeptDataScope(String deptId, SsoUser ssoUser)
    {
        if (!StrUtil.equals(ssoUser.getFlagAdmin(), EnumYesNoType.YES.getValue()))
        {
            SsoDept dept = new SsoDept();
            dept.setId(deptId);
            List<SsoDept> depts = this.selectDeptList(dept);
            if (StringUtils.isEmpty(depts))
            {
                throw new ServiceException("没有权限访问部门数据！");
            }
        }
    }

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public void insertDept(SsoDept dept)
    {
        SsoDept info = this.selectDeptById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus()))
        {
            throw new ServiceException("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());

        dept.setKeyName(dept.getKeyName());
        dept.setId(UUID.fastUUID().toString());
        dept.setCreateTime(YFLocalDateTimeUtil.now());
        dept.setPartitionKey(CosmosConfig.getPartitionKeyPrefix() + dept.getKeyName());
        this.save(dept);
    }

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public void updateDept(SsoDept dept)
    {
        SsoDept newParentDept = this.selectDeptById(dept.getParentId());
        SsoDept oldDept = this.selectDeptById(dept.getId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept))
        {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getId(), newAncestors, oldAncestors);
        }
        dept.setUpdateTime(LocalDateTimeUtil.now().toString());
        this.update(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors())
                && !StringUtils.equals("0", dept.getAncestors()))
        {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SsoDept dept)
    {
        List<String> ancestors = yftools.stringToList(dept.getAncestors(),",");
        if (CollectionUtil.isNotEmpty(ancestors)){
            String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
            sql = sql + " AND c.id in "+getListStringInSql(ancestors);
            List<SsoDept> ssoDeptList = this.list(sql);
            for (SsoDept ssoDept : ssoDeptList){
                ssoDept.setStatus("0");
                this.update(ssoDept);
            }
        }

    }

    /**
     * 修改子元素关系
     *
     * @param deptId 被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(String deptId, String newAncestors, String oldAncestors)
    {
        List<SsoDept> children = this.selectChildrenDeptById(deptId);
        for (SsoDept child : children)
        {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
            this.update(child);
        }
    }

    public List<SsoDept> selectChildrenDeptById(String deptId){
        String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        sql = sql + "AND CONTAINS(',' + c.ancestors + ',', ',"+deptId+",')";
        return this.list(sql);
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public void deleteDeptById(String deptId)
    {
        this.delete(deptId);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SsoDept> list, SsoDept t)
    {
        // 得到子节点列表
        List<SsoDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SsoDept tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SsoDept> getChildList(List<SsoDept> list, SsoDept t)
    {
        List<SsoDept> tlist = new ArrayList<SsoDept>();
        Iterator<SsoDept> it = list.iterator();
        while (it.hasNext())
        {
            SsoDept n = (SsoDept) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && StrUtil.equals(n.getParentId(), t.getId()))
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SsoDept> list, SsoDept t)
    {
        return getChildList(list, t).size() > 0;
    }


    public String getCompanyDeptByDeptId(SsoLoginUser loginUser){
        SsoUser sysUser = loginUser.getUser();
        if (sysUser == null) {
            return null;
        }
        if (sysUser.getDeptId() == null) {
            return null;
        }
        return sysUser.getDeptId();

    }
}
