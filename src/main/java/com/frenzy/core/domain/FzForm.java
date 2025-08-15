package com.frenzy.core.domain;


import com.frenzy.core.config.CosmosConfig;
import com.frenzy.core.domain.subDomain.FzFormField;
import com.frenzy.core.domain.subDomain.FzFormTemplet;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yf
 * @since 2024-11-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="FzForm", description="")
public class FzForm implements Serializable {

    public static String keyName = "FzCacheSsoUser";

    public FzForm() {
        this.partitionKey = CosmosConfig.getPartitionKeyPrefix()+keyName;
    }

    public static String getPartitionKey() {
        return CosmosConfig.getPartitionKeyPrefix()+keyName;
    }

    @ApiModelProperty(value = "partitionKey")
    public String partitionKey;

    @ApiModelProperty(value = "id:表单英文名称")
    @Pattern(regexp = "^[_a-zA-Z0-9]+$", message = "数据库名格式错误")
    public String id;

    @ApiModelProperty(value = "创建时间")
    public LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    public LocalDateTime updateTime;

    @ApiModelProperty(value = "表单中文名")
    public String formTitle;

    @ApiModelProperty(value = "描述")
    public String descript;

    @ApiModelProperty(value = "表单类型：普通表单、层级表单、内联表单")
    public String formType;

    @ApiModelProperty(value = "是否采用标记删除")
    public String markDel;

    @ApiModelProperty(value = "字段列表")
    public List<FzFormField> formFieldList;

    @ApiModelProperty(value = "模板列表")
    public List<FzFormTemplet> formTempletList;




}
