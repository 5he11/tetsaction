package com.frenzy.sso.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frenzy.core.entity.PageData;
import com.frenzy.core.enums.EnumYesNoType;
import com.frenzy.core.service.AbstractCosmosService;
import com.frenzy.core.config.UploadConfig;
import com.frenzy.core.enums.EnumFieldType;
import com.frenzy.core.enums.EnumFromTempletType;
import com.frenzy.core.thirdParty.qiniu.UploadDemo;
import com.frenzy.core.utils.StringUtils;
import com.frenzy.core.utils.YFLocalDateTimeUtil;
import com.frenzy.core.utils.yftools;
import com.frenzy.sso.domain.*;
import com.frenzy.sso.entity.po.SsoFormFieldPo;
import com.frenzy.sso.entity.po.SsoFormTempletFieldPo;
import com.frenzy.sso.entity.po.SsoFormTempletPo;
import com.frenzy.sso.generator.constant.GenConfig;
import com.frenzy.sso.generator.domain.GenTable;
import com.frenzy.sso.generator.domain.GenTableColumn;
import com.frenzy.sso.generator.util.VelocityInitializer;
import com.frenzy.sso.generator.util.VelocityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.frenzy.core.config.UploadConfig.Upload_Folder_name;


/**
 * 登录校验方法
 * 
 * @author yf
 */
@Slf4j
@Component
public class SsoFormService extends AbstractCosmosService<SsoForm>
{

    public SsoFormService() {
        super(SsoForm.class);
    }

    @Autowired
    private SsoFormTempletFieldService formTempletFieldService;


    //根据条件查询查询
    public List<SsoForm> listAll(){
        String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        return this.list(sql);
    }


    //根据条件查询查询
    public List<SsoForm> listByFiter(SsoForm form){
        String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        if (StrUtil.isNotEmpty(form.getFormName())){
            sql = sql + "AND c.formName  = '"+form.getFormName()+"' ";
        }
        if (StrUtil.isNotEmpty(form.getFormTitle())){
            sql = sql + "AND c.formTitle  = '"+form.getFormTitle()+"' ";
        }
        if (StrUtil.isNotEmpty(form.getDescript())){
            sql = sql + "AND c.descript like concat('%', '"+form.getDescript()+"', '%')";
        }
        if (StrUtil.isNotEmpty(form.getFormGroup())){
            sql = sql + "AND c.formGroup  = '"+form.getFormGroup()+"' ";
        }
        if (StrUtil.isNotEmpty(form.getFormType())){
            sql = sql + "AND c.formType  = '"+form.getFormType()+"' ";
        }
        sql = sql + "order by c.createTime desc ";
        return this.list(sql);
    }


    public SsoForm findByFormName(String formName){
        String sql = "select TOP 1 * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        sql = sql + "AND c.formName = '"+formName+"'";
        return this.getOne(sql);
    }


    //储存表单，此处事务是无法生效的
    public void saveFrom(SsoForm form) {
        if (this.findByFormName(form.getFormName())!=null){
            yftools.throwException("新增表单'" + form.getFormName() + "'失败，表单名称已存在");
        }
        //写入记录
        form.setCreateTime(YFLocalDateTimeUtil.now());
        form.setNewValue();
        form.setFormFieldList(this.createBaseField(form.getId()));


        //添加默认模板
        List<SsoFormTempletPo> templetPoList = new ArrayList<>();
        templetPoList.add(this.creatTemplet(form,"管理员修改", EnumFromTempletType.EDIT.getValue()));
        templetPoList.add(this.creatTemplet(form,"管理员新增", EnumFromTempletType.ADD.getValue()));
        templetPoList.add(this.creatTemplet(form,"管理员列表", EnumFromTempletType.LIST.getValue()));
//        formTempletService.creatTemplet(form.getId(),"管理员详情", EnumFromTempletType.INFO.getValue(), ssoUser);

        form.setFormTempletList(templetPoList);
        this.save(form);

    }

    //删除表单
    public void removeFrom(String[] formId){
        if (ArrayUtil.isEmpty(formId)){
            yftools.throwException("参数不正确");
        }
        for (String formIdString:formId){
            SsoForm form=this.getById(formIdString);
            yftools.chkNullException(form, "该表单在数据库中不存在");

//            //删除表单下的字段
//            formFieldService.delFieldByFormID(form.getId());
//            //删除模板和模板字段
//            formTempletService.removeTemplet(form.getId());

            this.delete(form.getId());
        }

    }



