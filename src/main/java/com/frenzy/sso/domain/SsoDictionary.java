package com.frenzy.sso.domain;

import com.frenzy.core.domain.BaseCosmosDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@ApiModel(value="SsoDictionary", description="")
public class SsoDictionary extends BaseCosmosDomain {

    public SsoDictionary() {
        super();
    }

    @ApiModelProperty(value = "字典key")
    private String mKey;

    @ApiModelProperty(value = "字典值")
    private String mValue;

    @ApiModelProperty(value = "显示值")
    private String showValue;

    @ApiModelProperty(value = "是否系统")
    private String isSystem;

}
