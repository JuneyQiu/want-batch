package com.want.batch.job.archive.syncbussgrade.pojo;

public class ProdInfo {
	private String prodId;
	private String prodName;
	private String lineTypeId;//小批线ID
	
	
	
	public String getProdId() {
		return prodId;
	}
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public String getLineTypeId() {
		return lineTypeId;
	}
	public void setLineTypeId(String lineTypeId) {
		this.lineTypeId = lineTypeId;
	}
}
