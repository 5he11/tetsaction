package com.frenzy.sso.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.frenzy.sso.entity.po.SsoFormFieldPo;
import com.frenzy.sso.entity.po.SsoFormTempletFieldPo;
import com.frenzy.sso.entity.po.SsoFormTempletPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * 登录校验方法
 * 
 * @author yf
 */
@Slf4j
@Component
public class SsoFormTempletFieldService
{

    public SsoFormTempletPo delTempletField(SsoFormTempletPo ssoFormTempletPo, String templetFieldId){
        List<SsoFormTempletFieldPo> templetFieldList = ssoFormTempletPo.getFormTempletFieldList();
        if (CollectionUtil.isNotEmpty(templetFieldList)){
            templetFieldList.removeIf(item -> templetFieldId.equals(item.getId()));
        }
        ssoFormTempletPo.setFormTempletFieldList(templetFieldList);
        return ssoFormTempletPo;
    }


    public SsoFormTempletPo saveTempletField(SsoFormTempletPo ssoFormTempletPo, SsoFormTempletFieldPo templetField){
        List<SsoFormTempletFieldPo> templetFieldList = ssoFormTempletPo.getFormTempletFieldList();
        if (CollectionUtil.isEmpty(templetFieldList)){
            templetFieldList = new ArrayList<>();
        }
        templetField.setFormId(ssoFormTempletPo.getFormId());
        templetFieldList.add(templetField);
        ssoFormTempletPo.setFormTempletFieldList(templetFieldList);
        return ssoFormTempletPo;
    }

    public SsoFormTempletPo updateTempletField(SsoFormTempletPo ssoFormTempletPo, SsoFormTempletFieldPo templetField){
        List<SsoFormTempletFieldPo> templetFieldList = ssoFormTempletPo.getFormTempletFieldList();
        if (CollectionUtil.isNotEmpty(templetFieldList)){
            for (SsoFormTempletFieldPo templetFieldPo:templetFieldList){
                if (StrUtil.equals(templetFieldPo.getId(),templetField.getId())){
                    BeanUtils.copyProperties(templetFieldPo,templetField);
                }
            }
        }
        ssoFormTempletPo.setFormTempletFieldList(templetFieldList);
        return ssoFormTempletPo;
    }




    public List<SsoFormTempletFieldPo> findTempletFieldByFormTempletId(List<SsoFormFieldPo> formFieldList, List<SsoFormTempletFieldPo> formTempletFieldList, String templetId){
        List<SsoFormTempletFieldPo> templetFieldList= new ArrayList<>();

        for (SsoFormFieldPo formField:formFieldList){

            SsoFormTempletFieldPo hasSsoFormTempletField = null;
            for (SsoFormTempletFieldPo ssoFormTempletField:formTempletFieldList){
                if (StrUtil.equals(ssoFormTempletField.getFieldId(),formField.getId())){
                    hasSsoFormTempletField = ssoFormTempletField;
                }
            }

            if (hasSsoFormTempletField != null){
                hasSsoFormTempletField.setFieldTitle(formField.getFieldTitle());
                hasSsoFormTempletField.setFieldName(formField.getFieldName());
                hasSsoFormTempletField.setFieldType(formField.getFieldType());
                hasSsoFormTempletField.setFieldLength(formField.getFieldLength());
                hasSsoFormTempletField.setFieldDefault(formField.getFieldDefault());
                hasSsoFormTempletField.setFieldDecimalPoint(formField.getFieldDecimalPoint());
                hasSsoFormTempletField.setOptions(formField.getOptions());
                hasSsoFormTempletField.setParentFormName(formField.getParentFormName());
                hasSsoFormTempletField.setParentFieldName(formField.getParentFieldName());
                hasSsoFormTempletField.setParentTpId(formField.getParentTpId());
                hasSsoFormTempletField.setDictionary(formField.getDictionary());
                hasSsoFormTempletField.setIsSystem(formField.getIsSystem());
                hasSsoFormTempletField.setRegexpCheck(formField.getRegexpCheck());
                hasSsoFormTempletField.setJsonConfig(formField.getJsonConfig());
                hasSsoFormTempletField.setIsProtect(formField.getIsProtect());
                hasSsoFormTempletField.setIsIndex(formField.getIsIndex());
                hasSsoFormTempletField.setFieldNameCamel(formField.getFieldNameCamel());
                hasSsoFormTempletField.setParamType(formField.getParamType());

                templetFieldList.add(hasSsoFormTempletField);
            }

        }


        return templetFieldList;
    }



}
