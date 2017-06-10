package com.want.batch.job.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class RcLogImportJob extends AbstractWantJob {
	private String logPath;
	private String rcPath;

	@Autowired
	private RcLogDAO rcLogDAO;

	private static final Log logger = LogFactory.getLog(RcLogImportJob.class);

	@Override
	public void execute() throws Exception {
		logPath = ProjectConfig.getInstance().getString("rhoconnect.log.base");
		rcPath = ProjectConfig.getInstance().getString("rhoconnect.log.path");
		insertRCLog();
		//rcLogDAO.rebuild();
	}

	private List<ServerPath> getFiles(String config) {
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
	
	private List<ServerPath> getFiles(String config, Date date) {
		List<ServerPath> files = getFiles(config);
		List<ServerPath> results = new ArrayList<ServerPath>();
		SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMdd");

		String filePattern = "access.log-";
		String lastFileDate = filePattern + fileFormat.format(date);
		for (ServerPath sp: files) 
			if (sp.getFile().compareTo(lastFileDate) >= 0 
				&& sp.getFile().indexOf(filePattern) >= 0)
				results.add(sp);
		return results;
	}
	
	public void insertRCLog() throws ParseException {
		Date maxRCDate = rcLogDAO.getMaxRCDate();

		logger.info("max log date in the current Log DB:" + maxRCDate);

		List<ServerPath> list = getFiles(rcPath, maxRCDate);
		for (ServerPath sp : list)
			processLogFile(sp.getPath(), maxRCDate);
	}

	private static SimpleDateFormat format = new SimpleDateFormat(
			"dd/MMM/yyyy:HH:mm:ss", Locale.US);
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.US);
	private static SimpleDateFormat timeFormat = new SimpleDateFormat(
			"HH:mm:ss", Locale.US);
	
	private HttpLog parseLineFormatter(String str, Date maxDate) {
		HttpLog httpLog = null;

		if (str == null || str.contains(".gif") || str.contains(".js")
				|| str.contains(".css") || str.contains(".jpg")
				|| str.contains(".png"))
			return null;

		int logDateTimebeginIndex = str.indexOf("[");
		int logDateTimeEndIndex = str.indexOf("]");
		if ((str.indexOf("GET") != -1 || str.indexOf("POST") != -1)
				&& logDateTimebeginIndex != -1 && logDateTimeEndIndex != -1) {

			String logDateTime = str.substring(logDateTimebeginIndex,
					logDateTimeEndIndex);
			logDateTime = logDateTime.substring(1);

			String logDate = null;
			String logTime = null;
			try {
				Date date = format.parse(logDateTime);
				logDate = dateFormat.format(date);
				logTime = timeFormat.format(date);

				if (date != null && date.before(maxDate))
					return null;
			} catch (ParseException pe) {
				logger.error("ERROR: could not parse date in string \""
						+ logDateTime + "\"");
			}

			String[] items = str.split(" ");
			String clientIP = items[0];
			String httpMethod = items[5].split("\"")[1];
			String accessUril = items[6];
			String protocol = items[7];
			String status = items[8];
			String size = items[9];
			String responseTime = items[items.length - 1];

			if (accessUril.contains("/heartbeat")) {
				return null;
			} else {
				httpLog = new HttpLog();
				httpLog.setServerIP(rcPath);
				httpLog.setLogDate(logDate);
				httpLog.setLogTime(logTime);
				httpLog.setClientIP(clientIP);
				httpLog.setMethod(httpMethod);
				httpLog.setUri(accessUril);
				httpLog.setProtocol(protocol);
				httpLog.setResponseCode(status);
				httpLog.setResponseTime(responseTime);
				httpLog.setSize(size);
			}
		}

		return httpLog;
	}

	private void processLogFile(String fullPath, Date maxDate) {
		try {
			logger.info("Read " + fullPath + " ......");		
			BufferedReader br = new BufferedReader(new FileReader(fullPath));
			List<HttpLog> logs = new ArrayList<HttpLog>();
			int count = 0;
			String strLine;
			while ((strLine = br.readLine()) != null) {
				HttpLog httpLog = parseLineFormatter(strLine, maxDate);
				if (httpLog != null && httpLog.getClientIP() != null
						&& !httpLog.getClientIP().equals("10.0.1.191")) {
					logs.add(httpLog);
					count++;

					if (count > 2000) {
						try {
							rcLogDAO.insertLog(logs);
						} catch (Exception e) {
							logger.error("Error: " + e.getMessage());
						}
						count = 0;
						logs = new ArrayList<HttpLog>();
					}
				}
			}

			if (logs.size() > 0) {
				try {
					rcLogDAO.insertLog(logs);
				} catch (Exception e) {
					logger.error("Error: " + e.getMessage());
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error: " + e.getMessage());
		}
	}

}