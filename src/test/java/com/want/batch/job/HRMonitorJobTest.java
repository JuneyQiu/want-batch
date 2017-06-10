package com.want.batch.job;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.want.batch.job.monitor.HRMonitorJob;

public class HRMonitorJobTest {
	private static String[] contexts = { "classpath:applicationContext.xml",
			"classpath:test-env.xml", "classpath:test-sap-access.xml","models.xml" };
	@Test
	public void executeTest(){
		/* Load the configuration file */
		ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);		
		/* getBean by Spring factory */
		HRMonitorJob hr = (HRMonitorJob) ac.getBean("hrMonitorJob");
		hr.executeBatch();
		Assert.assertTrue(true);
	}
}
