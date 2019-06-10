package com.vtest.it.tskplatform.datadeal;

import com.vtest.it.tskplatform.pojo.mes.SlotAndSequenceConfigBean;
import com.vtest.it.tskplatform.service.mes.impl.GetMesInforImpl;
import com.vtest.it.tskplatform.service.tools.impl.GetFileListNeedDeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;

@Service
public class TskPlatformDataDeal {
    @Autowired
    private GetFileListNeedDeal getFileListNeedDeal;
    @Autowired
    private GetMesInforImpl getMesInfor;
    public void deal(File dataSource) {
        ArrayList<File> fileNeedCheckList = getFileListNeedDeal.getList(dataSource, 60);
        for (File lot : fileNeedCheckList) {
            SlotAndSequenceConfigBean slotAndSequenceConfigBean=getMesInfor.getLotSlotConfig(lot.getName());
            if (slotAndSequenceConfigBean.getReadType().equals("SLOT")){
                File[] waferIds=lot.listFiles();
                for (File wafer : waferIds) {
                    Integer slot=Integer.valueOf(wafer.getName().substring(0,3));
                    String waferId=getMesInfor.getWaferIdBySlot(lot.getName(),""+slot);
                    System.out.println(waferId);
                }
            }else {
                File[] waferIds=lot.listFiles();
                for (File wafer : waferIds) {
                    String waferId=wafer.getName().substring(4,wafer.getName().length());
                    System.out.println(waferId);
                }
            }
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
