package com.want.batch.job.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.want.batch.WantBatchException;

public class DBUtils {

	private static Log logger = LogFactory.getLog(DBUtils.class);

    public static void rebuildIndexes(SimpleJdbcOperations jdbcOperations, String ... indexNames) {
    	for (String indexName : indexNames) {
			rebulidIndex(jdbcOperations, indexName);
		}
    }
    
    public static List<String> rebuildIndexesIgnoreException(SimpleJdbcOperations jdbcOperations, String ... indexNames) {
    	List<String> errorIndexNames = new ArrayList<String>();
    	for (String indexName : indexNames) {
    		String errorIndexName = rebulidIndexIgnoreException(jdbcOperations, indexName);
    		if (StringUtils.isNotEmpty(errorIndexName)) {
    			errorIndexNames.add(errorIndexName);
    		}
		}
    	return errorIndexNames;
    }

    public static void rebulidIndex(SimpleJdbcOperations jdbcOperations, String indexName) {
    	
    	if (jdbcOperations == null) {
    		throw new WantBatchException(new NullPointerException("JdbcOperations cloudn't null."));
    	}
    	
		logger.info(String.format("rebuild index[%s] begin... ", indexName));
		jdbcOperations.update(String.format("ALTER INDEX %s REBUILD NOLOGGING", indexName));
		logger.info(String.format("rebuild index[%s] end!", indexName));
	}

    public static String rebulidIndexIgnoreException(SimpleJdbcOperations jdbcOperations, String indexName) {
    	
    	if (jdbcOperations == null) {
    		throw new WantBatchException(new NullPointerException("JdbcOperations cloudn't null."));
    	}
    	
    	try {
			rebulidIndex(jdbcOperations, indexName);
    	}
    	catch (Exception e) {
    		logger.error(String.format("rebuild index [%s] error! ", indexName), e);
    		return indexName;
    	}
    	
    	return null;
	}

    /*
    Create a method in DBUtils.rebuldTable to rebuild table with table name, period, week day

    For example:
    DBUtils.rebuldTable(historyRptlogJdbcOperations, "PROD_ERROR_INFO", "PROD_ERROR_INFO_TMP", "create_date", 3, 2);

    Use historyRptlogJdbcOperations
    	source table PROD_ERROR_INFO
    	temp table PROD_ERROR_INFO_TMP
    	create_date as where clause for date logic column 
    	3 is date period to be retained
    	2 means to execute on Monday.
	*/
	public static void rebuildTable(SimpleJdbcOperations op, String sourceTbl, String tempTbl, String dateColumn, int days, int dateOfWeek) {
		
		String dateOfWeekSql = "select to_char(sysdate, 'D') from dual";
		
		int weekDate = op.queryForInt(dateOfWeekSql);
		
		if (weekDate == dateOfWeek) {
			String insert = "insert into " + tempTbl + " select * from " + sourceTbl + " where " + dateColumn + " >= to_date(to_char(sysdate-" + days + ",'yyyy-mm-dd'),'yyyy-mm-dd')";
			String truncate = "truncate table " + sourceTbl; 
			String renameToSwitch = "alter table " + sourceTbl + " rename to " + sourceTbl + "_SW";
			String restore = "alter table " + tempTbl + " rename to " + sourceTbl;
			String renameSwitchToTemp = "alter table " + sourceTbl + "_SW rename to " + tempTbl;
			
			int rows = op.update(insert);
			logger.info(insert + ", rows " + rows);
			op.update(truncate);
			logger.info(truncate);
			op.update(renameToSwitch);
			logger.info(renameToSwitch);
			op.update(restore);
			logger.info(restore);
			op.update(renameSwitchToTemp);
			logger.info(renameSwitchToTemp);
			logger.info("rebuild " + sourceTbl + " done");
		} else 
			logger.info("Current date of week " + weekDate);
	}

	
	public static void rebuildTableStringDate(SimpleJdbcOperations op, String sourceTbl, String tempTbl, String dateColumn, int days, int dateOfWeek) {
		
		String dateOfWeekSql = "select to_char(sysdate, 'D') from dual";
		
		int weekDate = op.queryForInt(dateOfWeekSql);
		
		if (weekDate == dateOfWeek) {
			String insert = "insert into " + tempTbl + " select * from " + sourceTbl + " where " + dateColumn + " >= to_char(sysdate-" + days + ",'yyyy-mm-dd')";
			String truncate = "truncate table " + sourceTbl; 
			String renameToSwitch = "alter table " + sourceTbl + " rename to " + sourceTbl + "_SW";
			String restore = "alter table " + tempTbl + " rename to " + sourceTbl;
			String renameSwitchToTemp = "alter table " + sourceTbl + "_SW rename to " + tempTbl;
			
			int rows = op.update(insert);
			logger.info(insert + ", rows " + rows);
			op.update(truncate);
			logger.info(truncate);
			op.update(renameToSwitch);
			logger.info(renameToSwitch);
			op.update(restore);
			logger.info(restore);
			op.update(renameSwitchToTemp);
			logger.info(renameSwitchToTemp);
			logger.info("rebuild " + sourceTbl + " done");
		} else 
			logger.info("Current date of week " + weekDate);
	}

}
