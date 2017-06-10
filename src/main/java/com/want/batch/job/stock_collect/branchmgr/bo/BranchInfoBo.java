package com.want.batch.job.stock_collect.branchmgr.bo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>Title: HeWang2009</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Want Want Group</p>
 */
@SuppressWarnings("serial")
public class BranchInfoBo  implements Serializable{
    private int SID;
    private String EMAIL1 = "";
    private String EMAIL2 = "";
    private String BRANCH_ID;
    private String BRANCH_NAME;
    private int COMPANY_SID=0;
    private int STATUS;
    private int REAL_STATE_SID=0;
    private String IS_SUB_BRANCH;
    private int PARENT_BRANCH_SID=0;
    private String CREATOR;
    private Timestamp CREATE_DATE;
    private String UPDATOR;
    private Timestamp UPDATE_DATE;
    private String BRANCH_ADDRESS;
    private String BRANCH_DESC;
    public BranchInfoBo() {
    }

    public void setEMAIL2(String EMAIL2) {
        if(EMAIL2!=null) this.EMAIL2 = EMAIL2;
    }

    public String getEMAIL2() {
        return EMAIL2;
    }

    public void setEMAIL1(String EMAIL1) {
        if(EMAIL1!=null) this.EMAIL1 = EMAIL1;
    }

    public String getEMAIL1() {
        return EMAIL1;
    }

    public void setSID(int SID) {
        this.SID = SID;
    }

    public int getSID() {
        return SID;
    }

    public void setBRANCH_ID(String BRANCH_ID) {
        this.BRANCH_ID = BRANCH_ID;
    }

    public String getBRANCH_ID() {
        return BRANCH_ID;
    }

    public void setBRANCH_NAME(String BRANCH_NAME) {
        this.BRANCH_NAME = BRANCH_NAME;
    }

    public String getBRANCH_NAME() {
        return BRANCH_NAME;
    }

    public void setCOMPANY_SID(int COMPANY_SID) {
        this.COMPANY_SID = COMPANY_SID;
    }

    public int getCOMPANY_SID() {
        return COMPANY_SID;
    }

    public void setSTATUS(int STATUS) {
        this.STATUS = STATUS;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public void setREAL_STATE_SID(int REAL_STATE_SID) {
        this.REAL_STATE_SID = REAL_STATE_SID;
    }

    public int getREAL_STATE_SID() {
        return REAL_STATE_SID;
    }

    public void setIS_SUB_BRANCH(String IS_SUB_BRANCH) {
        this.IS_SUB_BRANCH = IS_SUB_BRANCH;
    }

    public String getIS_SUB_BRANCH() {
        return IS_SUB_BRANCH;
    }

    public void setPARENT_BRANCH_SID(int PARENT_BRANCH_SID) {
        this.PARENT_BRANCH_SID = PARENT_BRANCH_SID;
    }

    public int getPARENT_BRANCH_SID() {
        return PARENT_BRANCH_SID;
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

    public String getBRANCH_ADDRESS() {
        return BRANCH_ADDRESS;
    }

    public void setBRANCH_ADDRESS(String BRANCH_ADDRESS) {
        this.BRANCH_ADDRESS = BRANCH_ADDRESS;
    }

    public String getBRANCH_DESC() {
        return BRANCH_DESC;
    }

    public void setBRANCH_DESC(String BRANCH_DESC) {
        this.BRANCH_DESC = BRANCH_DESC;
    }
}
