package com.want.batch.job.lds;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.lds.bo.Group;
import com.want.batch.job.lds.bo.HRDao;
import com.want.batch.job.lds.bo.LDAP;

public class SyncApGroup {

	private static final Log logger = LogFactory.getLog(SyncCustomer.class);

	public static List<String> apGroupsSynced = new ArrayList<String>();

	private String resources;

	public HRDao hrDao;

	public void sync() {
		logger.info("Sync AP groups begin");
		List<Group> groups = hrDao.getAPGroupNames();
		sync(groups);
	}

	public void sync(String... ids) {
		LDAP.getInstance(resources);
		List<Group> groups = hrDao.getAPGroupNames(ids);
		sync(groups);
	}

	private void sync(List<Group> groups) {

		for (Group group : groups) {
			if (!apGroupsSynced.contains(group)) {
				try {
					group.setMembers(hrDao.getAPEmpList(group.getId()));
					group.save();
					apGroupsSynced.add(group.getDn());
					logger.info("customer " + group.getDn() + " synced");
				} catch (Exception e) {
					logger.error(group.getDn() + " sync fail for " + e.getMessage());
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
