package com.vtest.it.tskplatform.dao.vtptmt;

import com.vtest.it.tskplatform.pojo.mes.MesProperties;
import com.vtest.it.tskplatform.pojo.vtptmt.BinWaferInforBean;
import com.vtest.it.tskplatform.pojo.vtptmt.CheckItemBean;
import com.vtest.it.tskplatform.pojo.vtptmt.DataInforToMesBean;
import com.vtest.it.tskplatform.pojo.vtptmt.DataParseIssueBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Mapper
@Repository
public interface VtptmtDao {
    public int dataErrorsRecord(@Param("list") ArrayList<DataParseIssueBean> list);

    public ArrayList<CheckItemBean> getCheckItemList();

    public ArrayList<DataInforToMesBean> getList();

    public ArrayList<BinWaferInforBean> getTesterStatus();

    public BinWaferInforBean getTesterStatusSingle(@Param("tester") String tester);

    public int insertWaferInforToBinWaferSummary(BinWaferInforBean binWaferInforBean);

    public void waferFailTypeCheckOthers(@Param("waferId") String waferId, @Param("cp") String cpProcess, @Param("tester") String tester);

    public MesProperties getProperties();

    public int updateProperties(MesProperties mesProperties);
}
