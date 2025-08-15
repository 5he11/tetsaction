package com.frenzy.sso.domain;

import com.frenzy.core.domain.BaseCosmosDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@ApiModel(value="YfDictionary", description="")
public class YfDictionary implements Serializable {


    public String id;

    @ApiModelProperty(value = "字典key")
    public String dictKey;

    @ApiModelProperty(value = "字典值")
    public String dictValue;

    @ApiModelProperty(value = "显示值")
    public String showValue;

}
