package com.want.batch.job.archive.notify.pojo;

/**
 * @author 00078588
 * 分类统计异常个数
 */
public class NotifyCategoryNum{
	private int categoryId;
	private String categoryName="";
	private int excNum=0;
	
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public int getExcNum() {
		return excNum;
	}
	public void setExcNum(int excNum) {
		this.excNum = excNum;
	}
}