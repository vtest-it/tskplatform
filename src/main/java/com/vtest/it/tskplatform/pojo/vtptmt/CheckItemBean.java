package com.vtest.it.tskplatform.pojo.vtptmt;

import java.io.Serializable;

public class CheckItemBean implements Serializable {
    private static final long serialVersionUID = 1l;
    private String property;
    private boolean checkIsNa;
    private String equalsItem;
    private String customCode;
    private int level;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public boolean isCheckIsNa() {
        return checkIsNa;
    }

    public void setCheckIsNa(boolean checkIsNa) {
        this.checkIsNa = checkIsNa;
    }

    public String getEqualsItem() {
        return equalsItem;
    }

    public void setEqualsItem(String equalsItem) {
        this.equalsItem = equalsItem;
    }

    public String getCustomCode() {
        return customCode;
    }

    public void setCustomCode(String customCode) {
        this.customCode = customCode;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
