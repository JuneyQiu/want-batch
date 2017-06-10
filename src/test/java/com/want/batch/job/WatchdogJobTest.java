package com.want.batch.job;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.want.batch.job.monitor.ICustomerWatchdogJob;
import com.want.batch.job.monitor.WatchdogJob;


public class WatchdogJobTest {
	/*testing environment   */
	private String[] contexts = { "classpath:applicationContext.xml",
			"classpath:test-env.xml", "classpath:test-sap-access.xml","models.xml" };
	
	/*
	 * Formal environment
	 * 
	 * private String[] contexts = { "classpath:applicationContext.xml",
	 *   "classpath:data-access.xml", "classpath:test-sap-access.xml" };
	 */
	

	@Test
	public void executeTest() {
		try {
			/* Load the configuration file */
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);		
			/* getBean by Spring factory */
			WatchdogJob it = (WatchdogJob) ac.getBean("psysWatchdogJobs");
			
			
			System.out.println(it==null);
			System.out.println(it.getServers().size());
			
			
			it.executeBatch();
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
