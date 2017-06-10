package com.want.batch.job.org;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.org.util.SapOrgParam;
import com.want.utils.SapDAO;
import com.want.utils.SapUtils;

@Component
public class SyncOrg extends AbstractWantJob {

	@Autowired
	public SapDAO sapDAO;

	@Override
	public void execute() {
		syncThird();
		syncMarket();
		syncBranch();
		syncCompany();
		syncThirdMarketRel();
		syncMarketBranchRel();
		syncThirdCompanyRel();
		syncFullMarketLevelTempTbl();// full_market_level_temp
		syncCompanyBranchTbl();//company_branch
	}

	/**
	 * 同步三级地数据
	 */
	private void syncThird() {
		logger.info("开始同步三级地......");
		String delSql = " delete from third_sap ";
		String insertSql = " insert into third_sap (third_id,third_name,create_user,create_date) values (?,?,'sys',sysdate) ";

		List list = sapDAO.getSAPData3(SapOrgParam.FUNC_NAME.getTableName(),
				SapOrgParam.THIRD.getTableName());
		logger.info("三级地条数=" + list.size());

		if (list == null || list.size() == 0) {
			String msg = "读取 " + SapOrgParam.FUNC_NAME.getTableName() + " " + SapOrgParam.THIRD.getTableName() + "无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		getiCustomerJdbcOperations().update(delSql);
		logger.info("删除icustomer中现有的三级地");
		List<Object[]> values = SapUtils.getUniqueMapValue(list, "CITYP_CODE", "CITY_PART");
		int [] inserts = getiCustomerJdbcOperations().batchUpdate(insertSql, values);
		logger.info("将从sap查询出来的三级地数据插入icustomer......" + inserts.length);
		logger.info("同步三级地完成");
	}

	/**
	 * 同步标准市场数据
	 */
	private void syncMarket() {
		logger.info("开始同步标准市场......");
		List list = sapDAO.getSAPData3(SapOrgParam.FUNC_NAME.getTableName(),
				SapOrgParam.MARKET.getTableName());
		logger.info("标准市场条数=" + list.size());

		if (list == null || list.size() == 0) {
			String msg = "读取 " + SapOrgParam.FUNC_NAME.getTableName() + " " + SapOrgParam.MARKET.getTableName() + "无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		String delSql = " delete from market_sap ";
		StringBuffer insertSql = new StringBuffer("");
		insertSql
				.append(" insert into market_sap(market_id,market_name,market_level,rp_market_id")
				.append(",rp_market_name,yp_market_id,yp_market_name,create_user,create_date)  ")
				.append(" values(?,?,?,?,?,?,?,'sys',sysdate)");


		this.getiCustomerJdbcOperations().update(delSql);
		logger.info("删除icustomer中现有的标准市场......");

		List<Object[]> values = SapUtils.getUniqueMapValue(list, "ZBZSC", "ZNAME", "ZLEVEL", "ZBZSC1", "ZNAME1", "ZBZSC2", "ZNAME2");
		int [] inserts = getiCustomerJdbcOperations().batchUpdate(insertSql.toString(), values);
		logger.info("将从sap查询出来的标准市场数据插入icustomer......" + inserts.length);
		logger.info("同步标准市场完成");
	}

	/**
	 * 同步营业所数据
	 */
	private void syncBranch() {
		logger.info("开始同步营业所......");
		String delSql = " delete from branch_sap ";
		String insertSql = " insert into branch_sap (branch_id,branch_name,create_user,create_date) values (?,?,'sys',sysdate)";

		List list = sapDAO.getSAPData3(SapOrgParam.FUNC_NAME.getTableName(),
				SapOrgParam.BRANCH.getTableName());
		logger.info("营业所条数=" + list.size());

		if (list == null || list.size() == 0) {
			String msg = "读取 " + SapOrgParam.FUNC_NAME.getTableName() + " " + SapOrgParam.BRANCH.getTableName() + "无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		logger.info("删除icustomer中现有的营业所......");
		this.getiCustomerJdbcOperations().update(delSql);

		List<Object[]> values = SapUtils.getUniqueMapValue(list, "VKBUR", "BEZEI");
		int [] inserts = getiCustomerJdbcOperations().batchUpdate(insertSql.toString(), values);
		logger.info("将从sap查询出来的营业所数据插入icustomer......" + inserts.length);
		
		String delSql1 = " delete from BRANCH_INFO_TBL ";
		String insertSql1 = " insert into branch_info_tbl select branch_id, branch_name from branch_sap";
		this.getiCustomerJdbcOperations().update(delSql1);
		this.getiCustomerJdbcOperations().update(insertSql1);

		logger.info("同步营业所完成");
	}

	/**
	 * 同步分公司数据
	 */
	private void syncCompany() {
		logger.info("开始同步分公司......");
		List list = sapDAO.getSAPData3(SapOrgParam.FUNC_NAME.getTableName(),
				SapOrgParam.COMPANY.getTableName());
		logger.info("分公司条数=" + list.size());

		if (list == null || list.size() == 0) {
			String msg = "读取 " + SapOrgParam.FUNC_NAME.getTableName() + " " + SapOrgParam.COMPANY.getTableName() + "无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		String delSql = " delete from company_sap ";
		String insertSql = " insert into company_sap (company_id,company_name,create_user,create_date) values (?,?,'sys',sysdate)";

		this.getiCustomerJdbcOperations().update(delSql);
		logger.info("删除icustomer中现有的分公司......");

		List<Object[]> values = SapUtils.getUniqueMapValue(list, "ZSD_WWCOMP", "ZSD_WWCOMP_NAME");
		for (Object[] value: values) {
			String key = (String) value[0];
			value[0] = key.replace("W", "C");
		}
		int [] inserts = getiCustomerJdbcOperations().batchUpdate(insertSql.toString(), values);
		logger.info("将从sap查询出来的分公司数据插入icustomer......" + inserts.length);
		logger.info("同步分公司完成");
	}

	/**
	 * 同步三级地－标准市场关系数据
	 */
	private void syncThirdMarketRel() {
		logger.info("开始同步三级地－标准市场关系......");
		List list = sapDAO.getSAPData3(SapOrgParam.FUNC_NAME.getTableName(),
				SapOrgParam.THIRD_MARKET.getTableName());
		logger.info("三级地－标准市场关系 条数=" + list.size());

		if (list == null || list.size() == 0) {
			String msg = "读取 " + SapOrgParam.FUNC_NAME.getTableName() + " " + SapOrgParam.THIRD_MARKET.getTableName() + "无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		String delSql = " delete from third_market_rel_sap ";
		String insertSql = " insert into third_market_rel_sap (third_id,market_id,market_rate,is_not_open,is_rough,create_user,create_date) values (?,?,?,?,?,'sys',sysdate)";

		logger.info("删除icustomer中现有的三级地－标准市场关系......");
		this.getiCustomerJdbcOperations().update(delSql);

		List<Object[]> values = SapUtils.getMapValue(list, "CITYP_CODE", "ZBZSC", "ZYYSC", "ZBKFSC", "ZSFCFX");
		int [] inserts = getiCustomerJdbcOperations().batchUpdate(insertSql.toString(), values);
		logger.info("将从sap查询出来的 三级地－标准市场关系 数据插入icustomer......" + inserts.length);
		logger.info("同步 三级地－标准市场关系 完成");
	}

	/**
	 * 同步标准市场－营业所关系
	 */
	private void syncMarketBranchRel() {
		logger.info("开始同步标准市场－营业所关系......");
		List list = sapDAO.getSAPData3(SapOrgParam.FUNC_NAME.getTableName(),
				SapOrgParam.MARKET_BRANCH.getTableName());
		logger.info("标准市场－营业所 条数=" + list.size());

		String delSql = " delete from market_branch_rel_sap ";
		String insertSql = " insert into market_branch_rel_sap (market_id,branch_id,create_user,create_date) values (?,?,'sys',sysdate)";

		if (list == null || list.size() == 0) {
			String msg = "读取 " + SapOrgParam.FUNC_NAME.getTableName() + " " + SapOrgParam.MARKET_BRANCH.getTableName() + "无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		this.getiCustomerJdbcOperations().update(delSql);
		logger.info("删除icustomer中现有的标准市场－营业所关系......");
		
		List<Object[]> values = SapUtils.getMapValue(list, "ZBZSC", "VKBUR");
		int [] inserts = getiCustomerJdbcOperations().batchUpdate(insertSql.toString(), values);
		logger.info("将从sap查询出来的 标准市场－营业所关系 数据插入icustomer......" + inserts.length);
		logger.info("同步 三级地－标准市场关系 完成");
	}

	/**
	 * 同步三级地－分公司关系数据
	 */
	private void syncThirdCompanyRel() {
		logger.info("开始同步三级地－分公司关系......");
		List list = sapDAO.getSAPData3(SapOrgParam.FUNC_NAME.getTableName(),
				SapOrgParam.THIRD_COMPANY.getTableName());
		logger.info("三级地－分公司 条数=" + list.size());

		if (list == null || list.size() == 0) {
			String msg = "读取 " + SapOrgParam.FUNC_NAME.getTableName() + " " + SapOrgParam.THIRD_COMPANY.getTableName() + "无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		String delSql = " delete from third_company_rel_sap ";
		String insertSql = " insert into third_company_rel_sap (third_id,company_id,create_user,create_date) values (?,?,'sys',sysdate)";

		this.getiCustomerJdbcOperations().update(delSql);
		logger.info("删除icustomer中现有的三级地－分公司关系......");

		List<Object[]> values = SapUtils.getMapValue(list, "CITYP_CODE", "ZSD_WWCOMP");
		for (Object[] value: values) {
			String key = (String) value[1];
			value[1] = key.replace("W", "C");
		}
		int [] inserts = getiCustomerJdbcOperations().batchUpdate(insertSql.toString(), values);
		logger.info("将从sap查询出来的 三级地－分公司关系 数据插入icustomer......" + inserts.length);

		/*
		Set<ThirdCompanyRel> tcSet = new HashSet<ThirdCompanyRel>();
		for (int i = 0; i < list.size(); i++) {
			Map<?, ?> map = (HashMap<?, ?>) list.get(i);
			String thirdId = (String) map.get("CITYP_CODE");
			String companyId = (String) map.get("ZSD_WWCOMP");
			companyId = companyId.replace("W", "C");// 把W转成C

			ThirdCompanyRel tc = new ThirdCompanyRel(thirdId, companyId);
			tcSet.add(tc);
		}
		logger.info("有效(去除重复后)三级地－分公司关系条数=" + tcSet.size());
		logger.info("将从sap查询出来的 三级地－分公司关系 数据插入icustomer......");
		Iterator<ThirdCompanyRel> itr = tcSet.iterator();
		while (itr.hasNext()) {
			ThirdCompanyRel tc = itr.next();

			this.getiCustomerJdbcOperations().update(insertSql.toString(),
					new Object[] { tc.getThirdId(), tc.getCompanyId() });
		}
		*/
		logger.info("同步 三级地－分公司关系 完成");
	}

	/**
	 * full_market_level_temp
	 */
	private void syncFullMarketLevelTempTbl() {
		logger.info("同步full_market_level_temp表......");
		String delSql = " delete from full_market_level_temp ";
		StringBuffer buf = new StringBuffer("");
		buf.append(
				"insert into full_market_level_temp(company_id,company_name,company_s_name")
				.append(",branch_id,branch_name,market_id,third_market,market_level,third_id,third_name,create_date)  ")

				.append(" select g.company_id,g.company_name,g.company_name,f.branch_id,f.branch_name")
				.append(",e.market_id,e.market_name,e.market_level,d.third_id,d.third_name,sysdate  ")
				.append(" from third_market_rel_sap a ")
				.append(" inner join market_branch_rel_sap b on a.market_id=b.market_id ")
				.append(" inner join third_company_rel_sap c on a.third_id=c.third_id ")
				.append(" inner join third_sap d on a.third_id=d.third_id ")
				.append(" inner join market_sap e on a.market_id=e.market_id ")
				.append(" inner join branch_sap f on b.branch_id=f.branch_id ")
				.append(" inner join company_sap g on c.company_id=g.company_id ");

		this.getiCustomerJdbcOperations().update(delSql);
		this.getiCustomerJdbcOperations().update(buf.toString());
		logger.info("同步full_market_level_temp表完成");
	}

	/**
	 * company_branch
	 */
	private void syncCompanyBranchTbl() {
		logger.info("同步company_branch表sap信息完成");
		String delSql = " delete from company_branch ";
		StringBuffer buf = new StringBuffer("");
		buf.append(
				"insert into company_branch(company_sap_id,company_name,branch_sap_id,branch_name)  ")

				.append("  select distinct e.company_id,e.company_name,a.branch_id,a.branch_name  ")
				.append(" from branch_sap a ")
				.append(" inner join market_branch_rel_sap b on a.branch_id=b.branch_id ")
				.append(" inner join third_market_rel_sap c on b.market_id=c.market_id ")
				.append(" inner join third_company_rel_sap d on c.third_id=d.third_id ")
				.append(" inner join company_sap e on d.company_id=e.company_id ");

		this.getiCustomerJdbcOperations().update(delSql);
		this.getiCustomerJdbcOperations().update(buf.toString());
		logger.info("同步company_branch表sap信息完成");
		
		logger.info("同步company_branch表HR信息");
		String selectSql1 = "select distinct company_name from company_branch ";
		String updateSql1 = " update company_branch set COMPANY_HR_ID = (select ORG_ID from TEMPORG.ORGANIZATION where ORG_NAME= ?) where COMPANY_NAME=? ";
		List<Map<String, Object>> companyList = this
				.getiCustomerJdbcOperations().queryForList(selectSql1);

		ArrayList<Object[]> params = new ArrayList<Object[]>();

		for (Map<String, Object> map : companyList) {
			String cName = map.get("company_name").toString();
			String org_name = cName;
			if (cName.endsWith("分"))
				org_name = cName + "公司";
			else
				org_name = cName + "分公司";
			params.add(new Object[] {org_name, cName });
		}
		int [] updates = getiCustomerJdbcOperations().batchUpdate(updateSql1, params);
		logger.info("更新 company_branch 笔树: " + updates.length);

		
		String selectSql2 = "select distinct BRANCH_NAME from company_branch";
		String updateSql2 = " update company_branch set BRANCH_HR_ID = (select ORG_ID from TEMPORG.ORGANIZATION where ORG_NAME= ?) where BRANCH_NAME=? ";
		List<Map<String, Object>> branchList = this
				.getiCustomerJdbcOperations().queryForList(selectSql2);
		params = new ArrayList<Object[]>();
		for (Map<String, Object> map : branchList) {
			String cName = map.get("BRANCH_NAME").toString();
			params.add(new Object[] {cName, cName });
		}
		updates = getiCustomerJdbcOperations().batchUpdate(updateSql2, params);
		logger.info("更新 company_branch 笔树: " + updates.length);
		
		logger.info("同步company_branch表HR信息完成");
	}
}
