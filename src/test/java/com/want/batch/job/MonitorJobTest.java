
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

import com.want.batch.job.reportproduce.MonitorJob;
import com.want.batch.job.reportproduce.dao.DirectiveTblDao;


// ~ Comments
// ==================================================

/**
 * 监控排程测试类.
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
public class MonitorJobTest {

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================
	
	//private String[] contexts = { "classpath:applicationContext.xml", "classpath:test-env.xml", "test-sap-access.xml" };
	private String[] contexts = { "classpath:applicationContext.xml", "classpath:data-access.xml", "classpath:sap-access.xml" };
	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	@Test
	public void executeTest() {
		
		try {
			
			ApplicationContext ac = new ClassPathXmlApplicationContext(contexts);
			MonitorJob monitorJob = ac.getBean(MonitorJob.class);
			monitorJob.execute();
			
			// 执行时解除以下注释
			/*List<Map<String, Object>> returnData = monitorJob.execute();
			
			if ((returnData == null)||(returnData != null && returnData.size() == 0)) {
				
				Assert.assertTrue(true);
			}
			else {
				
				// 查询这些资料的状态是否都变为FINISH，若是则返回true，否则返回false
				DirectiveTblDao directiveTblDao = ac.getBean(DirectiveTblDao.class);
				boolean flag = directiveTblDao.getTaskStatus(returnData);
				Assert.assertSame(flag, true);
			}*/
		} catch (BeansException e) {
			
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			
			e.printStackTrace();
			Assert.fail();
		}
	}
}
