package com.want.batch.job.basedata;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;
import com.want.utils.SapDAO;
import com.want.utils.SapQuery;

@Component
public class SyncKNVV extends AbstractWantJob {

	protected final Log logger = LogFactory.getLog(this.getClass());

	private static final String sqlcmd2 = "INSERT INTO  KNVV(ID,CUSTOMER_ID,COMPANY_ID,SALES_CHANNEL,PRODUCT,BRANCH_ID,CUSTOMER_GROUP1,CUSTOMER_GROUP2,"
			+ "CUSTOMER_GROUP3,CUSTOMER_GROUP4,CREATE_DATE)  "
			+ "VALUES(null,  ?,  ?,  ?,  ?,  ? ,  ?,  ?,  ?,  ? ,SYSDATE)";

	@Autowired
	public SapDAO sapDAO;

	@Override
	public void execute() throws Exception {
		
		HashMap querydata = new HashMap(9);
		SapQuery query = sapDAO.getSapQuery("ZRFC14_1", "ZST014", querydata);

		if (query.getNumRows() > 0) {
			
			String deletesqlcmd = "delete from KNVV";
			getiCustomerJdbcOperations().update(deletesqlcmd);
			logger.info("完成: " + deletesqlcmd);

			ArrayList<Object[]> params = new ArrayList<Object[]>();
			int buffer = 1000;

			for (int j = 0; j < query.getNumRows(); j++) {
				query.setIndex(j);
				
				String grp2 = query.getString("KVGR2");
				String grp3 = query.getString("KVGR3");
				String p7 = (grp2 == null || grp2.trim().equals("")) ? " " : grp2;
				String p8 = (grp3 == null || grp3.trim().equals("")) ? " " : grp3;
				params.add(new Object[] { query.getString("KUNNR"), query.getString("VKORG"),
						query.getString("VTWEG"), query.getString("SPART"),
						query.getString("VKBUR"), query.getString("KVGR1"), p7, p8,
						query.getString("KVGR4") });
				if (params.size() == buffer || (j + 1) == query.getNumRows()) {
					int[] inserts = getiCustomerJdbcOperations().batchUpdate(sqlcmd2,
							params);
					logger.info("新增笔数: " + inserts.length + ", " + sqlcmd2);
					params.clear();
					Runtime.getRuntime().gc();
					params = new ArrayList<Object[]>();
				}
			}
			
			logger.info(" 笔数 " + query.getNumRows());
			logger.info("max memory " + Runtime.getRuntime().maxMemory()
					+ ", remaining memory " + Runtime.getRuntime().freeMemory());
		
			query.close();
		
			final String[] expectCompanies = new String[] { "C301", "C161", "C151",
					"C331", "C261", "C171", "C511" };

			String sql = String.format(
					"DELETE FROM KNVV WHERE COMPANY_ID IN ('%s') ",
					StringUtils.join(expectCompanies, "','"));
			getiCustomerJdbcOperations().update(sql);
			logger.info("完成: " + sql);

			sql = "delete from CUSTOMER_BRANCH_TBL";
			getiCustomerJdbcOperations().update(sql);
			logger.info("完成: " + sql);

			sql = "insert into CUSTOMER_BRANCH_TBL SELECT  CUSTOMER_ID, BRANCH_ID FROM KNVV  GROUP BY CUSTOMER_ID, BRANCH_ID";
			getiCustomerJdbcOperations().update(sql);
			logger.info("完成: " + sql);

			sql = "delete from COMPANY_BRANCH_KNVV";
			getiCustomerJdbcOperations().update(sql);
			logger.info("完成: " + sql);

			sql = "insert into COMPANY_BRANCH_KNVV SELECT COMPANY_ID, BRANCH_ID FROM KNVV WHERE BRANCH_ID NOT LIKE '%00' GROUP BY COMPANY_ID, BRANCH_ID ";
			getiCustomerJdbcOperations().update(sql);
			logger.info("完成: " + sql);
		
		}
		
		
		/*
		
		HashMap querydata = new HashMap(9);
		ArrayList list = sapDAO.getSAPData("ZRFC14_1", "ZST014", querydata);
		logger.info("ZST014 list:" + list.size());

		if (list == null || list.size() == 0) {
			String msg = "读取 ZRFC14_1 ZST014 无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		String deletesqlcmd = "delete from KNVV";
		getiCustomerJdbcOperations().update(deletesqlcmd);
		logger.info("完成: " + deletesqlcmd);

		String sqlcmd2 = "INSERT INTO  KNVV(ID,CUSTOMER_ID,COMPANY_ID,SALES_CHANNEL,PRODUCT,BRANCH_ID,CUSTOMER_GROUP1,CUSTOMER_GROUP2,"
				+ "CUSTOMER_GROUP3,CUSTOMER_GROUP4,CREATE_DATE)  "
				+ "VALUES(null,  ?,  ?,  ?,  ?,  ? ,  ?,  ?,  ?,  ? ,SYSDATE)";
		ArrayList<Object[]> params = new ArrayList<Object[]>();

		for (int l = 0; l < list.size(); l++) {
			HashMap temphm = (HashMap) list.get(l);
			String grp2 = (String) temphm.get("KVGR2");
			String grp3 = (String) temphm.get("KVGR3");
			String p7 = (grp2 == null || grp2.trim().equals("")) ? " " : grp2;
			String p8 = (grp3 == null || grp3.trim().equals("")) ? " " : grp3;
			params.add(new Object[] { temphm.get("KUNNR"), temphm.get("VKORG"),
					temphm.get("VTWEG"), temphm.get("SPART"),
					temphm.get("VKBUR"), temphm.get("KVGR1"), p7, p8,
					temphm.get("KVGR4") });
		}
		int[] inserts = getiCustomerJdbcOperations().batchUpdate(sqlcmd2,
				params);
		logger.info("新增笔数: " + inserts.length + ", " + sqlcmd2);
		*/
			
			
	}
}
