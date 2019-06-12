package com.vtest.it.tskplatform.service.tools.impl.rawdataCheck;

import com.vtest.it.tskplatform.dao.vtptmt.VtptmtDao;
import com.vtest.it.tskplatform.pojo.vtptmt.CheckItemBean;
import com.vtest.it.tskplatform.pojo.vtptmt.DataParseIssueBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@Service
public class RawDataCheck {
    @Autowired
    private VtptmtDao vtptmtDao;
    @Autowired
    private GetIssueBean getIssueBean;

    public boolean check(File rawdata, ArrayList<DataParseIssueBean> issueBeans) {
        HashMap<String, String> waferInfor = new HashMap<>();
        try {
            ArrayList<CheckItemBean> checkItems = vtptmtDao.getCheckItemList();
            HashMap<Integer, Integer> actualSummaryMap = new HashMap<>();
            HashMap<Integer, Integer> theroySummaryMap = new HashMap<>();
            ArrayList<String> markAndSkipDieMap = new ArrayList<>();
            ArrayList<String> testDiesArray = new ArrayList<>();
            HashMap<String, String> properties = new HashMap<>();
            boolean testDieStartFlag = false;
            boolean markAndSkipFlag = false;
            boolean startFlag = true;
            boolean binSummaryFlag = false;
            FileReader in = new FileReader(rawdata);
            BufferedReader reader = new BufferedReader(in);
            String content;
            while ((content = reader.readLine()) != null) {
                if (content.contains("Bin Summary")) {
                    binSummaryFlag = true;
                    continue;
                }
                if (content.contains("SkipAndMarkStart")) {
                    markAndSkipFlag = true;
                    continue;
                }
                if (content.contains("SkipAndMarkEnd")) {
                    markAndSkipFlag = false;
                    continue;
                }
                if (content.contains("DataEnd")) {
                    testDieStartFlag = false;
                    continue;
                }
                if (content.contains("RawData")) {
                    startFlag = false;
                    testDieStartFlag = true;
                    continue;
                }
                if (startFlag) {
                    String key = content.substring(0, content.indexOf(":")).trim();
                    String value = content.substring(content.indexOf(":") + 1).trim();
                    properties.put(key, value);
                }
                if (testDieStartFlag) {
                    testDiesArray.add(content);
                }
                if (markAndSkipFlag) {
                    markAndSkipDieMap.add(content);
                }
                if (binSummaryFlag) {
                    if (content.contains("Bin")) {
                        String[] infor = content.split(":");
                        Integer bin = Integer.valueOf(infor[0].substring(3).trim());
                        Integer value = Integer.valueOf(infor[1]);
                        theroySummaryMap.put(bin, value);
                    }
                }
            }
            reader.close();

            waferInfor.put("customCode", properties.get("Customer Code"));
            waferInfor.put("device", properties.get("Device Name"));
            waferInfor.put("cpStep", properties.get("CP Process"));
            waferInfor.put("waferNo", properties.get("Wafer ID"));
            waferInfor.put("lot", properties.get("Lot ID"));
            waferInfor.put("resource", properties.get("DataBase"));

            propertiesCheck(properties, checkItems, issueBeans, waferInfor);
            markAndSkipDieCheck(markAndSkipDieMap, issueBeans, waferInfor);
            testDieCheck(testDiesArray, actualSummaryMap, issueBeans, waferInfor);
            BinSummaryDieCheck(theroySummaryMap, actualSummaryMap, issueBeans, waferInfor);
        } catch (IOException e) {
            DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBean(waferInfor, 99, "generate again!");
            issueBeans.add(dataParseIssueBean);
        }
        if (issueBeans.size() > 0) {
            for (DataParseIssueBean issueBean : issueBeans) {
                if (issueBean.getIssuLevel() == 99) {
                    return false;
                }
            }
        }
        return true;
    }

