package com.frenzy.sso.action;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.frenzy.core.config.CosmosConfig;
import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.utils.ValidationUtil;
import com.frenzy.core.utils.YFLocalDateTimeUtil;
import com.frenzy.sso.domain.YfDictionary;
import com.frenzy.sso.domain.YfDictionaryGroup;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 数据字典信息
 *
 * @author ruoyi
 */
@Slf4j
@Component
public class SsoYfDictDataAction {

    public static final String API_PREFIX = "sso/yfdict/data";


    @Autowired
    private YfDictionaryGroupService dictionaryService;


    @ApiOperation(value = "获取参数配置列表")
    @FunctionName("ssoAllDictDataList")
    public Object ssoAllDictDataList(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/list", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<YfDictionary>> request,
            final ExecutionContext context) {

        YfDictionary dictData = new YfDictionary();
        if (!request.getBody().isEmpty()){
            dictData = request.getBody().get();
        }


        YfDictionaryGroup dictionaryGroup=dictionaryService.getGroupByKey(dictData.getDictKey());
        List<YfDictionary> list = dictionaryGroup.getDictList();
        return AjaxResult.success(request, list);
    }



    @ApiOperation(value = "获取参数配置列表")
    @FunctionName("ssoDictDataListDict")
    public Object ssoDictDataListDict(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/listDict", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        List<YfDictionaryGroup> dictionaryList=dictionaryService.listAll();
        return AjaxResult.success(request, dictionaryList);
    }



    @ApiOperation(value = "查询字典数据详细")
    @FunctionName("ssoDictDataGetInfo")
    public Object ssoDictDataGetInfo(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/info/{dictCode}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("dictCode") String dictCode, // 绑定路径参数
            final ExecutionContext context) {
        return AjaxResult.success(request, dictionaryService.getById(dictCode));
    }



    @ApiOperation(value = "根据字典类型查询字典数据信息")
    @FunctionName("ssoDictDataDictType")
    public Object ssoDictDataDictType(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/dictType/{dictType}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("dictType") String dictType, // 绑定路径参数
            final ExecutionContext context) {
        List<YfDictionary> dictionaryList=dictionaryService.listAllbyKey(dictType);
        return AjaxResult.success(request,dictionaryList);
    }


    @ApiOperation(value = "根据字典类型查询字典数据信息")
    @FunctionName("ssoDictDataDictType1")
    public Object ssoDictDataDictType1(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/type/{dictType}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("dictType") String dictType, // 绑定路径参数
            final ExecutionContext context) {
        List<YfDictionary> dictionaryList=dictionaryService.listAllbyKey(dictType);
        return AjaxResult.success(request,dictionaryList);
    }



    @ApiOperation(value = "新增参数配置")
    @FunctionName("ssoDictDataAdd")
    public Object ssoDictDataAdd(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/add", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<YfDictionary>> request,
            final ExecutionContext context) {

        YfDictionary dict = request.getBody().get();
        ValidationUtil.validate(dict);

        YfDictionaryGroup yfDictionaryGroup = dictionaryService.getGroupByKey(dict.getDictKey());
        List<YfDictionary> list = yfDictionaryGroup.getDictList();
        dict.setId(UUID.fastUUID().toString());
        list.add(dict);
        yfDictionaryGroup.setDictList(list);
        dictionaryService.update(yfDictionaryGroup);
        return AjaxResult.success(request);
    }


    @ApiOperation(value = "修改保存字典类型")
    @FunctionName("ssoDictDataEdit")
    public Object ssoDictDataEdit(
            @HttpTrigger(name = "httpRequest", route =  API_PREFIX+"/edit", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<YfDictionary>> request,
            final ExecutionContext context) {

        YfDictionary dict = request.getBody().get();
        ValidationUtil.validate(dict);

        YfDictionaryGroup yfDictionaryGroup = dictionaryService.getGroupByKey(dict.getDictKey());
        List<YfDictionary> list = yfDictionaryGroup.getDictList();
        if (CollectionUtil.isNotEmpty(list)){
            for (YfDictionary dictionary:list){
                if (StrUtil.equals(dictionary.getDictValue(),dict.getDictValue())){
                    BeanUtils.copyProperties(dict,dictionary);
                }
            }
        }else{
            list = new ArrayList<>();
            list.add(dict);
        }

        yfDictionaryGroup.setDictList(list);
        dictionaryService.update(yfDictionaryGroup);
        return AjaxResult.success(request);

    }


    @ApiOperation(value = "删除字典类型")
    @FunctionName("ssoDictDataRemove")
    public Object ssoDictDataRemove(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/{dictCodes}", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("dictCodes") String dictCodes, // 绑定路径参数
            final ExecutionContext context) {

        YfDictionaryGroup yfDictionaryGroup = dictionaryService.getGroupByDictId(dictCodes);
        List<YfDictionary> list = yfDictionaryGroup.getDictList();

        list.removeIf(item -> dictCodes.equals(item.getId()));

        yfDictionaryGroup.setDictList(list);
        dictionaryService.update(yfDictionaryGroup);

        return AjaxResult.success(request);
    }


}
