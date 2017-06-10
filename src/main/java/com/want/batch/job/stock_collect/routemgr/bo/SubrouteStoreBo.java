package com.want.batch.job.stock_collect.routemgr.bo;

import java.io.Serializable;
import java.sql.*;

@SuppressWarnings("serial")
public class SubrouteStoreBo implements Serializable {
    public SubrouteStoreBo() {
    }

    private int SUBROUTE_SID;
    private int STORE_SID;
    private int VISIT_ORDER;
    private String STORE_ID;
    private String STORE_NAME;
    private String MUST_VISIT;
    private String YEARMONTH;
    private String IS_CURRENT;
    private String CREATOR;
    private Timestamp CREATE_DATE;
    private String UPDATOR;
    private Timestamp UPDATE_DATE;

    public void setVISIT_ORDER(int VISIT_ORDER) {
        this.VISIT_ORDER = VISIT_ORDER;
    }

    public int getVISIT_ORDER() {
        return VISIT_ORDER;
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

    public void setSUBROUTE_SID(int SUBROUTE_SID) {
        this.SUBROUTE_SID = SUBROUTE_SID;
    }

    public int getSUBROUTE_SID() {
        return SUBROUTE_SID;
    }

    public void setSTORE_SID(int STORE_SID) {
        this.STORE_SID = STORE_SID;
    }

    public int getSTORE_SID() {
        return STORE_SID;
    }

    public void setSTORE_NAME(String STORE_NAME) {
        this.STORE_NAME = STORE_NAME;
    }

    public String getSTORE_NAME() {
        return STORE_NAME;
    }

    public void setSTORE_ID(String STORE_ID) {
        this.STORE_ID = STORE_ID;
    }

    public String getSTORE_ID() {
        return STORE_ID;
    }

    public void setMUST_VISIT(String MUST_VISIT) {
        this.MUST_VISIT = MUST_VISIT;
    }

    public String getMUST_VISIT() {
        return MUST_VISIT;
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

    public void setYEARMONTH(String YEARMONTH) {
        this.YEARMONTH = YEARMONTH;
    }

    public String getYEARMONTH() {
        return YEARMONTH;
    }

    public void setIS_CURRENT(String IS_CURRENT) {
        this.IS_CURRENT = IS_CURRENT;
    }

    public String getIS_CURRENT() {
        return IS_CURRENT;
    }

}
