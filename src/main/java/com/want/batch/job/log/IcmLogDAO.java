package com.want.batch.job.log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

@Component
public class IcmLogDAO {

	private static final Log logger = LogFactory.getLog(IcmLogDAO.class);

	@Autowired
	private SimpleJdbcOperations historyRptlogJdbcOperations;


	public void insertLog(String table, List logList) {
		String ISNERT_SQL = "insert into " + table + " (client_ip,server_ip,log_date,http_method,access_uri,status_code,"
				+ "response_time,bytes,referral) values(?,?,?,?,?,?,?,?,?) ";
		
		if (logList == null || logList.size() <= 0)
			return;

		ArrayList<Object[]> params = new ArrayList<Object[]>();

		for (int i = 0; i < logList.size(); i++) {
			IcmLog httpLog = (IcmLog) logList.get(i);
			Object[] row = new Object[] { httpLog.getClientIP(),
					httpLog.getServerIP(), httpLog.getIcmDate(),
					httpLog.getMethod(), httpLog.getUri(),
					httpLog.getResponseCode(), httpLog.getResponseTime(),
					httpLog.getSize(), httpLog.getReferral() };
			params.add(row);
		}
		historyRptlogJdbcOperations.batchUpdate(ISNERT_SQL, params);
		logger.info(params.size() + " inserted");
	}

	public Date getMaxDate(String table) {
		String sql = "select max(log_date) from " + table;
		Timestamp value = historyRptlogJdbcOperations.queryForObject(sql,
				Timestamp.class);
		if (value == null) 
			value = new Timestamp((new Date(100, 1, 1)).getTime());
		
		Date da = new Date();
		da.setTime(value.getTime());
		return da;
	}
	public int delete(String dateStr, String table) {
		String sql = "delete from " + table + " WHERE to_char(log_date, 'yyyy-mm-dd')='" + dateStr + "'";
		return historyRptlogJdbcOperations.update(sql);
	}
}
