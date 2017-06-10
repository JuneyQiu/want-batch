package com.want.batch.job.reportproduce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.reportproduce.dao.BatchReportAttributeTblDao;
import com.want.batch.job.reportproduce.dao.DirectiveTblDao;
import com.want.batch.job.reportproduce.report.GHStoreReportJob;
import com.want.batch.job.reportproduce.report.SpecialDisplayActualJob;
import com.want.batch.job.reportproduce.report.SpecialDisplayApplicationBataJob;
import com.want.batch.job.reportproduce.report.SpecialDisplayApplicationJob;
import com.want.batch.job.reportproduce.report.TchUncheckedReportBataJob;
import com.want.batch.job.reportproduce.report.TchUncheckedReportJob;

/**
 * 监控指令排程.
 * 
 * <table>
 * <tr>
 * <th>日期</th>
 * <th>变更说明</th>
 * </tr>
 * <tr>
 * <td>2013-6-20</td>
 * <td>Mirabelle新建</td>
 * </tr>
 * <tr colspan='2'>
 * <td>junit执行结束时，若线程未运行完成，线程会被终止，因此无法得到生成的报表，<br>
 * 故使用junit运行代码时需要根据下列的注释调整代码</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Mirabelle
 */
@Component
// 当使用junit测试时不继承AbstractWantJob(40,56-57行)、使得excute方法有返回值(156行)，供junit比对数据是否正确；60-61行，76-77行
// public class MonitorJob {
public class MonitorJob extends AbstractWantJob {

	// 特陈实际报表下载job
	@Autowired
	public SpecialDisplayActualJob specialDisplayActualJob;

	// 指令dao
	@Autowired
	public DirectiveTblDao directiveTblDao;

	@Autowired
	public BatchReportAttributeTblDao batchReportAttributeTblDao;
	
	// add 2013-08-01 mandy 稽核专员终端报表
	@Autowired
	public GHStoreReportJob gHStoreReportJob;
	
	// add 2013-08-09 mandy 特陈计划报表
	@Autowired
	public SpecialDisplayApplicationJob specialDisplayApplicationJob;
	
	// add 2013-09-12 mandy 特陈实际未检核明细表
	@Autowired
	public TchUncheckedReportJob tchUncheckedReportJob;
	
	//add 2014-09-18 mandy 特陈计划报表测试
	@Autowired
	public SpecialDisplayApplicationBataJob specialDisplayApplicationBataJob;
	
	//add 2014-09-19 mandy 特陈未检核报表测试
	@Autowired
	public TchUncheckedReportBataJob tchUncheckedReportBataJob;
	
	public static final long TASK_TIMEOUT = 3600000;

	public void execute() throws Exception {

		logger.info("wake up working");

		// 记录运行的线程
		ArrayList<Future<Boolean>> tasks = new ArrayList<Future<Boolean>>();
		HashMap<Integer,Elapse> elapses = new HashMap<Integer,Elapse>();

		while (true) {
			// 取得线程最大的启用数量
			String attributeVal = batchReportAttributeTblDao
					.getAttributeValByFuncIdAndAttrId("MonitorJob",
							"CONCURRENT");
			int concurrency = Integer.parseInt(attributeVal);
			int maxNoExcuteTasks = concurrency*3;

			ArrayList<Map<String, Object>> noExcuteTasks = directiveTblDao
					.getNoExcuteTask(maxNoExcuteTasks);
			
			if (noExcuteTasks == null || noExcuteTasks.size() <= 0)
				break;

			for (int jid=0; jid<noExcuteTasks.size(); jid++) {
				
				Map<String, Object> newjob = noExcuteTasks.get(jid);
				
				boolean added = false;
				boolean isLast = (jid==(noExcuteTasks.size()-1) && tasks.size() > 0);
				
				while (!added || isLast) {
					
					
					if (!added && tasks.size() < concurrency) {
						if ("specialDisplayActualJob"
								.equalsIgnoreCase((String) (newjob
										.get("EXCUTE_JOB")))) {

							Future<Boolean> task = specialDisplayActualJob.run(newjob);
							logger.info("run " + task);
							tasks.add(task);
							added = true;
						}
						// add 2013-08-01 mandy 稽核专员终端报表
						else if("GHStoreReportJob"
								.equalsIgnoreCase((String) (newjob
										.get("EXCUTE_JOB")))) {
							
							Future<Boolean> task = gHStoreReportJob.run(newjob);
							logger.info("run " + task);
							tasks.add(task);
							added = true;
						}
						// add 2013-08-12 mandy 特陈计划报表
						else if("SpecialDisplayApplicationJob"
								.equalsIgnoreCase((String) (newjob
										.get("EXCUTE_JOB")))) {
							
							Future<Boolean> task = specialDisplayApplicationJob.run(newjob);
							logger.info("run " + task);
							tasks.add(task);
							added = true;
						}
						// add 2013-08-12 mandy 特陈实际
						else if("TchUncheckedReportJob"
								.equalsIgnoreCase((String) (newjob
										.get("EXCUTE_JOB")))) {
							
							Future<Boolean> task = tchUncheckedReportJob.run(newjob);
							logger.info("run " + task);
							tasks.add(task);
							added = true;
						}
						// 2014-09-18 add mandy 测试
						else if("SpecialDisplayApplicationBataJob".equalsIgnoreCase((String) (newjob.get("EXCUTE_JOB")))) {
							
							Future<Boolean> task = specialDisplayApplicationBataJob.run(newjob);
							tasks.add(task);
							added = true;
						}
						// 2014-09-19 add mandy 测试
						else if("TchUncheckedReportBataJob"
								.equalsIgnoreCase((String) (newjob.get("EXCUTE_JOB")))) {
							
							Future<Boolean> task = tchUncheckedReportBataJob.run(newjob);
							tasks.add(task);
							added = true;
						}
					}
						
					StringBuffer buf = new StringBuffer("Running " + tasks.size() + " tasks: process to " + jid + "'th of "+ noExcuteTasks.size() + " elements [");
						
					for (int i = (tasks.size()-1); i>=0 ; i--) {
						Future<Boolean> task = tasks.get(i);
						Integer taskHashCode = new Integer(task.hashCode());
						
						Elapse elapse = elapses.get(taskHashCode);
						if (elapse == null) {
							elapse = new Elapse(System.currentTimeMillis());
							elapses.put(taskHashCode, elapse);
						}
						
						buf.append(" " + (int)(elapse.getElapseTime()/1000));
						
						if (task.isDone() || task.isCancelled()) {
							tasks.remove(i);
							elapses.remove(new Integer(task.hashCode()));
							logger.info("task " + i + " done: " + task);
						} else if (elapse.getElapseTime() > TASK_TIMEOUT) {
							logger.error("task running over 1 hr, stop and remove");
							tasks.remove(i);
							elapses.remove(taskHashCode);
							task.cancel(true);
						}
					}
					
					buf.append("]");
					logger.info(buf.toString());
					
					if (tasks.size() <= 0)
						isLast = false;

					if ((noExcuteTasks.size()-jid) < maxNoExcuteTasks) {
						ArrayList<Map<String, Object>> newTasks = directiveTblDao.getNoExcuteTask(maxNoExcuteTasks-(noExcuteTasks.size()-jid)+1);
						if (newTasks != null && newTasks.size() > 0) {
							noExcuteTasks.addAll(newTasks);
							logger.info("append tasks " + newTasks.size());
						}
					}

					try {
						Thread.sleep(2000);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
				
			}
		}
		
		logger.info("all jobs done, bye bye");
	}

}


