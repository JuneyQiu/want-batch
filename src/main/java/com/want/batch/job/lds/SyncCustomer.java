package com.want.batch.job.lds;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.lds.bo.Customer;
import com.want.batch.job.lds.bo.HRDao;
import com.want.batch.job.lds.bo.LDAP;

public class SyncCustomer {

	private static final Log logger = LogFactory.getLog(SyncCustomer.class);

	public static List<String> customersSynced = new ArrayList<String>();

	private String resources;

	public HRDao hrDao;

	protected void sync() {
		logger.info("Sync customers begin");
		LDAP.getInstance(resources);
		List<Customer> customers = hrDao.getCustomerList();
		sync(customers);
		
		logger.info("Clean up retired customers ...");
		List<String> retired = hrDao.getRetiredCustomerList();
		for (String id: retired) {
			if (LDAP.getInstance().delete(id))
				logger.info(id + " deleted");
		}
	}

	protected void sync(String... ids) {
		LDAP.getInstance(resources);
		List<Customer> customers = hrDao.getCustomerList(ids);
		sync(customers);
	}

	private void sync(List<Customer> customers) {

		for (Customer cust : customers) {
			if (!customersSynced.contains(cust)) {
				try {
					if (cust.save())
						logger.info("customer " + cust.getDn() + " synced");
					customersSynced.add(cust.getDn());
				} catch (Exception e) {
					logger.error(cust.getDn() + " sync fail for " + e.getMessage());
				}
			}
		}
	}

	public String getResources() {
		return resources;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}


	public HRDao getHrDao() {
		return hrDao;
	}


	public void setHrDao(HRDao hrDao) {
		this.hrDao = hrDao;
	}
	
	
}
