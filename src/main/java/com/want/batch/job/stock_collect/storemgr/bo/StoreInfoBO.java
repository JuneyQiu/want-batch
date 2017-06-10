package com.want.batch.job.stock_collect.storemgr.bo;

import java.sql.Timestamp;

public class StoreInfoBO {
    private int SID;
    private String STORE_NAME="";
    private String STORE_ID="";
    private String STORE_AREA="";
    private String ADD1="";
    private int ADD1_TYPE_SID;
    private String ADD2="";
    private int ADD2_TYPE_SID;
    private String ADD3="";
    private int ADD3_TYPE_SID;
    private String ADD4="";
    private int ADD4_TYPE_SID;
    private String ADD5="";
    private int ADD5_TYPE_SID;
    private String ADD6="";
    private String STORE_OWNER="";
    private int OWNER_GENDER;
    private String STORE_AREA_CODE="";
    private String STORE_ZIP1="";
    private String STORE_PHONE1="";
    private String STORE_ZIP2="";
    private String STORE_PHONE2="";
    private String MOBILE1="";
    private String MOBILE2="";
    private int REFREGIATOR;
    private String STATUS;
    private int FILE1;
    private int FILE2;
    private int FILE3;
    private String CREATOR="";
    private Timestamp CREATE_DATE;
    private String UPDATOR;
    private Timestamp UPDATE_DATE;
    private String PROJECT_SIDS;
    private String PAPER_SID;
    private String NOTE="";
    private String ACREAGE;
    private int SALE_METHOD;
    private int PAYMENT_TERM;
    private String IDENTITY_ID="";
    private String POST_CODE="";
    private String STORE_TYPE;
    private String [] STORE_CHANNEL;
    private int STORE_TYPE_SID;
    private String STORE_DESC="";
    private int FORTH_SID;
    private String S_CREATE_DATE="";
    private String STORE_TYPE_ID;
    private String STORE_SUBTYPE;
    private String STORE_TYPE_NAME;
    
    private DirectBussinessBO DirectBo;
    public StoreInfoBO() {

   }

    public void setPROJECT_SIDS(String PROJECT_SIDS) {
        if(PROJECT_SIDS != null) this.PROJECT_SIDS = PROJECT_SIDS;
    }

    public String getPROJECT_SIDS() {
        return PROJECT_SIDS;
    }

    public void setUPDATOR(String UPDATOR) {
        if(UPDATOR != null) this.UPDATOR = UPDATOR;
    }

    public String getUPDATOR() {
        return UPDATOR;
    }

    public void setCREATE_DATE(Timestamp CREATE_DATE) {
        if(CREATE_DATE != null) this.CREATE_DATE = CREATE_DATE;
    }

    public Timestamp getCREATE_DATE() {
        return CREATE_DATE;
    }

    public void setCREATOR(String CREATOR) {
       if(CREATOR != null)  this.CREATOR = CREATOR;
    }

    public String getCREATOR() {
        return CREATOR;
    }

    public void setFILE3(int FILE3) {
         this.FILE3 = FILE3;
    }

    public int getFILE3() {
        return FILE3;
    }

    public void setFILE2(int FILE2) {
        this.FILE2 = FILE2;
    }

    public int getFILE2() {
        return FILE2;
    }

