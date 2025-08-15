package com.frenzy.sso.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.frenzy.core.config.CosmosConfig;
import com.frenzy.core.entity.PageData;
import com.frenzy.core.enums.EnumDelFlag;
import com.frenzy.core.enums.EnumFieldType;
import com.frenzy.core.enums.EnumFromTempletType;
import com.frenzy.core.utils.yftools;
import com.frenzy.sso.domain.SsoForm;
import com.frenzy.sso.domain.YfDictionary;
import com.frenzy.sso.entity.po.SsoFormTempletFieldPo;
import com.frenzy.sso.entity.po.SsoFormTempletPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 登录校验方法
 * 
 * @author yf
 */
@Slf4j
@Component
public class SsoTpService
{

    @Autowired
    public YfDictionaryGroupService dictionaryService;
    @Autowired
    public SsoSqlService sqlService;

    public void getConfigForTp(SsoFormTempletFieldPo templetField, PageData rules, PageData formData, EnumFromTempletType templetType){
        List<YfDictionary> dictionaryList=new ArrayList<>();
        List<PageData> formValueList=new ArrayList<>();
        PageData params=new PageData();
        String fieldType=templetField.getFieldType();
        if (fieldType.equals(EnumFieldType.DateTime.getValue()) || fieldType.equals(EnumFieldType.LocalDateTime.getValue())){//日期时间(text)
            if (templetType.getValue().equals(EnumFromTempletType.ADD.getValue())){
                if (yftools.isEquals(templetField.getAddDefaultValue(),"NOW()"))
                    formData.put(templetField.getFieldName(), DateUtil.now());
                else if (!yftools.isEmpty(templetField.getAddDefaultValue()))
                    formData.put(templetField.getFieldName(),templetField.getAddDefaultValue());
                else
                    formData.put(templetField.getFieldName(),"");
            }else if (templetType.getValue().equals(EnumFromTempletType.EDIT.getValue()) || templetType.getValue().equals(EnumFromTempletType.INFO.getValue())){
                if (yftools.isEmpty(formData.getString(templetField.getFieldName()))){
                    if (yftools.isEquals(templetField.getEditDefaultValue(),"NOW()"))
                        formData.put(templetField.getFieldName(),DateUtil.now());
                    else if (!yftools.isEmpty(templetField.getEditDefaultValue()))
                        formData.put(templetField.getFieldName(),templetField.getEditDefaultValue());
                    else
                        formData.put(templetField.getFieldName(),"");
                }
            }

        }else if (fieldType.equals(EnumFieldType.DateYYYYMMDD.getValue()) || fieldType.equals(EnumFieldType.LocalDate.getValue())) {//日期(text)
            if (templetType.getValue().equals(EnumFromTempletType.ADD.getValue())){
                if (yftools.isEquals(templetField.getAddDefaultValue(),"NOW()"))
                    formData.put(templetField.getFieldName(),DateUtil.format(DateUtil.date(), DatePattern.NORM_DATE_FORMATTER));
                else if (!yftools.isEmpty(templetField.getAddDefaultValue()))
                    formData.put(templetField.getFieldName(),templetField.getAddDefaultValue());
                else
                    formData.put(templetField.getFieldName(),"");
            }else if (templetType.getValue().equals(EnumFromTempletType.EDIT.getValue()) || templetType.getValue().equals(EnumFromTempletType.INFO.getValue())){
                if (yftools.isEmpty(formData.getString(templetField.getFieldName()))){
                    if (yftools.isEquals(templetField.getEditDefaultValue(),"NOW()"))
                        formData.put(templetField.getFieldName(),DateUtil.format(DateUtil.date(), DatePattern.NORM_DATE_FORMATTER));
                    else if (!yftools.isEmpty(templetField.getEditDefaultValue()))
                        formData.put(templetField.getFieldName(),templetField.getEditDefaultValue());
                    else
                        formData.put(templetField.getFieldName(),"");
                }
            }

        }else if (fieldType.equals(EnumFieldType.TimeOnly.getValue()) || fieldType.equals(EnumFieldType.LocalTime.getValue())) {//时间(text)
            if (templetType.getValue().equals(EnumFromTempletType.ADD.getValue())){
                if (yftools.isEquals(templetField.getAddDefaultValue(),"NOW()"))
                    formData.put(templetField.getFieldName(),DateUtil.format(DateUtil.date(), DatePattern.NORM_TIME_FORMATTER));
                else if (!yftools.isEmpty(templetField.getAddDefaultValue()))
                    formData.put(templetField.getFieldName(),templetField.getAddDefaultValue());
                else
                    formData.put(templetField.getFieldName(),"");
            }else if (templetType.getValue().equals(EnumFromTempletType.EDIT.getValue()) || templetType.getValue().equals(EnumFromTempletType.INFO.getValue())){
                if (yftools.isEmpty(formData.getString(templetField.getFieldName()))){
                    if (yftools.isEquals(templetField.getEditDefaultValue(),"NOW()"))
                        formData.put(templetField.getFieldName(),DateUtil.format(DateUtil.date(), DatePattern.NORM_TIME_FORMATTER));
                    else if (!yftools.isEmpty(templetField.getEditDefaultValue()))
                        formData.put(templetField.getFieldName(),templetField.getEditDefaultValue());
                    else
                        formData.put(templetField.getFieldName(),"");
                }
            }

        }else if (fieldType.equals(EnumFieldType.TimeStamp.getValue())) {//时间戳(text)
            if (templetType.getValue().equals(EnumFromTempletType.ADD.getValue())){
                if (yftools.isEquals(templetField.getAddDefaultValue(),"NOW()"))
                    formData.put(templetField.getFieldName(),yftools.getUnixTime()+"");
                else if (!yftools.isEmpty(templetField.getAddDefaultValue()))
                    formData.put(templetField.getFieldName(),templetField.getAddDefaultValue());
                else
                    formData.put(templetField.getFieldName(),"");
            }else if (templetType.getValue().equals(EnumFromTempletType.EDIT.getValue()) || templetType.getValue().equals(EnumFromTempletType.INFO.getValue())){
                if (yftools.isEmpty(formData.getString(templetField.getFieldName()))){
                    if (yftools.isEquals(templetField.getEditDefaultValue(),"NOW()"))
                        formData.put(templetField.getFieldName(),yftools.getUnixTime()+"");
                    else if (!yftools.isEmpty(templetField.getEditDefaultValue()))
                        formData.put(templetField.getFieldName(),templetField.getEditDefaultValue());
                    else
                        formData.put(templetField.getFieldName(),"");
                }
            }

        }else if (fieldType.equals(EnumFieldType.UpLoadFile.getValue())//七牛上传文件
                || fieldType.equals(EnumFieldType.UpLoadPic.getValue())//七牛上传图片
                || fieldType.equals(EnumFieldType.AliUpLoadPic.getValue())//阿里云上传图片
                || fieldType.equals(EnumFieldType.AliUpLoadFile.getValue())//阿里云上传文件
        ) {
            if (templetType.getValue().equals(EnumFromTempletType.ADD.getValue())){
                params=new PageData();
                if (!yftools.isEmpty(templetField.getAddDefaultValue())){
                    String[] arr=templetField.getAddDefaultValue().split(",");
                    List<PageData> arrList=new ArrayList<>();
                    for (int i=0;i<arr.length;i++){
                        PageData file=new PageData();
                        file.put("name", arr[i]);
                        file.put("url", arr[i]);
                        arrList.add(file);
                    }
                    params.put("fileList",arrList);
                }else{
                    params.put("fileList",new ArrayList<>());
                }
                templetField.setParams(params);
                setDefaultValue(formData,templetField);
            }else if (templetType.getValue().equals(EnumFromTempletType.EDIT.getValue()) || templetType.getValue().equals(EnumFromTempletType.INFO.getValue())){
                params=new PageData();
                if (yftools.isEmpty(formData.getString(templetField.getFieldName()))){
                    if (!yftools.isEmpty(templetField.getEditDefaultValue())){
                        String[] arr=templetField.getEditDefaultValue().split(",");
                        List<PageData> arrList=new ArrayList<>();
                        for (int i=0;i<arr.length;i++){
                            PageData file=new PageData();
                            file.put("name", arr[i]);
                            file.put("url", arr[i]);
                            arrList.add(file);
                        }
                        params.put("fileList",arrList);
                    }else{
                        params.put("fileList",new ArrayList<>());
                    }
                    templetField.setParams(params);
                    setEditDefaultValue(formData,templetField);
                }else{
                    String[] arr=formData.getString(templetField.getFieldName()).split(",");
                    List<PageData> arrList=new ArrayList<>();
                    for (int i=0;i<arr.length;i++){
                        PageData file=new PageData();
                        file.put("name", arr[i]);
                        file.put("url", arr[i]);
                        arrList.add(file);
                    }
                    params.put("fileList",arrList);
                    templetField.setParams(params);
                }
            }

//        }else if (fieldType.equals(EnumFieldType.FormSelectMutiTag.getValue())//表单多标签(checkbox)
//                || fieldType.equals(EnumFieldType.FormSelectSignle.getValue())) {//表单下拉列表
//            if(templetField.getParentFormName().equals(DictConst.FormList.config_order_type)){
//                PageData all=new PageData();
//                all.put("id", "0");
//                all.put("showValue", "顶级");
//                formValueList=formService.getSqlList("select id," + templetField.getParentFieldName() + " as showValue from " + templetField.getParentFormName() + " where up_type ='0' order by id desc limit 0,100");
//                formValueList.add(0, all);
//                params=new PageData();
//                params.put("formValueList",formValueList);
//                templetField.setParams(params);
//            }
        }else if (fieldType.equals(EnumFieldType.FromSelectMutiTable.getValue())//表单多选列表
                || fieldType.equals(EnumFieldType.FormSelectMutiTag.getValue())//表单多标签(checkbox)
                || fieldType.equals(EnumFieldType.FormSelectSignle.getValue())//表单下拉列表
                || fieldType.equals(EnumFieldType.FormSelectMuti.getValue())//表单多选
                || fieldType.equals(EnumFieldType.FromSelectMutiComponent.getValue())//表单多选组件
                || fieldType.equals(EnumFieldType.FormSelectSingleComponent.getValue())//表单单选组件
        ) {
//            formValueList=formService.getSqlList("select id," + templetField.getParentFieldName() + " as showValue from " + templetField.getParentFormName() + " where dept_id='"+sysDeptService.getCompanyDeptByDeptId(SecurityUtils.getLoginUser())+"'  order by id desc limit 0,100");
//            formValueList=sqlService.getSqlList("select id," + templetField.getParentFieldName() + " as showValue from " + templetField.getParentFormName() + " where dept_id='"+sysDeptService.getCompanyDeptByDeptId(SecurityUtils.getLoginUser())+"'  order by id desc limit 0,100");
            formValueList = sqlService.getSqlList(templetField.getParentFormName(), templetField.getParentFieldName(), 100, "");

            params=new PageData();
            params.put("formValueList",formValueList);
            templetField.setParams(params);
            if (templetType.getValue().equals(EnumFromTempletType.ADD.getValue())){
                setDefaultValue(formData,templetField);
            }else if (templetType.getValue().equals(EnumFromTempletType.EDIT.getValue()) || templetType.getValue().equals(EnumFromTempletType.INFO.getValue())){
                if (yftools.isEmpty(formData.getString(templetField.getFieldName()))){
                    setEditDefaultValue(formData,templetField);
                }
            }

        }else if (fieldType.equals(EnumFieldType.DictionaryRadio.getValue())//字典单选
                || fieldType.equals(EnumFieldType.DictionarySelect.getValue())//字典下拉列表(select)
                || fieldType.equals(EnumFieldType.DictionaryCheckbox.getValue())//字典复选框(checkbox)
                || fieldType.equals(EnumFieldType.DictionarySelectMutiTag.getValue())) {//字典多标签(checkbox)
            dictionaryList = dictionaryService.listAllbyKey(templetField.getDictionary());
            params=new PageData();
            params.put("dictionaryList",dictionaryList);
            templetField.setParams(params);

            if (templetType.getValue().equals(EnumFromTempletType.ADD.getValue())){
                setDefaultValue(formData,templetField);
            }else if (templetType.getValue().equals(EnumFromTempletType.EDIT.getValue()) || templetType.getValue().equals(EnumFromTempletType.INFO.getValue())){
                if (yftools.isEmpty(formData.getString(templetField.getFieldName()))){
                    setEditDefaultValue(formData,templetField);
                }
            }

        }else if (fieldType.equals(EnumFieldType.TextSelect.getValue())//下拉列表(select)
                || fieldType.equals(EnumFieldType.TextRadio.getValue())//单选框(radio)
                || fieldType.equals(EnumFieldType.TextCheckbox.getValue())) {//复选框(checkbox)

            if(StrUtil.isNotEmpty(templetField.getOptions())){
                JSONObject jsonObject2 = new JSONObject(templetField.getOptions());
                List<String> optionList = Arrays.asList(String.valueOf(jsonObject2.get("options")).split(","));
                params=new PageData();
                params.put("optionsList",optionList);
                templetField.setParams(params);

                if (templetType.getValue().equals(EnumFromTempletType.ADD.getValue())){
                    setDefaultValue(formData,templetField);
                }else if (templetType.getValue().equals(EnumFromTempletType.EDIT.getValue()) || templetType.getValue().equals(EnumFromTempletType.INFO.getValue())){
                    if (yftools.isEmpty(formData.getString(templetField.getFieldName()))){
                        setEditDefaultValue(formData,templetField);
                    }
                }
            }

        }else{
            if (templetType.getValue().equals(EnumFromTempletType.ADD.getValue())){
                setDefaultValue(formData,templetField);
            }else if (templetType.getValue().equals(EnumFromTempletType.EDIT.getValue()) || templetType.getValue().equals(EnumFromTempletType.INFO.getValue())){
                if (yftools.isEmpty(formData.getString(templetField.getFieldName()))){
                    setEditDefaultValue(formData,templetField);
                }
            }
        }


    }



