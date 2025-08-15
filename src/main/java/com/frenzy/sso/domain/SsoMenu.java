package com.frenzy.sso.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.frenzy.core.domain.BaseCosmosDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限表 sys_menu
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SsoMenu", description="")
public class SsoMenu extends BaseCosmosDomain
{
    public SsoMenu() {
        super();
    }


    @ExcelProperty(value = "菜单名称")
    @ApiModelProperty(value = "菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;


    @ExcelProperty(value = "父菜单ID")
    @ApiModelProperty(value = "父菜单ID")
    private String parentId;


    @ExcelProperty(value = "显示顺序")
    @ApiModelProperty(value = "显示顺序")
    @NotBlank(message = "显示顺序不能为空")
    private String orderNum;


    @ExcelProperty(value = "路由地址")
    @ApiModelProperty(value = "路由地址")
    @Size(min = 0, max = 200, message = "路由地址不能超过200个字符")
    private String path;


    @ExcelProperty(value = "组件路径")
    @ApiModelProperty(value = "组件路径")
    @Size(min = 0, max = 200, message = "组件路径不能超过255个字符")
    private String component;


    @ExcelProperty(value = "路由参数")
    @ApiModelProperty(value = "路由参数")
    private String query;


    @ExcelProperty(value = "是否为外链（0是 1否） ")
    @ApiModelProperty(value = "是否为外链（0是 1否） ")
    private String isFrame;


    @ExcelProperty(value = "是否缓存")
    @ApiModelProperty(value = "是否缓存")
    private String isCache;


    @ExcelProperty(value = "类型（M目录 C菜单 F按钮）")
    @ApiModelProperty(value = "类型（M目录 C菜单 F按钮）")
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;


    @ExcelProperty(value = "显示状态")
    @ApiModelProperty(value = "显示状态")
    private String visible;


    @ExcelProperty(value = "菜单状态")
    @ApiModelProperty(value = "菜单状态")
    private String status;


    @ExcelProperty(value = "权限字符串")
    @ApiModelProperty(value = "权限字符串")
    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
    private String perms;


    @ExcelProperty(value = "菜单图标")
    @ApiModelProperty(value = "菜单图标")
    private String icon;


    @ExcelProperty(value = "子菜单")
    @ApiModelProperty(value = "子菜单")
    private List<SsoMenu> children = new ArrayList<SsoMenu>();


}
