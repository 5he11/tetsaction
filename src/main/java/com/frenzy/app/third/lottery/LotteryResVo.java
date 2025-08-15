package com.frenzy.app.third.lottery;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LotteryResVo {


    @JsonProperty("number")
    private String number;
    @JsonProperty("number1")
    private String number1;
    @JsonProperty("qihao")
    private String qihao;
    @JsonProperty("time")
    private String time;
    @JsonProperty("no1num")
    private String no1num;
    @JsonProperty("no2num")
    private String no2num;
    @JsonProperty("no3num")
    private String no3num;
    @JsonProperty("no4num")
    private String no4num;
    @JsonProperty("no5num")
    private String no5num;
    @JsonProperty("no6num")
    private String no6num;
    @JsonProperty("no1money")
    private String no1money;
    @JsonProperty("no2money")
    private String no2money;
    @JsonProperty("no3money")
    private String no3money;
    @JsonProperty("no4money")
    private String no4money;
    @JsonProperty("no5money")
    private String no5money;
    @JsonProperty("no6money")
    private String no6money;
    @JsonProperty("name")
    private String name;
    @JsonProperty("xiaoshou")
    private String xiaoshou;
    @JsonProperty("jiangchi")
    private String jiangchi;
    @JsonProperty("no1msg")
    private String no1msg;
    @JsonProperty("code")
    private Integer code;
}
