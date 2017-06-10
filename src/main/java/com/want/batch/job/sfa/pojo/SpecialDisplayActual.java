package com.want.batch.job.sfa.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**对应表special_display_actual
 * @author 00078588
 *
 */
public class SpecialDisplayActual {
	private Long sid=null;
	private Long appSid=null;
	private String submitCheckStatusXg="";
	private Date submitCheckDateXg=null;
	private String submitCheckStatusZr="";
	private Date submitCheckDateZr=null;
	private String submitCheckStatusSz="";
	private Date submitCheckDateSz=null;
	private String submitCheckStatusZj="";
	private Date submitCheckDateZj=null;
	private String checkStatus="";
	private String fillInStatusYd="";
	private Date fillInDateYd=null;
	private String fillInStatusZr="";
	private Date fillInDateZr=null;
	private String fillInStatusKh="";
	private Date fillInDateKh=null;
	private Long submitAmount=null;
	private String dataSource="";
	private List<SdActualDisplay> sadList=new ArrayList<SdActualDisplay>();
	
	
	public Long getSid() {
		return sid;
	}
	public void setSid(Long sid) {
		this.sid = sid;
	}
	public Long getAppSid() {
		return appSid;
	}
	public void setAppSid(Long appSid) {
		this.appSid = appSid;
	}
	public String getSubmitCheckStatusXg() {
		return submitCheckStatusXg;
	}
	public void setSubmitCheckStatusXg(String submitCheckStatusXg) {
		this.submitCheckStatusXg = submitCheckStatusXg;
	}
	public Date getSubmitCheckDateXg() {
		return submitCheckDateXg;
	}
	public void setSubmitCheckDateXg(Date submitCheckDateXg) {
		this.submitCheckDateXg = submitCheckDateXg;
	}
	public String getSubmitCheckStatusZr() {
		return submitCheckStatusZr;
	}
	public void setSubmitCheckStatusZr(String submitCheckStatusZr) {
		this.submitCheckStatusZr = submitCheckStatusZr;
	}
	public Date getSubmitCheckDateZr() {
		return submitCheckDateZr;
	}
	public void setSubmitCheckDateZr(Date submitCheckDateZr) {
		this.submitCheckDateZr = submitCheckDateZr;
	}
	public String getSubmitCheckStatusSz() {
		return submitCheckStatusSz;
	}
	public void setSubmitCheckStatusSz(String submitCheckStatusSz) {
		this.submitCheckStatusSz = submitCheckStatusSz;
	}
	public Date getSubmitCheckDateSz() {
		return submitCheckDateSz;
	}
	public void setSubmitCheckDateSz(Date submitCheckDateSz) {
		this.submitCheckDateSz = submitCheckDateSz;
	}
	public String getSubmitCheckStatusZj() {
		return submitCheckStatusZj;
	}
	public void setSubmitCheckStatusZj(String submitCheckStatusZj) {
		this.submitCheckStatusZj = submitCheckStatusZj;
	}
	public Date getSubmitCheckDateZj() {
		return submitCheckDateZj;
	}
	public void setSubmitCheckDateZj(Date submitCheckDateZj) {
		this.submitCheckDateZj = submitCheckDateZj;
	}
	public String getCheckStatus() {
		return checkStatus;
	}
	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}
	public String getFillInStatusYd() {
		return fillInStatusYd;
	}
	public void setFillInStatusYd(String fillInStatusYd) {
		this.fillInStatusYd = fillInStatusYd;
	}
	public Date getFillInDateYd() {
		return fillInDateYd;
	}
	public void setFillInDateYd(Date fillInDateYd) {
		this.fillInDateYd = fillInDateYd;
	}
	public String getFillInStatusZr() {
		return fillInStatusZr;
	}
	public void setFillInStatusZr(String fillInStatusZr) {
		this.fillInStatusZr = fillInStatusZr;
	}
	public Date getFillInDateZr() {
		return fillInDateZr;
	}
	public void setFillInDateZr(Date fillInDateZr) {
		this.fillInDateZr = fillInDateZr;
	}
	public String getFillInStatusKh() {
		return fillInStatusKh;
	}
	public void setFillInStatusKh(String fillInStatusKh) {
		this.fillInStatusKh = fillInStatusKh;
	}
	public Date getFillInDateKh() {
		return fillInDateKh;
	}
	public void setFillInDateKh(Date fillInDateKh) {
		this.fillInDateKh = fillInDateKh;
	}
	public Long getSubmitAmount() {
		return submitAmount;
	}
	public void setSubmitAmount(Long submitAmount) {
		this.submitAmount = submitAmount;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public List<SdActualDisplay> getSadList() {
		return sadList;
	}
	public void setSadList(List<SdActualDisplay> sadList) {
		this.sadList = sadList;
	}
}
