package com.frenzy.sso.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.frenzy.core.domain.BaseCosmosDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 参数配置表 sys_config
 * 
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SsoConfig", description="")
public class SsoConfig extends BaseCosmosDomain
{
    public SsoConfig() {
        super();
    }


    @ExcelProperty(value = "参数名称")
    @ApiModelProperty(value = "参数名称")
    @NotBlank(message = "参数名称不能为空")
    @Size(min = 0, max = 100, message = "参数名称不能超过100个字符")
    private String configName;


    @ExcelProperty(value = "参数键名")
    @ApiModelProperty(value = "参数键名")
    @NotBlank(message = "参数键名长度不能为空")
    @Size(min = 0, max = 100, message = "参数键名长度不能超过100个字符")
    private String configKey;


    @ExcelProperty(value = "参数键值")
    @ApiModelProperty(value = "参数键值")
    @NotBlank(message = "参数键值不能为空")
    @Size(min = 0, max = 500, message = "参数键值长度不能超过500个字符")
    private String configValue;


//    @ExcelProperty(value = "系统内置")
//    @ApiModelProperty(value = "系统内置")
//    private String configType;


}
