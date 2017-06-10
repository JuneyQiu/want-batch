// ~ Package Declaration
// ==================================================

package com.want.batch.job.reportproduce.pojo;

// ~ Comments
// ==================================================

/**
 * 终端特陈图片关联档.
 * 
 * <pre>
 * 歷史紀錄：
 * 2010-3-26 Deli
 * 	新建檔案
 * </pre>
 * 
 * @author <pre>
 * SD
 * 	
 * PG
 * Deli
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
public class TaskStoreSpecialPicture {

	// ~ Static Fields
	// ==================================================

	private static final long serialVersionUID = -1845901050453106666L;

	// ~ Fields
	// ==================================================

	private Integer taskStoreSpecialSid;

	private String taskPictureSid;

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * @return 傳回 taskStoreSpecialSid。
	 */
	public Integer getTaskStoreSpecialSid() {

		return this.taskStoreSpecialSid;
	}

	/**
	 * @param taskStoreSpecialSid
	 *          要設定的 taskStoreSpecialSid。
	 */
	public void setTaskStoreSpecialSid(Integer taskStoreSpecialSid) {

		this.taskStoreSpecialSid = taskStoreSpecialSid;
	}

	/**
	 * @return 傳回 taskPictureSid。
	 */
	public String getTaskPictureSid() {

		return this.taskPictureSid;
	}

	/**
	 * @param taskPictureSid
	 *          要設定的 taskPictureSid。
	 */
	public void setTaskPictureSid(String taskPictureSid) {

		this.taskPictureSid = taskPictureSid;
	}
}
