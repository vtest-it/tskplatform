package com.vtest.it.tskplatform.dao.vtptmt;

import com.vtest.it.tskplatform.pojo.vtptmt.DataParseIssueBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Mapper
@Repository
public interface VtptmtDao {
    public String getInformation(@Param("username") String username);
    public int dataErrorsRecord(@Param("list") ArrayList<DataParseIssueBean> list);
}
