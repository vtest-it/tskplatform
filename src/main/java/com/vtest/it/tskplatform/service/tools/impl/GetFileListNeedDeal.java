package com.vtest.it.tskplatform.service.tools.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@Service
public class GetFileListNeedDeal {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetFileListNeedDeal.class);
    private static final String FORMAT = "yyyyMMddHHmmss";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT);
    public ArrayList<File> getList(ArrayList<File> fileNeedDealList,int seconds){
        ArrayList<File> fileNeedCheckList=new ArrayList<>();
        for (File lot : fileNeedDealList) {
            if (((System.currentTimeMillis()-lot.lastModified())/(1000))>seconds){
                fileNeedCheckList.add(lot);
            }
        }
        return  fileNeedCheckList;
    }

    public boolean checkWafersAgain(File lot) {
        long now = System.currentTimeMillis();
        LOGGER.error(lot.getName() + " time now: " + simpleDateFormat.format(now));
        for (File wafer : lot.listFiles()) {
            LOGGER.error(lot.getName() + " & " + wafer.getName() + " last modify time: " + simpleDateFormat.format(wafer.lastModified()));
            if (((now - wafer.lastModified()) / 1000 < 60)) {
                LOGGER.error(lot.getName() + " result: " + false);
                return false;
            }
        }
        LOGGER.error(lot + " result: " + true);
        return true;
    }
}
