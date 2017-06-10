/**
 *<pre>
 *
 * Project name：want-batch
 *
 * File name：TCHWriterPicture.java
 *
 * File creation time： 2013-5-7 下午02:29:07
 * 
 * Copyright (c) 2013, WantWant Group All Rights Reserved. 
 *
 * </pre>	
 */
package com.want.batch.job.tch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.sql.BLOB;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.utils.ProjectConfig;

/**
 * @author Jerry Yu
 * 
 * @description :
 * 
 * @mail mlzxcs001@want-want.com
 * 
 *       Create time 下午02:29:07
 * 
 * @version 1.0
 * @since JDK 1.6
 */
@Component
public class TCHWriterPicture extends AbstractWantJob {

	// 查询特陈照片
	private StringBuilder queryTaskPicture = new StringBuilder()
			.append(" SELECT A.SID, A.NAME, A.DESCRIPTION, ")
			.append(" A.CONTENT, A.STATUS, A.CREATE_USER, ")
			.append(" A.CREATE_DATE, A.UPDATE_USER, A.UPDATE_DATE, ")
			.append(" A.COMPRESSION FROM ICUSTOMER.TASK_PICTURE A ")
			.append(" WHERE A.SID IN ( SELECT ")
			.append(" B.PICTURE_SID  FROM  ICUSTOMER.SD_ACTUAL_PICTURE B ")
			.append(" WHERE B.ACTUAL_DISPLAY_SID=:ACTUAL_DISPLAY_SID ) ");

	/*
	 * 查询照片文件名组合 每张照片的命名：客户编码+终端编码+特陈形式（DISPLAY_TYPE_TBL.
	 * DISPLAY_TYPE_NAME）+唯一码（照片的SID即可） 例如：0011033300_ Z000015572_地堆_1234567
	 */
	private StringBuilder queryFileStructureInformation = new StringBuilder()
			.append(" SELECT DISTINCT E.SID, E.STORE_ID, ")
			.append(" E.STORE_DISPLAY_SID, G.DISPLAY_TYPE_NAME, H.ID, ")
			.append(" J.COMPANY_ID, J.COMPANY_NAME FROM ")
			.append(" ICUSTOMER.SPECIAL_DISPLAY_POLICY A INNER JOIN ")
			.append(" ICUSTOMER.SPECIAL_DISPLAY_APPLICATION B ON ")
			.append(" A.SID=B.POLICY_SID AND A.DIVISION_SID=:DIVISION_SID ")
			.append(" INNER JOIN ICUSTOMER.SPECIAL_DISPLAY_ACTUAL C ")
			.append(" ON B.SID=C.APPLICATION_SID INNER JOIN ")
			.append(" ICUSTOMER.SD_ACTUAL_DISPLAY E ON ")
			.append(" C.SID=E.ACTUAL_SID AND TO_CHAR(E.CREATE_DATE,'YYYY-MM-DD')=:CREATE_DATE ")
			.append(" INNER JOIN  ICUSTOMER.DISPLAY_TYPE_TBL G ")
			.append(" ON E.DISPLAY_TYPE_SID=G.SID INNER JOIN ")
			.append(" ICUSTOMER.CUSTOMER_INFO_TBL H ON B.CUSTOMER_SID=H.SID ")
			.append(" INNER JOIN  ICUSTOMER.CUSTOMER_BRANCH_TBL I ON ")
			.append(" H.ID=I.CUSTOMER_ID INNER JOIN  ")
			.append(" ICUSTOMER.FULL_MARKET_LEVEL_VIEW J ON ")
			.append(" I.BRANCH_ID=J.BRANCH_ID WHERE  J.COMPANY_ID=:COMPANY_ID ");

