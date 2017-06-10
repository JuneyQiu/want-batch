package com.want.batch.job;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.tch.TCHWriterPicture;
/**
 * 
 * @author Jerry Yu
 * 
 * @description : 
 *
 * @mail mlzxcs001@want-want.com
 * 
 * Create time 下午05:37:21
 *
 * @version 
 * @since JDK 1.6
 */
public class TestTCHWriterPicture {

	private String[] contexts = { "classpath:applicationContext.xml", "classpath:test-env.xml", "classpath:test-sap-access.xml" };

	@Test
	public void executeTest(){
		ApplicationContext applicationContext=new ClassPathXmlApplicationContext(contexts);
		try {
			TCHWriterPicture tchWriterPicture=applicationContext.getBean(TCHWriterPicture.class);
			tchWriterPicture.execute();
			Assert.assertTrue(true);
		} catch (BeansException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
