package com.want.batch.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sap.mw.jco.JCO.Client;
import com.want.utils.SapDataSource;

public class SapTest {

	protected final Log logger = LogFactory.getLog(SapTest.class);

	private String[] contexts = { "classpath:applicationContext.xml", "classpath:test-env.xml", "classpath:test-sap-access.xml" };
	//private String[] contexts = { "classpath:applicationContext.xml", "classpath:data-access.xml", "classpath:sap-access.xml" };
	
	@Test
	public void executeTest() {
		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			SapDataSource sapDataSource = ac.getBean(SapDataSource.class);
			/*
			Client client = sapDataSource.createConnection();
			logger.info(client.getASHost());
			sapDataSource.close(client);
			*/
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
