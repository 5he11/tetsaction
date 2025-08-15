package com.frenzy.sso.action;

import cn.hutool.core.util.StrUtil;
import com.frenzy.core.constant.UserConstants;
import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.utils.StringUtils;
import com.frenzy.sso.domain.SsoDept;
import com.frenzy.sso.service.SsoDeptService;
import com.frenzy.sso.service.SsoUserService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SsoDeptAction {

    public static final String API_PREFIX = "sso/dept";

    @Autowired
    private SsoDeptService deptService;
    @Autowired
    private SsoUserService userService;


    @ApiOperation(value = "获取部门列表")
    @FunctionName("ssoDeptList")
    public Object ssoDeptList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/list", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoDept>> request,
            final ExecutionContext context) {

        SsoDept dept = new SsoDept();
        if (!request.getBody().isEmpty()){
            dept = request.getBody().get();
        }
        List<SsoDept> depts = deptService.selectDeptList(dept);
        return AjaxResult.success(request, depts);
    }


    @ApiOperation(value = "查询部门列表（排除节点）")
    @FunctionName("ssoDeptListEexclude")
    public Object ssoDeptListEexclude(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/list/exclude/{deptId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("deptId") String deptId, // 绑定路径参数
            final ExecutionContext context) {

        List<SsoDept> depts = deptService.selectDeptList(new SsoDept());
        Iterator<SsoDept> it = depts.iterator();
        while (it.hasNext())
        {
            SsoDept d = (SsoDept) it.next();
            if (StrUtil.equals(d.getId(), deptId)
                    || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""))
            {
                it.remove();
            }
        }
        return AjaxResult.success(request, depts);

    }



    @ApiOperation(value = "根据部门编号获取详细信息")
    @FunctionName("ssoDeptGetInfo")
    public Object ssoDeptGetInfo(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/info/{deptId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("deptId") String deptId, // 绑定路径参数
            final ExecutionContext context) {
        deptService.checkDeptDataScope(deptId, userService.getLoginUser(request));
        return AjaxResult.success(request, deptService.selectDeptById(deptId));
    }



    @ApiOperation(value = "获取部门下拉树列表")
    @FunctionName("ssoDeptTreeselect")
    public Object ssoDeptTreeselect(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/treeselect", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoDept>> request,
            final ExecutionContext context) {

        SsoDept dept = new SsoDept();
        if (!request.getBody().isEmpty()){
            dept = request.getBody().get();
        }

        List<SsoDept> depts = deptService.selectDeptList(dept);
        return AjaxResult.success(request, deptService.buildDeptTreeSelect(depts));
    }


    @ApiOperation(value = "根据部门编号获取详细信息")
    @FunctionName("ssoDeptRoleDeptTreeselect")
    public Object ssoDeptRoleDeptTreeselect(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/roleDeptTreeselect/{roleId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("roleId") String roleId, // 绑定路径参数
            final ExecutionContext context) {
        List<SsoDept> depts = deptService.selectDeptList(new SsoDept());
        HashMap<String,Object> map = new HashMap<>();
        map.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        map.put("depts", deptService.buildDeptTreeSelect(depts));
        return AjaxResult.success(request, map);

//        AjaxResult ajax = AjaxResult.success(request);
//        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
//        ajax.put("depts", deptService.buildDeptTreeSelect(depts));
//        return ajax;
    }




    @ApiOperation(value = "新增部门")
    @FunctionName("ssoDeptAdd")
    public Object ssoDeptAdd(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/add", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoDept>> request,
            final ExecutionContext context) {

        SsoDept dept = new SsoDept();
        if (!request.getBody().isEmpty()) {
            dept = request.getBody().get();
        }

        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept)))
        {
            return AjaxResult.error(request, "新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        deptService.insertDept(dept);
        return AjaxResult.success(request);
    }



    @ApiOperation(value = "修改部门")
    @FunctionName("ssoDeptEdit")
    public Object ssoDeptEdit(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/edit", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoDept>> request,
            final ExecutionContext context) {

        SsoDept dept = new SsoDept();
        if (!request.getBody().isEmpty()) {
            dept = request.getBody().get();
        }

        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept)))
        {
            return AjaxResult.error(request, "修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        else if (dept.getId().equals(dept.getId()))
        {
            return AjaxResult.error(request, "修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }
        else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus())
                && deptService.selectNormalChildrenDeptById(dept.getId()) > 0)
        {
            return AjaxResult.error(request, "该部门包含未停用的子部门！");
        }
        deptService.updateDept(dept);
        return AjaxResult.success(request);
    }




    @ApiOperation(value = "删除部门")
    @FunctionName("ssoDeptRemove")
    public Object ssoDeptRemove(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/{deptId}", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("deptId") String deptId, // 绑定路径参数
            final ExecutionContext context) {

        if (deptService.hasChildByDeptId(deptId))
        {
            return AjaxResult.error(request, "存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId))
        {
            return AjaxResult.error(request, "部门存在用户,不允许删除");
        }
        deptService.deleteDeptById(deptId);
        return AjaxResult.success(request);
    }



}
