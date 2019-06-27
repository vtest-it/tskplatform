package com.vtest.it.tskplatform.advisor;

import com.vtest.it.tskplatform.pojo.mes.SlotAndSequenceConfigBean;
import com.vtest.it.tskplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.tskplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.tskplatform.service.tools.impl.FailDieCheck.AdjacentFailDieCheck;
import com.vtest.it.tskplatform.service.tools.impl.rawdataCheck.GetIssueBean;
import com.vtest.it.tskplatform.service.vtptmt.impl.VtptmtInforImpl;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class TskMappingParseFail implements Ordered {
    @Autowired
    private GetIssueBean getIssueBean;
    @Autowired
    private VtptmtInforImpl vtptmtInfor;
    @Autowired
    private AdjacentFailDieCheck adjacentFailDieCheck;
    @Override
    public int getOrder() {
        return 0;
    }

    @AfterThrowing(value = "execution(* generateRawdata(..)) && target(com.vtest.it.tskplatform.datadeal.GenerateRawdataInitInformation)&& args(wafer,slotAndSequenceConfigBean,waferId,lot)", throwing = "exception")
    private void mappingParseDealException(File wafer, SlotAndSequenceConfigBean slotAndSequenceConfigBean, String waferId, String lot, Exception exception) {
        try {
            DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBeanForException(5, exception.getMessage(), wafer, waferId, lot);
            ArrayList<DataParseIssueBean> list = new ArrayList<>();
            list.add(dataParseIssueBean);
            vtptmtInfor.dataErrorsRecord(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    @AfterReturning(value = "execution(* generateRawdata(..)) && target(com.vtest.it.tskplatform.datadeal.GenerateRawdataInitInformation)", returning = "rawdataInitBean")
    public void optimizeRawdataBean(RawdataInitBean rawdataInitBean) {
        adjacentFailDieCheck.perfectDeal(rawdataInitBean);
    }
}
