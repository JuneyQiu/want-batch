// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.pojo;

import java.util.Date;

// ~ Comments
// ==================================================

/**
 * 行程表.
 * 
 * <pre>
 * 历史纪录：
 * 2010-3-8 Lucien
 * 	新建文件
 * </pre>
 * 
 * @author <pre>
 * SD
 * 	Lucien
 * PG
 * 
 * UT
 * 
 * MA
 * </pre>
 * @version $Rev$
 * 
 *          <p/>
 *          $Id$
 * 
 */
public class TaskDetail {

	// ~ Static Fields
	// ==================================================

	private static final long serialVersionUID = 8425472233566422768L;

	public static final String UNDELETE = "0";
	public static final String DELETED = "1";
	
	/**
	 * 01：交通在途
	 */
	public static final String TASK_TYPE_TRAFFIC = "01";

	/**
	 * 02：客户
	 */
	public static final String TASK_TYPE_CUSTOMER = "02";

	/**
	 * 03：终端
	 */
	public static final String TASK_TYPE_STORE = "03";

	/**
	 * 01：准时
	 */
	public static final String DETAIL_STATE_TASK_ONTIME = "01";

	/**
	 * 02：延迟
	 */
	public static final String DETAIL_STATE_TASK_DELAY = "02";

	/**
	 * 03：未上传
	 */
	public static final String DETAIL_STATE_TASK_UNPROCESSED = "03";

	// ~ Fields
	// ==================================================

	private int taskListId;
	private Date taskDate;
	private String taskType;
	private String taskWayBegin;
	private String taskWayEnd;
	private Date taskTimeUpload;
	private String taskDescrib;
	private String detailStateTask;
	private String isDelete;
	private String remark;

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * @return 传回 taskListId。
	 */
	public int getTaskListId() {

		return this.taskListId;
	}

	/**
	 * @param taskListId
	 *          要设定的 taskListId。
	 */
	public void setTaskListId(int taskListId) {

		this.taskListId = taskListId;
	}

	/**
	 * @return 传回 taskDate。
	 */
	public Date getTaskDate() {

		return this.taskDate;
	}

	/**
	 * @param taskDate
	 *          要设定的 taskDate。
	 */
	public void setTaskDate(Date taskDate) {

		this.taskDate = taskDate;
	}

	/**
	 * @return 传回 taskType。
	 */
	public String getTaskType() {

		return this.taskType;
	}

	/**
	 * @param taskType
	 *          要设定的 taskType。
	 */
	public void setTaskType(String taskType) {

		this.taskType = taskType;
	}

	/**
	 * @return 传回 taskWayBegin。
	 */
	public String getTaskWayBegin() {

		return this.taskWayBegin;
	}

	/**
	 * @param taskWayBegin
	 *          要设定的 taskWayBegin。
	 */
	public void setTaskWayBegin(String taskWayBegin) {

		this.taskWayBegin = taskWayBegin;
	}

	/**
	 * @return 传回 taskWayEnd。
	 */
	public String getTaskWayEnd() {

		return this.taskWayEnd;
	}

	/**
	 * @param taskWayEnd
	 *          要设定的 taskWayEnd。
	 */
	public void setTaskWayEnd(String taskWayEnd) {

		this.taskWayEnd = taskWayEnd;
	}

	/**
	 * @return 传回 taskTimeUpload。
	 */
	public Date getTaskTimeUpload() {

		return this.taskTimeUpload;
	}

	/**
	 * @param taskTimeUpload
	 *          要设定的 taskTimeUpload。
	 */
	public void setTaskTimeUpload(Date taskTimeUpload) {

		this.taskTimeUpload = taskTimeUpload;
	}

	/**
	 * @return 传回 taskDescrib。
	 */
	public String getTaskDescrib() {

		return this.taskDescrib;
	}

	/**
	 * @param taskDescrib
	 *          要设定的 taskDescrib。
	 */
	public void setTaskDescrib(String taskDescrib) {

		this.taskDescrib = taskDescrib;
	}

	/**
	 * @return 传回 detailStateTask。
	 */
	public String getDetailStateTask() {

		return this.detailStateTask;
	}

	/**
	 * @param detailStateTask
	 *          要设定的 detailStateTask。
	 */
	public void setDetailStateTask(String detailStateTask) {

		this.detailStateTask = detailStateTask;
	}

	/**
	 * @return 传回 remark。
	 */
	public String getRemark() {

		return this.remark;
	}

	/**
	 * @param remark
	 *          要设定的 remark。
	 */
	public void setRemark(String remark) {

		this.remark = remark;
	}
}
