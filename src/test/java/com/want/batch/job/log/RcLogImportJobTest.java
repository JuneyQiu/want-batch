package com.want.batch.job.log;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RcLogImportJobTest {
	//private String[] contexts = { "classpath:applicationContext.xml",
	//		"classpath:test-env.xml", "classpath:test-sap-access.xml" };
	private String[] contexts = { "classpath:applicationContext.xml",
			"classpath:data-access.xml", "classpath:test-sap-access.xml" };
	@Test
	public void executeTest() {

		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			RcLogImportJob rcLogImportJob = ac.getBean(RcLogImportJob.class);
			rcLogImportJob.execute();
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
