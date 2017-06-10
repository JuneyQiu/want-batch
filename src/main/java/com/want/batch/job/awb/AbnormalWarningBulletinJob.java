/**
 *<pre>
 *
 * Project name：want-batch
 *
 * File name：AbnormalWarningBulletinJob.java
 *
 * File creation time： 2013-12-20 下午05:10:12
 * 
 * Copyright (c) 2013, WantWant Group All Rights Reserved. 
 *
 * </pre>	
 */
package com.want.batch.job.awb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.data.pojo.AwbPojo;
import com.want.service.SmsModel;

/**
 * @author Chris Yu
 * 
 * @description : 异常预警通报 job
 * 
 * @mail 18621961029@163.com
 * 
 *       Create time 下午05:10:12
 * 
 * @version
 * @since JDK 1.6
 */
@Component
public class AbnormalWarningBulletinJob extends AbstractWantJob {

	private static final String EXCEL_SUFFIX = ".xls";

	private static final String ERROR_EXCELFILE_SUFFIX = "_log";

	private static final int DEFUALT_TIME = 5;

	private static final String DF_LONG = "yyyy-MM-dd HH:mm:ss";

	private static final String DF_STORT = "yyyyMMdd";

	@Value("${abnormal.warning.bulletin}")
	private String smb_url;

	@Value("${awb.excel.file.prefix}")
	private String excelFile_prefix;

	@Value("${awb.send.date}")
	private String sendDate;

	@Value("${sms.func.sid}")
	private String func_sid;

	@Value("${sms.func.case.id}")
	private String func_case_id;

	@Value("${if.promptly.execute}")
	private String execute;

	@Value("${execute.time}")
	private String execute_time;

	private StringBuilder smbStr = new StringBuilder();

	private List<SmsModel> SmsModelList = null;

	private String phone = "";
	private String context = "";

	// 计数器变量
	private int counter = 0;

	@Override
	public void execute() throws Exception {

		DateTime dateTime = new DateTime();
		smbStr.append(smb_url);
		File file = new File(smb_url);
		// 获取当前文件夹下所有文件
		File[] fileList = file.listFiles();
		// 保存当前日期对应的excel文件
		List<String> currentDateExcelData = new ArrayList<String>();
		String currentDate = DateFormatUtils
				.format(dateTime.toDate(), DF_STORT);
		List<String> fileUrlList = new ArrayList<String>();
		for (File Files : fileList) {
			String fileName = Files.getName();
			String fileUrl = Files.getPath();
			// 抓去当前日期的excel文件
			if (fileUrl.indexOf(currentDate) != -1) {
				// 不读取带有log的文件
				if (fileUrl.indexOf(ERROR_EXCELFILE_SUFFIX) != -1) {
					continue;
				}
				currentDateExcelData.add(fileUrl);
				logger.info(String.format(
						"current date SMB file name [%s] url [%s] .", fileName,
						fileUrl));
				continue;
			}
			// 保存共享文件内不属于当天的有文件路径
			if (fileUrl.indexOf(EXCEL_SUFFIX) != -1) {
				fileUrlList.add(fileUrl);
			}
		}
		// 读取当前日期的excel文件，并解析写入sms_tbl
		readExcelInfo(currentDateExcelData);
		// 删除三天前的文件
		deleteFileBatch(smbStr.append(excelFile_prefix).toString(),
				fileUrlList, DateFormatUtils.format(dateTime.minusDays(3)
						.toDate(), DF_STORT));

	}

