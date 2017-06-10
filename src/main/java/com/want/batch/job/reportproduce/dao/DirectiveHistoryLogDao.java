
// ~ Package Declaration
// ==================================================

package com.want.batch.job.reportproduce.dao;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.want.batch.job.reportproduce.pojo.Constant;


// ~ Comments
// ==================================================

/**
 * 指令历史记录表.
 * 
 * <table>
 * 	<tr>
 * 		<th>日期</th>
 * 		<th>变更说明</th>
 * 	</tr>
 * 	<tr>
 * 		<td>2013-6-21</td>
 * 		<td>Mirabelle新建</td>
 * 	</tr>
 * </table>
 *
 *@author Mirabelle
 */
@Component
public class DirectiveHistoryLogDao {

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================
	
	private static final String insertSql = " INSERT INTO	ICUSTOMER.DIRECTIVE_HISTORY_LOG	(SID,DIRECTIVE_SID,OPERATE_USER,STATUS,EXCUTE_JOB,"
			+ "	ROOT_PATH,SELECT_PARAM_VALUE,SELECT_PARAM_NAME_EN,SELECT_PARAM_NAME_CN,SELECT_SQL,FILE_NAME,REPORT_NAME,CREATE_DATE, "
			+ " UPDATE_DATE,START_TIME,END_TIME,ENTER_TYPE,EXCEPTION_REASON) VALUES (ICUSTOMER.DIRECTIVE_HISTORY_LOG_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String insertLogSql = "INSERT INTO	ICUSTOMER.DIRECTIVE_HISTORY_LOG	(SID,OPERATE_USER,STATUS,EXCUTE_JOB,ROOT_PATH,"
		 + "SELECT_PARAM_VALUE,SELECT_PARAM_NAME_EN,SELECT_PARAM_NAME_CN,SELECT_SQL,FILE_NAME,CREATE_DATE,UPDATE_DATE,REPORT_NAME,DIRECTIVE_SID,"
		 + "START_TIME,END_TIME,ENTER_TYPE,EXCEPTION_REASON) SELECT	ICUSTOMER.DIRECTIVE_HISTORY_LOG_SEQ.nextval,OPERATE_USER,STATUS,EXCUTE_JOB,"
		 + "ROOT_PATH,SELECT_PARAM_VALUE,SELECT_PARAM_NAME_EN,SELECT_PARAM_NAME_CN,SELECT_SQL,FILE_NAME,CREATE_DATE,sysdate,REPORT_NAME,SID,"
		 + "START_TIME,END_TIME,'SCHEDULER',EXCEPTION_REASON FROM	ICUSTOMER.DIRECTIVE_tbl WHERE SID=?";
	
	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * <pre>
	 * 2013-6-21 Mirabelle
	 * 新增log档.
	 * </pre>	
	 * 
	 * @param conn
	 */
	public void addHistory(Connection conn, List<Map<String, Object>> args) throws Exception {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement(insertSql);
			
			for (int i = 0; i < args.size(); i++) {
				
				String selectSql = (null == args.get(i).get("SELECT_SQL")) ? "" : args.get(i).get("SELECT_SQL").toString();
				
				String exceptionReason = (null == args.get(i).get("EXCEPTION_REASON")) ? "" : args.get(i).get("EXCEPTION_REASON").toString();
				
				Reader clobReader = new StringReader(selectSql); 
				
				pstmt.setLong(1, Long.parseLong(args.get(i).get("SID").toString()));// modify 2013-09-04 mandy SID类型修改使用Long
//				pstmt.setInt(1, Integer.parseInt(args.get(i).get("SID").toString()));
				pstmt.setString(2, (String)args.get(i).get("OPERATE_USER"));
				pstmt.setString(3, (String)args.get(i).get("STATUS"));
				pstmt.setString(4, (String)args.get(i).get("EXCUTE_JOB"));
				pstmt.setString(5, (String)args.get(i).get("ROOT_PATH"));
				pstmt.setString(6, (String)args.get(i).get("SELECT_PARAM_VALUE"));
				pstmt.setString(7, (String)args.get(i).get("SELECT_PARAM_NAME_EN"));
				pstmt.setString(8, (String)args.get(i).get("SELECT_PARAM_NAME_CN"));
				
				// setString()方法，驱动程序会将它转换成一个 SQL VARCHAR 或 LONGVARCHAR 值，值长度如果超过2000会转成Long类型
				pstmt.setCharacterStream(9, clobReader, selectSql.length());
				pstmt.setString(10, (String)args.get(i).get("FILE_NAME"));
				pstmt.setString(11, (String)args.get(i).get("REPORT_NAME"));
				pstmt.setTimestamp(12, (Timestamp)args.get(i).get("CREATE_DATE"));
				
				// mirabelle add 记录删除的时间以及排程产生报表时的开始时间和结束时间
				pstmt.setTimestamp(13, new Timestamp(new Date().getTime()));
				pstmt.setTimestamp(14, (Timestamp)args.get(i).get("START_TIME"));
				pstmt.setTimestamp(15, (Timestamp)args.get(i).get("END_TIME"));
				pstmt.setString(16, Constant.DIRECTIVEHISTORYLOG_ENTER_TYPE_SCHEDULER);// mandy add 删除类型，从排程删除
				pstmt.setCharacterStream(17, new StringReader(exceptionReason), exceptionReason.length());
				
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
			
		}
		catch (Exception e) {
			
			throw new Exception(e);
		}
		finally {
			
			if (pstmt != null) {
				
				pstmt.close();
			}
		}
	}

	/**
	 * <pre>
	 * 2013-9-29 Mirabelle
	 * 用于查找异常资料测试，一笔一笔新增.
	 * </pre>	
	 * 
	 * @param conn
	 * @param args
	 * @throws Exception
	 */
	public void addHistory(Connection conn, Map<String, Object> args)  throws Exception {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement(insertLogSql);
			pstmt.setLong(1, Long.parseLong(args.get("SID").toString()));
			pstmt.execute();
		}
		catch (Exception e) {
			
			throw new Exception(e);
		}
		finally {
			
			if (pstmt != null) {
				
				pstmt.close();
			}
		}
	}
}
