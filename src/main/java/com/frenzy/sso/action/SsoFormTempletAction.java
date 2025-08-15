package com.frenzy.sso.action;

import cn.hutool.core.lang.UUID;
import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.utils.ValidationUtil;
import com.frenzy.core.utils.YFLocalDateTimeUtil;
import com.frenzy.sso.domain.SsoMenu;
import com.frenzy.sso.entity.po.SsoFormTempletPo;
import com.frenzy.sso.service.SsoFormService;
import com.frenzy.sso.service.SsoMenuService;
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

import java.util.Optional;

@Slf4j
@Component
public class SsoFormTempletAction {

    public static final String API_PREFIX = "sso/formtemplet";

    @Autowired
    private SsoUserService userService;
    @Autowired
    private SsoMenuService menuService;
    @Autowired
    private SsoFormService formService;



    @ApiOperation(value = "获取模板列表")
    @FunctionName("ssoFormTempletList")
    public Object ssoFormTempletList(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/list", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("formId") String formId, // 绑定路径参数
            final ExecutionContext context) {
        return AjaxResult.success(request, formService.listFormTempletBySsoForm(formId));
    }


    @ApiOperation(value = "获取全部列表类型模板列表")
    @FunctionName("ssoFormTempletAllListTypeList")
    public Object ssoFormTempletAllListTypeList(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/list", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("formId") String formId, // 绑定路径参数
            final ExecutionContext context) {
        return AjaxResult.success(request, formService.listAllListTemplet(formId));
    }


    @ApiOperation(value = "获取模板详细信息")
    @FunctionName("ssoFormTempletGetInfo")
    public Object ssoFormTempletGetInfo(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/info/{formTempletId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("formTempletId") String formTempletId, // 绑定路径参数
            final ExecutionContext context) {
        return AjaxResult.success(request, formService.getFormTempletByFormTempletId(formTempletId));
    }


    @ApiOperation(value = "新增模板")
    @FunctionName("ssoFormTempletAdd")
    public Object ssoFormTempletAdd(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/add", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoFormTempletPo>> request,
            final ExecutionContext context) {

        SsoFormTempletPo formTemplet = request.getBody().get();
        ValidationUtil.validate(formTemplet);

        formService.saveTempLet(formTemplet);
        return AjaxResult.success(request);
    }



    @ApiOperation(value = "修改模板")
    @FunctionName("ssoFormTempletEdit")
    public Object ssoFormTempletEdit(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/edit", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoFormTempletPo>> request,
            final ExecutionContext context) {

        SsoFormTempletPo formTemplet = request.getBody().get();
        ValidationUtil.validate(formTemplet);

        formService.updateTempLet(formTemplet);
        return AjaxResult.success(request);
    }



    @ApiOperation(value = "删除模板")
    @FunctionName("ssoFormTempletRemove")
    public Object ssoFormTempletRemove(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/{formId}/{formTempletId}", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("formId") String formId, // 绑定路径参数
            @BindingName("formTempletId") String formTempletId, // 绑定路径参数
            final ExecutionContext context) {
        String[] formTempletIds = formTempletId.split(",");
        formService.delTempLetByTempLetIds(formId, formTempletIds);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "添加模板菜单")
    @FunctionName("ssoFormTempletAddTpMenu")
    public Object ssoFormTempletAddTpMenu(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/addTpMenu", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoMenu>> request,
            final ExecutionContext context) {

        SsoMenu menu = request.getBody().get();
        ValidationUtil.validate(menu);

        SsoFormTempletPo formTemplet=formService.getFormTempletByFormTempletId(menu.getRemark());
        if (formTemplet==null)
            return AjaxResult.error("模板不存在");

        String uuid= UUID.fastUUID().toString(false);
        menu.setRemark(uuid);
        menu.setCreateBy(userService.getLoginUser(request).getUserName());
        menu.setCreateTime(YFLocalDateTimeUtil.now());
        menuService.insertMenu(menu);

        return AjaxResult.success(request);
    }



}
