package com.vtest.it.tskplatform.advisor;

import com.vtest.it.tskplatform.pojo.rawdataBean.RawdataInitBean;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Order(2)
public class RawdataBeanDealWithBin0 {
    @AfterReturning(value = "execution(* generateRawdata(..)) && target(com.vtest.it.tskplatform.datadeal.GenerateRawdataInitInformation)", returning = "rawdataInitBean")
    public void optimizeRawdataBeanWithBin0(RawdataInitBean rawdataInitBean) {
        try {
            HashMap<String, String> testDieMap = rawdataInitBean.getTestDieMap();
            String osBin = rawdataInitBean.getProperties().get("OS Bins").split(",")[0];
            for (Map.Entry<String, String> entry : testDieMap.entrySet()) {
                if (entry.getValue().equals("   0   0   0")) {
                    entry.setValue(String.format("%4s", osBin) + String.format("%4s", osBin) + String.format("%4s", "0"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
