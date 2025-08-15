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
@ApiModel(value="FzCacheSsoUser", description="")
public class FzCacheSsoUser extends BaseCosmosDomain {

    public FzCacheSsoUser() {
        super();
    }

    @ApiModelProperty(value = "用户信息")
    public SsoLoginUser loginUser;

    @ApiModelProperty(value = "过期时间，-1或null为不过期，过期时间单位秒")
    public int ttl = -1;


}
