package com.want.batch.job.lds;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.lds.bo.LDAP;

public class SyncSalesLdapJob extends AbstractWantJob {
	private static final Log logger = LogFactory.getLog(LDAP.class);

	public SyncOrganization syncOrganization;
	
 
	@Override
	public void execute() throws Exception {

		long begin = System.currentTimeMillis();

		syncOrganization.sync();
		
		
		long diff = System.currentTimeMillis() - begin;

		logger.info("complete sync: organizations synced " + SyncOrganization.organizationsSynced.size()
				+ ", positions synced " + SyncPosition.positionsSynced.size() + " employees synced "
				+ SyncEmployee.employeesSynced.size() + ", took " + ((int) (diff / (1000 * 60))) + " minutes");
	}

	public SyncOrganization getSyncOrganization() {
		return syncOrganization;
	}

	public void setSyncOrganization(SyncOrganization syncOrganization) {
		this.syncOrganization = syncOrganization;
	}

}
