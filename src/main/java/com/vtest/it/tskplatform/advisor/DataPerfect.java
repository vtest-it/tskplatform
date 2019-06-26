package com.vtest.it.tskplatform.advisor;

import com.vtest.it.tskplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.tskplatform.pojo.vtptmt.BinWaferInforBean;
import com.vtest.it.tskplatform.service.prober.impl.ProberServicesImpl;
import com.vtest.it.tskplatform.service.tools.impl.FailDieCheck.AdjacentFailDieCheck;
import com.vtest.it.tskplatform.service.tools.impl.rawdatatools.GenerateVtptmtWaferInforBean;
import com.vtest.it.tskplatform.service.tools.impl.urlMesInformation.WaferIdBinSummaryWrite;
import com.vtest.it.tskplatform.service.vtptmt.impl.VtptmtInforImpl;
import com.vtest.it.tskplatform.service.vtptmt.impl.VtptmtServices;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

@Aspect
@Component
public class DataPerfect {
    @Autowired
    private AdjacentFailDieCheck adjacentFailDieCheck;
    @Autowired
    private GenerateVtptmtWaferInforBean generateVtptmtWaferInforBean;
    @Autowired
    private ProberServicesImpl proberServices;
    @Autowired
    private VtptmtInforImpl vtptmtInfor;
    @Autowired
    private VtptmtServices vtptmtServices;
    @Autowired
    private WaferIdBinSummaryWrite waferIdBinSummaryWrite;

    @Pointcut(value = "execution(* generateTempRawdata(..))&&target(com.vtest.it.tskplatform.service.tools.impl.rawdatatools.GenerateRawdataTemp)")
    private void perfectRawdataBeanMethod() {
    }

    @AfterReturning(value = "perfectRawdataBeanMethod()&&args(rawdataInitBean,*)", returning = "checkFlag")
    public void record(RawdataInitBean rawdataInitBean, boolean checkFlag) {
        if (checkFlag && !vtptmtServices.checkDeviceIfInsetIntoMes(rawdataInitBean.getProperties().get("Customer Code"), rawdataInitBean.getProperties().get("Device Name"))) {
            waferIdBinSummaryWrite.write(rawdataInitBean);
        }
    }

    @AfterReturning(value = "perfectRawdataBeanMethod()&&args(rawdataInitBean,*)", returning = "checkFlag")
    @Async
    public void adjacentDieCheck(RawdataInitBean rawdataInitBean, boolean checkFlag) {
        if (checkFlag) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BinWaferInforBean binWaferInforBean = new BinWaferInforBean();
            generateVtptmtWaferInforBean.generate(rawdataInitBean, binWaferInforBean);
            adjacentFailDieCheck.deal(rawdataInitBean, binWaferInforBean);
            String customerCode = rawdataInitBean.getProperties().get("Customer Code");
            String device = rawdataInitBean.getProperties().get("Device Name");
            String tester = rawdataInitBean.getProperties().get("Tester ID");
            String lot = rawdataInitBean.getProperties().get("Lot ID");
            String cp = rawdataInitBean.getProperties().get("CP Process");
            String waferId = rawdataInitBean.getProperties().get("Wafer ID");
            String[] passBins = rawdataInitBean.getProperties().get("Pass Bins").split(",");
            ArrayList<Integer> passBinsArray = new ArrayList<>();
            for (int i = 0; i < passBins.length; i++) {
                passBinsArray.add(Integer.valueOf(passBins[i]));
            }
            proberServices.singleWaferDeal(binWaferInforBean, customerCode, device, lot, cp, waferId, rawdataInitBean.getSiteBinSum(), "N", passBinsArray);
            LinkedHashMap<String, String> properties = rawdataInitBean.getProperties();
            if (tester.equals("NA")) {
                return;
            }
            try {
                String endTime = properties.get("Test End Time").substring(0, 14);
                Date testEndTime = new SimpleDateFormat("yyyyMMddHHmmss").parse(endTime);
                BinWaferInforBean dbOldTesterStatus = vtptmtInfor.getTesterStatusSingle(tester);
                Date dbEendTime = dbOldTesterStatus.getEndTime();
                if (dbEendTime.getTime() > testEndTime.getTime()) {
                    return;
                }
            } catch (Exception e) {

            }
            vtptmtInfor.singleWaferDeal(binWaferInforBean, waferId, cp, tester);
        }
    }

}
