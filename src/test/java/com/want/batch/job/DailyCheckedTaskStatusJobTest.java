/**
 * 
 */
package com.want.batch.job;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.ghbatch.DailyCheckedTaskStatusJob;

/**
 * @author MandyZhang
 *
 */
public class DailyCheckedTaskStatusJobTest {

	private String[] contexts = { "classpath:applicationContext.xml", "classpath:test-env.xml", "classpath:test-sap-access.xml" };
	
	@Test
	public void executeTest() {
		
		try {
			
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			
			DailyCheckedTaskStatusJob dailyCheckedTaskStatusJob = ac.getBean(DailyCheckedTaskStatusJob.class);
			
			dailyCheckedTaskStatusJob.execute();
			
			Assert.assertTrue(true);
		} catch (BeansException e) {
			
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			
			e.printStackTrace();
			Assert.fail();
		}
	}
}
