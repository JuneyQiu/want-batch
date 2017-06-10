package com.want.batch.job.archive.store_data_transmit.pojo;

import java.util.Date;

//对应表customer_storage_info_tbl
public class CustStorageInfoTbl {
	private long SID=-99999999L;
	private String CUSTOMER_ID="";//客户编号
	private long PROJECT_SID=-9999999L;//渠道编号
	private String YEARMONTH="";//年月
	private String DAY="";//日
	private String PROD_ID="";//货料代号
	private String PROD_NAME="";//品项名称
	private String PROD_SPEC="";//品项规格
	private double PROD_PRICE=0d;//单价
	private int QTY_1=0;//库存量1
	private int QTY_2=0;//库存量2
	private int QTY_3=0;//库存量3
	private int QTY_4=0;//库存量4
	private int QTY_5=0;//库存量5
	private int QTY_6=0;//库存量6
	private int QTY_7=0;//库存量7
	private int QTY_8=0;//库存量8
	private int QTY_9=0;//库存量9
	private int QTY_10=0;//库存量10
	private int QTY_11=0;//库存量11
	private int QTY_12=0;//库存量12
	private int TOTAL_QTY=0;//库存总量
	private int LAST_TOTAL_QTY=0;//上次盘点库存量
	private int SEND_TOTAL_QTY=0;//上次盘点到本次的交货量
	private String CREATOR="";//建立人员
	private Date CREATE_DATE=null;//建立时间
	private String UPDATOR="";//更新人员
	private Date UPDATE_DATE=null;//更新时间
	private int UPDATE_COUNT=0;//更新次数
	private int S_QTY_1=0;
	private int S_QTY_2=0;
	private int S_QTY_3=0;
	private int S_QTY_4=0;
	private int S_QTY_5=0;
	private int S_QTY_6=0;
	private int S_QTY_7=0;
	private int S_QTY_8=0;
	private int S_QTY_9=0;
	private int S_QTY_10=0;
	private int S_QTY_11=0;
	private int S_QTY_12=0;
	private String STATUS="";//0:新录入 1:业代修改 2:客户修改并确认; 3.客户已确认但未修改
	private String CREDIT_ID="";
	private String ISMARK="";
	private Date CONFIRM_DATE=null;
	
	//以客户编号+年月+日+货料代号 作为判重的条件
	public boolean equals(Object obj){
		if((obj instanceof CustStorageInfoTbl)==false) return false;
		CustStorageInfoTbl csit=(CustStorageInfoTbl)obj;
		
		if(csit.getCUSTOMER_ID().equals(this.getCUSTOMER_ID())&&csit.getYEARMONTH().equals(this.getYEARMONTH())&&
				csit.getDAY().equals(this.getDAY())&&csit.getPROD_ID().equals(this.getPROD_ID())) return true;
		return false;
	}
	public int hashCode(){
		return this.getCUSTOMER_ID().hashCode()+this.getYEARMONTH().hashCode()+this.getDAY().hashCode()+this.getPROD_ID().hashCode();
	}
		