	/**
	 * 读取smb 协议下的Excel文件
	 * 
	 * @param currentDateExcelData
	 * @param smbFileUrlList
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void readExcelInfo(List<String> currentDateExcelData)
			throws MalformedURLException, IOException {
		HSSFWorkbook workbook = null;
		File currentSmbfile = null;
		FileInputStream fis = null;
		// 存放多个sheet异常数据
		Map<String, List<AwbPojo>> errorAwbSheetMap = null;
		// 存放row异常数据
		List<AwbPojo> errorAwbRowList = null;
		if (null != currentDateExcelData && currentDateExcelData.size() > 0) {
			for (String strSmbFileUrl : currentDateExcelData) {
				boolean flag = false;
				counter = 0;
				currentSmbfile = new File(strSmbFileUrl);
				fis = new FileInputStream(currentSmbfile);
				// 保存读取数据
				SmsModelList = new ArrayList<SmsModel>();
				workbook = new HSSFWorkbook(fis);
				errorAwbSheetMap = new HashMap<String, List<AwbPojo>>();
				int sheetNum = workbook.getNumberOfSheets();
				for (int i = 0; i < sheetNum; i++) {
					HSSFSheet sheet = workbook.getSheetAt(i);
					int rownum = sheet.getPhysicalNumberOfRows();
					errorAwbRowList = new ArrayList<AwbPojo>();
					// 从第二行读取
					for (int j = 1; j < rownum; j++) {
						StringBuilder sb = new StringBuilder();
						HSSFRow row = sheet.getRow(j);
						int cellNum = (int) row.getLastCellNum();
						AwbPojo awbPojo = new AwbPojo();
						SmsModel model = new SmsModel();
						for (int k = 0; k < cellNum; k++) {
							HSSFCell cell = row.getCell((short) k);
							if (null != cell) {
								// 解析cell
								if (k == 0 || k == 1) {
									switch (cell.getCellType()) {
									case HSSFCell.CELL_TYPE_STRING:
										String str = StringUtils.trim(cell
												.getStringCellValue());
										if (k == 0) {
											phone = str;
											// 检查手机号码
											flag = checkPhoneNum(flag, j, sb,
													cellNum, awbPojo, phone, k,
													workbook.getSheetName(i),
													currentSmbfile.getName());
										} else {
											if (StringUtils.isNotBlank(str)) {
												context = str;
											} else {
												sb.append("短信内容为空_");
												createAwbError(j, cellNum, sb,
														awbPojo);
												flag = true;
											}
										}
										break;
									case HSSFCell.CELL_TYPE_NUMERIC:
										if (HSSFDateUtil
												.isCellDateFormatted(cell)) {
											// 日期类型错误
											sb.append("手机号码必须是11位数字_");
											createAwbError(j, cellNum, sb,
													awbPojo);
											flag = true;
										} else {
											// 十六进制数转换成数字字符串类型
											phone = new DecimalFormat("#")
													.format(cell
															.getNumericCellValue());
											logger.debug(String.format(
													"decimal format %s", phone));
											flag = checkPhoneNum(flag, j, sb,
													cellNum, awbPojo, phone, k,
													workbook.getSheetName(i),
													currentSmbfile.getName());
										}
										break;

									case HSSFCell.CELL_TYPE_BLANK:

										flag = readCellNull(j, sb, cellNum,
												awbPojo, k);
										break;
									default:
										flag = readCellNull(j, sb, cellNum,
												awbPojo, k);
										break;
									}
								} else {
									break;
								}
								if (flag) {
									counter++;
									// break;
								}
							}
						}
						if (!flag) {
							createSmsModel(model, phone, context);
						} else {
							// 保存异常数据
							errorAwbRowList.add(awbPojo);
							// 保存每个sheet内容
							errorAwbSheetMap.put(workbook.getSheetName(i),
									errorAwbRowList);
						}
						// 重新改变flag值
						flag = false;
					}
				}
				// 写入sms_tbl表
				if (null != SmsModelList && SmsModelList.size() > 0) {
					this.getSmsSendService().sendBatchSms(SmsModelList);
				}
				// 错误数据重新保存,命名文件名后缀需增加“log”字样
				repeatErrorExcelFile(workbook, errorAwbSheetMap, strSmbFileUrl,
						counter);
			}
		}
	}

	/**
	 * 
	 * @param j
	 * @param sb
	 * @param cellNum
	 * @param awbPojo
	 * @param k
	 */
	private boolean readCellNull(int j, StringBuilder sb, int cellNum,
			AwbPojo awbPojo, int k) {

		switch (k) {
		case 0:
			sb.append("手机号码为空_");
			createAwbError(j, cellNum, sb, awbPojo);
			break;
		case 1:
			sb.append("短信内容为空_");
			createAwbError(j, cellNum, sb, awbPojo);
			break;
		// 预留扩展
		default:

			break;
		}

		return true;
	}

