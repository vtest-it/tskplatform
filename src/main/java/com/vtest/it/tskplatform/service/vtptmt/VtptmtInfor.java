package com.vtest.it.tskplatform.service.vtptmt;

import com.vtest.it.tskplatform.pojo.vtptmt.CheckItemBean;
import com.vtest.it.tskplatform.pojo.vtptmt.DataInforToMesBean;
import com.vtest.it.tskplatform.pojo.vtptmt.DataParseIssueBean;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

public interface VtptmtInfor {
    public int dataErrorsRecord(@Param("list") ArrayList<DataParseIssueBean> list);

    public ArrayList<CheckItemBean> getCheckItemList();

    public ArrayList<DataInforToMesBean> getList();
}
