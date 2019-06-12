package com.vtest.it.tskplatform.service.vtptmt.impl;

import com.vtest.it.tskplatform.dao.vtptmt.VtptmtDao;
import com.vtest.it.tskplatform.pojo.vtptmt.CheckItemBean;
import com.vtest.it.tskplatform.pojo.vtptmt.DataInforToMesBean;
import com.vtest.it.tskplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.tskplatform.service.vtptmt.VtptmtInfor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public int dataErrorsRecord(ArrayList<DataParseIssueBean> list) {
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
}
