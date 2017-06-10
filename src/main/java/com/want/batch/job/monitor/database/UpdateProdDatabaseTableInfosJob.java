package com.want.batch.job.monitor.database;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

public class UpdateProdDatabaseTableInfosJob extends AbstractMonitorDatabaseJob {

	@Override
	public void execute() {

		DateTime currentDateTime = new DateTime();
		Date currentDate = currentDateTime.toDate();
		String ip = this.getProdDatabaseInfos().getIp();
		String instanceSid = this.getProdDatabaseInfos().getInstanceSid();
		SimpleJdbcOperations jdbcOperations = this.getProdDatabaseInfos().getJdbcOperations();

		logger.info("start~");

		for (String schema : this.getProdDatabaseInfos().getSchemas()) {
			List<Map<String, Object>> tableInfos = jdbcOperations.queryForList(
				new StringBuilder()
					.append("SELECT A.*, ")
					// 查询index数量
					.append("  (SELECT COUNT(1) ")
					.append("   FROM DBA_INDEXES B ")
					.append("   WHERE A.OWNER = B.TABLE_OWNER AND A.TABLE_NAME = B.TABLE_NAME ")
					.append("  ) INDEXES_NUM ")
					.append("FROM DBA_TABLES A ")
					.append("WHERE A.OWNER = ? ")
					.toString(), schema);

			for (Map<String, Object> tableInfo : tableInfos) {

				String tableName = tableInfo.get("TABLE_NAME").toString();
				int rowsNum = toInt(tableInfo, "NUM_ROWS");
				int indexesNum = toInt(tableInfo, "INDEXES_NUM");

				// 查询此table信息是否已经存在
				List<Map<String, Object>> prodTableInfos = this.getDataMartJdbcOperations().queryForList(
					new StringBuilder()
						.append("SELECT * ")
						.append("FROM PROD_TABLE_INFOS ")
						.append("WHERE IP = ? AND INSTANCE_SID = ? AND SCHEMA_NAME = ? AND TABLE_NAME = ? ")
						.append("  AND DELETE_DATE IS NULL ")
						.toString(), 
					ip, instanceSid, schema, tableName);

				// 没有table 信息，就需要新增此table
				if (prodTableInfos.isEmpty()) {
					this.getDataMartJdbcOperations().update(
						new StringBuilder()
							.append("INSERT INTO PROD_TABLE_INFOS ")
							.append("(SID, IP, INSTANCE_SID, SCHEMA_NAME, TABLE_NAME, ROWS_NUM, INDEXES_NUM, CREATE_DATE, UPDATE_DATE) ")
							.append("VALUES(PROD_TABLE_INFOS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?) ")
							.toString(), 
						ip, instanceSid, schema, tableName, rowsNum, indexesNum, currentDate, currentDate);
					logger.info(String.format("%s %s.%s ok~", "create", schema, tableName));
				}
				
				// 已有 table 信息，则需要更新table状态
				else {
					Map<String, Object> prodTableInfoMap = prodTableInfos.get(0);
					int sid = toInt(prodTableInfoMap, "SID");
					int rowsIncrement = rowsNum - toInt(prodTableInfoMap, "ROWS_NUM");
					this.getDataMartJdbcOperations().update(
						new StringBuilder()
							.append("UPDATE PROD_TABLE_INFOS ")
							.append("SET ROWS_NUM = ?, ROWS_INCREMENT = ?, INDEXES_NUM = ?, UPDATE_DATE = ? ")
							.append("WHERE SID = ? ")
							.toString(), 
						rowsNum, rowsIncrement, indexesNum, currentDate, sid);
					logger.info(String.format("%s %s.%s ok~", "update", schema, tableName));
				}
			}
		}

		// 查看是否有删除的table，并记录
		int deleteStatus = this.getDataMartJdbcOperations().update(
			new StringBuilder()
				.append("UPDATE PROD_TABLE_INFOS ")
				.append("SET UPDATE_DATE = ?, DELETE_DATE = ? ")
				.append("WHERE IP = ? AND INSTANCE_SID = ? AND TO_CHAR(UPDATE_DATE, 'YYYYMMDD') < ? ")
				.append("  AND DELETE_DATE IS NULL ")
				.toString(), 
			currentDate, currentDate, ip, instanceSid, currentDateTime.toString("yyyyMMdd"));
		logger.info(String.format("%s[%s] ok~", "delete", deleteStatus));
		
		// 将今日的更新信息插入到历史资料表
		int hisStatus = this.getDataMartJdbcOperations().update(
			new StringBuilder()
				.append("INSERT INTO RPTLOG.PROD_TABLE_INFOS_HIS ")
				.append("SELECT A.*, SYSDATE ")
				.append("FROM PROD_TABLE_INFOS A ")
				.append("WHERE IP = ? AND INSTANCE_SID = ? ")
				.toString(), 
			ip, instanceSid);
		logger.info(String.format("%s[%s] ok~", "insert his", hisStatus));
	}

	private int toInt(Map<String, Object> tableInfo, String columnName) {
		if (tableInfo.get(columnName) == null) {
			return 0;
		}

		return Integer.parseInt(tableInfo.get(columnName).toString());
	}

}
