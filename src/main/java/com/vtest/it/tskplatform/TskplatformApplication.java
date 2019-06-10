package com.vtest.it.tskplatform;

import com.vtest.it.tskplatform.service.vtptmt.GetUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableAspectJAutoProxy
@RestController
public class TskplatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(TskplatformApplication.class, args);
    }
    @Autowired
    private GetUser getUser;
    @RequestMapping("/testname")
    public String ttt(String name){
        return  getUser.getUser(name);
    }


}
