package com.frenzy.sso.action;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.frenzy.core.config.CosmosConfig;
import com.frenzy.core.entity.AjaxResult;
import com.frenzy.core.entity.PageData;
import com.frenzy.core.enums.EnumDelFlag;
import com.frenzy.core.enums.EnumFieldType;
import com.frenzy.core.enums.EnumFromTempletType;
import com.frenzy.core.utils.SecurityUtils;
import com.frenzy.core.utils.YFLocalDateTimeUtil;
import com.frenzy.core.utils.yftools;
import com.frenzy.sso.domain.SsoForm;
import com.frenzy.sso.domain.YfDictionary;
import com.frenzy.sso.entity.po.SsoFormFieldPo;
import com.frenzy.sso.entity.po.SsoFormTempletFieldPo;
import com.frenzy.sso.entity.po.SsoFormTempletPo;
import com.frenzy.sso.service.*;
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

import java.util.*;

@Slf4j
@Component
public class SsoTpAction {

    public static final String API_PREFIX = "sso/tp";

    @Autowired
    private SsoFormService formService;
    @Autowired
    private SsoFormTempletFieldService formTempletFieldService;
    @Autowired
    private YfDictionaryGroupService dictionaryService;
    @Autowired
    private SsoSqlService sqlService;
    @Autowired
    private SsoTpService tpService;


    @ApiOperation(value = "获取表单列表")
    @FunctionName("ssoTpList")
    public Object ssoTpList(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX + "/list", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<PageData>> request,
            @BindingName("tp") String tp, // 绑定路径参数
            final ExecutionContext context) {

        PageData pd = new PageData();
        if (!request.getBody().isEmpty()) {
            pd = request.getBody().get();
        }

        SsoForm form = formService.getFormByFormTempletId(tp);
        SsoFormTempletPo formTemplet = formService.getFormTempletByFormTempletIdForm(form,tp);

        List<SsoFormTempletFieldPo> templetFields = formTempletFieldService.findTempletFieldByFormTempletId(form.getFormFieldList(), formTemplet.getFormTempletFieldList(), tp);


        String SqlHasPage = tpService.getSQL(templetFields, formTemplet, form, pd, true);
        String SqlCount = tpService.getSQL(templetFields, formTemplet, form, pd, false);



//        startPage();
        List<PageData> res = sqlService.getSqlList(SqlHasPage);
        int total = sqlService.getSqlListCount(SqlCount);
        return AjaxResult.getDataTable(res, total);
    }


    @ApiOperation(value = "获取表单列表")
    @FunctionName("ssoAddFromResultSearch")
    public Object ssoAddFromResultSearch(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX + "/add_from_result_search", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<PageData>> request,
            @BindingName("formTempletId") String formTempletId, @BindingName("fieldId") String fieldId, @BindingName("formId") String formId, @BindingName("keyword") String keyword,
            final ExecutionContext context) {


        SsoFormFieldPo formField=formService.getFormFieldByFormFieldId(formId, fieldId);
        yftools.chkNullException(fieldId,"数据异常");

        List<PageData> formValueList = sqlService.getSqlList(formField.getParentFormName(), formField.getParentFieldName(), 100, " and c."+formField.getParentFieldName()+" like '%"+keyword+"%' ");
        PageData ajax = new PageData();
        ajax.put("formValueList", formValueList);

        return AjaxResult.success(request, ajax);
    }




