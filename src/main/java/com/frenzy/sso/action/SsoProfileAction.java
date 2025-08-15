package com.frenzy.sso.action;

import com.frenzy.core.constant.UserConstants;
import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.utils.StringUtils;
import com.frenzy.sso.domain.SsoPost;
import com.frenzy.sso.domain.SsoRole;
import com.frenzy.sso.domain.SsoUser;
import com.frenzy.sso.entity.po.SsoLoginUser;
import com.frenzy.sso.service.SsoMenuService;
import com.frenzy.sso.service.SsoUserService;
import com.frenzy.sso.service.TokenService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SsoProfileAction {

    public static final String API_PREFIX = "sso/user/profile";

    @Autowired
    private TokenService tokenService;
    @Autowired
    private SsoUserService userService;



    @ApiOperation(value = "个人信息")
    @FunctionName("ssoUserProfileList")
    public Object ssoUserProfileList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/info", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        SsoUser ssoUser = userService.getLoginUser(request);
        HashMap<String,Object> map = new HashMap<>();
        map.put("roleGroup", userService.selectUserRoleGroup(ssoUser.getId()));
        map.put("postGroup", userService.selectUserPostGroup(ssoUser.getId()));
        return AjaxResult.success(request, map);
    }



    @ApiOperation(value = "修改用户")
    @FunctionName("ssoUserProfileEdit")
    public Object ssoUserProfileEdit(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/edit", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoUser>> request,
            final ExecutionContext context) {

        SsoUser user = new SsoUser();
        if (!request.getBody().isEmpty()){
            user = request.getBody().get();
        }

        SsoUser ssoUser = userService.getLoginUser(request);
        SsoLoginUser ssoLoginUser = tokenService.getLoginUser(request);
        user.setUserName(ssoUser.getUserName());
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error(request, "修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error(request, "修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setId(ssoUser.getId());
        user.setPassword(null);
        // 更新缓存用户信息
        ssoUser.setNickName(user.getNickName());
        ssoUser.setPhonenumber(user.getPhonenumber());
        ssoUser.setEmail(user.getEmail());
        ssoUser.setSex(user.getSex());
        ssoLoginUser.setUser(ssoUser);
        tokenService.setLoginUser(ssoLoginUser);
        return AjaxResult.success(request);

    }



//    /**
//     * 重置密码
//     */
//    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
//    @PutMapping("/updatePwd")
//    public AjaxResult updatePwd(String oldPassword, String newPassword)
//    {
//        LoginUser loginUser = getLoginUser();
//        String userName = loginUser.getUsername();
//        String password = loginUser.getPassword();
//        if (!SecurityUtils.matchesPassword(oldPassword, password))
//        {
//            return AjaxResult.error("修改密码失败，旧密码错误");
//        }
//        if (SecurityUtils.matchesPassword(newPassword, password))
//        {
//            return AjaxResult.error("新密码不能与旧密码相同");
//        }
//        if (userService.resetUserPwd(userName, SecurityUtils.encryptPassword(newPassword)) > 0)
//        {
//            // 更新缓存用户密码
//            loginUser.getUser().setPassword(SecurityUtils.encryptPassword(newPassword));
//            tokenService.setLoginUser(loginUser);
//            return AjaxResult.success();
//        }
//        return AjaxResult.error("修改密码异常，请联系管理员");
//    }
//
//    /**
//     * 头像上传
//     */
//    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
//    @PostMapping("/avatar")
//    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file) throws IOException
//    {
//        if (!file.isEmpty())
//        {
//            LoginUser loginUser = getLoginUser();
//            String avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file);
//            if (userService.updateUserAvatar(loginUser.getUsername(), avatar))
//            {
//                AjaxResult ajax = AjaxResult.success();
//                ajax.put("imgUrl", avatar);
//                // 更新缓存用户头像
//                loginUser.getUser().setAvatar(avatar);
//                tokenService.setLoginUser(loginUser);
//                return ajax;
//            }
//        }
//        return AjaxResult.error("上传图片异常，请联系管理员");
//    }
}
