/**
 * 
 */
package com.want.batch.job.ghbatch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.ghbatch.dao.TaskListDao;
import com.want.batch.job.ghbatch.pojo.TaskList;
import com.want.batch.job.ghbatch.util.DateUtils;

/**
 * @author MandyZhang
 *
 */
@Component
public class AssignedJob extends AbstractWantJob {

	@Autowired
	private TaskListDao taskListDao;
	
	public void execute() throws Exception {
		
		@SuppressWarnings("unused")
		List<TaskList> tasks = this.getTaskListDao()
		// 暂时在直接在分派的时候调整为 {@link TaskList#STATE_TASK_ACCEPTED}, 且无需进行mail 发送...
			.updateStateAndQueryTheseTasksInDateInterval(TaskList.STATE_TASK_ACCEPTED,
				DateUtils.shiftMondayThisWeek(),
				DateUtils.shiftSaturdayThisWeek());

		// this.seanMail(tasks);
	}
	
	/**
	 * @return the taskListDao
	 */
	public TaskListDao getTaskListDao() {
		return taskListDao;
	}

	/**
	 * @param taskListDao the taskListDao to set
	 */
	public void setTaskListDao(TaskListDao taskListDao) {
		this.taskListDao = taskListDao;
	}
}
