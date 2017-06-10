package com.want.batch.job.monitor;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.want.batch.job.AbstractWantJob;

/**
 * 
 * Access log 命名原则：<p>
 * 1. 导入前：_access_ip_port_date_time.log <p>
 * 2. 导入后：datetime_access_ip_port_date_time.log<p>
 * 
 * 导入规则：
 *   1. 找到所有 _access_ip_port_date_time.log
 *   2. 导入后将导入文件 rename : datetime_access_ip_port_date_time.log
 * 
 * 
 * 
 * @author 00079241
 *
 */
public class ProdAccessLogToDBJob extends AbstractWantJob {

	private static final String DOWNLOAD_LOCAL_TRUE = "1";
	private static final String IMPORT_DB_TRUE = "1";
	
	private boolean ftp = true;
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
	

	@Autowired(required = false)
	@Qualifier("historyRptlogJdbcOperations")
	private SimpleJdbcOperations historyRptlogJdbcOperations;
	
	@Override
	public void execute() {
		String date = this.namePath();
		FTPFileFilter accessLogFileFilter = new AccessLogFileFilter();

		this.downloadAccessFile(accessLogFileFilter,date);

		this.writeDataToDB(date);
	}

	@SuppressWarnings("unchecked")
	private void writeDataToDB(String date) {
		
		// 准备写入db的语句
		String insertAccessLogSql = new StringBuilder()
			.append("INSERT INTO RPTLOG.BPM_ACCESS_LOG ( ")
			.append("  CLIENT_IP, SERVER_IP, LOG_DATE, LOG_TIME, ")
			.append("  HTTP_METHOD, ACCESS_URI, STATUS_CODE, RESPONSE_TIME, BYTES )")
			.append("VALUES (")
			.append("  :clientIp, :serverIp, :date, :time, ")
			.append("  :httpMethod, :uri, :statusCode, :responseTime, :bytes )")
			.toString();
		
		// 准备更新完成导入文件状态的语句
		String updateAccessLogFileInfoImportDBSql = new StringBuilder()
			.append("UPDATE RPTLOG.BPM_ACCESS_LOG_FILE_INFO ")
			.append("SET IMPORT_DB = ?")
			.append("WHERE LOCAL_FILE_NAME = ? ")
			.toString(); 

		// 读取需要写入db的文件名称
		String readFileNamesSql = new StringBuilder()
			.append("SELECT LOCAL_FILE_NAME ")
			.append("FROM RPTLOG.BPM_ACCESS_LOG_FILE_INFO ")
			.append("WHERE SERVER_IP = ? AND SERVER_NAME = ? AND PORT = ? AND IMPORT_DB IS NULL ")

			// 保留此句，用于之后的测试
			//.append("  AND LOCAL_FILE_NAME = '_access_10.0.0.187_7110_20110405_235800.log' ")

			.append("ORDER BY LOCAL_FILE_NAME DESC ")
			.toString();
		
		// 获取需要写入DB的文件列表
		List<Map<String, Object>> readFileNameMaps = this.historyRptlogJdbcOperations.queryForList(
			readFileNamesSql, 
			this.serverIp, this.serverName, this.port
		);

		for (Map<String, Object> readFileNameMap : readFileNameMaps) {
			
			String readFileName = readFileNameMap.get("LOCAL_FILE_NAME").toString();

			// 导入笔数的计数器
			int count = 0;
			logger.info(readFileName + " loading ... " + count);

			try {
				
				// 获取 local access log file
				List<String> accessLogs = 
					FileUtils.readLines(new File(this.downloadAccessFileRootPath +date+ "\\" + readFileName));

				// 获取数据，每次 onceUpdateCount 笔
				for (int i = this.accessLogFirstIndex; i < accessLogs.size(); i += this.onceUpdateCount) {
					int to = (i + this.onceUpdateCount < accessLogs.size()) ? 
							 (i + this.onceUpdateCount) : accessLogs.size();
					List<Map<String, ?>> accessLogMaps = new ArrayList<Map<String, ?>>();
					
					// 获取一次插入的数据
					for (String accessLog : accessLogs.subList(i, to)) {
						Map<String, ?> accessLogParameterMap = this.accessLogParameters(accessLog);
						if (!accessLogParameterMap.isEmpty()) {
							accessLogMaps.add(accessLogParameterMap);
						}
					}

					// 添加数据
					int[] onceCounts = this.historyRptlogJdbcOperations.batchUpdate(
						insertAccessLogSql, 
						accessLogMaps.toArray(new Map[]{})
					);
					
					logger.debug(readFileName + "loading ... " + (count += onceCounts.length));
				}

				logger.info(readFileName + "loading over. " + count);
			} 
			catch (IOException e) {
				throw new RuntimeException(e);
			}

			// 标记已完成导入的文件状态
			this.historyRptlogJdbcOperations.update(
				updateAccessLogFileInfoImportDBSql, 
				IMPORT_DB_TRUE, readFileName
			);
		}
	}

