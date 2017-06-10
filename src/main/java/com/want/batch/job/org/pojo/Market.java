package com.want.batch.job.org.pojo;

/**
 * @author 00078588
 * 标准市场
 */
public class Market {
	private String marketId="";
	private String marketName="";
	private String marketLevel="";
	private String rpMarketId="";
	private String rpMarketName="";
	private String ypMarketId="";
	private String ypMarketName="";
	
	public Market(){}
	
	public Market(String marketId,String marketName,String marketLevel,String rpMarketId,String rpMarketName,String ypMarketId,String ypMarketName){
		this.marketId=marketId;
		this.marketName=marketName;
		this.marketLevel=marketLevel;
		this.rpMarketId=rpMarketId;
		this.rpMarketName=rpMarketName;
		this.ypMarketId=ypMarketId;
		this.ypMarketName=ypMarketName;
	}
	
	public String getMarketId() {
		return marketId;
	}
	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}
	public String getMarketName() {
		return marketName;
	}
	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}
	public String getMarketLevel() {
		return marketLevel;
	}
	public void setMarketLevel(String marketLevel) {
		this.marketLevel = marketLevel;
	}
	public String getRpMarketId() {
		return rpMarketId;
	}
	public void setRpMarketId(String rpMarketId) {
		this.rpMarketId = rpMarketId;
	}
	public String getRpMarketName() {
		return rpMarketName;
	}
	public void setRpMarketName(String rpMarketName) {
		this.rpMarketName = rpMarketName;
	}
	public String getYpMarketId() {
		return ypMarketId;
	}
	public void setYpMarketId(String ypMarketId) {
		this.ypMarketId = ypMarketId;
	}
	public String getYpMarketName() {
		return ypMarketName;
	}
	public void setYpMarketName(String ypMarketName) {
		this.ypMarketName = ypMarketName;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof Market)) return false;
		
		Market m=(Market)o;
		return m.getMarketId().equals(this.getMarketId());
	}
	public int hashCode(){
		return this.getMarketId().hashCode();
	}
}
