package com.want.batch.job.log;

import org.springframework.stereotype.Component;

import com.want.batch.job.utils.ProjectConfig;

@Component
public class SRMIcmLogImportJob extends IcmLogImportJob {

	private String table = "rptlog.ICM_ACCESS_LOG_SRM";

	@Override
	public void execute() throws Exception {
		executeAll(ProjectConfig.getInstance().getString("icm.log.path.srm"),
				table);
	}

	public void execute(String dateStr) throws Exception {
		executeOneDay(
				ProjectConfig.getInstance().getString("icm.log.path.srm"),
				table, dateStr);
	}
}
