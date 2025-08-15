package com.frenzy.app.third.ems.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PrintOrderPo {


    @JsonProperty("serialNo")
    private String serialNo;
    @JsonProperty("retCode")
    private String retCode;
    @JsonProperty("retMsg")
    private String retMsg;
    @JsonProperty("retBody")
    private RetBody retBody;
    @JsonProperty("retDate")
    private String retDate;
    @JsonProperty("success")
    private Boolean success;

    @Data
    @NoArgsConstructor
    public static class RetBody {
        private byte[] data;
    }
}
