package com.vtest.it.tskplatform.service.tools.impl;

import com.vtest.it.tskplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.tskplatform.service.tools.RawDataAfterDeal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class AbstractRawDataAfterDeal implements RawDataAfterDeal {
    public void perfectDeal(RawdataInitBean bean) {
        Integer passDieSum = 0;
        Integer failDieSum = 0;
        Integer totalDieSum = 0;
        LinkedHashMap<String, String> properties = bean.getProperties();
        String[] passBins = properties.get("Pass Bins").split(",");
        ArrayList<Integer> passBinArr = new ArrayList<>();
        for (int i = 0; i < passBins.length; i++) {
            passBinArr.add(Integer.valueOf(passBins[i]));
        }
        HashMap<Integer, HashMap<Integer, Integer>> siteBinSum = new HashMap<>();
        HashMap<String, String> testDieMap = bean.getTestDieMap();
        Collection<String> values = testDieMap.values();
        totalDieSum = values.size();
        for (String value : values) {
            Integer softBin = Integer.valueOf(value.substring(4, 8).trim());
            if (passBinArr.contains(softBin)) {
                passDieSum += 1;
            }
            Integer site = Integer.valueOf(value.substring(8).trim());
            if (siteBinSum.containsKey(site)) {
                HashMap<Integer, Integer> binSummary = siteBinSum.get(site);
                if (binSummary.containsKey(softBin)) {
                    binSummary.put(softBin, binSummary.get(softBin) + 1);
                } else {
                    binSummary.put(softBin, 1);
                }
            } else {
                HashMap<Integer, Integer> binSummary = new HashMap<>();
                binSummary.put(softBin, 1);
                siteBinSum.put(site, binSummary);
            }
        }
        failDieSum = totalDieSum - passDieSum;
        properties.put("Gross Die", String.valueOf(totalDieSum));
        properties.put("Pass Die", String.valueOf(passDieSum));
        properties.put("Fail Die", String.valueOf(failDieSum));
        properties.put("Wafer Yield", (totalDieSum == 0 ? 0 : String.format("%4.2f", (double) passDieSum * 100 / totalDieSum)) + "%");
        if (properties.get("CP Yields").equals("NA")) {
            properties.put("CP Yields", properties.get("CP Process") + "&" + (totalDieSum == 0 ? 0 : String.format("%4.2f", (double) passDieSum * 100 / totalDieSum)) + "%;");
        } else {
            properties.put("CP Yields", properties.get("CP Yields") + ";" + properties.get("CP Process") + "&" + (totalDieSum == 0 ? 0 : String.format("%4.2f", (double) passDieSum * 100 / totalDieSum)) + "%;");
        }
        bean.setProperties(properties);
        bean.setSiteBinSum(siteBinSum);
    }
}
