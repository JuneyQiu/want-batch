package com.want.batch.job.stock_collect.forwardermgr.bo;

import java.sql.Timestamp;

public class ForwarderInfoBO {
    private int SID;
    private int BRANCH_SID;
    private String FORWARDER_ID;
    private String FORWARDER_NAME;
    private String OWNER="";
    private String ZIP1="";
    private String PHONE1="";
    private String ZIP2="";
    private String PHONE2="";
    private String MOBILE1="";
    private String MOBILE2="";
    private String EMAIL1="";
    private String EMAIL2="";
    private String ADDRESS="";
    private String NOTE="";
    private String STATUS;
    private String CREATOR;
    private Timestamp CREATE_DATE;
    private String UPDATOR;
    private Timestamp UPDATE_DATE;
    private int FORWARDER_TYPE_SID;
    private int PROJECT_SID;

    public ForwarderInfoBO() {
    }

    public void setBRANCH_SID(int BRANCH_SID) {
        this.BRANCH_SID = BRANCH_SID;
    }

    public int getBRANCH_SID() {
        return BRANCH_SID;
    }

    public void setSID(int SID) {
        this.SID = SID;
    }

    public int getSID() {
        return SID;
    }

    public void setZIP2(String ZIP2) {
        if(ZIP2!=null)this.ZIP2 = ZIP2;
    }

    public String getZIP2() {
        return ZIP2;
    }

    public void setZIP1(String ZIP1) {
        if(ZIP1!=null)this.ZIP1 = ZIP1;
    }

    public String getZIP1() {
        return ZIP1;
    }

    public void setUPDATOR(String UPDATOR) {
        this.UPDATOR = UPDATOR;
    }

    public String getUPDATOR() {
        return UPDATOR;
    }

    public void setUPDATE_DATE(Timestamp UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public Timestamp getUPDATE_DATE() {
        return UPDATE_DATE;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setPHONE2(String PHONE2) {
        if(PHONE2!=null)this.PHONE2 = PHONE2;
    }

    public String getPHONE2() {
        return PHONE2;
    }

    public void setPHONE1(String PHONE1) {
        if(PHONE1!=null)this.PHONE1 = PHONE1;
    }

    public String getPHONE1() {
        return PHONE1;
    }

    public void setOWNER(String OWNER) {
        if(OWNER!=null)this.OWNER = OWNER;
    }

    public String getOWNER() {
        return OWNER;
    }

    public void setNOTE(String NOTE) {
        if(NOTE!=null)this.NOTE = NOTE;
    }

    public String getNOTE() {
        return NOTE;
    }

    public void setMOBILE2(String MOBILE2) {
        if(MOBILE2!=null) this.MOBILE2 = MOBILE2;
    }

    public String getMOBILE2() {
        return MOBILE2;
    }

    public void setMOBILE1(String MOBILE1) {
        if(MOBILE1!=null)this.MOBILE1 = MOBILE1;
    }

    public String getMOBILE1() {
        return MOBILE1;
    }

    public void setFORWARDER_TYPE_SID(int FORWARDER_TYPE_SID) {
        this.FORWARDER_TYPE_SID = FORWARDER_TYPE_SID;
    }

    public int getFORWARDER_TYPE_SID() {
        return FORWARDER_TYPE_SID;
    }

    public void setFORWARDER_NAME(String FORWARDER_NAME) {
        this.FORWARDER_NAME = FORWARDER_NAME;
    }

    public String getFORWARDER_NAME() {
        return FORWARDER_NAME;
    }

    public void setFORWARDER_ID(String FORWARDER_ID) {
        this.FORWARDER_ID = FORWARDER_ID;
    }

    public String getFORWARDER_ID() {
        return FORWARDER_ID;
    }

    public void setEMAIL2(String EMAIL2) {
         if(EMAIL2!=null)this.EMAIL2 = EMAIL2;
    }

    public String getEMAIL2() {
        return EMAIL2;
    }

    public void setEMAIL1(String EMAIL1) {
         if(EMAIL1!=null)this.EMAIL1 = EMAIL1;
    }

    public String getEMAIL1() {
        return EMAIL1;
    }

    public void setCREATOR(String CREATOR) {
        this.CREATOR = CREATOR;
    }

    public String getCREATOR() {
        return CREATOR;
    }

    public void setCREATE_DATE(Timestamp CREATE_DATE) {
        this.CREATE_DATE = CREATE_DATE;
    }

    public Timestamp getCREATE_DATE() {
        return CREATE_DATE;
    }

    public void setADDRESS(String ADDRESS) {
        if(ADDRESS!=null)this.ADDRESS = ADDRESS;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setPROJECT_SID(int PROJECT_SID) {
        this.PROJECT_SID = PROJECT_SID;
    }

    public int getPROJECT_SID() {
        return PROJECT_SID;
    }
}
