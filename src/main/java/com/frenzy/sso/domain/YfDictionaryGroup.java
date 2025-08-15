package com.frenzy.sso.domain;

import com.frenzy.core.domain.BaseCosmosDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

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
@ApiModel(value="YfDictionaryGroup", description="")
public class YfDictionaryGroup extends BaseCosmosDomain {

    public YfDictionaryGroup() {
        super();
    }


    @ApiModelProperty(value = "字典key")
    public String dictKey;

    public List<YfDictionary> dictList;

}
