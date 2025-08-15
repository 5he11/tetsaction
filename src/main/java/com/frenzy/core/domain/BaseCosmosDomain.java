package com.frenzy.core.domain;


import cn.hutool.core.lang.UUID;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.frenzy.core.config.CosmosConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yf
 * @since 2024-11-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BaseCosmosDomain implements Serializable {

    // 可以根据实际情况调整 keyName 的值
    protected String keyName;
    // 抽象方法，子类必须实现该方法来提供 keyName
//    protected abstract String getKeyName();


    public String getKeyName() {
        return this.getClass().getSimpleName();
    }

    // 构造方法，初始化 partitionKey
    public BaseCosmosDomain() {
        this.partitionKey = CosmosConfig.getPartitionKeyPrefix() + getKeyName();
    }
    // 获取 partitionKey 的方法
    public String getPartitionKey() {
        return this.partitionKey;
    }

    // 获取 getId 的方法
    public String getId() {
        return this.id;
    }


    public void setNewValue(){
        this.keyName = getKeyName();
        this.partitionKey = CosmosConfig.getPartitionKeyPrefix() + getKeyName();
        this.id = UUID.fastUUID().toString();
    }

    public void setNewValue2(){
        setKeyName(getKeyName());
        setPartitionKey(CosmosConfig.getPartitionKeyPrefix() + getKeyName());
        setId(UUID.fastUUID().toString());
//        this.keyName = getKeyName();
//        this.partitionKey = CosmosConfig.getPartitionKeyPrefix() + getKeyName();
//        this.id = UUID.fastUUID().toString();
    }


    // partitionKey 的字段，所有子类都会继承
    @ApiModelProperty(value = "partitionKey")
    protected String partitionKey;

    @ApiModelProperty(value = "id")
    public String id;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    public String delFlag;

    @ApiModelProperty(value = "创建者")
    public String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    public String createTime;

    @ApiModelProperty(value = "更新者")
    public String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    public String updateTime;

    @ApiModelProperty(value = "备注")
    public String remark;

    @ApiModelProperty(value = "部门ID")
    private String deptId;

    @ApiModelProperty(value = "搜索参数")
    private String searchValue;

}
