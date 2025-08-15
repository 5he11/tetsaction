package com.frenzy.core.thirdParty.rabbitMq;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author yannkeynes
 * @since 2022-07-20
 */
@Data
@ApiModel(value="RabbitVo对象", description="")
public class RabbitVo implements Serializable {

    private String method;
    private String relatedForm;
    private String relatedId;
    private String relatedForm2;
    private String relatedId2;
    private String messageId;
    private LocalDateTime createTime;



}
