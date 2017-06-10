package com.want.batch.job.monitor;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.sfa2.monitor.jobs.Sfa2InsertAndDelMonitor;
public class Sfa2InsertAndDelMonitorTest {

	private String[] contexts = { "classpath:applicationContext.xml", "classpath:test-env.xml", "classpath:test-sap-access.xml" };
//	private String[] contexts = { "classpath:applicationContext.xml", "classpath:data-access.xml", "classpath:sap-access.xml" };

	@Test
	public void executeTest() {
		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			Sfa2InsertAndDelMonitor sfa2InsertAndDelMonitor = ac.getBean(Sfa2InsertAndDelMonitor.class);
			sfa2InsertAndDelMonitor.executeBatch();
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
