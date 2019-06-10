package com.vtest.it.tskplatform.advisor;

import com.vtest.it.tskplatform.pojo.rawdataBean.RawdataInitBean;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.File;

@Aspect
@Component
public class TskMappingParseFail implements Ordered {
    @Override
    public int getOrder() {
        return 1;
    }

    @AfterThrowing(value = "execution(* parse(..)) && target(com.vtest.it.tskplatform.MappingParseTools.TskProberMappingParse)&& args(file,gpibBin,bean)",throwing = "exception")
    private void  getMappingDealException(Exception exception, File file, int gpibBin, RawdataInitBean bean){
        System.out.println(exception.toString());
        System.out.println(file.getPath());
        System.out.println(gpibBin);
    }
    public void dealException(){

    }
}
