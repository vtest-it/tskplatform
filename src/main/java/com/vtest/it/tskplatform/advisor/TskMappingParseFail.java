package com.vtest.it.tskplatform.advisor;

import com.vtest.it.tskplatform.pojo.mes.SlotAndSequenceConfigBean;
import com.vtest.it.tskplatform.pojo.vtptmt.DataParseIssueBean;
import com.vtest.it.tskplatform.service.tools.impl.rawdataCheck.GetIssueBean;
import com.vtest.it.tskplatform.service.vtptmt.impl.VtptmtInforImpl;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Aspect
@Component
public class TskMappingParseFail implements Ordered {
    @Autowired
    private GetIssueBean getIssueBean;
    @Autowired
    private VtptmtInforImpl vtptmtInfor;
    @Override
    public int getOrder() {
        return 0;
    }

    @AfterThrowing(value = "execution(* generateRawdata(..)) && target(com.vtest.it.tskplatform.datadeal.GenerateRawdataInitInformation)&& args(wafer,slotAndSequenceConfigBean,waferId,lot)", throwing = "exception")
    private void mappingParseDealException(File wafer, SlotAndSequenceConfigBean slotAndSequenceConfigBean, String waferId, String lot, Exception exception) {
        try {
            DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBeanForException(5, exception.getMessage(), wafer, waferId, lot);
            ArrayList<DataParseIssueBean> list = new ArrayList<>();
            list.add(dataParseIssueBean);
            vtptmtInfor.dataErrorsRecord(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
