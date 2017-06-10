// ~ Package Declaration
// ==================================================

package com.want.batch.job.shipment.bo;

import java.math.BigDecimal;

import com.want.batch.job.shipment.util.DateFormater;

// ~ Comments
// ==================================================

public class StockBO {

	public StockBO() {
		YEARMONTH = new DateFormater().getCurrentYearMonth();
	}

	private String TCOMPANY_SEQ_ID;
	private String PRODUCT_SEQ_ID;
	private String YEARMONTH;
	private String CUSTOMER_ID;
	private String MATERIAL_ID;
	private BigDecimal QTY;
	private String UPDATE_DATE;
	private String VTWEG;
	private String SPART;
	private String LAST_YEARMONTH;
	private String PROJECT_SID;

	public String getYEARMONTH() {

		return YEARMONTH;
	}

	public void setYEARMONTH(String yearmonth) {

		YEARMONTH = yearmonth;
	}

	public String getCUSTOMER_ID() {

		return CUSTOMER_ID;
	}

	public void setCUSTOMER_ID(String customer_id) {

		CUSTOMER_ID = customer_id;
	}

	public String getMATERIAL_ID() {

		return MATERIAL_ID;
	}

	public void setMATERIAL_ID(String material_id) {

		MATERIAL_ID = material_id;
	}

	public BigDecimal getQTY() {

		return QTY;
	}

	public void setQTY(BigDecimal qty) {

		QTY = qty;
	}

	public String getUPDATE_DATE() {

		return UPDATE_DATE;
	}

	public void setUPDATE_DATE(String update_date) {

		UPDATE_DATE = update_date;
	}

	public String getVTWEG() {

		return VTWEG;
	}

	public void setVTWEG(String vtweg) {

		VTWEG = vtweg;
	}

	public String getSPART() {

		return SPART;
	}

	public void setSPART(String spart) {

		SPART = spart;
	}

	public void setTCOMPANY_SEQ_ID(String TCOMPANY_SEQ_ID) {
		this.TCOMPANY_SEQ_ID = TCOMPANY_SEQ_ID;
	}

	public String getTCOMPANY_SEQ_ID() {
		return TCOMPANY_SEQ_ID;
	}

	public void setPRODUCT_SEQ_ID(String PRODUCT_SEQ_ID) {
		this.PRODUCT_SEQ_ID = PRODUCT_SEQ_ID;
	}

	public String getPRODUCT_SEQ_ID() {
		return PRODUCT_SEQ_ID;
	}

	public void setLAST_YEARMONTH(String LAST_YEARMONTH) {
		this.LAST_YEARMONTH = LAST_YEARMONTH;
	}

	public String getLAST_YEARMONTH() {
		return LAST_YEARMONTH;
	}

	public void setPROJECT_SID(String PROJECT_SID) {
		this.PROJECT_SID = PROJECT_SID;
	}

	public String getPROJECT_SID() {
		return PROJECT_SID;
	}

}
