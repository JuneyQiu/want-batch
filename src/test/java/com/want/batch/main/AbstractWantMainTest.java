package com.want.batch.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

public class AbstractWantMainTest {

	protected Log logger = LogFactory.getLog(this.getClass());
	
	protected ApplicationContext applicationContext;
	protected SimpleJdbcOperations iCustomerJdbcOperations;
	
	@Before
	public void before() {
		applicationContext = new ClassPathXmlApplicationContext(new String[]{
				"classpath:applicationContext.xml", 
				"classpath:test-main-env.xml"
		});
		
		iCustomerJdbcOperations = applicationContext.getBean("iCustomerJdbcOperations", SimpleJdbcOperations.class);
	}

}
