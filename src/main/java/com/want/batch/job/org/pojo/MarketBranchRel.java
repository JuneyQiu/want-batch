package com.want.batch.job.org.pojo;

/**
 * @author 00078588
 * 标准市场－营业所对应关系
 */
public class MarketBranchRel {
	private String marketId="";
	private String branchId="";
	
	public MarketBranchRel(){}
	public MarketBranchRel(String marketId,String branchId){
		this.marketId=marketId;
		this.branchId=branchId;
	}
	
	public String getMarketId() {
		return marketId;
	}
	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof MarketBranchRel)) return false;
		
		MarketBranchRel mb=(MarketBranchRel)o;
		return mb.getMarketId().equals(this.getMarketId())&&mb.getBranchId().equals(this.getBranchId());
	}
	public int hashCode(){
		return this.getMarketId().hashCode()+this.getBranchId().hashCode();
	}
}