	public long getSID() {
		return SID;
	}
	public void setSID(long sID) {
		SID = sID;
	}
	public CustStorageInfoTbl initSID(long sID){
		setSID(sID);
		return this;
	}
	public String getCUSTOMER_ID() {
		return CUSTOMER_ID;
	}
	public void setCUSTOMER_ID(String cUSTOMERID) {
		CUSTOMER_ID = cUSTOMERID;
	}
	public CustStorageInfoTbl initCUSTOMER_ID(String cUSTOMERID){
		setCUSTOMER_ID(cUSTOMERID);
		return this;
	}
	public long getPROJECT_SID() {
		return PROJECT_SID;
	}
	public void setPROJECT_SID(long pROJECTSID) {
		PROJECT_SID = pROJECTSID;
	}
	public CustStorageInfoTbl initPROJECT_SID(long pROJECTSID){
		setPROJECT_SID(pROJECTSID);
		return this;
	}
	public String getYEARMONTH() {
		return YEARMONTH;
	}
	public void setYEARMONTH(String yEARMONTH) {
		YEARMONTH = yEARMONTH;
	}
	public CustStorageInfoTbl initYEARMONTH(String yEARMONTH){
		setYEARMONTH(yEARMONTH);
		return this;
	}
	public String getDAY() {
		return DAY;
	}
	public void setDAY(String dAY) {
		DAY = dAY;
	}
	public CustStorageInfoTbl initDAY(String dAY){
		setDAY(dAY);
		return this;
	}
	public String getPROD_ID() {
		return PROD_ID;
	}
	public void setPROD_ID(String pRODID) {
		PROD_ID = pRODID;
	}
	public CustStorageInfoTbl initPROD_ID(String pRODID){
		setPROD_ID(pRODID);
		return this;
	}
	public String getPROD_NAME() {
		return PROD_NAME;
	}
	public void setPROD_NAME(String pRODNAME) {
		PROD_NAME = pRODNAME;
	}
	public CustStorageInfoTbl initPROD_NAME(String pRODNAME){
		setPROD_NAME(pRODNAME);
		return this;
	}
	public String getPROD_SPEC() {
		return PROD_SPEC;
	}
	public void setPROD_SPEC(String pRODSPEC) {
		PROD_SPEC = pRODSPEC;
	}
	public CustStorageInfoTbl initPROD_SPEC(String pRODSPEC){
		setPROD_SPEC(pRODSPEC);
		return this;
	}
	public double getPROD_PRICE() {
		return PROD_PRICE;
	}
	public void setPROD_PRICE(double pRODPRICE) {
		PROD_PRICE = pRODPRICE;
	}
	public CustStorageInfoTbl initPROD_PRICE(double pRODPRICE){
		setPROD_PRICE(pRODPRICE);
		return this;
	}
	public int getQTY_1() {
		return QTY_1;
	}
	public void setQTY_1(int qTY_1) {
		QTY_1 = qTY_1;
	}
	public CustStorageInfoTbl initQTY_1(int qTY_1){
		setQTY_1(qTY_1);
		return this;
	}
	public int getQTY_2() {
		return QTY_2;
	}
	public void setQTY_2(int qTY_2) {
		QTY_2 = qTY_2;
	}
	public CustStorageInfoTbl initQTY_2(int qTY_2){
		setQTY_2(qTY_2);
		return this;
	}
	public int getQTY_3() {
		return QTY_3;
	}
	public void setQTY_3(int qTY_3) {
		QTY_3 = qTY_3;
	}
	public CustStorageInfoTbl initQTY_3(int qTY_3){
		setQTY_3(qTY_3);
		return this;
	}
	public int getQTY_4() {
		return QTY_4;
	}
	public void setQTY_4(int qTY_4) {
		QTY_4 = qTY_4;
	}
	public CustStorageInfoTbl initQTY_4(int qTY_4){
		setQTY_4(qTY_4);
		return this;
	}
	public int getQTY_5() {
		return QTY_5;
	}
	public void setQTY_5(int qTY_5) {
		QTY_5 = qTY_5;
	}
	public CustStorageInfoTbl initQTY_5(int qTY_5){
		setQTY_5(qTY_5);
		return this;
	}
	public int getQTY_6() {
		return QTY_6;
	}
	public void setQTY_6(int qTY_6) {
		QTY_6 = qTY_6;
	}
	public CustStorageInfoTbl initQTY_6(int qTY_6){
		setQTY_6(qTY_6);
		return this;
	}
	public int getQTY_7() {
		return QTY_7;
	}
	public void setQTY_7(int qTY_7) {
		QTY_7 = qTY_7;
	}
	public CustStorageInfoTbl initQTY_7(int qTY_7){
		setQTY_7(qTY_7);
		return this;
	}
	public int getQTY_8() {
		return QTY_8;
	}
	public void setQTY_8(int qTY_8) {
		QTY_8 = qTY_8;
	}
	public CustStorageInfoTbl initQTY_8(int qTY_8){
		setQTY_8(qTY_8);
		return this;
	}
	public int getQTY_9() {
		return QTY_9;
	}
	public void setQTY_9(int qTY_9) {
		QTY_9 = qTY_9;
	}
	public CustStorageInfoTbl initQTY_9(int qTY_9){
		setQTY_9(qTY_9);
		return this;
	}
	public int getQTY_10() {
		return QTY_10;
	}
	public void setQTY_10(int qTY_10) {
		QTY_10 = qTY_10;
	}
	public CustStorageInfoTbl initQTY_10(int qTY_10){
		setQTY_10(qTY_10);
		return this;
	}
	public int getQTY_11() {
		return QTY_11;
	}
	public void setQTY_11(int qTY_11) {
		QTY_11 = qTY_11;
	}
	public CustStorageInfoTbl initQTY_11(int qTY_11){
		setQTY_11(qTY_11);
		return this;
	}
	public int getQTY_12() {
		return QTY_12;
	}
	public void setQTY_12(int qTY_12) {
		QTY_12 = qTY_12;
	}
	public CustStorageInfoTbl initQTY_12(int qTY_12){
		setQTY_12(qTY_12);
		return this;
	}
	public int getTOTAL_QTY() {
		return TOTAL_QTY;
	}
	public void setTOTAL_QTY(int tOTALQTY) {
		TOTAL_QTY = tOTALQTY;
	}
	public CustStorageInfoTbl initTOTAL_QTY(int tOTALQTY){
		setTOTAL_QTY(tOTALQTY);
		return this;
	}
	public int getLAST_TOTAL_QTY() {
		return LAST_TOTAL_QTY;
	}
	public void setLAST_TOTAL_QTY(int lASTTOTALQTY) {
		LAST_TOTAL_QTY = lASTTOTALQTY;
	}
	public CustStorageInfoTbl initLAST_TOTAL_QTY(int lASTTOTALQTY){
		setLAST_TOTAL_QTY(lASTTOTALQTY);
		return this;
	}
	public int getSEND_TOTAL_QTY() {
		return SEND_TOTAL_QTY;
	}
	public void setSEND_TOTAL_QTY(int sENDTOTALQTY) {
		SEND_TOTAL_QTY = sENDTOTALQTY;
	}
	public CustStorageInfoTbl initSEND_TOTAL_QTY(int sENDTOTALQTY){
		setSEND_TOTAL_QTY(sENDTOTALQTY);
		return this;
	}
	public String getCREATOR() {
		return CREATOR;
	}
	public void setCREATOR(String cREATOR) {
		CREATOR = cREATOR;
	}
	public CustStorageInfoTbl initCREATOR(String cREATOR){
		setCREATOR(cREATOR);
		return this;
	}
	public Date getCREATE_DATE() {
		return CREATE_DATE;
	}
	public void setCREATE_DATE(Date cREATEDATE) {
		CREATE_DATE = cREATEDATE;
	}
	public CustStorageInfoTbl initCREATE_DATE(Date cREATEDATE){
		setCREATE_DATE(cREATEDATE);
		return this;
	}
	public String getUPDATOR() {
		return UPDATOR;
	}
	public void setUPDATOR(String uPDATOR) {
		UPDATOR = uPDATOR;
	}
	public CustStorageInfoTbl initUPDATOR(String uPDATOR){
		setUPDATOR(uPDATOR);
		return this;
	}
	public Date getUPDATE_DATE() {
		return UPDATE_DATE;
	}
	public void setUPDATE_DATE(Date uPDATEDATE) {
		UPDATE_DATE = uPDATEDATE;
	}
	public CustStorageInfoTbl initUPDATE_DATE(Date uPDATEDATE){
		setUPDATE_DATE(uPDATEDATE);
		return this;
	}
	public int getUPDATE_COUNT() {
		return UPDATE_COUNT;
	}
	public void setUPDATE_COUNT(int uPDATECOUNT) {
		UPDATE_COUNT = uPDATECOUNT;
	}
	public CustStorageInfoTbl initUPDATE_COUNT(int uPDATECOUNT){
		setUPDATE_COUNT(uPDATECOUNT);
		return this;
	}
	public int getS_QTY_1() {
		return S_QTY_1;
	}
	public void setS_QTY_1(int sQTY_1) {
		S_QTY_1 = sQTY_1;
	}
	public CustStorageInfoTbl initS_QTY_1(int sQTY_1){
		setS_QTY_1(sQTY_1);
		return this;
	}
	public int getS_QTY_2() {
		return S_QTY_2;
	}
	public void setS_QTY_2(int sQTY_2) {
		S_QTY_2 = sQTY_2;
	}
	public CustStorageInfoTbl initS_QTY_2(int sQTY_2){
		setS_QTY_2(sQTY_2);
		return this;
	}
	public int getS_QTY_3() {
		return S_QTY_3;
	}
	public void setS_QTY_3(int sQTY_3) {
		S_QTY_3 = sQTY_3;
	}
	public CustStorageInfoTbl initS_QTY_3(int sQTY_3){
		setS_QTY_3(sQTY_3);
		return this;
	}
	public int getS_QTY_4() {
		return S_QTY_4;
	}
	public void setS_QTY_4(int sQTY_4) {
		S_QTY_4 = sQTY_4;
	}
	public CustStorageInfoTbl initS_QTY_4(int sQTY_4){
		setS_QTY_4(sQTY_4);
		return this;
	}
	public int getS_QTY_5() {
		return S_QTY_5;
	}
	public void setS_QTY_5(int sQTY_5) {
		S_QTY_5 = sQTY_5;
	}
	public CustStorageInfoTbl initS_QTY_5(int sQTY_5){
		setS_QTY_5(sQTY_5);
		return this;
	}
	public int getS_QTY_6() {
		return S_QTY_6;
	}
	public void setS_QTY_6(int sQTY_6) {
		S_QTY_6 = sQTY_6;
	}
	public CustStorageInfoTbl initS_QTY_6(int sQTY_6){
		setS_QTY_6(sQTY_6);
		return this;
	}
	public int getS_QTY_7() {
		return S_QTY_7;
	}
	public void setS_QTY_7(int sQTY_7) {
		S_QTY_7 = sQTY_7;
	}
	public CustStorageInfoTbl initS_QTY_7(int sQTY_7){
		setS_QTY_7(sQTY_7);
		return this;
	}
	public int getS_QTY_8() {
		return S_QTY_8;
	}
	public void setS_QTY_8(int sQTY_8) {
		S_QTY_8 = sQTY_8;
	}
	public CustStorageInfoTbl initS_QTY_8(int sQTY_8){
		setS_QTY_8(sQTY_8);
		return this;
	}
	public int getS_QTY_9() {
		return S_QTY_9;
	}
	public void setS_QTY_9(int sQTY_9) {
		S_QTY_9 = sQTY_9;
	}
	public CustStorageInfoTbl initS_QTY_9(int sQTY_9){
		setS_QTY_9(sQTY_9);
		return this;
	}
	public int getS_QTY_10() {
		return S_QTY_10;
	}
	public void setS_QTY_10(int sQTY_10) {
		S_QTY_10 = sQTY_10;
	}
	public CustStorageInfoTbl initS_QTY_10(int sQTY_10){
		setS_QTY_10(sQTY_10);
		return this;
	}
	public int getS_QTY_11() {
		return S_QTY_11;
	}
	public void setS_QTY_11(int sQTY_11) {
		S_QTY_11 = sQTY_11;
	}
	public CustStorageInfoTbl initS_QTY_11(int sQTY_11){
		setS_QTY_11(sQTY_11);
		return this;
	}
	public int getS_QTY_12() {
		return S_QTY_12;
	}
	public void setS_QTY_12(int sQTY_12) {
		S_QTY_12 = sQTY_12;
	}
	public CustStorageInfoTbl initS_QTY_12(int sQTY_12){
		setS_QTY_12(sQTY_12);
		return this;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public CustStorageInfoTbl initSTATUS(String sTATUS){
		setSTATUS(sTATUS);
		return this;
	}
	public String getCREDIT_ID() {
		return CREDIT_ID;
	}
	public void setCREDIT_ID(String cREDITID) {
		CREDIT_ID = cREDITID;
	}
	public CustStorageInfoTbl initCREDIT_ID(String cREDITID){
		setCREDIT_ID(cREDITID);
		return this;
	}
	public String getISMARK() {
		return ISMARK;
	}
	public void setISMARK(String iSMARK) {
		ISMARK = iSMARK;
	}
	public CustStorageInfoTbl initISMARK(String iSMARK){
		setISMARK(iSMARK);
		return this;
	}
	public Date getCONFIRM_DATE() {
		return CONFIRM_DATE;
	}
	public void setCONFIRM_DATE(Date cONFIRMDATE) {
		CONFIRM_DATE = cONFIRMDATE;
	}
	public CustStorageInfoTbl initCONFIRM_DATE(Date cONFIRMDATE){
		setCONFIRM_DATE(cONFIRMDATE);
		return this;
	}
}
