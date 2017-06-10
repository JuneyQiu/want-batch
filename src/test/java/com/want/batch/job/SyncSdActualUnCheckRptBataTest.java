package com.want.batch.job;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.archive.storemgr.client.SyncSdActualUnCheckRptBata;

public class SyncSdActualUnCheckRptBataTest {

	private String[] contexts = { "classpath:applicationContext.xml", "classpath:test-env.xml", "classpath:test-sap-access.xml" };
//	private String[] contexts = { "classpath:applicationContext.xml", "classpath:data-access.xml", "classpath:sap-access.xml" };

	@Test
	public void executeTest() {
		try {
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			SyncSdActualUnCheckRptBata syncSdActualUnCheckRptBata = ac.getBean(SyncSdActualUnCheckRptBata.class);
			syncSdActualUnCheckRptBata.execute();
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
