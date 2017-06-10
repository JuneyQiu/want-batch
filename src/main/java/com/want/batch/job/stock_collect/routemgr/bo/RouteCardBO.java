package com.want.batch.job.stock_collect.routemgr.bo;

import java.io.Serializable;
import java.sql.Timestamp;

@SuppressWarnings("serial")
public class RouteCardBO implements Serializable {
  public RouteCardBO() {
  }

   private int PORJECT_SID;
   private int COMPANY_SID;
   private String COMPANY_NAME;
   private int BRANCH_SID;
   private String BRANCH_NAME;
   private int ROUTE_SID;
   private int SUBROUTE_SID;
   private String ROUTE_NAME;
   private Timestamp VISIT_DATE;
   private int FORTH_SID;
   private String FORTH_LV_NAME;
   private int STORE_COUNT;
   private int TOTAL_COUNT;
   private String EMP_NAME;
   private String EMP_ID;
   private int STORE_COUNT_2;

  public void setVISIT_DATE(Timestamp VISIT_DATE) {
    this.VISIT_DATE = VISIT_DATE;
  }

  public Timestamp getVISIT_DATE() {
    return VISIT_DATE;
  }

  public void setSTORE_COUNT(int STORE_COUNT) {
    this.STORE_COUNT = STORE_COUNT;
  }

  public int getSTORE_COUNT() {
    return STORE_COUNT;
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

  public void setPORJECT_SID(int PORJECT_SID) {
    this.PORJECT_SID = PORJECT_SID;
  }

  public int getPORJECT_SID() {
    return PORJECT_SID;
  }

  public void setFORTH_LV_NAME(String FORTH_LV_NAME) {
    this.FORTH_LV_NAME = FORTH_LV_NAME;
  }

  public String getFORTH_LV_NAME() {
    return FORTH_LV_NAME;
  }

  public void setCOMPANY_SID(int COMPANY_SID) {
    this.COMPANY_SID = COMPANY_SID;
  }

  public int getCOMPANY_SID() {
    return COMPANY_SID;
  }

  public void setCOMPANY_NAME(String COMPANY_NAME) {
    this.COMPANY_NAME = COMPANY_NAME;
  }

  public String getCOMPANY_NAME() {
    return COMPANY_NAME;
  }

  public void setBRANCH_SID(int BRANCH_SID) {
    this.BRANCH_SID = BRANCH_SID;
  }

  public int getBRANCH_SID() {
    return BRANCH_SID;
  }

  public void setBRANCH_NAME(String BRANCH_NAME) {
    this.BRANCH_NAME = BRANCH_NAME;
  }

  public String getBRANCH_NAME() {
    return BRANCH_NAME;
  }

  public void setTOTAL_COUNT(int TOTAL_COUNT) {
    this.TOTAL_COUNT = TOTAL_COUNT;
  }

  public int getTOTAL_COUNT() {
    return TOTAL_COUNT;
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

  public void setSTORE_COUNT_2(int STORE_COUNT_2) {
    this.STORE_COUNT_2 = STORE_COUNT_2;
  }

  public int getSTORE_COUNT_2() {
    return STORE_COUNT_2;
  }

  public void setSUBROUTE_SID(int SUBROUTE_SID) {
    this.SUBROUTE_SID = SUBROUTE_SID;
  }

  public int getSUBROUTE_SID() {
    return SUBROUTE_SID;
  }

  public void setFORTH_SID(int FORTH_SID) {
    this.FORTH_SID = FORTH_SID;
  }

  public int getFORTH_SID() {
    return FORTH_SID;
  }

}
