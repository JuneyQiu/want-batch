package com.want.batch.job.stock_collect.routemgr.bo;

import java.io.Serializable;
import java.sql.*;

/**
 * <p>Title: HeWang2009</p>
 *
 * <p>Description: He Wang System</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Want Want Group</p>
 *
 * @author Zhang_Lei
 *
 * @version 1.0
 * 2009-02-17
 */

@SuppressWarnings("serial")
public class RouteFunctionBo implements Serializable {
    public RouteFunctionBo() {
    }

    private int SID;
    private int ROUTE_SID;
    private String ROUTE_NAME;
    private int STORE_SID;
    private String STORE_ID;
    private String STORE_NAME;
    private int SUBROUTE_SID;
    private String SUBROUTE_NAME;
    private String WEEKORDATE;
    private int VISIT_ORDER;
    private int WEEK;
    private int WEEK_DAY;
    private Timestamp VISIT_DATE;
    private String YEARMONTH;
    private String IS_CURRENT;
    private String CREATOR;
    private Timestamp CREATE_DATE;
    private String UPDATOR;
    private Timestamp UPDATE_DATE;

    public void setYEARMONTH(String YEARMONTH) {
        this.YEARMONTH = YEARMONTH;
    }

    public String getYEARMONTH() {
        return YEARMONTH;
    }

    public void setWEEK_DAY(int WEEK_DAY) {
        this.WEEK_DAY = WEEK_DAY;
    }

    public int getWEEK_DAY() {
        return WEEK_DAY;
    }

    public void setWEEKORDATE(String WEEKORDATE) {
        this.WEEKORDATE = WEEKORDATE;
    }

    public String getWEEKORDATE() {
        return WEEKORDATE;
    }

    public void setWEEK(int WEEK) {
        this.WEEK = WEEK;
    }

    public int getWEEK() {
        return WEEK;
    }

    public void setVISIT_DATE(Timestamp VISIT_DATE) {
        this.VISIT_DATE = VISIT_DATE;
    }

    public Timestamp getVISIT_DATE() {
        return VISIT_DATE;
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

    public void setSUBROUTE_NAME(String SUBROUTE_NAME) {
        this.SUBROUTE_NAME = SUBROUTE_NAME;
    }

    public String getSUBROUTE_NAME() {
        return SUBROUTE_NAME;
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

    public void setSID(int SID) {
        this.SID = SID;
    }

    public int getSID() {
        return SID;
    }

    public void setROUTE_SID(int ROUTE_SID) {
        this.ROUTE_SID = ROUTE_SID;
    }

    public int getROUTE_SID() {
        return ROUTE_SID;
    }

    public void setROUTE_NAME(String ROUTE_NAME) {
        this.ROUTE_NAME = ROUTE_NAME;
    }

    public String getROUTE_NAME() {
        return ROUTE_NAME;
    }

    public void setIS_CURRENT(String IS_CURRENT) {
        this.IS_CURRENT = IS_CURRENT;
    }

    public String getIS_CURRENT() {
        return IS_CURRENT;
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

    public void setVISIT_ORDER(int VISIT_ORDER) {
        this.VISIT_ORDER = VISIT_ORDER;
    }

    public int getVISIT_ORDER() {
        return VISIT_ORDER;
    }

    public void setSTORE_ID(String STORE_ID) {
        this.STORE_ID = STORE_ID;
    }

    public String getSTORE_ID() {
        return STORE_ID;
    }


}