    public void setSTATUS(String STATUS) {
        if(STATUS != null) this.STATUS = STATUS;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setREFREGIATOR(int REFREGIATOR) {
       this.REFREGIATOR = REFREGIATOR;
    }

    public int getREFREGIATOR() {
        return REFREGIATOR;
    }

    public void setSTORE_AREA_CODE(String STORE_AREA_CODE) {
       if(STORE_AREA_CODE != null)  this.STORE_AREA_CODE = STORE_AREA_CODE;
    }

    public String getSTORE_AREA_CODE() {
        return STORE_AREA_CODE;
    }

    public void setOWNER_GENDER(int OWNER_GENDER) {
       this.OWNER_GENDER = OWNER_GENDER;
    }

    public int getOWNER_GENDER() {
        return OWNER_GENDER;
    }

    public void setSTORE_OWNER(String STORE_OWNER) {
       if(STORE_OWNER != null)  this.STORE_OWNER = STORE_OWNER;
    }

    public String getSTORE_OWNER() {
        return STORE_OWNER;
    }

    public void setADD6(String ADD6) {
       if(ADD6 != null)  this.ADD6 = ADD6;
    }

    public String getADD6() {
        return ADD6;
    }

    public void setADD4(String ADD4) {
       if(ADD4 != null)  this.ADD4 = ADD4;
    }

    public String getADD4() {
        return ADD4;
    }

    public void setADD3(String ADD3) {
       if(ADD3 != null)  this.ADD3 = ADD3;
    }

    public String getADD3() {
        return ADD3;
    }

    public void setSTORE_AREA(String STORE_AREA) {
       if(STORE_AREA != null)  this.STORE_AREA = STORE_AREA;
    }

    public String getSTORE_AREA() {
        return STORE_AREA;
    }

    public void setSTORE_ID(String STORE_ID) {
       if(STORE_ID != null)  this.STORE_ID = STORE_ID;
    }

    public String getSTORE_ID() {
        return STORE_ID;
    }

    public void setSTORE_NAME(String STORE_NAME) {
       if(STORE_NAME != null)  this.STORE_NAME = STORE_NAME;
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

    public void setUPDATE_DATE(Timestamp UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public Timestamp getUPDATE_DATE() {
        return UPDATE_DATE;
    }

    public void setNOTE(String NOTE) {
       if(NOTE != null)  this.NOTE = NOTE;
    }

    public String getNOTE() {
        return NOTE;
    }

    public void setPAPER_SID(String PAPER_SID) {
       if(PAPER_SID != null)  this.PAPER_SID = PAPER_SID;
    }

    public String getPAPER_SID() {
        return PAPER_SID;
    }

    public void setFILE1(int FILE1) {
        this.FILE1 = FILE1;
    }

    public int getFILE1() {
        return FILE1;
    }

    public void setPAYMENT_TERM(int PAYMENT_TERM) {
        this.PAYMENT_TERM = PAYMENT_TERM;
    }

    public int getPAYMENT_TERM() {
        return PAYMENT_TERM;
    }

    public void setSALE_METHOD(int SALE_METHOD) {
        this.SALE_METHOD = SALE_METHOD;
    }

    public int getSALE_METHOD() {
        return SALE_METHOD;
    }

    public String getMOBILE1() {
        return MOBILE1;
    }

    public String getMOBILE2() {
        return MOBILE2;
    }

    public void setMOBILE2(String MOBILE2) {
        if(MOBILE2 != null)this.MOBILE2 = MOBILE2;
    }

    public void setMOBILE1(String MOBILE1) {
       if(MOBILE1 != null) this.MOBILE1 = MOBILE1;
    }

    public void setSTORE_TYPE(String STORE_TYPE) {
       if(STORE_TYPE != null) this.STORE_TYPE = STORE_TYPE;
    }

    public String getSTORE_TYPE() {
        return STORE_TYPE;
    }

    public String getADD1() {
        return ADD1;
    }


    public String getADD2() {
        return ADD2;
    }


    public void setADD1(String ADD1) {
        this.ADD1 = ADD1;
    }


    public void setADD2(String ADD2) {
        this.ADD2 = ADD2;
    }

    public String getADD5() {
        return ADD5;
    }



    public void setADD5(String ADD5) {
        this.ADD5 = ADD5;
    }


    public String getSTORE_ZIP1() {
        return STORE_ZIP1;
    }

    public String getSTORE_ZIP2() {
        return STORE_ZIP2;
    }

    public void setSTORE_ZIP1(String STORE_ZIP1) {
        if(STORE_ZIP1 != null)this.STORE_ZIP1 = STORE_ZIP1;
    }

    public void setSTORE_ZIP2(String STORE_ZIP2) {
        if(STORE_ZIP2 != null)this.STORE_ZIP2 = STORE_ZIP2;
    }

    public void setSTORE_PHONE2(String STORE_PHONE2) {
       if(STORE_PHONE2 != null) this.STORE_PHONE2 = STORE_PHONE2;
    }

    public void setSTORE_PHONE1(String STORE_PHONE1) {
        if(STORE_PHONE1 != null)this.STORE_PHONE1 = STORE_PHONE1;
    }

    public String getSTORE_PHONE1() {
        return STORE_PHONE1;
    }

    public String getSTORE_PHONE2() {
        return STORE_PHONE2;
    }

    public String getACREAGE() {
        return ACREAGE;
    }

    public void setACREAGE(String ACREAGE) {
        this.ACREAGE = ACREAGE;
    }

    public String getPOST_CODE() {
        return POST_CODE;
    }

    public void setPOST_CODE(String POST_CODE) {
        this.POST_CODE = POST_CODE;
    }

    public String getIDENTITY_ID() {
        return IDENTITY_ID;
    }

    public void setIDENTITY_ID(String IDENTITY_ID) {
        this.IDENTITY_ID = IDENTITY_ID;
    }

    public String[] getSTORE_CHANNEL() {
        return STORE_CHANNEL;
    }

    public void setSTORE_CHANNEL(String[] STORE_CHANNEL) {
        this.STORE_CHANNEL = STORE_CHANNEL;
    }

    public int getSTORE_TYPE_SID() {
        return STORE_TYPE_SID;
    }

    public void setSTORE_TYPE_SID(int STORE_TYPE_SID) {
        this.STORE_TYPE_SID = STORE_TYPE_SID;
    }

    public int getADD1_TYPE_SID() {
        return ADD1_TYPE_SID;
    }

    public int getADD2_TYPE_SID() {
        return ADD2_TYPE_SID;
    }

    public int getADD3_TYPE_SID() {
        return ADD3_TYPE_SID;
    }

    public int getADD4_TYPE_SID() {
        return ADD4_TYPE_SID;
    }

    public int getADD5_TYPE_SID() {
        return ADD5_TYPE_SID;
    }

    public void setADD5_TYPE_SID(int ADD5_TYPE_SID) {
        this.ADD5_TYPE_SID = ADD5_TYPE_SID;
    }

    public void setADD4_TYPE_SID(int ADD4_TYPE_SID) {
        this.ADD4_TYPE_SID = ADD4_TYPE_SID;
    }

    public void setADD3_TYPE_SID(int ADD3_TYPE_SID) {
        this.ADD3_TYPE_SID = ADD3_TYPE_SID;
    }

    public void setADD2_TYPE_SID(int ADD2_TYPE_SID) {
        this.ADD2_TYPE_SID = ADD2_TYPE_SID;
    }

    public void setADD1_TYPE_SID(int ADD1_TYPE_SID) {
        this.ADD1_TYPE_SID = ADD1_TYPE_SID;
    }

    public String getSTORE_DESC() {
        return STORE_DESC;
    }

    public void setSTORE_DESC(String STORE_DESC) {
        this.STORE_DESC = STORE_DESC;
    }

    public int getFORTH_SID() {
        return FORTH_SID;
    }

    public void setFORTH_SID(int FORTH_SID) {
        this.FORTH_SID = FORTH_SID;
    }

    public String getS_CREATE_DATE() {
        return S_CREATE_DATE;
    }

    public void setS_CREATE_DATE(String S_CREATE_DATE) {
       if(S_CREATE_DATE != null) this.S_CREATE_DATE = S_CREATE_DATE;
    }

    public String getSTORE_TYPE_ID() {
        return STORE_TYPE_ID;
    }

    public void setSTORE_TYPE_ID(String STORE_TYPE_ID) {
        this.STORE_TYPE_ID = STORE_TYPE_ID;
    }

    public String getSTORE_SUBTYPE() {
        return STORE_SUBTYPE;
    }

    public String getSTORE_TYPE_NAME() {
        return STORE_TYPE_NAME;
    }

    public void setSTORE_TYPE_NAME(String STORE_TYPE_NAME) {
        this.STORE_TYPE_NAME = STORE_TYPE_NAME;
    }

    public void setSTORE_SUBTYPE(String STORE_SUBTYPE) {
        this.STORE_SUBTYPE = STORE_SUBTYPE;
    }

	public DirectBussinessBO getDirectBo() {
		return DirectBo;
	}

	public void setDirectBo(DirectBussinessBO directBo) {
		DirectBo = directBo;
	}

    
}
