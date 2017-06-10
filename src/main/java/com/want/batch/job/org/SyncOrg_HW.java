package com.want.batch.job.org;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.org.pojo.Market;
import com.want.batch.job.org.pojo.ThirdBranchRel;

@Component
public class SyncOrg_HW extends AbstractWantJob {

	@Override
	public void execute() {
		syncMarketInfo();
		syncThirdInfo();
	}

	/**
	 * 合旺标准市场同步
	 */
	private void syncMarketInfo() {
		logger.debug("合旺标准市场同步排程开始......");

		// 1、查询出新增的标准市场
		List<Map<String, Object>> list = queryNewMarket();

		// 2、将新增的标准市场同步至hw09
		insertNewMarket(list);
		
		logger.debug("合旺标准市场同步排程结束......");
	}

	/**
	 * 合旺三级地同步
	 */
	private void syncThirdInfo() {
		logger.debug("合旺三级地同步排程开始......");

	

		// 1、修改变更的三级地名称
		updateThirdName();

		// 2、修改三级地归属的营业所
		updateThirdBranch();

		// 3、修改三级地归属的标准市场
		updateThirdMarket();
		
		// 4、同步新增的三级地
		insertNewThird();
		
		logger.debug("合旺三级地同步排程结束......");
	}

	/**
	 * 查询新增标准市场
	 * 
	 * @return List<Map<String, Object>>
	 */
	private List<Map<String, Object>> queryNewMarket() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT a.MARKET_NAME,a.MARKET_LEVEL,a.MARKET_ID  FROM  ICUSTOMER.MARKET_SAP a ");
		sql.append(" LEFT JOIN HW09.MARKET_INFO_TBL b ON a.market_name =b.market_name");
		sql.append(" WHERE b.MARKET_NAME IS NULL");
		List<Map<String, Object>> list = this.getHw09JdbcOperations()
				.queryForList(sql.toString());
		return list;
	}

	/**
	 * 将新增的标准市场同步至hw09
	 * 
	 * @param list
	 *            新增的标准市场
	 */
	private void insertNewMarket(List<Map<String, Object>> list) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO market_info_tbl values(?,?,'1','mis',sysdate,NULL,NULL, ? ,?)");
		for (Map<String, Object> map : list) {
			Market m = new Market();
			m.setMarketId(map.get("MARKET_ID").toString().trim());
			m.setMarketName(map.get("MARKET_NAME").toString().trim());
			m.setMarketLevel(map.get("MARKET_LEVEL").toString().trim());
		
			this.getHw09JdbcOperations().update(
					sql.toString(),
					new Object[] { getMaxSid(), m.getMarketName(),
							m.getMarketId(), m.getMarketLevel() });
		}
	}

	/**
	 * 获得当前hw09.market_info_tbl 最大sid的值
	 * 
	 * @return
	 */
	private String getMaxSid() {
		String maxSid = null;
		String sql = " select max(CAST(SID AS NUMERIC ))+1 as MAXSID from MARKET_INFO_TBL";
		List<Map<String, Object>> list = this.getHw09JdbcOperations()
				.queryForList(sql);
		for (Map<String, Object> map : list) {
			maxSid = map.get("MAXSID").toString().trim();
		}
		return maxSid;
	}

	/**
	 * 同步新增的三级地
	 */
	private void insertNewThird() {
		StringBuilder sql = new StringBuilder("insert into third_lv_info_tbl");
		sql.append(" select  THIRD_LV_INFO_TBL_SEQ.nextval, c.THIRD_ID, c.third_name, c.branch_sid, '1', ");
		sql.append(" 'mis', sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL, c.market_sid from (");
		sql.append(" SELECT a.THIRD_ID, SUBSTR(a.THIRD_NAME,5,LENGTH(a.third_name)-4) AS third_name,");
		sql.append(" bt.sid AS branch_sid ,mt.SID AS market_sid from  ICUSTOMER.FULL_MARKET_LEVEL_TEMP a ");
		sql.append(" INNER JOIN BRANCH_INFO_TBL bt on  bt.BRANCH_NAME=a.branch_name ");
		sql.append(" INNER JOIN MARKET_INFO_TBL mt on mt.MARKET_NAME=a.THIRD_MARKET ");
		sql.append(" LEFT JOIN HW09.THIRD_LV_INFO_TBL b ON SUBSTR(a.THIRD_NAME,5,LENGTH(a.third_name)-4)=b.THIRD_LV_NAME and b.branch_sid=bt.sid");
		sql.append(" WHERE bt.status='1' and  b.THIRD_LV_NAME IS NULL )  c");

		this.getHw09JdbcOperations().update(sql.toString());
	}

	/**
	 * 修改变更的三级地名称
	 * 
	 * 根据三级地的SAP编码，比对icustomer.full_market_level_temp 筛选出三级地名称不一致的三级地
	 */
	private void updateThirdName() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT  a.SID,a.THIRD_LV_ID,a.THIRD_LV_NAME,SUBSTR(b.THIRD_NAME,5,LENGTH(b.third_name)-4) as third_name");
		sql.append(" FROM    THIRD_LV_INFO_TBL a");
		sql.append(" INNER JOIN ICUSTOMER.FULL_MARKET_LEVEL_TEMP b ON a.THIRD_LV_ID = b.THIRD_ID");
		sql.append(" WHERE SUBSTR(b.THIRD_NAME,5,LENGTH(b.third_name)-4) !=a.THIRD_LV_NAME ");

		List<Map<String, Object>> updateNameList = this.getHw09JdbcOperations()
				.queryForList(sql.toString());
		List<ThirdBranchRel> list = new ArrayList<ThirdBranchRel>();
		for (Map<String, Object> map : updateNameList) {
			ThirdBranchRel rel = new ThirdBranchRel();
			rel.setThird_sap_id(map.get("THIRD_LV_ID").toString());
			rel.setThirdSid((BigDecimal) map.get("SID"));
			rel.setThirdName(map.get("third_name").toString());
			logger.debug("三级地：" + rel.getThirdSid() + ", 更新后的名称为：  "
					+ rel.getThirdName());
			list.add(rel);
		}

		String updateSql = "update THIRD_LV_INFO_TBL set THIRD_LV_NAME =? where SID= ?";
		for (ThirdBranchRel third : list) {
			this.getHw09JdbcOperations().update(updateSql,
					new Object[] { third.getThirdName(), third.getThirdSid() });
		}
	}

	/**
	 * 修改三级地变更的归属营业所
	 * 
	 * 根据三级地的SAP编码，连接icustomer.full_market_level_temp， 查询三级地现在归属的营业所名称
	 * name1，并根据查询出的营业所名称查询营业所sid
	 * 
	 * 根据三级地归属的营业所sid ，连接 branch_info_tbl 查询三级地原有的营业所名称name2
	 * 
	 * 筛选出name1不等于name2 的三级地
	 * 
	 * 
	 */
	private void updateThirdBranch() {
		StringBuilder sql1 = new StringBuilder();
		sql1.append(" SELECT  a.sid as third_sid,a.THIRD_LV_Name,c.sid as old_branch_sid, c.BRANCH_NAME as old_branch_name, ");
		sql1.append(" d.sid as new_branch_sid, b.BRANCH_NAME as new_branch_name FROM HW09.THIRD_LV_INFO_TBL a ");
		sql1.append(" INNER JOIN"
				+ " ICUSTOMER.FULL_MARKET_LEVEL_TEMP b ON a.THIRD_LV_ID=b.THIRD_ID");
		sql1.append(" INNER JOIN ( ");
		sql1.append(" SELECT DISTINCT a.sid, a.BRANCH_NAME FROM HW09.BRANCH_INFO_TBL a WHERE STATUS='1'");
		sql1.append("  ) c ON a.BRANCH_SID= c.sid");
		sql1.append(" INNER JOIN branch_info_tbl d ON d.BRANCH_NAME=b.BRANCH_NAME");
		sql1.append(" WHERE a.STATUS='1' and d.STATUS='1' and b.BRANCH_NAME != c.branch_name");

		List<Map<String, Object>> updateBranchList = this
				.getHw09JdbcOperations().queryForList(sql1.toString());

		List<ThirdBranchRel> list = new ArrayList<ThirdBranchRel>();
		for (Map<String, Object> map : updateBranchList) {
			ThirdBranchRel rel = new ThirdBranchRel();
			rel.setThirdSid((BigDecimal) map.get("third_sid"));
			rel.setThirdName(map.get("THIRD_LV_NAME").toString());
			rel.setBranchSid((BigDecimal) map.get("new_branch_sid"));
			rel.setBranchName(map.get("new_branch_name").toString());
			
			logger.debug("三级地名称： " + rel.getThirdName() + ",原营业所为："
					+ map.get("old_branch_name") + ",现营业所为："
					+ rel.getBranchName());
			
			list.add(rel);
		}

		String upadteSql = "update THIRD_LV_INFO_TBL set BRANCH_SID =? where SID= ?";
		for (ThirdBranchRel third : list) {
			this.getHw09JdbcOperations().update(upadteSql,
					new Object[] { third.getBranchSid(), third.getThirdSid() });
		}
	}

	/**
	 * 修改三级地归属的标准市场
	 * 
	 * 逻辑同更新三级地归属的营业所
	 */
	public void updateThirdMarket() {
		StringBuilder sql1 = new StringBuilder();
		sql1.append("SELECT  a.sid AS third_sid,a.THIRD_LV_Name, c.sid AS old_market_sid, c.MARKET_NAME as old_market_name,");
		sql1.append(" d.sid AS new_market_sid, b.THIRD_MARKET as new_market_name FROM  HW09.THIRD_LV_INFO_TBL a");
		sql1.append(" INNER JOIN ICUSTOMER.FULL_MARKET_LEVEL_TEMP b ON  a.THIRD_LV_ID=b.THIRD_ID");
		sql1.append(" INNER JOIN( ");
		sql1.append(" SELECT DISTINCT a.sid, a.MARKET_NAME FROM HW09.MARKET_INFO_TBL a");
		sql1.append(" ) c ON a.MARKET_SID= c.sid");
		sql1.append(" INNER JOIN MARKET_INFO_TBL d ON d.MARKET_NAME=b.THIRD_MARKET");
		sql1.append(" WHERE b.THIRD_MARKET != c.MARKET_NAME");

		List<Map<String, Object>> updateMarketList = this
				.getHw09JdbcOperations().queryForList(sql1.toString());
		List<ThirdBranchRel> list = new ArrayList<ThirdBranchRel>();
		for (Map<String, Object> map : updateMarketList) {
			ThirdBranchRel rel = new ThirdBranchRel();
			rel.setThirdSid((BigDecimal) map.get("third_sid"));
			rel.setThirdName(map.get("THIRD_LV_NAME").toString());
			rel.setMarketName(map.get("new_market_name").toString());
			rel.setMarketSid(map.get("new_market_sid").toString());

			logger.debug("三级地名称： " + rel.getThirdName() + ",原标准市场为："
					+ map.get("old_market_name") + ",现标准市场为："
					+ rel.getMarketName());

			list.add(rel);
		}

		String updateSql = " update THIRD_LV_INFO_TBL set MARKET_SID =? where SID= ? ";
		for (ThirdBranchRel third : list) {
			this.getHw09JdbcOperations().update(updateSql,
					new Object[] { third.getMarketSid(), third.getThirdSid() });
		}
	}

}
