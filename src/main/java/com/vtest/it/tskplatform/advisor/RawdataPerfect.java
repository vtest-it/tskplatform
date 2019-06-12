package com.vtest.it.tskplatform.advisor;

import com.vtest.it.tskplatform.pojo.rawdataBean.RawdataInitBean;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RawdataPerfect implements Ordered {
    @Override
    public int getOrder() {
        return 0;
    }

    @Before("execution(* generate(..))&& target(com.vtest.it.tskplatform.service.tools.impl.GenerateRawdata)&&args(rawdataInitBean)")
    public void perfect(RawdataInitBean rawdataInitBean) {

    }
}
