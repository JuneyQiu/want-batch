package com.want.batch.job.log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
public class NwLogImportJob extends AbstractWantJob {
	private String apServer;
	private String logPath;
	private String nwPath;

	@Autowired
	private NwLogDAO nwLogDAO;

	private static final Log logger = LogFactory.getLog(NwLogImportJob.class);

	@Override
	public void execute() throws Exception {
		logPath = ProjectConfig.getInstance().getString("nw.log.base");
		nwPath = ProjectConfig.getInstance().getString("nw.log.path");
		insertNWLog();
		// nwLogDAO.rebuild();
	}

	public void insertNWLog() {
		String maxEPPDate = nwLogDAO.getMaxEPPDate();
		String maxCEPDate = nwLogDAO.getMaxCEPDate();
		String maxIMCDate = nwLogDAO.getMaxIMCDate();
		String maxLSPDate = nwLogDAO.getMaxLSPDate();
		String maxEPIDate = nwLogDAO.getMaxEPIDate();

		logger.info("max log date in the current Log DB: EPP " + maxEPPDate
				+ ", CEP " + maxCEPDate + ", IMC " + maxIMCDate);

		String[] cePaths = nwPath.split(",");

		logger.info(nwPath + "ccccccccccc");
		for (int i = 0; i < cePaths.length; i++) {
			apServer = cePaths[i];
			String localPath = logPath + "/" + apServer;
			File dir = new File(localPath);
			String[] children = dir.list();
			logger.info("configration path:" + localPath + "true path:["
					+ dir.getPath() + "],[" + dir.getAbsolutePath() + "],["
					+ dir.getParent() + "],[" + dir.getName() + "]");
			logger.info("file size:" + children.length);
			if (children != null) {
				for (int j = 0; j < children.length; j++) {
					String filename = children[j];
					logger.info(filename + "lllllllllll");
					if (filename.indexOf(".trc") != -1) {
						String fullPath = localPath + "/" + filename;
						logger.info(fullPath + "hhhhhhhhhhhhh");
						if (fullPath.indexOf("EPP") >= 0)
							processLogFile(fullPath, maxEPPDate);
						else if (fullPath.indexOf("CEP") >= 0)
							processLogFile(fullPath, maxCEPDate);
						else if (fullPath.indexOf("IMC") >= 0)
							processLogFile(fullPath, maxIMCDate);
						else if (fullPath.indexOf("LSP") >= 0)
							processLogFile(fullPath, maxLSPDate);
						else if (fullPath.indexOf("EPI") >= 0)
							processLogFile(fullPath, maxEPIDate);
					}
				}
			}
		}

	}

	private HttpLog parseLineFormatter(String str, String maxDate) {
		HttpLog httpLog = null;
		String[] items = str.split(" ");

		if (str == null
				|| str.contains(".gif")
				|| str.contains(".js")
				|| str.contains(".css")
				|| str.contains(".jpg")
				|| str.contains(".png")
				|| str.contains("/sap.com~tc~bpem~him~uwlconn~provider~web/bpemuwlconn"))
			return null;

		int logDateTimebeginIndex = str.indexOf("[");
		int logDateTimeEndIndex = str.indexOf("]");
		if (items != null
				&& (str.indexOf("GET") != -1 || str.indexOf("POST") != -1)
				&& logDateTimebeginIndex != -1 && logDateTimeEndIndex != -1) {

			String logDateTime = str.substring(logDateTimebeginIndex,
					logDateTimeEndIndex);
			logDateTime = logDateTime.substring(1);

			String logDate = null;
			String logTime = null;

			try {
				Locale locale = Locale.US;
				SimpleDateFormat format = new SimpleDateFormat(
						"MMM dd,yyyy hh:mm:ss aa", locale);
				Date date = format.parse(logDateTime);
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd", locale);
				logDate = dateFormat.format(date);
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss",
						locale);
				logTime = timeFormat.format(date);

				String logDT = logDate + " " + logTime;

				if (logDate == null || logTime == null
						|| logDT.compareTo(maxDate) <= 0) {
					return null;
				}
			} catch (ParseException pe) {
				logger.error("ERROR: could not parse date in string \""
						+ logDateTime + "\"");
			}

			String clientIP = items[items.length - 8];
			String httpMethod = items[items.length - 6];
			String accessUril = items[items.length - 5];
			String protocol = items[items.length - 4];
			String status = items[items.length - 3];
			String size = items[items.length - 2];
			String responseTime = "";

			responseTime = items[items.length - 1];
			int beginIndex = responseTime.indexOf("[");
			int endIndex = responseTime.indexOf("]");
			if (beginIndex != -1 && endIndex != -1)
				responseTime = responseTime.substring(beginIndex + 1, endIndex);

			double d = 0;
			try {
				d = Double.parseDouble(responseTime) / 1000;
			} catch (Exception e) {
			}

			String value = String.valueOf(d);
			int index = value.indexOf(".");
			if (index != -1) {
				int end = index + 4;
				if (end > value.length())
					end = value.length();
				responseTime = value.substring(0, index)
						+ value.substring(index, end);
			}

			httpLog = new HttpLog();
			httpLog.setServerIP(apServer);
			httpLog.setLogDate(logDate);
			httpLog.setLogTime(logTime);
			httpLog.setClientIP(clientIP);
			httpLog.setMethod(httpMethod);
			httpLog.setUri(accessUril);
			httpLog.setProtocol(protocol);
			httpLog.setResponseCode(status);
			httpLog.setResponseTime(responseTime);
			httpLog.setSize(size);
			String uri = httpLog.getUri();
		}

		return httpLog;
	}

	private void processLogFile(String fullPath, String maxDate) {
		try {
			logger.info("Read " + fullPath + " ......");
			DataInputStream in = new DataInputStream(new FileInputStream(
					fullPath));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			List<HttpLog> logs = new ArrayList<HttpLog>();
			int count = 0;
			String strLine;
			while ((strLine = br.readLine()) != null) {
				HttpLog httpLog = parseLineFormatter(strLine, maxDate);
				if (httpLog != null) {
					logs.add(httpLog);
					count++;

					if (count > 2000) {
						nwLogDAO.insertLog(logs);
						count = 0;
						logs = new ArrayList<HttpLog>();
					}
				}
			}

			if (logs.size() > 0)
				nwLogDAO.insertLog(logs);

			in.close();
		} catch (Exception e) {
			logger.error("Error: " + e.getMessage());
		}
	}

}