    @ApiOperation(value = "获取列表配置信息")
    @FunctionName("ssoTpListConfig")
    public Object ssoTpListConfig(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX + "/config/{formTempletId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<PageData>> request,
            @BindingName("formTempletId") String formTempletId, // 绑定路径参数
            final ExecutionContext context) {

        SsoForm form = formService.getFormByFormTempletId(formTempletId);//.getById(formTemplet.getFormId());
        SsoFormTempletPo formTemplet = formService.getFormTempletByFormTempletIdForm(form, formTempletId);

        List<SsoFormTempletFieldPo> listShowField = formTempletFieldService.findTempletFieldByFormTempletId(form.getFormFieldList(), formTemplet.getFormTempletFieldList(), formTempletId);


        List<SsoFormTempletFieldPo> searchShowField = new ArrayList<>();

        PageData queryParams = new PageData();
        queryParams.put("pageNum", 1);
        queryParams.put("pageSize", 10);
        queryParams.put("formName", "undefined");
        queryParams.put("tableName", "undefined");
        queryParams.put("formType", "undefined");
        queryParams.put("tp", formTempletId);


        for (SsoFormTempletFieldPo formField2TempletField : listShowField) {
            PageData params = new PageData();

            String fieldType = formField2TempletField.getFieldType();
            if (fieldType.equals(EnumFieldType.FormSelectMutiTag.getValue())//表单多标签(checkbox)
                    || fieldType.equals(EnumFieldType.FormSelectSignle.getValue())//表单下拉列表
                    || fieldType.equals(EnumFieldType.FormSelectSingleComponent.getValue())//表单单选组件
            ) {
                List<PageData> formValueList = new ArrayList<>();
                PageData all = new PageData();
                all.put("id", "");
                all.put("showValue", "全部");
                formValueList.add(all);
                formValueList.addAll(sqlService.getSqlList(formField2TempletField.getParentFormName(), formField2TempletField.getParentFieldName(), 100, ""));
                params = new PageData();
                params.put("formValueList", formValueList);
                formField2TempletField.setParams(params);
            } else if (fieldType.equals(EnumFieldType.FromSelectMutiTable.getValue())//表单多选列表
                    || fieldType.equals(EnumFieldType.FormSelectMuti.getValue())//表单多选
                    || fieldType.equals(EnumFieldType.FromSelectMutiComponent.getValue())//表单多选组件
            ) {
                List<PageData> formValueList = new ArrayList<>();
                PageData all = new PageData();
                all.put("id", "");
                all.put("showValue", "全部");
                formValueList.add(all);
                formValueList.addAll(sqlService.getSqlList(formField2TempletField.getParentFormName(), formField2TempletField.getParentFieldName(), 100, ""));
                params = new PageData();
                params.put("formValueList", formValueList);
                formField2TempletField.setParams(params);
            } else if (fieldType.equals(EnumFieldType.DictionaryRadio.getValue())//字典单选
                    || fieldType.equals(EnumFieldType.DictionarySelect.getValue())//字典下拉列表(select)
                    || fieldType.equals(EnumFieldType.DictionaryCheckbox.getValue())//字典复选框(checkbox)
                    || fieldType.equals(EnumFieldType.DictionarySelectMutiTag.getValue())) {//字典多标签(checkbox)
                List<YfDictionary> dictionaryList = new ArrayList<>();
                YfDictionary alldict = new YfDictionary();
                alldict.setDictValue("");
                alldict.setShowValue("全部");
                dictionaryList.add(alldict);

                dictionaryList.addAll(dictionaryService.listAllbyKey(formField2TempletField.getDictionary()));
                params = new PageData();
                params.put("dictionaryList", dictionaryList);
                formField2TempletField.setParams(params);
            } else if (fieldType.equals(EnumFieldType.TextSelect.getValue())//下拉列表(select)
                    || fieldType.equals(EnumFieldType.TextRadio.getValue())//单选框(radio)
                    || fieldType.equals(EnumFieldType.TextCheckbox.getValue())) {//复选框(checkbox)

                if (StrUtil.isNotEmpty(formField2TempletField.getOptions())) {
                    JSONObject jsonObject2 = new JSONObject(formField2TempletField.getOptions());
                    List<String> optionList = Arrays.asList(String.valueOf(jsonObject2.get("options")).split(","));
                    List<String> arr = new ArrayList<>();
                    arr.add("全部");
                    arr.addAll(optionList);
                    params = new PageData();
                    params.put("optionsList", arr);
                    formField2TempletField.setParams(params);
                }

            } else if (fieldType.equals(EnumFieldType.Authen.getValue())) {//审核开关
                List<YfDictionary> dictionaryList = new ArrayList<>();

                YfDictionary alldict = new YfDictionary();
                alldict.setDictValue("");
                alldict.setShowValue("全部");
                dictionaryList.add(alldict);

                dictionaryList.addAll(dictionaryService.listAllbyKey("TrueFalse"));
                params = new PageData();
                params.put("dictionaryList", dictionaryList);
                formField2TempletField.setParams(params);
            }

            if (yftools.isEquals(formField2TempletField.getListSearch(), "YES")) {
                SsoFormTempletFieldPo searchField = new SsoFormTempletFieldPo();
                BeanUtils.copyProperties(formField2TempletField, searchField);
                searchShowField.add(searchField);
                queryParams.put(formField2TempletField.getFieldName(), "");
            }

        }


        HashMap<String, Object> ajax = new HashMap<>();
        ajax.put("formTemplet", JSON.parse(JSON.toJSONString(formTemplet)));
        ajax.put("form", JSON.parse(JSON.toJSONString(form)));
        ajax.put("listShowField", JSON.parseArray(JSON.toJSONString(listShowField)));
        ajax.put("searchShowField", JSON.parseArray(JSON.toJSONString(searchShowField)));
        ajax.put("queryParams", JSON.parse(JSON.toJSONString(queryParams)));

        //列出表单列表
        return AjaxResult.success(request, ajax);

    }

