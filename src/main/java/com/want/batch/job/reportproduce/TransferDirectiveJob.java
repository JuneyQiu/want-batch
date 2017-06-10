package com.want.batch.job.reportproduce;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.reportproduce.dao.DirectiveHistoryLogDao;
import com.want.batch.job.reportproduce.dao.DirectiveTblDao;
import com.want.batch.job.reportproduce.pojo.Constant;

/**
 * 删除过期报表和指令.
 * 
 * <table>
 * 	<tr>
 * 		<th>日期</th>
 * 		<th>变更说明</th>
 * 	</tr>
 * 	<tr>
 * 		<td>2013-6-20</td>
 * 		<td>Mirabelle新建</br>在报表值保留1周，一周之后就会被删除</td>
 * 	</tr>
 * </table>
 *
 *@author Mirabelle
 */
@Component
public class TransferDirectiveJob extends AbstractWantJob {
	
	@Autowired
	public DirectiveTblDao directiveTblDao;
	
	@Autowired
	public DataSource iCustomerDataSource;
	
	@Autowired
	public DirectiveHistoryLogDao directiveHistoryLogDao;

	public void execute() throws Exception {
		
		Connection conn = null;
		
		try {

			conn = iCustomerDataSource.getConnection();
			conn.setAutoCommit(false);
			
			// 取得8天前产生的指令信息
			List<Map<String, Object>> tasks = this.directiveTblDao.getTaskByDeleted();
			
			List<Map<String, Object>> batchDelete = new ArrayList<Map<String, Object>>();
			
			// 删除指定路径上的报表
			for (int i = 0; i < tasks.size(); i++) {
				
				Map<String, Object> task = tasks.get(i);
				
				logger.info("资料SID：" + task.get("SID"));
				
				String filePath = task.get("ROOT_PATH").toString() + task.get("FILE_NAME");
				File file = new File(filePath + ".xls");
				
				logger.info("要删除的文件路径： " + filePath);
				
				logger.info("文件是否存在：" + file.exists());
				// 如果文件存在，则删除文件
				if (file.exists()) {
					
					file.delete();
					logger.info("文件已删除" + filePath + new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date()));
				}
				
				this.directiveHistoryLogDao.addHistory(conn, task);
				logger.info("资料移转完成" + task.get("SID"));
				
				// 2013-10-17 modify mandy 删除指令，每1000笔删除一次
				batchDelete.add(task);
				
				if(batchDelete.size() == 1000 || i == tasks.size() - 1) {
					
					this.directiveTblDao.deleteTask(conn, batchDelete);
					
					batchDelete.clear();
				}
				
//				logger.info("资料删除完成" + task.get("SID"));
			}
			
			// 将这些资料新增入history表中
//			this.directiveHistoryLogDao.addHistory(conn, tasks);
//			logger.info("资料移转完成");
			// 删除指令信息
//			this.directiveTblDao.deleteTask(conn, tasks);
//			logger.info("资料删除完成");
			conn.commit();
			conn.setAutoCommit(true);
			logger.info("删除指令排程执行完成");
		}
		catch (Exception e) {

			logger.error(Constant.generateExceptionMessage(e));
			conn.rollback();
			throw e;
		}
		finally {
			
			if (conn != null) {
				
				conn.close();
			}
		}
	}

}