    //复制表单
    public void copyFrom(SsoForm form, SsoUser ssoUser){
        SsoForm oldForm=this.getById(form.getId());
        if (oldForm==null){
            yftools.throwException("原始表单不存在");
        }

        SsoForm newForm=new SsoForm();
        BeanUtils.copyProperties(oldForm,newForm);
        newForm.setFormName(form.getFormName());
        newForm.setFormTitle(form.getFormName()+"_"+ RandomUtil.randomNumbers(4));
        newForm.setCreateBy(ssoUser.getUserName());
        newForm.setCreateTime(YFLocalDateTimeUtil.now());
        newForm.setId(UUID.fastUUID().toString());

        List<SsoFormTempletPo> templetPoList = newForm.getFormTempletList();
        if (CollectionUtil.isNotEmpty(templetPoList)){
            for (SsoFormTempletPo templetPo:templetPoList){
                templetPo.setId(UUID.fastUUID().toString());
            }
            newForm.setFormTempletList(templetPoList);
        }

        //写入记录
        this.save(newForm);
    }



    public String proCode(String formIds) throws Exception{
        String[] arr = formIds.split(",");
        String fileName = "fzcms";
        List<SsoForm> formList = new ArrayList<>();
        for (String id : arr) {
            SsoForm ssoForm = getById(id);
            formList.add(ssoForm);
            if (formList.size() == 1){
                fileName = ssoForm.getFormName();
            }
        }
        //生成导出
        return genCode(this.downloadCode(formList), fileName);
    }


