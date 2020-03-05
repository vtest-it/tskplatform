package com.vtest.it.tskplatform.service.mes;

import com.vtest.it.tskplatform.pojo.mes.CustomerCodeAndDeviceBean;
import com.vtest.it.tskplatform.pojo.mes.MesConfigBean;
import com.vtest.it.tskplatform.pojo.mes.SlotAndSequenceConfigBean;

public interface GetMesInfor {
    public String getWaferIdBySlot(String lot, String slot);
    public SlotAndSequenceConfigBean getLotSlotConfig(String lot);
    public MesConfigBean getWaferConfigFromMes(String waferId,String cpProcess);
    public CustomerCodeAndDeviceBean getCustomerAndDeviceByLot(String lot);
    public CustomerCodeAndDeviceBean getCustomerAndDeviceByWaferAndCpStep(String waferId, String cpStep);

    public String getWaferIdCurrentCpStep(String waferId);
}