	@Override
	public void execute() throws Exception {
		// 获取根目录
		String rootFileDirectory = ProjectConfig.getInstance().getString(
				"tch.report.location");
		deleteRootFileDirectory(rootFileDirectory);
		String divisionArray[] = getPropertiesDivisionSids();
		List<Map<String, Object>> divisionList = queryDivisionList(divisionArray);
		if (null != divisionList && divisionList.size() > 0) {
			for (Map<String, Object> divisionMap : divisionList) {
				String divisionSid = divisionMap.get("SID").toString();
				String divisionName = divisionMap.get("NAME").toString();
				String divisionLibDirectory = createDivisionDirectory(divisionName,rootFileDirectory);
				logger.debug("create division all lib directory "
						+ divisionLibDirectory);
				// 获取所有的分公司，并创建分公司目录
				List<Map<String, Object>> companyList = queryCompanyList();
				if (null != companyList && companyList.size() > 0) {
					for (Map<String, Object> companyMap : companyList) {
						String companyId = companyMap.get("COMPANY_ID").toString();
						String companyName = companyMap.get("COMPANY_NAME").toString();
						// 创建分公司目录
						String companyLibDirectory = createObjDirectory(divisionLibDirectory, companyName);
						logger.info("create company lib directory "+ companyLibDirectory);
						String tchExcuteDay[]=ProjectConfig.getInstance().getString("tch.excute.day")
						.split(",");
						int  excuteDay=Integer.valueOf(tchExcuteDay[0]).intValue();
						logger.info(excuteDay);
						// 创建每个分公司下的时间目录
						for (int i = 0; i < excuteDay; i++) {
							Date createDate = new DateTime().minusDays(i + 1).toDate();
							crete7DayTchPictureLib(divisionSid, companyId,companyLibDirectory,createDate);							
						}
					}
				}
			}
		}
	}


	private void crete7DayTchPictureLib(String divisionSid, String companyId,String companyLibDirectory,Date createDate) {
		String dateTimeLibDirectory = createObjDirectory(companyLibDirectory,transformDateStr(createDate,"yyyyMMdd"));
		logger.info("create dateTime lib directory "+ dateTimeLibDirectory);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("DIVISION_SID", divisionSid);
		param.put("COMPANY_ID", companyId);
		param.put("CREATE_DATE", transformDateStr(createDate,"yyyy-MM-dd"));
		List<Map<String, Object>> fileStructureInformationList = queryFileStructureInformation(param);
		for (Map<String, Object> fileStructureInformationMap : fileStructureInformationList) {
			String customerId=fileStructureInformationMap.get("ID").toString();
			String taskPictureSid=fileStructureInformationMap.get("SID").toString();
			String storeId = fileStructureInformationMap.get("STORE_ID").toString();
			String displayTypeName = fileStructureInformationMap.get("DISPLAY_TYPE_NAME").toString();
			StringBuilder imageNameSplicing=new StringBuilder()
				.append(customerId)
				.append("_")
				.append(storeId)
				.append("_")
				.append(displayTypeName)
				.append("_");
			logger.info("task picture sid"+taskPictureSid);
			StringBuilder fileImageDirectory=new StringBuilder()
				.append(dateTimeLibDirectory)
				.append(File.separatorChar);
			createTaskPicture(taskPictureSid,fileImageDirectory.append(imageNameSplicing));
		}
	}


