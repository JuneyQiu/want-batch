package com.want.batch.job.log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.utils.ProjectConfig;

@Component
public class IcmLogImportJob extends AbstractWantJob {

	protected String logPath = ProjectConfig.getInstance().getString("icm.log.base");
	
	protected String excludeRegEx = ProjectConfig.getInstance().getString("icm.log.exclude");


	@Autowired
	protected IcmLogDAO icmLogDAO;

	private static final Log logger = LogFactory.getLog(IcmLogImportJob.class);

	protected static final SimpleDateFormat format = new SimpleDateFormat(
			"[dd/MMM/yyyy:hh:mm:ss", Locale.US);

	protected static final SimpleDateFormat fileFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	
	protected static Date OUT_RANGE_DATE = null;
	
	static {
		Calendar cal = Calendar.getInstance(); 
		cal.setTimeInMillis(System.currentTimeMillis());
		//cal.add(Calendar.YEAR, 1);
		OUT_RANGE_DATE = cal.getTime();
	}
	

	@Override
	public void execute() throws Exception {
		/*
		logPath = ProjectConfig.getInstance().getString("icm.log.base");
		icmPath = ProjectConfig.getInstance().getString("icm.log.path");
		excludeRegEx = ProjectConfig.getInstance().getString("icm.log.exclude");
		*/
		// insertICMLog();
		// nwLogDAO.rebuild();
	}

	protected List<ServerPath> getFiles(String config) {
		List<ServerPath> files = new ArrayList<ServerPath>(); 
		String[] cePaths = config.split(",");
		for (int i = 0; i < cePaths.length; i++) {
			String localPath = logPath + "/" + cePaths[i];
			String server = cePaths[i];
			File dir = new File(localPath);
			String[] children = dir.list();
			if (children != null) {
				for (int j = 0; j < children.length; j++) 
					files.add(new ServerPath(server, children[j], localPath + "/" + children[j]));
			}
		}
		
		return files;
	}
	
	protected List<ServerPath> getFiles(String config, Date date) {
		List<ServerPath> files = getFiles(config);
		List<ServerPath> results = new ArrayList<ServerPath>();
		String lastFileDate = "access_log-"
			+ fileFormat.format(date) + ".log";
		for (ServerPath sp: files) 
			if (sp.getFile().compareTo(lastFileDate) >= 0)
				results.add(sp);
		return results;
	}

	protected List<ServerPath> getFiles(String config, String dateStr) throws ParseException {
		Date date = fileFormat.parse(dateStr);
		List<ServerPath> files = getFiles(config);
		List<ServerPath> results = new ArrayList<ServerPath>();
		String lastFileDate = "access_log-"
			+ fileFormat.format(date) + ".log";
		for (ServerPath sp: files) 
			if (sp.getFile().compareTo(lastFileDate) == 0)
				results.add(sp);
		return results;
	}

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

	protected void executeOneDay(String config, String table, String dateStr) throws ParseException {
		int count = icmLogDAO.delete(dateStr, table);
		logger.info(config + " deleted " + table + ", " + dateStr + ": " + count);
		List<ServerPath> list = getFiles(config, dateStr);
		for (ServerPath sp : list)
			try {
				processLogFile(sp.getPath(), null, sp.getServer(), table);
			} catch (Exception e) {
				logger.error("process file " + sp.getPath() + e.getMessage());
			}
		logger.info(config + " for " + table + " on " + dateStr + " executed");
	}

	protected IcmLog parseLineFormatter(String str, Date maxDate, String apServer) {
		IcmLog icmLog = null;
		String[] items = str.split(" ");

		if (items.length < 13)
			return null;
		else if (items[6] != null && items[6].matches(excludeRegEx))
			return null;

		Date logDate;

		try {
			logDate = format.parse(items[0]);
			if (maxDate != null && logDate.before(maxDate) && logDate.after(OUT_RANGE_DATE))
				return null;
		} catch (Exception e) {
			logger.error("parse date exception: " + items[0]);
			return null;
		}

		String responseTime = items[10].substring(
				items[10].indexOf("total(ms):") + 10, items[10].length() - 1);
		responseTime = Double.toString(Double.parseDouble(responseTime) / 1000);

		icmLog = new IcmLog();
		
		if (apServer != null && apServer.length() < 100)
			icmLog.setServerIP(apServer);
		else {
			logger.error("Over length for apServer: " + apServer);
			return null;
		}
		
		icmLog.setIcmDate(new Timestamp(logDate.getTime()));
		
		if (items[3] != null && items[3].length() < 100)
			icmLog.setServerIP(apServer);
		else
			return null;
		
		icmLog.setClientIP(items[3]);
		icmLog.setMethod(items[5]);
		
		if (items[6] != null && items[6].length() < 1000)
			icmLog.setUri(items[6]);
		else {
			logger.error("Over length for uri: " + str);
			return null;
		}
			
		
		icmLog.setProtocol(items[7]);
		icmLog.setResponseCode(items[8]);
		icmLog.setResponseTime(responseTime);
		icmLog.setSize(items[9]);
		if (items.length >= 14) {
			if (items[13] != null && items[13].length() > 1000) {
				logger.error("Over length for referral: " + str);
				return null;
			} else
				icmLog.setReferral(items[13]);
		}

		return icmLog;
	}

	protected void processLogFile(String fullPath, Date maxDate, String apServer, String table)
			throws Exception {
		logger.info("Read " + fullPath + " ......");
		DataInputStream in = new DataInputStream(new FileInputStream(fullPath));
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		List<HttpLog> logs = new ArrayList<HttpLog>();
		int count = 0;
		String strLine;
		while ((strLine = br.readLine()) != null) {
			try {
				IcmLog icmLog = parseLineFormatter(strLine, maxDate, apServer);
				if (icmLog != null) {
					logs.add(icmLog);
					count++;

					if (count > 2000) {
						icmLogDAO.insertLog(table, logs);
						count = 0;
						logs = new ArrayList<HttpLog>();
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}

		if (logs.size() > 0)
			icmLogDAO.insertLog(table, logs);

		in.close();
	}
	
	
	public static void main(String args[]) {
		String a = "access_log-2015-05-15.log";
		String b = "access_log-2015-05-17.log";
		System.out.println(a.compareTo(b));
	}

}
