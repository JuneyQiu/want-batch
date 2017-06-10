package com.want.batch.job.sfa.pojo;

/**sd_actual_prod
 * @author 00078588
 *
 */
public class SdActualProd {
	private Long sid=null;
	private Long actualDisplaySid=null;
	private String productId="";
	private String dataSource="";
	
	
	public Long getSid() {
		return sid;
	}
	public void setSid(Long sid) {
		this.sid = sid;
	}
	public Long getActualDisplaySid() {
		return actualDisplaySid;
	}
	public void setActualDisplaySid(Long actualDisplaySid) {
		this.actualDisplaySid = actualDisplaySid;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
}
