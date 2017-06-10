package com.want.batch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.want.batch.job.AbstractWantJob;

/**
* @ClassName: ExecuteJob
* @Description: run job
* @author 00079241 Timothy
* @date 2010-11-3 下午06:14:06
*
*/
public class ImmediatelyExecuteJob {

	private static final Log logger = LogFactory.getLog(ImmediatelyExecuteJob.class);
	private static List<String> CONFIG_LOCATIONS = new ArrayList<String>();

	public static void main(String[] args) {

		if (args.length == 0) {
			throw new WantBatchException(new NullPointerException("No execute job..."));
		}
		else if (args.length > 1) {
			throw new WantBatchException(new IllegalArgumentException("only 1 execute job..."));
		}

		addClasspathConfigLocations("applicationContext", "job-immediately");
		addDataAccessConfig();
		addSapAccessConfig();

		logger.debug(CONFIG_LOCATIONS);

		ApplicationContext ac = new ClassPathXmlApplicationContext(CONFIG_LOCATIONS.toArray(new String[]{}));

		ac.getBean(args[0], AbstractWantJob.class).executeBatch();
	}

	private static void addSapAccessConfig() {

		// 使用生产环境的参数设定
		if (BooleanUtils.toBoolean(System.getenv("WANT_BATCH_SAP_ACCESS_PROD"))) {
			addClasspathConfigLocations("sap-access(prod)");
		}

		// 使用 QAS 环境的参数设定
		else if (BooleanUtils.toBoolean(System
				.getenv("WANT_BATCH_SAP_ACCESS_QAS"))) {
			addClasspathConfigLocations("sap-access(qas)");
		} else {
			addClasspathConfigLocations("sap-access");
		}
	}

	private static void addDataAccessConfig() {

		// 当传入参数 [prod] 时，使用生产环境的参数设定
		addClasspathConfigLocations(BooleanUtils.toBoolean(System
				.getenv("WANT_BATCH_DB_ACCESS_PROD")) ? "data-access(prod)"
				: "data-access");
	}

	private static void addClasspathConfigLocations(String... configLocations) {
		for (String configLocation : configLocations) {
			CONFIG_LOCATIONS.add(String.format("classpath:%s.xml", configLocation));
		}
	}

}
