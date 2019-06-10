package com.vtest.it.tskplatform.dao.mes;

import com.vtest.it.tskplatform.pojo.mes.CustomerCodeAndDeviceBean;
import com.vtest.it.tskplatform.pojo.mes.MesConfigBean;
import com.vtest.it.tskplatform.pojo.mes.SlotAndSequenceConfigBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MesDao {
    public String getWaferIdBySlot(@Param("lot")String lot,@Param("slot")String slot);
    public SlotAndSequenceConfigBean getLotSlotConfig(@Param("lot") String lot);
    public MesConfigBean getWaferConfigFromMes(@Param("waferId") String waferId, @Param("cpProcess") String cpProcess);
    public CustomerCodeAndDeviceBean getCustomerAndDeviceByLot(@Param("lot")String lot);
}
