package com.want.batch.job.shipment.bo;

public class VtwegSpartBO {

	public VtwegSpartBO() {

	}

	private String VTWEG;
	private String SPART;
	private String COMPANY_ID;
	private String FORWARDER_ID;
	private String STATUS;

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

	public String getCOMPANY_ID() {

		return COMPANY_ID;
	}

	public void setCOMPANY_ID(String company_id) {

		COMPANY_ID = company_id;
	}

	public String getFORWARDER_ID() {

		return FORWARDER_ID;
	}

	public void setFORWARDER_ID(String forwarder_id) {

		FORWARDER_ID = forwarder_id;
	}

	public String getSTATUS() {

		return STATUS;
	}

	public void setSTATUS(String status) {

		STATUS = status;
	}

}
