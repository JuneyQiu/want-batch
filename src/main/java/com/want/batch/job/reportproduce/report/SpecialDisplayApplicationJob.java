/**
 * 
 */
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
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.want.batch.job.reportproduce.dao.DirectiveTblDao;
import com.want.batch.job.reportproduce.dao.SpecDisplayApplicationDao;
import com.want.batch.job.reportproduce.pojo.ApplicationCheckLog;
import com.want.batch.job.reportproduce.pojo.Constant;
import com.want.batch.job.reportproduce.util.CommonUtils;

/**
 * @author MandyZhang
 *
 */
@Component
public class SpecialDisplayApplicationJob {

	protected final Log logger = LogFactory.getLog(SpecialDisplayApplicationJob.class);
	
	private int linesPerPage = 64000;
			
	@Autowired
	private SpecDisplayApplicationDao specDisplayApplicationDao;
	
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
			
			logger.info("特陈计划报表下载，更新db状态为running");
			
			// 排程执行时，更新DIRECTIVE_TBL表中的栏位STATUS
			commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_RUNNING, Integer.parseInt(directiveTblMap.get("SID").toString()), null);
			
			logger.info("特陈计划报表下载" + directiveTblMap.get("SID") + "开始时间：" + new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date()));
			
			// 产生excel
			toExcel(reportType, directiveTblMap);
			
			logger.info("特陈计划报表下载 " + directiveTblMap.get("SID") + "完成结束时间：" + new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date()));
			
			logger.info("特陈计划报表下载，更新db状态为finish");
			commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_FINISH, Integer.parseInt(directiveTblMap.get("SID").toString()), null);
			
			try {
				
				commonUtils.sendMail(directiveTblDao.getDirectiveBySid(directiveTblMap.get("SID").toString()));
				
				logger.info("发送mail结束");
			}
			catch (Exception e) {

				logger.info("发送mail出现异常");
				logger.error(Constant.generateExceptionMessage(e));
			}
		} 
		catch (Exception e) {
			
			result = false;
			
			// 此处已是线程的最后一层，发生异常后MonitorJob无法捕获，所以在此处将异常信息记录进log文件中
			logger.error(Constant.generateExceptionMessage(e));

			try {				
				commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_EXCEPTION, Integer.parseInt(directiveTblMap.get("SID").toString()), Constant.generateExceptionMessage(e));
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
	 * 生成excel文件
	 * 
	 * @param puttype
	 * @param directiveTblMap
	 * @throws Exception
	 */
	private void toExcel(String puttype, Map<String, Object> directiveTblMap) throws Exception {
		
		Connection conn = null;
		ResultSet specDisplayApplicationRpts = null;
		
		try {
			
			logger.info("取得数据源。。。");
			conn = iCustomerDataSource.getConnection();
			
			logger.info("特陈计划报表，开始执行查询。。。。" + new Date());
			
			specDisplayApplicationRpts = 
				specDisplayApplicationDao.getSpecDispalyApplicationRpt(conn, directiveTblMap);
			
			logger.info("特陈计划报表，结束查询。。。。" + new Date());
			
			// 文件
			File file = new File((null == directiveTblMap.get("ROOT_PATH") ? null : directiveTblMap.get("ROOT_PATH").toString())
					+ (null == directiveTblMap.get("FILE_NAME") ? null : directiveTblMap.get("FILE_NAME").toString())
					+ ".xls");
			
			logger.info("特陈计划报表文件：" + directiveTblMap.get("ROOT_PATH").toString() + directiveTblMap.get("FILE_NAME").toString() + ".xls");
			
			// 创建文件
			if (!file.exists()) {

				file.createNewFile();
			}
			
			FileOutputStream out = new FileOutputStream(file);
			
			logger.info("特陈计划报表，开始创建excel文件");
			
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = null;
			HSSFCellStyle cellStyle = wb.createCellStyle();
			
			HSSFFont f = wb.createFont();
			cellStyle.setFont(f);
			cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
			
			int i = 1;
			
			// 判断是否是sheet的最后一行
			int lastRowNum =0;
			
			// 若有资料，则循环添加资料
			while (specDisplayApplicationRpts.next()) {
				
				// 当为第一笔资料时，创建表头;否则，达到每个sheet的最大笔数时，新建一个sheet
				if ((specDisplayApplicationRpts.getRow() == 1) || (sheet != null && sheet.getLastRowNum() >= linesPerPage)) {

					sheet = wb.createSheet(String.valueOf(i));
					logger.info(puttype + ">>>>>>>>>>>>>>>>>>创建excel行表头");
					createHeadRow(puttype, sheet, cellStyle);
					i++;
				}
				
				lastRowNum = sheet.getLastRowNum();
				
				// 以下是添加明细资料
				createDetailRow(puttype, sheet, lastRowNum + 1, specDisplayApplicationRpts, cellStyle);
			}
			
			// 当没有资料时给报表添加标题
			if (sheet == null) {
				
				sheet = wb.createSheet(String.valueOf(i));
				createHeadRow(puttype, sheet, cellStyle);
			}
			
			if (i == 1 && sheet.getLastRowNum() == 0) {
				
				logger.info(puttype + "资料总笔数：0");
			}
			else {
			
				logger.info(puttype + "资料总笔数：" + (linesPerPage*(i-2)+(sheet.getLastRowNum() + 1 )-(i-1)));
			}
			
			wb.write(out);

			out.flush();
			out.close();
			
			logger.info("特陈计划报表，结束创建excel文件");
		} 
		// 无论异常有无发生，此处都需要关闭结果集和数据库连接
		finally {

			logger.info(puttype + "执行完毕，关闭数据库连接");
			if (specDisplayApplicationRpts != null) {
			
				specDisplayApplicationRpts.close();
			}
			
			if (conn != null) {
				
				conn.close();
			}
		}
	}
	
	public void createHeadRow(String puttype, HSSFSheet sheet, HSSFCellStyle cellStyle) {
		
		HSSFRow headerRow = sheet.createRow(0);
		
		if(puttype.equals("1")){//审核档
			
		   createStringCell(headerRow, (short) 0, "事业部", cellStyle);
		   createStringCell(headerRow, (short) 1, "分公司", cellStyle);
		   createStringCell(headerRow, (short) 2, "营业所", cellStyle);
		   createStringCell(headerRow, (short) 3, "客户编号", cellStyle);
		   createStringCell(headerRow, (short) 4, "客户名称", cellStyle);
		   createStringCell(headerRow, (short) 5, "特陈政策名称", cellStyle);
		   createStringCell(headerRow, (short) 6, "特陈月份", cellStyle);
		   createStringCell(headerRow, (short) 7, "单据编码", cellStyle);
		   createStringCell(headerRow, (short) 8, "计划单据状态", cellStyle);
		   createStringCell(headerRow, (short) 9, "申请日期", cellStyle);
		}
		else if(puttype.equals("2")) {//基础档
		
			createStringCell(headerRow, (short) 0, "分公司", cellStyle);
			createStringCell(headerRow, (short) 1, "营业所", cellStyle);
			createStringCell(headerRow, (short) 2, "客户编号", cellStyle);
			createStringCell(headerRow, (short) 3, "客户名称", cellStyle);
			createStringCell(headerRow, (short) 4, "三级地市", cellStyle);
			createStringCell(headerRow, (short) 5, "月度标准投入费用", cellStyle);
			createStringCell(headerRow, (short) 6, "本月可投放数量", cellStyle);
			createStringCell(headerRow, (short) 7, "单据编码", cellStyle);
			createStringCell(headerRow, (short) 8, "终端编号", cellStyle);
			// 添加新终端编码栏位 2014-02-13 mandy add
			createStringCell(headerRow, (short) 9, "新终端编码", cellStyle);
			createStringCell(headerRow, (short)10, "终端名称", cellStyle);
			createStringCell(headerRow, (short)11, "终端面积", cellStyle);
			createStringCell(headerRow, (short)12, "计划销量", cellStyle);
			createStringCell(headerRow, (short)13, "计划费用", cellStyle);
			createStringCell(headerRow, (short)14, "特陈政策名称", cellStyle);
			createStringCell(headerRow, (short)15, "特陈位置", cellStyle);
			createStringCell(headerRow, (short)16, "流水码", cellStyle);
			createStringCell(headerRow, (short)17, "特陈形式", cellStyle);
			createStringCell(headerRow, (short)18, "附加选项", cellStyle);
		}
		else if(puttype.equals("3")){//品项档
		
			createStringCell(headerRow, (short) 0, "事业部", cellStyle);
		   createStringCell(headerRow, (short) 1, "分公司", cellStyle);
		   createStringCell(headerRow, (short) 2, "营业所", cellStyle);
		   createStringCell(headerRow, (short) 3, "客户编号", cellStyle);
		   createStringCell(headerRow, (short) 4, "客户名称", cellStyle);
		   createStringCell(headerRow, (short) 5, "三级地市", cellStyle);
		   createStringCell(headerRow, (short) 6, "月度标准投入费用", cellStyle);
		   createStringCell(headerRow, (short) 7, "单据编码", cellStyle);
		   createStringCell(headerRow, (short) 8, "终端编号", cellStyle);
		   createStringCell(headerRow, (short) 9, "终端名称", cellStyle);
		   createStringCell(headerRow, (short)10, "计划销量", cellStyle);
		   createStringCell(headerRow, (short)11, "计划费用", cellStyle);
		   createStringCell(headerRow, (short)12, "特陈政策名称", cellStyle);
		   createStringCell(headerRow, (short)13, "特陈位置", cellStyle);
		   createStringCell(headerRow, (short)14, "特陈形式", cellStyle);
		   createStringCell(headerRow, (short)15, "附加选项", cellStyle);
		   createStringCell(headerRow, (short)16, "线别", cellStyle);
		   createStringCell(headerRow, (short)17, "品项ID", cellStyle);
		   createStringCell(headerRow, (short)18, "品项名称", cellStyle);
		}
		else{//未填写档
			
			createStringCell(headerRow, (short) 0, "事业部", cellStyle);
		   createStringCell(headerRow, (short) 1, "分公司", cellStyle);
		   createStringCell(headerRow, (short) 2, "营业所", cellStyle);
		   createStringCell(headerRow, (short) 3, "客户编号", cellStyle);
		   createStringCell(headerRow, (short) 4, "客户名称", cellStyle);
		   createStringCell(headerRow, (short) 5, "计划单据状态", cellStyle);
		}
	}
	
	public void createDetailRow(String puttype, HSSFSheet sheet, int lastRowNum, ResultSet specDisplayApplicationRpts, HSSFCellStyle cellStyle) throws SQLException {
		
		HSSFRow row = sheet.createRow(lastRowNum);
		
		if(puttype.equals("1")){//审核档
			
			createStringCell(row, (short) 0, specDisplayApplicationRpts.getString("DIVSION"), cellStyle);
		   createStringCell(row, (short) 1, specDisplayApplicationRpts.getString("COMPANY_NAME"), cellStyle);
		   createStringCell(row, (short) 2, specDisplayApplicationRpts.getString("BRANCH_NAME"), cellStyle);
		   createStringCell(row, (short) 3, specDisplayApplicationRpts.getString("id"), cellStyle);
		   createStringCell(row, (short) 4, specDisplayApplicationRpts.getString("NAME"), cellStyle);
		   createStringCell(row, (short) 5, specDisplayApplicationRpts.getString("POLICY_NAME"), cellStyle);
		   createStringCell(row, (short) 6, specDisplayApplicationRpts.getString("YEAR_MONTH"), cellStyle);
		   createStringCell(row, (short) 7, specDisplayApplicationRpts.getString("SD_NO"), cellStyle);
		   createStringCell(row, (short) 8, getStatusNameBySid(specDisplayApplicationRpts.getString("CHECK_STATUS"),specDisplayApplicationRpts.getString("CHECK_USER_TYPE")), cellStyle);
		   createStringCell(row, (short) 9, specDisplayApplicationRpts.getString("CREATE_DATE"), cellStyle);
		}
		else if(puttype.equals("2")) {//基础档
			
			createStringCell(row, (short) 0, specDisplayApplicationRpts.getString("COMPANY_NAME"), cellStyle);
			createStringCell(row, (short) 1, specDisplayApplicationRpts.getString("BRANCH_NAME"), cellStyle);
			createStringCell(row, (short) 2, specDisplayApplicationRpts.getString("id"), cellStyle);
			createStringCell(row, (short) 3, specDisplayApplicationRpts.getString("NAME"), cellStyle);
			createStringCell(row, (short) 4, specDisplayApplicationRpts.getString("SUBCITY_NAME"), cellStyle);
			createStringCell(row, (short) 5, specDisplayApplicationRpts.getString("AMOUNT"), cellStyle);
			createStringCell(row, (short) 6, specDisplayApplicationRpts.getString("QUANTITY"), cellStyle);
			createStringCell(row, (short) 7, specDisplayApplicationRpts.getString("SD_NO"), cellStyle);
			createStringCell(row, (short) 8, specDisplayApplicationRpts.getString("STORE_ID"), cellStyle);
			// 添加新终端编码栏位 2014-02-13 mandy add
			createStringCell(row, (short) 9, specDisplayApplicationRpts.getString("MDM_STORE_ID"), cellStyle);
			createStringCell(row, (short)10, specDisplayApplicationRpts.getString("STORE_NAME"), cellStyle);
			createStringCell(row, (short)11, specDisplayApplicationRpts.getString("STORE_ACREAGE"), cellStyle);
			createStringCell(row, (short)12, specDisplayApplicationRpts.getString("PLAN_SALES"), cellStyle);
			createStringCell(row, (short)13, specDisplayApplicationRpts.getString("PLAN_PAYMENT"), cellStyle);
			createStringCell(row, (short)14, specDisplayApplicationRpts.getString("POLICY_NAME"), cellStyle);
			createStringCell(row, (short)15, specDisplayApplicationRpts.getString("LOCATION_TYPE_NAME"), cellStyle);
			createStringCell(row, (short)16, specDisplayApplicationRpts.getString("SID"), cellStyle);
			createStringCell(row, (short)17, specDisplayApplicationRpts.getString("DISPLAY_TYPE_NAME"), cellStyle);
			createStringCell(row, (short)18, specDisplayApplicationRpts.getString("fjo"), cellStyle);
		}
		else if(puttype.equals("3")){//品项档
		
			createStringCell(row, (short) 0, specDisplayApplicationRpts.getString("DIVSION"), cellStyle);
			createStringCell(row, (short) 1, specDisplayApplicationRpts.getString("COMPANY_NAME"), cellStyle);
			createStringCell(row, (short) 2, specDisplayApplicationRpts.getString("BRANCH_NAME"), cellStyle);
			createStringCell(row, (short) 3, specDisplayApplicationRpts.getString("id"), cellStyle);
			createStringCell(row, (short) 4, specDisplayApplicationRpts.getString("NAME"), cellStyle);
			createStringCell(row, (short) 5, specDisplayApplicationRpts.getString("SUBCITY_NAME"), cellStyle);
			createStringCell(row, (short) 6, specDisplayApplicationRpts.getString("AMOUNT"), cellStyle);
			createStringCell(row, (short) 7, specDisplayApplicationRpts.getString("SD_NO"), cellStyle);
			createStringCell(row, (short) 8, specDisplayApplicationRpts.getString("STORE_ID"), cellStyle);
			createStringCell(row, (short) 9, specDisplayApplicationRpts.getString("STORE_NAME"), cellStyle);
			createStringCell(row, (short)10, specDisplayApplicationRpts.getString("PLAN_SALES"), cellStyle);
			createStringCell(row, (short)11, specDisplayApplicationRpts.getString("PLAN_PAYMENT"), cellStyle);
			createStringCell(row, (short)12, specDisplayApplicationRpts.getString("POLICY_NAME"), cellStyle);
			createStringCell(row, (short)13, specDisplayApplicationRpts.getString("LOCATION_TYPE_NAME"), cellStyle);
			createStringCell(row, (short)14, specDisplayApplicationRpts.getString("DISPLAY_TYPE_NAME"), cellStyle);
			createStringCell(row, (short)15, specDisplayApplicationRpts.getString("fjo"), cellStyle);
			createStringCell(row, (short)16, specDisplayApplicationRpts.getString("PROD_NAME"), cellStyle);
			createStringCell(row, (short)17, specDisplayApplicationRpts.getString("PROD_ID"), cellStyle);
			createStringCell(row, (short)18, specDisplayApplicationRpts.getString("NAME1"), cellStyle);
		}
		else{//未填写档
			
			createStringCell(row, (short) 0, specDisplayApplicationRpts.getString("DIVSION"), cellStyle);
			createStringCell(row, (short) 1, specDisplayApplicationRpts.getString("COMPANY_NAME"), cellStyle);
			createStringCell(row, (short) 2, specDisplayApplicationRpts.getString("BRANCH_NAME"), cellStyle);
			createStringCell(row, (short) 3, specDisplayApplicationRpts.getString("id"), cellStyle);
			createStringCell(row, (short) 4, specDisplayApplicationRpts.getString("NAME"), cellStyle);
			createStringCell(row, (short) 5, "客户未填写", cellStyle);
		}
	}
	
	private static void createStringCell(HSSFRow row, short index,
			String value, HSSFCellStyle cs) {
		HSSFCell cell = row.createCell(index);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(value);

	}
	
	public String getStatusNameBySid(String CHECK_STATUS, String CHECK_USER_TYPE) {
		String checkStatus = "";
		String checkUser = "客户未提交";
		checkStatus = CHECK_STATUS == null ? checkStatus : CHECK_STATUS;
		if (ApplicationCheckLog.CHECK_STATUS_DISAGREE.equals(checkStatus)) {
			checkStatus = "驳回";
		} else if (ApplicationCheckLog.CHECK_STATUS_AGREE.equals(checkStatus)) {
			checkStatus = "核准";
		} else if (ApplicationCheckLog.CHECK_STATUS_SENDTOSZ
				.equals(checkStatus)) {
			checkStatus = "呈所长";
		}
		// Ryan加入呈总监 && 客户提交状态
		else if (ApplicationCheckLog.CHECK_STATUS_SENDTOZJ.equals(checkStatus)) {
			checkStatus = "呈总监";
		} else if (ApplicationCheckLog.CHECK_STATUS_SENDTOKH
				.equals(checkStatus)) {
			checkStatus = "已提交";
		}
		// 审核人员处理
		// checkUser = rs.getString("CHECK_USER_TYPE");
		checkUser = CHECK_USER_TYPE == null ? checkUser : CHECK_USER_TYPE;
		if (ApplicationCheckLog.CHECK_USER_XG.equals(checkUser)) {
			checkUser = "销管";
		} else if (ApplicationCheckLog.CHECK_USER_ZR.equals(checkUser)) {
			checkUser = "主任";
		} else if (ApplicationCheckLog.CHECK_USER_SZ.equals(checkUser)) {
			checkUser = "所长";
		} else if (ApplicationCheckLog.CHECK_USER_ZJ.equals(checkUser)) {
			checkUser = "总监";
		}
		// Ryan加入人员类型 客户
		else if (ApplicationCheckLog.CHECK_USER_KH.equals(checkUser)) {
			checkUser = "客户";
		}
		return checkUser + checkStatus;
	}
}
