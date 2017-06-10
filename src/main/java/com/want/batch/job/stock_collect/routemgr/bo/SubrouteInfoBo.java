package com.want.batch.job.stock_collect.routemgr.bo;

import java.io.Serializable;
import java.sql.Timestamp;
//import com.want.batch.job.stock_collect.storemgr.bo.*;

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
 */

@SuppressWarnings("serial")
public class SubrouteInfoBo implements Serializable {

    private int SID;
    private int ROUTE_SID;
    private String SUBROUTE_NAME;
    private String SUBROUTE_DESC;
    private String YEARMONTH;
    private Timestamp VISIT_DATE;
    private String IS_CURRENT;
    private String CREATOR;
    private Timestamp CREATE_DATE;
    private String UPDATOR;
    private Timestamp UPDATE_DATE;
    private Integer STORE_COUNT;
//    private StoreInfoBO STORE_INFO;
    public SubrouteInfoBo() {
        SUBROUTE_NAME="";
        STORE_COUNT= Integer.valueOf(0);
    }
    /**
     *  初始�\uFFFD
     */
    public void init() {


    }


    public void setYEARMONTH(String YEARMONTH) {
        this.YEARMONTH = YEARMONTH;
    }

    public String getYEARMONTH() {
        return YEARMONTH;
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

    public void setSUBROUTE_NAME(String SUBROUTE_NAME) {
        this.SUBROUTE_NAME = SUBROUTE_NAME;
    }

    public String getSUBROUTE_NAME() {
        return SUBROUTE_NAME;
    }

    public void setSUBROUTE_DESC(String SUBROUTE_DESC) {
        this.SUBROUTE_DESC = SUBROUTE_DESC;
    }

    public String getSUBROUTE_DESC() {
        return SUBROUTE_DESC;
    }

    public void setSTORE_COUNT(Integer STORE_COUNT) {
        this.STORE_COUNT = STORE_COUNT;
    }

    public Integer getSTORE_COUNT() {
        return STORE_COUNT;
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

//    public void setSTORE_INFO(StoreInfoBO STORE_INFO) {
//        this.STORE_INFO = STORE_INFO;
//    }
//
//    public StoreInfoBO getSTORE_INFO() {
//        return STORE_INFO;
//    }


}
