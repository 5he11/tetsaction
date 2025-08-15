package com.frenzy.core.domain.subDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
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
@ApiModel(value="FzFormTemplet", description="")
public class FzFormTemplet implements Serializable {

    @ApiModelProperty(value = "模板编号")
    private String templetCode;

    @ApiModelProperty(value = "模板名称")
    private String templetTitle;

    @ApiModelProperty(value = "模板类型")
    private String templetType;

    @ApiModelProperty(value = "主要字段英文名称")
    private String mainField;

    @ApiModelProperty(value = "表单自定义模板地址")
    private String templetUrl;

    @ApiModelProperty(value = "卡片类型每行数量")
    private String cardRows;

    @ApiModelProperty(value = "列表模板中是否显示数据ID")
    private String listShowId;

    @ApiModelProperty(value = "列表模板中是否显示新增按钮")
    private String listShowAddBtn;

    @ApiModelProperty(value = "列表模板中是否显示编辑按钮")
    private String listShowEditBtn;

    @ApiModelProperty(value = "列表模板中是否显示删除按钮")
    private String listShowDelBtn;

    @ApiModelProperty(value = "列表模板中是否显示导出Excel按钮")
    private String listShowExcelBtn;

    @ApiModelProperty(value = "列表模板中是否显示全选功能")
    private String listShowSelAll;

    @ApiModelProperty(value = "列表模板中是否显示详情按钮")
    private String listShowInfoBtn;

    @ApiModelProperty(value = "列表模板中跳转新增页面的模板ID")
    private Integer listAddTempletId;

    @ApiModelProperty(value = "列表模板中跳转编辑页面的模板ID")
    private Integer listEditTempletId;

    @ApiModelProperty(value = "列表模板中跳转详情页面的模板ID")
    private Integer listInfoTempletId;

    @ApiModelProperty(value = "列表模板中打开新增按钮类型，NEW：新页面打开，OLD：本页打开")
    private String listOpenAddType;

    @ApiModelProperty(value = "列表模板中打开编辑按钮类型，NEW：新页面打开，OLD：本页打开")
    private String listOpenEditType;

    @ApiModelProperty(value = "列表模板中打开详情按钮类型，NEW：新页面打开，OLD：本页打开")
    private String listOpenInfoType;

    @ApiModelProperty(value = "栅格数量")
    private Integer gridRows;

    private List<FzFormTempletField> formTempletFieldList;

}
