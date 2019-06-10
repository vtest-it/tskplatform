package com.vtest.it.tskplatform.advisor;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GetRightWaferId implements Ordered {
    @Override
    public int getOrder() {
        return 0;
    }

    @Before("execution(public * getWaferIdBySlot(..))&&args(lot,slot)")
    public void modifyWaferId(String lot, String slot) {
        System.out.println("slot is: " + slot);
    }

    @AfterReturning(value = "execution(public * getWaferIdBySlot(..))", returning = "waferId")
    public void modifyWaferIdAfter(String waferId) {
        System.out.println("waferId is: " + waferId);
    }
}
