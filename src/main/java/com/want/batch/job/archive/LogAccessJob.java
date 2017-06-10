package com.want.batch.job.archive;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component
public class LogAccessJob extends AbstractWantJob {

	private int keepDays = 45;
	private String filePath = "e:\\log_data_backup\\";
	private String fileName = "LogAccessDatas_%s.txt";

	@Override
	public void execute() {

		String logDate = new DateTime().minusDays(keepDays).toString("yyyy-MM-dd");

		String archiveLogDatesSql = "SELECT LOG_DATE FROM ACCESS_LOG WHERE LOG_DATE < ? GROUP BY LOG_DATE";
		List<Map<String, Object>> archiveLogDateMaps = this.getLoggerJdbcOperations().queryForList(archiveLogDatesSql, logDate);

		logger.info("Need archive >>> " + archiveLogDateMaps);

		for (Map<String, Object> archiveLogDateMap : archiveLogDateMaps) {
			archiveData(archiveLogDateMap.get("LOG_DATE").toString());
		}

		logger.info("Archive finished!");
	}

	private void archiveData(String logDate) {

		logger.info(String.format("archive %s's data start... ", logDate));

		String sql = new StringBuilder()
			.append("SELECT CLIENT_IP, SERVER_IP, LOG_DATE, LOG_TIME, HTTP_METHOD, ")
			.append("       ACCESS_URI, STATUS_CODE, RESPONSE_TIME, FUNCTION_NAME ")
			.append("FROM ACCESS_LOG ")
			.append("WHERE LOG_DATE = ? ")
			.toString();

		List<Map<String, Object>> archiveDataMaps = this.getLoggerJdbcOperations().queryForList(sql, logDate);
		
		List<String> archiveDatas = new ArrayList<String>();
		
		for (Map<String,Object> archiveDataMap : archiveDataMaps) {
			archiveDatas.add(
				new StringBuilder()
					.append(archiveDataMap.get("CLIENT_IP")).append("\t")
					.append(archiveDataMap.get("SERVER_IP")).append("\t")
					.append(archiveDataMap.get("LOG_DATE")).append("\t")
					.append(archiveDataMap.get("LOG_TIME")).append("\t")
					.append(archiveDataMap.get("HTTP_METHOD")).append("\t")
					.append(archiveDataMap.get("ACCESS_URI")).append("\t")
					.append(archiveDataMap.get("STATUS_CODE")).append("\t")
					.append(archiveDataMap.get("RESPONSE_TIME")).append("\t")
					.append(archiveDataMap.get("FUNCTION_NAME")).append("\t")
					.toString()
			);
		}

		
		try {
			File archiveFile = new File(filePath + String.format(fileName, logDate));
			FileUtils.writeLines(archiveFile, archiveDatas);
			
			logger.info(String.format("archive %s's data successed!", logDate));

			logger.info(String.format("delete %s's data start... ", logDate));
			this.getLoggerJdbcOperations().update("DELETE FROM ACCESS_LOG WHERE LOG_DATE = ?", logDate);
			logger.info(String.format("delete %s's data successed!", logDate));
		} catch (IOException ioe) {
			logger.error(String.format("archive %s's data failed!", logDate), ioe);
		}
	}

}
