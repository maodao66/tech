package com.techfinance.external;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.techfinance")
@EnableFeignClients(basePackages = "com.techfinance")
public class ExternalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalApplication.class, args);
    }
}
