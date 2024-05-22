package com.freezonex.aps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApsApplication.class, args);
    }

}
