package com.want.batch.job.stock_collect.util.Scheduler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.stock_collect.branchmgr.dao.BranchDAO;
import com.want.batch.job.stock_collect.util.store.StoreBranchParallelTask;

@Component
public class RunTask_StoreConllect_Branch1 extends AbstractWantJob {

	// log4j
	static Logger logger = Logger.getLogger(RunTask_StoreConllect_Branch1.class
			.getName());
	
	@Autowired
	public BranchDAO branchDAO;
	
	@Autowired
	public StoreBranchParallelTask storeBranchParallelTask;
	
	private static final int CONCURRENT = 10;

	@Override
	public void execute() throws SQLException {
		logger.info("storelist one start branch. BEGIN");
		ArrayList<Integer> branchlist = branchDAO.getAllBranch();
		ArrayList<Future<Boolean>> tasks = new ArrayList<Future<Boolean>>();
		logger.info("begin to wait thread exec....");

		for (Integer sid: branchlist) {
			boolean added = false;
			while (!added) {
				if (!added && tasks.size() < CONCURRENT) {
					Future<Boolean> task = storeBranchParallelTask.run(sid);
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

		logger.info("storelist one start branch. END");
	}
}
