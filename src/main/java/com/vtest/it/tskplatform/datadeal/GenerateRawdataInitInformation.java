package com.vtest.it.tskplatform.datadeal;

import com.vtest.it.tskplatform.MappingParseTools.TskProberMappingParse;
import com.vtest.it.tskplatform.pojo.mes.MesConfigBean;
import com.vtest.it.tskplatform.pojo.mes.SlotAndSequenceConfigBean;
import com.vtest.it.tskplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.tskplatform.service.mes.impl.GetMesInforImpl;
import com.vtest.it.tskplatform.service.tools.impl.InitMesConfigToRawdataProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class GenerateRawdataInitInformation {
    @Autowired
    private TskProberMappingParse tskProberMappingParse;
    @Autowired
    private InitMesConfigToRawdataProperties initMesConfigToRawdataProperties;
    @Autowired
    private GetMesInforImpl getMesInfor;

    public RawdataInitBean generateRawdata(File wafer, SlotAndSequenceConfigBean slotAndSequenceConfigBean, String waferId, String lot) throws Exception {
        RawdataInitBean rawdataInitBean = new RawdataInitBean();
        try {
            tskProberMappingParse.parse(wafer, Integer.valueOf(slotAndSequenceConfigBean.getGpibBin()), rawdataInitBean, waferId, lot);
        } catch (Exception e) {
            throw new Exception("there are error in file coding");
        }
        MesConfigBean mesConfigBean = getMesInfor.getWaferConfigFromMes(waferId, rawdataInitBean.getDataProperties().get("CP Process"));
        if (null == mesConfigBean.getInnerLot()) {
            throw new Exception("can't find this wafer in mes system : no such wafer or cpProcess");
        }
        initMesConfigToRawdataProperties.initMesConfig(rawdataInitBean, mesConfigBean);
        return rawdataInitBean;
    }
}
