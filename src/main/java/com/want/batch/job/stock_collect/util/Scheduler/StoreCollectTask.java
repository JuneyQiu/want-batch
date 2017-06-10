package com.want.batch.job.stock_collect.util.Scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component
public class StoreCollectTask extends AbstractWantJob {

	@Autowired
	RouteTask_StoreCollect_Company routeTask_StoreCollect_Company;

	@Autowired
	RunTask_StoreConllect_Branch1 runTask_StoreConllect_Branch1;
	
	@Override
	public void execute() throws Exception {
		runTask_StoreConllect_Branch1.execute();
		routeTask_StoreCollect_Company.execute();
	}

}
