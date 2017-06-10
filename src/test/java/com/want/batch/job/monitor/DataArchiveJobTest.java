package com.want.batch.job.monitor;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DataArchiveJobTest {

	private String[] contexts = { "classpath:applicationContext.xml",
			"classpath:data-access.xml", "classpath:sap-access.xml" };

	@Test
	public void executeTest() {

		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			DataArchiveJob dataArchiveJob = ac.getBean(DataArchiveJob.class);
			dataArchiveJob.execute();
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
