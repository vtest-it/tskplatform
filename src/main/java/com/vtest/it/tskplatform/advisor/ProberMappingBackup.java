package com.vtest.it.tskplatform.advisor;

import com.vtest.it.tskplatform.MappingParseTools.TskProberMappingParseCpAndWaferId;
import com.vtest.it.tskplatform.pojo.mes.CustomerCodeAndDeviceBean;
import com.vtest.it.tskplatform.service.mes.GetMesInfor;
import com.vtest.it.tskplatform.service.tools.impl.GetFileListNeedDeal;
import org.apache.commons.io.FileUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
@Order(0)
public class ProberMappingBackup {
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
    @Autowired
    private TskProberMappingParseCpAndWaferId tskProberMappingParseCpAndWaferId;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProberMappingBackup.class);
    @Around("target(com.vtest.it.tskplatform.datadeal.TskPlatformDataDeal)&&execution(* deal(..))")
    public void backupProberMapping(ProceedingJoinPoint proceedingJoinPoint) {
        String regex = "[0-9]{1,}";
        String format = "yyyyMMddHHmmss";
        Pattern pattern = Pattern.compile(regex);
        ArrayList<File> fileNeedCheckList = getFileListNeedDeal.getList((ArrayList<File>) proceedingJoinPoint.getArgs()[0], 60);
        ArrayList<File> fileNeedDealList = new ArrayList<>();
        for (File file : fileNeedCheckList) {
            if (file.isFile()) {
                try {
                    FileUtils.copyFile(file, new File(errorPath + "/" + file.getName()));
                    FileUtils.forceDelete(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (file.listFiles().length == 0) {
                try {
                    FileUtils.forceDelete(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (!getFileListNeedDeal.checkWafersAgain(file)) {
                continue;
            }
            String lot = file.getName();
            CustomerCodeAndDeviceBean customerCodeAndDeviceBean = getMesInfor.getCustomerAndDeviceByLot(lot);
            if (null != customerCodeAndDeviceBean.getCustomerCode()) {
                for (File wafer : file.listFiles()) {
                    try {
                        String waferIdSurface = wafer.getName().substring(4);
                        String result = tskProberMappingParseCpAndWaferId.parse(wafer);
                        String waferFromFile = result.split(":")[0].trim();
                        String cpProcess = result.split(":")[1].trim();
                        Matcher matcher = pattern.matcher(cpProcess.substring(2));
                        String customerCode = customerCodeAndDeviceBean.getCustomerCode();
                        String device = customerCodeAndDeviceBean.getDevice();
                        try {
                            Integer slot = Integer.valueOf(wafer.getName().substring(0, 3));
                            String waferId = getMesInfor.getWaferIdBySlot(lot, "" + slot);
                            CustomerCodeAndDeviceBean customerCodeAndDeviceBeanByWaferAndCpStep = getMesInfor.getCustomerAndDeviceByWaferAndCpStep(waferId, cpProcess);
                            if ((null != customerCodeAndDeviceBeanByWaferAndCpStep.getCustomerCode())) {
                                FileUtils.copyFile(wafer, new File(backupPath + "/" + customerCode + "/" + customerCodeAndDeviceBeanByWaferAndCpStep.getDevice() + "/" + lot + "/" + cpProcess + "/" + wafer.getName() + "_" + new SimpleDateFormat(format).format(new Date())));
                            } else {
                                FileUtils.copyFile(wafer, new File(backupPath + "/" + customerCode + "/" + device + "/" + lot + "/" + cpProcess + "/" + wafer.getName() + "_" + new SimpleDateFormat(format).format(new Date())));
                            }
                        } catch (Exception e) {
                            FileUtils.copyFile(wafer, new File(backupPath + "/" + customerCode + "/" + device + "/" + lot + "/" + cpProcess + "/" + wafer.getName() + "_" + new SimpleDateFormat(format).format(new Date())));
                        }
                        if ((!waferIdSurface.trim().equals(waferFromFile.trim())) || !matcher.find()) {
                            FileUtils.copyFile(wafer, new File(errorPath + "/waferCheckError/" + lot + "/" + wafer.getName()));
                            FileUtils.forceDelete(wafer);
                        }
                    } catch (IOException e) {
                        try {
                            FileUtils.copyFile(wafer, new File(errorPath + "/waferCheckError/" + lot + "/" + wafer.getName()));
                            FileUtils.forceDelete(wafer);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                try {
                    if (file.listFiles().length > 0) {
                        fileNeedDealList.add(file);
                        LOGGER.error("backup path: " + mapDownPath + file.getName());
                        FileUtils.copyDirectory(file, new File(mapDownPath + "/" + file.getName()));
                    } else {
                        FileUtils.forceDelete(file);
                    }
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
        try {
            ArrayList<File> fileBeDealList = (ArrayList<File>) proceedingJoinPoint.proceed(new Object[]{fileNeedDealList});
            backupProberMappingAfterDeal(fileBeDealList);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void backupProberMappingAfterDeal(ArrayList<File> fileBeDealList) {
        for (File file : fileBeDealList) {
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
