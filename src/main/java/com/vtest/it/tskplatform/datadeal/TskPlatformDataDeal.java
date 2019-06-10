package com.vtest.it.tskplatform.datadeal;

import com.vtest.it.tskplatform.MappingParseTools.TskProberMappingParse;
import com.vtest.it.tskplatform.MappingParseTools.TskProberMappingParseCpAndWaferId;
import com.vtest.it.tskplatform.pojo.mes.MesConfigBean;
import com.vtest.it.tskplatform.pojo.mes.SlotAndSequenceConfigBean;
import com.vtest.it.tskplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.tskplatform.service.mes.impl.GetMesInforImpl;
import com.vtest.it.tskplatform.service.tools.impl.GenerateRawdata;
import com.vtest.it.tskplatform.service.tools.impl.GetFileListNeedDeal;
import com.vtest.it.tskplatform.service.tools.impl.InitMesConfigToRawdataProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;

@Service
public class TskPlatformDataDeal {
    @Autowired
    private GetFileListNeedDeal getFileListNeedDeal;
    @Autowired
    private TskProberMappingParse tskProberMappingParse;
    @Autowired
    private GetMesInforImpl getMesInfor;
    @Autowired
    private TskProberMappingParseCpAndWaferId tskProberMappingParseCpAndWaferId;
    @Autowired
    private InitMesConfigToRawdataProperties initMesConfigToRawdataProperties;
    @Autowired
    private GenerateRawdata generateRawdata;
    public ArrayList<File> deal(ArrayList<File> fileNeedDealList) {
        ArrayList<File> filesBeDealedList = new ArrayList<>();
        for (File lot : fileNeedDealList) {
            SlotAndSequenceConfigBean slotAndSequenceConfigBean = getMesInfor.getLotSlotConfig(lot.getName());
            if (slotAndSequenceConfigBean.getReadType().equals("SLOT")) {
                File[] waferIds = lot.listFiles();
                if (waferIds.length > 0) {
                    for (File wafer : waferIds) {
                        try {
                            Integer slot = Integer.valueOf(wafer.getName().substring(0, 3));
                            String waferId = getMesInfor.getWaferIdBySlot(lot.getName(), "" + slot);
                            RawdataInitBean rawdataInitBean = new RawdataInitBean();
                            String cpProcess = tskProberMappingParseCpAndWaferId.parse(wafer).split(":")[1];
                            MesConfigBean mesConfigBean = getMesInfor.getWaferConfigFromMes(waferId, cpProcess);
                            tskProberMappingParse.parse(wafer, Integer.valueOf(slotAndSequenceConfigBean.getGpibBin()), rawdataInitBean);
                            initMesConfigToRawdataProperties.initMesConfig(rawdataInitBean, mesConfigBean);
                            generateRawdata.generate(rawdataInitBean);
                            filesBeDealedList.add(wafer);
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
                            String waferId = wafer.getName().substring(4, wafer.getName().length());
                            RawdataInitBean rawdataInitBean = new RawdataInitBean();
                            tskProberMappingParse.parse(wafer, 0, rawdataInitBean);
                            rawdataInitBean.getDataProperties().put("Wafer ID", waferId);
                            filesBeDealedList.add(wafer);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return filesBeDealedList;
    }
}
