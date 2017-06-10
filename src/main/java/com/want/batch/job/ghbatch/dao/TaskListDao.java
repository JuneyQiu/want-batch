/**
 * 
 */
package com.want.batch.job.ghbatch.dao;

import static com.want.batch.job.ghbatch.util.DateFormatEnum.DATE_NO_PARTITION;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.ghbatch.pojo.TaskDetail;
import com.want.batch.job.ghbatch.pojo.TaskList;

/**
 * @author MandyZhang
 *
 */
@Component
public class TaskListDao {

	@Autowired
	public DataSource iCustomerDataSource;
	
	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	/**
	 * 更新某结束日的 task 状态为“已稽核”.
	 * 当 allDetailsOnline = true 时，只更新所有行程为准时的Task，否则更新全部task状态为“已稽核”。
	 * @throws SQLException 
	 * */
	public void updateStateCheckedByAllDetailsHaveSameState(Date nDate, boolean allDetailsOnline) throws SQLException {
		
		StringBuilder sql = new StringBuilder()
			.append("UPDATE TASK_LIST TS ")
				.append("SET TS.STATE_TASK = ?, TS.UPDATE_USER = ?, TS.UPDATE_DATE = ? ")
				.append("WHERE ")
				.append(String.format("TO_CHAR(TS.NDATE, '%s') = ? ", DATE_NO_PARTITION.getPattern()))
				.append("  AND TS.IS_DELETE = ? ")
				.append("  AND TS.STATE_TASK <> ? ");

		if (allDetailsOnline) {

			sql.append("AND TS.TASK_LIST_ID NOT IN ( ");
			sql.append("  SELECT TD.TASK_LIST_ID ");
			sql.append("  FROM TASK_DETAIL TD ");
			sql.append("  WHERE TD.IS_DELETE = ? ");
			sql.append("    AND TD.DETAIL_STATE_TASK <> ? ");
			sql.append("  GROUP BY TD.TASK_LIST_ID ");
			sql.append(")");
		}
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			
			conn = iCustomerDataSource.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, TaskList.STATE_TASK_CHECKED);
			pstmt.setString(2, "sys");
			pstmt.setDate(3, new java.sql.Date(new Date().getTime()));
			pstmt.setString(4, DATE_NO_PARTITION.format(nDate));
			pstmt.setString(5, TaskList.UNDELETE);
			pstmt.setString(6, TaskList.STATE_TASK_CHECKED);
			
			if(allDetailsOnline) {
				
				pstmt.setString(7, TaskDetail.UNDELETE);
				pstmt.setString(8, TaskDetail.DETAIL_STATE_TASK_ONTIME);
			}
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			
			logger.error("TaskListDao: updateStateCheckedByAllDetailsHaveSameState----------------" + e.getLocalizedMessage());
			
			throw e;
		}
		finally {
			
			if (pstmt != null) {
				
				pstmt.close();
			}
			
			if (conn != null) {
			
				conn.close();
			}
		}
	}
	
	/**
	 * <pre>
	 * 2010-3-24 Timothy
	 *  更新在 开始日期 > beginDate, 任务结束日期 < endDate 之间的 tasks 为 stateTask 状态
	 * </pre>
	 * 
	 * @param stateTask
	 * @param beginDate
	 * @param endDate
	 * @param user
	 * @return
	 * @throws SQLException 
	 */
	public List<TaskList> updateStateAndQueryTheseTasksInDateInterval(String stateTask,
			Date beginDate,
			Date endDate) throws SQLException {

		List<TaskList> lstTaskList = new ArrayList<TaskList>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			// 组成sql的条件部分，因为之后的 查询 & 更新 条件一致
			String whereSql = String
			.format(" WHERE TO_CHAR(SDATE, '%s') BETWEEN ? AND ? AND IS_DELETE = ? ",
			DATE_NO_PARTITION.getPattern().toUpperCase());
			
			// 取得 update sql，并设定更新栏位参数后，更新数据
			// 2010-06-17 Deli add ACCEPT_DATETIME栏位
			String updateSql = String
			.format(" UPDATE %s SET STATE_TASK = ?, UPDATE_USER = ?, UPDATE_DATE = ?, ACCEPT_DATETIME = ? ", "TASK_LIST")
			+ whereSql;
			
			logger.error("Update assigned task sql >>> " + updateSql);
			
			conn = iCustomerDataSource.getConnection();
			pstmt = conn.prepareStatement(updateSql);
			
			// 更改状态为‘03’已接收
			pstmt.setString(1, stateTask);
			pstmt.setString(2, "sys");
			pstmt.setDate(3, new java.sql.Date(new Date().getTime()));
			pstmt.setDate(4, new java.sql.Date(new Date().getTime()));
			pstmt.setString(5, DATE_NO_PARTITION.format(beginDate));
			pstmt.setString(6, DATE_NO_PARTITION.format(endDate));
			pstmt.setString(7, TaskList.UNDELETE);
			
			pstmt.executeUpdate();
			
			// 取得 quert sql后，查询结果并返回结果集
			String querySql = String.format(" SELECT * FROM %s ", "TASK_LIST") + whereSql;
			
			logger.error("Query assigned task sql >>> " + querySql);
			
			pstmt = conn.prepareStatement(querySql);
			pstmt.setString(1, DATE_NO_PARTITION.format(beginDate));
			pstmt.setString(2, DATE_NO_PARTITION.format(endDate));
			pstmt.setString(3, TaskList.UNDELETE);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				TaskList taskList = new TaskList();
				
				lstTaskList.add(taskList);
			}
		} catch (SQLException e) {
			
			logger.error("TaskListDao: updateStateAndQueryTheseTasksInDateInterval----------------------------" + e.getLocalizedMessage());
			throw e;
		}
		finally {
			
			if (rs != null) {
				
				rs.close();
			}
			
			if (pstmt != null) {
				
				pstmt.close();
			}
			
			if (conn != null) {
				
				conn.close();
			}
		}
		
		return lstTaskList;
	}
	
	/**
	 * <pre>
	 * 	查询任务表findById
	 * </pre>
	 * 
	 * @param taskListId
	 *          任务编号
	 * @return TaskList
	 */
	public List<Map<String, Object>> findTaskListById(int taskListId) {
		
		StringBuilder strSql = new StringBuilder()
			.append(" SELECT ")
			.append(" 	TASK_LIST_ID,")
			.append(" 	SDATE,")
			.append(" 	NDATE,")
			.append(" 	JH_ID,")
			.append(" 	JH_NAME,")
			.append(" 	STATE_TASK,")
			.append(" 	WEEK_FILE_UPLOAD,")
			.append("	WEEK_FILE_UPLOAD_DATA,")
			.append(" 	STATE_CHECK,")
			.append(" 	IS_CHARGE_AGREE,")
			.append(" 	IS_CHIEP_AGREE,")
			.append(" 	IS_DELETE,")
			.append(" 	CREATE_USER,")
			.append(" 	CREATE_DATE,")
			.append(" 	UPDATE_USER,")
			.append(" 	UPDATE_DATE,")
			.append(" 	TASK_TIME_UPLOAD,")
			.append(" 	FILE_NAME,")
			.append(" 	ACCEPT_DATETIME ")
			.append(" FROM")
			.append(" 	TASK_LIST ")
			.append(" WHERE")
			.append(" 	TASK_LIST_ID=?");
		
		return iCustomerJdbcOperations.getJdbcOperations().queryForList(strSql.toString(), taskListId);
	}
}
