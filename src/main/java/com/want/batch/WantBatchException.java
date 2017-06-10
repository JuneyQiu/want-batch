/**  
* @Title: SapComponentException.java
* @Package com.want.component.sap
* @Description: 由 sap 组件发生的异常，统一抛出此异常实例
* @author 00079241  Timothy
* @date 2010-11-3 下午05:04:55
* @version V1.0  
*/ 
package com.want.batch;

/**
 * @ClassName: SapComponentException
 * @Description: 由排程程式发生的异常，统一抛出此异常实例
 * @author 00079241 Timothy
 * @date 2010-11-3 下午05:04:55
 */
public class WantBatchException extends RuntimeException {

	private static final long serialVersionUID = -5250476940482220685L;

	public WantBatchException() {
		super();
	}

	public WantBatchException(String message) {
		super(message);
	}

	public WantBatchException(Throwable cause) {
		super(cause);
	}

	public WantBatchException(String message, Throwable cause) {
		super(message, cause);
	}

}

