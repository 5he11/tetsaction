package com.frenzy.core.domain.subDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author yf
 * @since 2021-12-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="FzFormTempletField", description="")
public class FzFormTempletField implements Serializable {


    @ApiModelProperty(value = "字段名称")
    private String fieldName;

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

}
