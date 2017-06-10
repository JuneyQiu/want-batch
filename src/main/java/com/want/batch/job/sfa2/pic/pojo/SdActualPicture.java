// ~ Package Declaration
// ==================================================

package com.want.batch.job.sfa2.pic.pojo;

// ~ Comments
// ==================================================

/**
 * SdActualPicture.
 * 
 * <pre>
 * 歷史紀錄：
 * 2010-7-9 Lucien
 * 	新建檔案
 * </pre>
 * 
 * @author <pre>
 * SD
 * 	
 * PG
 * Lucien
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
public class SdActualPicture  {

	// ~ Fields
	// ==================================================

	private Long actualDisplaySid;
	private String pictureSid;
	

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * @return 傳回 pictureSid。
	 */
	public String getPictureSid() {
	
		return pictureSid;
	}
	 
	public Long getActualDisplaySid() {
		return actualDisplaySid;
	}

	public void setActualDisplaySid(Long actualDisplaySid) {
		this.actualDisplaySid = actualDisplaySid;
	}

	/**
	 * @param pictureSid 要設定的 pictureSid。
	 */
	public void setPictureSid(String pictureSid) {
	
		this.pictureSid = pictureSid;
	}

}