    @ApiOperation(value = "获取新增配置信息")
    @FunctionName("ssoTpAddConfig")
    public Object ssoTpAddConfig(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX + "/add_config/{formTempletId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("formTempletId") String formTempletId, // 绑定路径参数
            final ExecutionContext context) {

        SsoForm form = formService.getFormByFormTempletId(formTempletId);//.getById(formTemplet.getFormId());
        SsoFormTempletPo formTemplet = formService.getFormTempletByFormTempletIdForm(form, formTempletId);
        yftools.chkNullException(formTemplet,"模板不存在");

        List<SsoFormTempletFieldPo> templetFields = formTempletFieldService.findTempletFieldByFormTempletId(form.getFormFieldList(), formTemplet.getFormTempletFieldList(), formTempletId);



        PageData rules = new PageData();
        PageData formData = new PageData();


        for (SsoFormTempletFieldPo templetField : templetFields) {
            tpService.getConfigForTp(templetField, rules, formData, EnumFromTempletType.ADD);
//            rules = setRules(rules, templetField);
        }


        HashMap<String,Object> ajaxResult = new HashMap<>();
        ajaxResult.put("templetFields", templetFields);
        ajaxResult.put("rules", rules);
        ajaxResult.put("formData", formData);

        return AjaxResult.success(request,ajaxResult);

    }



    @ApiOperation(value = "获取编辑配置信息")
    @FunctionName("ssoTpEditConfig")
    public Object ssoTpEditConfig(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX + "/edit_config/{formTempletId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<PageData>> request,
            @BindingName("formTempletId") String formTempletId, // 绑定路径参数
            @BindingName("id") String id,
            final ExecutionContext context) {

        SsoForm form = formService.getFormByFormTempletId(formTempletId);//.getById(formTemplet.getFormId());
        SsoFormTempletPo formTemplet = formService.getFormTempletByFormTempletIdForm(form, formTempletId);
        yftools.chkNullException(formTemplet,"模板不存在");

        List<SsoFormTempletFieldPo> templetFields = formTempletFieldService.findTempletFieldByFormTempletId(form.getFormFieldList(), formTemplet.getFormTempletFieldList(), formTempletId);




        PageData formData = sqlService.getSqlOne(form.getFormName(), id, "");
        yftools.chkNullException(formData,"数据不存在");



        PageData rules = new PageData();
//        PageData formData=new PageData();


        for (SsoFormTempletFieldPo templetField : templetFields) {
            tpService.getConfigForTp(templetField, rules, formData, EnumFromTempletType.EDIT);
            rules = setEditRules(rules, templetField);
        }


        PageData ajaxResult = new PageData();
        ajaxResult.put("templetFields", templetFields);
        ajaxResult.put("rules", rules);
        ajaxResult.put("formData", formData);

        return AjaxResult.success(request,ajaxResult);
    }


