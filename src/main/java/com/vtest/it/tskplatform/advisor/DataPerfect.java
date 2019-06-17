package com.vtest.it.tskplatform.advisor;

import com.alibaba.fastjson.JSON;
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

import java.util.ArrayList;

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
            //maybe test time is later than base ,it is not should be insert into database;
            System.err.println(JSON.toJSONString(binWaferInforBean));
            proberServices.singleWaferDeal(binWaferInforBean, customerCode, device, lot, cp, waferId, rawdataInitBean.getSiteBinSum(), "N", passBinsArray);
            vtptmtInfor.singleWaferDeal(binWaferInforBean, waferId, cp, tester);
        }
    }

}
