package com.vtest.it.tskplatform.pojo.mes;

import java.io.Serializable;

public class CustomerCodeAndDeviceBean implements Serializable {
    public static final long serialVersionUID=1l;
    private String customerCode;
    private String device;
    private String lot;

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }
}
