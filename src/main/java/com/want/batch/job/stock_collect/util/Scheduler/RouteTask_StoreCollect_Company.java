package com.want.batch.job.stock_collect.util.Scheduler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.stock_collect.companymgr.bo.CompanyInfoBO;
import com.want.batch.job.stock_collect.companymgr.dao.CompanyDAO;
import com.want.batch.job.stock_collect.util.store.StoreCompanyParallelTask;

/**
 * 终端汇总(分公司)
 * <p>
 * Title: 每日运行
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: 旺旺集团
 * </p>
 * 
 * @author ajhr
 * @version 1.0
 */
@Component
public class RouteTask_StoreCollect_Company extends AbstractWantJob {

	// log4j
	static Logger logger = Logger
			.getLogger(RouteTask_StoreCollect_Company.class.getName());

	@Autowired
	public StoreCompanyParallelTask storeCompanyParallelTask;
	
	@Autowired
	public CompanyDAO companyDAO;
	
	private static final int CONCURRENT = 1;
	
	@Override
	public void execute() throws IOException, SQLException {
		logger.info("storelist one start company. BEGIN");
		ArrayList<CompanyInfoBO> companylist = companyDAO.getAllCompnay();
		ArrayList<Future<Boolean>> tasks = new ArrayList<Future<Boolean>>();
		logger.info("begin to wait thread exec....");
		
		for (CompanyInfoBO company: companylist) {
			boolean added = false;
			while (!added) {
				if (!added && tasks.size() < CONCURRENT) {
					Future<Boolean> task = storeCompanyParallelTask.run(company);
					tasks.add(task);
					added = true;
				} else {
					for (int i=0; i<tasks.size(); i++) {
						Future<Boolean> task = tasks.get(i);
						if (task.isDone()) {
							tasks.remove(i);
							break;
						}
					}
				}
				
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}			
			}
		}
		
		logger.info("storelist one start company. END");
	}
}
