package com.vtest.it.tskplatform.service.vtptmt.impl;

import com.vtest.it.tskplatform.dao.vtptmt.VtptmtDao;
import com.vtest.it.tskplatform.pojo.mes.MesProperties;
import com.vtest.it.tskplatform.pojo.vtptmt.BinWaferInforBean;
import com.vtest.it.tskplatform.pojo.vtptmt.CheckItemBean;
import com.vtest.it.tskplatform.pojo.vtptmt.DataInforToMesBean;
import com.vtest.it.tskplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.tskplatform.service.vtptmt.VtptmtInfor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional(transactionManager = "vtptmtTransactionManager", isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
public class VtptmtInforImpl implements VtptmtInfor {
    @Autowired
    private VtptmtDao vtptmtDao;

    @Override
    @Transactional(transactionManager = "vtptmtTransactionManager", isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public int dataErrorsRecord(ArrayList<DataParseIssueBean> list) {
        for (DataParseIssueBean bean : list) {
            if (null == bean.getCustomCode()) {
                bean.setCustomCode("NA");
            }
            if (null == bean.getDevice()) {
                bean.setDevice("NA");
            }
        }
        return vtptmtDao.dataErrorsRecord(list);
    }

    @Override
    @Cacheable(cacheNames = {"SystemPropertiesCache"}, key = "#root.methodName")
    public ArrayList<CheckItemBean> getCheckItemList() {
        return vtptmtDao.getCheckItemList();
    }

    @Override
    @Cacheable(cacheNames = {"SystemPropertiesCache"}, key = "#root.methodName")
    public ArrayList<DataInforToMesBean> getList() {
        return vtptmtDao.getList();
    }

    @Override
    @Cacheable(cacheNames = {"SystemPropertiesCache"}, key = "#root.methodName")
    public ArrayList<BinWaferInforBean> getTesterStatus() {
        return vtptmtDao.getTesterStatus();
    }

    @Override
    @Cacheable(cacheNames = {"SystemPropertiesCache"}, key = "#root.methodName+'&'+#tester")
    public BinWaferInforBean getTesterStatusSingle(String tester) {
        return vtptmtDao.getTesterStatusSingle(tester);
    }

    @Override
    @Transactional(transactionManager = "vtptmtTransactionManager", isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public int insertWaferInforToBinWaferSummary(BinWaferInforBean binWaferInforBean) {
        return vtptmtDao.insertWaferInforToBinWaferSummary(binWaferInforBean);
    }

    @Override
    @Transactional(transactionManager = "vtptmtTransactionManager", isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public void waferFailTypeCheckOthers(String waferId, String cpProcess, String tester) {
        vtptmtDao.waferFailTypeCheckOthers(waferId, cpProcess, tester);
    }

    @Override
    @Cacheable(cacheNames = {"SystemPropertiesCache"}, key = "#root.methodName")
    public MesProperties getProperties() {
        return vtptmtDao.getProperties();
    }

    @Override
    @CacheEvict(cacheNames = {"SystemPropertiesCache"}, key = "'getProperties'")
    public int updateProperties(MesProperties mesProperties) {
        return vtptmtDao.updateProperties(mesProperties);
    }

    @Transactional(transactionManager = "vtptmtTransactionManager", isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    @Caching(evict = {
            @CacheEvict(cacheNames = {"SystemPropertiesCache"}, key = "'getTesterStatusSingle&'+#tester"),
            @CacheEvict(cacheNames = {"SystemPropertiesCache"}, key = "'getTesterStatus'")
    })
    public void singleWaferDeal(BinWaferInforBean binWaferInforBean, String waferId, String cpProcess, String tester) {
        vtptmtDao.insertWaferInforToBinWaferSummary(binWaferInforBean);
        vtptmtDao.waferFailTypeCheckOthers(waferId, cpProcess, tester);
    }
}
