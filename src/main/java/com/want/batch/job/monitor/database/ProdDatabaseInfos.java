package com.want.batch.job.monitor.database;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

public class ProdDatabaseInfos {

	private String ip;
	private String instanceSid;
	private List<String> schemas = new ArrayList<String>();
	private SimpleJdbcOperations jdbcOperations;
	private List<String> sysSchemas = new ArrayList<String>();

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getInstanceSid() {
		return instanceSid;
	}

	public void setInstanceSid(String instanceSid) {
		this.instanceSid = instanceSid;
	}

	public List<String> getSchemas() {
		return schemas;
	}

	public void setSchemas(List<String> schemas) {
		this.schemas = schemas;
	}

	public SimpleJdbcOperations getJdbcOperations() {
		return jdbcOperations;
	}

	public void setJdbcOperations(SimpleJdbcOperations jdbcOperations) {
		this.jdbcOperations = jdbcOperations;
	}

	public List<String> getSysSchemas() {
		return sysSchemas;
	}

	public void setSysSchemas(List<String> sysSchemas) {
		this.sysSchemas = sysSchemas;
	}

}
