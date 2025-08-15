//package com.frenzy.core.service.impl;
//
//import cn.hutool.core.date.LocalDateTimeUtil;
//import cn.hutool.core.util.ArrayUtil;
//import cn.hutool.core.util.RandomUtil;
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson.JSON;
//import com.frenzy.core.service.IYfFormService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * <p>
// *  服务实现类
// * </p>
// *
// * @author yf
// * @since 2021-12-12
// */
//@Slf4j
//@Service
//public class YfFormServiceImpl implements IYfFormService {
//
//
//    @Autowired
//    private IYfFormLogService formLogService;
//    @Autowired
//    private IYfFormFieldService formFieldService;
//    @Autowired
//    private IYfFormTempletService formTempletService;
//    @Autowired
//    private IYfFormTempletFieldService formTempletFieldService;
//    @Autowired
//    private IGenTableService genTableService;
//
//
//    //根据表名查询
//    @Override
//    public YfForm findByFormName(String formName){
//        return this.getOne(new LambdaQueryWrapper<YfForm>().eq(YfForm::getFormName,formName).last("limit 1"));
//    }
//
//    //根据条件查询查询
//    @Override
//    public List<YfForm> listByFiter(YfForm form){
//        LambdaQueryWrapper<YfForm> wrapper=new LambdaQueryWrapper<>();
//        if (StringUtils.isNotEmpty(form.getFormName())){
//            wrapper.eq(YfForm::getFormName,form.getFormName());
//        }
//        if (StringUtils.isNotEmpty(form.getFormTitle())){
//            wrapper.eq(YfForm::getFormTitle,form.getFormTitle());
//        }
//        if (StringUtils.isNotEmpty(form.getDescript())){
//            wrapper.like(YfForm::getDescript,form.getDescript());
//        }
//        if (StringUtils.isNotEmpty(form.getFormGroup())){
//            wrapper.eq(YfForm::getFormGroup,form.getFormGroup());
//        }
//        if (StringUtils.isNotEmpty(form.getFormType())){
//            wrapper.eq(YfForm::getFormType,form.getFormType());
//        }
//        wrapper.orderByDesc(YfForm::getId);
//        List<YfForm> formList=baseMapper.selectList(wrapper);
//        return formList;
//    }
//
//    //单条执行sql
//    @Override
//    public List<PageData> getSqlList(String sql){
//        return this.getBaseMapper().getSqlList(sql);
//    }
//
//    //单条执行sql
//    @Override
//    public PageData getSqlMode(String sql){
//        return this.getBaseMapper().getSqlMode(sql);
//    }
//
//    //单条执行sql
//    @Override
//    public String getSqlString(String sql){
//        return this.getBaseMapper().getSqlString(sql);
//    }
//
//    //单条执行sql
//    @Override
//    public void runSql(String sql){
//        this.getBaseMapper().runSql(sql);
//    }
//
//    //储存表单，此处事务是无法生效的
//    @Override
//    public YfForm saveFrom(YfForm form) {
//        if (this.findByFormName(form.getFormName())!=null)
//            throw new ServiceException("新增表单'" + form.getFormName() + "'失败，表单名称已存在", HttpStatus.ERROR);
//
//        //创建表单，由于创建表单如果失败，则事务会失效，所以提前到最前面操作
//        if(EnumFromType.LEVEL.getValue().equals(form.getFormType())){
//            this.createTableLevel(form.getFormName(), form.getFormTitle());
//        }else if(EnumFromType.INNER.getValue().equals(form.getFormType())){
//            this.createTableInner(form.getFormName(), form.getFormTitle());
//        }else {
//            this.createTable(form.getFormName(), form.getFormTitle());
//        }
//
//        //写入记录
//        form.setCreateBy(SecurityUtils.getUserId()+"");
//        form.setCreateTime(LocalDateTimeUtil.now());
//        this.baseMapper.insert(form);
//
//        //增加日志
//        formLogService.saveLog(form.getFormName(),"", EnumSqlLogType.FORM_ADD, JSON.toJSONString(form));
//
//        try {
//            //快速创建部门字段
//            formFieldService.creatDepartmentField(form.getId());
//        }catch (Exception e){
//            log.error(e.toString(), e);
//            //由于事务失效，此处需要手动删除已经建立的表单
//            this.dropFormByFormID(form.getId(),form.getFormName());
//            throw new ServiceException("创建数据表异常", HttpStatus.ERROR);
//        }
//
//
//        //添加默认模板
//        formTempletService.creatTemplet(form.getId(),form.getFormName(),"管理员列表", EnumFromTempletType.LIST.getValue());
//        formTempletService.creatTemplet(form.getId(),form.getFormName(),"管理员新增", EnumFromTempletType.ADD.getValue());
//        formTempletService.creatTemplet(form.getId(),form.getFormName(),"管理员修改", EnumFromTempletType.EDIT.getValue());
//        formTempletService.creatTemplet(form.getId(),form.getFormName(),"管理员详情", EnumFromTempletType.INFO.getValue());
//
//        return form;
//    }
//    //复制表单
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void copyFrom(YfForm form){
//        YfForm oldForm=this.getById(form.getId());
//        if (oldForm==null)
//            throw new ServiceException("原始表单不存在", HttpStatus.ERROR);
//
//        YfForm newForm=new YfForm();
//        BeanUtils.copyProperties(oldForm,newForm);
//        newForm.setFormName(form.getFormName());
//        newForm.setFormTitle(form.getFormName()+"_"+ RandomUtil.randomNumbers(4));
//        newForm.setCreateBy(SecurityUtils.getUsername());
//        newForm.setCreateTime(LocalDateTimeUtil.now());
//
//        //创建表单，由于创建表单如果失败，则事务会失效，所以提前到最前面操作
//        if(EnumFromType.LEVEL.getValue().equals(newForm.getFormType())){
//            this.createTableLevel(newForm.getFormName(), newForm.getFormTitle());
//        }else if(EnumFromType.INNER.getValue().equals(newForm.getFormType())){
//            this.createTableInner(newForm.getFormName(), newForm.getFormTitle());
//        }else {
//            this.createTable(newForm.getFormName(), newForm.getFormTitle());
//        }
//
//        //写入记录
//        this.save(newForm);
//
//        //增加日志
//        formLogService.saveLog(newForm.getFormName(),"", EnumSqlLogType.FORM_ADD, JSON.toJSONString(newForm));
//
//
//        try {
//            //快速创建部门字段
//            formFieldService.creatDepartmentField(newForm.getId());
//        }catch (Exception e){
//            log.error(e.toString(), e);
//            //由于事务失效，此处需要手动删除已经建立的表单
//            this.dropFormByFormID(newForm.getId(),newForm.getFormName());
//            throw new ServiceException("创建数据表异常", HttpStatus.ERROR);
//        }
//
//        //复制字段
//        try {
//            List<YfFormField> formFieldList=formFieldService.formFieldListByFormId(oldForm.getId());
//            formFieldList.forEach(formField->{
//                if (!formField.getFieldName().equals("del_flag")
//                        && !formField.getFieldName().equals("create_by")
//                        && !formField.getFieldName().equals("create_time")
//                        && !formField.getFieldName().equals("update_by")
//                        && !formField.getFieldName().equals("update_time")
//                        && !formField.getFieldName().equals("remark")
//                        && !formField.getFieldName().equals("dept_id") ){
//                    formField.setFormId(newForm.getId());
//                    formFieldService.creatField(formField);
//                }
//
//            });
//        }catch (Exception e){
//            log.error(e.toString(), e);
//            //由于事务失效，此处需要手动删除已经建立的表单
//            this.removeById(newForm.getId());
//            throw new ServiceException("创建数据表异常", HttpStatus.ERROR);
//        }
//
//        //复制模板
//        List<YfFormTemplet> formTempletList=formTempletService.formTempletListByFormId(oldForm.getId());
//        formTempletList.forEach(formTemplet -> {
//            //复制模板数据
//            YfFormTemplet newFormTemplet=new YfFormTemplet();
//            BeanUtils.copyProperties(formTemplet,newFormTemplet);
//            newFormTemplet.setFormId(newForm.getId());
//            formTempletService.creatTemplet(newForm.getFormName(), newFormTemplet);
//            //复制模板字段数据
//            List<YfFormTempletField> formTempletFieldList=formTempletFieldService.listByFormTempletId(formTemplet.getId());
//            formTempletFieldList.forEach(templetField -> {
//                YfFormTempletField newTempletField=new YfFormTempletField();
//                BeanUtils.copyProperties(templetField,newTempletField);
//                newTempletField.setFieldId(formFieldService.findFormFieldId(form.getId(),templetField.getFieldId()));
//                newTempletField.setTempletId(newFormTemplet.getId());
//                formTempletFieldService.creatTempletField(newForm.getFormName(), newTempletField);
//            });
//        });
//    }
//
//    //删除表单记录和移除数据库表单
//    @Override
//    public void dropFormByFormID(int formId,String formName){
//        this.removeById(formId);
//        String dropSql="DROP TABLE `"+formName+"`";
//        try {
//            this.runSql(dropSql);
//        }catch (Exception e){
//            log.error(e.toString(), e);
//            throw new ServiceException("删除数据表异常", HttpStatus.ERROR);
//        }
//        formLogService.saveLog(formName,"", EnumSqlLogType.FORM_DEL, String.valueOf(formId));
//        formLogService.saveLog(formName,"", EnumSqlLogType.FORM_DROP, dropSql);
//
//    }
//
//    //修改数据表注释
//    @Override
//    public void editFormComment(String formName, String formTitle){
//        String dropSql="alter table `"+formName+"` comment '"+formTitle+"';";
//        try {
//            this.runSql(dropSql);
//        }catch (Exception e){
//            log.error(e.toString(), e);
//            throw new ServiceException("修改数据表异常", HttpStatus.ERROR);
//        }
//    }
//
//    //删除表单
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void removeFrom(String[] formId){
//        if (ArrayUtil.isEmpty(formId))
//            throw new ServiceException("参数不正确", HttpStatus.ERROR);
//        for (String formIdString:formId){
//            YfForm form=this.getById(formIdString);
//            if(form==null)
//                throw new ServiceException("该表单在数据库中不存在", HttpStatus.ERROR);
//
//            //删除表单记录和移除数据库表单
//            this.dropFormByFormID(form.getId(),form.getFormName());
//            //删除表单下的字段
//            formFieldService.delFieldByFormID(form.getFormName(),form.getId());
//            //删除模板和模板字段
//            List<YfFormTemplet> formTempletList=formTempletService.formTempletListByFormId(form.getId());
//            formTempletList.forEach(formTemplet -> {
//                formTempletService.delTempletAndField(form.getFormName(), formTemplet.getId());
//            });
//
//        }
//
//    }
//
//    /**
//     * 导出表单
//     * @param formIds
//     */
//    @Override
//    public byte[] export(List<Long> formIds) {
//        List<YfForm> formList=this.listByIds(formIds);
//        for (YfForm yfForm : formList) {
//            List<YfFormField> formFieldList=formFieldService.formFieldListByFormId(yfForm.getId());
//            yfForm.setFormFieldList(formFieldList);
//            List<YfFormTemplet> formTempletList=formTempletService.formTempletListByFormId(yfForm.getId());
//            formTempletList.forEach(formTemplet -> {
//                List<YfFormTempletField> formTempletFieldList=formTempletFieldService.listByFormTempletId(formTemplet.getId());
//                formTemplet.setFormTempletFieldList(formTempletFieldList);
//            });
//            yfForm.setFormTempletList(formTempletList);
//        }
//        return JSONUtil.toJsonStr(formList).getBytes();
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void importForm(String json) {
//        List<YfForm> formList=JSONUtil.toList(json,YfForm.class);
//        for (YfForm oldForm : formList) {
//            if (this.findByFormName(oldForm.getFormName())!=null){
//                continue;
//            }
//            YfForm newForm=new YfForm();
//            BeanUtils.copyProperties(oldForm,newForm);
//            newForm.setCreateBy(SecurityUtils.getUsername());
//            newForm.setCreateTime(LocalDateTimeUtil.now());
//
//            //写入记录
//            this.save(newForm);
//            //增加日志
//            formLogService.saveLog(newForm.getFormName(),"", EnumSqlLogType.FORM_ADD, JSON.toJSONString(newForm));
//            //创建数据表
//            if(EnumFromType.LEVEL.getValue().equals(newForm.getFormType())){
//                this.createTableLevel(newForm.getFormName(), newForm.getFormTitle());
//            }else if(EnumFromType.INNER.getValue().equals(newForm.getFormType())){
//                this.createTableInner(newForm.getFormName(), newForm.getFormTitle());
//            }else {
//                this.createTable(newForm.getFormName(), newForm.getFormTitle());
//            }
//
//            List<YfFormField> formFieldList=oldForm.getFormFieldList();
//
//            Map<Integer,Integer> fieldIdMap=new HashMap<>();
//            formFieldList.forEach(formField->{
//                YfFormField newFormField=new YfFormField();
//                BeanUtils.copyProperties(formField,newFormField);
//                newFormField.setFormId(newForm.getId());
//                if (formField.getFieldName().equals("remark") || formField.getFieldName().equals("update_time") || formField.getFieldName().equals("update_by") || formField.getFieldName().equals("create_time") || formField.getFieldName().equals("create_by") || formField.getFieldName().equals("del_flag")){
//                    newFormField.setCreateBy(SecurityUtils.getUserId()+"");
//                    newFormField.setCreateTime(LocalDateTimeUtil.now());
//                    formFieldService.save(newFormField);
//                    //记录日志
//                    formLogService.saveLog(newForm.getFormName(), newFormField.getFieldName(), EnumSqlLogType.FIELD_ADD, String.valueOf(newFormField));
//                }else{
//                    formFieldService.creatField(newFormField);
//                }
//                fieldIdMap.put(formField.getId(),newFormField.getId());
//            });
//
////            formFieldService.SqlCreatField(newForm.getFormName(), YfFormField formField);
//
//            List<YfFormTemplet> formTempletList=oldForm.getFormTempletList();
//            formTempletList.forEach(formTemplet -> {
//                YfFormTemplet newFormTemplet=new YfFormTemplet();
//                BeanUtils.copyProperties(formTemplet,newFormTemplet);
//                newFormTemplet.setFormId(newForm.getId());
//                formTempletService.creatTemplet(newForm.getFormName(), newFormTemplet);
//                List<YfFormTempletField> formTempletFieldList = formTemplet.getFormTempletFieldList();
//                formTempletFieldList.forEach(templetField -> {
//                    YfFormTempletField newTempletField=new YfFormTempletField();
//                    BeanUtils.copyProperties(templetField,newTempletField);
//                    newTempletField.setFieldId(fieldIdMap.get(templetField.getFieldId()));
//                    newTempletField.setTempletId(newFormTemplet.getId());
//                    formTempletFieldService.creatTempletField(newForm.getFormName(), newTempletField);
//                });
//            });
//        }
//
//    }
//
//
//    //建立表单
//    private void createTable(String formName, String formTitle){
//        String sql="CREATE TABLE `"+formName+"` (`id` int(4) NOT NULL AUTO_INCREMENT ,PRIMARY KEY (`id`)," +
//                "  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）'," +
//                "  `create_by` varchar(64) DEFAULT '' COMMENT '创建者'," +
//                "  `create_time` datetime DEFAULT NULL COMMENT '创建时间'," +
//                "  `update_by` varchar(64) DEFAULT '' COMMENT '更新者'," +
//                "  `update_time` datetime DEFAULT NULL COMMENT '更新时间'," +
//                "  `remark` varchar(255) DEFAULT NULL COMMENT '备注') comment='"+formTitle+"'";
//        try {
//            this.runSql(sql);
//        }catch (Exception e){
//            log.error(e.toString(), e);
//            throw new ServiceException("创建数据表异常，可能该数据表已存在", HttpStatus.ERROR);
//        }
//        formLogService.saveLog(formName,"",EnumSqlLogType.FORM_CREAT,sql);
//    }
//
//
//    //建立表单-内联表单
//    private void createTableInner(String formName, String formTitle){
//        String sql="CREATE TABLE `"+formName+"` (`id` int(4) NOT NULL AUTO_INCREMENT ,PRIMARY KEY (`id`)," +
//                "  `innerStr` VARCHAR(255) NOT NULL DEFAULT ''," +
//                "  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）'," +
//                "  `create_by` varchar(64) DEFAULT '' COMMENT '创建者'," +
//                "  `create_time` datetime DEFAULT NULL COMMENT '创建时间'," +
//                "  `update_by` varchar(64) DEFAULT '' COMMENT '更新者'," +
//                "  `update_time` datetime DEFAULT NULL COMMENT '更新时间'," +
//                "  `remark` varchar(255) DEFAULT NULL COMMENT '备注') comment='"+formTitle+"'";
//        try {
//            this.runSql(sql);
//        }catch (Exception e){
//            log.error(e.toString(), e);
//            throw new ServiceException("创建数据表异常，可能该数据表已存在", HttpStatus.ERROR);
//        }
//        formLogService.saveLog(formName,"",EnumSqlLogType.FORM_CREAT,sql);
//    }
//
//    //建立表单-联动表单
//    private void createTableLevel(String formName, String formTitle){
//        String sql="CREATE TABLE `"+formName+"` (`id` int(4) NOT NULL AUTO_INCREMENT ,PRIMARY KEY (`id`)," +
//                "  `up_id` INT(11) NOT NULL DEFAULT 0 COMMENT '上级ID'," +
//                "  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）'," +
//                "  `create_by` varchar(64) DEFAULT '' COMMENT '创建者'," +
//                "  `create_time` datetime DEFAULT NULL COMMENT '创建时间'," +
//                "  `update_by` varchar(64) DEFAULT '' COMMENT '更新者'," +
//                "  `update_time` datetime DEFAULT NULL COMMENT '更新时间'," +
//                "  `remark` varchar(255) DEFAULT NULL COMMENT '备注') comment='"+formTitle+"'";
//        try {
//            this.runSql(sql);
//        }catch (Exception e){
//            log.error(e.toString(), e);
//            throw new ServiceException("创建数据表异常，可能该数据表已存在", HttpStatus.ERROR);
//        }
//        formLogService.saveLog(formName,"",EnumSqlLogType.FORM_CREAT,sql);
//    }
//
//    /**
//     * 导出表单代码
//     * @param formId
//     * @return
//     */
//    @Override
//    public void proCode(String formId, HttpServletResponse response) throws Exception{
//        String[] arr = formId.split(",");
//        List<String> formList = new ArrayList<>();
//        if (arr.length>0){
//            for (String id:arr){
//                YfForm form=getById(id);
//                GenTable table = genTableService.selectGenTableByName(form.getFormName());
//                if(table!=null){
//                    //更新生成表
//                    genTableService.synchDb(table.getTableName());
//                }else{
//                    //插入生成表
//                    List<GenTable> tableList = genTableService.selectDbTableListByNames(Convert.toStrArray(form.getFormName()));
//                    genTableService.importGenTable(tableList);
//                }
//                formList.add(form.getFormName());
//            }
//        }
//
//        String[] ans2 = formList.toArray(new String[formList.size()]);
//
//        //生成导出
//        byte[] data = genTableService.downloadCode(ans2);
//        genCode(response, data);
//    }
//
//    /**
//     * 生成zip文件
//     */
//    private void genCode(HttpServletResponse response, byte[] data) throws IOException
//    {
//        response.reset();
//        response.addHeader("Access-Control-Allow-Origin", "*");
//        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
//        response.setHeader("Content-Disposition", "attachment; filename=\"yfcms.zip\"");
//        response.addHeader("Content-Length", "" + data.length);
//        response.setContentType("application/octet-stream; charset=UTF-8");
//        IOUtils.write(data, response.getOutputStream());
//    }
//
//}
