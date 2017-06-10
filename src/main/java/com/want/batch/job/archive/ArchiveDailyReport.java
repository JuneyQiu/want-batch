package com.want.batch.job.archive;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component
public class ArchiveDailyReport extends AbstractWantJob {

	private int keepDays = 40;

	@Override
	public void execute() {

		String archiveDate = new DateTime().minusDays(keepDays).toString("yyyy-MM-dd HH:mm:ss");

		logger.info(String.format("Archive daily report before %s start...", archiveDate));

		// 查询需要 archive 数据的最大 sid
		long archiveMaxSid = this.getiCustomerJdbcOperations()
			.queryForLong("SELECT MAX(DR_SID) FROM DAILY_REPORT WHERE CREATE_DATE < SYSDATE - " + keepDays);
		logger.debug("archive max sid >>> " + archiveMaxSid);

		// 将需要 archive insert 至 DAILY_REPORT_HIS
		long updateCount = this.getiCustomerJdbcOperations()
			.update("INSERT INTO DAILY_REPORT_HIS SELECT * FROM DAILY_REPORT WHERE DR_SID < ?", archiveMaxSid);
		logger.debug("update count >>> " + updateCount);

		// 删除 DAILY_REPORT 中已经 archive 的数据
		long deleteCount = this.getiCustomerJdbcOperations()
			.update("DELETE FROM DAILY_REPORT WHERE DR_SID < ?", archiveMaxSid);

		logger.info(String.format("Archive daily report before %s end, %s items.", archiveDate, deleteCount));
	}

}
