package com.frenzy;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableConfigurationProperties
@Configuration
@SpringBootApplication
@JsonInclude(JsonInclude.Include.ALWAYS)
@EnableScheduling
public class FunctionApp {
    public static void main(String[] args) {
        SpringApplication.run(FunctionApp.class, args);
    }
}