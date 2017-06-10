package com.want.batch.job.basedata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;

/*
 delete emp;
 insert into emp select * from temporg.emp;

 delete EMP_POSITION;
 insert into  EMP_POSITION select EMP_ID,POS_ID,JOB_NAME,DIRECTOR_POS_ID,DIRECTOR_EMP_ID,DEPT_DIRECTOR_FLAG,DIVISION_DIRECTOR_FLAG,TOP_DIRECTOR_FLAG,'MIS_SYS',sysdate,null,null,MASTER_POS from temporg.EMP_POSITION_A where MASTER_POS=1;
 */

@Component
public class SyncTemporgData extends AbstractWantJob {

	private static String TRUNC_EMP = "truncate table emp";
	private static String INSERT_EMP = "insert into emp select A.*,SYSDATE from TEMPORG.EMP A";

	private static String TRUNC_ORG_B = "truncate table ORGANIZATION_B  ";
	private static String INSERT_ORG_B = "insert into ORGANIZATION_B select A.*,SYSDATE from TEMPORG.ORGANIZATION_A A";

	private static String TRUNC_POS_B = "truncate table POSITION_B";
	// 2014-07-08 mirabelle add POS_TYPE_ID,POS_TYPE_NAME
	private static String INSERT_POS_B = "insert into POSITION_B (POS_ID,POS_NAME,ORG_ID,POS_PROPERTY_ID,POS_PROPERTY_NAME,DIRECTOR_POS_ID,CREATE_DATE,POS_TYPE_ID,POS_TYPE_NAME) select A.POS_ID,A.POS_NAME,A.ORG_ID,A.POS_PROPERTY_ID,A.POS_PROPERTY_NAME,A.DIRECTOR_POS_ID,SYSDATE,POS_TYPE_ID,POS_TYPE_NAME from TEMPORG.POSITION_A A";

	// 添加包含有副岗位的EMP_POSITION_A
	private static String TRUNCATE_EMP_POSITION_A = "truncate table EMP_POSITION_A";
	private static String INSERT_EMP_POSITION_A = "insert into  EMP_POSITION_A select A.*,SYSDATE from TEMPORG.EMP_POSITION_A A";
	// 添加包含有副岗位的EMP_POSITION_A

	private static String TRUNC_EMP_POSITIONO = "truncate table EMP_POSITION";
	private static String INSERT_EMP_POSITION = "insert into  EMP_POSITION "
			+ "select EMP_ID,POS_ID,JOB_NAME,DIRECTOR_POS_ID,DIRECTOR_EMP_ID,DEPT_DIRECTOR_FLAG,DIVISION_DIRECTOR_FLAG,TOP_DIRECTOR_FLAG,'MIS_SYS',sysdate,null,null,MASTER_POS "
			+ "from EMP_POSITION_A where MASTER_POS=1 AND EMP_ID NOT IN ("
			+ "select EMP_ID from EMP_POSITION_A where MASTER_POS=1 GROUP BY EMP_ID HAVING COUNT(EMP_ID)>1 )";
//	private static String INSERT_EMP_POSITION = "insert into  EMP_POSITION " 
//							+ " select EMP_ID,POS_ID,JOB_NAME,DIRECTOR_POS_ID,DIRECTOR_EMP_ID,DEPT_DIRECTOR_FLAG,DIVISION_DIRECTOR_FLAG,TOP_DIRECTOR_FLAG,'MIS_SYS',sysdate,null,null,MASTER_POS "
//							+ " from EMP_POSITION_A ";

	private static String sqlcmd_check = "select EMP_ID from EMP_POSITION_A where MASTER_POS=1 GROUP BY EMP_ID HAVING COUNT(EMP_ID)>1";

	@Override
	public void execute() throws SQLException {
		logger.info("SyncTemporgData start!!");

		Connection checkcon = this.getICustomerConnection();
		if (checkDataSource("TEMPORG.EMP", checkcon)
				&& checkDataSource("TEMPORG.ORGANIZATION_A", checkcon)
				&& checkDataSource("TEMPORG.POSITION_A", checkcon)
				&& checkDataSource("TEMPORG.EMP_POSITION_A", checkcon)) {
			this.syncTemporgData(checkcon);
		}
		checkcon.close();
	}

