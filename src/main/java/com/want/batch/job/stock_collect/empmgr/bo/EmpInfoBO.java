package com.want.batch.job.stock_collect.empmgr.bo;

import java.sql.Timestamp;

public class EmpInfoBO {
    public EmpInfoBO() {
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

    public void setSID(int SID) {
        this.SID = SID;
    }

    public int getSID() {
        return SID;
    }

    public void setMOBILE2(String MOBILE2) {
         if(MOBILE2!=null)this.MOBILE2 = MOBILE2;
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

    public void setMANAGER_SID(int MANAGER_SID) {
        this.MANAGER_SID = MANAGER_SID;
    }

    public int getMANAGER_SID() {
        return MANAGER_SID;
    }

    public void setEMP_TYPE_SID(int EMP_TYPE_SID) {
        this.EMP_TYPE_SID = EMP_TYPE_SID;
    }

    public int getEMP_TYPE_SID() {
        return EMP_TYPE_SID;
    }

    public void setEMP_SALE_TYPE_SID(int EMP_SALE_TYPE_SID) {
        this.EMP_SALE_TYPE_SID = EMP_SALE_TYPE_SID;
    }

    public int getEMP_SALE_TYPE_SID() {
        return EMP_SALE_TYPE_SID;
    }

    public void setEMP_NAME(String EMP_NAME) {
        this.EMP_NAME = EMP_NAME;
    }

    public String getEMP_NAME() {
        return EMP_NAME;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getEMP_ID() {
        return EMP_ID;
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

    public void setBRANCH_SID(int BRANCH_SID) {
        this.BRANCH_SID = BRANCH_SID;
    }

    public int getBRANCH_SID() {
        return BRANCH_SID;
    }

    public void setSALE_TYPE_NAME(String SALE_TYPE_NAME) {
        this.SALE_TYPE_NAME = SALE_TYPE_NAME;
    }

    public String getSALE_TYPE_NAME() {
        return SALE_TYPE_NAME;
    }

    public void setEMP_TYPE_NAME(String EMP_TYPE_NAME) {
        this.EMP_TYPE_NAME = EMP_TYPE_NAME;
    }

    public String getEMP_TYPE_NAME() {
        return EMP_TYPE_NAME;
    }

    public void setNOTE(String NOTE) {
        if(NOTE!=null)this.NOTE = NOTE;
    }

    public String getNOTE() {
        return NOTE;
    }

    public void setMANAGER_NAME(String MANAGER_NAME) {
        this.MANAGER_NAME = MANAGER_NAME;
    }

    public String getMANAGER_NAME() {
        return MANAGER_NAME;
    }

    public void setMANAGER_TYPE(String MANAGER_TYPE) {
        this.MANAGER_TYPE = MANAGER_TYPE;
    }

    public String getMANAGER_TYPE() {
        return MANAGER_TYPE;
    }

    public void setMANAGER_ID(String MANAGER_ID) {
        this.MANAGER_ID = MANAGER_ID;
    }

    public String getMANAGER_ID() {
        return MANAGER_ID;
    }

    private int SID;
    private String EMP_ID;
    private String EMP_NAME;
    private int EMP_TYPE_SID;
    private int MANAGER_SID;
    private int EMP_SALE_TYPE_SID;
    private String MOBILE1="";
    private String MOBILE2="";
    private String STATUS;
    private int BRANCH_SID;
    private String CREATOR;
    private Timestamp CREATE_DATE;
    private String UPDATOR;
    private Timestamp UPDATE_DATE;
    private String SALE_TYPE_NAME;
    private String EMP_TYPE_NAME;
    private String NOTE="";
    private String MANAGER_NAME;
    private String MANAGER_ID;
    private String MANAGER_TYPE;

}
