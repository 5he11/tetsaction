package com.frenzy.sso.generator.util;

import com.alibaba.fastjson.JSONObject;
import com.frenzy.core.utils.StringUtils;
import com.frenzy.core.utils.YFLocalDateTimeUtil;
import com.frenzy.sso.domain.YfDictionary;
import com.frenzy.sso.generator.constant.GenConfig;
import com.frenzy.sso.generator.constant.GenConstants;
import com.frenzy.sso.generator.domain.GenTable;
import com.frenzy.sso.generator.domain.GenTableColumn;
import org.apache.velocity.VelocityContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 模板处理工具类
 *
 * @author ruoyi
 */
public class VelocityUtils
{
    /** 项目空间路径 */
    private static final String PROJECT_PATH = "main/java";

    /** mybatis空间路径 */
    private static final String MYBATIS_PATH = "main/resources/mapper";

    /** 默认上级菜单，系统工具 */
    private static final String DEFAULT_PARENT_MENU_ID = "3";


    /**
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    public static VelocityContext prepareContextDict(String key, List<YfDictionary> dictList)
    {

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("datetime", YFLocalDateTimeUtil.now());
        velocityContext.put("dictList", dictList);
        velocityContext.put("key", key);
        velocityContext.put("author", GenConfig.getAuthor());
        velocityContext.put("packageName", GenConfig.getPackageName());

        return velocityContext;
    }


    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getTemplateListDict()
    {
        List<String> templates = new ArrayList<String>();
        templates.add("vm/dict/dict.java.vm");
        return templates;
    }


    /**
     * 获取文件名
     */
    public static String getFileNameDict(String template, String key)
    {
        // 文件名称
        String fileName = "";
        if (template.contains("dict.java.vm"))
        {
            fileName = StringUtils.format("Enum{}.java", key);
        }
        return fileName;
    }


    /**
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    public static VelocityContext prepareContext(GenTable genTable)
    {
        String moduleName = genTable.getModuleName();
        String businessName = genTable.getBusinessName();
        String packageName = genTable.getPackageName();
        String functionName = genTable.getFunctionName();

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tplCategory", genTable.getTplCategory());
        velocityContext.put("tableName", genTable.getTableName());
        velocityContext.put("functionName", StringUtils.isNotEmpty(functionName) ? functionName : "【请填写功能名称】");
        velocityContext.put("ClassName", genTable.getClassName());
        velocityContext.put("className", StringUtils.uncapitalize(genTable.getClassName()));
        velocityContext.put("moduleName", genTable.getModuleName());
        velocityContext.put("BusinessName", StringUtils.capitalize(genTable.getBusinessName()));
        velocityContext.put("businessName", genTable.getBusinessName());
        velocityContext.put("basePackage", getPackagePrefix(packageName));
        velocityContext.put("packageName", packageName);
        velocityContext.put("author", genTable.getFunctionAuthor());
        velocityContext.put("datetime", YFLocalDateTimeUtil.now());
        velocityContext.put("pkColumn", genTable.getPkColumn());
        velocityContext.put("importList", getImportList(genTable));
        velocityContext.put("permissionPrefix", getPermissionPrefix(moduleName, businessName));
        velocityContext.put("columns", genTable.getColumns());
        velocityContext.put("table", genTable);
//        velocityContext.put("dicts", getDicts(genTable));
//        setMenuVelocityContext(velocityContext, genTable);

        return velocityContext;
    }

    public static void setMenuVelocityContext(VelocityContext context, GenTable genTable)
    {
        String options = genTable.getOptions();
        JSONObject paramsObj = JSONObject.parseObject(options);
        String parentMenuId = getParentMenuId(paramsObj);
        context.put("parentMenuId", parentMenuId);
    }



    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getTemplateList()
    {
        List<String> templates = new ArrayList<String>();
        templates.add("vm/java/domain.java.vm");
//        templates.add("vm/java/mapper.java.vm");
        templates.add("vm/java/service.java.vm");
//        templates.add("vm/java/serviceImpl.java.vm");
        return templates;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, GenTable genTable)
    {
        // 文件名称
        String fileName = "";
        // 包路径
        String packageName = genTable.getPackageName();
        // 模块名
        String moduleName = genTable.getModuleName();
        // 大写类名
        String className = genTable.getClassName();
        // 业务名称
        String businessName = genTable.getBusinessName();

        String javaPath = "other/" + PROJECT_PATH + "/" + StringUtils.replace(packageName, ".", "/");
        String mybatisPath = "other/" + MYBATIS_PATH + "/" + moduleName;
        String vuePath = "other/"+className+"/vue";

        if (template.contains("domain.java.vm"))
        {
//            fileName = StringUtils.format("{}/domain/{}.java", javaPath, className);
            fileName = StringUtils.format("{}.java", className);
        }
        if (template.contains("sub-domain.java.vm") && StringUtils.equals(GenConstants.TPL_SUB, genTable.getTplCategory()))
        {
//            fileName = StringUtils.format("{}/domain/{}.java", javaPath, genTable.getSubTable().getClassName());
            fileName = StringUtils.format("{}.java", genTable.getSubTable().getClassName());
        }
        else if (template.contains("mapper.java.vm"))
        {
//            fileName = StringUtils.format("{}/mapper/{}Mapper.java", javaPath, className);
            fileName = StringUtils.format("mapper/{}Mapper.java", className);
        }
        else if (template.contains("service.java.vm"))
        {
//            fileName = StringUtils.format("{}/service/I{}Service.java", javaPath, className);
            fileName = StringUtils.format("{}Service.java", className);
        }
        else if (template.contains("serviceImpl.java.vm"))
        {
//            fileName = StringUtils.format("{}/service/impl/{}ServiceImpl.java", javaPath, className);
            fileName = StringUtils.format("serviceImpl/{}ServiceImpl.java", className);
        }
        else if (template.contains("controller.java.vm"))
        {
            fileName = StringUtils.format("{}/controller/{}Controller.java", javaPath, className);
        }
        else if (template.contains("mapper.xml.vm"))
        {
//            fileName = StringUtils.format("{}/{}Mapper.xml", mybatisPath, className);
            fileName = StringUtils.format("xml/{}Mapper.xml", className);
        }
        else if (template.contains("sql.vm"))
        {
//            fileName = businessName + "Menu.sql";
            fileName = StringUtils.format("other/{}/mysql/controller/{}Menu.sql", className, businessName);
        }
        else if (template.contains("api.js.vm"))
        {
            fileName = StringUtils.format("{}/api/{}/{}.js", vuePath, moduleName, businessName);
        }
        else if (template.contains("index.vue.vm"))
        {
            fileName = StringUtils.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName);
        }
        else if (template.contains("index-tree.vue.vm"))
        {
            fileName = StringUtils.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName);
        }
        return fileName;
    }

    /**
     * 获取包前缀
     *
     * @param packageName 包名称
     * @return 包前缀名称
     */
    public static String getPackagePrefix(String packageName)
    {
        int lastIndex = packageName.lastIndexOf(".");
        String basePackage = StringUtils.substring(packageName, 0, lastIndex);
        return basePackage;
    }

