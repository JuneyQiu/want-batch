/**
 * 
 */
package com.want.batch.job;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.storeprodseries.StoreProdSeriesJob;

/**
 * @author MandyZhang
 *
 */
public class StoreProdSeriesJobTest {

	private String[] contexts = { "classpath:applicationContext.xml", "classpath:test-env.xml", "classpath:test-sap-access.xml" };
	
	@Test
	public void executeTest() {
		
		try {
			
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			
			StoreProdSeriesJob storeProdSeriesJob = ac.getBean(StoreProdSeriesJob.class);
			
			storeProdSeriesJob.execute();
			
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
