package com.frenzy.app.third.ems.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class GetBarCodePo {


    @JsonProperty("serialNo")
    private String serialNo;
    @JsonProperty("retCode")
    private String retCode;
    @JsonProperty("retMsg")
    private String retMsg;
    @JsonProperty("retBody")
    private List<RetBodyDTO> retBody;
    @JsonProperty("retDate")
    private String retDate;
    @JsonProperty("success")
    private Boolean success;

    @NoArgsConstructor
    @Data
    public static class RetBodyDTO {
        @JsonProperty("logisticsOrderId")
        private String logisticsOrderId;
        @JsonProperty("success")
        private String success;
        @JsonProperty("bar_code")
        private String barCode;
    }
}
