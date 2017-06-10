package com.want.batch.job.archive;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.joda.time.DateTime;

import com.want.batch.job.AbstractWantJob;

public class DeleteAccessLogByServerForNotWindows extends AbstractWantJob {

	private String serverIp;
	private String serverName;
	private String port;
	private String accessRootPath;
	private String sourceAccessFileModifyDateFormatPattern;
	private String ftpLoginUser;
	private String ftpLoginPassword;
	private String downloadAccessFileRootPath;
	private String accessLogFileNamePattern;
	private int onceUpdateCount = 1000;
	private String splitPattern;
	private String[] columns;
	private Map<Integer, String> columnIndexMap = new HashMap<Integer, String>();
	private int accessLogFirstIndex = 0;
	private List<String> accessLogContains = new ArrayList<String>();
	private int maxResponseTime = 100;

	@Override
	public void execute() {

		FTPFileFilter accessLogFileFilter = new AccessLogFileFilter();
		downloadAccessFileFromFtp(accessLogFileFilter);

	}
	private void downloadAccessFileFromFtp(FTPFileFilter fileFilter) {

		logger.debug("login ftp ... ");

		FTPClient ftpClient = null;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(this.serverIp);
			ftpClient.login(this.ftpLoginUser, this.ftpLoginPassword);
			
			// 看是否登陆成功
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				ftpClient.disconnect();
				return;
			}

			logger.debug("ftp connect?" + ftpClient.isConnected());

			ftpClient.changeWorkingDirectory(this.accessRootPath);

			for (FTPFile ftpFile : ftpClient.listFiles(null, fileFilter)) {
				logger.debug(ftpFile.getName());
			}
			
			ftpClient.logout();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if (ftpClient != null && ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class AccessLogFileFilter implements FTPFileFilter, FileFilter {

		@Override
		public boolean accept(FTPFile file) {
			String filename = file.getName();
			DateTime modifyDatetime = new DateTime(file.getTimestamp());
			return isBpmAccessLog(filename) && modifyDatetimeLessCurrent(modifyDatetime);
		}

		@Override
		public boolean accept(File file) {
			String filename = file.getName();
			DateTime modifyDatetime = new DateTime(file.lastModified());
			return isWeblogicAccessLog(filename) && modifyDatetimeLessCurrent(modifyDatetime);
		}

		private boolean isBpmAccessLog(String filename) {
			return filename.contains(accessLogFileNamePattern);
		}

		private boolean isWeblogicAccessLog(String filename) {
			return filename.length() > accessLogFileNamePattern.length();
		}
		
		private boolean modifyDatetimeLessCurrent(DateTime modifyDatetime) {
			return Long.parseLong(modifyDatetime.toString(sourceAccessFileModifyDateFormatPattern)) <
				Long.parseLong(new DateTime().toString(sourceAccessFileModifyDateFormatPattern));
		}
	}

	public void setSourceAccessFileModifyDateFormatPattern(
			String sourceAccessFileModifyDateFormatPattern) {
		this.sourceAccessFileModifyDateFormatPattern = sourceAccessFileModifyDateFormatPattern;
	}

	public void setAccessLogFileNamePattern(String accessLogFileNamePattern) {
		this.accessLogFileNamePattern = accessLogFileNamePattern;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public void setAccessRootPath(String accessRootPath) {
		this.accessRootPath = accessRootPath;
	}
	public void setFtpLoginUser(String ftpLoginUser) {
		this.ftpLoginUser = ftpLoginUser;
	}
	public void setFtpLoginPassword(String ftpLoginPassword) {
		this.ftpLoginPassword = ftpLoginPassword;
	}
	public void setDownloadAccessFileRootPath(String downloadAccessFileRootPath) {
		this.downloadAccessFileRootPath = downloadAccessFileRootPath;
	}
	public void setOnceUpdateCount(int onceUpdateCount) {
		this.onceUpdateCount = onceUpdateCount;
	}
	public void setSplitPattern(String splitPattern) {
		this.splitPattern = splitPattern;
	}
	public void setColumns(String[] columns) {
		this.columns = columns;
	}
	public void setColumnIndexMap(Map<Integer, String> columnIndexMap) {
		this.columnIndexMap = columnIndexMap;
	}
	public void setAccessLogFirstIndex(int accessLogFirstIndex) {
		this.accessLogFirstIndex = accessLogFirstIndex;
	}
	public void setAccessLogContains(List<String> accessLogContains) {
		this.accessLogContains = accessLogContains;
	}
	public void setMaxResponseTime(int maxResponseTime) {
		this.maxResponseTime = maxResponseTime;
	}

}
