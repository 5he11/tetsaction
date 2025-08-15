package com.frenzy.sso.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.frenzy.core.domain.BaseCosmosDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@ApiModel(value="SsoRole", description="")
public class SsoRole extends BaseCosmosDomain
{
    public SsoRole() {
        super();
    }

    @ExcelProperty(value = "角色名称")
    @ApiModelProperty(value = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 0, max = 30, message = "角色名称长度不能超过30个字符")
    private String roleName;


    @ExcelProperty(value = "角色权限")
    @ApiModelProperty(value = "角色权限")
    @NotBlank(message = "权限字符不能为空")
    @Size(min = 0, max = 100, message = "权限字符长度不能超过100个字符")
    private String roleKey;

    @ExcelProperty(value = "角色排序")
    @ApiModelProperty(value = "角色排序")
    @NotBlank(message = "显示顺序不能为空")
    private String roleSort;


    @ExcelProperty(value = "数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限）")
    @ApiModelProperty(value = "数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限）")
    private String dataScope;


    @ExcelProperty(value = "菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）")
    @ApiModelProperty(value = "菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）")
    private boolean menuCheckStrictly;


    @ExcelProperty(value = "部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ）")
    @ApiModelProperty(value = "部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ）")
    private boolean deptCheckStrictly;


    @ExcelProperty(value = "角色状态")
    @ApiModelProperty(value = "角色状态")
    private String status;


    @ExcelProperty(value = "管理员标志")
    @ApiModelProperty(value = "管理员标志")
    private String adminFlag;


    @ExcelProperty(value = "用户是否存在此角色标识 默认不存在")
    @ApiModelProperty(value = "用户是否存在此角色标识 默认不存在")
    private boolean flag = false;


    @ExcelProperty(value = "菜单组")
    @ApiModelProperty(value = "菜单组")
    private List<String> menuIds;


    @ExcelProperty(value = "部门组（数据权限）")
    @ApiModelProperty(value = "部门组（数据权限）")
    private List<String> deptIds;






}