    /**
     * 根据列类型获取导入包
     *
     * @param genTable 业务表对象
     * @return 返回需要导入的包列表
     */
    public static HashSet<String> getImportList(GenTable genTable)
    {
        List<GenTableColumn> columns = genTable.getColumns();
        GenTable subGenTable = genTable.getSubTable();
        HashSet<String> importList = new HashSet<String>();
        if (StringUtils.isNotNull(subGenTable))
        {
            importList.add("java.util.List");
        }
        for (GenTableColumn column : columns)
        {
            if (GenConstants.TYPE_DATE.equals(column.getJavaType()))
            {
                importList.add("java.util.Date");
                importList.add("com.fasterxml.jackson.annotation.JsonFormat");
            }
            else if (GenConstants.TYPE_BIGDECIMAL.equals(column.getJavaType()))
            {
                importList.add("java.math.BigDecimal");
            }
            else if (GenConstants.TYPE_LOCAL_DATE_TIME.equals(column.getJavaType()))
            {
                importList.add("java.time.LocalDateTime");
            }
            else if (GenConstants.TYPE_LOCAL_DATE.equals(column.getJavaType()))
            {
                importList.add("java.time.LocalDate");
            }
            else if (GenConstants.TYPE_LOCAL_TIME.equals(column.getJavaType()))
            {
                importList.add("java.time.LocalTime");
            }
            else if (GenConstants.TYPE_TIME_STAMP.equals(column.getJavaType()))
            {
                importList.add("java.security.Timestamp");
            }
        }
        return importList;
    }

    /**
     * 根据列类型获取字典组
     *
     * @param genTable 业务表对象
     * @return 返回字典组
     */
    public static String getDicts(GenTable genTable)
    {
        List<GenTableColumn> columns = genTable.getColumns();
        List<String> dicts = new ArrayList<String>();
        for (GenTableColumn column : columns)
        {
            if (StringUtils.isNotEmpty(column.getDictType()) && StringUtils.equalsAny(
                    column.getHtmlType(),
                    new String[] { GenConstants.HTML_SELECT, GenConstants.HTML_RADIO, GenConstants.HTML_CHECKBOX }))
            {
                dicts.add("'" + column.getDictType() + "'");
            }
        }
        return StringUtils.join(dicts, ", ");
    }

    /**
     * 获取权限前缀
     *
     * @param moduleName 模块名称
     * @param businessName 业务名称
     * @return 返回权限前缀
     */
    public static String getPermissionPrefix(String moduleName, String businessName)
    {
        return StringUtils.format("{}:{}", moduleName, businessName);
    }

    /**
     * 获取上级菜单ID字段
     *
     * @param paramsObj 生成其他选项
     * @return 上级菜单ID字段
     */
    public static String getParentMenuId(JSONObject paramsObj)
    {
        if (StringUtils.isNotEmpty(paramsObj) && paramsObj.containsKey(GenConstants.PARENT_MENU_ID)
                && StringUtils.isNotEmpty(paramsObj.getString(GenConstants.PARENT_MENU_ID)))
        {
            return paramsObj.getString(GenConstants.PARENT_MENU_ID);
        }
        return DEFAULT_PARENT_MENU_ID;
    }

    /**
     * 表名转换成Java类名
     *
     * @param tableName 表名称
     * @return 类名
     */
    public static String convertClassName(String tableName)
    {
        boolean autoRemovePre = GenConfig.getAutoRemovePre();
        String tablePrefix = GenConfig.getTablePrefix();
        if (autoRemovePre && StringUtils.isNotEmpty(tablePrefix))
        {
            String[] searchList = StringUtils.split(tablePrefix, ",");
            tableName = replaceFirst(tableName, searchList);
        }
        return StringUtils.convertToCamelCase(tableName);
    }

    /**
     * 批量替换前缀
     *
     * @param replacementm 替换值
     * @param searchList 替换列表
     * @return
     */
    public static String replaceFirst(String replacementm, String[] searchList)
    {
        String text = replacementm;
        for (String searchString : searchList)
        {
            if (replacementm.startsWith(searchString))
            {
                text = replacementm.replaceFirst(searchString, "");
                break;
            }
        }
        return text;
    }

}
