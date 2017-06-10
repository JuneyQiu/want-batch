package com.want.batch.job.lds.bo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;

public abstract class Updatable {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private String dn;
	
	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}
	
	@Override
	public int hashCode() {
		return dn.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Updatable) {
			Updatable updatable = (Updatable) obj;
			return dn.equals(updatable.getDn());
		}
		return super.equals(obj);
	}

	public abstract Attributes getContainer() throws Exception;
	
	public ModificationItem[] getModifications() throws Exception {
		return null;
	}
	
	public Date parseLdapDate(String ldapDate) throws Exception {
	    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.parse(ldapDate);
	}
	
	public boolean save() throws Exception {
		return LDAP.getInstance().save(this);
	}

}
