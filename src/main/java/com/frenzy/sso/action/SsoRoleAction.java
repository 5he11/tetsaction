package com.frenzy.sso.action;

import cn.hutool.core.util.StrUtil;
import com.frenzy.core.constant.UserConstants;
import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.enums.EnumYesNoType;
import com.frenzy.core.utils.StringUtils;
import com.frenzy.core.utils.ValidationUtil;
import com.frenzy.core.utils.YFLocalDateTimeUtil;
import com.frenzy.sso.domain.SsoRole;
import com.frenzy.sso.domain.SsoUser;
import com.frenzy.sso.entity.po.SsoLoginUser;
import com.frenzy.sso.service.SsoPermissionService;
import com.frenzy.sso.service.SsoRoleService;
import com.frenzy.sso.service.SsoUserService;
import com.frenzy.sso.service.TokenService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class SsoRoleAction {

    public static final String API_PREFIX = "sso/role";

    @Autowired
    private SsoRoleService roleService;
    @Autowired
    private SsoUserService userService;
    @Autowired
    public TokenService tokenService;
    @Autowired
    public SsoPermissionService permissionService;

    @ApiOperation(value = "获取角色列表")
    @FunctionName("ssoRoleList")
    public Object ssoRoleList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/list", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoRole>> request,
            final ExecutionContext context) {
        SsoRole role = new SsoRole();
        if (!request.getBody().isEmpty()){
            role = request.getBody().get();
        }

        List<SsoRole> list = roleService.selectRoleList(role);
        return AjaxResult.success(request, list);
    }



    @ApiOperation(value = "根据岗位编号获取详细信息")
    @FunctionName("ssoRoleGetInfo")
    public Object ssoRoleGetInfo(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/info/{roleId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("roleId") String roleId, // 绑定路径参数
            final ExecutionContext context) {
        roleService.checkRoleDataScope(roleId,userService.getLoginUser(request));
        return AjaxResult.success(request, roleService.selectRoleById(roleId));
    }



    @ApiOperation(value = "新增角色")
    @FunctionName("ssoRoleAdd")
    public Object ssoRoleAdd(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/add", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoRole>> request,
            final ExecutionContext context) {

        SsoRole role = request.getBody().get();
        ValidationUtil.validate(role);

        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role)))
        {
            return AjaxResult.error(request, "新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role)))
        {
            return AjaxResult.error(request, "新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        roleService.insertRole(role);
        return AjaxResult.success(request);
    }



    @ApiOperation(value = "修改保存角色")
    @FunctionName("ssoRoleEdit")
    public Object ssoRoleEdit(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/edit", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoRole>> request,
            final ExecutionContext context) {
        SsoRole role = request.getBody().get();
        ValidationUtil.validate(role);

        roleService.checkRoleAllowed(role);
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role)))
        {
            return AjaxResult.error(request, "修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role)))
        {
            return AjaxResult.error(request, "修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }

        role.setUpdateTime(YFLocalDateTimeUtil.now());
        roleService.updateRole(role);

        // 更新缓存用户权限
        SsoLoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser.getUser()) && !StrUtil.equals(loginUser.getUser().getFlagAdmin(),EnumYesNoType.YES.getValue()))
        {
            loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getUser()));
            loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUserName()));
            tokenService.setLoginUser(loginUser);
        }
        return AjaxResult.success(request);

    }


    @ApiOperation(value = "修改保存数据权限")
    @FunctionName("ssoRoleEditDataScope")
    public Object ssoRoleEditDataScope(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/editDataScope", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoRole>> request,
            final ExecutionContext context) {
        SsoRole role = request.getBody().get();
        ValidationUtil.validate(role);

        roleService.checkRoleAllowed(role);
        roleService.authDataScope(role);
        return AjaxResult.success(request);

    }


    @ApiOperation(value = "状态修改")
    @FunctionName("ssoRoleChangeStatus")
    public Object ssoRoleChangeStatus(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/changeStatus", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoRole>> request,
            final ExecutionContext context) {
        SsoRole role = request.getBody().get();
        ValidationUtil.validate(role);

        roleService.checkRoleAllowed(role);
        roleService.updateRoleStatus(role);
        return AjaxResult.success(request);

    }


    @ApiOperation(value = "删除角色")
    @FunctionName("ssoRoleRemove")
    public Object ssoRoleRemove(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/{roleIds}", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("roleIds") String roleIds, // 绑定路径参数
            final ExecutionContext context) {
        roleService.deleteRoleByIds(roleIds);
        return AjaxResult.success(request);
    }



    @ApiOperation(value = "获取角色选择框列表")
    @FunctionName("ssoRoleOptionselect")
    public Object ssoRoleOptionselect(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/optionselect", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        return AjaxResult.success(request, roleService.selectRoleAll());
    }




    @ApiOperation(value = "查询已分配用户角色列表")
    @FunctionName("ssoRoleAuthUserAllocatedList")
    public Object ssoRoleAuthUserAllocatedList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/authUser/allocatedList", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoUser>> request,
            final ExecutionContext context) {

        SsoUser user = request.getBody().get();
        List<SsoUser> list = userService.selectAllocatedList(user);
        return AjaxResult.success(request, list);

    }


    @ApiOperation(value = "查询未分配用户角色列表")
    @FunctionName("ssoRoleAuthUserUnallocatedList")
    public Object ssoRoleAuthUserUnallocatedList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/authUser/unallocatedList", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoUser>> request,
            final ExecutionContext context) {

        SsoUser user = request.getBody().get();
        List<SsoUser> list = userService.selectUnallocatedList(user);
        return AjaxResult.success(request, list);

    }



    @ApiOperation(value = "取消授权用户")
    @FunctionName("ssoRoleAuthUserCancel")
    public Object ssoRoleAuthUserCancel(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/authUser/cancel", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        // 获取查询参数
        Map<String, String> queryParams = request.getQueryParameters();
        String roleId = queryParams.get("roleId");
        String userId = queryParams.get("userId");
        roleService.deleteAuthUser(roleId, userId);
        return AjaxResult.success(request);
    }



    @ApiOperation(value = "批量取消授权用户")
    @FunctionName("ssoRoleAuthUserCancelAll")
    public Object ssoRoleAuthUserCancelAll(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/authUser/cancelAll", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        // 获取查询参数
        Map<String, String> queryParams = request.getQueryParameters();
        // roleId 参数解析
        String roleId = queryParams.get("roleId");
        String userIdsStr = queryParams.get("userIds");
        String[] userIds = userIdsStr.split(",");
        roleService.deleteAuthUsers(roleId, userIds);
        return AjaxResult.success(request);
    }



    @ApiOperation(value = "批量选择用户授权")
    @FunctionName("ssoRoleAuthUserSelectAll")
    public Object ssoRoleAuthUserSelectAll(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/authUser/selectAll", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        // 获取查询参数
        Map<String, String> queryParams = request.getQueryParameters();
        // roleId 参数解析
        String roleId = queryParams.get("roleId");
        String userIdsStr = queryParams.get("userIds");
        String[] userIds = userIdsStr.split(",");
        roleService.insertAuthUsers(roleId, userIds);
        return AjaxResult.success(request);

    }


}
