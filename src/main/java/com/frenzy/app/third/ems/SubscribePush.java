package com.frenzy.app.third.ems;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SubscribePush {

//    {"logitcsInterface":"|$4|kF9KM/Iuq04gYlfM8qjxCO3+dLFPovkCiEPsFRG85WLSu/eR/nuAccmh7ra1kAzUz1nPWcupVeKZAAr4I5oFaSeyBDjGt7mECFGdy7NfqLfLJXiEDiU92H6D8xdp2Vgw4ju8N1vYJLuYLck2jAaCLFyy0uPfTZjhFEWteNoY0cv/dNQxMwR936U4hMbrf5bBL351/5kQoNePLJoKkLQwI6O3Z60o64/jySoKg6cehfnM4vwEbiRlxvNA1WJk0Q0VB4lFnIoPgOyFkxOEB3JkoIq2kc3exCrp40bcD7no1OfT/3s0/aZUUAUS4UwNAEWrn9FmJsuHXDwKY+doqXkCX83K8U867PRDfTUJvQ70Jx8/yYGjTalkF3kDOrYGn+GtHLBEvQlKRbznE2/EEOqKHwbWIsUoxM1mP/LS9v9sQ1j/3TsEJSj3dpDpn52CXqXdVJCM1r88WVIS9oeI9AxthSC/b0CbeKSxe7P/E0Zf39Rd3o5OoDMLbD+wyN37Xx7w","method":"040002","msgType":"0","senderNo":"","serialNo":"C3E7E1A2E6F44EB9B09EB9116C89FC86","timeStamp":"2025-08-12 16:56:50","version":"v1.0.0"}

    @JsonProperty("logitcsInterface")
    private String logitcsInterface;
    @JsonProperty("method")
    private String method;
    @JsonProperty("msgType")
    private String msgType;
    @JsonProperty("senderNo")
    private String senderNo;
    @JsonProperty("serialNo")
    private String serialNo;
    @JsonProperty("timeStamp")
    private String timeStamp;
    @JsonProperty("version")
    private String version;
}
