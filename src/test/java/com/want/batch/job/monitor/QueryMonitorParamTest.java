package com.want.batch.job.monitor;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.want.batch.job.sfa2.monitor.jobs.QueryMonitorParam;
public class QueryMonitorParamTest {

	private String[] contexts = { "classpath:applicationContext.xml", "classpath:test-env.xml", "classpath:test-sap-access.xml" };
//	private String[] contexts = { "classpath:applicationContext.xml", "classpath:data-access.xml", "classpath:sap-access.xml" };

	@Test
	public void executeTest() {
		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			QueryMonitorParam queryMonitorParam = ac.getBean(QueryMonitorParam.class);
			queryMonitorParam.executeBatch();
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
