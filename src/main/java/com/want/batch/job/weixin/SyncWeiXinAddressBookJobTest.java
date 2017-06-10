package com.want.batch.job.weixin;

import org.junit.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SyncWeiXinAddressBookJobTest {
	private static String[] contexts = { "classpath:applicationContext.xml",
		// "classpath:data-access.xml", "classpath:test-sap-access.xml","models.xml" };
		"classpath:test-env.xml", "classpath:test-sap-access.xml","models.xml" };
	
	
	
	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);			
		SyncWeiXinAddressBookJob lds = (SyncWeiXinAddressBookJob) ac.getBean(SyncWeiXinAddressBookJob.class);
		try {
			
			//lds.testEmp();
			
			lds.execute();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertTrue(true);
		
		//System. out .println( " 内存信息 :" + toMemoryInfo()); 
	}
	
   


    
}