    private void propertiesCheck(HashMap<String, String> properties, ArrayList<CheckItemBean> checkItems, ArrayList<DataParseIssueBean> issueBeans, HashMap<String, String> waferInfor) {
        for (CheckItemBean itemBean : checkItems) {
            if (itemBean.getCustomCode().equals("ALL") || itemBean.getCustomCode().equals(waferInfor.get("customCode"))) {
                String dbPrperty = itemBean.getProperty();
                boolean checkNa = itemBean.isCheckIsNa();
                String equalsItem = itemBean.getEqualsItem();
                int checkLevel = itemBean.getLevel();
                if (checkNa) {
                    if (properties.get(dbPrperty).equals("NA")) {
                        DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBean(waferInfor, 2, "miss property : " + dbPrperty);
                        issueBeans.add(dataParseIssueBean);
                    }
                }
                if (!equalsItem.equals("na")) {
                    if (equalsItem.contains("&")) {
                        String[] checkItemsArr = equalsItem.split("&");
                        Integer sourcePropertie = Integer.valueOf(properties.get(dbPrperty));
                        Integer sum = 0;
                        for (int i = 0; i < checkItemsArr.length; i++) {
                            sum += Integer.valueOf(properties.get(checkItemsArr[i]));
                        }
                        if (sum - sourcePropertie != 0) {
                            DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBean(waferInfor, 5, "the sum of " + equalsItem + " is not equals " + dbPrperty);
                            issueBeans.add(dataParseIssueBean);
                        }
                    } else {
                        if (!properties.get(dbPrperty).equals(properties.get(equalsItem))) {
                            DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBean(waferInfor, 5, "it's different from : " + dbPrperty + " and " + equalsItem);
                            issueBeans.add(dataParseIssueBean);
                        }
                    }
                }
            }
        }
    }

    private void testDieCheck(ArrayList<String> testDiesArray, HashMap<Integer, Integer> actualSummaryMap, ArrayList<DataParseIssueBean> issueBeans, HashMap<String, String> waferInfor) {
        for (String content : testDiesArray) {
            if (content.length() > 20) {
                DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBean(waferInfor, 5, content + " die's bin or coordinate  (mark or skip) more than limit,please check");
                issueBeans.add(dataParseIssueBean);
            } else if (content.length() < 20) {
                DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBean(waferInfor, 99, "generate again!");
                issueBeans.add(dataParseIssueBean);
                return;
            }
            Integer value = Integer.valueOf(content.substring(12, 16).trim());
            if (actualSummaryMap.containsKey(value)) {
                actualSummaryMap.put(value, actualSummaryMap.get(value) + 1);
            } else {
                actualSummaryMap.put(value, 1);
            }
        }
    }

    private void markAndSkipDieCheck(ArrayList<String> markAndSkipDieMap, ArrayList<DataParseIssueBean> issueBeans, HashMap<String, String> waferInfor) {
        for (String content : markAndSkipDieMap) {
            if (content.length() > 20) {
                DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBean(waferInfor, 5, content + " die's bin or coordinate  (mark or skip) more than limit,please check");
                issueBeans.add(dataParseIssueBean);
            } else if (content.length() < 20) {
                DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBean(waferInfor, 99, "generate again!");
                issueBeans.add(dataParseIssueBean);
                return;
            }
        }
    }

    private void BinSummaryDieCheck(HashMap<Integer, Integer> theroySummaryMap, HashMap<Integer, Integer> actualSummaryMap, ArrayList<DataParseIssueBean> issueBeans, HashMap<String, String> waferInfor) {
        if (theroySummaryMap.size() != actualSummaryMap.size()) {
            DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBean(waferInfor, 99, "generate again!");
            issueBeans.add(dataParseIssueBean);
            return;
        } else {
            Set<Integer> theroyBinSet = theroySummaryMap.keySet();
            for (Integer bin : theroyBinSet) {
                if (!theroySummaryMap.containsKey(bin)) {
                    DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBean(waferInfor, 99, "generate again!");
                    issueBeans.add(dataParseIssueBean);
                    return;
                } else {
                    Integer diff = actualSummaryMap.get(bin) - theroySummaryMap.get(bin);
                    if (diff != 0) {
                        DataParseIssueBean dataParseIssueBean = getIssueBean.getDataBean(waferInfor, 99, "generate again!");
                        issueBeans.add(dataParseIssueBean);
                        return;
                    }
                }
            }
        }
    }
}
