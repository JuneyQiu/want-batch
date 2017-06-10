package com.want.batch.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.monitor.BpmWatchdogJob;
import com.want.data.pojo.WatchdogServer;

public class BpmWatchdogJobTest {
	private String[] contexts = { "classpath:applicationContext.xml",
			"classpath:test-env.xml", "classpath:test-sap-access.xml" };

	// private String[] contexts = { "classpath:applicationContext.xml",
	// "classpath:data-access.xml", "classpath:test-sap-access.xml" };

	@Test
	public void executeTest() {
		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			BpmWatchdogJob bpmWatchdogJob = ac.getBean(BpmWatchdogJob.class);
			bpmWatchdogJob.setRootUrl("/servlet/manager/TestDataSourceServlet");
			List<WatchdogServer> servers = new ArrayList<WatchdogServer>();

			WatchdogServer ws = new WatchdogServer();
			ws.setIp("10.0.0.187");
			Map<String, Integer> namePorts = new HashMap<String, Integer>();
			namePorts.put("bpm187:7001", Integer.valueOf("7110"));
			namePorts.put("bpm187:7002", Integer.valueOf("7120"));
			namePorts.put("bpm187:7003", Integer.valueOf("7130"));
			namePorts.put("bpm187:7004", Integer.valueOf("7140"));
			namePorts.put("bpm187:7005", Integer.valueOf("7150"));
			ws.setNamePorts(namePorts);
			servers.add(ws);
			ws = new WatchdogServer();
			ws.setIp("10.0.0.187");
			namePorts = new HashMap<String, Integer>();
			namePorts.put("bpm188:7001", Integer.valueOf("7110"));
			namePorts.put("bpm188:7002", Integer.valueOf("7120"));
			namePorts.put("bpm188:7003", Integer.valueOf("7130"));
			namePorts.put("bpm188:7004", Integer.valueOf("7140"));
			namePorts.put("bpm188:7005", Integer.valueOf("7150"));
			ws.setNamePorts(namePorts);
			servers.add(ws);
			bpmWatchdogJob.setServers(servers);
			bpmWatchdogJob.executeBatch();
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
