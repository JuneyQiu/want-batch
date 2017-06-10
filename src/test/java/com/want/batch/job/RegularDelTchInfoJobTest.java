
// ~ Package Declaration
// ==================================================

package com.want.batch.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.want.batch.job.regulardeltchinfor.RegularDelTchInfoJob;
import com.want.data.pojo.AwbPojo;

// ~ Comments
// ==================================================

public class RegularDelTchInfoJobTest {

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================
		
//	private String[] contexts = { "classpath:applicationContext.xml", "classpath:test-env.xml", "classpath:test-sap-access.xml" };
	private String[] contexts = { "classpath:applicationContext.xml", "classpath:test-env.xml", "classpath:test-sap-access.xml" };
	
	@Test
	public void executeTest() {
		
		try {
			
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			
			// 与之相应的java文件名
			RegularDelTchInfoJob regularDelTchInfoJob = ac.getBean(RegularDelTchInfoJob.class);
			
			regularDelTchInfoJob.execute();
			
			Assert.assertTrue(true);
		} catch (BeansException e) {
			
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			
			e.printStackTrace();
			Assert.fail();
		}
	}
}
