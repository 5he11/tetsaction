package com.frenzy.sso.action;

import cn.hutool.core.util.StrUtil;
import com.frenzy.core.constant.UserConstants;
import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.enums.EnumYesNoType;
import com.frenzy.core.utils.SecurityUtils;
import com.frenzy.core.utils.StringUtils;
import com.frenzy.core.utils.ValidationUtil;
import com.frenzy.sso.domain.SsoRole;
import com.frenzy.sso.domain.SsoUser;
import com.frenzy.sso.mapper.SsoUserMapper;
import com.frenzy.sso.service.SsoPostService;
import com.frenzy.sso.service.SsoRoleService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SsoUserAction {

    public static final String API_PREFIX = "sso/user";

    @Autowired
    private SsoUserService userService;
    @Autowired
    private SsoRoleService roleService;
    @Autowired
    private SsoPostService postService;
    @Autowired
    private SsoUserMapper userMapper;


    @ApiOperation(value = "获取用户列表")
    @FunctionName("ssoUserList")
    public Object ssoUserList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/list", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoUser>> request,
            final ExecutionContext context) {

        SsoUser user = new SsoUser();
        if (!request.getBody().isEmpty()){
            user = request.getBody().get();
        }
        List<SsoUser> list = userService.selectUserList(user);
//        return AjaxResult.success(list);
        return AjaxResult.success(request,list);
    }




//    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
//    @PreAuthorize("@ss.hasPermi('system:user:export')")
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, SysUser user)
//    {
//        List<SysUser> list = userService.selectUserList(user);
//        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
//        util.exportExcel(response, list, "用户数据");
//    }
//
//    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
//    @PreAuthorize("@ss.hasPermi('system:user:import')")
//    @PostMapping("/importData")
//    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
//    {
//        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
//        List<SysUser> userList = util.importExcel(file.getInputStream());
//        String operName = getUsername();
//        String message = userService.importUser(userList, updateSupport, operName);
//        return AjaxResult.success(message);
//    }
//
//    @PostMapping("/importTemplate")
//    public void importTemplate(HttpServletResponse response)
//    {
//        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
//        util.importTemplateExcel(response, "用户数据");
//    }


    @ApiOperation(value = "根据用户编号获取详细信息")
    @FunctionName("ssoUserGetInfo")
    public Object ssoUserGetInfo(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/info/{userId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("userId") String userId, // 绑定路径参数
            final ExecutionContext context) {

        SsoUser ssoUser = userService.getLoginUser(request);
        userService.checkUserDataScope(userId, ssoUser);
        HashMap<String,Object> map = new HashMap<>();
        List<SsoRole> roles = roleService.selectRoleAll();
        map.put("roles", StrUtil.equals(ssoUser.getFlagAdmin(),EnumYesNoType.YES.getValue()) ? roles : roles.stream().filter(r -> !StrUtil.equals(r.getAdminFlag(), EnumYesNoType.YES.getValue())).collect(Collectors.toList()));
        map.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId))
        {
            map.put(AjaxResult.DATA_TAG, userService.selectUserById(userId));
            map.put("postIds", postService.selectPostListByUserId(userId));
            map.put("roleIds", roleService.selectRoleListByUserId(userId));
        }
        return AjaxResult.success(request, map);
    }


    @ApiOperation(value = "新增用户")
    @FunctionName("ssoUserAdd")
    public Object ssoUserAdd(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/add", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoUser>> request,
            final ExecutionContext context) {

        SsoUser user = request.getBody().get();
        ValidationUtil.validate(user);

        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName())))
        {
            return AjaxResult.error(request, "新增用户'" + user.getUserName() + "'失败，登录账号系统已占用，请更换账号");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error(request, "新增用户'" + user.getUserName() + "'失败，手机号码系统已占用，请更换账号");
        }
        else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error(request, "新增用户'" + user.getUserName() + "'失败，邮箱账号系统已占用，请更换账号");
        }
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        userService.insertUser(user);
        return AjaxResult.success(request);
    }



    @ApiOperation(value = "修改用户")
    @FunctionName("ssoUserEdit")
    public Object ssoUserEdit(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/edit", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoUser>> request,
            final ExecutionContext context) {

        SsoUser user = request.getBody().get();
        ValidationUtil.validate(user);

        userService.checkUserAllowed(user);
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error(request, "修改用户'" + user.getUserName() + "'失败，手机号码系统已占用，请更换账号");
        }
        else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error(request, "修改用户'" + user.getUserName() + "'失败，邮箱账号系统已占用，请更换账号");
        }
        userMapper.updateUser(user);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "删除用户")
    @FunctionName("ssoUserRemove")
    public Object ssoUserRemove(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/{userIds}", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("userIds") String[] userIds, // 绑定路径参数
            final ExecutionContext context) {
            SsoUser ssoUser = userService.getLoginUser(request);
            if (ArrayUtils.contains(userIds, ssoUser.getId()))
            {
                return AjaxResult.error(request, "当前用户不能删除");
            }
        userService.deleteUserByIds(userIds);
        return AjaxResult.success(request);
    }




    @ApiOperation(value = "重置密码")
    @FunctionName("ssoUserResetPwd")
    public Object ssoUserResetPwd(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/resetPwd", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoUser>> request,
            final ExecutionContext context) {

        SsoUser user = request.getBody().get();
        ValidationUtil.validate(user);

        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        userService.resetPwd(user);

        return AjaxResult.success(request);
    }



    @ApiOperation(value = "状态修改")
    @FunctionName("ssoUserChangeStatus")
    public Object ssoUserChangeStatus(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/changeStatus", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoUser>> request,
            final ExecutionContext context) {
        SsoUser user = request.getBody().get();
        ValidationUtil.validate(user);
        userService.checkUserAllowed(user);
        userService.updateUserStatus(user);
        return AjaxResult.success(request);
    }





    @ApiOperation(value = "根据用户编号获取授权角色")
    @FunctionName("ssoUserAuthRoleUserId")
    public Object ssoUserAuthRoleUserId(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/authRole/{userId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("userId") String userId, // 绑定路径参数
            final ExecutionContext context) {
        HashMap<String,Object> map = new HashMap<>();
        SsoUser user = userService.selectUserById(userId);
        List<SsoRole> roles = roleService.selectRolesByUserId(userId);
        map.put("user", user);
        map.put("roles", StrUtil.equals(user.getFlagAdmin(),EnumYesNoType.YES.getValue()) ? roles : roles.stream().filter(r -> !StrUtil.equals(r.getAdminFlag(), EnumYesNoType.YES.getValue())).collect(Collectors.toList()));

        return AjaxResult.success(request, map);
    }



    @ApiOperation(value = "用户授权角色")
    @FunctionName("ssoUpdateUserAuthRole")
    public Object ssoUpdateUserAuthRole(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/updateAuthRole", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        // 获取查询参数
        Map<String, String> queryParams = request.getQueryParameters();
        // roleId 参数解析
        String userId = queryParams.get("userId");
        String roleIdsStr = queryParams.get("roleIds");
        String[] roleIds = roleIdsStr.split(",");
        userService.insertUserAuth(userId, roleIds);
        return AjaxResult.success(request);
    }


}
