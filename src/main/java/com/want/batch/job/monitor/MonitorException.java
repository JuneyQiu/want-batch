package com.want.batch.job.monitor;

public class MonitorException extends RuntimeException {

	private static final long serialVersionUID = 2109713009267552496L;

	public MonitorException(String message) {
		super(message);
	}
	
	public MonitorException(Throwable t) {
		super(t);
	}
}
