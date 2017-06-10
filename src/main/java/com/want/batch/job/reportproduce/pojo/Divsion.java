// ~ Package Declaration
// ==================================================

package com.want.batch.job.reportproduce.pojo;

// ~ Comments
// ==================================================

/**
 * 
 * 事业部.
 * 
 * <pre>
 * 历史纪录：
 * 2010-3-3 Timothy
 * 	新建文件
 * </pre>
 * 
 * @author <pre>
 * SD
 * 	Timothy
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
public class Divsion {

	// ~ Static Fields
	// ==================================================

	private static final long serialVersionUID = 9195782743967160101L;
	
	/**
	 * wonci, 2011-4-14 <br> 
	 * 县城休闲
	 */
	public static final String TownXX = "30";
	
	/**
	 * wonci, 2011-4-14 <br> 
	 * 县城乳饮
	 */
	public static final String TownRY = "31";
	
	/**
	 * wonci, 2011-8-3 <br> 
	 * STATUS=1
	 */
	public static final String STATUS_TYPE = "1";

	// ~ Fields
	// ==================================================

	private String name;
	private Integer channel;
	
	/**
	 * wonci, 2012-3-28 <br> 
	 */
	private String status;
	
	private Integer id;

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * @return 传回 name。
	 */
	public String getName() {

		return this.name;
	}

	/**
	 * @param name
	 *          要设定的 name。
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * @return 传回 channel。
	 */
	public Integer getChannel() {

		return this.channel;
	}

	/**
	 * @param channel
	 *          要设定的 channel。
	 */
	public void setChannel(Integer channel) {

		this.channel = channel;
	}

	/**
	 * @return 傳回 status。
	 */
	public String getStatus() {
	
		return status;
	}

	/**
	 * @param status 要設定的 status。
	 */
	public void setStatus(String status) {
	
		this.status = status;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
}
