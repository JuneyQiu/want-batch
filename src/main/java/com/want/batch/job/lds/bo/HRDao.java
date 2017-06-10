package com.want.batch.job.lds.bo;

import java.util.Collection;
import java.util.List;

/**
 * @author 00110392
 * 
 */
public interface HRDao {

	public Collection<Organization> getOrganizations(String... ids);
	
	public Collection<Position> getPositionsByOrg(String... ids);
	
	public Collection<Position> getPositionsById(String... ids);

	public List<Employee> getEmployeesByPosition(String... ids);

	public List<Employee> getEmployeesById(String... ids);

	public List<Customer> getCustomerList(String... ids);
	
	public List<String> getRetiredCustomerList();	

	public List<String> getHrObjectList(String areaId);
	
	public List<String> getAPEmpList(String groupName);
	
	public List<Group> getAPGroupNames(String... names);
	
	public List<Group> getCustomizedAPGroupNames(String... names);
	
	public List<String> getCustomizedAPEmpList(String groupName);
	
	public List<HrGroup> getListAreasIds(String... names);

}