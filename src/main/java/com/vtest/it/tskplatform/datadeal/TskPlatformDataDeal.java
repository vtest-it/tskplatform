package com.vtest.it.tskplatform.datadeal;

import com.vtest.it.tskplatform.pojo.mes.SlotAndSequenceConfigBean;
import com.vtest.it.tskplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.tskplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.tskplatform.service.mes.impl.GetMesInforImpl;
import com.vtest.it.tskplatform.service.tools.impl.DiffUtil;
import com.vtest.it.tskplatform.service.tools.impl.GenerateRawdata;
import com.vtest.it.tskplatform.service.tools.impl.rawdataCheck.RawDataCheck;
import com.vtest.it.tskplatform.service.vtptmt.impl.VtptmtInforImpl;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Service
public class TskPlatformDataDeal {
    @Value("${system.properties.tsk.rawdata-path}")
    private String rawdataPath;
    @Autowired
    private GetMesInforImpl getMesInfor;
    @Autowired
    private GenerateRawdata generateRawdata;
    @Autowired
    private RawDataCheck rawDataCheck;
    @Autowired
    private VtptmtInforImpl vtptmtInfor;
    @Autowired
    private GenerateRawdataInitInformation generateRawdataInitInformation;
    public ArrayList<File> deal(ArrayList<File> fileNeedDealList) {
        ArrayList<File> filesBeDealedList = new ArrayList<>();
        for (File lot : fileNeedDealList) {
            try {
                SlotAndSequenceConfigBean slotAndSequenceConfigBean = getMesInfor.getLotSlotConfig(lot.getName());
                if (slotAndSequenceConfigBean.getReadType().equals("SLOT")) {
                    File[] waferIds = lot.listFiles();
                    if (waferIds.length > 0) {
                        for (File wafer : waferIds) {
                            try {
                                ArrayList<DataParseIssueBean> dataParseIssueBeans = new ArrayList<DataParseIssueBean>();
                                Integer slot = Integer.valueOf(wafer.getName().substring(0, 3));
                                String waferId = getMesInfor.getWaferIdBySlot(lot.getName(), "" + slot);
                                generateRawdata(wafer, slotAndSequenceConfigBean, waferId, lot.getName(), dataParseIssueBeans, filesBeDealedList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    File[] waferIds = lot.listFiles();
                    if (waferIds.length > 0) {
                        for (File wafer : waferIds) {
                            try {
                                ArrayList<DataParseIssueBean> dataParseIssueBeans = new ArrayList<>();
                                String waferId = wafer.getName().substring(4);
                                generateRawdata(wafer, slotAndSequenceConfigBean, waferId, lot.getName(), dataParseIssueBeans, filesBeDealedList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filesBeDealedList;
    }

    public void generateRawdata(File wafer, SlotAndSequenceConfigBean slotAndSequenceConfigBean, String waferId, String lot, ArrayList<DataParseIssueBean> dataParseIssueBeans, ArrayList<File> filesBeDealedList) throws Exception {
        RawdataInitBean rawdataInitBean = generateRawdataInitInformation.generateRawdata(wafer, slotAndSequenceConfigBean, waferId, lot);
        if (generateTempRawdata(rawdataInitBean, dataParseIssueBeans)) {
            vtptmtInfor.dataErrorsRecord(dataParseIssueBeans);
            filesBeDealedList.add(wafer);
        }
    }

    public boolean generateTempRawdata(RawdataInitBean rawdataInitBean, ArrayList<DataParseIssueBean> dataParseIssueBeans) throws Exception {
        File tempRawdata = generateRawdata.generate(rawdataInitBean);
        boolean checkFlag = rawDataCheck.check(tempRawdata, dataParseIssueBeans);
        if (!checkFlag) {
            return false;
        }
        generateFinalRawdata(tempRawdata, rawdataInitBean);
        return true;
    }

    private void generateFinalRawdata(File file, RawdataInitBean rawdataInitBean) {
        String customerCode = rawdataInitBean.getProperties().get("Customer Code");
        String device = rawdataInitBean.getProperties().get("Device Name");
        String lot = rawdataInitBean.getProperties().get("Lot ID");
        String cpProcess = rawdataInitBean.getProperties().get("CP Process");
        String waferId = rawdataInitBean.getProperties().get("Wafer ID");
        File destFile = new File(rawdataPath + "/" + customerCode + "/" + device + "/" + lot + "/" + cpProcess + "/" + waferId + ".raw");
        try {
            FileUtils.copyFile(file, destFile);
            if (!DiffUtil.check(file, destFile)) {
                try {
                    FileUtils.forceDelete(destFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                generateFinalRawdata(file, rawdataInitBean);
            } else {
                FileUtils.forceDelete(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
