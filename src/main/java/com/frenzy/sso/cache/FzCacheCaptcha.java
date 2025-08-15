package com.frenzy.sso.cache;


import com.frenzy.core.domain.BaseCosmosDomain;
import com.frenzy.sso.entity.po.SsoLoginUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yf
 * @since 2024-11-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="FzCacheCaptcha", description="")
public class FzCacheCaptcha extends BaseCosmosDomain {

    public FzCacheCaptcha() {
        super();
    }

    @ApiModelProperty(value = "验证码")
    public String code;

    @ApiModelProperty(value = "过期时间，-1或null为不过期，过期时间单位秒")
    public int ttl = 600;


}
