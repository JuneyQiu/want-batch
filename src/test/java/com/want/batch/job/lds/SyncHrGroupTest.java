package com.want.batch.job.lds;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SyncHrGroupTest {
	private String[] contexts = { "classpath:applicationContext.xml",
			"classpath:data-access.xml", "classpath:test-sap-access.xml" };

	@Test
	public void executeTest() {
		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			SyncHrGroup group = ac.getBean(SyncHrGroup.class);
			//group.sync("C2");
			group.sync();
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