	private Map<String, ?> accessLogParameters(String accessLog) {

		String[] accessLogColumns = StringUtils.split(accessLog, this.splitPattern);

		Map<String, Object> parameters = new HashMap<String, Object>();

		for (int i = 0; i < accessLogColumns.length; i++) {
			parameters.put(this.columnIndexMap.get(i), accessLogColumns[i]);
		}

		// 依照 serverName:port 的方式塞入serverIp栏位
		parameters.put("serverIp", this.serverName + ":" + port);

		if (!checked(parameters)) {
			parameters.clear();
		}

		return parameters;
	}
	
	private boolean checked(Map<String, Object> parameters) {

		Object uri = parameters.get("uri");
		if (uri == null || StringUtils.isBlank(uri.toString())) {
			return false;
		}

		Object responseTime = parameters.get("responseTime");
		if (responseTime == null || StringUtils.isBlank(responseTime.toString())) {
			return false;
		}

		return checkUri(parameters.get("uri").toString()) 
			&& checkResponseTime(Double.parseDouble(parameters.get("responseTime").toString()));
	}

	private boolean checkResponseTime(double responseTime) {
		return this.maxResponseTime <= 0 || responseTime < this.maxResponseTime;
	}

	private boolean checkUri(String uri) {
		
		for (String containString : this.accessLogContains) {
			if (uri.contains(containString)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * 从服务器读取 access log file 到本地
	 * 
	 * @param fileFilter
	 */
	private void downloadAccessFile(FTPFileFilter fileFilter,String date) {
		System.out.println(ftp);
		if (ftp) {
			this.downloadAccessFileFromFtp(fileFilter,date);
		}
	}
	
	/**
	 * 
	 * 从 ftp server download access 文件，目前适用于bpm
	 * 
	 * @param fileFilter
	 */
	private void downloadAccessFileFromFtp(FTPFileFilter fileFilter,String date) {
		System.out.println("method:"+ftp);
		if (!ftp) {
			return;
		}

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

			List<String> sourceFileNames = new ArrayList<String>();
			String sourceFileNamesSql = new StringBuilder()
				.append("SELECT SOURCE_FILE_NAME ")
				.append("FROM RPTLOG.BPM_ACCESS_LOG_FILE_INFO ")
				.append("WHERE SERVER_IP = ? AND SERVER_NAME = ? AND PORT = ? AND DOWNLOAD_LOCAL = ?")
				.toString();
			List<Map<String, Object>> sourceFileNameMaps = this.historyRptlogJdbcOperations.queryForList(
				sourceFileNamesSql, 
				this.serverIp, this.serverName, this.port, DOWNLOAD_LOCAL_TRUE
			);
			for (Map<String, Object> sourceFileNameMap : sourceFileNameMaps) {
				sourceFileNames.add(sourceFileNameMap.get("SOURCE_FILE_NAME").toString());
			}

			String insertSql = new StringBuilder()
				.append("INSERT INTO RPTLOG.BPM_ACCESS_LOG_FILE_INFO ")
				.append("(SERVER_IP, SERVER_NAME, PORT, SOURCE_FILE_NAME, LOCAL_FILE_NAME, DOWNLOAD_LOCAL) ")
				.append("VALUES(?, ?, ?, ?, ?, ?)")
				.toString();
			//add by 20131204
			for(String str:sourceFileNames){
				logger.info("*********************sourceFileName from db begin");
				logger.info(str);
				logger.info("*********************sourceFileName from db end");
			}
			//add by 20131204

			for (FTPFile ftpFile : ftpClient.listFiles(null, fileFilter)) {
				
				String sourceFileName = ftpFile.getName();
				logger.info("sourceFileName from service:"+sourceFileName);
				if (!sourceFileNames.contains(sourceFileName)) {

					String localFileName = generateLocalFileName(ftpFile.getTimestamp());
					File localFile = new File(this.downloadAccessFileRootPath +date+ "\\" + localFileName);
					if (!localFile.exists()) {
						localFile.createNewFile();	
					}

					OutputStream os = null;
					try {
						os = new FileOutputStream(localFile);
						
						logger.info(String.format("Download source file %s > %s start... ", sourceFileName, localFileName));
						ftpClient.retrieveFile(sourceFileName, os);
					}
					finally {
						if (os != null) {
							os.close();
						}
					}
					
					this.historyRptlogJdbcOperations.update(
						insertSql, 
						this.serverIp, this.serverName, this.port, 
						ftpFile.getName(), localFileName, DOWNLOAD_LOCAL_TRUE
					);
					logger.info(String.format("Download source file %s > %s over. ", sourceFileName, localFileName));
				}
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

	private String generateLocalFileName(Calendar lastModifyDate) {
		return new StringBuilder()
			.append("_access_")
			.append(this.serverIp)
			.append("_")
			.append(this.port)
			.append(new DateTime(lastModifyDate).toString("_yyyyMMdd_HHmmss"))
			.append(".log")
			.toString();
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
	public String namePath(){
		String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		File file = new File(this.downloadAccessFileRootPath+date);
		try {
			FileUtils.forceMkdir(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public void setFtp(boolean ftp) {
		this.ftp = ftp;
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

	public void setDownloadAccessFileRootPath(String downloadAccessFileRootPath) throws IOException {
		this.downloadAccessFileRootPath = downloadAccessFileRootPath;
	}

	public void setFtpLoginUser(String ftpLoginUser) {
		this.ftpLoginUser = ftpLoginUser;
	}

	public void setFtpLoginPassword(String ftpLoginPassword) {
		this.ftpLoginPassword = ftpLoginPassword;
	}

	public void setSourceAccessFileModifyDateFormatPattern(
			String sourceAccessFileModifyDateFormatPattern) {
		this.sourceAccessFileModifyDateFormatPattern = sourceAccessFileModifyDateFormatPattern;
	}

	public void setAccessLogFileNamePattern(String accessLogFileNamePattern) {
		this.accessLogFileNamePattern = accessLogFileNamePattern;
	}

	public void setOnceUpdateCount(int onceUpdateCount) {
		this.onceUpdateCount = onceUpdateCount;
	}

	public void setSplitPattern(String splitPattern) {
		this.splitPattern = splitPattern;
	}

	public void setColumns(String columns) {
		this.columns = StringUtils.split(columns, this.splitPattern);
		for (int i = 0; i < this.columns.length; i++) {
			this.columnIndexMap.put(i, this.columns[i]);
		}
	}

	public void setAccessLogFirstIndex(int accessLogFirstIndex) {
		this.accessLogFirstIndex = accessLogFirstIndex;
	}

	public void setMaxResponseTime(int maxResponseTime) {
		this.maxResponseTime = maxResponseTime;
	}

	public void setAccessLogContains(List<String> accessLogContains) {
		this.accessLogContains = accessLogContains;
	}

}
