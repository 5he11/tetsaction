package com.frenzy.sso.action;

import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.utils.yftools;
import com.frenzy.sso.domain.SsoForm;
import com.frenzy.sso.entity.po.SsoFormTempletFieldPo;
import com.frenzy.sso.entity.po.SsoFormTempletPo;
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

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SsoFormTempletFieldAction {

    public static final String API_PREFIX = "sso/formtempletfield";

    @Autowired
    private SsoFormService formService;




    @ApiOperation(value = "获取模板字段列表")
    @FunctionName("ssoTempletFieldList")
    public Object ssoTempletFieldList(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/list", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("formTempletId") String formTempletId, // 绑定路径参数
            final ExecutionContext context) {

        SsoForm ssoForm = formService.getFormByFormTempletId(formTempletId);
        yftools.chkNullException(ssoForm,"表单不存在");
        SsoFormTempletPo formTemplet = formService.getFormTempletByFormTempletIdForm(ssoForm, formTempletId);
        List<SsoFormTempletFieldPo> allField = formService.findTempletFieldByTempletId(ssoForm, formTempletId);

        HashMap<String,Object> ajax = new HashMap<>();
        ajax.put("formTemplet", formTemplet);
        ajax.put("allField", allField);
        return AjaxResult.success(request, ajax);
    }



    @ApiOperation(value = "修改模板字段")
    @FunctionName("ssoTempletFieldEdit")
    public Object ssoTempletFieldEdit(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/edit", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<List<SsoFormTempletFieldPo>>> request,
            final ExecutionContext context) {

        List<SsoFormTempletFieldPo> formTempletFields = request.getBody().get();

        formService.templetFieldEdit(formTempletFields);
        return AjaxResult.success(request);
    }



}
