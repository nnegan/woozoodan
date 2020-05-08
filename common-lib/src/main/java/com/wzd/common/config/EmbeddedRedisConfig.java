package com.wzd.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.stream.Collectors;

@Slf4j //lombok
@Profile("local") // profile이 local일때만 활성화
@Configuration
public class EmbeddedRedisConfig {

    @Value("${spring.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void redisServer()  {
        redisServer = new RedisServer(redisPort);
        redisServer.start();
        log.info("Embedded Redis Started");
        log.info(" >> Port: " + redisServer.ports().stream().map(n -> String.valueOf(n)).collect(Collectors.joining()));
    }

    @PreDestroy
    public void stopRedis() {
        redisServer.stop();
        log.info("Embedded Redis Stopped");
    }
}