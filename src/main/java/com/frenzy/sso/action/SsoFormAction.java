package com.frenzy.sso.action;

import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.utils.ValidationUtil;
import com.frenzy.core.utils.YFLocalDateTimeUtil;
import com.frenzy.sso.domain.SsoForm;
import com.frenzy.sso.service.SsoFormService;
import com.frenzy.sso.service.SsoUserService;
import com.microsoft.azure.functions.*;
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
public class SsoFormAction {

    public static final String API_PREFIX = "sso/form";

    @Autowired
    private SsoFormService formService;
    @Autowired
    private SsoUserService userService;







    @ApiOperation(value = "获取表单列表")
    @FunctionName("ssoFormList")
    public Object ssoFormList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/list", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoForm>> request,
            final ExecutionContext context) {
        SsoForm form = new SsoForm();
        if (!request.getBody().isEmpty()){
            form = request.getBody().get();
        }

        List<SsoForm> formList = formService.listByFiter(form);
        return AjaxResult.success(request, formList);
    }


    @ApiOperation(value = "获取表单详细信息")
    @FunctionName("ssoFormGetInfo")
    public Object ssoFormGetInfo(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/info/{formId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("formId") String formId, // 绑定路径参数
            final ExecutionContext context) {
        return AjaxResult.success(request, formService.getById(formId));
    }






    @ApiOperation(value = "新增表单")
    @FunctionName("ssoFormAdd")
    public Object ssoFormAdd(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/add", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoForm>> request,
            final ExecutionContext context) {

        SsoForm form = request.getBody().get();
        ValidationUtil.validate(form);

        formService.saveFrom(form);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "修改表单")
    @FunctionName("ssoFormEdit")
    public Object ssoFormEdit(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/edit", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoForm>> request,
            final ExecutionContext context) {

        SsoForm form = request.getBody().get();
        ValidationUtil.validate(form);

        form.setUpdateBy(userService.getLoginUser(request).getUserName());
        form.setUpdateTime(YFLocalDateTimeUtil.now());
        formService.update(form);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "删除表单")
    @FunctionName("ssoFormRemove")
    public Object ssoFormRemove(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/{formIds}", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("formIds") String formIds, // 绑定路径参数
            final ExecutionContext context) {
        String[] formId = formIds.split(",");
        formService.removeFrom(formId);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "复制表单")
    @FunctionName("ssoCopyForm")
    public Object ssoCopyForm(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/copy_form", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<SsoForm>> request,
            final ExecutionContext context) {

        SsoForm form = request.getBody().get();
        ValidationUtil.validate(form);

        formService.copyFrom(form, userService.getLoginUser(request));
        return AjaxResult.success(request);
    }


//    @ApiOperation(value = "导出表单")
//    @FunctionName("ssoExportForm")
//    public HttpResponseMessage ssoExportForm(
//            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/export", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<List<String>>> request,
//            final ExecutionContext context) {
//
//        try {
//            List<String> formIds = request.getBody().orElseThrow(() ->
//                    new IllegalArgumentException("请求体不能为空"));
//            byte[] data = formService.export(formIds);
//
//            HttpResponseMessage response = request.createResponseBuilder(HttpStatus.OK)
//                    .header("Access-Control-Allow-Origin", "*")
//                    .header("Content-Disposition",
//                            "attachment; filename=\"form_metadata_" + DateUtil.now() + ".json\"")
//                    .header("Content-Type", "application/octet-stream; charset=UTF-8")  // 替代 .contentType()
//                    .body(data)
//                    .build();
//
//
//
//            return response;
//        } catch (Exception e) {
//            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("服务器内部错误")
//                    .build();
//        }
//
//    }
//
//
//
//    @ApiOperation(value = "导入表单")
//    @FunctionName("ssoImportForm")
//    public Object ssoImportForm(
//            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/import", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<MultipartFile>> request,
//            final ExecutionContext context)  throws Exception {
//
//        MultipartFile file = request.getBody().get();
//        String json = IOUtils.toString(file.getInputStream(), "UTF-8");
//        formService.importForm(json);
//
//        return AjaxResult.success(request);
//    }
//
//
    @ApiOperation(value = "导出表单代码")
    @FunctionName("ssoExportFormCode")
    public Object ssoExportFormCode(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/proCode/{formId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("formId") String formId, // 绑定路径参数
            final ExecutionContext context) throws Exception {
        String downLoadUrl = formService.proCode(formId);
        return AjaxResult.success(request,downLoadUrl);
    }






}