	/**
	 * 
	 * @param taskPictureSid
	 * @param fileImageDirectory
	 */
	private void createTaskPicture(String taskPictureSid,StringBuilder fileImageDirectory) {
		if (StringUtils.isNotBlank(taskPictureSid)) {
			Connection conn =	this.getICustomerConnection();			
			try {
				PreparedStatement ps=conn.prepareStatement(queryTaskPicture.toString());
				ps.setInt(1, Integer.valueOf(taskPictureSid));
				ResultSet rs= ps.executeQuery();
				while (rs.next()) {
					String writeFileImageDirectory=StringUtils.substringBeforeLast(fileImageDirectory.toString(), "_");
					String imageName=rs.getString("SID")+".jpg";
					logger.info(" image file name "+imageName);	
					FileOutputStream fos=new FileOutputStream(writeFileImageDirectory+"_"+imageName);						
					BLOB contentBolb=(BLOB) rs.getBlob("CONTENT");
					InputStream inStream = contentBolb.getBinaryStream();
					byte b[]=new byte[1024];
					int num=0;
					while ((num=inStream.read(b))!=-1) {
						fos.write(b,0,num);
					}
					inStream.close();
					fos.close();					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

	private List<Map<String, Object>> queryFileStructureInformation(
			Map<String, Object> param) {
		return this.getiCustomerJdbcOperations().queryForList(
				queryFileStructureInformation.toString(), param);
	}

	/**
	 * 拼接上层目录
	 * 
	 * @param upOneLevelDriectory
	 * @param nextDriectoryName
	 * @return
	 */
	private String createObjDirectory(String upOneLevelDriectory,
			String nextDriectoryName) {
		StringBuilder createDivisionDirectory = new StringBuilder()
				.append(upOneLevelDriectory).append(File.separatorChar)
				.append(nextDriectoryName);
		// 创建渠道目录
		return checkFileExists(createDivisionDirectory.toString());
	}

	/**
	 * 创建渠道目录
	 * 
	 * @param objectName
	 * @return
	 */
	private String createDivisionDirectory(String divisionName,String rootFileDirectory) {
		StringBuilder createDivisionDirectory = new StringBuilder()
				.append(createRootFileDirectory(rootFileDirectory)).append(File.separatorChar)
				.append(divisionName);
		// 创建渠道目录
		return checkFileExists(createDivisionDirectory.toString());
	}

	/**
	 * 获取所有分公司
	 * 
	 * @param divisionDirectoryList
	 * @return
	 */
	private List<Map<String, Object>> queryCompanyList() {
		return this
				.getiCustomerJdbcOperations()
				.queryForList(
						" SELECT COMPANY_ID, COMPANY_NAME, CREATE_USER,  CREATE_DATE FROM ICUSTOMER.COMPANY_SAP ");
	}

	/**
	 * 获取创建渠道的路径
	 * 
	 * @return List<String> 创建渠道的路径列表
	 */
	private List<Map<String, Object>> queryDivisionList(String divisionArray[]) {
		List<Map<String, Object>> divisionList = null;
		for (String divisionSid : divisionArray) {
			divisionList = this
					.getiCustomerJdbcOperations()
					.queryForList(
							" SELECT  SID, NAME FROM   ICUSTOMER.DIVSION where DIVSION.SID = ? ",
							divisionSid);
		}
		return divisionList;
	}

	/**
	 * 解析配置档多个渠道情况，并解析成String数组
	 * 
	 * @return string数组
	 */
	private String[] getPropertiesDivisionSids() {
		return ProjectConfig.getInstance().getString("tch.report.divisions")
				.split(",");
	}

	/**
	 * 创建特陈照片导出root目录
	 * 
	 * @return root路径
	 */
	private String createRootFileDirectory(String rootFileDirectory) {
		return checkFileExists(rootFileDirectory);
	}
	
	
	private void deleteRootFileDirectory(String rootFileDirectory) {
		 //使用运行时命令 cmd   /c  rd /s /q  
		  Runtime   rt   =   Runtime.getRuntime();
		  try {
			  rt.exec("cmd   /c  rd /s /q " + rootFileDirectory);
		  } catch (IOException e) {			  
		   logger.info("Schema.runtimeDelFile throw exception ! ");
		   //e.printStackTrace();
		  }
	}

	/**
	 * 
	 * @param fileDirectory
	 * @return 返回创建的文件绝对路径
	 */
	private String checkFileExists(String fileDirectory) {
		String fileAbsolutePath = "";
		File file = new File(fileDirectory);
		if (!file.exists()) {
			if (file.mkdir()) {
				fileAbsolutePath = file.getAbsolutePath();
				logger.info("create file absolute path " + true);
			} else {
				logger.info("create file absolute path " + false);
			}
		} else {
			fileAbsolutePath = file.getAbsolutePath();
		}
		logger.info("create file absolute path " + fileAbsolutePath);
		return fileAbsolutePath;
	}

	/**
	 * 格式化日期
	 * 
	 * @param formatStr
	 *            传入格式化的字符串
	 * @return string
	 */
	private String transformDateStr(Date createDate,String formatStr) {
		return DateFormatUtils.format(createDate, formatStr);
	}
}
