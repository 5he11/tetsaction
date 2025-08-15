package com.frenzy.core.domain.subDomain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yf
 * @since 2024-11-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="FzFormField", description="")
public class FzFormField implements Serializable {

    @ApiModelProperty(value = "创建时间")
    public LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    public LocalDateTime updateTime;

    @ApiModelProperty(value = "表单id")
    public String formId;

    @ApiModelProperty(value = "字段中文名称")
    public String fieldTitle;

    @ApiModelProperty(value = "字段英文名称")
    @Pattern(regexp = "^[_a-zA-Z0-9]+$", message = "字段英文名称格式错误")
    public String fieldName;

    @ApiModelProperty(value = "字段类型")
    public String fieldType;

    @ApiModelProperty(value = "字段长度")
    public int fieldLength;

    @ApiModelProperty(value = "字段默认值")
    public String fieldDefault;

    @ApiModelProperty(value = "字段小数点位数")
    public int fieldDecimalPoint;

    @ApiModelProperty(value = "备选值")
    public String options;

    @ApiModelProperty(value = "关联表英文名")
    public String parentFormName;

    @ApiModelProperty(value = "关联字段英文名")
    public String parentFieldName;

    @ApiModelProperty(value = "关联模板ID")
    public String parentTpId;

    @ApiModelProperty(value = "关联字典")
    public String dictionary;

    @ApiModelProperty(value = "是否系统字段")
    public String isSystem;

    @ApiModelProperty(value = "正则校验")
    public String regexpCheck;

    @ApiModelProperty(value = "json配置")
    public String jsonConfig;

    @ApiModelProperty(value = "是否保护字段")
    public String isProtect;

    @ApiModelProperty(value = "是否为索引")
    public String isIndex;

    public String paramType;

}
