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

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户对象 sys_user
 * 
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SsoUser", description="")
public class SsoUser extends BaseCosmosDomain
{
    public SsoUser() {
        super();
    }

    @ExcelProperty(value = "登录名称")
    @ApiModelProperty(value = "登录名称")
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    private String userName;

    @ExcelProperty(value = "用户昵称")
    @ApiModelProperty(value = "用户昵称")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    private String nickName;

    @ExcelProperty(value = "用户邮箱")
    @ApiModelProperty(value = "用户邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    @ExcelProperty(value = "手机号码")
    @ApiModelProperty(value = "手机号码")
    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    private String phonenumber;

    @ExcelProperty(value = "用户性别")
    @ApiModelProperty(value = "用户性别")
    private String sex;

    @ExcelProperty(value = "用户头像")
    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ExcelProperty(value = "密码")
    @ApiModelProperty(value = "密码")
    private String password;

    @ExcelProperty(value = "帐号状态")
    @ApiModelProperty(value = "帐号状态")
    private String status;

    @ExcelProperty(value = "最后登录IP")
    @ApiModelProperty(value = "最后登录IP")
    private String loginIp;

    @ExcelProperty(value = "最后登录时间")
    @ApiModelProperty(value = "最后登录时间")
    private LocalDateTime loginDate;

    @ExcelProperty(value = "角色组")
    @ApiModelProperty(value = "角色组")
    private List<String> roleIds;

    @ExcelProperty(value = "岗位组")
    @ApiModelProperty(value = "岗位组")
    private List<String> postIds;



    @ExcelProperty(value = "盐加密")
    @ApiModelProperty(value = "盐加密")
    private String salt;

    @ExcelProperty(value = "超级管理员")
    @ApiModelProperty(value = "超级管理员")
    private String flagAdmin;






}
