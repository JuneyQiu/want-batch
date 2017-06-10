package com.want.batch.job.spring_example.bo;

import java.math.BigDecimal;

import com.want.batch.job.spring_example.util.bo.BaseObject;

/**
 * WenSend的值对象类，对应的数据库表是
 * 
 * @author suyulin
 */
public class WenSend extends BaseObject {

	private static final long serialVersionUID = 325752335628326L;

	private int sid;
	private int wenSid;
	private int customerSid;
	private BigDecimal sendQty = new BigDecimal("0");   //实际已交货量（按销售单位）发货数量
	private BigDecimal refuseQty = new BigDecimal("0"); //实际已交货量（按销售单位）拒收退货数量
	private String sendDate; //分公司发货时间
	private String ruKuDate; //分公司入库时间
	
	private String tempMEINS; //基本计量单位
	private String tempVBELN; //销售和分销凭证号
	private String tempSPART; //产品组
	private String tempVTWEG; //分销渠道
	private String tempVKORG; //销售组织
	
	private int sendDate8Num; //分公司发货时间(8位，例如: 20091201)
	private String sendQtyStr; //sendQty的字符串表示方式
	private String useEndDateStr; //使用截止日期      的字符串表示方式，等于"分公司发货日期"加50天
	
	
	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public int getWenSid() {
		return wenSid;
	}

	public void setWenSid(int wenSid) {
		this.wenSid = wenSid;
	}

	public int getCustomerSid() {
		return customerSid;
	}

	public void setCustomerSid(int customerSid) {
		this.customerSid = customerSid;
	}

	public BigDecimal getSendQty() {
		return sendQty;
	}

	public void setSendQty(BigDecimal sendQty) {
		if (sendQty != null) {
			this.sendQty = sendQty;
		}
	}

	public String getSendDate() {
		return sendDate;
	}

	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}

	public String getSendQtyStr() {
		return sendQtyStr;
	}

	public void setSendQtyStr(String sendQtyStr) {
		this.sendQtyStr = sendQtyStr;
	}

	public String getUseEndDateStr() {
		return useEndDateStr;
	}

	public void setUseEndDateStr(String useEndDateStr) {
		this.useEndDateStr = useEndDateStr;
	}

	public BigDecimal getRefuseQty() {
		return refuseQty;
	}

	public void setRefuseQty(BigDecimal refuseQty) {
		if (refuseQty != null) {
			this.refuseQty = refuseQty;
		}
	}

	public String getRuKuDate() {
		return ruKuDate;
	}

	public void setRuKuDate(String ruKuDate) {
		this.ruKuDate = ruKuDate;
	}

	public String getTempMEINS() {
		return tempMEINS;
	}

	public void setTempMEINS(String tempMEINS) {
		this.tempMEINS = tempMEINS;
	}

	public String getTempVBELN() {
		return tempVBELN;
	}

	public void setTempVBELN(String tempVBELN) {
		this.tempVBELN = tempVBELN;
	}

	public String getTempSPART() {
		return tempSPART;
	}

	public void setTempSPART(String tempSPART) {
		this.tempSPART = tempSPART;
	}

	public String getTempVTWEG() {
		return tempVTWEG;
	}

	public void setTempVTWEG(String tempVTWEG) {
		this.tempVTWEG = tempVTWEG;
	}

	public String getTempVKORG() {
		return tempVKORG;
	}

	public void setTempVKORG(String tempVKORG) {
		this.tempVKORG = tempVKORG;
	}

	public int getSendDate8Num() {
		return sendDate8Num;
	}

	public void setSendDate8Num(int sendDate8Num) {
		this.sendDate8Num = sendDate8Num;
	}

}
