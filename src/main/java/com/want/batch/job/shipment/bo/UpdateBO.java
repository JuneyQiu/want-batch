package com.want.batch.job.shipment.bo;

import java.math.BigDecimal;

@SuppressWarnings("serial")
public class UpdateBO implements java.io.Serializable{
    private String TCOMPANY_SID;
    private String PRODUCT_SID;
    private BigDecimal QTY=new BigDecimal("0");
    public String getPRODUCT_SID() {
        return PRODUCT_SID;
    }

    public void setPRODUCT_SID(String PRODUCT_SID) {
        this.PRODUCT_SID = PRODUCT_SID;
    }

    public BigDecimal getQTY() {
        return QTY;
    }

    public void setQTY(BigDecimal QTY) {
        this.QTY = QTY;
    }

    public String getTCOMPANY_SID() {
        return TCOMPANY_SID;
    }

    public void setTCOMPANY_SID(String TCOMPANY_SID) {
        this.TCOMPANY_SID = TCOMPANY_SID;
    }
}
