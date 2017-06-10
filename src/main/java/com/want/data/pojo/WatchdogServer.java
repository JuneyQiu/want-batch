/**
 * 
 */
package com.want.data.pojo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * @author 00079241
 *
 */
@Component
public class WatchdogServer {

	private String ip;

	private Map<String, Integer> namePorts = new HashMap<String, Integer>();
	private Map<String, String> serverInstances = new HashMap<String, String>();

	public Map<String, String> getServerInstances() {

		if (this.serverInstances.isEmpty()) {
			String url = "http://%s:%s";
			for (String name : this.getNamePorts().keySet()) {
				this.serverInstances.put(name, String.format(url, this.ip, this.getNamePorts().get(name)));
			}
		}

		return serverInstances;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Map<String, Integer> getNamePorts() {
		return namePorts;
	}

	public void setNamePorts(Map<String, Integer> namePorts) {
		this.namePorts = namePorts;
	}

	@Override
	public String toString() {
		return getServerInstances().toString();
	}
}
