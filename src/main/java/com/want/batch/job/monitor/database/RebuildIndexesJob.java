package com.want.batch.job.monitor.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.want.batch.WantBatchException;
import com.want.batch.job.utils.DBUtils;

public class RebuildIndexesJob extends AbstractMonitorDatabaseJob {

	private int limitTableRows = 1000000;
	
	@Override
	public void execute() {

		String ip = this.getProdDatabaseInfos().getIp();
		String instanceSid = this.getProdDatabaseInfos().getInstanceSid();
		SimpleJdbcOperations jdbcOperations = this.getProdDatabaseInfos().getJdbcOperations();

		for (String schema : this.getProdDatabaseInfos().getSchemas()) {
			
			List<String> tableNames = new ArrayList<String>();
			List<Map<String, Object>> tableInfos = this.getDataMartJdbcOperations().queryForList(
				new StringBuilder()
					.append("SELECT * ")
					.append("FROM PROD_TABLE_INFOS ")
					.append("WHERE IP = ? AND INSTANCE_SID = ? AND SCHEMA_NAME = ? AND ROWS_NUM >= ? ")
					.append("  AND DELETE_DATE IS NULL ")
					.toString(), 
				ip, instanceSid, schema, limitTableRows);
			
			for (Map<String, Object> tableInfoMap : tableInfos) {
				tableNames.add(tableInfoMap.get("TABLE_NAME").toString());
			}
			
			logger.info("reblid tables >>> " + tableNames);
			
			List<String> indexNames = new ArrayList<String>();
			List<Map<String, Object>> indexInfos = jdbcOperations.queryForList(
				new StringBuilder()
					.append("SELECT * ")
					.append("FROM DBA_INDEXES ")
					.append("WHERE TABLE_OWNER = ? ")
					.append(String.format(" AND TABLE_NAME IN ('%s')", StringUtils.join(tableNames, "','")))
					.toString(), 
				schema);
			
			for (Map<String, Object> indexInfoMap : indexInfos) {
				indexNames.add(String.format(
					"%s.%s", indexInfoMap.get("TABLE_OWNER").toString(), indexInfoMap.get("INDEX_NAME").toString()));
			}
			
			logger.info("reblid indexes >>> " + indexNames);

			List<String> errorIndexNames = 
				DBUtils.rebuildIndexesIgnoreException(jdbcOperations, indexNames.toArray(new String[]{}));
			if (errorIndexNames.size() > 0) {
				throw new WantBatchException("rebuild indexes error >>> " + errorIndexNames);
			}
		}
	}

	public void setLimitTableRows(int limitTableRows) {
		this.limitTableRows = limitTableRows;
	}

}
