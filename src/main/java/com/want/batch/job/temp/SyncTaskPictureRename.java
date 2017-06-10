package com.want.batch.job.temp;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component
public class SyncTaskPictureRename extends AbstractWantJob {

	private int syncDays = 90;
	private int onceSyncCount = 1000;
	
	@Override
	public void execute() {
		
		if (syncDays < 0) {
			return;
		}
		
		DateTime beginDate = new DateTime()
			.minusDays(syncDays)
			.withHourOfDay(0)
			.withMinuteOfHour(0)
			.withSecondOfMinute(0)
			.withMillisOfSecond(0);

		String countSql = new StringBuilder()
			.append("SELECT count(1) ")
			.append("FROM TASK_PICTURE ")
			.append("WHERE CREATE_DATE > ? ")
			.append("  AND CREATE_DATE < TO_DATE('20111213', 'YYYYMMDD') ")
			.append("  AND SID NOT IN (SELECT SID FROM TASK_PICTURE_RENAME) ")
			.append("  AND BACKUP IS NOT NULL ")
			.toString();

		String sizeSql = "SELECT SUM(LENGTH(CONTENT)) FROM TASK_PICTURE_RENAME";
		
		String sql = new StringBuilder()
			.append("INSERT INTO TASK_PICTURE_RENAME ")
			.append("SELECT * ")
			.append("FROM TASK_PICTURE ")
			.append("WHERE CREATE_DATE > ? ")
			.append("  AND CREATE_DATE < TO_DATE('20111213', 'YYYYMMDD') ")
			.append("  AND SID NOT IN (SELECT SID FROM TASK_PICTURE_RENAME) ")
			.append("  AND BACKUP IS NOT NULL ")
			.append("  AND ROWNUM <= ? ")
			.toString();

		int syncAll = this.getiCustomerJdbcOperations().queryForInt(countSql, beginDate.toDate());
		int syncCount = this.getiCustomerJdbcOperations().update(sql, beginDate.toDate(), onceSyncCount);
		int syncSum = syncCount;
		int initSize = 0;
		
		while (syncCount > 0) {
			long size = this.getiCustomerJdbcOperations().queryForLong(sizeSql) / (1024 * 1024);
			String message = String.format("sync task_picture_rename >>> [%s / %s / %s], size:%sM(%sG)", 
				syncCount, syncSum, syncAll, size, 
				BigDecimal.valueOf(size).divide(BigDecimal.valueOf(1024), 2, RoundingMode.HALF_UP));
			logger.info(message);
			
			if (size > initSize) {
				this.getMailService()
					.to("song_wenlei@want-want.com", "Yang_Weilei@want-want.com")
					.subject(String.format("图片移转进度: %s / %s; Size : %sM(%sG).", syncSum, syncAll, size, 
						BigDecimal.valueOf(size).divide(BigDecimal.valueOf(1024), 2, RoundingMode.HALF_UP)))
					.content("如题~~~")
					.send();
				initSize += 5120;
			}
			
			syncCount = this.getiCustomerJdbcOperations().update(sql, beginDate.toDate(), onceSyncCount);
			syncSum += syncCount;
		}
	}

	public void setSyncDays(int syncDays) {
		this.syncDays = syncDays;
	}

}
