package com.want.batch.job;

import java.sql.SQLException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.old.DailyOrgAndAuthJobs;

public class DailyOrgAndAuthJobsTest {
//	private static String[] contexts = {"classpath:applicationContext.xml","classpath:data-access.xml","classpath:sap-access.xml"};
	private static String[] contexts = {"classpath:applicationContext.xml","classpath:test-env.xml","classpath:sap-access.xml"};
	@Test
	public void excute(){
		ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
		DailyOrgAndAuthJobs doa = ac.getBean(DailyOrgAndAuthJobs.class);
		try {
			doa.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
