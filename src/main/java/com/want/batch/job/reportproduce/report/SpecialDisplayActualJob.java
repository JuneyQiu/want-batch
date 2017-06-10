package com.want.batch.job.reportproduce.report;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.want.batch.job.reportproduce.dao.DirectiveTblDao;
import com.want.batch.job.reportproduce.dao.SpecialDisplayActualDao;
import com.want.batch.job.reportproduce.pojo.Constant;
import com.want.batch.job.reportproduce.util.CommonUtils;
import com.want.batch.job.utils.Toolkit;

/**
 * SpecialDisplayActualJob
 * 
 * <table>
 * <tr>
 * <th>日期</th>
 * <th>变更说明</th>
 * </tr>
 * <tr>
 * <td>2013-6-24</td>
 * <td>Nash新建</td>
 * </tr>
 * </table>
 * 
 *@author Nash
 */
@Component
public class SpecialDisplayActualJob {

	protected final Log logger = LogFactory.getLog(SpecialDisplayActualJob.class);
	
	@Autowired
	public SpecialDisplayActualDao specialDisplayActualDao;
	
	@Autowired
	public DirectiveTblDao directiveTblDao;
	
	@Autowired
	public DataSource iCustomerDataSource;
	
	@Autowired
	public CommonUtils commonUtils;

