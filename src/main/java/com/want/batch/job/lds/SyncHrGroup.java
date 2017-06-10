package com.want.batch.job.lds;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.lds.bo.HRDao;
import com.want.batch.job.lds.bo.HrGroup;
import com.want.batch.job.lds.bo.LDAP;

public class SyncHrGroup {
	private static final Log logger = LogFactory.getLog(SyncCustomer.class);

	public static List<String> hrGroupsSynced = new ArrayList<String>();

	public HRDao hrDao;

	private String resources;
	
	public void sync() {
		logger.info("Sync HR groups begin");
		LDAP.getInstance(resources);
		List<HrGroup> groups = hrDao.getListAreasIds(); 
		sync(groups);
	}

	public void sync(String... ids) {
		LDAP.getInstance(resources);
		List<HrGroup> groups = hrDao.getListAreasIds(ids);
		sync(groups);
	}

	private void sync(List<HrGroup> groups) {

		for (HrGroup group : groups) {
			if (!hrGroupsSynced.contains(group)) {
				try {
					group.setMembers(hrDao.getHrObjectList(group.getId()));
					group.save();
					hrGroupsSynced.add(group.getDn());
					logger.info("customer " + group.getDn() + " synced");
				} catch (Exception e) {
					logger.error(group.getDn() + " sync fail for " + e.getMessage());
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
