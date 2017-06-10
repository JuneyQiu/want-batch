/**
 * 
 */
package com.want.batch.job.reportproduce.report;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.want.batch.job.reportproduce.dao.DirectiveTblDao;
import com.want.batch.job.reportproduce.dao.DivsionDao;
import com.want.batch.job.reportproduce.dao.SdActualNotcheckReportDao;
import com.want.batch.job.reportproduce.pojo.Constant;
import com.want.batch.job.reportproduce.util.CommonUtils;

/**
 * @author MandyZhang
 *
 */
@Component
public class TchUncheckedReportJob {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	/**
	 * Lucien, 2010-7-26 <br> 
	 * 政策定点
	 */
	public static final String SPECIAL_TYPE_LOCATION = "2";
	
	/**
	 * Lucien, 2010-7-26 <br> 
	 * 政策定额
	 */
	public static final String SPECIAL_TYPE_PRICE = "1";
	
	@Autowired
	public CommonUtils commonUtils;
	
	@Autowired
	public SdActualNotcheckReportDao sdActualNotcheckReportDao;
	
	@Autowired
	public DivsionDao divsionDao;
	
	@Autowired
	public DirectiveTblDao directiveTblDao;
	
	@Autowired
	public DataSource iCustomerDataSource;
	
	@Async
	public Future<Boolean> run(Map<String, Object> directiveTblMap)  {
		
		boolean result = true;
		
		try {
			
			Connection conn = null;
			ResultSet sdActualNotchecks = null;
			
			logger.info("取得数据源。。。");
			conn = iCustomerDataSource.getConnection();
			
			commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_RUNNING, Integer.parseInt(directiveTblMap.get("SID").toString()), null);
			logger.info("特陈实际未检核明细表，更新db状态为running");
			
			logger.info("特陈实际未检核明细表开始执行查询。。。。" + new Date());
			
			sdActualNotchecks = this.sdActualNotcheckReportDao.getTchNotcheck(directiveTblMap, conn);
			
			logger.info("特陈实际未检核明细表开始产生报表>>>>>>>>>>>>" + new Date());
			
			this.buildExcelDocument(sdActualNotchecks, directiveTblMap);
			
			logger.info("特陈实际未检核明细表开始产生报表结束>>>>>>>>>>>>" + new Date());
			
			commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_FINISH, Integer.parseInt(directiveTblMap.get("SID").toString()), null);
			
