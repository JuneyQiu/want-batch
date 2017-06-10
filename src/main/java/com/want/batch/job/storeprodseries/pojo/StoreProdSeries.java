// ~ Package Declaration
// ==================================================

package com.want.batch.job.storeprodseries.pojo;

import java.util.Date;

/**
 * 终端品项陈列标准表.
 * 
 * @author MandyZhang
 *
 */
public class StoreProdSeries {

	// ~ Static Fields
	// ==================================================
	private static final long serialVersionUID = -3147022655151793347L;

	// ~ Fields
	// ==================================================

	// 生效日期
	private Date effectiveDate;

	// 级别
	private String levelId;

	// 分公司编码
	private String companyId;

	// 营业所编码
	private String branchId;

	// 第五层产品编号
	private String lv5Id;

	// 终端类型编号
	private String storeAreaId;

	// 主推品项类型编号
	private String recommendId;

	// 陈列标准
	private Integer exhibitStandard;

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * @return 传回 effectiveDate。
	 */
	public Date getEffectiveDate() {

		return effectiveDate;
	}

	/**
	 * @param effectiveDate
	 *          要设定的 effectiveDate。
	 */
	public void setEffectiveDate(Date effectiveDate) {

		this.effectiveDate = effectiveDate;
	}

	/**
	 * @return 传回 levelId。
	 */
	public String getLevelId() {

		return levelId;
	}

	/**
	 * @param levelId
	 *          要设定的 levelId。
	 */
	public void setLevelId(String levelId) {

		this.levelId = levelId;
	}

	/**
	 * @return 传回 companyId。
	 */
	public String getCompanyId() {

		return companyId;
	}

	/**
	 * @param companyId
	 *          要设定的 companyId。
	 */
	public void setCompanyId(String companyId) {

		this.companyId = companyId;
	}

	/**
	 * @return 传回 branchId。
	 */
	public String getBranchId() {

		return branchId;
	}

	/**
	 * @param branchId
	 *          要设定的 branchId。
	 */
	public void setBranchId(String branchId) {

		this.branchId = branchId;
	}

	/**
	 * @return 传回 lv5Id。
	 */
	public String getLv5Id() {

		return lv5Id;
	}

	/**
	 * @param lv5Id
	 *          要设定的 lv5Id。
	 */
	public void setLv5Id(String lv5Id) {

		this.lv5Id = lv5Id;
	}

	/**
	 * @return 传回 storeAreaId。
	 */
	public String getStoreAreaId() {

		return storeAreaId;
	}

	/**
	 * @param storeAreaId
	 *          要设定的 storeAreaId。
	 */
	public void setStoreAreaId(String storeAreaId) {

		this.storeAreaId = storeAreaId;
	}

	/**
	 * @return 传回 recommendId。
	 */
	public String getRecommendId() {

		return recommendId;
	}

	/**
	 * @param recommendId
	 *          要设定的 recommendId。
	 */
	public void setRecommendId(String recommendId) {

		this.recommendId = recommendId;
	}

	/**
	 * @return 传回 exhibitStandard。
	 */
	public Integer getExhibitStandard() {

		return exhibitStandard;
	}

	/**
	 * @param exhibitStandard
	 *          要设定的 exhibitStandard。
	 */
	public void setExhibitStandard(Integer exhibitStandard) {

		this.exhibitStandard = exhibitStandard;
	}

}
