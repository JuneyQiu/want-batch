package com.want.batch.job.lds.bo;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.ModificationItem;

public class Customer extends Updatable {

	private String id;
	
	private String customerId;
	
	private String address;
	
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getCustomerId() {
		return customerId;
	}


	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public Attributes getContainer() {
		Attributes container = new BasicAttributes();

		// Create the objectclass to add
		Attribute objClasses = new BasicAttribute("objectClass");
		container.put(objClasses);
		objClasses.add("top");
		objClasses.add("person");
		objClasses.add("organizationalPerson");
		objClasses.add("user");
		objClasses.add("wantWantIcPerson");
		
		container.put(new BasicAttribute("userPassword", "P@ssw0rd"));
		container.put(new BasicAttribute("mail", "ecad_tester@want-want.com"));
		
		container.put(new BasicAttribute("cn", getId()));
		container.put(new BasicAttribute("uid", this.getCustomerId()));
		container.put(new BasicAttribute("userPrincipalName", getId()));

		container.put(new BasicAttribute("sn", getAddress()));

		return container;
	}
	
	public boolean save() throws Exception {
		return LDAP.getInstance().update(this);
	}

}
