package com.vtest.it.tskplatform.datadeal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;

@Component
public class StartDeal {
    @Value("${system.properties.tsk.mapup}")
    private String mapup;
    @Autowired
    private TskPlatformDataDeal tskPlatformDataDeal;

    @Scheduled(fixedDelay = 2000)
    public void startDeal() {
        File dataSource = new File(mapup);
        if (dataSource.listFiles().length==0){
            return;
        }
        ArrayList<File> fileNeedDealList=new ArrayList<>();
        for (File lot : dataSource.listFiles()) {
            fileNeedDealList.add(lot);
        }
        tskPlatformDataDeal.deal(fileNeedDealList);
    }
}
