package com.want.batch.job.temp;

import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component
public class DailyReportP2M extends AbstractWantJob {

	@Override
	public void execute() {

		long condition = 1338818429L;
		long intervel = 10000000L;

		while (condition < 1492861483L) {
			logger.info("Update P2M count >>> " + 
				this.getiCustomerJdbcOperations()
					.update(" update DAILY_REPORT set LV_5_ID = replace(LV_5_ID, 'P', 'M') where dr_sid between ? and ? "
						, condition, condition += intervel));
		}
	}

}
