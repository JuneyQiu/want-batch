package com.want.batch.job.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component
public class SyncProductCustomerNW extends AbstractWantJob {

	private static final String sqlClear = "truncate table CUSTOMER_PRODUCT_REL_NEW";
	private static final String sqlcmd = "select company_id from company_info_tbl where company_id like 'C%' order by company_id";

	private static final String sqlCleanActive = "truncate table CUSTOMER_PRODUCT_REL";
	private static final String sqlActivate = "insert into CUSTOMER_PRODUCT_REL select * from CUSTOMER_PRODUCT_REL_NEW";

	private int threadSize = 0;
	private int total = 0;
	
	private static int THREAD_DELAY_PERIOD = 5000;
	
	@Autowired
	public ProductCustomerNWTask productCustomerTask;

	@Autowired
	private SimpleJdbcOperations iCustomerNWJdbcOperations;

	@Override
	public void execute() throws SQLException {
		logger.info("可销品项同步开始:");
		clear();

		productCustomerTask.backup();

		List<String> companyList = getCompanyList();
		this.threadSize = companyList.size();

		logger.info("同步 " + threadSize + " 分公司");

		ArrayList<Future<Integer>> tasks = new ArrayList<Future<Integer>>();
		for (int i = 0; i < companyList.size(); i++) {
			String companyId = (String) companyList.get(i);
			Future<Integer> task = productCustomerTask.update(companyId);
			tasks.add(task);
		}

		logger.info("begin to wait thread exec....");
		boolean completed = false;

		while (!completed) {
			completed = true;
			int inserts = 0;
			int companies = 0;
			for (Future<Integer> task: tasks)
				if (!task.isDone()) {
					completed = false;
				} else {
					Integer value;
					try {
						value = task.get();
						inserts += value.intValue();
						companies ++;
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
			
			try {
				logger.info("完成 " + companies + "家分公司 , 总笔树 " + inserts);
				logger.info("max ram " + Runtime.getRuntime().maxMemory()
						+ ", free ram " + Runtime.getRuntime().freeMemory());
				Thread.sleep(THREAD_DELAY_PERIOD);
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}			
		}

		activate();
	}

	public void activate() {
		Date starttime = new Date(System.currentTimeMillis());
		logger.info("开始启动:" + starttime);

		iCustomerNWJdbcOperations.update(sqlCleanActive);
		logger.info("完成: " + sqlCleanActive);

		// 将 CUSTOMER_PRODUCT_REL_NEW 备份到 CUSTOMER_PRODUCT_REL
		int inserted = iCustomerNWJdbcOperations.update(sqlActivate);
		logger.info("完成: " + inserted + ", " + sqlActivate);

		starttime = new Date(System.currentTimeMillis());
		logger.info("结束启动:" + starttime + ", 总笔数 " + total);
	}

	public void clear() {
		iCustomerNWJdbcOperations.update(sqlClear);
		logger.info("完成: " + sqlClear);
	}

	public List<String> getCompanyList() throws SQLException {
		List<String> companyList = new ArrayList<String>();
		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
			companyList.add(rs.getString(1));
		rs.close();
		pstmt.close();
		conn.close();
		return companyList;
	}	
}