    public SsoFormTempletFieldPo setEditDefaultValue(PageData formData, SsoFormTempletFieldPo templetField){
        if (!yftools.isEmpty(templetField.getEditDefaultValue()))
            formData.put(templetField.getFieldName(),templetField.getEditDefaultValue());
        else
            formData.put(templetField.getFieldName(),"");

        return templetField;
    }

    public SsoFormTempletFieldPo setDefaultValue(PageData formData,SsoFormTempletFieldPo templetField){
        if (!yftools.isEmpty(templetField.getAddDefaultValue()))
            formData.put(templetField.getFieldName(),templetField.getAddDefaultValue());
        else
            formData.put(templetField.getFieldName(),"");

        return templetField;
    }

    public String getSQL(List<SsoFormTempletFieldPo> templetFields, SsoFormTempletPo formTemplet, SsoForm form, PageData pd, boolean hasPage){
        String search1Page = "select * from c where c.partitionKey = '" + CosmosConfig.getPartitionKeyPrefix() + form.getFormName() + "' and c.delFlag='"+ EnumDelFlag.NORMAL.getValue()+"'";
        String search1Count = "select VALUE COUNT(1) from c where c.partitionKey = '" + CosmosConfig.getPartitionKeyPrefix() + form.getFormName() + "' and c.delFlag='"+ EnumDelFlag.NORMAL.getValue()+"'";
        String search="";
        String search2="";//第二部分，模板固定查询
        String sortSql="";



        for(SsoFormTempletFieldPo templetField:templetFields){
            if(yftools.isEquals(templetField.getListSearch(),"YES")){
                String FieldType=templetField.getFieldType();
                if(FieldType.equals(EnumFieldType.TextSelect.getValue())
                        || FieldType.equals(EnumFieldType.TextRadio.getValue())
                        || FieldType.equals(EnumFieldType.TextCheckbox.getValue())){//3、下拉列表，6、单选框(radio)，7、复选框(checkbox)
                    if (pd.containsKey(templetField.getFieldName())){
                        if(!yftools.isEmpty(pd.getString(templetField.getFieldName()))){
                            if(!yftools.isEquals(pd.getString(templetField.getFieldName()),"全部")){
                                search = search + " and c."+templetField.getFieldName()+" = '"+pd.getString(templetField.getFieldName())+"' ";
                            }
                        }
                    }
                }else if(FieldType.equals(EnumFieldType.DateTime.getValue())
                        || FieldType.equals(EnumFieldType.DateYYYYMMDD.getValue())
                        || FieldType.equals(EnumFieldType.TimeOnly.getValue())){//5、日期时间(text) 43、日期(text) 44、时间(text)
                    if(pd.containsKey(templetField.getFieldName()+"_beginTime"))
                        if(!yftools.isEmpty(pd.getString(templetField.getFieldName()+"_beginTime")))
                            search=search+" and c."+templetField.getFieldName()+" >= '"+pd.getString(templetField.getFieldName()+"_beginTime")+"' ";
                    if(pd.containsKey(templetField.getFieldName()+"_endTime"))
                        if(!yftools.isEmpty(pd.getString(templetField.getFieldName()+"_endTime")))
                            search=search+" and c."+templetField.getFieldName()+" <= '"+pd.getString(templetField.getFieldName()+"_endTime")+"' ";
                }else if(FieldType.equals(EnumFieldType.TimeStamp.getValue())){//时间戳(text)
                    if(pd.containsKey(templetField.getFieldName()+"_beginTime"))
                        if(!yftools.isEmpty(pd.getString(templetField.getFieldName()+"_beginTime")))
                            search=search+" and c."+templetField.getFieldName()+" >= '"+yftools.dateToStamp(pd.getString(templetField.getFieldName()+"_beginTime"))+"' ";
                    if(pd.containsKey(templetField.getFieldName()+"_endTime"))
                        if(!yftools.isEmpty(pd.getString(templetField.getFieldName()+"_endTime")))
                            search=search+" and c."+templetField.getFieldName()+" <= '"+yftools.dateToStamp(pd.getString(templetField.getFieldName()+"_endTime"))+"' ";
                }else if(FieldType.equals(EnumFieldType.NumberInt.getValue())
                        || FieldType.equals(EnumFieldType.NumberDouble.getValue())
                        || FieldType.equals(EnumFieldType.NumberBigDecimal.getValue())){//4、数字(int)，22、数字(Double)
                    if(pd.containsKey(templetField.getFieldName()+"_begin"))
                        if(!yftools.isEmpty(pd.getString(templetField.getFieldName()+"_begin")))
                            search=search+" and c."+templetField.getFieldName()+" >= '"+pd.getString(templetField.getFieldName()+"_begin")+"' ";
                    if(pd.containsKey(templetField.getFieldName()+"_end"))
                        if(!yftools.isEmpty(pd.getString(templetField.getFieldName()+"_end")))
                            search=search+" and c."+templetField.getFieldName()+" <= '"+pd.getString(templetField.getFieldName()+"_end")+"' ";
                }else if(FieldType.equals(EnumFieldType.FormSelectSignle.getValue())
                        || FieldType.equals(EnumFieldType.FormInner.getValue())
                        || FieldType.equals(EnumFieldType.FormSelectMuti.getValue())
                        || FieldType.equals(EnumFieldType.FormSelectMutiTag.getValue())
                        || FieldType.equals(EnumFieldType.FromSelectMutiTable.getValue())
                        || FieldType.equals(EnumFieldType.FromSelectMutiComponent.getValue())
                        || FieldType.equals(EnumFieldType.FormSelectSingleComponent.getValue())
                ){//12、表单下拉列表，20、表单多选，42、表单多标签,52、表单多选列表,表单多选组件,表单单选组件
                    if(pd.containsKey(templetField.getFieldName()))
                        if(!yftools.isEmpty(pd.getString(templetField.getFieldName())))
                            search=search+" and c."+templetField.getFieldName()+" ='"+pd.getString(templetField.getFieldName())+"' ";
                }else if(FieldType.equals(EnumFieldType.TextSelectMutiTag.getValue())){//23、多标签(checkbox)
                    if(pd.containsKey(templetField.getFieldName()))
                        if(!yftools.isEmpty(pd.getString(templetField.getFieldName()))){
//                            search=search+" and FIND_IN_SET('"+pd.getString(templetField.getFieldName())+"',c."+templetField.getFieldName()+") ";
                            search=search+" and ARRAY_CONTAINS(c."+templetField.getFieldName()+", '"+pd.getString(templetField.getFieldName())+"') ";
                        }
                }else if(FieldType.equals(EnumFieldType.DictionarySelect.getValue())
                        || FieldType.equals(EnumFieldType.DictionaryRadio.getValue())
                        || FieldType.equals(EnumFieldType.DictionaryCheckbox.getValue())
                        || FieldType.equals(EnumFieldType.DictionarySelectMutiTag.getValue())
                        || FieldType.equals(EnumFieldType.Authen.getValue())){//31、字典下拉列表(select)，32、字典单选框(radio)，33、字典复选框(checkbox)，34、字典多标签(checkbox)
                    if(pd.containsKey(templetField.getFieldName()))
                        if(!yftools.isEmpty(pd.getString(templetField.getFieldName())))
                            if(FieldType.equals(EnumFieldType.DictionaryCheckbox.getValue())
                                    ||FieldType.equals(EnumFieldType.DictionarySelectMutiTag.getValue())){//33、字典复选框(checkbox)，34、字典多标签(checkbox)
//                                search=search+" and FIND_IN_SET('"+pd.getString(templetField.getFieldName())+"',c."+templetField.getFieldName()+") ";
                                search=search+" and ARRAY_CONTAINS(c."+templetField.getFieldName()+",'"+pd.getString(templetField.getFieldName())+"') ";
                            }else{
                                search=search+" and c."+templetField.getFieldName()+" = '"+pd.getString(templetField.getFieldName())+"' ";
                            }
                }else if(FieldType.equals(EnumFieldType.ProvenceCityArea.getValue())){//省市区联动
                    if(pd.containsKey(templetField.getFieldName())){
                        if(!yftools.isEmpty(pd.getString(templetField.getFieldName()))){
                            String[] arr=pd.getString(templetField.getFieldName()).split(",");
                            if (arr.length==3){
                                if(!"全部".equals(arr[0]) && !yftools.isEmpty(arr[0])){
                                    search=search+" and c.yf_provence = '"+arr[0]+"' ";
                                }

                                if(!"全部".equals(arr[1]) && !yftools.isEmpty(arr[1])){
                                    search=search+" and c.yf_city = '"+arr[1]+"' ";
                                }

                                if(!"全部".equals(arr[2]) && !yftools.isEmpty(arr[2])){
                                    search=search+" and c.yf_area = '"+arr[2]+"' ";
                                }
                            }
                        }
                    }
                }else{
                    if(pd.containsKey(templetField.getFieldName())){
                        if(!yftools.isEmpty(pd.getString(templetField.getFieldName())))
//                            search=search+" and c."+templetField.getFieldName()+" like CONCAT(CONCAT('%', '"+pd.getString(templetField.getFieldName())+"'),'%') ";
                            search=search+" and c."+templetField.getFieldName()+" like '%"+pd.getString(templetField.getFieldName())+"%' ";
                    }
                }

            }

            if(!yftools.isEmpty(templetField.getListPramsType())){
                if(yftools.isEquals(templetField.getListPramsType(),"FIXED")){
                    if(!yftools.isEmpty(templetField.getListPramsValue()))
                        search2=search2 + " and c."+templetField.getFieldName()+" ='"+templetField.getListPramsValue()+"' ";
                }else if(yftools.isEquals(templetField.getListPramsType(),"URL")){
                    if(!yftools.isEmpty(templetField.getListPramsValue()))
                        search2=search2 + " and c."+templetField.getFieldName()+" ='"+pd.getString(templetField.getListPramsValue())+"' ";
                }
            }

            if(!yftools.isEmpty(templetField.getListSortType())){
                if(templetField.getListSortType().equals("ASC") || templetField.getListSortType().equals("DESC")){
                    if("".equals(sortSql)){
                        sortSql=" order by c."+templetField.getFieldName()+" "+templetField.getListSortType()+" ";
                    }else{
                        sortSql=sortSql+" ,c."+templetField.getFieldName()+" "+templetField.getListSortType()+" ";
                    }
                }
            }
        }

        if (hasPage){
            int page = new BigDecimal(pd.getDouble("pageNum")).intValue();
            int size = new BigDecimal(pd.getDouble("pageSize")).intValue();
            int offset = (page - 1) * size;

            String pageSql = "OFFSET " + offset + " LIMIT " + size;
            return search1Page + search + search2 + pageSql + sortSql;
        }else{

            return search1Count + search + search2 + sortSql;
        }



    }



}
