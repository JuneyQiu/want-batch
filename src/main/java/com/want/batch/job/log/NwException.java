package com.want.batch.job.log;

import java.sql.Timestamp;

public class NwException {

	private Timestamp date = null;
	private String id = null;
	private String path = null;
	private String message = "";
	private StringBuffer trace = null;
	private String type = "";
	private String server = "";
	
	public Timestamp getDate() {
		return date;
	}

	public void setDate(String date) {
		String[] tokens = date.split(" ");
		String ds = tokens[0] + "-" + tokens[1] + "-" + tokens[2];
		tokens = tokens[3].split(":");
		String ts = tokens[0] + ":" + tokens[1] + ":" + tokens[2];
		this.date = Timestamp.valueOf(ds + " " + ts);
	}
	
	public boolean isAfter(Timestamp maxDate) {
		return this.date.equals(maxDate) || this.date.before(maxDate);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null)
			this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if (path != null)
			this.path = path;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		if (message != null)
			this.message = message;
		else
			this.message = "";
	}

	public String getTrace() {
		return this.trace.toString();
	}

	public void appendTrace(String trace) {
		if (this.trace == null)
			this.trace = new StringBuffer();
		
		if (trace != null)
			this.trace.append(trace + "\n");
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type != null)
			this.type = type;
		else
			this.type = "";
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		if (server != null)
			this.server = server;
		else
			this.server = "";
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Type: " + type + "\n");
		buf.append("\t, server: " + server + "\n");
		buf.append("\t, date: " + date.toString() + "\n");
		buf.append("\t, ID: " + id + "\n");
		buf.append("\t, message: " + message + "\n");
		buf.append("\t, URI: " + path + "\n");
		buf.append("\t, exception: " + trace + "\n");
		buf.append("\n");
		return buf.toString();
	}
}
