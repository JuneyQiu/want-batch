package com.want.batch.job.log;

import org.springframework.stereotype.Component;

import com.want.batch.job.utils.ProjectConfig;

@Component
public class EPPIcmLogImportJob extends IcmLogImportJob {

	private String table = "rptlog.ICM_ACCESS_LOG_EPP"; 

	@Override
	public void execute() throws Exception {
		executeAll(ProjectConfig.getInstance().getString("icm.log.path.epp"),
				table);
	}

	public void execute(String dateStr) throws Exception {
		executeOneDay(
				ProjectConfig.getInstance().getString("icm.log.path.epp"),
				table, dateStr);
	}
}