	public void syncTemporgData(Connection conn) throws SQLException {
		getiCustomerJdbcOperations().update(TRUNC_EMP);
		logger.info("完成: " + TRUNC_EMP);
		//pstmt.executeUpdate();
		//pstmt.close();

		getiCustomerJdbcOperations().update(INSERT_EMP);
		//pstmt.executeUpdate();
		logger.info("完成: " + INSERT_EMP);
		//pstmt.close();

		getiCustomerJdbcOperations().update(TRUNC_ORG_B);
		//pstmt.executeUpdate();
		logger.info("完成: " + TRUNC_ORG_B);
		//pstmt.close();

		getiCustomerJdbcOperations().update(INSERT_ORG_B);
		//pstmt.executeUpdate();
		logger.info("完成: " + INSERT_ORG_B);
		//pstmt.close();
		
		getiCustomerJdbcOperations().update(TRUNC_POS_B);
		//pstmt.executeUpdate();
		logger.info("完成: " + TRUNC_POS_B);
		//pstmt.close();

		getiCustomerJdbcOperations().update(INSERT_POS_B);
		//pstmt.executeUpdate();
		logger.info("完成: " + INSERT_POS_B);
		//pstmt.close();

		// 添加包含有副岗位的EMP_POSITION_A
		getiCustomerJdbcOperations().update(TRUNCATE_EMP_POSITION_A);
		//pstmt.executeUpdate();
		logger.info("完成: " + TRUNCATE_EMP_POSITION_A);
		//pstmt.close();

		getiCustomerJdbcOperations().update(INSERT_EMP_POSITION_A);
		//pstmt.executeUpdate();
		logger.info("完成: " + INSERT_EMP_POSITION_A);
		//pstmt.close();
		
		// 添加包含有副岗位的EMP_POSITION_A

		getiCustomerJdbcOperations().update(TRUNC_EMP_POSITIONO);
		logger.info("完成: " + TRUNC_EMP_POSITIONO);
		
		getiCustomerJdbcOperations().update(INSERT_EMP_POSITION);
		logger.info("完成: " + INSERT_EMP_POSITION);

		List<Map<String, Object>> checkResults = this
				.getiCustomerJdbcOperations().queryForList(sqlcmd_check);

		if (checkResults.size() > 0) {
			for (Map emp: checkResults)
				logger.error("EMP has more than one MAS_POS: " + emp);
			throw new WantBatchException(checkResults.toString());
		}
	}

	private boolean checkDataSource(String tableName, Connection con)
			throws SQLException {

		String checksql = "select count(1) as sum from " + tableName;
		int hasNum = 0;
		PreparedStatement pstmt = con.prepareStatement(checksql);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			hasNum = rs.getInt(1);
		}
		logger.info(tableName + "表中的数据量为：" + hasNum);
		pstmt.close();

		return hasNum > 0;
	}
}

/*
String tempPositionA = "select * from temporg.emp_position_a";
PreparedStatement pstmtTemporgPositionA = conn.prepareStatement(tempPositionA);
ResultSet rs = pstmtTemporgPositionA.executeQuery();
ResultSetMetaData meta = rs.getMetaData();
int columns = meta.getColumnCount();
StringBuffer insertPositionA = new StringBuffer("insert into emp_position_a values ("); 
for (int i=0; i<columns; i++) {
	insertPositionA.append("?");
	if (i<(columns-1))
		insertPositionA.append(",");
	else
		insertPositionA.append(",SYSDATE)");
}

PreparedStatement pstmtInsert = conn.prepareStatement(insertPositionA.toString());
int inserts = 0;

while (rs.next()) {
	try {
		pstmtInsert.clearParameters();
		for (int i=1; i<=columns; i++) {
			Object value = rs.getObject(i);
			//logger.debug("insert " + i + " value " + value);
			pstmtInsert.setObject(i, value);
		}
		pstmtInsert.executeUpdate();
		inserts ++;
	} catch (SQLException e) {
		logger.error("insert " + rs.getObject(1) + " fail, error: " + e.getMessage());
	}
}

logger.info("新邹笔数: " + inserts);
pstmtTemporgPositionA.close();
pstmtInsert.close();
*/

