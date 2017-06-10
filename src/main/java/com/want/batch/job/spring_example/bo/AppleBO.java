package com.want.batch.job.spring_example.bo;

import java.math.BigDecimal;

import com.want.batch.job.spring_example.util.bo.BaseObject;

/**
 * apple的值对象类，对应的数据库表是：XXX_APPLE_TEST_TBL
 * 
 * @author suyulin
 */
public class AppleBO extends BaseObject {

	private static final long serialVersionUID = 3257528263961828326L;

	private long id;
	private String name;
	
	private long sid; //与apple无关
	private BigDecimal money = new BigDecimal("0"); //与apple无关
	private String companyId;
	
	private String prodId;
	private BigDecimal storeQty = new BigDecimal("0"); //库存
	private BigDecimal last3m = new BigDecimal("0"); //last3m
	private BigDecimal xishu = new BigDecimal("0"); //xishu
	private BigDecimal qty = new BigDecimal("0"); //qty
	private BigDecimal qytqQty = new BigDecimal("0"); //qytq
	private String subcity;
	private BigDecimal maxQty = new BigDecimal("0"); //MAX_QTY
	
	
	private String C_ID;
	private String P_ID;
	private String QTY2;
	private String yearmonth;
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		if(money != null) {
			this.money = money;
		}
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getProdId() {
		return prodId;
	}

	public void setProdId(String prodId) {
		this.prodId = prodId;
	}

	public BigDecimal getStoreQty() {
		return storeQty;
	}

	public void setStoreQty(BigDecimal storeQty) {
		if(storeQty != null) {
			this.storeQty = storeQty;
		}
	}

	public BigDecimal getLast3m() {
		return last3m;
	}

	public void setLast3m(BigDecimal last3m) {
		if(last3m != null) {
			this.last3m = last3m;
		}
	}

	public BigDecimal getXishu() {
		return xishu;
	}

	public void setXishu(BigDecimal xishu) {
		if(xishu != null) {
			this.xishu = xishu;
		}
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		if(qty != null) {
			this.qty = qty;
		}
	}

	public BigDecimal getQytqQty() {
		return qytqQty;
	}

	public void setQytqQty(BigDecimal qytqQty) {
		if(qytqQty != null) {
			this.qytqQty = qytqQty;
		}
	}

	public String getSubcity() {
		return subcity;
	}

	public void setSubcity(String subcity) {
		this.subcity = subcity;
	}

	public BigDecimal getMaxQty() {
		return maxQty;
	}

	public void setMaxQty(BigDecimal maxQty) {
		if(maxQty != null) {
			this.maxQty = maxQty;
		}
	}

	public String getC_ID() {
		return C_ID;
	}

	public void setC_ID(String c_id) {
		C_ID = c_id;
	}

	public String getP_ID() {
		return P_ID;
	}

	public void setP_ID(String p_id) {
		P_ID = p_id;
	}

	public String getQTY2() {
		return QTY2;
	}

	public void setQTY2(String qty2) {
		QTY2 = qty2;
	}

	public String getYearmonth() {
		return yearmonth;
	}

	public void setYearmonth(String yearmonth) {
		this.yearmonth = yearmonth;
	}

}
