package com.want.batch.job.lds.bo;

public abstract class User extends Updatable {
	
	private String id;
	private String name; //ldap givenName + sn
	private String gender;
	private String ou;	
	//add by TaoJie begin
	private String password;
	//add by TaoJie end
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getOu() {
		return ou;
	}
	public void setOu(String ou) {
		this.ou = ou;
	}		
}
