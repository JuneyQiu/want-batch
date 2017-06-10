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
public class Sfa2HisDataArchiveJob extends AbstractWantJob {
	
	private static final Log logger = LogFactory.getLog(Sfa2HisDataArchiveJob.class);

	@Autowired 
	private SimpleJdbcOperations sfa2JdbcOperations;

	@Autowired(required = false)
	private DataSource sfa2DataSource;
	
	@Override
	public void execute() throws SQLException {
		moveHistoryToArchive();
	}
	
	private void moveHistoryToArchive() {
		String moveToArcSql = "insert into daily_report_archive select * from daily_report_his where to_char(create_date, 'yyyy-mm') < to_char(add_months(sysdate, -1), 'yyyy-mm')";
		String moveToTmpSql = "insert into daily_report_his_tmp select * from daily_report_his where to_char(create_date, 'yyyy-mm') >= to_char(add_months(sysdate, -1), 'yyyy-mm')";
		String truncateSql = "truncate table daily_report_his"; 
		String switchSql = "alter table daily_report_his rename to daily_report_his_SW";
		String restoreSql = "alter table daily_report_his_tmp rename to daily_report_his";
		String restoreSwitchSql ="alter table daily_report_his_SW rename to daily_report_his_tmp";
		
		int moveToArcCount = sfa2JdbcOperations.update(moveToArcSql);
		logger.info(moveToArcCount + " affected: " + moveToArcSql);

		if (moveToArcCount > 0) {

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
			logger.info("DAILY_REPORT_HIS no data moved");
		}		
	}

	public SimpleJdbcOperations getSfa2JdbcOperations() {
		return sfa2JdbcOperations;
	}

	public void setSfa2JdbcOperations(SimpleJdbcOperations sfa2JdbcOperations) {
		this.sfa2JdbcOperations = sfa2JdbcOperations;
	}

	public DataSource getSfa2DataSource() {
		return sfa2DataSource;
	}

	public void setSfa2DataSource(DataSource sfa2DataSource) {
		this.sfa2DataSource = sfa2DataSource;
	}
}
