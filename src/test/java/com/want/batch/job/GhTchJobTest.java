
// ~ Package Declaration
// ==================================================

package com.want.batch.job;


import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.ghbatch.GhTchJob;


// ~ Comments
// ==================================================

public class GhTchJobTest {

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	private static String[] contexts = {"classpath:applicationContext.xml","classpath:test-env.xml","classpath:test-sap-access.xml"};
	@Test
	public void excute(){
		ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
		GhTchJob ghTchJob = ac.getBean(GhTchJob.class);
		try {
			ghTchJob.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
