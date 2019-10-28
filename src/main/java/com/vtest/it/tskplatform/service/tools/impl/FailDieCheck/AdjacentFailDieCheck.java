package com.vtest.it.tskplatform.service.tools.impl.FailDieCheck;


import com.vtest.it.tskplatform.pojo.rawdataBean.RawdataInitBean;
import com.vtest.it.tskplatform.pojo.vtptmt.BinWaferInforBean;
import com.vtest.it.tskplatform.service.tools.impl.AbstractRawDataAfterDeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

@Service
public class AdjacentFailDieCheck extends AbstractRawDataAfterDeal {
    @Autowired
    private AdjacentFailDieCheckTool adjacentFailDieCheckTool;
    @Autowired
    private FailTypeCheck failTypeCheck;

    @Override
    public void deal(RawdataInitBean rawdataInitBean, BinWaferInforBean binWaferInforBean) {
        try {
            LinkedHashMap<String, String> properties = rawdataInitBean.getProperties();
            ArrayList<String> passBins = new ArrayList<>();
            for (String bin : properties.get("Pass Bins").split(",")) {
                passBins.add(bin);
            }
            Integer TestDieRow = Integer.valueOf(properties.get("TestDieMaxY")) - Integer.valueOf(properties.get("TestDieMinY")) + 1;
            Integer TestDieCol = Integer.valueOf(properties.get("TestDieMaxX")) - Integer.valueOf(properties.get("TestDieMinX")) + 1;
            String[][] testDie = new String[TestDieRow][TestDieCol];
            Integer up = Integer.valueOf(properties.get("TestDieMinY"));
            Integer left = Integer.valueOf(properties.get("TestDieMinX"));
            HashMap<String, String> testDieMap = rawdataInitBean.getTestDieMap();
            Set<String> coordinateSet = testDieMap.keySet();
            for (String coordinate : coordinateSet) {
                int x = Integer.valueOf(coordinate.substring(0, 4).trim()) - left;
                int y = Integer.valueOf(coordinate.substring(4, 8).trim()) - up;
                String value = testDieMap.get(coordinate).substring(4, 8).trim();
                testDie[y][x] = value;
            }
            ArrayList<Set<String>> failDieChains = adjacentFailDieCheckTool.check(testDie, TestDieRow, TestDieCol, passBins);
            HashMap<String, Boolean> checkResult = new HashMap<>();
            for (Set<String> node : failDieChains) {
                if (checkResult.values().size() > 0 && !checkResult.values().contains(false)) {
                    break;
                }
                if (node.size() >= 5) {
                    if (node.size() >= 5000) {
                        checkResult.put("10 interflow Fail", true);
                    }
                    if ((checkResult.containsKey("8 Neighborhood Fail") && !checkResult.get("8 Neighborhood Fail")) || !checkResult.containsKey("8 Neighborhood Fail")) {
                        boolean eightAdjacentFailDieCheckFlag = failTypeCheck.eightAdjacentFailDieCheck(node);
                        checkResult.put("8 Neighborhood Fail", eightAdjacentFailDieCheckFlag);
                    }
                    if ((checkResult.containsKey("4 Neighborhood Fail") && !checkResult.get("4 Neighborhood Fail")) || !checkResult.containsKey("4 Neighborhood Fail")) {
                        boolean fourAdjacentFailDieCheckFlag = failTypeCheck.fourAdjacentFailDieCheck(node);
                        checkResult.put("4 Neighborhood Fail", fourAdjacentFailDieCheckFlag);
                    }
                    if ((checkResult.containsKey("Y_Stretch Fail") && !checkResult.get("Y_Stretch Fail")) || !checkResult.containsKey("Y_Stretch Fail")) {
                        boolean xDirectionAdjacentFailDieCheckFlag = failTypeCheck.xDirectionAdjacentFailDieCheck(node);
                        checkResult.put("Y_Stretch Fail", xDirectionAdjacentFailDieCheckFlag);
                    }
                    if ((checkResult.containsKey("X_Stretch Fail") && !checkResult.get("X_Stretch Fail")) || !checkResult.containsKey("X_Stretch Fail")) {
                        boolean yDirectionAdjacentFailDieCheckFlag = failTypeCheck.yDirectionAdjacentFailDieCheck(node);
                        checkResult.put("X_Stretch Fail", yDirectionAdjacentFailDieCheckFlag);
                    }
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String description : checkResult.keySet()) {
                if (checkResult.get(description)) {
                    stringBuilder.append(description + ";");
                }
            }
            if (stringBuilder.toString().length() == 0) {
                stringBuilder.append("normal");
            }
            binWaferInforBean.setCheckResult(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
