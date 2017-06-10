package com.want.batch.job.smsapp;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.monitor.HRMonitorJob;

public class MasAppjobTest {
	private static String[] contexts = { "classpath:applicationContext.xml",
		"classpath:test-env.xml", "classpath:test-sap-access.xml","models.xml" };
@Test
public void executeTest() throws Exception{
	/* Load the configuration file */
	ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);		
	
	MasAppjob mp = (MasAppjob) ac.getBean(MasAppjob.class);
	mp.execute();
	Assert.assertTrue(true);
}

public static void main(String[] args) {
	
	/* Load the configuration file */
	ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);		
	
	MasAppjob mp = (MasAppjob) ac.getBean(MasAppjob.class);
	try {
		mp.execute();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	Assert.assertTrue(true);
	
	}
}
