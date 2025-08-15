package com.frenzy.sso.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.frenzy.core.domain.BaseCosmosDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 系统访问记录表 sys_logininfor
 * 
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SsoLogininfor", description="")
public class SsoLogininfor extends BaseCosmosDomain
{
    public SsoLogininfor() {
        super();
    }


    @ExcelProperty(value = "用户账号")
    @ApiModelProperty(value = "用户账号")
    private String userName;

    @ExcelProperty(value = "登录状态")
    @ApiModelProperty(value = "登录状态")
    private String status;

    @ExcelProperty(value = "登录地址")
    @ApiModelProperty(value = "登录地址")
    private String ipaddr;

    @ExcelProperty(value = "登录地点")
    @ApiModelProperty(value = "登录地点")
    private String loginLocation;

    @ExcelProperty(value = "浏览器")
    @ApiModelProperty(value = "浏览器")
    private String browser;

    @ExcelProperty(value = "操作系统")
    @ApiModelProperty(value = "操作系统")
    private String os;

    @ExcelProperty(value = "提示消息")
    @ApiModelProperty(value = "提示消息")
    private String msg;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "访问时间")
    @ApiModelProperty(value = "访问时间")
    private Date loginTime;


}
