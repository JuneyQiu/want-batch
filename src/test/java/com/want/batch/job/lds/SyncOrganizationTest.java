package com.want.batch.job.lds;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.lds.SyncOrganization;

public class SyncOrganizationTest {
	
	private String[] contexts = { "classpath:applicationContext.xml",
			"classpath:data-access.xml", "classpath:test-sap-access.xml" };

	@Test
	public void executeTest() {
		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			SyncOrganization org = (SyncOrganization) ac.getBean("syncSalesOrganization");
			org.sync("10040000");
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
