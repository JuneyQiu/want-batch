package com.want.batch.job.old;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;
import com.want.utils.StackTrace;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2010
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
@Component
public class DailyOrgAndAuthJobs extends AbstractWantJob {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	public CreateYDAccount createYDAccount;

	@Autowired
	public CreateXGCustomerRel createXGCustomerRel;

	@Autowired
	public CreateZRCustomerRel createZRCustomerRel;

	@Autowired
	public CreateSZ_ZJCustomerRel createSZ_ZJCustomerRel;

	@Autowired
	public SyncKAOrg syncKAOrg;

	@Autowired
	public SyncKAOrg2 syncKAOrg2;

	@Autowired
	public CreateEMPCompanyBranchRel createEMPCompanyBranchRel;

	@Autowired
	public CreateEMPCompanyRel createEMPCompanyRel;

	@Autowired
	public UpdateAuthSFA2 updateAuthSFA2;
	
	@Autowired
	public CreateDuDaoAccunt createDuDaoAccount;

	public void execute() throws SQLException {
		logger.info("同步资料开始! --");

		StringBuffer errors = new StringBuffer();

		try {
			logger.info("建立业代帐号! ");
			// CreateYDAccount cydaTest = new CreateYDAccount();
			createYDAccount.createSalesAccount();
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			logger.info("结束建立业代帐号! ");

			// logger.info("开始同步营业所销管资料! ");
			// CreateXGCustomerRel.createBranchXGCustomerRel();
			logger.info("结束同步营业所销管资料! ");
			logger.info("开始同步分公司销管资料! ");
			createXGCustomerRel.createCompanyXGCustomerRel();
			logger.info("结束同步分公司销管资料! ");
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			logger.info("开始同步城区主任资料! ");

			createZRCustomerRel.createCQZRCustomerRel();
			logger.info("结束同步城区主任资料! ");
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			logger.info("开始同步县城主任资料! ");
			createZRCustomerRel.createXCZRCustomerRel();
			logger.info("结束同步县城主任资料! ");
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			logger.info("更新直营主任user_type_sid! ");// Modify by David Luo on
													// 2012/8/30
													// for update KA_ZR
													// user_type_sid
			createZRCustomerRel.updateKAZRUserTypeSid();
			logger.info("结束更新直营主任user_type_sid! ");
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			logger.info("开始同步主任事业部关系资料! ");
			createZRCustomerRel.syncZRDivisionRel();
			logger.info("结束同步主任事业部关系资料! ");
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			logger.info("开始同步所长资料! ");
			createSZ_ZJCustomerRel.createSZCustomerRel();
			logger.info("结束同步所长资料! ");
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			logger.info("开始同步总监资料! ");
			createSZ_ZJCustomerRel.createZJCustomerRel();
			logger.info("结束同步总监资料! ");
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			logger.info("开始同步直营专员，客户主管资料! ");
			syncKAOrg.synckaorgkhzg();
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			syncKAOrg.synckaorgzy();
			// 等胡蓉通知上线再打开
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			logger.info("----开始同步批发专员资料！  -- 孙建");
			syncKAOrg.synckaorgzy_pf();
			logger.info("----结束同步批发专员资料！  -- 孙建");
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			// 同步"地方"直营人员组织功能权限 modified by David Luo on 20120412
			logger.info("----开始同步直营权限！  -- David");
			syncKAOrg2.s_syncgnqx();
			logger.info("----结束同步直营权限！  -- David");
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			logger.info("结束同步直营专员，客户主管资料! ");

			logger.info("开始同步员工与分公司营业所资料! ");
			createEMPCompanyBranchRel.createEMPCompanyBranchRel();
			logger.info("结束同步员工与分公司营业所资料! ");
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			logger.info("开始同步业代与分公司关系! ");
			createEMPCompanyRel.createEMPCompanyRel();
			logger.info("结束同步业代与分公司关系! ");
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		try {
			logger.info("---- ******   开始更新SFA2 上线地区员工权限！  -- David");
			updateAuthSFA2.updateSfaAuth();
			logger.info("---- ******   结束更新SFA2 上线地区员工权限！  -- David");
		} catch (Exception e) {
			errors.append(StackTrace.get(e));
		}

		logger.info("同步资料结束! ");

		try {
			
			// 2015-07-28 mirabelle add 同步督导
			createDuDaoAccount.execute();
		}
		catch (Exception e) {
			
			errors.append(StackTrace.get(e));
		}
		
		if (errors.toString().length() > 0) 
			throw new WantBatchException(errors.toString());
	}
	
	
}