	/**
	 * 
	 * @param model
	 * @param phone
	 * @param context
	 */
	private void createSmsModel(SmsModel model, String phone, String context) {
		DateTime currentDate = new DateTime();
		model.setPhoneNumber(Long.valueOf(phone));
		model.setContent(context);
		if (NumberUtils.isNumber(func_sid)) {
			model.setFuncSid(Integer.valueOf(func_sid));
		} else {
			// defualt value 31
			model.setFuncSid(31);
		}
		model.setFuncCaseId(func_case_id);
		model.setCreateDate(new Date());
		// 判断开关
		if (NumberUtils.isNumber(execute)) {
			if (StringUtils.equals("1", execute)) {
				model.setSendDate(currentDate.plusMinutes(DEFUALT_TIME)
						.toDate());
			} else {
				if (StringUtils.isNotBlank(sendDate)) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(DF_LONG);
					try {
						Date simpleSendDate = dateFormat.parse(sendDate);
						model.setSendDate(simpleSendDate);
					} catch (ParseException e) {
						logger.debug("date fromat error !!!");
					}
				} else {
					if (StringUtils.isNotBlank(execute_time)) {
						int deferTime = Integer.valueOf(execute_time);
						model.setSendDate(currentDate.plusMinutes(deferTime)
								.toDate());
					} else {
						// 配置档内，执行时间为空默认系统当前时间延迟5分钟
						model.setSendDate(currentDate.plusMinutes(DEFUALT_TIME)
								.toDate());
					}
				}
			}
		}
		// 加入list
		SmsModelList.add(model);
	}

	/**
	 * 
	 * @param flag
	 * @param currentRowNum
	 * @param sb
	 * @param readCellNum
	 * @param awbPojo
	 * @param phone
	 * @param currentCellNum
	 * @return
	 */
	private boolean checkPhoneNum(boolean flag, int currentRowNum,
			StringBuilder sb, int readCellNum, AwbPojo awbPojo, String phone,
			int currentCellNum, String sheetName, String fileName) {
		if (StringUtils.isNotBlank(phone)) {
			if (NumberUtils.isNumber(phone)) {
				if (!phone.matches("1[3-8]+\\d{9}")) {
					flag = phoneErrorTyple(currentRowNum, sb, readCellNum,
							awbPojo, currentCellNum, sheetName, "无效的手机号码_",
							fileName);
				}
			} else {
				flag = phoneErrorTyple(currentRowNum, sb, readCellNum, awbPojo,
						currentCellNum, sheetName, "手机号码必须是11位数字_", fileName);
			}
		} else {
			flag = phoneErrorTyple(currentRowNum, sb, readCellNum, awbPojo,
					currentCellNum, sheetName, "手机号码为空_", fileName);
		}
		return flag;
	}

	/**
	 * 
	 * @param currentRowNum
	 * @param sb
	 * @param readCellNum
	 * @param awbPojo
	 * @param currentCellNum
	 * @param sheetName
	 * @param errorContext
	 * @return
	 */
	private boolean phoneErrorTyple(int currentRowNum, StringBuilder sb,
			int readCellNum, AwbPojo awbPojo, int currentCellNum,
			String sheetName, String errorContext, String fileName) {
		sb.append(errorContext);
		createAwbError(currentRowNum, readCellNum, sb, awbPojo);
		return errorDailyRecord(fileName, sheetName, currentRowNum,
				currentCellNum, sb);
	}

	/**
	 * 创建awb error 内容
	 * 
	 * @param rowNum
	 * @param cellNum
	 * @param sb
	 * @param awbPojo
	 */
	private void createAwbError(int rowNum, int cellNum, StringBuilder sb,
			AwbPojo awbPojo) {
		// 保存数据
		awbPojo.setColumnNum(cellNum);
		awbPojo.setRowNum(rowNum);
		awbPojo.setErrrorContext(sb.toString());
	}

	/**
	 * Error daily record
	 * 
	 * @param sf
	 * @param rowNum
	 * @param cellNum
	 * @return
	 */
	private boolean errorDailyRecord(String smsFileName, String sheetName,
			int rowNum, int cellNum, StringBuilder sb) {
		// 记录日志
		logger.info(String
				.format("awb excel file  name %s,error sheetName[%s] [row %s column %s] context: [%s].",
						smsFileName, sheetName, rowNum, (cellNum + 1),
						sb.toString()));
		return true;
	}

	/**
	 * <pre>
	 * 重新保存异常文件
	 *  格式SMS_20131211_上海分公司_001{异常信息}.xls
	 *  	add by Chris Yu
	 *      2013-12-23 
	 * @param sb
	 * @param strSmsFileUrl
	 * @param flag
	 * @throws IOException
	 * </pre>
	 */
	private void repeatErrorExcelFile(HSSFWorkbook workbook,
			Map<String, List<AwbPojo>> errorAwbSheetMap, String strSmsFileUrl,
			int counter) throws IOException {
		if (counter > 0) {
			StringBuilder smbErrorContextFile = new StringBuilder()
					.append(StringUtils.substringBeforeLast(strSmsFileUrl,
							EXCEL_SUFFIX)).append(ERROR_EXCELFILE_SUFFIX)
					.append(EXCEL_SUFFIX);
			File smsFile = new File(smbErrorContextFile.toString());
			FileOutputStream fos = new FileOutputStream(smsFile);
			if (null != errorAwbSheetMap && errorAwbSheetMap.size() > 0) {
				saveAwbExcelErrorData(workbook, errorAwbSheetMap);
			}
			workbook.write(fos);

			fos.close();
		}
	}

	/**
	 * 写入awb异常数据
	 * 
	 * @param workbook
	 * @param errorAwbSheetMap
	 */
	private void saveAwbExcelErrorData(HSSFWorkbook workbook,
			Map<String, List<AwbPojo>> errorAwbSheetMap) {
		for (Map.Entry<String, List<AwbPojo>> map : errorAwbSheetMap.entrySet()) {
			String sheetName = map.getKey();
			List<AwbPojo> awbPojoList = map.getValue();
			HSSFSheet sheet = workbook.getSheet(sheetName);
			writeCell(sheet, 0, 2, "错误提示");
			for (AwbPojo awbPojo : awbPojoList) {
				String errorContext = StringUtils.substringBeforeLast(awbPojo
						.getErrrorContext().toString(), "_");
				// 写在第三个column
				writeCell(sheet, awbPojo.getRowNum(), 2, errorContext);
			}
		}
	}

	private void writeCell(HSSFSheet sheet, int rowNum, int cellNum,
			String context) {
		HSSFRow headrow = sheet.createRow(rowNum);
		HSSFCell headCell = headrow.createCell((short) cellNum);
		headCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		headCell.setCellValue(context);
	}

	/**
	 * 
	 * 
	 * @param smbStr
	 *            smb文件路径前缀
	 * @param fileUrlList
	 * @param beforeThreeDayStr
	 * @throws MalformedURLException
	 */
	public void deleteFileBatch(String smbStr, List<String> fileUrlList,
			String beforeThreeDayStr) throws MalformedURLException {
		int longSmbPrefix = smbStr.length();
		int interceptedLength = longSmbPrefix + beforeThreeDayStr.length();
		Integer beforeThreeDayInt = 0;
		String interceptedAfterStr = "";
		if (NumberUtils.isNumber(beforeThreeDayStr)) {
			beforeThreeDayInt = Integer.valueOf(beforeThreeDayStr);
		}
		if (null != fileUrlList && fileUrlList.size() > 0) {
			for (String fileUrl : fileUrlList) {
				// 获取文件中间部分日期
				interceptedAfterStr = fileUrl.substring(longSmbPrefix,
						interceptedLength);
				// 找出文件是三天前的
				if (beforeThreeDayInt >= Integer.valueOf(interceptedAfterStr)) {
					File smbFile = new File(fileUrl);
					// 自动删除
					deleteSmbFile(smbFile, fileUrl);
				}
			}
		}
	}

	private void deleteSmbFile(File smsFile, String fileUrl) {
		// 判断文件是否存在
		if (smsFile.exists()) {
			smsFile.delete();
			logger.info(String.format(
					"awb file delete url [%s],delete successful.", fileUrl));
		}
	}
}