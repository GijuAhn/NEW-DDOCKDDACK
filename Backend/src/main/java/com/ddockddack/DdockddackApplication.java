package com.ddockddack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = {"com.ddockddack.domain.bestcut.repository",
    "com.ddockddack.domain.game.repository",
    "com.ddockddack.domain.member.repository",
    "com.ddockddack.domain.report.repository"})
public class DdockddackApplication {

    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    public static void main(String[] args) {
        SpringApplication.run(DdockddackApplication.class, args);
    }

}


