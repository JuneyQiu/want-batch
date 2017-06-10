// ~ Package Declaration
// ==================================================

package com.want.batch.job.reportproduce.pojo;

// ~ Comments
// ==================================================

/**
 * 特陈与品项清单关联表.
 * 
 * <pre>
 * 历史纪录：
 * 2010-3-17 Lucien
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
public class SpecialExhibitProduct {

	// ~ Static Fields
	// ==================================================

	private static final long serialVersionUID = 2004530339303616228L;

	// ~ Fields
	// ==================================================

	private Integer taskStoreSpecialExhibitSid;

	private String productId;

	private String productName;

	private String isHaveSpecialExhibit;

	private String dateRoute;
	
	private Integer id;

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================
	
	/**
	 * @return 传回 taskStoreSpecialExhibitSid。
	 */
	public Integer getTaskStoreSpecialExhibitSid() {

		return taskStoreSpecialExhibitSid;
	}

	/**
	 * @param taskStoreSpecialExhibitSid
	 *          要设定的 taskStoreSpecialExhibitSid。
	 */
	public void setTaskStoreSpecialExhibitSid(Integer taskStoreSpecialExhibitSid) {

		this.taskStoreSpecialExhibitSid = taskStoreSpecialExhibitSid;
	}

	/**
	 * @return 传回 productId。
	 */
	public String getProductId() {

		return productId;
	}

	/**
	 * @param productId
	 *          要设定的 productId。
	 */
	public void setProductId(String productId) {

		this.productId = productId;
	}

	/**
	 * @return 传回 productName。
	 */
	public String getProductName() {

		return productName;
	}

	/**
	 * @param productName
	 *          要设定的 productName。
	 */
	public void setProductName(String productName) {

		this.productName = productName;
	}

	/**
	 * @return 传回 isHaveSpecialExhibit。
	 */
	public String getIsHaveSpecialExhibit() {

		return isHaveSpecialExhibit;
	}

	/**
	 * @param isHaveSpecialExhibit
	 *          要设定的 isHaveSpecialExhibit。
	 */
	public void setIsHaveSpecialExhibit(String isHaveSpecialExhibit) {

		this.isHaveSpecialExhibit = isHaveSpecialExhibit;
	}

	/**
	 * @return 传回 dateRoute。
	 */
	public String getDateRoute() {

		return dateRoute;
	}

	/**
	 * @param dateRoute
	 *          要设定的 dateRoute。
	 */
	public void setDateRoute(String dateRoute) {

		this.dateRoute = dateRoute;
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
