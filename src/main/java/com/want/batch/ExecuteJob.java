package com.want.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
* @ClassName: ExecuteJob
* @Description: run job
* @author 00079241 Timothy
* @date 2010-11-3 下午06:14:06
*
*/
public class ExecuteJob {

	private static final Log logger = LogFactory.getLog(ExecuteJob.class);
	
	public static ApplicationContext applicationContext = null;

	public static void main(String[] args) {
		Runtime run = Runtime.getRuntime(); 

		long max = run.maxMemory(); 

		long total = run.totalMemory(); 

		long free = run.freeMemory(); 

		long usable = max - total + free; 

		logger.info("最大内存 = " + max); 
		logger.info("已分配内存 = " + total); 
		logger.info("已分配内存中的剩余空间 = " + free); 
		logger.info("最大可用内存 = " + usable); 

		String[] contexts = null;
		
		if (args.length == 0)
			contexts = new String[] {"classpath:applicationContext.xml", "classpath:data-access.xml", "classpath:sap-access.xml", "classpath:job-scheduling.xml"};
		else if (args.length == 1)
			contexts = new String[] {"classpath:applicationContext.xml", "classpath:data-access.xml", "classpath:sap-access.xml", "classpath:" + args[0]};

		logger.info("loading context: ");
		for (String c: contexts)
			logger.info("\t" + c);

		ExecuteJob.applicationContext = new ClassPathXmlApplicationContext(contexts);
	}
}
