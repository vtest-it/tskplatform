package com.vtest.it.tskplatform.service.tools.impl.rawdataCheck;


import com.vtest.it.tskplatform.dao.vtptmt.VtptmtDao;
import com.vtest.it.tskplatform.pojo.vtptmt.DataInforToMesBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CheckIfInforToMes {
    @Autowired
    private VtptmtDao vtptmtDao;

    public boolean check(String customCode, String device) {
        ArrayList<DataInforToMesBean> allConfigs = vtptmtDao.getList();
        for (DataInforToMesBean bean : allConfigs) {
            if (bean.getCustomCode().equals(customCode) && (bean.getDevice().equals("ALL") || bean.getDevice().equals(device))) {
                return true;
            }
        }
        return false;
    }
}
