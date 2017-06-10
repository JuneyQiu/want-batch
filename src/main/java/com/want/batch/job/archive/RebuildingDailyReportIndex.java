package com.want.batch.job.archive;

import java.util.List;

import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.utils.DBUtils;

@Component
public class RebuildingDailyReportIndex extends AbstractWantJob {

	@Override
	public void execute() {
		logger.info("rebuild daily report indexes... ");

		List<String> errorIndexNames = DBUtils.rebuildIndexesIgnoreException(
			getiCustomerJdbcOperations(), 
			"DAILY_REPORT_IDX01", 
			"DAILY_REPORT_IDX02", 
			"DAILY_REPORT_IDX03", 
			"DAILY_REPORT_IDX04", 
			"DAILY_REPORT_IDX06", 
			"DAILY_REPORT_PK"
		);
		if (errorIndexNames.size() > 0) {
			throw new WantBatchException("rebuild indexes error >>> " + errorIndexNames);
		}
		logger.info("rebuild daily report indexes end.");
	}

}
