package com.frenzy.app.domain;

import java.time.LocalDateTime;
import com.frenzy.core.domain.BaseCosmosDomain;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * 三方api记录对象 third_api_log
 * 
 * @author yannkeynes
 * @date 2025-08-12 17:28:38
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ThirdApiLog对象", description="三方api记录对象")
public class ThirdApiLog extends BaseCosmosDomain
{
    public ThirdApiLog() {
        super();
    }

//    @ApiModelProperty(value = "partitionKey")
//    public String partitionKey;
//
//    @ApiModelProperty(value = "id")
//    public String id;
//
//    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
//    public String delFlag;
//
//    @ApiModelProperty(value = "创建者")
//    public String createBy;
//
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @ApiModelProperty(value = "创建时间")
//    public LocalDateTime createTime;
//
//    @ApiModelProperty(value = "更新者")
//    public String updateBy;
//
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @ApiModelProperty(value = "更新时间")
//    public LocalDateTime updateTime;
//
//    @ApiModelProperty(value = "备注")
//    public String remark;
//
//    @ApiModelProperty(value = "部门")
//    public String deptId;

    @ApiModelProperty(value = "内容")
    public String content;

    @ApiModelProperty(value = "opOrgCode")
    public String opOrgCode;

    @ApiModelProperty(value = "orderNo")
    public String orderNo;

    @ApiModelProperty(value = "opTime")
    public String opTime;

    @ApiModelProperty(value = "opName")
    public String opName;

    @ApiModelProperty(value = "opDesc")
    public String opDesc;

    @ApiModelProperty(value = "operatorNo")
    public String operatorNo;

    @ApiModelProperty(value = "opCode")
    public String opCode;

    @ApiModelProperty(value = "opOrgName")
    public String opOrgName;

    @ApiModelProperty(value = "opOrgProvName")
    public String opOrgProvName;

    @ApiModelProperty(value = "operatorName")
    public String operatorName;

    @ApiModelProperty(value = "opOrgCity")
    public String opOrgCity;

    @ApiModelProperty(value = "waybillNo")
    public String waybillNo;




}
