package com.wzd.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class WzdBackendApplication {

    public static void main(String[] args) {

      //  String profile = "local";
        System.setProperty("spring.profiles.active", "h2");
        SpringApplication.run(WzdBackendApplication.class, args);
    }

}
