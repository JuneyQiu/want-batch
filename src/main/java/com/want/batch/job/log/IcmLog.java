package com.want.batch.job.log;

import java.sql.Timestamp;

public class IcmLog extends HttpLog {
	
	private Timestamp icmDate;
	private String referral;

	public Timestamp getIcmDate() {
		return icmDate;
	}

	public void setIcmDate(Timestamp icmDate) {
		this.icmDate = icmDate;
	}

	public String getReferral() {
		return referral;
	}

	public void setReferral(String referral) {
		this.referral = referral;
	}

}
