package com.frenzy.sso.domain;

import com.frenzy.core.domain.BaseCosmosDomain;
import com.frenzy.sso.entity.po.SsoFormFieldPo;
import com.frenzy.sso.entity.po.SsoFormTempletPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色表 sys_role
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SsoForm", description="")
public class SsoForm extends BaseCosmosDomain
{
    public SsoForm() {
        super();
    }


    @ApiModelProperty(value = "表单中文名")
    @NotBlank(message = "表单中文名不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9]+$", message = "表单名称只能为中文")
    private String formTitle;

    @ApiModelProperty(value = "表单英文名称")
    @NotBlank(message = "表单英文名称不能为空")
    @Pattern(regexp = "^[_a-zA-Z0-9]+$", message = "数据库名格式错误")
    private String formName;

    @ApiModelProperty(value = "描述")
    private String descript;

    @ApiModelProperty(value = "表单分组：系统表单、模块表单、用户表单")
    private String formGroup;

    @ApiModelProperty(value = "表单类型：普通表单、层级表单、内联表单")
    private String formType;

    @ApiModelProperty(value = "是否采用标记删除")
    private String isMarkDel;

    private Double isMarkDel1;

    private List<SsoFormFieldPo> formFieldList;

    private List<SsoFormTempletPo> formTempletList;

}
