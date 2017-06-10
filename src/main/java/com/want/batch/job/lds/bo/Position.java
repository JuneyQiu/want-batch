package com.want.batch.job.lds.bo;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

public class Position extends Updatable {

	private String id;
	private String name;//y
	private String orgId;//y
	private String masterId;//y
	private boolean isDepartmentDirector;//y
	private boolean isDivisionDirector;//y
	private boolean isTopDirector;//y
	private String areaCode;//y
	private String supervisor;//y
	private String supervisorPosId;//y
	private String ou;	
	private Organization organization;
	private List<Employee> employees;	
	
	/*add by wang yi cheng*/
	private boolean supervisor_status;
	
	/*
	 * 支援岗 add by wangyicheng 
	 */
	private String supportEmpId;//y
	private String supportPosId;//y
	
	/*
	 * Propertey add by wangyicheng
	 */
	private String pos_property_id;//y
	private String pos_property_name;//y
	
	private String director_pos_id;//无用属性
	
	/*
	 * Position memberof
	 */
	private List<String> listPosMember = new ArrayList<String>(); 
	
	
	public String getMasterId() {
		return masterId;
	}
	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}
	public boolean isDepartmentDirector() {
		return isDepartmentDirector;
	}
	public void setDepartmentDirector(boolean isDepartmentDirector) {
		this.isDepartmentDirector = isDepartmentDirector;
	}
	public boolean isDivisionDirector() {
		return isDivisionDirector;
	}
	public void setDivisionDirector(boolean isDivisionDirector) {
		this.isDivisionDirector = isDivisionDirector;
	}
	public boolean isTopDirector() {
		return isTopDirector;
	}
	public void setTopDirector(boolean isTopDirector) {
		this.isTopDirector = isTopDirector;
	}
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
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	public List<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}		
	public String getOu() {
		return ou;
	}
	public void setOu(String ou) {
		this.ou = ou;
	}	
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	public String getSupervisorPosId() {
		return supervisorPosId;
	}
	public void setSupervisorPosId(String supervisorPosId) {
		this.supervisorPosId = supervisorPosId;
	}
	
	
	
	
	public String getSupportEmpId() {
		return supportEmpId;
	}
	public void setSupportEmpId(String supportEmpId) {
		this.supportEmpId = supportEmpId;
	}
	public String getSupportPosId() {
		return supportPosId;
	}
	public void setSupportPosId(String supportPosId) {
		this.supportPosId = supportPosId;
	}
	public boolean isSupervisor_status() {
		return supervisor_status;
	}
	public void setSupervisor_status(boolean supervisor_status) {
		this.supervisor_status = supervisor_status;
	}
	
	
	public String getPos_property_id() {
		return pos_property_id;
	}
	public void setPos_property_id(String pos_property_id) {
		this.pos_property_id = pos_property_id;
	}
	public String getPos_property_name() {
		return pos_property_name;
	}
	public void setPos_property_name(String pos_property_name) {
		this.pos_property_name = pos_property_name;
	}
	
	public List<String> getListPosMember() {
		return listPosMember;
	}
	public void setListPosMember(List<String> listPosMember) {
		this.listPosMember = listPosMember;
	}
	public String getDirector_pos_id() {
		return director_pos_id;
	}
	public void setDirector_pos_id(String director_pos_id) {
		this.director_pos_id = director_pos_id;
	}
	public String toString() {
		StringBuffer temp = new StringBuffer();
		temp.append("岗位代码:"+getId()+"    岗位名:" + getName() +"    岗位顺序:" + getMasterId() +"\n");
		temp.append("部门主管:" + isDepartmentDirector()+"    单位主管:"+isDivisionDirector+"    单位最高主管:"+isTopDirector +"\n");
		temp.append("人事范围代码:" + getAreaCode()+"    上级主管工号:"+getSupervisor()+"    上级主管岗位代码:"+getSupervisorPosId() +"\n");
		if(getEmployees()!=null)
			for (Employee emp: getEmployees())
				temp.append("员工资讯:\n" + emp +"\n");		
		return temp.toString();
	}
	
	@Override
	public Attributes getContainer() {
		Attributes container = new BasicAttributes();
		
		Attribute objClasses = new BasicAttribute("objectClass");
		objClasses.add("top");
		objClasses.add("group");
		objClasses.add("groupOfWantWantPositions");
		container.put(objClasses);

		if (isDepartmentDirector())
			container.put(new BasicAttribute("deptDirector", "Y"));
		else
			container.put(new BasicAttribute("deptDirector", "N"));
			
		if (isDivisionDirector())
			container.put(new BasicAttribute("divisionDirector", "Y"));
		else
			container.put(new BasicAttribute("divisionDirector", "N"));

		if (isTopDirector())
			container.put(new BasicAttribute("topDirector", "Y"));
		else
			container.put(new BasicAttribute("topDirector", "N"));
			
		container.put(new BasicAttribute("cn", getId()));
		container.put(new BasicAttribute("description", getName()));
		// Attribute empMasterPos = new BasicAttribute("empMasterPos",
		// masterPos);
		container.put(new BasicAttribute("businessCategory", getMasterId()));
		container.put(new BasicAttribute("supervisor", getSupervisor()));
		container.put(new BasicAttribute("empAreaCode", getAreaCode()));

		/**
		 * 上级主管岗位号
		 */
		if (getSupervisorPosId() != null)
			container.put(new BasicAttribute("supervisorPosId", getSupervisorPosId()));

		/*
		 * 添加支援岗
		 */
		if (getSupportEmpId() != null && getSupportEmpId().equals("") == false)
			container.put(new BasicAttribute("supportEmpId", getSupportEmpId()));

		if (getSupportPosId() != null && getSupportPosId().equals("") == false)
			container.put(new BasicAttribute("supportPosId", getSupportPosId()));
	

		/*
		 * 添加propertyId propertyName
		 */
		if (getPos_property_id() != null)
			container.put(new BasicAttribute("posPropertyId", getPos_property_id()));

		if (getPos_property_name() != null)
			container.put(new BasicAttribute("posPropertyName", getPos_property_name()));
		
		Attribute members = null;
		
		if (this.getEmployees() != null && this.getEmployees().size() > 0) {
			if (members == null) 
				members = new BasicAttribute("member");
			
			for (Employee emp: this.getEmployees())
				members.add(emp.getDn());
			
			container.put(members);
		}
		

		return container;
	}

}
