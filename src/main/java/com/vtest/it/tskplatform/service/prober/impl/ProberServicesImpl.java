package com.vtest.it.tskplatform.service.prober.impl;

import com.vtest.it.tskplatform.dao.prober.ProberDao;
import com.vtest.it.tskplatform.pojo.vtptmt.BinWaferInforBean;
import com.vtest.it.tskplatform.service.prober.ProberServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED, transactionManager = "proberTransactionManager")
public class ProberServicesImpl implements ProberServices {
    @Autowired
    private ProberDao proberDao;

    @Override
    public int insertSiteInforToBinInfoSummary(String customerCode, String device, String lot, String cp, String waferId, HashMap<Integer, HashMap<Integer, Integer>> siteMap, String testType, ArrayList<Integer> passBins) {
        return proberDao.insertSiteInforToBinInfoSummary(customerCode, device, lot, cp, waferId, siteMap, testType, passBins);
    }

    @Override
    public int deleteSiteInforToBinInfoSummary(String customerCode, String device, String lot, String cp, String waferId) {
        return proberDao.deleteSiteInforToBinInfoSummary(customerCode, device, lot, cp, waferId);
    }

    @Override
    public int insertWaferInforToBinWaferSummary(BinWaferInforBean binWaferInforBean) {
        return proberDao.insertWaferInforToBinWaferSummary(binWaferInforBean);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, transactionManager = "proberTransactionManager")
    public void singleWaferDeal(BinWaferInforBean binWaferInforBean, String customerCode, String device, String lot, String cp, String waferId, HashMap<Integer, HashMap<Integer, Integer>> siteMap, String testType, ArrayList<Integer> passBins) {
        proberDao.deleteSiteInforToBinInfoSummary(customerCode, device, lot, cp, waferId);
        proberDao.insertSiteInforToBinInfoSummary(customerCode, device, lot, cp, waferId, siteMap, "P", passBins);
        proberDao.insertSiteInforToBinInfoSummary(customerCode, device, lot, cp, waferId, siteMap, "F", passBins);
        proberDao.insertWaferInforToBinWaferSummary(binWaferInforBean);
    }
}
