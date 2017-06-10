// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.pojo;

import java.util.Date;

// ~ Comments
// ==================================================

/**
 * 稽核任务清单表.
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
public class TaskList {

	// ~ Static Fields
	// ==================================================

	private static final long serialVersionUID = 7795431839785080110L;

	public static final String WEEK_FILE_UNUPLOAD = "0";
	public static final String WEEK_FILE_UPLOAD = "1";
	
	public static final String UNDELETE = "0";
	public static final String DELETED = "1";

	/**
	 * 01：未指派
	 */
	public static final String STATE_TASK_UNASSIGN = "01";

	/**
	 * 02：已指派
	 */
	public static final String STATE_TASK_ASSIGNED = "02";

	/**
	 * 03：已接收
	 */
	public static final String STATE_TASK_ACCEPTED = "03";

	/**
	 * 04：进行中
	 */
	public static final String STATE_TASK_PROGRESSING = "04";

	/**
	 * 05：已稽核
	 */
	public static final String STATE_TASK_CHECKED = "05";

	/**
	 * 06：已取消
	 */
	public static final String STATE_TASK_CANCEL = "06";

	// ~ Fields
	// ==================================================

	private Date sdate;
	private Date ndate;
	private String jhId;
	private String jhName;
	private String stateTask;
	private String weekFileUpload;
	private byte[] weekFileUploadData;
	private String stateCheck;
	private String isChargeAgree;
	private String isChiepAgree;
	private String isDelete;
	private Date taskTimeUpload;

	// 2010-06-17 Deli add
	private Date acceptDatetime;

	/**
	 * Timothy, 2010-3-19 <br>
	 * 上传周报表文件名称
	 */
	private String fileName;

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * @return 传回 fileName。
	 */
	public String getFileName() {

		return this.fileName;
	}

	/**
	 * @param fileName
	 *          要设定的 fileName。
	 */
	public void setFileName(String fileName) {

		this.fileName = fileName;
	}

	/**
	 * @return 传回 sdate。
	 */
	public Date getSdate() {

		return this.sdate;
	}

	/**
	 * @param sdate
	 *          要设定的 sdate。
	 */
	public void setSdate(Date sdate) {

		this.sdate = sdate;
	}

	/**
	 * @return 传回 ndate。
	 */
	public Date getNdate() {

		return this.ndate;
	}

	/**
	 * @param ndate
	 *          要设定的 ndate。
	 */
	public void setNdate(Date ndate) {

		this.ndate = ndate;
	}

	/**
	 * @return 传回 jhId。
	 */
	public String getJhId() {

		return this.jhId;
	}

	/**
	 * @param jhId
	 *          要设定的 jhId。
	 */
	public void setJhId(String jhId) {

		this.jhId = jhId;
	}

	/**
	 * @return 传回 jhName。
	 */
	public String getJhName() {

		return this.jhName;
	}

	/**
	 * @param jhName
	 *          要设定的 jhName。
	 */
	public void setJhName(String jhName) {

		this.jhName = jhName;
	}

	/**
	 * @return 传回 stateTask。
	 */
	public String getStateTask() {

		return this.stateTask;
	}

	/**
	 * @param stateTask
	 *          要设定的 stateTask。
	 */
	public void setStateTask(String stateTask) {

		this.stateTask = stateTask;
	}

	/**
	 * @return 传回 weekFileUpload。
	 */
	public String getWeekFileUpload() {

		return this.weekFileUpload;
	}

	/**
	 * @param weekFileUpload
	 *          要设定的 weekFileUpload。
	 */
	public void setWeekFileUpload(String weekFileUpload) {

		this.weekFileUpload = weekFileUpload;
	}

	/**
	 * @return 传回 weekFileUploadData。
	 */
	public byte[] getWeekFileUploadData() {

		return this.weekFileUploadData;
	}

	/**
	 * @param weekFileUploadData
	 *          要设定的 weekFileUploadData。
	 */
	public void setWeekFileUploadData(byte[] weekFileUploadData) {

		this.weekFileUploadData = weekFileUploadData;
	}

	/**
	 * @return 传回 stateCheck。
	 */
	public String getStateCheck() {

		return this.stateCheck;
	}

	/**
	 * @param stateCheck
	 *          要设定的 stateCheck。
	 */
	public void setStateCheck(String stateCheck) {

		this.stateCheck = stateCheck;
	}

	/**
	 * @return 传回 isChargeAgree。
	 */
	public String getIsChargeAgree() {

		return this.isChargeAgree;
	}

	/**
	 * @param isChargeAgree
	 *          要设定的 isChargeAgree。
	 */
	public void setIsChargeAgree(String isChargeAgree) {

		this.isChargeAgree = isChargeAgree;
	}

	/**
	 * @return 传回 isChiepAgree。
	 */
	public String getIsChiepAgree() {

		return this.isChiepAgree;
	}

	/**
	 * @param isChiepAgree
	 *          要设定的 isChiepAgree。
	 */
	public void setIsChiepAgree(String isChiepAgree) {

		this.isChiepAgree = isChiepAgree;
	}

	/**
	 * @return 传回 isDelete。
	 */
	public String getIsDelete() {

		return this.isDelete;
	}

	/**
	 * @param isDelete
	 *          要设定的 isDelete。
	 */
	public void setIsDelete(String isDelete) {

		this.isDelete = isDelete;
	}

	/**
	 * @param taskTimeUpload
	 *          要设定的 taskTimeUpload。
	 */
	public void setTaskTimeUpload(Date taskTimeUpload) {

		this.taskTimeUpload = taskTimeUpload;
	}

	/**
	 * @return 传回 taskTimeUpload。
	 */
	public Date getTaskTimeUpload() {

		return this.taskTimeUpload;
	}

	/**
	 * @return 傳回 acceptDatetime。
	 */
	public Date getAcceptDatetime() {

		return acceptDatetime;
	}

	/**
	 * @param acceptDatetime
	 *          要設定的 acceptDatetime。
	 */
	public void setAcceptDatetime(Date acceptDatetime) {

		this.acceptDatetime = acceptDatetime;
	}
}
