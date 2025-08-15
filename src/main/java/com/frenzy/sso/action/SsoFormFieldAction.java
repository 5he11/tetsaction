package com.frenzy.sso.action;

import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.utils.ValidationUtil;
import com.frenzy.core.utils.YFLocalDateTimeUtil;
import com.frenzy.sso.entity.po.SsoFormFieldPo;
import com.frenzy.sso.service.SsoFormService;
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
public class SsoFormFieldAction {

    public static final String API_PREFIX = "sso/formfield";

    @Autowired
    private SsoFormService formService;






    @ApiOperation(value = "获取字段列表")
    @FunctionName("ssoFormFieldList")
    public Object ssoFormFieldList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/list/{formId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("formId") String formId, // 绑定路径参数
            final ExecutionContext context) {
        List<SsoFormFieldPo> formList = formService.formFieldListByFormId(formId);
        return AjaxResult.success(request, formList);
    }


    @ApiOperation(value = "获取字段详细信息")
    @FunctionName("ssoFormFieldGetInfo")
    public Object ssoFormFieldGetInfo(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/info/{formId}/{formfieldId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("formId") String formId, // 绑定路径参数
            @BindingName("formfieldId") String formfieldId, // 绑定路径参数
            final ExecutionContext context) {
        return AjaxResult.success(request, formService.getFormFieldByFormFieldId(formId, formfieldId));
    }






    @ApiOperation(value = "新增字段")
    @FunctionName("ssoFormFieldAdd")
    public Object ssoFormFieldAdd(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/add", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoFormFieldPo>> request,
            final ExecutionContext context) {

        SsoFormFieldPo formField = request.getBody().get();
        ValidationUtil.validate(formField);

        formService.createField(formField);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "修改字段")
    @FunctionName("ssoFormFieldEdit")
    public Object ssoFormFieldEdit(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/edit", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoFormFieldPo>> request,
            final ExecutionContext context) {

        SsoFormFieldPo formField = request.getBody().get();
        ValidationUtil.validate(formField);

        formField.setUpdateTime(YFLocalDateTimeUtil.now());
        formService.updateField(formField);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "删除字段")
    @FunctionName("ssoFormFieldRemove")
    public Object ssoFormFieldRemove(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/{formId}/{formfieldId}", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("formId") String formId, // 绑定路径参数
            @BindingName("formfieldId") String formfieldId, // 绑定路径参数
            final ExecutionContext context) {
        String[] formfieldIds = formfieldId.split(",");
        formService.delFieldByFields(formId, formfieldIds);
        return AjaxResult.success(request);
    }






}
