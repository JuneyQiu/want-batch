package com.want.batch.job.sfa2.monitor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.want.batch.job.sfa2.monitor.jobs.QueryMonitorParam;
import com.want.batch.job.sfa2.monitor.pojo.MonitorParam;
import com.want.batch.job.utils.ProjectConfig;

public class ParamUtil {
	private static String cp = "param.properties";

	
	public static MonitorParam getParamFromFile() throws FileNotFoundException, IOException{
		MonitorParam param = new MonitorParam();
		String rootPath = ProjectConfig.getInstance().getString("sfa.db.monitor.param.path");
		File file = new File(rootPath+cp);
		List lines = IOUtils.readLines(new FileInputStream(file));
		if(null == lines || lines.size()<=0){
			return null;
		}
		Object object = lines.get(0);
		if(null != object && !"".equals(object)){
			String p = object.toString();
			param.setEmpId(p.substring(0, 7));
			param.setCustomerId(p.substring(9, 19));
			param.setDivisionSid(15+"");
			param.setProjectSid(12+"");
		}
		return param;
	}
	
	public static void writeParamToFile(MonitorParam param) throws FileNotFoundException, IOException {
		String rootPath = ProjectConfig.getInstance().getString("sfa.db.monitor.param.path");
		File file = new File(rootPath+cp);
		String empId= param.getEmpId();
		String customerId =param.getCustomerId();
		String params = empId+"|"+customerId;
		IOUtils.write(params, new FileOutputStream(file));
	}

}
