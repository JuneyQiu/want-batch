package com.want.batch.job;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.monitor.HRMonitorJob;
import com.want.batch.job.monitor.RebuildIndexJob;


public class RebuildIndexJobTest {
	private static String[] contexts = { "classpath:applicationContext.xml",
		"classpath:test-env.xml", "classpath:test-sap-access.xml","models.xml" };
	
	@Test
	public void executeTest(){
		ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);		
		/* getBean by Spring factory */
		RebuildIndexJob ri = (RebuildIndexJob) ac.getBean("rjob");
		ri.executeBatch();
		Assert.assertTrue(true);
	}
}
