package com.want.batch.job.lds.exception;

public class OrganizationNotFoundException extends Exception {
	private static final long serialVersionUID = 515305458417971739L;

	public String toString() {
        String s = getClass().getName();
        String message = "Organization Not Found in LDAP.";
        return (message != null) ? (s + ": " + message) : s;
    }
}
