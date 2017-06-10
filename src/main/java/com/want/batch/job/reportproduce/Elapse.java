package com.want.batch.job.reportproduce;

public class Elapse {
	private long time;
	
	protected Elapse(long time) {
		this.time = time;
	}
	
	protected long getElapseTime() {
		return System.currentTimeMillis() - time;
	}
}