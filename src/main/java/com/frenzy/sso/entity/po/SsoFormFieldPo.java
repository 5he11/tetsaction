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
@ApiModel(value="SsoFormFieldPo", description="")
public class SsoFormFieldPo implements Serializable
{

    @ApiModelProperty(value = "id")
    public String id;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    public String createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    public String updateTime;

}
