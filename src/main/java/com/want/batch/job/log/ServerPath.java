package com.want.batch.job.log;

public class ServerPath {

	private String server;
	private String path;
	private String file;
	
	public ServerPath(String server, String file, String path) {
		super();
		this.server = server;
		this.file = file;
		this.path = path;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
}
