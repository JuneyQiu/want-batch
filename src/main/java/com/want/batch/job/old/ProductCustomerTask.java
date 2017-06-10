package com.want.batch.job.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.want.utils.SapDAO;
import com.want.utils.SapQuery;

@Component
public class ProductCustomerTask {

	private static final Log logger = LogFactory
			.getLog(ProductCustomerTask.class);

	private static final String sqlCleanBackup = "truncate table icustomer.PROD_CUSTOMER_REL_BAK";
	private static final String sqlBackup = "insert into icustomer.PROD_CUSTOMER_REL_BAK select * from icustomer.PROD_CUSTOMER_REL";

	private static final String insertSql = "insert into icustomer.PROD_CUSTOMER_REL_NEW (CUSTOMER_ID,COMPANY_ID,MATNR,MAKTX,NORMT,KBETR,KKBER,KKBTX,RECORD_DATE)"
			+ " values(?,?,?,?,?,?,?,?,to_char(sysdate+1, 'YYYY-MM-DD'))";

	@Autowired
	public SapDAO sapDAO;

	@Autowired
	private SimpleJdbcOperations iCustomerProdCustRelJdbcOperations;

	@Async
	public Future<Integer> update(String id) {
		int inserted = 0;
		HashMap querydata = new HashMap(5);

		ArrayList templ = new ArrayList();
		templ.add("BUKRS");
		templ.add(id);
		querydata.put("L_T_BUKRS", templ);
		
		try {
			SapQuery query = sapDAO.getSapQuery("ZRFC04_1", "ZST04", querydata);

			if (query.getNumRows() > 0) {
				ArrayList<Object[]> params = new ArrayList<Object[]>();
				int buffer = 2000;

				for (int j = 0; j < query.getNumRows(); j++) {
					query.setIndex(j);
					Object[] row = new Object[] { query.getString("KUNNR"), id,
							query.getString("MATNR"), query.getString("MAKTX"),
							query.getString("NORMT"), query.getString("KBETR"),
							query.getString("KKBER"), query.getString("KKBTX") };
					/*
					logger.debug(query.getString("KUNNR") + "," + id + ","
							+ query.getString("MATNR") + ","
							+ query.getString("MAKTX") + ","
							+ query.getString("NORMT") + ","
							+ query.getString("KBETR") + ","
							+ query.getString("KKBER") + ","
							+ query.getString("KKBTX"));
					*/
					params.add(row);
					if (params.size() == buffer || (j + 1) == query.getNumRows()) {
						iCustomerProdCustRelJdbcOperations.batchUpdate(insertSql, params); 
						params = new ArrayList<Object[]>();
					}
				}
				
				params.clear();
				Runtime.getRuntime().gc();
				inserted = query.getNumRows();
				logger.info(id + " 笔数 " + inserted);

			} else {
				logger.error("Read ZRC04_1 for " + id + " is empty");
			}
			
			query.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return new AsyncResult<Integer>(new Integer(inserted));
	}
	
	@Async
	public void backup() {
		logger.info("开始备份");
		iCustomerProdCustRelJdbcOperations.update(sqlCleanBackup);
		logger.info("完成: " + sqlCleanBackup);

		iCustomerProdCustRelJdbcOperations.update(sqlBackup);
		logger.info("完成: " + sqlBackup);
	}
}
