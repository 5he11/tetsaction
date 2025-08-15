package com.frenzy.sso.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author yf
 * @since 2021-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YfDictionary对象", description="")
public class LoginDict implements Serializable {

    @ApiModelProperty(value = "字典key")
    public String dictKey;

    @ApiModelProperty(value = "字典值")
    public String dictValue;

    @ApiModelProperty(value = "显示值")
    public String label;

}
