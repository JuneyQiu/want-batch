package com.want.batch.job.log;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class NwLogImportJobTest {
	private String[] contexts = { "classpath:applicationContext.xml",
			"classpath:test-env.xml", "classpath:test-sap-access.xml" };

	@Test
	public void executeTest() {

		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			NwLogImportJob nwLogImportJob = ac.getBean(NwLogImportJob.class);
			nwLogImportJob.execute();
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
