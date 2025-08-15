package com.frenzy.sso.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.frenzy.core.domain.BaseCosmosDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志记录表 oper_log
 * 
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SsoUser", description="")
public class SsoOperLog extends BaseCosmosDomain
{
    public SsoOperLog() {
        super();
    }


    @ExcelProperty(value = "操作模块")
    @ApiModelProperty(value = "操作模块")
    private String title;


    @ExcelProperty(value = "业务类型（0=其它,1=新增,2=修改,3=删除,4=授权,5=导出,6=导入,7=强退,8=生成代码,9=清空数据）")
    @ApiModelProperty(value = "业务类型（0=其它,1=新增,2=修改,3=删除,4=授权,5=导出,6=导入,7=强退,8=生成代码,9=清空数据）")
    private Integer businessType;


    @ExcelProperty(value = "业务类型数组")
    @ApiModelProperty(value = "业务类型数组")
    private List<String> businessTypes;


    @ExcelProperty(value = "请求方法")
    @ApiModelProperty(value = "请求方法")
    private String method;


    @ExcelProperty(value = "请求方式")
    @ApiModelProperty(value = "请求方式")
    private String requestMethod;


    @ExcelProperty(value = "操作类别（0其它 1后台用户 2手机端用户）")
    @ApiModelProperty(value = "操作类别（0其它 1后台用户 2手机端用户）")
    private Integer operatorType;


    @ExcelProperty(value = "操作人员")
    @ApiModelProperty(value = "操作人员")
    private String operName;


    @ExcelProperty(value = "部门名称")
    @ApiModelProperty(value = "部门名称")
    private String deptName;


    @ExcelProperty(value = "请求地址")
    @ApiModelProperty(value = "请求地址")
    private String operUrl;


    @ExcelProperty(value = "操作地址")
    @ApiModelProperty(value = "操作地址")
    private String operIp;


    @ExcelProperty(value = "操作地点")
    @ApiModelProperty(value = "操作地点")
    private String operLocation;


    @ExcelProperty(value = "请求参数")
    @ApiModelProperty(value = "请求参数")
    private String operParam;


    @ExcelProperty(value = "返回参数")
    @ApiModelProperty(value = "返回参数")
    private String jsonResult;


    @ExcelProperty(value = "操作状态")
    @ApiModelProperty(value = "操作状态")
    private String status;


    @ExcelProperty(value = "错误消息")
    @ApiModelProperty(value = "错误消息")
    private String errorMsg;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "操作时间")
    @ApiModelProperty(value = "操作时间")
    private LocalDateTime operTime;


}
