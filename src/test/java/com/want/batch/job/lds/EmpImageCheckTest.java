package com.want.batch.job.lds;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.lds.image.EmpImageCheck;

public class EmpImageCheckTest {

	private String[] contexts = { "classpath:applicationContext.xml",
			// "classpath:test-env.xml", "classpath:test-sap-access.xml" };
			"classpath:data-access.xml", "classpath:test-sap-access.xml" };

	@Test
	public void executeTest() {
		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			EmpImageCheck ldap = ac.getBean(EmpImageCheck.class);
			ldap.execute();
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
