/*
 * 
用在新增全部ADLDS数据的程序
 * 
 */

package com.want.batch.job.lds;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.lds.bo.LDAP;

/**
 * 
 * @author wang yi cheng
 * 
 *         sync LDS
 * 
 *         1.ENV 2.private static final String AP private static final String HR
 *         3.findDiffAttrEmp() 4.syncemp 中的删除离职员工 5.LDAPMananger -- iwwp 6.配置文件
 *         7.对应的DB 220 or 181
 */
public class SyncAdminLdapJob extends AbstractWantJob {

	private static final Log logger = LogFactory.getLog(LDAP.class);

	public SyncOrganization syncOrganization;

	public SyncCustomer syncCustomer;

	public SyncApGroup syncApGroup;

	public SyncCustomGroup syncCustomGroup;

	public SyncHrGroup syncHrGroup;
 
	@Override
	public void execute() throws Exception {

		long begin = System.currentTimeMillis();

		syncOrganization.sync();
		
		syncCustomer.sync();
		
		syncApGroup.sync();
		
		syncCustomGroup.sync();
		
		syncHrGroup.sync();
		
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

	public SyncCustomer getSyncCustomer() {
		return syncCustomer;
	}

	public void setSyncCustomer(SyncCustomer syncCustomer) {
		this.syncCustomer = syncCustomer;
	}

	public SyncApGroup getSyncApGroup() {
		return syncApGroup;
	}

	public void setSyncApGroup(SyncApGroup syncApGroup) {
		this.syncApGroup = syncApGroup;
	}

	public SyncCustomGroup getSyncCustomGroup() {
		return syncCustomGroup;
	}

	public void setSyncCustomGroup(SyncCustomGroup syncCustomGroup) {
		this.syncCustomGroup = syncCustomGroup;
	}

	public SyncHrGroup getSyncHrGroup() {
		return syncHrGroup;
	}

	public void setSyncHrGroup(SyncHrGroup syncHrGroup) {
		this.syncHrGroup = syncHrGroup;
	}
	
	
}
