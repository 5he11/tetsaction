package com.frenzy.sso.action;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.UUID;
import com.frenzy.FrenzyConfig;
import com.frenzy.core.constant.Constants;
import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.enums.EnumFieldType;
import com.frenzy.core.utils.SecurityUtils;
import com.frenzy.core.utils.yftools;
import com.frenzy.sso.domain.SsoMenu;
import com.frenzy.sso.domain.SsoUser;
import com.frenzy.sso.domain.YfDictionary;
import com.frenzy.sso.domain.YfDictionaryGroup;
import com.frenzy.sso.entity.po.SsoLoginUser;
import com.frenzy.sso.entity.reqVo.SsoLoginBody;
import com.frenzy.sso.service.*;
import com.google.code.kaptcha.Producer;
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
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SsoLoginAction {

    @Autowired
    public SsoLoginService loginService;
    @Autowired
    public TokenService tokenService;
    @Autowired
    public FrenzyConfig frenzyConfig;
    @Autowired
    private Producer captchaProducer;
    @Autowired
    private Producer captchaProducerMath;
    @Autowired
    private FzCacheCaptchaService fzCacheCaptchaService;
    @Autowired
    private SsoUserService ssoUserService;
    @Autowired
    private SsoMenuService menuService;
    @Autowired
    private SsoPermissionService permissionService;
    @Autowired
    private YfDictionaryGroupService dictionaryService;


    @ApiOperation(value = "sso/user")
    @FunctionName("sso_user")
    public Object sso_user(
            @HttpTrigger(name = "httpRequest", route = "sso/user", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        Long startTime = System.currentTimeMillis();
        context.getLogger().info("User request <sso/login> API start.");

        SsoUser ssoUser = new SsoUser();
        ssoUser.setId(UUID.fastUUID().toString());
        ssoUser.setUserName("yknet");
        ssoUser.setPassword(SecurityUtils.encryptPassword("123456"));
        ssoUserService.save(ssoUser);

        context.getLogger().info("User request <sso/login> API succeed,use time "+(System.currentTimeMillis() - startTime)+"ms.");
        return AjaxResult.success(request, ssoUser);
    }




    @ApiOperation(value = "sso/login")
    @FunctionName("sso_login")
    public Object actionHello(
            @HttpTrigger(name = "httpRequest", route = "sso/login", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoLoginBody>> request,
            final ExecutionContext context) {
        Long startTime = System.currentTimeMillis();
        context.getLogger().info("User request <sso/login> API start.");

        SsoLoginBody loginBody = request.getBody().get();

        HashMap<String,String> map = new HashMap<>();

//        AjaxResult ajax = AjaxResult.success(request);
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        map.put(Constants.TOKEN,token);
//        ajax.put(Constants.TOKEN, token);

//        try {
            // 模拟业务逻辑
//            throw new ServiceException("测试非法参数");
//        } catch (IllegalArgumentException ex) {
//            context.getLogger().severe("捕获到异常: " + ex.getMessage());
//            return AjaxResult.error("捕获到异常: " + ex.getMessage());
//        } catch (Exception ex) {
//            context.getLogger().severe("服务器内部错误: " + ex.getMessage());
//            return AjaxResult.error("服务器内部错误: " + ex.getMessage());
//        }

        context.getLogger().info("User request <sso/login> API succeed,use time "+(System.currentTimeMillis() - startTime)+"ms.");
        return AjaxResult.success(request, map);
    }


    @ApiOperation(value = "sso/userInfo")
    @FunctionName("sso_userInfo")
    public Object sso_userInfo(
            @HttpTrigger(name = "httpRequest", route = "sso/userInfo", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoLoginBody>> request,
            final ExecutionContext context) {
        Long startTime = System.currentTimeMillis();
        context.getLogger().info("User request <sso/userInfo> API start.");

        SsoLoginBody loginBody = request.getBody().get();
        HashMap<String,Object> map = new HashMap<>();
        map.put(Constants.TOKEN,loginBody);
        SsoLoginUser ssoLoginUser = tokenService.getLoginUser(request);
        map.put("loginBody", ssoLoginUser);

//        AjaxResult ajax = AjaxResult.success();
//
//        ajax.put(Constants.TOKEN, loginBody);
//        ajax.put("loginBody", ssoLoginUser);

        context.getLogger().info("User request <sso/userInfo> API succeed,use time "+(System.currentTimeMillis() - startTime)+"ms.");
        return AjaxResult.success(request, map);
    }


    @ApiOperation(value = "sso/main_config")
    @FunctionName("sso_main_config")
    public Object sso_main_config(
            @HttpTrigger(name = "httpRequest", route = "sso/main_config", methods = {HttpMethod.POST,HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoLoginBody>> request,
            final ExecutionContext context) {
        Long startTime = System.currentTimeMillis();
        context.getLogger().info("User request <sso/main_config> API start.");

        HashMap<String,Object> map = new HashMap<>();
        map.put("fieldType",getFieldType());
        map.put("allDict",getAllDict());
        map.put("userYzm", "NO");

        context.getLogger().info("User request <sso/main_config> API succeed,use time "+(System.currentTimeMillis() - startTime)+"ms.");
        return AjaxResult.success(request, map);
    }

    public List<HashMap<String, String>> getFieldType(){
        List<HashMap<String, String>> FieldTypes=new ArrayList<>();
        EnumFieldType[] enumAry = EnumFieldType.values();
        List<String> excludeList=new ArrayList<>();
        excludeList.add(EnumFieldType.FormSelectMuti.getValue());
        excludeList.add(EnumFieldType.FormSelectMutiTag.getValue());
        excludeList.add(EnumFieldType.Email.getValue());
        for (int i = 0; i < enumAry.length; i++) {
            String value=enumAry[i].getValue();
            if(!excludeList.contains(value)){
                FieldTypes.add(setFieldType(value,enumAry[i].getDesc()));
            }
        }
        return FieldTypes;
    }

    public HashMap<String, String> setFieldType(String VALUE,String INFO){
        HashMap<String, String> FieldType = new HashMap<String, String>();
        FieldType.put("value",VALUE);
        FieldType.put("label",INFO);
        return FieldType;
    }

   public Map<String, List<YfDictionary>> getAllDict(){
       List<YfDictionaryGroup> dictionaryList=dictionaryService.listAll();
       Map<String, List<YfDictionary>> dictMap = new HashMap<>();
       for (YfDictionaryGroup dictionaryGroup:dictionaryList){
           dictMap.put(dictionaryGroup.getDictKey(), dictionaryGroup.getDictList());
       }
        return dictMap;
    }


    @ApiOperation(value = "sso/getInfo")
    @FunctionName("sso_getInfo")
    public Object getInfo(
        @HttpTrigger(name = "httpRequest", route = "sso/getInfo", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoLoginBody>> request,
    final ExecutionContext context) {
        SsoUser user = ssoUserService.getLoginUser(request);
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
//        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        HashMap<String,Object> map = new HashMap<>();
        map.put("user", user);
        map.put("roles", roles);
        map.put("permissions", permissions);
        map.put("roles",  "admin");
        map.put("permissions",  "*:*:*");
        return AjaxResult.success(request, map);


//        AjaxResult ajax = AjaxResult.success();
//        ajax.put("user", user);
//        ajax.put("roles", roles);
//        ajax.put("permissions", permissions);
//        ajax.put("roles",  "admin");
//        ajax.put("permissions",  "*:*:*");
//        return ajax;
    }


    @ApiOperation(value = "获取路由信息")
    @FunctionName("sso_getRouters")
    public Object getRouters(
            @HttpTrigger(name = "httpRequest", route = "sso/getRouters", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoLoginBody>> request,
            final ExecutionContext context) {
        List<SsoMenu> menus = menuService.selectMenuTreeByUserId(ssoUserService.getLoginUser(request));
        return AjaxResult.success(request, menuService.buildMenus(menus));
    }


    @ApiOperation(value = "退出登录")
    @FunctionName("sso_logout")
    public Object logout(
            @HttpTrigger(name = "httpRequest", route = "sso/logout", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoLoginBody>> request,
    final ExecutionContext context) {
//        FrontLoginUserVo loginUser = frontTokenComponent.getLoginUser(request);
//        // 删除用户缓存记录
//        frontTokenComponent.delLoginUser(loginUser.getToken());

        return AjaxResult.success(request);
    }


    @ApiOperation(value = "sso/captchaImage")
    @FunctionName("sso_captchaImage")
    public Object sso_captchaImage(
        @HttpTrigger(name = "httpRequest", route = "sso/captchaImage", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoLoginBody>> request,
        final ExecutionContext context) {
        HashMap<String,Object> map = new HashMap<>();
        boolean captchaOnOff = frenzyConfig.isCaptchaOn();
        map.put("captchaOnOff", captchaOnOff);

//        AjaxResult ajax = AjaxResult.success();
//        boolean captchaOnOff = frenzyConfig.isCaptchaOn();
//        ajax.put("captchaOnOff", captchaOnOff);
        if (!captchaOnOff)
        {
            return AjaxResult.success(request, map);
        }

        // 保存验证码信息
        String verifyKey = UUID.fastUUID().toString();

        String capStr = null, code = null;
        BufferedImage image = null;

        // 生成验证码
        String captchaType = frenzyConfig.getCaptchaType();
        if ("math".equals(captchaType))
        {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        }
        else if ("char".equals(captchaType))
        {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        fzCacheCaptchaService.saveCache(verifyKey, code);
//        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try
        {
            ImageIO.write(image, "jpg", os);
        }
        catch (IOException e)
        {
            return AjaxResult.error(request, e.getMessage());
        }

        map.put("uuid", verifyKey);
        map.put("img", Base64.encode(os.toByteArray()));
        return AjaxResult.success(request, map);
//        ajax.put("uuid", verifyKey);
//        ajax.put("img", Base64.encode(os.toByteArray()));
//        return ajax;
    }



}
