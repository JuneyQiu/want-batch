package com.want.batch.job.log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.want.batch.job.utils.ProjectConfig;

@Component
public class PLMLogImportJob extends IcmLogImportJob {

	private static final Log logger = LogFactory.getLog(PLMLogImportJob.class);

	private String table = "rptlog.ICM_ACCESS_LOG_PLM";

	protected static final SimpleDateFormat fileFormat = new SimpleDateFormat(
			"yyMMdd");

	protected static final SimpleDateFormat format = new SimpleDateFormat(
			"MM/dd/yyyy hh:mm:ss", Locale.US);
	@Override
	public void execute() throws Exception {
		executeAll(ProjectConfig.getInstance().getString("icm.log.path.plm"),
				table);
	}

	@Override
	protected void executeAll(String config, String table) {
		Date date = icmLogDAO.getMaxDate(table);
		List<ServerPath> list = getFiles(config, date);
		for (ServerPath sp : list)
			try {
				processLogFile(sp.getPath(), date, sp.getServer(), table);
			} catch (Exception e) {
				logger.error("process file " + sp.getPath() + e.getMessage());
			}
		logger.info(config + " for " + table + " all executed");
	}

	@Override
	protected List<ServerPath> getFiles(String config, Date date) {
		List<ServerPath> files = getFiles(config);
		List<ServerPath> results = new ArrayList<ServerPath>();
		String lastFileDate = "u_in" + fileFormat.format(date) + ".log";
		for (ServerPath sp : files)
			if (sp.getFile().compareTo(lastFileDate) >= 0)
				results.add(sp);
		return results;
	}
	
	@Override
	protected IcmLog parseLineFormatter(String str, Date maxDate, String apServer) {
		String[] items = str.split(",");
		IcmLog icmLog = new IcmLog();

		if (items.length < 15) {
			logger.error("token less than 15, actual: " + items.length);
			return null;
		}
		
		if (items[13] == null || items[13].matches(excludeRegEx)) 
			return null;
		else
			icmLog.setUri(items[13]);
		
		if (items[14] != null)
			icmLog.setReferral(items[14]);


		Date logDate;

		try {
			logDate = format.parse(items[2] + " " + items[3]);
			if (maxDate != null && logDate.before(maxDate) && logDate.after(OUT_RANGE_DATE))
				return null;
			icmLog.setIcmDate(new Timestamp(logDate.getTime()));
		} catch (Exception e) {
			logger.error("parse date exception: " + items[2] + " " + items[3]);
			return null;
		}

		icmLog.setResponseTime(Double.toString(Double.parseDouble(items[7]) / 1000));
		icmLog.setServerIP(apServer);		
		icmLog.setClientIP(items[0]);
		icmLog.setMethod(items[12]);
		icmLog.setSize(items[9]);
		icmLog.setResponseCode(items[10]);
		//if (items.length >=14)
		//icmLog.setReferral(items[14]);
		return icmLog;
	}

	public static void main(String args[]) {
		SimpleDateFormat format = new SimpleDateFormat(
				"MM/dd/yyyy hh:mm:ss");
		try {
			Date date = format.parse("5/26/2015 14:21:48");
			System.out.println(date);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
