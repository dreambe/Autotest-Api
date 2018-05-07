package com.autotest.api;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class MobileApplication {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("message");

        ApplicationContext ctx = SpringApplication.run(MobileApplication.class, args);
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        for (String profile : activeProfiles) {
            logger.warning("Spring Boot 使用profile为:{}" + profile);
        }
    }
}
