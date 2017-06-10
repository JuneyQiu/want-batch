package com.want.batch.job.stock_collect.reportmgr.bo;

public class ReportQueryBO {
    private int COMPANY_SID;
    private int BRANCH_SID;
    private String CREATE_DATE_FROM;
    private String CREATE_DATE_TO;
    private String ORDER_DATE_FROM;
    private String ORDER_DATE_TO;
    private int PROJECTSID;
    public ReportQueryBO() {
    }

    public void setORDER_DATE_TO(String ORDER_DATE_TO) {
        this.ORDER_DATE_TO = ORDER_DATE_TO;
    }

    public String getORDER_DATE_TO() {
        return ORDER_DATE_TO;
    }

    public void setORDER_DATE_FROM(String ORDER_DATE_FROM) {
        this.ORDER_DATE_FROM = ORDER_DATE_FROM;
    }

    public String getORDER_DATE_FROM() {
        return ORDER_DATE_FROM;
    }

    public void setCREATE_DATE_TO(String CREATE_DATE_TO) {
        this.CREATE_DATE_TO = CREATE_DATE_TO;
    }

    public String getCREATE_DATE_TO() {
        return CREATE_DATE_TO;
    }

    public void setCREATE_DATE_FROM(String CREATE_DATE_FROM) {
        this.CREATE_DATE_FROM = CREATE_DATE_FROM;
    }

    public String getCREATE_DATE_FROM() {
        return CREATE_DATE_FROM;
    }

    public void setCOMPANY_SID(int COMPANY_SID) {
        this.COMPANY_SID = COMPANY_SID;
    }

    public int getCOMPANY_SID() {
        return COMPANY_SID;
    }

    public void setBRANCH_SID(int BRANCH_SID) {
        this.BRANCH_SID = BRANCH_SID;
    }

    public int getBRANCH_SID() {
        return BRANCH_SID;
    }

    public void setPROJECTSID(int PROJECTSID) {
        this.PROJECTSID = PROJECTSID;
    }

    public int getPROJECTSID() {
        return PROJECTSID;
    }
}
