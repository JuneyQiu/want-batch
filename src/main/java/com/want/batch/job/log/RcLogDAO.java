package com.want.batch.job.log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.utils.DBUtils;

@Component
public class RcLogDAO {

	private static final Log logger = LogFactory.getLog(RcLogDAO.class);

	@Autowired
	private SimpleJdbcOperations historyRptlogJdbcOperations;

	private static final String ISNERT_SQL = "insert into rptlog.RC_ACCESS_LOG_TMP (client_ip,server_ip,log_date,log_time,http_method,access_uri,status_code,"
			+ "response_time,bytes) values(?,?,?,?,?,?,?,?,?) ";

	public void insertLog(List logList) {
		if (logList == null || logList.size() <= 0)
			return;

		ArrayList<Object[]> params = new ArrayList<Object[]>();

		for (int i = 0; i < logList.size(); i++) {
			HttpLog httpLog = (HttpLog) logList.get(i);
			String cIP = httpLog.getClientIP();
			String sIP = httpLog.getServerIP();
			String ld = httpLog.getLogDate();
			String lt = httpLog.getLogTime();
			String m = httpLog.getMethod();
			String u = httpLog.getUri();
			String rc = httpLog.getResponseCode();
			String rt = httpLog.getResponseTime();
			String s = httpLog.getSize();

			Object[] row = new Object[] { cIP == null ? "" : cIP,
					sIP == null ? "" : sIP, ld == null ? "" : ld,
					lt == null ? "" : lt, m == null ? "" : m,
					u == null ? "" : u, rc == null ? "" : rc,
					rt == null ? "" : rt, s == null ? "" : s };
			params.add(row);
		}
		historyRptlogJdbcOperations.batchUpdate(ISNERT_SQL, params);
		logger.info(params.size() + " inserted");
	}

	public Date getMaxRCDate() throws ParseException {
		String sql = "select max(log_date || ' ' || log_time) from rptlog.RC_ACCESS_LOG_TMP ";
		String value = historyRptlogJdbcOperations.queryForObject(sql, String.class);
		SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (value == null)
			value = "2013-05-10 00:10:00";
		return fileFormat.parse(value);
	}
	
	
	public void rebuild() {
		DBUtils.rebuildTableStringDate(historyRptlogJdbcOperations, "RC_ACCESS_LOG", "RC_ACCESS_LOG_TMP", "LOG_DATE", 7, 3);
	}
}
