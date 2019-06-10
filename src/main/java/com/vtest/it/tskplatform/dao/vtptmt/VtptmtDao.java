package com.vtest.it.tskplatform.dao.vtptmt;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface VtptmtDao {
    public String getInformation(@Param("username") String username);
}
