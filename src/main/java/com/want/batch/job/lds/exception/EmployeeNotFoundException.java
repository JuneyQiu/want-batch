package com.want.batch.job.lds.exception;

public class EmployeeNotFoundException extends Exception {
	private static final long serialVersionUID = -3793799577321541697L;

	public String toString() {
        String s = getClass().getName();
        String message = "Employee Not Found in LDAP.";
        return (message != null) ? (s + ": " + message) : s;
    }
}
