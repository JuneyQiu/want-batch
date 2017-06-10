package com.want.batch.job.lds;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SyncLdapJobTest {
	private String[] contexts = { "classpath:applicationContext.xml",
			"test-env.xml", "classpath:test-sap-access.xml" };

	@Test
	public void executeTest() {
		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			//SyncAdminLdapJob ldap = ac.getBean(SyncAdminLdapJob.class);
			SyncSalesLdapJob ldap = ac.getBean(SyncSalesLdapJob.class);
			ldap.execute();
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
