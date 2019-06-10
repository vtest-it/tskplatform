package com.vtest.it.tskplatform.dao.mes;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MesDaoTest {
    @Autowired
    private  MesDao mesDao;

    @Test
    public void  ttt(){
        System.out.println(mesDao.getWaferIdBySlot("S1L724","1"));
        System.out.println(JSON.toJSONString(mesDao.getLotSlotConfig("S1L724")));
        System.out.println(JSON.toJSONString(mesDao.getWaferConfigFromMes("S1L724-01","CP1")));
        System.out.println(mesDao.getCustomerAndDeviceByLot("dsad")==null);
        System.out.println(JSON.toJSONString(mesDao.getCustomerAndDeviceByLot("S1L724")));
    }
}