package com.want.batch.job.stock_collect.companymgr.bo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>Title: HWXZ</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Want Want Group</p>
 *
 * @author David
 * @version 1.0
 */
@SuppressWarnings("serial")
public class CompanyInfoBO implements Serializable {
    private int SID;
    private String COMPANY_ID = "";
    private String COMPANY_NAME = "";
    private int STATE_SID;
    private String STATUS;
    private String CREATOR = "";
    private Timestamp CREATE_DATE;
    private int REAL_STATE_SID;
    private String EMAIL;
    private String REMARK;
    private String UPDATOR="";
    private String PIN_YIN = "";
    // for jsp

    public CompanyInfoBO() {
    }

    public void setCREATE_DATE(Timestamp CREATE_DATE) {
        this.CREATE_DATE = CREATE_DATE;
    }

    public Timestamp getCREATE_DATE() {
        return CREATE_DATE;
    }

    public void setCREATOR(String CREATOR) {
        if (CREATOR != null) {
            this.CREATOR = CREATOR;
        }
    }

    public String getCREATOR() {
        return CREATOR;
    }

    public void setCOMPANY_ID(String COMPANY_ID) {
        this.COMPANY_ID = COMPANY_ID;
    }

    public String getCOMPANY_ID() {
        return COMPANY_ID;
    }

    public void setSTATE_SID(int STATE_SID) {
        this.STATE_SID = STATE_SID;
    }

    public int getSTATE_SID() {
        return STATE_SID;
    }

    public void setCOMPANY_NAME(String COMPANY_NAME) {
        this.COMPANY_NAME = COMPANY_NAME;
    }

    public String getCOMPANY_NAME() {
        return COMPANY_NAME;
    }

    public void setSID(int SID) {
        this.SID = SID;
    }

    public int getSID() {
        return SID;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setREAL_STATE_SID(int REAL_STATE_SID) {
        this.REAL_STATE_SID = REAL_STATE_SID;
    }

    public int getREAL_STATE_SID() {
        return REAL_STATE_SID;
    }

    public String getREMARK() {
        return REMARK;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public String getUPDATOR() {
        return UPDATOR;
    }

    public void setUPDATOR(String UPDATOR) {
        this.UPDATOR = UPDATOR;
    }

  public void setPIN_YIN(String PIN_YIN) {
    this.PIN_YIN = PIN_YIN;
  }

  public String getPIN_YIN() {
    return PIN_YIN;
  }

}
