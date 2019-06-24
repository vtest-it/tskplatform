package com.vtest.it.tskplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableAspectJAutoProxy
@RestController
@EnableTransactionManagement
@EnableAsync
public class TskplatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(TskplatformApplication.class, args);
    }
}
