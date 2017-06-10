package com.want.batch.job.lds.bo;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

public class Group extends Updatable {
	
	protected String id;
	
	protected List<String> members;
	
	public Group() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getMembers() {
		if (this.members == null)
			this.members = new ArrayList<String>();

		return members;
	}

	public void setMembers(List<String> members) {
		addMember(members);
	}
	
	public void addMember(List<String> members) {
		if (members != null)
			for (String m: members)
				if (m != null && !this.getMembers().contains(m))
					this.getMembers().add(m);
	}

	@Override
	public Attributes getContainer() throws Exception {
		Attributes container = new BasicAttributes();

		Attribute objClasses = new BasicAttribute("objectClass");
		objClasses.add("top");
		objClasses.add("group");
		container.put(objClasses);

		container.put(new BasicAttribute("cn", id));
		container.put(new BasicAttribute("description", id));
		
		if (this.members != null && this.members.size() > 0) {
			Attribute attrs = new BasicAttribute("member");
			
			List<String> dns = LDAP.getInstance().getDns(LDAP.userOU, members, "person");

			for (String m: dns)
				attrs.add(m);
			
			container.put(attrs);
		}

		return container;
	}

}