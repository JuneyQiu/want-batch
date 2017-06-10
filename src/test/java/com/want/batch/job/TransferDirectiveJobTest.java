
// ~ Package Declaration
// ==================================================

package com.want.batch.job;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.reportproduce.TransferDirectiveJob;
import com.want.batch.job.reportproduce.dao.DirectiveTblDao;


// ~ Comments
// ==================================================

/**
 * 删除指令排程.
 * 
 * <table>
 * 	<tr>
 * 		<th>日期</th>
 * 		<th>变更说明</th>
 * 	</tr>
 * 	<tr>
 * 		<td>2013-6-20</td>
 * 		<td>Mirabelle新建</td>
 * 	</tr>
 * </table>
 *
 *@author Mirabelle
 */
public class TransferDirectiveJobTest {

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================

	private static String[] contexts = { "classpath:applicationContext.xml", "classpath:test-env.xml", "test-sap-access.xml" };
	
	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	@Test
	public void executeTest() {

		try {
			
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			TransferDirectiveJob deleteDirectiveJob = ac.getBean(TransferDirectiveJob.class);
			deleteDirectiveJob.execute();
			
			DirectiveTblDao directiveTblDao= ac.getBean(DirectiveTblDao.class);
			List<Map<String, Object>> returnData =directiveTblDao.getTaskByDeleted();
			
			Assert.assertNotSame(returnData == null ? 0 : returnData.size() , 0);
			
		} catch (BeansException e) {
			
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			
			e.printStackTrace();
			Assert.fail();
		}
	}
}
