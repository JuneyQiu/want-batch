/**
 *<pre>
 *
 * Project name：want-batch
 *
 * File name：AwbPojo.java
 *
 * File creation time： 2013-12-25 上午11:12:43
 * 
 * Copyright (c) 2013, WantWant Group All Rights Reserved. 
 *
 * </pre>	
 */
package com.want.data.pojo;

import java.io.Serializable;

/**
 * @author Chris Yu
 * 
 * @description :异常预警通报pojo
 * 
 * @mail 18621961029@163.com
 * 
 *       Create time 上午11:12:43
 * 
 * @version
 * @since JDK 1.6
 */
public class AwbPojo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int rowNum;
	private int columnNum;
	private String errrorContext ;

	/**
	 * @return the rowNum
	 */
	public int getRowNum() {
		return rowNum;
	}

	/**
	 * @param rowNum
	 *            the rowNum to set
	 */
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	/**
	 * @return the columnNum
	 */
	public int getColumnNum() {
		return columnNum;
	}

	/**
	 * @param columnNum
	 *            the columnNum to set
	 */
	public void setColumnNum(int columnNum) {
		this.columnNum = columnNum;
	}

	/**
	 * @return the errrorContext
	 */
	public String getErrrorContext() {
		return errrorContext;
	}

	/**
	 * @param errrorContext the errrorContext to set
	 */
	public void setErrrorContext(String errrorContext) {
		this.errrorContext = errrorContext;
	}

	

}
