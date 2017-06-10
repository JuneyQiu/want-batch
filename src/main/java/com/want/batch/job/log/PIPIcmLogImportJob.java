package com.want.batch.job.log;

import org.springframework.stereotype.Component;

import com.want.batch.job.utils.ProjectConfig;

@Component
public class PIPIcmLogImportJob extends IcmLogImportJob {

	private String table = "rptlog.ICM_ACCESS_LOG_PIP";

	@Override
	public void execute() throws Exception {
		executeAll(ProjectConfig.getInstance().getString("icm.log.path.pip"),
				table);
	}

	public void execute(String dateStr) throws Exception {
		executeOneDay(
				ProjectConfig.getInstance().getString("icm.log.path.pip"),
				table, dateStr);
	}
}
