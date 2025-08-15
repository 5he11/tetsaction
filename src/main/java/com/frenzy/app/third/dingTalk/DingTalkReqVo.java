package com.frenzy.app.third.dingTalk;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DingTalkReqVo {

    private String msgtype;
    private DataDTO text;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String content;
    }
}
