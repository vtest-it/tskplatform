package com.vtest.it.tskplatform.pojo.mes;

import java.io.Serializable;

public class SlotAndSequenceConfigBean implements Serializable {
    public static final long serialVersionUID=1l;
    private String readType;
    private String sequence;
    private String gpibBin;
    public String getReadType() {
        return readType;
    }

    public void setReadType(String readType) {
        this.readType = readType;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getGpibBin() {
        return gpibBin;
    }

    public void setGpibBin(String gpibBin) {
        this.gpibBin = gpibBin;
    }
}
