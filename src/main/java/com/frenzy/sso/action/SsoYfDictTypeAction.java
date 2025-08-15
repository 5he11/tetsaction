package com.frenzy.sso.action;


import com.frenzy.core.entity.AjaxResult;
import com.frenzy.sso.domain.SsoForm;
import com.frenzy.sso.domain.YfDictionary;
import com.frenzy.sso.domain.YfDictionaryGroup;
import com.frenzy.sso.service.SsoFormService;
import com.frenzy.sso.service.YfDictionaryGroupService;
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

import java.util.*;


/**
 * 数据字典信息
 *
 * @author ruoyi
 */
@Slf4j
@Component
public class SsoYfDictTypeAction {

    public static final String API_PREFIX = "sso/yfdict/type";


    @Autowired
    private YfDictionaryGroupService dictionaryService;
    @Autowired
    private SsoFormService formService;


    @ApiOperation(value = "获取参数配置列表")
    @FunctionName("ssoAllDictTypeList")
    public Object ssoAllDictTypeList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/allDictList", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        List<YfDictionaryGroup> dictionaryList=dictionaryService.listAll();
        return AjaxResult.success(request, dictionaryList);
    }


    @ApiOperation(value = "获取参数配置列表")
    @FunctionName("ssoDictTypeList")
    public Object ssoDictTypeList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/list", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<YfDictionary>> request,
            final ExecutionContext context) {

        YfDictionary dict = new YfDictionary();
        if (!request.getBody().isEmpty()){
            dict = request.getBody().get();
        }
        List<YfDictionaryGroup> dictionaryList=dictionaryService.selectDictList(dict,true);
        return AjaxResult.success(request, dictionaryList);
    }


    @ApiOperation(value = "字典导出")
    @FunctionName("ssoDictTypeExport")
    public Object ssoDictTypeExport(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/export", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context)  throws Exception{


        List<YfDictionaryGroup> dictionaryList=dictionaryService.listAll();
        Map<String, List<YfDictionary>> dictMap = new HashMap<>();
        for (YfDictionaryGroup dictionaryGroup:dictionaryList){
            dictMap.put(dictionaryGroup.getDictKey(), dictionaryGroup.getDictList());
        }

        List<YfDictionary> formDictionaryList = new ArrayList<>();
        List<SsoForm> formList=formService.listAll();
        formList.forEach(form -> {
            YfDictionary dictionary = new YfDictionary();
            dictionary.setDictKey("FormList");
            dictionary.setDictValue(form.getFormName());
            dictionary.setShowValue(form.getFormTitle());
            formDictionaryList.add(dictionary);
        });
        dictMap.put("FormList",formDictionaryList);

        String downLoadUrl = dictionaryService.proCode(dictMap);
        return AjaxResult.success(request,downLoadUrl);
    }





    @ApiOperation(value = "查询字典类型详细")
    @FunctionName("ssoDictTypeGetInfo")
    public Object ssoConfigGetInfo(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/info/{dictId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("dictId") String dictId, // 绑定路径参数
            final ExecutionContext context) {
        return AjaxResult.success(request, dictionaryService.getById(dictId));
    }


}
