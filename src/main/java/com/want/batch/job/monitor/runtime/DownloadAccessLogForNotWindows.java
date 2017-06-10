package com.want.batch.job.monitor.runtime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;

import com.want.batch.job.AbstractWantJob;

public class DownloadAccessLogForNotWindows extends AbstractWantJob {

	private String serverIp;
	private int port;
	private String accessRootPath;
	private String ftpLoginUser;
	private String ftpLoginPassword;
	private String downloadAccessFileRootPath;
	private String accessLogFileNamePattern;
	private String tempDirectPrefix;
	private String tempDirect;

	@Override
	public void execute() {
		if (createTempDirect()) {
			FTPFileFilter accessLogFileFilter = new AccessLogFileFilter();
			downloadAccessFileFromFtp(accessLogFileFilter);
		}
	}

	private boolean createTempDirect() {

		this.tempDirect = this.tempDirectPrefix;
		
		File tempDirect = new File(String.format("%s\\%s", this.downloadAccessFileRootPath, this.tempDirect));
		
		if (!tempDirect.exists()) {
			tempDirect.mkdir();
		}
		
		logger.debug("temp direct >>> " + this.tempDirect);
		
		return true;
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

			FTPFile[] ftpFiles = ftpClient.listFiles(null, fileFilter);
			if (ftpFiles != null && ftpFiles.length > 0) {
				FTPFile ftpFile = ftpFiles[ftpFiles.length - 1];
				String ftpFileName = ftpFile.getName();
				logger.debug(ftpFileName);

				String localFileName = "bpm" + StringUtils.right(this.serverIp, 3) + "_" + this.port;
				File localFile = new File(String.format("%s\\%s\\%s", this.downloadAccessFileRootPath, this.tempDirect, localFileName));
				if (!localFile.exists()) {
					localFile.createNewFile();	
				}

				OutputStream os = null;
				try {
					os = new FileOutputStream(localFile);
					
					logger.info(String.format("Download source file %s start... ", ftpFileName));
					ftpClient.retrieveFile(ftpFileName, os);
				}
				finally {
					if (os != null) {
						os.close();
					}
				}

				logger.info(String.format("Download source file %s over[%s]. ", ftpFileName, localFileName));
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

	private class AccessLogFileFilter implements FTPFileFilter {

		@Override
		public boolean accept(FTPFile file) {
			String filename = file.getName();
			return isBpmAccessLog(filename);
		}

		private boolean isBpmAccessLog(String filename) {
			return filename.contains(accessLogFileNamePattern);
		}
	}

	public void setAccessLogFileNamePattern(String accessLogFileNamePattern) {
		this.accessLogFileNamePattern = accessLogFileNamePattern;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public void setPort(int port) {
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

	public void setTempDirectPrefix(String tempDirectPrefix) {
		this.tempDirectPrefix = tempDirectPrefix;
	}

}
