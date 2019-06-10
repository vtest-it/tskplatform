package com.vtest.it.tskplatform.service.vtptmt;

import com.vtest.it.tskplatform.dao.vtptmt.VtptmtDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class GetUser {
    @Autowired
    private VtptmtDao vtptmtDao;

    @Cacheable(value = {"SystemPropertiesCache","MesInformationCache"},key = "#username")
    public String getUser(String username){
        String password=vtptmtDao.getInformation(username);
        if (password==null){
            return "NA";
        }
        return password;
    }
}
