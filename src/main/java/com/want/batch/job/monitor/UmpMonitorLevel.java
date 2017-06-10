package com.want.batch.job.monitor;

public enum UmpMonitorLevel {

	L5("5"), L4("4"), L3("3");
	
	private String string;
	
	private UmpMonitorLevel(String string) {
		this.string = string;
	}
	
	@Override
	public String toString() {
		return this.string;
	}
	
}