	@Async
	public Future<Boolean> run(Map<String, Object> directiveTblMap)  {
		
		boolean result = true;
		
		try {
			// 报表类型
			String reportType = (null == directiveTblMap.get("SELECT_PARAM_VALUE") ? null : directiveTblMap
				.get("SELECT_PARAM_VALUE")
					.toString()
					.split(";")[4]);
			
			// 排程执行时，更新DIRECTIVE_TBL表中的栏位STATUS
			commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_RUNNING, Integer.parseInt(directiveTblMap.get("SID").toString()), null);

			// 产生特陈实际-审核档报表
			if (Constant.REPORT_SPECDISPLAYAUDITRPT.equals(reportType)) {

				logger.info("产生特陈实际-审核档报表" + directiveTblMap.get("SID") + "开始时间：" + new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date()));
				downloadSpecDisplayActualRpt("audit", directiveTblMap);
				logger.info("产生特陈实际-审核档报表 " + directiveTblMap.get("SID") + "完成结束时间：" + new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date()));
			}
			// 产生特陈实际-基础档报表
			else if (Constant.REPORT_SPECDISPLAYBASICRPT.equals(reportType)) {

				logger.info("产生特陈实际-基础档报表" + directiveTblMap.get("SID") + "开始时间：" + new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date()));
				downloadSpecDisplayActualRpt("basic", directiveTblMap);
				logger.info("产生特陈实际-基础档报表" + directiveTblMap.get("SID")+ "完成结束时间：" + new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date()));
			}
			// 产生特陈实际-品项档报表
			else if (Constant.REPORT_SPECDISPLAYPRODRPT.equals(reportType)) {

				logger.info("产生特陈实际-品项档报表" + directiveTblMap.get("SID") + "开始时间：" + new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date()));
				downloadSpecDisplayActualRpt("prod", directiveTblMap);
				logger.info("产生特陈实际-品项档报表" + directiveTblMap.get("SID") + "完成结束时间：" + new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date()));
			}
			else {				
				
				logger.error("资料异常无法产生报表");
				
				try {				
					
					commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_EXCEPTION, Integer.parseInt(directiveTblMap.get("SID").toString()),"资料异常无法产生报表");
					logger.info("产生异常，更新db状态为exception");
				}
				catch (Exception e1) {
					
					logger.error(Constant.generateExceptionMessage(e1));
				}		
			}
			
			try {
				commonUtils.sendMail(directiveTblDao.getDirectiveBySid(directiveTblMap.get("SID").toString()));				
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		catch (Exception e) {
			
			result = false;
			
			// 此处已是线程的最后一层，发生异常后MonitorJob无法捕获，所以在此处将异常信息记录进log文件中
			logger.error(Constant.generateExceptionMessage(e));

			try {				
				commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_EXCEPTION, Integer.parseInt(directiveTblMap.get("SID").toString()),Constant.generateExceptionMessage(e));
				logger.info("产生异常，更新db状态为exception");
			}
			catch (Exception e1) {
				
				logger.error(Constant.generateExceptionMessage(e1));
			}		
		}
		finally {
			
			// 内存溢出属于error，不属于exception，所以捕获不到，所以只能放在finally里面处理
			// 如果执行完，指令状态还是running，则认定为发生内存溢出，则修改状态为exception，原因为内存溢出
			Map<String, Object> map = this.directiveTblDao.findDirectiveById(directiveTblMap.get("SID").toString());
			
			if(map.get("STATUS").equals(Constant.DIRECTIVE_STATUS_RUNNING)) {
				
				result = false;
				
				commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_EXCEPTION, Integer.parseInt(directiveTblMap.get("SID").toString()), "内存溢出");
			}
		}
		
		return new AsyncResult<Boolean>(Boolean.valueOf(result));
	}

	/**
	 * <pre>
	 * 2013-6-20 Nash
	 * 下载特陈实际表
	 * </pre>
	 * 
	 * <ol>
	 * <li>
	 * <li>
	 * </ol>
	 * 
	 * @param map
	 * @throws Exception 当有异常产生时，此方法可以将异常抛出
	 */
	private void downloadSpecDisplayActualRpt(String type, Map<String, Object> map) throws Exception {

		Connection conn = null;
		ResultSet specDisplayAuditRpts = null;
		
		try {
			
			logger.info("取得数据源。。。");
			conn = iCustomerDataSource.getConnection();
			
			logger.info(type + "更新db状态为running");
			logger.info(type + "开始执行查询。。。。" + new Date());
			
			// 获取数据
			specDisplayAuditRpts = specialDisplayActualDao.getSpecDisplayActualRpt(conn, map);

			logger.info(type + "开始查询完毕。。。。" + new Date());
			
			// 文件
			File file = new File((null == map.get("ROOT_PATH") ? null : map.get("ROOT_PATH").toString())
					+ (null == map.get("FILE_NAME") ? null : map.get("FILE_NAME").toString())
					+ ".xls");
			
			// 创建文件
			if (!file.exists()) {

				file.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(file);
			
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = null;
			HSSFCellStyle cellStyle = wb.createCellStyle();

			int i = 1;
			int lastRowNum =0;
			logger.info(type + "开始创建excel文件");
			
			// 若有资料，则循环添加资料
			while (specDisplayAuditRpts.next()) {

				// 当为第一笔资料时，创建表头;否则，达到每个sheet的最大笔数时，新建一个sheet
				if ((specDisplayAuditRpts.getRow() == 1) || (sheet != null && sheet.getLastRowNum() >= Constant.MAX_LINE_NUM_EVERY_SHEET)) {

					sheet = wb.createSheet("sheet" + i);
					logger.info(type + "创建excel行表头");
					createHeadRow(type, sheet, cellStyle);
					i++;
				}
				
				lastRowNum = sheet.getLastRowNum();
				
				// 以下是添加明细资料
				createDetailRow(type, sheet, lastRowNum + 1, specDisplayAuditRpts);
			}

			// 当没有资料时给报表添加标题
			if (sheet == null) {
				
				sheet = wb.createSheet("sheet" + i);
				createHeadRow(type, sheet, cellStyle);
			}
			
			if (i == 1 && sheet.getLastRowNum() == 0) {
				
				logger.info(type + "资料总笔数：0");
			}
			else {
			
				logger.info(type + "资料总笔数：" + (Constant.MAX_LINE_NUM_EVERY_SHEET*(i-2)+(sheet.getLastRowNum() + 1 )-(i-1)));
			}
			
			wb.write(out);

			out.flush();
			out.close();

			logger.info("Generated " + file.toString());
			// 排程执行完成后，更新DIRECTIVE_TBL表中的栏位STATUS,FILE_NAME
			commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_FINISH, Integer.parseInt(map.get("SID").toString()), null);
			
			logger.info(type + "产生完毕，更新db状态为finish");
		}
		// 无论异常有无发生，此处都需要关闭结果集和数据库连接
		finally {

			logger.info(type + "执行完毕，关闭数据库连接");
			if (specDisplayAuditRpts != null) {
			
					specDisplayAuditRpts.close();
			}
			
			if (conn != null) {
				
					conn.close();
			}
		}
	}
	
	/**
	 * <pre>
	 * 2013-7-5 Mirabelle
	 * 创建报表头.
	 * </pre>	
	 * 
	 * @param type
	 * @param sheet
	 * @param cellStyle
	 */
	public void createHeadRow(String type, HSSFSheet sheet, HSSFCellStyle cellStyle) {
		
		if ("audit".equals(type)) {
			
			createAuditHeadRow(sheet, cellStyle);
		}
		else if ("basic".equals(type)) {
			
			createBasicHeadRow(sheet, cellStyle);
		}
		else if ("prod".equals(type)) {
			
			createProdHeadRow(sheet, cellStyle);
		}
	}
	
	/**
	 * <pre>
	 * 2013-7-5 Mirabelle
	 * 添加报表明细资料.
	 * </pre>	
	 * 
	 * @param type
	 * @param sheet
	 * @param cellStyle
	 * @throws SQLException 
	 */
	public void createDetailRow(String type, HSSFSheet sheet, int lastRowNum, ResultSet specDisplayActualRpts) throws SQLException {
		
		if ("audit".equals(type)) {
			
			createAuditRow(sheet, lastRowNum, specDisplayActualRpts);
		}
		else if ("basic".equals(type)) {
			
			createBasicRow(sheet, lastRowNum , specDisplayActualRpts);
		}
		else if ("prod".equals(type)) {
			
			createProdRow(sheet, lastRowNum, specDisplayActualRpts);
		}
	}

	/**
	 * <pre>
	 * 2013-6-20 Nash
	 * 审核档报表 表头
	 * </pre>
	 * 
	 * <ol>
	 * <li>
	 * <li>
	 * </ol>
	 * 
	 * @param sheet
	 * @param headStyle
	 */
	private void createAuditHeadRow(HSSFSheet sheet, HSSFCellStyle headStyle) {

		headStyle.setFillPattern(HSSFCellStyle.ALIGN_LEFT);
		headStyle.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);
		HSSFRow rowHeader = sheet.createRow(0);

		HSSFCell divisionCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_DIVISION_COL);
		divisionCell.setCellStyle(headStyle);
		divisionCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		divisionCell.setCellValue("事业部");

		HSSFCell companyCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_COMPANY_COL);
		companyCell.setCellStyle(headStyle);
		companyCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		companyCell.setCellValue("分公司");

		HSSFCell branchCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_BRANCH_COL);
		branchCell.setCellStyle(headStyle);
		branchCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		branchCell.setCellValue("营业所");

		HSSFCell custIdCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_CUST_ID_COL);
		custIdCell.setCellStyle(headStyle);
		custIdCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		custIdCell.setCellValue("客户编号");

		HSSFCell custNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_CUST_NAME_COL);
		custNameCell.setCellStyle(headStyle);
		custNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		custNameCell.setCellValue("客户名称");

		HSSFCell yearMonthCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_YEAR_MONTH_COL);
		yearMonthCell.setCellStyle(headStyle);
		yearMonthCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		yearMonthCell.setCellValue("特陈年月");

		HSSFCell policyNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_POLICY_NAME_COL);
		policyNameCell.setCellStyle(headStyle);
		policyNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		policyNameCell.setCellValue("特陈政策名称");

		HSSFCell sdNoCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_SD_NO_COL);
		sdNoCell.setCellStyle(headStyle);
		sdNoCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		sdNoCell.setCellValue("单据编码");

		HSSFCell checkStatusCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_CHECK_STATUS_COL);
		checkStatusCell.setCellStyle(headStyle);
		checkStatusCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		checkStatusCell.setCellValue("实际单据状态");

		HSSFCell fillInStatusYdCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_FILL_IN_STATUS_YD_COL);
		fillInStatusYdCell.setCellStyle(headStyle);
		fillInStatusYdCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		fillInStatusYdCell.setCellValue("业代填写状态");

		HSSFCell fillInStatusZrCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_FILL_IN_STATUS_ZR_COL);
		fillInStatusZrCell.setCellStyle(headStyle);
		fillInStatusZrCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		fillInStatusZrCell.setCellValue("主任填写状态");

		HSSFCell fillInStatusKhCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_FILL_IN_STATUS_KH_COL);
		fillInStatusKhCell.setCellStyle(headStyle);
		fillInStatusKhCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		fillInStatusKhCell.setCellValue("客户填写状态");

		HSSFCell auditCheckStatusXgCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_AUDIT_CHECK_STATUS_XG_COL);
		auditCheckStatusXgCell.setCellStyle(headStyle);
		auditCheckStatusXgCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		auditCheckStatusXgCell.setCellValue("销管审核状态");

		HSSFCell auditCheckStatusZrCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_AUDIT_CHECK_STATUS_ZR_COL);
		auditCheckStatusZrCell.setCellStyle(headStyle);
		auditCheckStatusZrCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		auditCheckStatusZrCell.setCellValue("主任审核状态");

		HSSFCell auditCheckStatusSzCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_AUDIT_CHECK_STATUS_SZ_COL);
		auditCheckStatusSzCell.setCellStyle(headStyle);
		auditCheckStatusSzCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		auditCheckStatusSzCell.setCellValue("所长审核状态");

		HSSFCell auditCheckStatusZjCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_AUDIT_CHECK_STATUS_ZJ_COL);
		auditCheckStatusZjCell.setCellStyle(headStyle);
		auditCheckStatusZjCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		auditCheckStatusZjCell.setCellValue("总监审核状态");
	}

	/**
	 * <pre>
	 * 2013-6-20 Nash
	 *  审核档报表  创建  row
	 * </pre>
	 * 
	 * <ol>
	 * <li>
	 * <li>
	 * </ol>
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param rpt
	 * @throws SQLException 
	 */
	private void createAuditRow(HSSFSheet sheet, int rowNum, ResultSet rpt) throws SQLException {

		HSSFRow row = sheet.createRow(rowNum);

		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_DIVISION_COL, rpt.getString("DIVISION_NAME"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_COMPANY_COL, rpt.getString("COMPANY_NAME"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_BRANCH_COL, rpt.getString("BRANCH_NAME"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_CUST_ID_COL, rpt.getString("CUST_ID"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_CUST_NAME_COL, rpt.getString("CUST_NAME"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_YEAR_MONTH_COL, rpt.getString("YEAR_MONTH"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_POLICY_NAME_COL, rpt.getString("POLICYNAME"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_SD_NO_COL, rpt.getString("SD_NO"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_CHECK_STATUS_COL, rpt.getString("CHECK_STATUS"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_FILL_IN_STATUS_YD_COL, rpt.getString("FILL_IN_STATUS_YD"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_FILL_IN_STATUS_ZR_COL, rpt.getString("FILL_IN_STATUS_ZR"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_FILL_IN_STATUS_KH_COL, rpt.getString("FILL_IN_STATUS_KH"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_AUDIT_CHECK_STATUS_XG_COL, rpt.getString("AUDIT_CHECK_STATUS_XG"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_AUDIT_CHECK_STATUS_ZR_COL, rpt.getString("AUDIT_CHECK_STATUS_ZR"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_AUDIT_CHECK_STATUS_SZ_COL, rpt.getString("AUDIT_CHECK_STATUS_SZ"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_AUDIT_AUDIT_CHECK_STATUS_ZJ_COL, rpt.getString("AUDIT_CHECK_STATUS_ZJ"));
	}

	/**
	 * <pre>
	 * 2013-6-20 Nash
	 *  创建 cell
	 * </pre>
	 * 
	 * <ol>
	 * <li>
	 * <li>
	 * </ol>
	 * 
	 * @param row
	 * @param colNo
	 * @param val
	 */
	protected void createACell(HSSFRow row, int colNo, String val) {

		HSSFCell cell = row.createCell((byte) colNo);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(val);
	}

	/**
	 * <pre>
	 * 2013-6-20 Nash
	 * 特陈实际-基础档报表 表头
	 * </pre>
	 * 
	 * <ol>
	 * <li>
	 * <li>
	 * </ol>
	 * 
	 * @param sheet
	 * @param headStyle
	 */
	private void createBasicHeadRow(HSSFSheet sheet, HSSFCellStyle headStyle) {

		headStyle.setFillPattern(HSSFCellStyle.ALIGN_LEFT);
		headStyle.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);
		HSSFRow rowHeader = sheet.createRow(0);

		HSSFCell divisionCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_DIVISION_COL);
		divisionCell.setCellStyle(headStyle);
		divisionCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		divisionCell.setCellValue("事业部");

		HSSFCell compCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_COMPANY_NAME_COL);
		compCell.setCellStyle(headStyle);
		compCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		compCell.setCellValue("分公司");

		HSSFCell branchCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_BRANCH_NAME_COL);
		branchCell.setCellStyle(headStyle);
		branchCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		branchCell.setCellValue("营业所");

		HSSFCell thirdNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_THIRD_NAME_COL);
		thirdNameCell.setCellStyle(headStyle);
		thirdNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		thirdNameCell.setCellValue("三级地");

		HSSFCell custIdCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_CUST_ID_COL);
		custIdCell.setCellStyle(headStyle);
		custIdCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		custIdCell.setCellValue("客户编号");

		HSSFCell custNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_CUST_NAME_COL);
		custNameCell.setCellStyle(headStyle);
		custNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		custNameCell.setCellValue("客户名称");
		
	  // 客户状态 add amy 2015-10-12
		HSSFCell custNameStatus = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_CUST_STATUS_COL);
		custNameStatus.setCellStyle(headStyle);
		custNameStatus.setEncoding(HSSFCell.ENCODING_UTF_16);
		custNameStatus.setCellValue("客户状态");

		HSSFCell amountPerMonthCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AMOUNT_PER_MONTH_COL);
		amountPerMonthCell.setCellStyle(headStyle);
		amountPerMonthCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		amountPerMonthCell.setCellValue("月度标准投入费用");

		HSSFCell yearMonthCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_YEAR_MONTH_COL);
		yearMonthCell.setCellStyle(headStyle);
		yearMonthCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		yearMonthCell.setCellValue("特陈年月");

		HSSFCell policyNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_POLICY_NAME_COL);
		policyNameCell.setCellStyle(headStyle);
		policyNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		policyNameCell.setCellValue("特陈政策名称");

		HSSFCell sdNoCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_SD_NO_COL);
		sdNoCell.setCellStyle(headStyle);
		sdNoCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		sdNoCell.setCellValue("单据编码");

		HSSFCell storeIdCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_ID_COL);
		storeIdCell.setCellStyle(headStyle);
		storeIdCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		storeIdCell.setCellValue("终端店编码");
		
		// 添加新终端编码栏位 2014-02-13 mandy add
		HSSFCell newStoreIdCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_MDM_STORE_ID_COL);
		newStoreIdCell.setCellStyle(headStyle);
		newStoreIdCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		newStoreIdCell.setCellValue("新终端编码");

		HSSFCell storeNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_NAME_COL);
		storeNameCell.setCellStyle(headStyle);
		storeNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		storeNameCell.setCellValue("终端店名称");

		HSSFCell storeAcreageCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_ACREAGE_COL);
		storeAcreageCell.setCellStyle(headStyle);
		storeAcreageCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		storeAcreageCell.setCellValue("终端店面积");

		HSSFCell storePhoneCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_PHONE_COL);
		storePhoneCell.setCellStyle(headStyle);
		storePhoneCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		storePhoneCell.setCellValue("终端电话号码");

		HSSFCell storeAddressCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_ADDRESS_COL);
		storeAddressCell.setCellStyle(headStyle);
		storeAddressCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		storeAddressCell.setCellValue("终端地址");

		HSSFCell storeOwnerCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_OWNER_COL);
		storeOwnerCell.setCellStyle(headStyle);
		storeOwnerCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		storeOwnerCell.setCellValue("终端联系人");

		HSSFCell actualCheckerCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_DISPlAY_ACTUAL_CHECKER_COL);
		actualCheckerCell.setCellStyle(headStyle);
		actualCheckerCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		actualCheckerCell.setCellValue("特陈实际检核人");

		HSSFCell storeDisplaySidCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_DISPLAY_SID_COL);
		storeDisplaySidCell.setCellStyle(headStyle);
		storeDisplaySidCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		storeDisplaySidCell.setCellValue("流水码");

		HSSFCell locationTypeNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_LOCATION_TYPE_NAME_COL);
		locationTypeNameCell.setCellStyle(headStyle);
		locationTypeNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		locationTypeNameCell.setCellValue("特陈位置");

		HSSFCell displayTypeNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_DISPLAY_TYPE_NAME_COL);
		displayTypeNameCell.setCellStyle(headStyle);
		displayTypeNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		displayTypeNameCell.setCellValue("特陈形式");

		HSSFCell additionalOptionsCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ADDITIONAL_OPTIONS_COL);
		additionalOptionsCell.setCellStyle(headStyle);
		additionalOptionsCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		additionalOptionsCell.setCellValue("附加选项");

		HSSFCell notReachCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_NOT_REACH_COL);
		notReachCell.setCellStyle(headStyle);
		notReachCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		notReachCell.setCellValue("不达标");

		HSSFCell assertIdCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ASSERT_ID_COL);
		assertIdCell.setCellStyle(headStyle);
		assertIdCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		assertIdCell.setCellValue("财产编号");

		HSSFCell visitDateCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_FIRST_VIST_DATE_COL);
		visitDateCell.setCellStyle(headStyle);
		visitDateCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		visitDateCell.setCellValue("终端首次排定拜访时间");

		HSSFCell actualFillStatusCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_ACTUAL_FILL_STATUS_COL);
		actualFillStatusCell.setCellStyle(headStyle);
		actualFillStatusCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		actualFillStatusCell.setCellValue("终端点实际填写状况");

		HSSFCell fillDateYdCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_FILL_DATE_YD_COL);
		fillDateYdCell.setCellStyle(headStyle);
		fillDateYdCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		fillDateYdCell.setCellValue("业代实际首次填写时间");

		HSSFCell accountYdCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ACCOUNT_YD_COL);
		accountYdCell.setCellStyle(headStyle);
		accountYdCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		accountYdCell.setCellValue("业代实际首次填写人工号");

		HSSFCell userNameYdCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_USER_NAME_YD_COL);
		userNameYdCell.setCellStyle(headStyle);
		userNameYdCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		userNameYdCell.setCellValue("业代实际首次填写人姓名");

		HSSFCell fillDateZrCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_FILL_DATE_ZR_COL);
		fillDateZrCell.setCellStyle(headStyle);
		fillDateZrCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		fillDateZrCell.setCellValue("主任实际首次填写时间");

		HSSFCell accountZrCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ACCOUNT_ZR_COL);
		accountZrCell.setCellStyle(headStyle);
		accountZrCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		accountZrCell.setCellValue("主任实际首次填写人工号");

		HSSFCell userNameZrCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_USER_NAME_ZR_COL);
		userNameZrCell.setCellStyle(headStyle);
		userNameZrCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		userNameZrCell.setCellValue("主任实际首次填写人姓名");

		// 2015-09-23 mandy modify 实际费用修改成平均费用标准、旺旺公司承担费用、经销商承担费用
		HSSFCell actualCostCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ACTUAL_COST_COL);
		actualCostCell.setCellStyle(headStyle);
		actualCostCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		actualCostCell.setCellValue("平均费用标准");
		
		HSSFCell companyCostCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ACTUAL_COST_COMPANY_COL);
		companyCostCell.setCellStyle(headStyle);
		companyCostCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		companyCostCell.setCellValue("旺旺公司承担费用");
		
		HSSFCell customerCostCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ACTUAL_COST_CUSTOMER_COL);
		customerCostCell.setCellStyle(headStyle);
		customerCostCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		customerCostCell.setCellValue("经销商承担费用");
		
		HSSFCell actualSalesCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ACTUAL_SALES_COL);
		actualSalesCell.setCellStyle(headStyle);
		actualSalesCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		actualSalesCell.setCellValue("实际销量");

		HSSFCell fillDateKhCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_FILL_DATE_KH_COL);
		fillDateKhCell.setCellStyle(headStyle);
		fillDateKhCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		fillDateKhCell.setCellValue("客户填写实际费用与销量时间");

		HSSFCell isReceivedAgreementCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_IS_RECEIVED_AGREEMENT_COL);
		isReceivedAgreementCell.setCellStyle(headStyle);
		isReceivedAgreementCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		isReceivedAgreementCell.setCellValue("销管实际是否收到特陈协议");

		HSSFCell auditDateZrCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_DATE_ZR_COL);
		auditDateZrCell.setCellStyle(headStyle);
		auditDateZrCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		auditDateZrCell.setCellValue("主任实际审核时间");

		HSSFCell auditStatusZrCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_STATUS_ZR_COL);
		auditStatusZrCell.setCellStyle(headStyle);
		auditStatusZrCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		auditStatusZrCell.setCellValue("主任审核状态");

		HSSFCell auditDateSzCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_DATE_SZ_COL);
		auditDateSzCell.setCellStyle(headStyle);
		auditDateSzCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		auditDateSzCell.setCellValue("所长实际审核时间");

		HSSFCell auditStatusSzCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_STATUS_SZ_COL);
		auditStatusSzCell.setCellStyle(headStyle);
		auditStatusSzCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		auditStatusSzCell.setCellValue("所长审核状态");

		HSSFCell approvedAmountSzCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_APPROVED_AMOUNT_SZ_COL);
		approvedAmountSzCell.setCellStyle(headStyle);
		approvedAmountSzCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		approvedAmountSzCell.setCellValue("所长核定费用");

		HSSFCell auditDateZjCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_DATE_ZJ_COL);
		auditDateZjCell.setCellStyle(headStyle);
		auditDateZjCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		auditDateZjCell.setCellValue("总监实际审核时间");

		HSSFCell auditStatusZjCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_STATUS_ZJ_COL);
		auditStatusZjCell.setCellStyle(headStyle);
		auditStatusZjCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		auditStatusZjCell.setCellValue("总监核定状态");

		HSSFCell approvedAmountZjCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_APPROVED_AMOUNT_ZJ_COL);
		approvedAmountZjCell.setCellStyle(headStyle);
		approvedAmountZjCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		approvedAmountZjCell.setCellValue("总监核定费用");

		HSSFCell storeTypeCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_TYPE_COL);
		storeTypeCell.setCellStyle(headStyle);
		storeTypeCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		storeTypeCell.setCellValue("终端类别");

		HSSFCell withdrawAmountCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_WITHDRAW_AMOUNT_COL);
		withdrawAmountCell.setCellStyle(headStyle);
		withdrawAmountCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		withdrawAmountCell.setCellValue("当月回单金额");
	}

	/**
	 * <pre>
	 * 2013-6-20 Nash
	 * 特陈实际-基础档报表 row
	 * </pre>
	 * 
	 * <ol>
	 * <li>
	 * <li>
	 * </ol>
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param rpt
	 * @throws SQLException 
	 */
	private void createBasicRow(HSSFSheet sheet, int rowNum, ResultSet rpt) throws SQLException {

		HSSFRow row = sheet.createRow(rowNum);

		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_DIVISION_COL, rpt.getString("divisionName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_COMPANY_NAME_COL, rpt.getString("companyName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_BRANCH_NAME_COL, rpt.getString("branchName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_THIRD_NAME_COL, rpt.getString("thirdName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_CUST_ID_COL, rpt.getString("custId"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_CUST_NAME_COL, rpt.getString("custName"));
		
	  // 客户状态 add amy 20115-10-12
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_CUST_STATUS_COL, rpt.getString("CUSTOMSTATUS"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AMOUNT_PER_MONTH_COL, "" + rpt.getDouble("amountPerMonth"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_YEAR_MONTH_COL, rpt.getString("yearMonth"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_POLICY_NAME_COL, rpt.getString("policyName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_SD_NO_COL, rpt.getString("sdNo"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_ID_COL, rpt.getString("storeId"));
		// 添加新终端编码栏位 2014-02-13 mandy add
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_MDM_STORE_ID_COL, rpt.getString("MDM_STORE_ID"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_NAME_COL, rpt.getString("storeName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_ACREAGE_COL, "" + rpt.getDouble("storeAcreage"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_PHONE_COL, rpt.getString("storePhone"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_ADDRESS_COL, rpt.getString("address"));// 终端地址
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_OWNER_COL, rpt.getString("storeOwner"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_DISPlAY_ACTUAL_CHECKER_COL, rpt.getString("displayActualChecker"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_DISPLAY_SID_COL, rpt.getString("storeDisplaySid"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_LOCATION_TYPE_NAME_COL, rpt.getString("locationTypeName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_DISPLAY_TYPE_NAME_COL, rpt.getString("displayTypeName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ADDITIONAL_OPTIONS_COL, rpt.getString("fjo"));// 附加选项
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_NOT_REACH_COL, rpt.getString("SUBSTANDARD_NAME"));// 不达标
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ASSERT_ID_COL, rpt.getString("assertId"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_FIRST_VIST_DATE_COL, Toolkit.dateToString(rpt.getDate("VISIT_DATE"), "yyyy-MM-dd HH:mm:ss.SSS"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_ACTUAL_FILL_STATUS_COL, rpt.getString("storeActualFillStatus"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_FILL_DATE_YD_COL, Toolkit.dateToString(rpt.getDate("fillDateYd"),"yyyy-MM-dd HH:mm:ss"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ACCOUNT_YD_COL, rpt.getString("accountYd"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_USER_NAME_YD_COL, rpt.getString("userNameYd"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_FILL_DATE_ZR_COL, Toolkit.dateToString(rpt.getDate("fillDateZr"),"yyyy-MM-dd HH:mm:ss"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ACCOUNT_ZR_COL, rpt.getString("accountZr"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_USER_NAME_ZR_COL, rpt.getString("userNameZr"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ACTUAL_COST_COL, "" + rpt.getDouble("actualCost"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ACTUAL_COST_COMPANY_COL, "" + rpt.getDouble("COMPANY_PAYMENT"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ACTUAL_COST_CUSTOMER_COL, "" + rpt.getDouble("CUSTOMER_PAYMENT"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_ACTUAL_SALES_COL, "" + rpt.getDouble("actualSales"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_FILL_DATE_KH_COL, Toolkit.dateToString(rpt.getDate("fillDateKh"),"yyyy-MM-dd HH:mm:ss"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_IS_RECEIVED_AGREEMENT_COL, rpt.getString("isReceiveAgreement"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_DATE_ZR_COL, Toolkit.dateToString(rpt.getDate("auditActualDateZr"), "yyyy-MM-dd HH:mm:ss"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_STATUS_ZR_COL, rpt.getString("auditActualStatusZr"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_DATE_SZ_COL, Toolkit.dateToString(rpt.getDate("auditActualDateSz"), "yyyy-MM-dd HH:mm:ss"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_STATUS_SZ_COL, rpt.getString("auditActualStatusSz"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_APPROVED_AMOUNT_SZ_COL, "" + rpt.getDouble("approvedAmountSz"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_DATE_ZJ_COL, Toolkit.dateToString(rpt.getDate("auditActualDateZj"), "yyyy-MM-dd HH:mm:ss"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_STATUS_ZJ_COL, rpt.getString("auditActualStatusZj"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_APPROVED_AMOUNT_ZJ_COL, "" + rpt.getDouble("approvedAmountZj"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_TYPE_COL, rpt.getString("storeTypeId"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_BASIC_WITHDRAW_AMOUNT_COL, "" + rpt.getDouble("withdrawAmount"));
	}

	/**
	 * <pre>
	 * 2013-6-20 Nash
	 * 特陈实际-品项档报表 表头
	 * </pre>
	 * 
	 * <ol>
	 * <li>
	 * <li>
	 * </ol>
	 * 
	 * @param sheet
	 * @param headStyle
	 */
	private void createProdHeadRow(HSSFSheet sheet, HSSFCellStyle headStyle) {

		headStyle.setFillPattern(HSSFCellStyle.ALIGN_LEFT);
		headStyle.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);
		HSSFRow rowHeader = sheet.createRow(0);

		HSSFCell divisionCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_DIVISION_COL);
		divisionCell.setCellStyle(headStyle);
		divisionCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		divisionCell.setCellValue("事业部");

		HSSFCell companyCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_COMPANY_COL);
		companyCell.setCellStyle(headStyle);
		companyCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		companyCell.setCellValue("分公司");

		HSSFCell branchCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_BRANCH_COL);
		branchCell.setCellStyle(headStyle);
		branchCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		branchCell.setCellValue("营业所");

		HSSFCell custIdCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_CUST_ID_COL);
		custIdCell.setCellStyle(headStyle);
		custIdCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		custIdCell.setCellValue("客户编号");

		HSSFCell custNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_CUST_NAME_COL);
		custNameCell.setCellStyle(headStyle);
		custNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		custNameCell.setCellValue("客户名称");
		
		HSSFCell amountPerMonthCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_AMOUNT_PER_MONTH_COL);
		amountPerMonthCell.setCellStyle(headStyle);
		amountPerMonthCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		amountPerMonthCell.setCellValue("月度标准投入费用");

		HSSFCell yearMonthCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_YEAR_MONTH_COL);
		yearMonthCell.setCellStyle(headStyle);
		yearMonthCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		yearMonthCell.setCellValue("特陈年月");

		HSSFCell policyNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_POLICY_NAME_COL);
		policyNameCell.setCellStyle(headStyle);
		policyNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		policyNameCell.setCellValue("特陈政策名称");

		HSSFCell sdNoCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_SD_NO_COL);
		sdNoCell.setCellStyle(headStyle);
		sdNoCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		sdNoCell.setCellValue("单据编码");

		HSSFCell storeDisplaySidCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_STORE_DISPLAY_SID_COL);
		storeDisplaySidCell.setCellStyle(headStyle);
		storeDisplaySidCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		storeDisplaySidCell.setCellValue("终端店编码");

		HSSFCell storeNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_STORE_NAME_COL);
		storeNameCell.setCellStyle(headStyle);
		storeNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		storeNameCell.setCellValue("终端店名称");

		HSSFCell storeAcreageCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_STORE_ACREAGE_COL);
		storeAcreageCell.setCellStyle(headStyle);
		storeAcreageCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		storeAcreageCell.setCellValue("终端店面积");

		HSSFCell actualCheckerCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_DISPlAY_ACTUAL_CHECKER_COL);
		actualCheckerCell.setCellStyle(headStyle);
		actualCheckerCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		actualCheckerCell.setCellValue("特陈实际检核人");

		HSSFCell storeIdCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_STORE_ID_COL);
		storeIdCell.setCellStyle(headStyle);
		storeIdCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		storeIdCell.setCellValue("流水码");

		HSSFCell locationTypeNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_LOCATION_TYPE_NAME_COL);
		locationTypeNameCell.setCellStyle(headStyle);
		locationTypeNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		locationTypeNameCell.setCellValue("特陈位置");

		HSSFCell displayTypeNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_DISPLAY_TYPE_NAME_COL);
		displayTypeNameCell.setCellStyle(headStyle);
		displayTypeNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		displayTypeNameCell.setCellValue("特陈形式");

		HSSFCell additionalOptionsCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_ADDITIONAL_OPTIONS_COL);
		additionalOptionsCell.setCellStyle(headStyle);
		additionalOptionsCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		additionalOptionsCell.setCellValue("附加选项");

		HSSFCell assertIdCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_ASSERT_ID_COL);
		assertIdCell.setCellStyle(headStyle);
		assertIdCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		assertIdCell.setCellValue("财产编号");

		HSSFCell groupNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_PROD_GROUP_NAME_COL);
		groupNameCell.setCellStyle(headStyle);
		groupNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		groupNameCell.setCellValue("线别");

		HSSFCell prodIdCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_PROD_ID_COL);
		prodIdCell.setCellStyle(headStyle);
		prodIdCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		prodIdCell.setCellValue("品项ID");

		HSSFCell prodNameCell = rowHeader.createCell(Constant.SPECIAL_DISPLAY_ACTUAL_PROD_PROD_NAME_COL);
		prodNameCell.setCellStyle(headStyle);
		prodNameCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		prodNameCell.setCellValue("品项名称");
	}

	/**
	 * <pre>
	 * 2013-6-20 Nash
	 * 特陈实际-品项档报表 row
	 * </pre>
	 * 
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param rpt
	 * @throws SQLException 
	 */
	private void createProdRow(HSSFSheet sheet, int rowNum, ResultSet rpt) throws SQLException {

		HSSFRow row = sheet.createRow(rowNum);

		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_DIVISION_COL, rpt.getString("divisionName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_COMPANY_COL, rpt.getString("companyName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_BRANCH_COL, rpt.getString("branchName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_CUST_ID_COL, rpt.getString("custId"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_CUST_NAME_COL, rpt.getString("custName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_AMOUNT_PER_MONTH_COL, "" + rpt.getDouble("amountPerMonth"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_YEAR_MONTH_COL, rpt.getString("yearMonth"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_POLICY_NAME_COL, rpt.getString("policyName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_SD_NO_COL, rpt.getString("sdNo"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_STORE_DISPLAY_SID_COL, rpt.getString("storeDisplaySid"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_STORE_NAME_COL, rpt.getString("storeName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_STORE_ACREAGE_COL, "" + rpt.getDouble("storeAcreage"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_DISPlAY_ACTUAL_CHECKER_COL, rpt.getString("displayActualChecker"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_STORE_ID_COL, rpt.getString("storeId"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_LOCATION_TYPE_NAME_COL, rpt.getString("locationTypeName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_DISPLAY_TYPE_NAME_COL, rpt.getString("displayTypeName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_ADDITIONAL_OPTIONS_COL, rpt.getString("fjo"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_ASSERT_ID_COL, rpt.getString("assertId"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_PROD_GROUP_NAME_COL, rpt.getString("prodGroupName"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_PROD_ID_COL, rpt.getString("prodId"));
		createACell(row, Constant.SPECIAL_DISPLAY_ACTUAL_PROD_PROD_NAME_COL, rpt.getString("prodName"));
	}
}
