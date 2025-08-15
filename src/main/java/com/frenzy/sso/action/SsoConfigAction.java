package com.frenzy.sso.action;

import com.frenzy.core.constant.UserConstants;
import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.utils.ValidationUtil;
import com.frenzy.sso.domain.SsoConfig;
import com.frenzy.sso.service.SsoConfigService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SsoConfigAction {

    public static final String API_PREFIX = "sso/config";

    @Autowired
    private SsoConfigService configService;
    @Autowired
    private SsoUserService userService;


    @ApiOperation(value = "获取参数配置列表")
    @FunctionName("ssoConfigList")
    public Object ssoConfigList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/list", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoConfig>> request,
            final ExecutionContext context) {
        SsoConfig config = new SsoConfig();
        if (!request.getBody().isEmpty()){
            config = request.getBody().get();
        }
        List<SsoConfig> list = configService.selectConfigList(config);
        return AjaxResult.success(request, list);
    }



    @ApiOperation(value = "根据参数编号获取详细信息")
    @FunctionName("ssoConfigGetInfo")
    public Object ssoConfigGetInfo(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/info/{configId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("configId") String configId, // 绑定路径参数
            final ExecutionContext context) {
        return AjaxResult.success(request, configService.selectConfigById(configId));
    }


    @ApiOperation(value = "根据参数键名查询参数值")
    @FunctionName("ssoConfigGetConfigKey")
    public Object ssoConfigGetConfigKey(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/configKey/{configKey}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("configKey") String configKey, // 绑定路径参数
            final ExecutionContext context) {
        return AjaxResult.success(request, configService.selectConfigByKey(configKey));
    }


    @ApiOperation(value = "新增参数配置")
    @FunctionName("ssoConfigAdd")
    public Object ssoConfigAdd(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/add", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoConfig>> request,
            final ExecutionContext context) {

        SsoConfig config = request.getBody().get();
        ValidationUtil.validate(config);

        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config)))
        {
            return AjaxResult.error(request, "新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }

        configService.insertConfig(config);
        return AjaxResult.success(request);
    }



    @ApiOperation(value = "修改参数配置")
    @FunctionName("ssoConfigEdit")
    public Object ssoConfigEdit(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/edit", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoConfig>> request,
            final ExecutionContext context) {

        SsoConfig config = request.getBody().get();
        ValidationUtil.validate(config);

        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config)))
        {
            return AjaxResult.error(request, "修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        configService.updateConfig(config);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "删除参数配置")
    @FunctionName("ssoConfigRemove")
    public Object ssoConfigRemove(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/{configIds}", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("configIds") String configIds, // 绑定路径参数
            final ExecutionContext context) {
        configService.deleteConfigByIds(configIds);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "刷新参数缓存")
    @FunctionName("ssoConfigRefreshCache")
    public Object ssoConfigRefreshCache(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/refreshCache", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        configService.resetConfigCache();
        return AjaxResult.success(request);
    }


}