    public PageData setRules(PageData rules,SsoFormTempletFieldPo templetField){

        if (yftools.isEquals(templetField.getAddMustFill(), "YES")){
            List<PageData> rule=new ArrayList<>();
            PageData rule_item=new PageData();
            rule_item.put("required", true);
            rule_item.put("message", templetField.getFieldTitle()+"不能为空");
            rule_item.put("trigger", "blur");
            rule.add(rule_item);
            rules.put(templetField.getFieldName(),rule);
        }

        return rules;
    }

    public PageData setEditRules(PageData rules,SsoFormTempletFieldPo templetField){

        if (yftools.isEquals(templetField.getEditMustFill(), "YES")){
            List<PageData> rule=new ArrayList<>();
            PageData rule_item=new PageData();
            rule_item.put("required", true);
            rule_item.put("message", templetField.getFieldTitle()+"不能为空");
            rule_item.put("trigger", "blur");
            rule.add(rule_item);
            rules.put(templetField.getFieldName(),rule);
        }

        return rules;
    }


    @ApiOperation(value = "保存新增数据")
    @FunctionName("ssoTpAddSave")
    public Object ssoTpAddSave(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX + "/addsave", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<List<SsoFormTempletFieldPo>>> request,
            final ExecutionContext context) {

        List<SsoFormTempletFieldPo> field2TempletFieldList = new ArrayList<>();
        if (!request.getBody().isEmpty()){
            field2TempletFieldList = request.getBody().get();
        }

        SsoForm form = formService.getFormByFormTempletId(field2TempletFieldList.get(0).getTempletId());//.getById(formTemplet.getFormId());
        SsoFormTempletPo formTemplet = formService.getFormTempletByFormTempletIdForm(form, field2TempletFieldList.get(0).getTempletId());
        yftools.chkNullException(formTemplet,"模板不存在");

        PageData saveItem = new PageData();

        for(SsoFormTempletFieldPo templetField : field2TempletFieldList) {
            if(yftools.isEquals(templetField.getOnlyOne(),"YES")){
                PageData ckpd=sqlService.getSqlOneByField(form.getFormName(),templetField.getFieldName(),templetField.getRemark());
                if (ckpd!=null)
                    return AjaxResult.error(templetField.getFieldTitle()+"已存在");
            }

            if (templetField.getFieldType().equals(EnumFieldType.PasswordMd5.getValue())){//密码
                if (!yftools.isEmpty(templetField.getRemark())){
                    saveItem.put(templetField.getFieldName(),SecurityUtils.encryptPassword(templetField.getRemark()));
                }else{
                    saveItem.put(templetField.getFieldName(),"");
                }
            }else if (templetField.getFieldType().equals(EnumFieldType.ProvenceCityArea.getValue())){//省市区联动
                if (!yftools.isEmpty(templetField.getRemark())){
                    String[] arr=templetField.getRemark().split(",");
                    saveItem.put("yfProvence", arr[0]);
                    saveItem.put("yfCity", arr[1]);
                    saveItem.put("yfArea", arr[2]);
                    saveItem.put(templetField.getFieldName(), templetField.getRemark());
                }
            }else{
                if (!yftools.isEmpty(templetField.getRemark())){
                    saveItem.put(templetField.getFieldName(), templetField.getRemark());
                }
            }
        }


        saveItem.put("id", UUID.fastUUID().toString());
        saveItem.put("keyName", form.getFormName());
        saveItem.put("partitionKey", CosmosConfig.getPartitionKeyPrefix() + form.getFormName());
        saveItem.put("delFlag", EnumDelFlag.NORMAL.getValue());
        saveItem.put("createTime",YFLocalDateTimeUtil.now());
        sqlService.save(saveItem);
        return AjaxResult.success(request);

    }


