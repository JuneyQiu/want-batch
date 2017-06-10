package com.want.batch.job.monitor;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;

@Component
public class DataArchiveJob extends AbstractWantJob {

	private static final Log logger = LogFactory.getLog(DataArchiveJob.class);

	@Override
	public void execute() throws SQLException {
		//deleteRetiredDailyReportData();
		moveData("daily_report");
		moveData("daily_order");
	}
	
	@Autowired SimpleJdbcOperations iCustomerJdbcOperations;

	private void deleteRetiredDailyReportData() throws SQLException {
		String selectSql = "select distinct r.dr_sid from DAILY_REPORT r, DELETED_SUBROUTE_SID d where r.dr_sid=d.dr_sid";
		String deleteSql = "delete from DAILY_REPORT nologging where dr_sid=?";
		
		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = conn.prepareStatement(selectSql);
		ResultSet rs = pstmt.executeQuery();
		logger.info("executed: " + selectSql);
		
		int batchSize = 1000;
		boolean hasMore = true;
		int pos = 0;
		
		while (hasMore) {
			ArrayList<Object[]> params = new ArrayList<Object[]>();
			for (int i=0; i<batchSize; i++) {
				if (rs.next()) {
					pos++;
					params.add(new Object[] { new Long(rs.getLong("dr_sid")) });
				} else {
					hasMore = false;
					break;
				}
			}
			if (params.size() > 0) {
				int[] rows = iCustomerJdbcOperations.batchUpdate(deleteSql, params);
				logger.info("scan to " + pos + ", deleted " + rows.length);
			}
		}
		
		rs.close();
		pstmt.close();
		conn.close();
	}
	
	private void moveData(String table) {
		Date sysCurrent = (Date) iCustomerJdbcOperations.queryForObject("select sysdate+1 from dual", Date.class);
		Date reportCurrent = (Date) iCustomerJdbcOperations.queryForObject("select max(create_date) from " + table, Date.class);
		logger.info("System current date: " + sysCurrent
				+ ", report latest date: " + reportCurrent);
		if (sysCurrent.before(reportCurrent))
			throw new WantBatchException("data date newer than system date");

		List<Map<String, Object>> dates = iCustomerJdbcOperations.queryForList(
						"select distinct to_char(create_date, 'yyyy-mm-dd') dd from " + table + " where create_date < (?-41) order by dd",
						reportCurrent);

		if (dates != null && dates.size() > 0) {
			for (Map d : dates) {
				String dd = (String) d.get("dd");
				logger.info("moving date " + table + " " + dd);
				
				List<Map<String, Object>> hours = iCustomerJdbcOperations.queryForList("select distinct to_char(create_date, 'yyyy-mm-dd hh24') hh from " 
						+ table + " where to_char(create_date, 'yyyy-mm-dd')=?", dd);
				for (Map<String, Object> hourMap: hours) {
					String dateString = (String) hourMap.get("hh");
					logger.info("moving hour "  + table + " " + dateString);
					int rows = iCustomerJdbcOperations.update("insert into " + table + "_his select * from " + table + " where to_char(create_date, 'yyyy-mm-dd hh24')= ?",
									dateString);
					if (rows > 0) {
						rows = iCustomerJdbcOperations.update("delete from " + table + " where to_char(create_date, 'yyyy-mm-dd hh24')= ?", dateString);
						logger.info("moved " + table + " " + dateString + ", rows " + rows);
					} else
						logger.info("no record moved for " + table + " " + dateString);
				}

				/*
				for (int i = 0; i < 24; i++) {
					String dateString = dd + " " + String.format("%02d", i);
					logger.info("moving hour "  + table + " " + dateString);
					int rows = iCustomerJdbcOperations.update("insert into " + table + "_his select * from " + table + " where to_char(create_date, 'yyyy-mm-dd hh24')= ?",
									dateString);
					if (rows > 0) {
						rows = iCustomerJdbcOperations.update("delete from " + table + " where to_char(create_date, 'yyyy-mm-dd hh24')= ?",
										dateString);
						logger.info("moved " + table + " " + dateString + ", rows " + rows);
					} else
						logger.info("no record moved for " + table + " " + dateString);
				}
				*/
			}
		} else
			logger.info("no data need to be moved");

	}
}
