package com.frenzy.sso.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 角色表 sys_role
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SsoFormTempletFieldPo", description="")
public class SsoFormTempletFieldPo implements Serializable
{
    @ApiModelProperty(value = "id")
    public String id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    public String createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    public String updateTime;

    @ApiModelProperty(value = "字段id")
    private String fieldId;

    @ApiModelProperty(value = "模板id")
    private String templetId;

    @ApiModelProperty(value = "小提示")
    private String tips;

    @ApiModelProperty(value = "排序")
    private Integer sortNum;

    @ApiModelProperty(value = "表单高度")
    private Integer height;

    @ApiModelProperty(value = "表单宽度")
    private Integer width;

    @ApiModelProperty(value = "列表是否允许搜索")
    private String listSearch;

    @ApiModelProperty(value = "数据库中该字段是否唯一")
    private String onlyOne;

    @ApiModelProperty(value = "列表排序规则")
    private String listSortType;

    @ApiModelProperty(value = "添加表单中默认值")
    private String addDefaultValue;

    @ApiModelProperty(value = "编辑表单中默认值")
    private String editDefaultValue;

    @ApiModelProperty(value = "添加时是否必填")
    private String addMustFill;

    @ApiModelProperty(value = "编辑时是否必填")
    private String editMustFill;

    @ApiModelProperty(value = "添加时只读")
    private String addReadOnly;

    @ApiModelProperty(value = "编辑时只读")
    private String editReadOnly;

    @ApiModelProperty(value = "传值类型，FIXED：固定传值；URL:页面传值")
    private String listPramsType;

    @ApiModelProperty(value = "传值数据")
    private String listPramsValue;

    @ApiModelProperty(value = "列表是否允许修改")
    private String listEdit;

    private Object params;



//    原始字段

    @ApiModelProperty(value = "表单id")
    private String formId;

    @ApiModelProperty(value = "字段中文名称")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9]+$", message = "字段中文名称只能为中文")
    private String fieldTitle;

    @ApiModelProperty(value = "字段英文名称")
    @Pattern(regexp = "^[_a-zA-Z0-9]+$", message = "字段英文名称格式错误")
    private String fieldName;

    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    @ApiModelProperty(value = "字段长度")
    private int fieldLength;

    @ApiModelProperty(value = "字段默认值")
    private String fieldDefault;

    @ApiModelProperty(value = "字段小数点位数")
    private int fieldDecimalPoint;

    @ApiModelProperty(value = "备选值")
    private String options;

    @ApiModelProperty(value = "关联表英文名")
    private String parentFormName;

    @ApiModelProperty(value = "关联字段英文名")
    private String parentFieldName;

    @ApiModelProperty(value = "关联模板ID")
    private String parentTpId;

    @ApiModelProperty(value = "关联字典")
    private String dictionary;

    @ApiModelProperty(value = "是否系统字段")
    private String isSystem;

    @ApiModelProperty(value = "正则校验")
    private String regexpCheck;

    @ApiModelProperty(value = "json配置")
    private String jsonConfig;

    @ApiModelProperty(value = "是否保护字段")
    private String isProtect;

    @ApiModelProperty(value = "是否为索引")
    private String isIndex;

    @ApiModelProperty(value = "字段英文驼峰名称")
    private String fieldNameCamel;

    private String paramType;

    private String remark;

    private String searchValue;

}
