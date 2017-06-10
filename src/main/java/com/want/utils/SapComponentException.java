package com.want.utils;

public class SapComponentException extends Exception {
	private static final long serialVersionUID = 3324859894147374654L;

	public SapComponentException() {
	}

	public SapComponentException(String message) {
		super(message);
	}

	public SapComponentException(Throwable cause) {
		super(cause);
	}

	public SapComponentException(String message, Throwable cause) {
		super(message, cause);
	}
}