package com.frenzy.sso.service;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.frenzy.core.service.AbstractCosmosService;
import com.frenzy.core.config.UploadConfig;
import com.frenzy.core.thirdParty.qiniu.UploadDemo;
import com.frenzy.core.utils.yftools;
import com.frenzy.sso.domain.*;
import com.frenzy.sso.generator.util.VelocityInitializer;
import com.frenzy.sso.generator.util.VelocityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.frenzy.core.config.UploadConfig.Upload_Folder_name;

/**
 * @author yf
 * @since 2024-11-20
 */
@Slf4j
@Service
public class YfDictionaryGroupService extends AbstractCosmosService<YfDictionaryGroup> {


    public YfDictionaryGroupService() {
        super(YfDictionaryGroup.class);
    }


    //列出全部字典类型
//    public List<YfDictionary> listAllNoGroup() {
//        String sql = "select c.id,c.partitionKey,c.delFlag,c.createBy,c.createTime,c.updateBy,c.updateTime,c.remark,c.dictKey,c.dictValue,c.showValue from c where c.partitionKey = '" + this.getPartitionKeyFromItem() + "'";
//        return this.list(sql);
//    }

    //列出全部字典类型
    public YfDictionaryGroup getGroupByKey(String dictKey) {
        String sql = "select TOP 1 * from c where c.partitionKey = '" + this.getPartitionKeyFromItem() + "'";
        sql = sql + " AND c.dictKey = '" + dictKey + "' ";
        return this.getOne(sql);
    }

    //列出全部字典类型
    public List<YfDictionaryGroup> listAll() {
        String sql = "select * from c where c.partitionKey = '" + this.getPartitionKeyFromItem() + "'";
        return this.list(sql);
    }

    //根据key列表
    public List<YfDictionary> listAllbyKey(String dictKey) {
        String sql = "select TOP 1 * from c where c.partitionKey = '" + this.getPartitionKeyFromItem() + "'";
        sql = sql + " AND c.dictKey = '" + dictKey + "' ";
        YfDictionaryGroup dictionaryGroup = this.getOne(sql);
        return dictionaryGroup.getDictList();
    }


    //根据条件列表
    public List<YfDictionaryGroup> selectDictList(YfDictionary dictionary, boolean needGroup) {
        if (needGroup) {
            String sql = "SELECT * from c where c.partitionKey = '" + this.getPartitionKeyFromItem() + "'";

            if (StrUtil.isNotEmpty(dictionary.getDictKey())) {
                sql = sql + " AND c.dictKey = '" + dictionary.getDictKey() + "' ";
            }
            return this.list(sql);
        } else {
            String sql = "select * from c where c.partitionKey = '" + this.getPartitionKeyFromItem() + "'";

            if (StrUtil.isNotEmpty(dictionary.getDictKey())) {
                sql = sql + " AND c.dictKey = '" + dictionary.getDictKey() + "' ";
            }
            return this.list(sql);
        }

    }


    public String proCode(Map<String, List<YfDictionary>> dictMap) throws Exception{
        //生成导出
        return genCode(this.downloadCode(dictMap));
    }


    public byte[] downloadCode(Map<String, List<YfDictionary>> dictMap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String key : dictMap.keySet()) {
            List<YfDictionary> dictList = dictMap.get(key);
            generatorCode(key, dictList, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }


    /**
     * 查询表信息并生成代码
     */
    private void generatorCode(String key, List<YfDictionary> dictList, ZipOutputStream zip)
    {
        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContextDict(key, dictList);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateListDict();
        for (String template : templates)
        {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            try
            {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(VelocityUtils.getFileNameDict(template, key)));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.flush();
                zip.closeEntry();
            }
            catch (IOException e)
            {
                log.error("渲染模板失败，字典：" + key, e);
            }
        }
    }


    /**
     * 生成zip文件
     */
    private String genCode(byte[] data) throws IOException
    {
        String ffile = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_FORMATTER);
        String url = UploadDemo.main(data, Upload_Folder_name+"/"+ffile+"/dict_" + yftools.getRandNum(6) + ".zip");//UuidUtil.get32UUID()
        JSONObject jb=JSONObject.parseObject(url);
        url=jb.getString("key");
        return UploadConfig.domain + url;
    }

    public YfDictionaryGroup getGroupByDictId(String dictId){
        String sql = "select TOP 1 * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'  AND ARRAY_CONTAINS(c.dictList, {\"id\": \""+dictId+"\"}, true)";
        return this.getOne(sql);
    }


}
