package com.vtest.it.tskplatform.service.tools;


import com.vtest.it.tskplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.tskplatform.pojo.vtptmt.BinWaferInforBean;

public interface RawDataAfterDeal {
    void deal(RawdataInitBean rawdataInitBean, BinWaferInforBean binWaferInforBean);
}
