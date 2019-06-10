package com.vtest.it.tskplatform.service.tools.impl;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;

@Service
public class GetFileListNeedDeal {
    public ArrayList<File> getList(ArrayList<File> fileNeedDealList,int seconds){
        ArrayList<File> fileNeedCheckList=new ArrayList<>();
        for (File lot : fileNeedDealList) {
            if (((System.currentTimeMillis()-lot.lastModified())/(1000))>seconds){
                fileNeedCheckList.add(lot);
            }
        }
        return  fileNeedCheckList;
    }
}
