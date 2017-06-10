package com.want.batch.job.lds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.lds.bo.HRDao;
import com.want.batch.job.lds.bo.LDAP;
import com.want.batch.job.lds.bo.Position;

public class SyncPosition {

	private static final Log logger = LogFactory.getLog(SyncPosition.class);

	public static List<String> positionsSynced = new ArrayList<String>();

	public SyncEmployee syncEmployee;

	public HRDao hrDao;

	private String resources;

	protected void sync(String... ids) {
		LDAP.getInstance(resources);
		Collection<Position> positions = hrDao.getPositionsById(ids);
		sync(positions);
	}

	protected void sync(Collection<Position> positions) {
		
		if (positions == null || positions.size() <= 0)
			return;
		
		for (Position pos : positions) {

			if (!positionsSynced.contains(pos.getDn())) {
				pos.setEmployees(hrDao.getEmployeesByPosition(pos.getId()));
				syncEmployee.sync(pos.getEmployees());
				try {
					if (pos.save())
						logger.info("position " + pos.getDn() + " synced");
					positionsSynced.add(pos.getDn());
				} catch (Exception e) {
					logger.error(pos.getDn() + " sync fail for " + e.getMessage());
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

	public SyncEmployee getSyncEmployee() {
		return syncEmployee;
	}

	public void setSyncEmployee(SyncEmployee syncEmployee) {
		this.syncEmployee = syncEmployee;
	}
	
	
	public String getResources() {
		return resources;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}

}
