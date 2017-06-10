// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.pojo;

// ~ Comments
// ==================================================

/**
 * 产品基本资料表
 * 
 * <pre>
 * 历史纪录：
 * 2010-2-25 Chester
 * 	新建文件
 * </pre>
 * 
 * @author 
 * <pre>
 * SD
 * 	Chester
 * PG
 *
 * UT
 *
 * MA
 * </pre>
 * @version $Rev$
 *
 * <p/> $Id$
 *
 */
public class ProdInfo {

	// ~ Static Fields
	// ==================================================

	/**
	 * Chester, 2010-2-25 <br> 
	 * TODO: 字段的注释说明
	 */
	private static final long serialVersionUID = -3688878727572357763L;
	
	// ~ Fields
	// ==================================================

	private String prodId;
	private String name1;
	private String name2;
	private String specTaste;
	private String materiatType;
	private String baseUnit;
	private String groupTypeId;
	
	// 规格包装组编号
	private String lv5Id;
	
	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * @return 传回 name1。
	 */
	public String getName1() {
	
		return name1;
	}
	
	/**
	 * @return 传回 prodId。
	 */
	public String getProdId() {
	
		return prodId;
	}
	
	/**
	 * @param prodId 要设定的 prodId。
	 */
	public void setProdId(String prodId) {
	
		this.prodId = prodId;
	}

	/**
	 * @param name1 要设定的 name1。
	 */
	public void setName1(String name1) {
	
		this.name1 = name1;
	}

	/**
	 * @return 传回 name2。
	 */
	public String getName2() {
	
		return name2;
	}
	
	/**
	 * @param name2 要设定的 name2。
	 */
	public void setName2(String name2) {
	
		this.name2 = name2;
	}
	
	/**
	 * @return 传回 specTaste。
	 */
	public String getSpecTaste() {
	
		return specTaste;
	}
	
	/**
	 * @param specTaste 要设定的 specTaste。
	 */
	public void setSpecTaste(String specTaste) {
	
		this.specTaste = specTaste;
	}

	/**
	 * @return 传回 materiatType。
	 */
	public String getMateriatType() {
	
		return materiatType;
	}
	
	/**
	 * @param materiatType 要设定的 materiatType。
	 */
	public void setMateriatType(String materiatType) {
	
		this.materiatType = materiatType;
	}

	/**
	 * @return 传回 baseUnit。
	 */
	public String getBaseUnit() {
	
		return baseUnit;
	}

	/**
	 * @param baseUnit 要设定的 baseUnit。
	 */
	public void setBaseUnit(String baseUnit) {
	
		this.baseUnit = baseUnit;
	}

	/**
	 * @return 传回 groupTypeId。
	 */
	public String getGroupTypeId() {
	
		return groupTypeId;
	}

	/**
	 * @param groupTypeId 要设定的 groupTypeId。
	 */
	public void setGroupTypeId(String groupTypeId) {
	
		this.groupTypeId = groupTypeId;
	}
	
	/**
	 * @return 传回 lv5Id。
	 */
	public String getLv5Id() {
	
		return lv5Id;
	}
	
	/**
	 * @param lv5Id 要设定的 lv5Id。
	 */
	public void setLv5Id(String lv5Id) {
	
		this.lv5Id = lv5Id;
	}
}
