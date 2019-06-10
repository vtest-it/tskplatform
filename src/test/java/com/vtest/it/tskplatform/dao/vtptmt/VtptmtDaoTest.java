package com.vtest.it.tskplatform.dao.vtptmt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VtptmtDaoTest {
    @Autowired
    private TestRestTemplate template;
    @Test
    public void  Test(){
        ArrayList<Thread> arrayList=new ArrayList<>();
        for (int i = 0; i < 100; i++) {
           Thread thread=new Thread(new Runnable() {
               @Override
               public void run() {
                   System.out.println(template.getForEntity("/testname?name=v088",String.class).getBody());
               }
           });
            arrayList.add(thread);
        }
        for (Thread th : arrayList) {
            th.start();
        }
    }
}