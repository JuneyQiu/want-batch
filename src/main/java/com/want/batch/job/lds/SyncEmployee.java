package com.want.batch.job.lds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.lds.bo.Employee;
import com.want.batch.job.lds.bo.HRDao;
import com.want.batch.job.lds.bo.LDAP;

public class SyncEmployee {

	private static final Log logger = LogFactory.getLog(SyncEmployee.class);

	public static List<String> employeesSynced = new ArrayList<String>();

	public HRDao hrDao;

	private String resources;

	protected void sync(String... ids) {
		LDAP.getInstance(resources);
		List<Employee> employees = hrDao.getEmployeesById(ids);
		sync(employees);
	}

	protected void sync(Collection<Employee> employees) {
		//LDAP.getInstance(resources);

		for (Employee emp : employees) {
			if (!employeesSynced.contains(emp.getDn())) {
				try {
					if (emp.save())
						logger.info("employee " + emp.getDn() + " synced");
					employeesSynced.add(emp.getDn());
				} catch (Exception e) {
					logger.error(emp.getDn() + " sync fail for " + e.getMessage());
				}
			}
		}
	}

	public HRDao getHrDao() {
		return hrDao;
	}

	public void setHrDao(HRDao hrDao) {
		this.hrDao = hrDao;
	}
	
	
	public String getResources() {
		return resources;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}
	
}
