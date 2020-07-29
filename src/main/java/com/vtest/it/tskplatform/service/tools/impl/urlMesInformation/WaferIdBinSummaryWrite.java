package com.vtest.it.tskplatform.service.tools.impl.urlMesInformation;

import com.vtest.it.tskplatform.pojo.rawdataBean.RawdataInitBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

@Service
public class WaferIdBinSummaryWrite {
    private WaferidInforIntoMes waferidInforIntoMes;

    @Autowired
    public void setWaferidInforIntoMes(WaferidInforIntoMes waferidInforIntoMes) {
        this.waferidInforIntoMes = waferidInforIntoMes;
    }

    public void write(RawdataInitBean rawdataInitBean) {
        String lot = rawdataInitBean.getProperties().get("Lot ID");
        String cp = rawdataInitBean.getProperties().get("CP Process");
        String waferId = rawdataInitBean.getProperties().get("Wafer ID");

        HashMap<Integer, HashMap<Integer, Integer>> siteBinSmmary = rawdataInitBean.getSiteBinSum();
        TreeMap<Integer, Integer> binSummary = new TreeMap<>();
        Set<Integer> siteSet = siteBinSmmary.keySet();
        for (Integer site : siteSet) {
            HashMap<Integer, Integer> binMap = siteBinSmmary.get(site);
            Set<Integer> binSet = binMap.keySet();
            for (Integer bin : binSet) {
                if (binSummary.containsKey(bin)) {
                    binSummary.put(bin, binSummary.get(bin) + binMap.get(bin));
                } else {
                    binSummary.put(bin, binMap.get(bin));
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        Set<Integer> set = binSummary.keySet();
        for (Integer bin : set) {
            sb.append("|Bin").append(bin).append(":").append(binSummary.get(bin));
        }
        String startTime = rawdataInitBean.getProperties().get("Test Start Time");
        String endTime = rawdataInitBean.getProperties().get("Test End Time");
        startTime = startTime.length() > 14 ? startTime.substring(0, 14) : startTime;
        endTime = endTime.length() > 14 ? endTime.substring(0, 14) : endTime;
        sb.append("|TestStart:").append(startTime);
        sb.append("|TestEnd:").append(endTime);
        String summary = sb.toString();
        waferidInforIntoMes.write(lot, waferId, cp, summary);
    }
}
