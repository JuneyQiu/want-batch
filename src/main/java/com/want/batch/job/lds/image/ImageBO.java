package com.want.batch.job.lds.image;

public class ImageBO {
	public static final int INSERT = 0;
	public static final int UPDATE = 1;
	public static final int IGNORE = 2;
	
	private String	img_emp_id;
	private byte[]	img_content;
	private String	img_modify_time;
	private String	img_modify_emp;
	
	private int modifyType = IGNORE;
	
	public ImageBO() {
		
	}

	public ImageBO(String id, String time) {
		this.img_emp_id = id;
		this.img_modify_time = time;
	}

	public ImageBO(String id, String time, byte[] image) {
		this.img_emp_id = id;
		this.img_modify_time = time;
		this.img_content = image;
	}
	

	public int getModifyType() {
		return modifyType;
	}

	public void setModifyType(int modifyType) {
		this.modifyType = modifyType;
	}

	public String getImg_emp_id() {
		return img_emp_id;
	}

	public void setImg_emp_id(String img_emp_id) {
		this.img_emp_id = img_emp_id;
	}

	public byte[] getImg_content() {
		return img_content;
	}

	public void setImg_content(byte[] img_content) {
		this.img_content = img_content;
	}

	public String getImg_modify_time() {
		return img_modify_time;
	}

	public void setImg_modify_time(String img_modify_time) {
		this.img_modify_time = img_modify_time;
	}

	public String getImg_modify_emp() {
		return img_modify_emp;
	}

	public void setImg_modify_emp(String img_modify_emp) {
		this.img_modify_emp = img_modify_emp;
	}

}
