package com.want.batch.job.reportproduce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.reportproduce.pojo.Constant;

/**
 * 指令Dao.
 * 
 * <table>
 * 	<tr>
 * 		<th>日期</th>
 * 		<th>变更说明</th>
 * 	</tr>
 * 	<tr>
 * 		<td>2013-6-20</td>
 * 		<td>Mirabelle新建</td>
 * 	</tr>
 * </table>
 *
 *@author Mirabelle
 */
@Component
public class DirectiveTblDao {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	private static final String noExcuteTaskSql = "SELECT SID,OPERATE_USER,STATUS,EXCUTE_JOB,ROOT_PATH,SELECT_PARAM_VALUE,SELECT_PARAM_NAME_EN,"
			+ " SELECT_PARAM_NAME_CN,SELECT_SQL,UPDATE_DATE,REPORT_NAME,FILE_NAME,CREATE_DATE FROM	(SELECT * FROM DIRECTIVE_TBL WHERE	STATUS = ? "
			+ " ORDER BY	CREATE_DATE) WHERE ROWNUM <= ?";
	
	private static final String waitSql = "UPDATE DIRECTIVE_TBL SET STATUS = ? WHERE SID = ?";
	
	// mirabelle add 查询排程产生报表的开始时间与结束时间
	// modify mandy 2013-09-06 增加查询栏位异常;2013-09-30 mirabelle update 删除8天之前的资料同时删除前一天指令状态为excpeiton的
	private static final String taskByDeletedSql = "SELECT SID,OPERATE_USER,STATUS,EXCUTE_JOB,ROOT_PATH,SELECT_PARAM_VALUE,SELECT_PARAM_NAME_EN,"
			+ "   SELECT_PARAM_NAME_CN,SELECT_SQL,CREATE_DATE,UPDATE_DATE,REPORT_NAME,FILE_NAME,START_TIME,END_TIME,EXCEPTION_REASON FROM DIRECTIVE_TBL"
			+ " WHERE ((TRUNC(CREATE_DATE)<=TRUNC(SYSDATE-8)) OR (STATUS='" + Constant.DIRECTIVE_STATUS_EXCEPTION + "' AND TRUNC(CREATE_DATE)<=TRUNC(SYSDATE-1)))";
	
	private static final String empEmailSql = "SELECT	DIRECTIVE_TBL.SID,DIRECTIVE_TBL.OPERATE_USER,DIRECTIVE_TBL.STATUS,DIRECTIVE_TBL.EXCUTE_JOB,"
			+ " 	DIRECTIVE_TBL.ROOT_PATH,DIRECTIVE_TBL.SELECT_PARAM_VALUE,DIRECTIVE_TBL.SELECT_PARAM_NAME_EN,DIRECTIVE_TBL.SELECT_PARAM_NAME_CN,"
			+ " 	DIRECTIVE_TBL.SELECT_SQL,DIRECTIVE_TBL.UPDATE_DATE,DIRECTIVE_TBL.REPORT_NAME,DIRECTIVE_TBL.FILE_NAME,DIRECTIVE_TBL.CREATE_DATE, "
			+ "   EMP.EMP_EMAIL FROM DIRECTIVE_TBL INNER JOIN EMP ON substr(DIRECTIVE_TBL.OPERATE_USER,0,8) = EMP.EMP_ID WHERE DIRECTIVE_TBL.SID = ?"
			+ " ORDER BY CREATE_DATE";
	
	private static final String deleteSql = "DELETE FROM	DIRECTIVE_TBL WHERE SID = ?";
	
	private static final String taskStatusSql = "SELECT COUNT(*) AS DATACOUNT FROM DIRECTIVE_TBL WHERE STATUS=?";
	
