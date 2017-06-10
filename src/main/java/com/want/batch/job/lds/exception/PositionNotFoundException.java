package com.want.batch.job.lds.exception;

public class PositionNotFoundException extends Exception {
	private static final long serialVersionUID = -3423027810889153931L;

	public String toString() {
        String s = getClass().getName();
        String message = "Position Not Found in LDAP.";
        return (message != null) ? (s + ": " + message) : s;
    }
}
