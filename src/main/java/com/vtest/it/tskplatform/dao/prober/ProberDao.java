package com.vtest.it.tskplatform.dao.prober;

import com.vtest.it.tskplatform.pojo.vtptmt.BinWaferInforBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
@Repository
public interface ProberDao {
    public int insertSiteInforToBinInfoSummary(@Param("customerCode") String customerCode, @Param("device") String device, @Param("lot") String lot, @Param("cp") String cp, @Param("waferId") String waferId, @Param("siteMap") HashMap<Integer, HashMap<Integer, Integer>> siteMap, @Param("testType") String testType, @Param("passBins") ArrayList<Integer> passBins);

    public int deleteSiteInforToBinInfoSummary(@Param("customerCode") String customerCode, @Param("device") String device, @Param("lot") String lot, @Param("cp") String cp, @Param("waferId") String waferId);

    public int insertWaferInforToBinWaferSummary(BinWaferInforBean binWaferInforBean);
}
