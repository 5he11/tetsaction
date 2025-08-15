package com.frenzy.sso.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.frenzy.core.domain.BaseCosmosDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门表 sys_dept
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SsoDept", description="")
public class SsoDept extends BaseCosmosDomain
{
    public SsoDept() {
        super();
    }

    @ExcelProperty(value = "部门名称")
    @ApiModelProperty(value = "部门名称")
    @NotBlank(message = "部门名称不能为空")
    @Size(min = 0, max = 30, message = "部门名称长度不能超过30个字符")
    private String deptName;


    @ExcelProperty(value = "显示顺序")
    @ApiModelProperty(value = "显示顺序")
    @NotBlank(message = "显示顺序不能为空")
    private String orderNum;

    @ExcelProperty(value = "父部门ID")
    @ApiModelProperty(value = "父部门ID")
    private String parentId;

    @ExcelProperty(value = "祖级列表")
    @ApiModelProperty(value = "祖级列表")
    private String ancestors;


    @ExcelProperty(value = "负责人")
    @ApiModelProperty(value = "负责人")
    private String leader;


    @ExcelProperty(value = "联系电话")
    @ApiModelProperty(value = "联系电话")
    @Size(min = 0, max = 11, message = "联系电话长度不能超过11个字符")
    private String phone;


    @ExcelProperty(value = "邮箱")
    @ApiModelProperty(value = "邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    private String email;


    @ExcelProperty(value = "部门状态:0正常,1停用")
    @ApiModelProperty(value = "部门状态:0正常,1停用")
    private String status;


    @ExcelProperty(value = "删除标志")
    @ApiModelProperty(value = "删除标志")
    private String delFlag;


    @ExcelProperty(value = "子部门")
    @ApiModelProperty(value = "子部门")
    private List<SsoDept> children = new ArrayList<SsoDept>();


}
