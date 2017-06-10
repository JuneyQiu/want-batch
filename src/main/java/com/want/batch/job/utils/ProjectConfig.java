package com.want.batch.job.utils;

import java.util.ResourceBundle;

public class ProjectConfig {

	private static ProjectConfig instance = new ProjectConfig();
	
	private ResourceBundle bundle;
	
	private ProjectConfig() {
		bundle = ResourceBundle.getBundle("project");
	}
	
	public static ProjectConfig getInstance() {
		return instance;
	}
	
	public String getString(String key) {
		return bundle.getString(key);
	}
}
