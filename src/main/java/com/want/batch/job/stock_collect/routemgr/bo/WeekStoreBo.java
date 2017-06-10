package com.want.batch.job.stock_collect.routemgr.bo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WeekStoreBo implements Serializable {
    public WeekStoreBo() {
    }

    private int STORE_SID;
    private int ROUTE_SID;
    private String STORE_ID;
    private String STORE_NAME;
    private String ROUTE_NAME;
    private boolean Week_One;
    private boolean Week_Two;
    private boolean Week_Three;
    private boolean Week_Four;
    private boolean Week_Five;
    private boolean Week_Six;
    private String YEARMONTH;
    private int WEEK_DAY;

    public void init() {
        this.setWeek_One(false);
        this.setWeek_Two(false);
        this.setWeek_Three(false);
        this.setWeek_Four(false);
        this.setWeek_Five(false);
        this.setWeek_Six(false);
    }

    public void setWEEK_DAY(int WEEK_DAY) {
        this.WEEK_DAY = WEEK_DAY;
    }

    public int getWEEK_DAY() {
        return WEEK_DAY;
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

    public void setWeek_Two(boolean Week_Two) {
        this.Week_Two = Week_Two;
    }

    public boolean isWeek_Two() {
        return Week_Two;
    }

    public void setWeek_Three(boolean Week_Three) {
        this.Week_Three = Week_Three;
    }

    public boolean isWeek_Three() {
        return Week_Three;
    }

    public void setWeek_Six(boolean Week_Six) {
        this.Week_Six = Week_Six;
    }

    public boolean isWeek_Six() {
        return Week_Six;
    }

    public void setWeek_One(boolean Week_One) {
        this.Week_One = Week_One;
    }

    public boolean isWeek_One() {
        return Week_One;
    }

    public void setWeek_Four(boolean Week_Four) {
        this.Week_Four = Week_Four;
    }

    public boolean isWeek_Four() {
        return Week_Four;
    }

    public void setWeek_Five(boolean Week_Five) {
        this.Week_Five = Week_Five;
    }

    public boolean isWeek_Five() {
        return Week_Five;
    }

    public void setYEARMONTH(String YEARMONTH) {
        this.YEARMONTH = YEARMONTH;
    }

    public String getYEARMONTH() {
        return YEARMONTH;
    }



}
