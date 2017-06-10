package com.want.batch.job.lds;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.lds.SyncPosition;

public class SyncPositionTest {

	private String[] contexts = { "classpath:applicationContext.xml",
			"classpath:data-access.xml", "classpath:test-sap-access.xml" };

	@Test
	public void executeTest() {
		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			SyncPosition pos = (SyncPosition) ac.getBean("syncAdminPosition");
			pos.sync("Y0010001");
			//pos.sync();
			
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
