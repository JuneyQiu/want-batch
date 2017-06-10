package com.want.batch.job.lds.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component
public class EmpImageCheck extends AbstractWantJob {

	private static final Log logger = LogFactory.getLog(EmpImageCheck.class);

	public static String remoteImageBOPath = "\\\\10.0.0.26\\empimages";
	// public static String localImageBOPath = "D:\\20150204";
	public static final int IMAGE_MAX_SIZE = 60000;
	@Autowired
	private DataSource portalDataSource;// 注入

	@Autowired
	private EmpImageBOToDBJob empImageBOToDBJob;

	@Override
	public void execute() throws Exception {

		// 远程抓取并压缩
		// itdb.compressImageBO(remoteImageBOPath, localImageBOPath);
		//
		File[] files = new File(remoteImageBOPath).listFiles();
		Connection conn = portalDataSource.getConnection();
		List<String> dbEmployee = empImageBOToDBJob.getDBEmployees(conn);
		Map<String, ImageBO> dbImages = empImageBOToDBJob.getDBEmployeeImages(conn);
		conn.close();

		List<String> fileList = new ArrayList<String>();
		for (File f: files) 
			fileList.add(f.getName());
				
		// employee has no photo
		List<String> employeeHasNoImages = new ArrayList<String>();
		logger.info("以下为没有照片的员工清单");
		for (String id: dbEmployee) 
			if (dbImages.get(id) == null) {
				employeeHasNoImages.add(id);
				logger.info(id);
			}
		
		// employee has photo, but unreadable or corrupted or invalid photo name
		logger.info("\n\n以下为照片命名错误、或压缩失败、或无法读取");
		for (String id: employeeHasNoImages) 
			for (String file: fileList) {
				if (file.indexOf(id) >= 0 || id.indexOf(file) >= 0) {
					logger.info(file);
					break;
				}
			}
			
	}

}