	/**
	 * <pre>
	 * 2013-6-20 Mirabelle
	 * 取得未处理的任务.
	 * </pre>	
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public ArrayList<Map<String, Object>> getNoExcuteTask(int rownum) {
		
		List<Map<String, Object>> list = iCustomerJdbcOperations.queryForList(noExcuteTaskSql, Constant.DIRECTIVE_STATUS_NEWJOB, rownum);
		
		List<Object[]> sids = new ArrayList<Object[]>();
		for (int i=0; i<list.size(); i++) {
			Object sid = list.get(i).get("SID");
			sids.add(new Object[]{Constant.DIRECTIVE_STATUS_WAIT, sid});
			logger.info("change " + sid + " to WAIT");
		}
		
		if (list.size() > 0) {
			iCustomerJdbcOperations.batchUpdate(waitSql, sids);
			logger.info("wating jobs: " + list.size());
		}
		
		return (ArrayList) list;
	}

	/**
	 * <pre>
	 * 2013-6-20 Mirabelle
	 * junit测试时调用：取得需要删除的任务信息.
	 * </pre>	
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getTaskByDeleted() {
		
		return this.iCustomerJdbcOperations.queryForList(taskByDeletedSql);
	}
	
	/**
	 * <pre>
	 * 2013-6-20 Mirabelle
	 * 删除指令.
	 * </pre>	
	 * 
	 * @param args
	 * @throws SQLException 
	 */
	public void deleteTask(Connection conn, List<Map<String, Object>> args) throws Exception {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement(deleteSql);
			
			for (int i = 0; i < args.size(); i++) {
				
				pstmt.setInt(1, Integer.parseInt(args.get(i).get("SID").toString()));
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
	 * 用于查找异常资料测试，一笔一笔删除.
	 * </pre>	
	 * 
	 * @param conn
	 * @param args
	 * @throws Exception
	 */
	public void deleteTask(Connection conn, Map<String, Object> args) throws Exception {
		
		PreparedStatement pstmt = null;
		
		try {
			
			pstmt = conn.prepareStatement(deleteSql);
			
				pstmt.setInt(1, Integer.parseInt(args.get("SID").toString()));
			
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
	
	/**
	 * <pre>
	 * 2013-6-26 Nash
	 * 根据SID查询指令表   结果用于发送邮件
	 * </pre>	
	 * 
	 *	<ol>
	 *	</ol>
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> getDirectiveBySid(String directiveSid) throws SQLException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		List<Map<String, Object>> dataList = this.iCustomerJdbcOperations.queryForList(empEmailSql, directiveSid);
		
		if (dataList != null && dataList.size() != 0) {
			
			return dataList.get(0); 
		}
		
		return data;
	}

	/**
	 * <pre>
	 * 2013-7-3 Mirabelle
	 * junit测试用到此方法：查询本次监控执行的资料的状态是否都变为FINISH.
	 * </pre>	
	 * 
	 * @param dataList
	 * @return true/false
	 */
	public boolean getTaskStatus(List<Map<String, Object>> dataList) {
			
		boolean flag = false;
		String sids = "";
		
		for (int i = 0; i < dataList.size(); i++) {
			
			if (i == dataList.size() - 1) {
				
				sids += dataList.get(i).get("SID");
			}
			else {
				
				sids += dataList.get(i).get("SID") + ",";
			}
		}
		
		String sql = "SELECT STATUS FROM DIRECTIVE_TBL WHERE SID IN (" + sids + ")";
		
		List<Map<String, Object>> resultList = this.iCustomerJdbcOperations.queryForList(sql);
		
		if (resultList != null && resultList.size() != 0) {
			
			for (int i = 0; i < resultList.size(); i++) {
				
				if (Constant.DIRECTIVE_STATUS_FINISH.equals((String)resultList.get(i).get("STATUS"))) {
				
					flag = true;
				}
				else {
					
					flag = false;
					break;
				}
			}
		}
		
		return flag;
	}

	/**
	 * <pre>
	 * 2013-7-4 Mirabelle
	 * 根据任务状态查询任务的数量.
	 * </pre>	
	 * 
	 * @param status
	 * @return
	 */
	public int getTaskCountByStatus(String status) {
		
		return this.iCustomerJdbcOperations.queryForInt(taskStatusSql, status);
	}
	
	/**
	 * 2013-09-10 mandy
	 * 根据ID查询指令状态
	 * 
	 * @param directiveSid
	 * @return Map<String, Object>
	 */
	public Map<String, Object> findDirectiveById(String directiveSid) {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		String sql = "SELECT SID,STATUS FROM DIRECTIVE_TBL WHERE SID = ?";
		
		List<Map<String, Object>> lstDirective = this.iCustomerJdbcOperations.queryForList(sql, directiveSid);
		
		if(null != lstDirective && lstDirective.size() > 0) {
			
			return lstDirective.get(0);
		}
		
		return data;
	}
}
