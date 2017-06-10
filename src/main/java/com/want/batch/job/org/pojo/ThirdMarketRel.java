package com.want.batch.job.org.pojo;

/**
 * @author 00078588
 * 三级地－标准市场对应关系
 */
public class ThirdMarketRel {
	private String thirdId="";//三级地编号
	private String marketId="";//标准市场编号
	private double marketRate=0;//应有市场占比
	private String isNotOpen="";//是否有暂不开发市场
	private String isRough="";//是否为粗放线
	
	public ThirdMarketRel(){}
	public ThirdMarketRel(String thirdId,String marketId,double marketRate,String isNotOpen,String isRough){
		this.thirdId=thirdId;
		this.marketId=marketId;
		this.marketRate=marketRate;
		this.isNotOpen=isNotOpen;
		this.isRough=isRough;
	}
	
	public String getThirdId() {
		return thirdId;
	}
	public void setThirdId(String thirdId) {
		this.thirdId = thirdId;
	}
	public String getMarketId() {
		return marketId;
	}
	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}
	public double getMarketRate() {
		return marketRate;
	}
	public void setMarketRate(double marketRate) {
		this.marketRate = marketRate;
	}
	public String getIsNotOpen() {
		return isNotOpen;
	}
	public void setIsNotOpen(String isNotOpen) {
		this.isNotOpen = isNotOpen;
	}
	public String getIsRough() {
		return isRough;
	}
	public void setIsRough(String isRough) {
		this.isRough = isRough;
	}
	public boolean equals(Object o){
		if(!(o instanceof ThirdMarketRel)) return false;
		
		ThirdMarketRel tm=(ThirdMarketRel)o;
		return tm.getThirdId().equals(this.getThirdId())&&tm.getMarketId().equals(this.getMarketId());
	}
	public int hashCode(){
		return this.getThirdId().hashCode()+this.getMarketId().hashCode();
	}
}
