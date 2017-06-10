package com.want.batch.job.lds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.lds.bo.HRDao;
import com.want.batch.job.lds.bo.LDAP;
import com.want.batch.job.lds.bo.Organization;
import com.want.batch.job.lds.exception.InfiniteLoopException;


public class SyncOrganization {

	private static final Log logger = LogFactory.getLog(SyncOrganization.class);

	public static List<String> organizationsSynced = new ArrayList<String>();

	public HRDao hrDao;
	
	public SyncPosition syncPosition;
	
	private String resources;

	public void sync() {
		logger.info("Sync organization begin");
	//	sync("10000000");
		sync("10600095");
	}

	protected Collection<Organization> sync(String... ids) {
		LDAP.getInstance(resources);
		Collection<Organization> orgs = hrDao.getOrganizations(ids);
		return sync(orgs);
	}

	private Collection<Organization> sync(Collection<Organization> organizations) {
		for (Organization org : organizations) {
			if (!organizationsSynced.contains(org.getDn())) {
				try {
					if (org.getSubOrgIds() != null && org.getSubOrgIds().size() > 0)
						for (String m : org.getSubOrgIds()) {
							Collection<Organization> memOrgs = sync(m);
							org.addSubOrgs(memOrgs);
						}

					// org.getPositions() = hrDao
					org.setPositions(hrDao.getPositionsByOrg(org.getId()));

					syncPosition.sync(org.getPositions());

					if (org.save())
						logger.info("organization " + org.getDn() + " synced");
					organizationsSynced.add(org.getDn());
					

				} catch (InfiniteLoopException infinite) {
					logger.error("organization " + org.getDn() + " saving fail for " + infinite.getMessage());
				} catch (Exception e) {
					logger.error("organization " + org.getDn() + " saving fail for " + e.getMessage());
				}
			}
		}

		return organizations;
	}

	public HRDao getHrDao() {
		return hrDao;
	}

	public void setHrDao(HRDao hrDao) {
		this.hrDao = hrDao;
	}

	public SyncPosition getSyncPosition() {
		return syncPosition;
	}

	public void setSyncPosition(SyncPosition syncPosition) {
		this.syncPosition = syncPosition;
	}

	public String getResources() {
		return resources;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}
	
}
