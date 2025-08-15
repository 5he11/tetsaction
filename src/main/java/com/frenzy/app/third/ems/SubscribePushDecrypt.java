package com.frenzy.app.third.ems;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SubscribePushDecrypt {


    @JsonProperty("opOrgCode")
    private String opOrgCode;
    @JsonProperty("orderNo")
    private String orderNo;
    @JsonProperty("opTime")
    private String opTime;
    @JsonProperty("opName")
    private String opName;
    @JsonProperty("opDesc")
    private String opDesc;
    @JsonProperty("operatorNo")
    private String operatorNo;
    @JsonProperty("opCode")
    private String opCode;
    @JsonProperty("opOrgName")
    private String opOrgName;
    @JsonProperty("opOrgProvName")
    private String opOrgProvName;
    @JsonProperty("operatorName")
    private String operatorName;
    @JsonProperty("opOrgCity")
    private String opOrgCity;
    @JsonProperty("waybillNo")
    private String waybillNo;
}