			try {
				
				commonUtils.sendMail(directiveTblDao.getDirectiveBySid(directiveTblMap.get("SID").toString()));
			}
			catch (Exception e) {

				logger.error(Constant.generateExceptionMessage(e));
			}
		} 
		catch (Exception e) {
			
			result = false;
			
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
	 * 产生报表
	 * @throws Exception 
	 */
	public void buildExcelDocument(ResultSet sdActualNotchecks, Map<String, Object> directiveTblMap) throws Exception {

		try {
			
			// 文件
			File file = new File((null == directiveTblMap.get("ROOT_PATH") ? null : directiveTblMap.get("ROOT_PATH").toString())
					+ (null == directiveTblMap.get("FILE_NAME") ? null : directiveTblMap.get("FILE_NAME").toString())
					+ ".xls");
			logger.info("特陈实际未检核明细表存放路径 ： " + directiveTblMap.get("ROOT_PATH").toString() + directiveTblMap.get("FILE_NAME").toString() + ".xls");
			// 创建文件
			if (!file.exists()) {

				file.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(file);
			
			String paramValue = (String) directiveTblMap.get("SELECT_PARAM_VALUE");
			
			String sDate = paramValue.split(";")[3];
			String eDate = paramValue.split(";")[4];
			
			String dateStart = StringUtils.replace(sDate, "-", "/");
			String dateEnd = StringUtils.replace(eDate, "-", "/");
			
			String dateStr = dateStart + "～" + dateEnd;
			
			// excell中一个sheet允许最大行数
			int maxNumber = 65530;
			
			// 列印主报表-start		
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFCellStyle style = workbook.createCellStyle();
			HSSFFont font = workbook.createFont();
			
			HSSFSheet sheet = null;
			String sheetName = "";
			
			// 创建样式  add mandy 2013-09-13
			HSSFCellStyle cellStyle = this.getTitleStyleWrap(style, font, 10, "Y", HSSFCellStyle.ALIGN_LEFT);
			
			// 判断是第几个sheet
			int number =0;
			
			// 判断是否是sheet的最后一行
			int lastRowNum =0;
			
			// 若有资料，则循环添加资料
			while (sdActualNotchecks.next()) {
				
				// 当为第一笔资料时，创建表头;否则，达到每个sheet的最大笔数时，新建一个sheet
				if ((sdActualNotchecks.getRow() == 1) || (sheet != null && sheet.getLastRowNum() >= maxNumber)) {
					
					Map<String, Object> resultMap = this.bulidSheetTitle(workbook,  sheet, sheetName,  number,  font, dateStr, cellStyle);
					
					sheet = (HSSFSheet)resultMap.get("sheet");
					
					number ++;
				}
				
				lastRowNum = sheet.getLastRowNum();
				
				HSSFRow row = sheet.createRow(lastRowNum + 1);
				
				// wonci 2011-04-01 由静态的改为动态的自动生成变量
				int b = 0; 
				
				// 事业部  wonci 2011-03-01 修改事业部名称的取值由以前写死的改成动态取值
				this.createCell((short)b++, row, this.divsionDao.findById(sdActualNotchecks.getInt("DIVISION_SID")), 
					cellStyle );
					
				// 分公司
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("COMPANY_NAME")), 
					cellStyle );
				
				// 营业所
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("BRANCH_NAME")), 
					cellStyle );
				
				// 三级地区
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("THIRD_LV_NAME")), 
					cellStyle );
				
				// 客户编号
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("CUSTOMER_ID")), 
					cellStyle );
				
				// 客户名称
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("NAME")), 
					cellStyle );
				
				// 单据号
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("SD_NO")), 
					cellStyle );
				
				// 政策名称 wonci 2011-04-01 add
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("POLICY_NAME")), 
					cellStyle );
				
				// 特陈类型  wonci 2011-04-01 add
				this.createCell((short)b++, row, (SPECIAL_TYPE_PRICE.equals(this.getStringValue(sdActualNotchecks.getString("TYPE")))? "定额" : "定点"), 
					cellStyle );
				
				// 业代编码
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("EMP_ID")), 
					cellStyle );
				
				// 业代姓名
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("EMP_NAME")), 
					cellStyle );
				
				// 业代状态
				this.createCell((short)b++, row, this.getStatus(sdActualNotchecks.getString("EMP_STATUS")), 
					cellStyle );
				
				// 终端ID
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("STORE_ID")), 
					cellStyle );
				
				// 终端名称
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("STORE_NAME")), 
					cellStyle );
				
				// 填写状况
				this.createCell((short)b++, row, (sdActualNotchecks.getString("IS_LOCK") == null || sdActualNotchecks.getString("IS_LOCK") == "" ? "未填写" : "已填写"), 
					cellStyle );
				
				// 特陈位置
				this.createCell((short)b++, row, this.getLocation(sdActualNotchecks.getObject("LOCATION_TYPE_SID")), 
					cellStyle );
				
				// 陈列形式
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("DISPLAY_TYPE_NAME")), 
					cellStyle );
				
				// 陈列面积
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("DISPLAY_ACREAGE")), 
					cellStyle );
				
				// 拜访次数
				this.createCell((short)b++, row, this.getStringValue(sdActualNotchecks.getString("VISIT_COUNT")), 
					cellStyle );
			}
			
			// 当没有资料时给报表添加标题
			if (sheet == null) {
				
				this.bulidSheetTitle(workbook,  sheet, sheetName,  number,  font, dateStr, cellStyle);
			}
			
			workbook.write(out);

			out.flush();
			out.close();
		} 
		catch (Exception e) {
			
			logger.info("创建特陈实际未检核明细表报表excel文件过程中发生异常");
			throw e;
		}
	}
	
	/**
	 * <pre>
	 * 2011-6-23 wonci
	 * 	Excell中的sheet表头
	 * </pre>
	 * 
	 * @param workbook
	 * @param sheet
	 * @param sheetName
	 * @param number
	 * @param helper
	 * @return
	 */
	public Map<String, Object>  bulidSheetTitle(HSSFWorkbook workbook, 	
	                            HSSFSheet sheet, 	
	                            String sheetName, 
	                            int number,
	                            HSSFFont font,
	                            String dateStr,
	                            HSSFCellStyle cellStyle) {
		
		// sheet Name 
		sheetName = "第"+(number+1)+"页";
		
		// 新建sheet
		sheet = workbook.createSheet(sheetName);
		workbook.setSheetName(number, sheetName, HSSFWorkbook.ENCODING_UTF_16);
		
	  // 设置默认打印为横列
		sheet.getPrintSetup().setLandscape(true); 
		
		// 设置页眉页脚边距为 0
		sheet.getPrintSetup().setHeaderMargin(0);
		sheet.getPrintSetup().setFooterMargin(0);		
		
		// 设置页边距
		sheet.setMargin(HSSFSheet.TopMargin, 0.36);
		sheet.setMargin(HSSFSheet.BottomMargin, 0.36);
		sheet.setMargin(HSSFSheet.LeftMargin, 0.1);
		sheet.setMargin(HSSFSheet.RightMargin, 0.1);
		
		// wonci 2011-04-01 由静态的改为动态的自动生成变量。
		int i = 0;
		
		// 事业部
		sheet.setColumnWidth((short) i++, (short) 2500);
		
		// 分公司名称
		sheet.setColumnWidth((short) i++, (short) 8000);
		
		// 营业所名称
		sheet.setColumnWidth((short) i++, (short) 3000);
		
		// 三级地区
		sheet.setColumnWidth((short) i++, (short) 3000);
		
		// 客户编号
		sheet.setColumnWidth((short) i++, (short) 3000);
		
		// 客户名称
		sheet.setColumnWidth((short) i++, (short) 8000);
		
		// 单据号
		sheet.setColumnWidth((short) i++, (short) 4800);
		
		// 政策名称 wonci 2011-04-01 add
		sheet.setColumnWidth((short) i++, (short) 8000);
		
		// 特陈类型 wonci 2011-04-01 add
		sheet.setColumnWidth((short) i++, (short) 2500);
		
		// 业代编码
		sheet.setColumnWidth((short) i++, (short) 2500);
		
		// 业代姓名
		sheet.setColumnWidth((short) i++, (short) 2500);
		
		// 业代状态
		sheet.setColumnWidth((short) i++, (short) 2500);
		
		// 终端ID
		sheet.setColumnWidth((short) i++, (short) 3000);
		
		// 终端名称
		sheet.setColumnWidth((short) i++, (short) 4000);
		
		// 填写状况
		sheet.setColumnWidth((short) i++, (short) 2500);
		
		// 特陈位置
		sheet.setColumnWidth((short) i++, (short) 3000);
		
		// 陈列形式
		sheet.setColumnWidth((short) i++, (short) 2500);
		
		// 陈列面积
		sheet.setColumnWidth((short) i++, (short) 2500);
		
		// 拜访次数
		sheet.setColumnWidth((short) i++, (short) 2500);
		
		// 标题样式
		HSSFCellStyle titleCellStyle = this.getTitleStyle(workbook, 14, "", HSSFCellStyle.ALIGN_CENTER);
		
		// 设置标题
		HSSFRow rowTitle = sheet.createRow(0);
		this.createCell((short)4, rowTitle, "特陈实际未检核明细表_" + dateStr, 
				titleCellStyle );
		this.createCell((short)5, rowTitle, "", 
				titleCellStyle );
		this.createCell((short)6, rowTitle, "", 
				titleCellStyle );
		this.createCell((short)7, rowTitle, "", 
				titleCellStyle );
		this.createCell((short)8, rowTitle, "", 
				titleCellStyle );
		this.createCell((short)9, rowTitle, "", 
				titleCellStyle );
		
		HSSFRow rowTitle2 = sheet.createRow(1);
		this.createCell((short)4, rowTitle2, "", 
				titleCellStyle );
		this.createCell((short)5, rowTitle2, "", 
				titleCellStyle );
		this.createCell((short)6, rowTitle2, "", 
				titleCellStyle );
		this.createCell((short)7, rowTitle2, "", 
				titleCellStyle );
		this.createCell((short)8, rowTitle2, "", 
				titleCellStyle );
		this.createCell((short)9, rowTitle2, "", 
				titleCellStyle );
		
		sheet.addMergedRegion(new Region(0, (short)4, 1, (short)9));
		
		int rowNo = 3;
		
		HSSFRow rowTitle3 = sheet.createRow(rowNo);
		
		// wonci 2011-04-01 由静态的改为动态的自动生成变量
		int a = 0; 
		
		// 事业部
		this.createCell((short)a++, rowTitle3, "事业部", 
			cellStyle );
		
		// 分公司
		this.createCell((short)a++, rowTitle3, "分公司", 
			cellStyle );
		
		// 营业所
		this.createCell((short)a++, rowTitle3, "营业所", 
			cellStyle );
		
		// 三级地区
		this.createCell((short)a++, rowTitle3, "三级地区", 
			cellStyle );
		
		// 客户编号
		this.createCell((short)a++, rowTitle3, "客户编号", 
			cellStyle );
		
		// 客户名称
		this.createCell((short)a++, rowTitle3, "客户名称", 
			cellStyle );
		
		// 单据号
		this.createCell((short)a++, rowTitle3, "单据号", 
			cellStyle );
		
		// 政策名称 wonci 2011-04-01 add
		this.createCell((short)a++, rowTitle3, "政策名称", 
			cellStyle );
		
		// 特陈类型 wonci 2011-04-01 add
		this.createCell((short)a++, rowTitle3, "特陈类型", 
			cellStyle );
		
		// 业代编码
		this.createCell((short)a++, rowTitle3, "业代编码", 
			cellStyle );
		
		// 业代姓名
		this.createCell((short)a++, rowTitle3, "业代姓名", 
			cellStyle );
		
		// 业代状态
		this.createCell((short)a++, rowTitle3, "业代状态", 
			cellStyle );
		
		// 终端ID
		this.createCell((short)a++, rowTitle3, "终端ID", 
			cellStyle );
		
		// 终端名称
		this.createCell((short)a++, rowTitle3, "终端名称", 
			cellStyle );
		
		// 填写状况
		this.createCell((short)a++, rowTitle3, "填写状况", 
			cellStyle );
		
		// 特陈位置
		this.createCell((short)a++, rowTitle3, "特陈位置", 
			cellStyle );
		
		// 陈列形式
		this.createCell((short)a++, rowTitle3, "陈列形式", 
			cellStyle );
		
		// 陈列面积
		this.createCell((short)a++, rowTitle3, "陈列面积", 
			cellStyle );
		
		// 拜访次数
		this.createCell((short)a++, rowTitle3, "拜访次数", 
			cellStyle );
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sheet", sheet);
		map.put("rowNo", rowNo);
		
		return map;
	}
	
	/**
	 * <pre>
	 * 2010-3-4 A5KittyGuo
	 * 	在POI中设置样式: 显示样式, 字体大小, 格线是否加粗.
	 * </pre>
	 * 
	 * @param workBook
	 * @param fontSize
	 * @param strHaveBorder
	 * @param align
	 * @return
	 */
	public HSSFCellStyle getTitleStyle(HSSFWorkbook workBook, int fontSize, 
	                                   String strHaveBorder, short align) {

		HSSFCellStyle style = workBook.createCellStyle();

		style.setAlignment(align);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

		// 设定字体大小
		HSSFFont font = workBook.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) fontSize);
		style.setFont(font);

		if ("Y".equals(strHaveBorder)) {
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		}

		// 单元格线
		style.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
		style.setWrapText(false);

		return style;
	}
	
	/**
	 * <pre>
	 * 2010-3-4 A5KittyGuo
	 * 	在POI中设置样式: 显示样式, 字体大小, 格线是否加粗, 可自动折行.
	 * </pre>
	 * 
	 * @param workBook
	 * @param fontSize
	 * @param strHaveBorder
	 * @param align
	 * @return
	 */
	public HSSFCellStyle getTitleStyleWrap(HSSFCellStyle style, HSSFFont font, int fontSize, 
	                                   String strHaveBorder, short align) {

		style.setAlignment(align);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

		// 设定字体大小
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) fontSize);
		style.setFont(font);

		if ("Y".equals(strHaveBorder)) {
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		}

		// 单元格线
		style.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
		style.setWrapText(true);

		return style;
	}
	
	/**
	 * <pre>
	 * 2010-3-4 A5KittyGuo
	 * 	建立单元格.
	 * </pre>
	 * 
	 * @param cellNo
	 * @param row
	 * @param strValue
	 * @param style
	 */
	public void createCell(short cellNo, HSSFRow row, String strValue, HSSFCellStyle style) {

//		HSSFRichTextString richText = new HSSFRichTextString(strValue);
		
		HSSFCell cell = row.createCell((short) cellNo);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellStyle(style);
		cell.setCellValue(strValue);
	}

	/**
	 * <pre>
	 * 2010-11-29 Simon
	 * 	获取Object的String值
	 * </pre>
	 * 
	 * @param object
	 * @return
	 */
	private String getStringValue (Object object) {

		return object == null ? "" : object.toString();
	}
	
	/**
	 * <pre>
	 * 2010-11-27 Simon
	 * 	获取业代状态
	 * </pre>
	 * 
	 * @param id
	 * @return
	 */
	private String getStatus(Object id) {
		
		String status = "";
		
		if (id == null) {
			
			return status;
		}
		else if ("0".equals(id)) {
			
			status = "离职";
		}
		else if ("1".equals(id)) {
			
			status = "在职";
		}
		else if ("2".equals(id)) {
			
			status = "异动";
		}
		
		return status;
	}
	
	/**
	 * <pre>
	 * 2010-11-27 Simon
	 * 	获取特陈位置
	 * </pre>
	 * 
	 * @param locationTypeSid
	 * @return
	 */
	private String getLocation(Object locationTypeSid) {
		
		String location = "";
		
		if (locationTypeSid == null) {
			
			return location;
		}
		
		int id = Integer.parseInt(locationTypeSid.toString());
		
		switch (id) {
			
			case 0: 
				
				break;
				
			case 1: 
				
				location = "主通道区";						
				break;
				
			case 2: 
				
				location = "卖场入口区";						
				break;
				
			case 3: 
									
				location = "收银台区";						
				break;
									
			case 4: 
				
				location = "未陈列";						
				break;
		}
		
		return location;
	}
}
