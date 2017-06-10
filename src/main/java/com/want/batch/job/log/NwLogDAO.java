package com.want.batch.job.log;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.utils.DBUtils;

@Component
public class NwLogDAO {

	private static final Log logger = LogFactory.getLog(NwLogDAO.class);

	@Autowired
	private SimpleJdbcOperations historyRptlogJdbcOperations;

	private static final String ISNERT_SQL = "insert into rptlog.nw_access_log(client_ip,server_ip,log_date,log_time,http_method,access_uri,status_code,"
		+ "response_time,bytes) values(?,?,?,?,?,?,?,?,?) ";

	public void insertLog(List logList) {
		if (logList == null || logList.size() <= 0)
			return;
		
		ArrayList<Object[]> params = new ArrayList<Object[]>();

		for (int i = 0; i < logList.size(); i++) {
			HttpLog httpLog = (HttpLog) logList.get(i);
			Object[] row = new Object[] { httpLog.getClientIP(),
					httpLog.getServerIP(), httpLog.getLogDate(),
					httpLog.getLogTime(), httpLog.getMethod(),
					httpLog.getUri(), httpLog.getResponseCode(),
					httpLog.getResponseTime(), httpLog.getSize() };
			params.add(row);
		}
		historyRptlogJdbcOperations.batchUpdate(ISNERT_SQL, params);
		logger.info(params.size() + " inserted");
	}

	public String getMaxEPPDate() {
		String sql = "select max(log_date || ' ' || log_time) from rptlog.nw_access_log where server_ip like 'EP/EPP%' ";		
		String value = historyRptlogJdbcOperations.queryForObject(sql, String.class);
		if (value == null)
			value = "2013-05-10 00:10:00";
		return value;
	}

	public String getMaxCEPDate() {
		String sql = "select max(log_date || ' ' || log_time) from rptlog.nw_access_log where server_ip like 'EP/CEP%' ";
		String value = historyRptlogJdbcOperations.queryForObject(sql, String.class);
		if (value == null)
			value = "2013-05-10 00:10:00";
		return value;
	}

	public String getMaxIMCDate() {
		String sql = "select max(log_date || ' ' || log_time) from rptlog.nw_access_log where server_ip like 'EP/IMC%' ";
		String value = historyRptlogJdbcOperations.queryForObject(sql, String.class);
		if (value == null)
			value = "2013-05-10 00:10:00";
		return value;
	}

	public String getMaxLSPDate() {
		String sql = "select max(log_date || ' ' || log_time) from rptlog.nw_access_log where server_ip like 'EP/LSP%' ";
		String value = historyRptlogJdbcOperations.queryForObject(sql, String.class);
		if (value == null)
			value = "2013-05-10 00:10:00";
		return value;
	}

	public String getMaxEPIDate() {
		String sql = "select max(log_date || ' ' || log_time) from rptlog.nw_access_log where server_ip like 'EP/EPI%' ";
		String value = historyRptlogJdbcOperations.queryForObject(sql, String.class);
		if (value == null)
			value = "2013-05-10 00:10:00";
		return value;
	}

	/*
	public String getMaxNWDate() {
		String sql = "select max(log_date || ' ' || log_time) from rptlog.nw_access_log ";
		String value = historyRptlogJdbcOperations.queryForObject(sql, String.class);
		if (value == null)
			value = "2013-05-10 00:10:00";
		return value;
	}
	*/
	
//	public void rebuild() {
//		DBUtils.rebuildTableStringDate(historyRptlogJdbcOperations, "NW_ACCESS_LOG", "NW_ACCESS_LOG_TMP", "LOG_DATE", 3, 3);
//	}
}