    /**
     * 生成zip文件
     */
    private String genCode(byte[] data, String fileName) throws IOException
    {
        String ffile = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_FORMATTER);
        String url = UploadDemo.main(data, Upload_Folder_name+"/"+ffile+"/"+ fileName + "_" + yftools.getRandNum(6) + ".zip");//UuidUtil.get32UUID()
        JSONObject jb=JSONObject.parseObject(url);
        url=jb.getString("key");
        return UploadConfig.domain + url;
    }


    public byte[] downloadCode(List<SsoForm> forms)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (SsoForm form : forms)
        {
            generatorCode(form, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }


    /**
     * 查询表信息并生成代码
     */
    private void generatorCode(SsoForm ssoForm, ZipOutputStream zip)
    {
        List<SsoFormFieldPo> formFieldList = this.formFieldListByFormId(ssoForm.getId());
        GenTable table = new GenTable();
        table.setTableName(ssoForm.getFormName());
        table.setClassName(VelocityUtils.convertClassName(ssoForm.getFormName()));

        table.setFunctionName(ssoForm.getFormTitle());
        table.setPackageName(GenConfig.getPackageName());
        table.setFunctionAuthor(GenConfig.getAuthor());
        table.setModuleName("app");

        List<GenTableColumn> columnList = new ArrayList<>();
        for (SsoFormFieldPo ssoFormField:formFieldList){
            GenTableColumn tableColumn = new GenTableColumn();
            tableColumn.setColumnName(ssoFormField.getFieldName());
            tableColumn.setColumnComment(ssoFormField.getFieldTitle());
//            tableColumn.setJavaField(StringUtils.toCamelCase(ssoFormField.getFieldName()));
            tableColumn.setJavaField(ssoFormField.getFieldName());

            if (StrUtil.equals(ssoFormField.getFieldType(), EnumFieldType.NumberInt.getValue())){
                tableColumn.setJavaType("Integer");
            }else if (StrUtil.equals(ssoFormField.getFieldType(), EnumFieldType.NumberBigDecimal.getValue())){
                tableColumn.setJavaType("BigDecimal");
            }else if (StrUtil.equals(ssoFormField.getFieldType(), EnumFieldType.LocalDateTime.getValue())){
                tableColumn.setJavaType("LocalDateTime");
            }else if (StrUtil.equals(ssoFormField.getFieldType(), EnumFieldType.LocalDate.getValue())){
                tableColumn.setJavaType("LocalDate");
            }else if (StrUtil.equals(ssoFormField.getFieldType(), EnumFieldType.LocalTime.getValue())){
                tableColumn.setJavaType("LocalTime");
            }else if (StrUtil.equals(ssoFormField.getFieldType(), EnumFieldType.NumberDouble.getValue())){
                tableColumn.setJavaType("Double");
            }else if (StrUtil.equals(ssoFormField.getFieldType(), EnumFieldType.DateTime.getValue())){
                tableColumn.setJavaType("LocalDateTime");
            }else{
                tableColumn.setJavaType("String");
            }

            columnList.add(tableColumn);
        }
        table.setColumns(columnList);
        // 查询表信息
//        GenTable table = genTableMapper.selectGenTableByName(tableName);
//        // 设置主子表信息
//        setSubTable(table);
//        // 设置主键列信息
//        setPkColumn(table);

        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList();
        for (String template : templates)
        {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            try
            {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(VelocityUtils.getFileName(template, table)));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.flush();
                zip.closeEntry();
            }
            catch (IOException e)
            {
                log.error("渲染模板失败，表名：" + table.getTableName(), e);
            }
        }
    }



    //根据表单id和字段id，查询新的字段id
    public List<SsoFormFieldPo> formFieldListByFormId(String formId){
        SsoForm ssoForm = this.getById(formId);
        List<SsoFormFieldPo> fieldPoList = ssoForm.getFormFieldList();
        if (CollectionUtil.isNotEmpty(fieldPoList)){
            return fieldPoList;
        }else{
            return new ArrayList<>();
        }
    }


    //根据表单id和字段id，查询新的字段id
    public SsoFormFieldPo getFormFieldByFormFieldId(String formId, String formFieldId){
        SsoForm ssoForm = this.getById(formId);
        List<SsoFormFieldPo> formFieldPoList = ssoForm.getFormFieldList();
        for (SsoFormFieldPo formFieldPo:formFieldPoList){
            if (StrUtil.equals(formFieldPo.getId(),formFieldId)){
                return formFieldPo;
            }
        }
        return null;
    }

    //新建字段实际操作，入表、修改表字段
    public void createField(SsoFormFieldPo formField){
        SsoForm ssoForm = this.getById(formField.getFormId());
        List<SsoFormFieldPo> formFieldPoList = ssoForm.getFormFieldList();
        if (CollectionUtil.isNotEmpty(formFieldPoList)){
            for (SsoFormFieldPo formFieldPo:formFieldPoList){
                if (StrUtil.equals(formFieldPo.getFieldName(),formField.getFieldName())){
                    yftools.throwException("字段重复");
                }
            }
        }else{
            formFieldPoList = new ArrayList<>();
        }

        formField.setCreateTime(YFLocalDateTimeUtil.now());
        formField.setId(UUID.fastUUID().toString());

        formFieldPoList.add(formField);
        ssoForm.setFormFieldList(formFieldPoList);

        this.update(ssoForm);
    }


    public void updateField(SsoFormFieldPo formField){
        SsoForm ssoForm = this.getById(formField.getFormId());
        List<SsoFormFieldPo> formFieldPoList = ssoForm.getFormFieldList();
        for (SsoFormFieldPo formFieldPo:formFieldPoList){
            if (StrUtil.equals(formFieldPo.getId(),formField.getId())){
//                formFieldPo = formField;
                BeanUtils.copyProperties(formField, formFieldPo);
                formFieldPo.setUpdateTime(YFLocalDateTimeUtil.now());
            }
        }
        ssoForm.setFormFieldList(formFieldPoList);
        this.update(ssoForm);
    }


    //通过表单ID批量删除字段
    public void delFieldByFields(String formId, String[] formfieldId){
        SsoForm ssoForm = this.getById(formId);
        List<SsoFormFieldPo> formFieldPoList = ssoForm.getFormFieldList();

        for (String formfieldIdString:formfieldId){
            formFieldPoList.removeIf(item -> formfieldIdString.equals(item.getId()));
        }
        ssoForm.setFormFieldList(formFieldPoList);
        this.update(ssoForm);

//        for (String formfieldIdString:formfieldId){
//            SsoFormField formField = this.getById(formfieldIdString);
//            if(formField==null){
//                yftools.throwException("该字段在数据库中不存在");
//            }
//            templetFieldService.delTempletFieldByFieldId(formField.getId());
//            this.delete(formField.getId());
//            templetService.delFieldByAddField(formField);
//        }
    }



    private SsoFormFieldPo createOneBaseField(String formId, String fieldName, String fieldTitle){
        SsoFormFieldPo formField=new SsoFormFieldPo();
        formField.setFormId(formId);
        formField.setIsSystem(EnumYesNoType.YES.getValue());
        formField.setFieldDecimalPoint(0);
        formField.setIsProtect(EnumYesNoType.YES.getValue());
        formField.setFieldName(fieldName);
        formField.setFieldTitle(fieldTitle);
        if (fieldName.equals("partitionKey")){
            formField.setFieldType(EnumFieldType.TextSignleLine.getValue());
            formField.setFieldLength(150);
        }else if (fieldName.equals("id")){
            formField.setFieldType(EnumFieldType.TextSignleLine.getValue());
            formField.setFieldLength(150);
        }else if (fieldName.equals("delFlag")){
            formField.setFieldType(EnumFieldType.Authen.getValue());
            formField.setFieldLength(10);
        }else if (fieldName.equals("createBy")){
            formField.setFieldType(EnumFieldType.TextSignleLine.getValue());
            formField.setFieldLength(150);
        }else if (fieldName.equals("createTime")){
            formField.setFieldType(EnumFieldType.LocalDateTime.getValue());
            formField.setFieldLength(50);
        }else if (fieldName.equals("updateBy")){
            formField.setFieldType(EnumFieldType.TextSignleLine.getValue());
            formField.setFieldLength(150);
        }else if (fieldName.equals("updateTime")){
            formField.setFieldType(EnumFieldType.LocalDateTime.getValue());
            formField.setFieldLength(50);
        }else if (fieldName.equals("remark")){
            formField.setFieldType(EnumFieldType.TextMutiLineNoHtml.getValue());
            formField.setFieldLength(250);
        }else if (fieldName.equals("deptId")){
            formField.setFieldType(EnumFieldType.SelectByLevel.getValue());
            formField.setFieldLength(10);
        }

        formField.setCreateTime(YFLocalDateTimeUtil.now());
        formField.setId(UUID.fastUUID().toString());

        return formField;
    }


    public List<SsoFormFieldPo> createBaseField(String formId){
        List<SsoFormFieldPo> formFieldPoList = new ArrayList<>();
        formFieldPoList.add(this.createOneBaseField(formId, "partitionKey", "partitionKey"));
        formFieldPoList.add(this.createOneBaseField(formId, "id", "id"));
        formFieldPoList.add(this.createOneBaseField(formId, "delFlag", "删除标志（0代表存在 2代表删除）"));
        formFieldPoList.add(this.createOneBaseField(formId, "createBy", "创建者"));
        formFieldPoList.add(this.createOneBaseField(formId, "createTime", "创建时间"));
        formFieldPoList.add(this.createOneBaseField(formId, "updateBy", "更新者"));
        formFieldPoList.add(this.createOneBaseField(formId, "updateTime", "更新时间"));
        formFieldPoList.add(this.createOneBaseField(formId, "remark", "备注"));
        formFieldPoList.add(this.createOneBaseField(formId, "deptId", "部门"));
        return formFieldPoList;
    }


    //根据条件查询查询
    public List<SsoFormTempletPo> listFormTempletBySsoForm(String formId){
        SsoForm ssoForm = this.getById(formId);
        yftools.chkNullException(ssoForm,"表单不存在");

        List<SsoFormTempletPo> formTempletList=ssoForm.getFormTempletList();
        if (CollectionUtil.isNotEmpty(formTempletList)){
            formTempletList.forEach(item->{
                if(item.getTempletType().equals(EnumFromTempletType.ADD.getValue()) ||
                        item.getTempletType().equals(EnumFromTempletType.EDIT.getValue()) ||
                        item.getTempletType().equals(EnumFromTempletType.INFO.getValue()) ||
                        item.getTempletType().equals(EnumFromTempletType.ORDER_INFO.getValue()) ||
                        item.getTempletType().equals(EnumFromTempletType.USER_INFO.getValue())){
                    item.setListShowId(null);
                    item.setListShowAddBtn(null);
                    item.setListShowEditBtn(null);
                    item.setListShowDelBtn(null);
                    item.setListShowExcelBtn(null);
                    item.setListShowSelAll(null);
                    item.setListShowInfoBtn(null);
                    item.setListOpenAddType(null);
                    item.setListOpenEditType(null);
                    item.setListOpenInfoType(null);
                }
            });
            return formTempletList;
        }else{
            return new ArrayList<>();
        }

    }


    //新建模板
    public SsoFormTempletPo creatTemplet(SsoForm form, String templetTitle, String templetType){
        SsoFormTempletPo formTemplet=new SsoFormTempletPo();
        formTemplet.setFormId(form.getId());
        formTemplet.setTempletTitle(templetTitle);
        formTemplet.setTempletUrl("0");
        formTemplet.setTempletType(templetType);
        formTemplet.setListShowAddBtn("YES");
        formTemplet.setListShowEditBtn("YES");
        formTemplet.setListShowDelBtn("YES");
        formTemplet.setListShowExcelBtn("YES");
        formTemplet.setListShowInfoBtn("YES");
        formTemplet.setListShowSelAll("YES");
        formTemplet.setListShowId("YES");
        formTemplet.setListOpenAddType("OLD");
        formTemplet.setListOpenEditType("OLD");
        formTemplet.setListOpenInfoType("OLD");
        formTemplet.setCreateTime(YFLocalDateTimeUtil.now());
        formTemplet.setId(UUID.fastUUID().toString());
        return formTemplet;
    }


    //根据条件查询查询
    public List<SsoFormTempletPo> listAllListTemplet(String formId){
        List<SsoFormTempletPo> templetPoList = this.listFormTempletBySsoForm(formId);
        List<SsoFormTempletPo> resList = templetPoList.stream().filter(po -> po.getTempletType().equals("LIST")).collect(Collectors.toList());
        return resList;
    }



    public SsoFormTempletPo getFormTempletByFormTempletId(String formTempletId){
        SsoForm ssoForm = this.getFormByFormTempletId(formTempletId);
        if (ssoForm != null){
            List<SsoFormTempletPo> templetPoList = ssoForm.getFormTempletList();
            if (CollectionUtil.isNotEmpty(templetPoList)){
                for (SsoFormTempletPo templetPo:templetPoList){
                    if (StrUtil.equals(templetPo.getId(),formTempletId)){
                        return templetPo;
                    }
                }
            }
        }
        return null;
    }


    public SsoFormTempletPo getFormTempletByFormTempletIdForm(SsoForm ssoForm, String formTempletId){
        List<SsoFormTempletPo> templetPoList = ssoForm.getFormTempletList();
        if (CollectionUtil.isNotEmpty(templetPoList)){
            for (SsoFormTempletPo templetPo:templetPoList){
                if (StrUtil.equals(templetPo.getId(),formTempletId)){
                    return templetPo;
                }
            }
        }
        return null;
    }


    public SsoForm getFormByFormTempletId(String formTempletId){
        String sql = "select TOP 1 * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'  AND ARRAY_CONTAINS(c.formTempletList, {\"id\": \""+formTempletId+"\"}, true)";
        return this.getOne(sql);
    }


    public void saveTempLet(SsoFormTempletPo templetPo){
        SsoForm ssoForm = this.getById(templetPo.getFormId());
        List<SsoFormTempletPo> templetPoList = ssoForm.getFormTempletList();
        if (CollectionUtil.isEmpty(templetPoList)){
            templetPoList = new ArrayList<>();
        }

        templetPo.setCreateTime(YFLocalDateTimeUtil.now());
        templetPo.setId(UUID.fastUUID().toString());

        templetPoList.add(templetPo);
        ssoForm.setFormTempletList(templetPoList);

        this.update(ssoForm);
    }



    public void updateTempLet(SsoFormTempletPo templetPo){
        SsoForm ssoForm = this.getById(templetPo.getFormId());
        List<SsoFormTempletPo> templetPoList = ssoForm.getFormTempletList();
        for (SsoFormTempletPo templetPo1:templetPoList){
            if (StrUtil.equals(templetPo1.getId(),templetPo.getId())){
                BeanUtils.copyProperties(templetPo, templetPo1);
                templetPo1.setUpdateTime(YFLocalDateTimeUtil.now());
            }
        }
        ssoForm.setFormTempletList(templetPoList);
        this.update(ssoForm);
    }


    public void delTempLetByTempLetIds(String formId, String[] formTempletIds){
        SsoForm ssoForm = this.getById(formId);
        List<SsoFormTempletPo> templetPoList = ssoForm.getFormTempletList();

        for (String temoletId:formTempletIds){
            templetPoList.removeIf(item -> temoletId.equals(item.getId()));
        }
        ssoForm.setFormTempletList(templetPoList);
        this.update(ssoForm);
    }




    public List<SsoFormTempletFieldPo> findTempletFieldByTempletId(SsoForm ssoForm, String templetId){

        SsoFormTempletPo formTemplet = this.getFormTempletByFormTempletIdForm(ssoForm, templetId);
        yftools.chkNullException(formTemplet, "数据不存在");
        List<SsoFormTempletFieldPo> formTempletFieldList = formTemplet.getFormTempletFieldList();
        List<SsoFormFieldPo> formFieldList = ssoForm.getFormFieldList();

        List<SsoFormTempletFieldPo> templetFieldList= new ArrayList<>();

        for (SsoFormFieldPo formField:formFieldList){

            SsoFormTempletFieldPo hasSsoFormTempletField = null;
            if (CollectionUtil.isNotEmpty(formTempletFieldList)){
                for (SsoFormTempletFieldPo ssoFormTempletField:formTempletFieldList){
                    if (StrUtil.equals(ssoFormTempletField.getFieldId(),formField.getId())){
                        hasSsoFormTempletField = ssoFormTempletField;
                    }
                }
            }


            if (hasSsoFormTempletField == null){
                SsoFormTempletFieldPo templetField = new SsoFormTempletFieldPo();
                BeanUtils.copyProperties(formField, templetField);
                templetField.setId("");
                templetField.setFieldId(formField.getId());
                templetFieldList.add(templetField);
            }else{

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



    public void templetFieldEdit(List<SsoFormTempletFieldPo> formTempletFields){
        SsoForm ssoForm = this.getById(formTempletFields.get(0).getFormId());
        SsoFormTempletPo ssoFormTempletPo = this.getFormTempletByFormTempletIdForm(ssoForm, formTempletFields.get(0).getTempletId());
        for(SsoFormTempletFieldPo formTempletField:formTempletFields){
            if(StrUtil.equals(formTempletField.getRemark(),"false")){//不用了,需要删除该模板字段
                if (StrUtil.isNotEmpty(formTempletField.getId())){
                    ssoFormTempletPo = formTempletFieldService.delTempletField(ssoFormTempletPo, formTempletField.getId());
                }
            }else{//要用,看数据库中有没有
                SsoFormTempletFieldPo templetField=new SsoFormTempletFieldPo();
                templetField.setId(formTempletField.getId());

                templetField.setTips(formTempletField.getTips());
                templetField.setSortNum(formTempletField.getSortNum());
                templetField.setHeight(formTempletField.getHeight());
                templetField.setWidth(formTempletField.getWidth());
                templetField.setListSearch(formTempletField.getListSearch().equals("true")?"YES":"NO");
                templetField.setOnlyOne(formTempletField.getOnlyOne().equals("true")?"YES":"NO");
                templetField.setListSortType(formTempletField.getListSortType());
                templetField.setAddDefaultValue(formTempletField.getAddDefaultValue());
                templetField.setEditDefaultValue(formTempletField.getEditDefaultValue());
                templetField.setAddMustFill(formTempletField.getAddMustFill().equals("true")?"YES":"NO");
                templetField.setEditMustFill(formTempletField.getEditMustFill().equals("true")?"YES":"NO");
                templetField.setAddReadOnly(formTempletField.getAddReadOnly().equals("true")?"YES":"NO");
                templetField.setEditReadOnly(formTempletField.getEditReadOnly().equals("true")?"YES":"NO");
                templetField.setListPramsType(formTempletField.getListPramsType());
                templetField.setListPramsValue(formTempletField.getListPramsValue());

                if(StrUtil.isEmpty(formTempletField.getId()) || StrUtil.equals(formTempletField.getId(),"0")){//没有这个字段,需要新增
                    templetField.setFieldId(formTempletField.getFieldId());
                    templetField.setTempletId(formTempletField.getTempletId());
                    templetField.setCreateTime(YFLocalDateTimeUtil.now());
                    templetField.setTempletId(formTempletField.getTempletId());
                    templetField.setId(UUID.fastUUID().toString());
                    ssoFormTempletPo = formTempletFieldService.saveTempletField(ssoFormTempletPo, templetField);
                }else{//有这个字段,需要修改
                    templetField.setFieldId(formTempletField.getFieldId());
                    templetField.setTempletId(formTempletField.getTempletId());
                    templetField.setUpdateTime(YFLocalDateTimeUtil.now());
                    ssoFormTempletPo = formTempletFieldService.updateTempletField(ssoFormTempletPo, templetField);
                }
            }
        }
        List<SsoFormTempletPo> templetPoList = ssoForm.getFormTempletList();
        for (SsoFormTempletPo templetPo1:templetPoList){
            if (StrUtil.equals(templetPo1.getId(),ssoFormTempletPo.getId())){
                BeanUtils.copyProperties(ssoFormTempletPo, templetPo1);
                templetPo1.setUpdateTime(YFLocalDateTimeUtil.now());
            }
        }
        ssoForm.setFormTempletList(templetPoList);
        this.update(ssoForm);
    }


}