    @ApiOperation(value = "保存新增数据")
    @FunctionName("ssoTpEditSave")
    public Object ssoTpEditSave(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX + "/editsave", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<List<SsoFormTempletFieldPo>>> request,
            final ExecutionContext context) {

        List<SsoFormTempletFieldPo> field2TempletFieldList = new ArrayList<>();
        if (!request.getBody().isEmpty()){
            field2TempletFieldList = request.getBody().get();
        }

        SsoForm form = formService.getFormByFormTempletId(field2TempletFieldList.get(0).getTempletId());//.getById(formTemplet.getFormId());
        SsoFormTempletPo formTemplet = formService.getFormTempletByFormTempletIdForm(form, field2TempletFieldList.get(0).getTempletId());
        yftools.chkNullException(formTemplet,"模板不存在");

        PageData saveItem = sqlService.getSqlOne(form.getFormName(), field2TempletFieldList.get(0).getSearchValue(), "");
        yftools.chkNullException(saveItem,"数据不存在");

        for(SsoFormTempletFieldPo templetField : field2TempletFieldList) {
            if(yftools.isEquals(templetField.getOnlyOne(),"YES")){
                PageData ckpd=sqlService.getSqlOneByField(form.getFormName(),templetField.getFieldName(),templetField.getRemark());
                if (ckpd!=null){
                    if (!yftools.isEquals(ckpd.getString("id"),field2TempletFieldList.get(0).getSearchValue())){
                        return AjaxResult.error(templetField.getFieldTitle()+"已存在");
                    }
                }
            }

            if (templetField.getFieldType().equals(EnumFieldType.PasswordMd5.getValue())){//密码
                if (!yftools.isEmpty(templetField.getRemark())){
                    if (!yftools.isEquals(templetField.getRemark(),saveItem.getString("templetField.getFieldName()"))){
                        saveItem.put(templetField.getFieldName(),SecurityUtils.encryptPassword(templetField.getRemark()));
                    }
                }
            }else if (templetField.getFieldType().equals(EnumFieldType.ProvenceCityArea.getValue())){//省市区联动
                if (!yftools.isEmpty(templetField.getRemark())){
                    String[] arr=templetField.getRemark().split(",");
                    saveItem.put("yfProvence", arr[0]);
                    saveItem.put("yfCity", arr[1]);
                    saveItem.put("yfArea", arr[2]);
                    saveItem.put(templetField.getFieldName(), templetField.getRemark());
                }
            }else{
                if (!yftools.isEmpty(templetField.getRemark())){
                    saveItem.put(templetField.getFieldName(), templetField.getRemark());
                }
            }
        }


        saveItem.put("updateTime",YFLocalDateTimeUtil.now());
        sqlService.update(saveItem);

        return AjaxResult.success(request, saveItem);



    }



    @ApiOperation(value = "批量删除")
    @FunctionName("ssoTpRemove")
    public Object ssoTpRemove(
            @HttpTrigger(name = "httpRequest", route = API_PREFIX+"/{formTempletId}/{postId}", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("formTempletId") String formTempletId, // 绑定路径参数
            @BindingName("postId") String postId,
            final ExecutionContext context) {
        String[] postIds = postId.split(",");

        SsoForm form = formService.getFormByFormTempletId(formTempletId);//.getById(formTemplet.getFormId());
        SsoFormTempletPo formTemplet = formService.getFormTempletByFormTempletIdForm(form, formTempletId);
        yftools.chkNullException(formTemplet,"模板不存在");

        if (yftools.isEquals(form.getIsMarkDel(), "YES")){
            for (String pos:postIds){
                PageData item = sqlService.getSqlOne(form.getFormName(), pos, "");
                item.put("delFlag", EnumDelFlag.DEL.getValue());
                item.put("updateTime",YFLocalDateTimeUtil.now());
                sqlService.update(item);
            }
        }else{
            for (String pos:postIds){
                sqlService.delete(pos,form.getFormName());
            }
        }

        return AjaxResult.success(request);
    }







}
