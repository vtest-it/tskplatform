package com.vtest.it.tskplatform.advisor;

import com.vtest.it.tskplatform.pojo.mes.CustomerCodeAndDeviceBean;
import com.vtest.it.tskplatform.service.mes.GetMesInfor;
import com.vtest.it.tskplatform.service.tools.impl.GetFileListNeedDeal;
import org.apache.commons.io.FileUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Aspect
@Component
public class ProberMappingBackup implements Ordered {
    @Value("${system.properties.tsk.mapdown}")
    private String mapDownPath;
    @Value("${system.properties.tsk.backup-path}")
    private String backupPath;
    @Value("${system.properties.tsk.error-path}")
    private String errorPath;
    @Autowired
    private GetMesInfor getMesInfor;
    @Autowired
    private GetFileListNeedDeal getFileListNeedDeal;

    @Before("target(com.vtest.it.tskplatform.datadeal.TskPlatformDataDeal)&& args(dataSource)")
    public void backupProberMapping(File dataSource) {
        ArrayList<File> fileNeedCheckList = getFileListNeedDeal.getList(dataSource, 60);
        for (File file : fileNeedCheckList) {
            if (file.isFile()) {
                try {
                    FileUtils.copyDirectory(file, new File(errorPath));
                    FileUtils.forceDelete(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            String lot = file.getName();
            CustomerCodeAndDeviceBean customerCodeAndDeviceBean = getMesInfor.getCustomerAndDeviceByLot(lot);
            if (null != customerCodeAndDeviceBean.getCustomerCode()) {
                try {
                    FileUtils.copyDirectory(file, new File(mapDownPath + "/" + file.getName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    FileUtils.copyDirectory(file, new File(mapDownPath + "/" + file.getName()));
                    FileUtils.copyDirectory(file, new File(errorPath + "/" + file.getName()));
                    FileUtils.forceDelete(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @AfterReturning("execution(* deal(..))&&args(dataSource)")
    public void backupProberMappingAfterDeal(File dataSource) {

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
