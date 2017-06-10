package com.want.batch.job.lds.bo;

import java.util.ArrayList;
import java.util.Collection;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

public class Organization extends Updatable {

	private String ou;
	private String id;
	private String name;
	private String areaId;
	private String parentDept;
	private Collection<String> subOrgIds = null;
	private Collection<Organization> subOrgs = null;
	private Collection<Position> positions = null;

	public String getOu() {
		return ou;
	}

	public void setOu(String ou) {
		this.ou = ou;
	}

	public String getParentDept() {
		return parentDept;
	}

	public void setParentDept(String parentDept) {
		this.parentDept = parentDept;
	}

	public Collection<String> getSubOrgIds() {
		return subOrgIds;
	}

	public void setSubOrgIds(Collection<String> subOrgIds) {
		this.subOrgIds = subOrgIds;
	}

	public Collection<Organization> getSubOrgs() {
		return subOrgs;
	}

	public void addSubOrgs(Collection<Organization> subOrgs) {
		if (this.subOrgs == null)
			this.subOrgs = new ArrayList<Organization>();
		if (subOrgs != null && subOrgs.size() > 0)
			this.subOrgs.addAll(subOrgs);
	}

	public Collection<Position> getPositions() {
		return positions;
	}

	public void setPositions(Collection<Position> positions) {
		this.positions = positions;
	}

	public void addPositions(Collection<Position> positions) {
		if (this.positions == null)
			this.positions = new ArrayList<Position>();
		if (positions != null && positions.size() > 0)
			this.positions.addAll(positions);
	}

	public String getOrgLevel() {
		return orgLevel;
	}

	public void setOrgLevel(String orgLevel) {
		this.orgLevel = orgLevel;
	}

	private String orgLevel;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public Attributes getContainer() {
		Attributes container = new BasicAttributes();

		// Create the objectclass to add
		Attribute objClasses = new BasicAttribute("objectClass");
		objClasses.add("top");
		objClasses.add("group");
		container.put(objClasses);

		container.put(new BasicAttribute("cn", getId()));
		container.put(new BasicAttribute("description", getName()));
		container.put(new BasicAttribute("adminDescription", getOrgLevel()));

		Attribute members = null;
		
		if (this.subOrgs != null && this.subOrgs.size() > 0) {
			if (members == null) 
				members = new BasicAttribute("member");
			
			for (Organization child: this.subOrgs)
				members.add(child.getDn());
		}

		if (this.positions != null && this.positions.size() > 0) {
			if (members == null) 
				members = new BasicAttribute("member");

			for (Position child: this.positions)
				members.add(child.getDn());
		}

		if (members != null)
			container.put(members);

		return container;
	}

	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("id=" + this.getId() + ",部门代码:" + getId() + " 门名:" + getName() + " 部门层级:" + getOrgLevel() + "上级部门: "
				+ getParentDept());
		return temp.toString();
	}
}
