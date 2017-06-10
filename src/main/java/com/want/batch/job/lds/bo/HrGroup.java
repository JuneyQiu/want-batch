package com.want.batch.job.lds.bo;

import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

public class HrGroup extends Group {

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
			
			List<String> dns = LDAP.getInstance().getDns(LDAP.orgOU, members, "group");

			for (String m: dns)
				attrs.add(m);
			
			container.put(attrs);
		}

		return container;
	}

}
