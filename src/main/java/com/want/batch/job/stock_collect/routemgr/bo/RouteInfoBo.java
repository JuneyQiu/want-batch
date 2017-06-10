package com.want.batch.job.stock_collect.routemgr.bo;

import java.io.Serializable;
import java.sql.Timestamp;

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
 * 2009-02-09
 */

@SuppressWarnings("serial")
public class RouteInfoBo implements Serializable {

    private int SID;
    private int PROJECT_SID;
    private int BRANCH_SID;
    private int EMP_SID;
    private int FORWARDER_SID;
    private String PROJECT_NAME;
    private String ROUTE_NAME;
    private String ROUTE_DESC;
    private String YEARMONTH;
    private String IS_CURRENT;
    private Timestamp START_DATE;
    private String EMAIL2;
    private String IS_SUB_BRANCH;
    private String CREATOR;
    private String CreatorName;
    private Timestamp CREATE_DATE;
    private String UPDATOR;
    private Timestamp UPDATE_DATE;
    private Integer SUBROUTE_COUNT;
    private String FORWARDER_NAME;
    private String EMP_NAME;
    private String EMP_ID;
    private String FORWARDER_ID;

    public RouteInfoBo() {
    }

    public void setYEARMONTH(String YEARMONTH) {
        this.YEARMONTH = YEARMONTH;
    }

    public String getYEARMONTH() {
        return YEARMONTH;
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

    public void setSTART_DATE(Timestamp START_DATE) {
        this.START_DATE = START_DATE;
    }

    public Timestamp getSTART_DATE() {
        return START_DATE;
    }

    public void setSID(int SID) {
        this.SID = SID;
    }

    public int getSID() {
        return SID;
    }

    public void setROUTE_NAME(String ROUTE_NAME) {
        this.ROUTE_NAME = ROUTE_NAME;
    }

    public String getROUTE_NAME() {
        return ROUTE_NAME;
    }

    public void setROUTE_DESC(String ROUTE_DESC) {
        this.ROUTE_DESC = ROUTE_DESC;
    }

    public String getROUTE_DESC() {
        return ROUTE_DESC;
    }

    public void setPROJECT_SID(int PROJECT_SID) {
        this.PROJECT_SID = PROJECT_SID;
    }

    public int getPROJECT_SID() {
        return PROJECT_SID;
    }

    public void setIS_SUB_BRANCH(String IS_SUB_BRANCH) {
        this.IS_SUB_BRANCH = IS_SUB_BRANCH;
    }

    public String getIS_SUB_BRANCH() {
        return IS_SUB_BRANCH;
    }

    public void setIS_CURRENT(String IS_CURRENT) {
        this.IS_CURRENT = IS_CURRENT;
    }

    public String getIS_CURRENT() {
        return IS_CURRENT;
    }

    public void setFORWARDER_SID(int FORWARDER_SID) {
        this.FORWARDER_SID = FORWARDER_SID;
    }

    public int getFORWARDER_SID() {
        return FORWARDER_SID;
    }

    public void setEMP_SID(int EMP_SID) {
        this.EMP_SID = EMP_SID;
    }

    public int getEMP_SID() {
        return EMP_SID;
    }

    public void setEMAIL2(String EMAIL2) {
        this.EMAIL2 = EMAIL2;
    }

    public String getEMAIL2() {
        return EMAIL2;
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

    public void setSUBROUTE_COUNT(Integer SUBROUTE_COUNT) {
        this.SUBROUTE_COUNT = SUBROUTE_COUNT;
    }

    public Integer getSUBROUTE_COUNT() {
        return SUBROUTE_COUNT;
    }

    public void setFORWARDER_NAME(String FORWARDER_NAME) {
        this.FORWARDER_NAME = FORWARDER_NAME;
    }

    public String getFORWARDER_NAME() {
        return FORWARDER_NAME;
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

    public void setFORWARDER_ID(String FORWARDER_ID) {
        this.FORWARDER_ID = FORWARDER_ID;
    }

    public String getFORWARDER_ID() {
        return FORWARDER_ID;
    }

    public void setCreatorName(String CreatorName) {
        this.CreatorName = CreatorName;
    }

    public String getCreatorName() {
        return CreatorName;
    }

    public void setPROJECT_NAME(String PROJECT_NAME) {
        this.PROJECT_NAME = PROJECT_NAME;
    }

    public String getPROJECT_NAME() {
        return PROJECT_NAME;
    }


}
