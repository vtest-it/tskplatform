package com.vtest.it.tskplatform.service.mes.impl;

import com.vtest.it.tskplatform.dao.mes.MesDao;
import com.vtest.it.tskplatform.pojo.mes.CustomerCodeAndDeviceBean;
import com.vtest.it.tskplatform.pojo.mes.MesConfigBean;
import com.vtest.it.tskplatform.pojo.mes.SlotAndSequenceConfigBean;
import com.vtest.it.tskplatform.service.mes.GetMesInfor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "mesDataSourceTransactionManager", isolation = Isolation.REPEATABLE_READ, readOnly = true, propagation = Propagation.REQUIRED)
public class GetMesInforImpl implements GetMesInfor {
    @Autowired
    private MesDao mesDao;
    @Override
    @Cacheable(cacheNames = "MesInformationCache",key = "#root.methodName+'&'+#lot+'&'+#slot")
    public String getWaferIdBySlot(String lot, String slot) {
        String waferId=mesDao.getWaferIdBySlot(lot,slot);
        return null==waferId?"NA":waferId;
    }

    @Override
    @Cacheable(cacheNames = "MesInformationCache",key = "#root.methodName+'&'+#lot")
    public SlotAndSequenceConfigBean getLotSlotConfig(String lot) {
        SlotAndSequenceConfigBean slotAndSequenceConfigBean= mesDao.getLotSlotConfig(lot);
        SlotAndSequenceConfigBean slotAndSequenceConfigBeanTemp=new SlotAndSequenceConfigBean();
        slotAndSequenceConfigBeanTemp.setGpibBin("0");
        slotAndSequenceConfigBeanTemp.setReadType("OCR");
        slotAndSequenceConfigBeanTemp.setSequence("1-25");
        return null==slotAndSequenceConfigBean?slotAndSequenceConfigBeanTemp:slotAndSequenceConfigBean;
    }

    @Override
    @Cacheable(cacheNames = "MesInformationCache",key = "#root.methodName+'&'+#waferId+'&'+#cpProcess")
    public MesConfigBean getWaferConfigFromMes(String waferId, String cpProcess) {
        MesConfigBean mesConfigBean= mesDao.getWaferConfigFromMes(waferId,cpProcess);
        return null==mesConfigBean?new MesConfigBean():mesConfigBean;
    }

    @Override
    @Cacheable(cacheNames = "MesInformationCache",key = "#root.methodName+'&'+#lot")
    public CustomerCodeAndDeviceBean getCustomerAndDeviceByLot(String lot) {
        CustomerCodeAndDeviceBean customerCodeAndDeviceBean= mesDao.getCustomerAndDeviceByLot(lot);
        return null==customerCodeAndDeviceBean?new CustomerCodeAndDeviceBean():customerCodeAndDeviceBean;
    }

    @Override
    @Cacheable(cacheNames = "MesInformationCache", key = "#root.methodName+'&'+#waferId+'&'+#cpStep")
    public CustomerCodeAndDeviceBean getCustomerAndDeviceByWaferAndCpStep(String waferId, String cpStep) {
        CustomerCodeAndDeviceBean customerCodeAndDeviceBean = mesDao.getCustomerAndDeviceByWaferAndCpStep(waferId, cpStep);
        return null == customerCodeAndDeviceBean ? new CustomerCodeAndDeviceBean() : customerCodeAndDeviceBean;
    }

    @Override
    public String getWaferIdCurrentCpStep(String waferId) {
        String currentCpStep = mesDao.getWaferIdCurrentCpStep(waferId);
        return null == currentCpStep ? "NA" : currentCpStep;
    }
}
