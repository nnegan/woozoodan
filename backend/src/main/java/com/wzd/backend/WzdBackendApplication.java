package com.wzd.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication (exclude = { RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class })
@ComponentScan(basePackages = {"com.wzd"})
public class WzdBackendApplication {


    public static void main(String[] args) {

      //  String profile = "local";
        System.setProperty("spring.profiles.active", "local,mysql,mq");
        SpringApplication.run(WzdBackendApplication.class, args);

    }



}
