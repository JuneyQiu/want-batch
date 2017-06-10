package com.want.batch.job.monitor;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component
public class Sfa2DataArchiveJob extends AbstractWantJob {

	private static final Log logger = LogFactory.getLog(Sfa2DataArchiveJob.class);

	@Autowired 
	private SimpleJdbcOperations sfa2JdbcOperations;
	
	@Autowired(required = false)
	private DataSource sfa2DataSource;
	
	@Override
	public void execute() throws SQLException {
		moveToHistory();
	}
	
	private void moveToHistory() {
		String moveToHisSql = "insert into daily_report_his select * from daily_report where to_char(create_date, 'yyyy-mm') < to_char(sysdate, 'yyyy-mm')";
		String moveToTmpSql = "insert into daily_report_tmp select * from daily_report where to_char(create_date, 'yyyy-mm') >= to_char(sysdate, 'yyyy-mm')";
		String truncateSql = "truncate table daily_report"; 
		String switchSql = "alter table daily_report rename to daily_report_SW";
		String restoreSql = "alter table daily_report_tmp rename to daily_report";
		String restoreSwitchSql ="alter table daily_report_SW rename to daily_report_tmp";
		
		int moveToHisCount = sfa2JdbcOperations.update(moveToHisSql);
		logger.info(moveToHisCount + " affected: " + moveToHisSql);

		if (moveToHisCount > 0) {

			int moveToTmpCount = sfa2JdbcOperations.update(moveToTmpSql);
			logger.info(moveToTmpCount + " affected: " + moveToTmpSql);
			
			sfa2JdbcOperations.update(truncateSql);
			logger.info(truncateSql);
			
			sfa2JdbcOperations.update(switchSql);
			logger.info(switchSql);
			
			sfa2JdbcOperations.update(restoreSql);
			logger.info(restoreSql);

			sfa2JdbcOperations.update(restoreSwitchSql);
			logger.info(restoreSwitchSql);

		} else {
			logger.info("DAILY_REPORT no data moved");
		}		
	}

}
