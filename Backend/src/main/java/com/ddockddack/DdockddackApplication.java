package com.ddockddack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.config.BootstrapMode;

@EnableJpaAuditing
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = {"com.ddockddack.domain.bestcut.repository",
    "com.ddockddack.domain.game.repository",
    "com.ddockddack.domain.member.repository",
    "com.ddockddack.domain.report.repository"}, bootstrapMode = BootstrapMode.DEFERRED)
@EnableRedisRepositories(basePackages = {"com.ddockddack.domain.gameRoom.repository"})
public class DdockddackApplication {

    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    public static void main(String[] args) {
        SpringApplication.run(DdockddackApplication.class, args);
    }

}


