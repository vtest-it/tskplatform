package com.vtest.it.tskplatform.datadeal;

import com.vtest.it.tskplatform.pojo.mes.SlotAndSequenceConfigBean;
import com.vtest.it.tskplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.tskplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.tskplatform.service.mes.impl.GetMesInforImpl;
import com.vtest.it.tskplatform.service.tools.impl.rawdatatools.GenerateRawdataTemp;
import com.vtest.it.tskplatform.service.vtptmt.impl.VtptmtInforImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class TskPlatformDataDeal {
    @Autowired
    private GetMesInforImpl getMesInfor;
    @Autowired
    private VtptmtInforImpl vtptmtInfor;
    @Autowired
    private GenerateRawdataInitInformation generateRawdataInitInformation;
    @Autowired
    private GenerateRawdataTemp generateRawdataTemp;
    private static final Logger LOGGER = LoggerFactory.getLogger(TskPlatformDataDeal.class);

    public ArrayList<File> deal(ArrayList<File> fileNeedDealListFinal, HashMap<File, String> waferLotMap) {
        ArrayList<File> filesBeDealedList = new ArrayList<>();
        for (File wafer : fileNeedDealListFinal) {
            try {
                String lotName = waferLotMap.get(wafer);
                SlotAndSequenceConfigBean slotAndSequenceConfigBean = getMesInfor.getLotSlotConfig(lotName);
                if (slotAndSequenceConfigBean.getReadType().toUpperCase().equals("SLOT")) {
                    ArrayList<DataParseIssueBean> dataParseIssueBeans = new ArrayList<DataParseIssueBean>();
                    Integer slot = Integer.valueOf(wafer.getName().substring(0, 3));
                    String waferId = getMesInfor.getWaferIdBySlot(lotName, "" + slot);
                    LOGGER.error(lotName + "& special file name:" + wafer.getName() + " & true waferId:" + waferId);
                    LOGGER.error("wafer size:" + wafer.length() + " &  timeDiff:" + (System.currentTimeMillis() - wafer.lastModified()) / 1000);
                    generateRawdata(wafer, slotAndSequenceConfigBean, waferId, lotName, dataParseIssueBeans, filesBeDealedList);
                } else {
                    ArrayList<DataParseIssueBean> dataParseIssueBeans = new ArrayList<>();
                    String waferId = wafer.getName().substring(4);
                    LOGGER.error(lotName + "& special file name: " + wafer.getName() + " & true waferId: " + waferId);
                    LOGGER.error("wafer size:" + wafer.length() + " & timeDiff: " + (System.currentTimeMillis() - wafer.lastModified()) / 1000);
                    generateRawdata(wafer, slotAndSequenceConfigBean, waferId, lotName, dataParseIssueBeans, filesBeDealedList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filesBeDealedList;
    }

    public void generateRawdata(File wafer, SlotAndSequenceConfigBean slotAndSequenceConfigBean, String waferId, String lot, ArrayList<DataParseIssueBean> dataParseIssueBeans, ArrayList<File> filesBeDealedList) throws Exception {
        RawdataInitBean rawdataInitBean = generateRawdataInitInformation.generateRawdata(wafer, slotAndSequenceConfigBean, waferId, lot);
        boolean checkFlag = generateRawdataTemp.generateTempRawdata(rawdataInitBean, dataParseIssueBeans);
        if (checkFlag) {
            if (dataParseIssueBeans.size() > 0) {
                vtptmtInfor.dataErrorsRecord(dataParseIssueBeans);
            }
            filesBeDealedList.add(wafer);
        }
    }

}
