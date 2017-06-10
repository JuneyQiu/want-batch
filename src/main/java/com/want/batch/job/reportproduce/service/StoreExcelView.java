
// ~ Package Declaration
// ==================================================

package com.want.batch.job.reportproduce.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.springframework.stereotype.Component;

import com.want.batch.job.reportproduce.pojo.SpecialExhibitProduct;
import com.want.batch.job.reportproduce.pojo.TaskStoreSpecialExhibit;
import com.want.batch.job.utils.ProjectConfig;

// ~ Comments
// ==================================================

/**
 * 稽核终端ExcelView.
 * 
 * <pre>
 * 历史纪录：
 * 2010-3-24 Ryan
 * 	新建文件
 * </pre>
 * 
 * @author 
 * <pre>
 * SD
 * 	WendyLu
 * PG
 *	Ryan
 * UT
 *
 * MA
 * </pre>
 * @version $Rev$
 *
 * <p/> $Id$
 *
 */
@Component
public class StoreExcelView {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	// mandy add 2013-04-22
	@SuppressWarnings("unused")
	private String taskStoreProjSids;
	
	@SuppressWarnings("unchecked")
	public void buildExcelDocument(Map<String, Object> storeSubrote, List<Map<String, Object>> storeInfos, 
			List<Map<String, Object>> detailReport, List<Map<String, Object>> taskList,
			Map<String, Object> directiveTblMap) throws Exception {
		
		try {
			// 2011-7-22 Deli add 当storeSubrote和storeInfos不为null时，进行列印;
			// 2011-08-29 mirabelle add detailReport不为null的情况
			if ((storeSubrote != null) && (storeInfos != null) && (detailReport != null)) {
				
				// 2010-06-05 Deli add 获取线路对应的projectSid
				String projectSid = (null != storeSubrote.get("projectSid")) ? storeSubrote.get("projectSid").toString() : null;			
				
				// 获得任务物件
				Map<String, Object> mapTaskList = taskList != null && taskList.size() > 0 ? taskList.get(0) : null;		
				String strJhName = "";
				if (mapTaskList != null) {
					strJhName = mapTaskList.get("JH_NAME").toString();
				}		
				
				// 文件
				File file = new File((null == directiveTblMap.get("ROOT_PATH") ? null : directiveTblMap.get("ROOT_PATH").toString())
						+ (null == directiveTblMap.get("FILE_NAME") ? null : directiveTblMap.get("FILE_NAME").toString())
						+ ".xls");
				logger.info("稽核专员终端报表存放路径 ： " + directiveTblMap.get("ROOT_PATH").toString() + directiveTblMap.get("FILE_NAME").toString() + ".xls");
				// 创建文件
				if (!file.exists()) {

					file.createNewFile();
				}

				FileOutputStream out = new FileOutputStream(file);
				
				logger.info("稽核专员终端报表 : 开始创建excel文件");
				
				logger.info("稽核专员终端报表，列印主报表-start");
				// sheet Name
				String sheetName = strJhName+"-终端列表";
				
				HSSFWorkbook workbook = new HSSFWorkbook();
				
				// 新建sheet
				HSSFSheet sheet = workbook.createSheet();
				
				workbook.setSheetName(0, sheetName, HSSFWorkbook.ENCODING_UTF_16);
				//workbook.setSheetName(0, sheetName);

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
				
				// 设置列宽
				sheet.setColumnWidth((short) 0, (short) 1300);
				sheet.setColumnWidth((short) 1, (short) 1300);
				
				// 营业所名称
				sheet.setColumnWidth((short) 2, (short) 3500);
				sheet.setColumnWidth((short) 3, (short) 3000);
				
				// 业代线别
				sheet.setColumnWidth((short) 4, (short) 3200);
				
				// 业代线别名称
				sheet.setColumnWidth((short) 5, (short) 3500);
				
				// 业代姓名
				sheet.setColumnWidth((short) 6, (short) 3000);
				
				// 业代姓名名称
				sheet.setColumnWidth((short) 7, (short) 4000);
				
				// 业代日期
				sheet.setColumnWidth((short) 8, (short) 3000);
				
				// 业代日期名称
				sheet.setColumnWidth((short) 9, (short) 2000);
				sheet.setColumnWidth((short) 10, (short) 2500);
				sheet.setColumnWidth((short) 11, (short) 1200);
				sheet.setColumnWidth((short) 12, (short) 1200);
				sheet.setColumnWidth((short) 13, (short) 1200);
				sheet.setColumnWidth((short) 14, (short) 1200);
				sheet.setColumnWidth((short) 15, (short) 1200);
				sheet.setColumnWidth((short) 16, (short) 1200);
				
				// 设置标题
				HSSFRow rowTitle = sheet.createRow(0);
				this.createCell((short)4, rowTitle, "终端稽核路线表", 
					this.getTitleStyle(workbook, 14, "", HSSFCellStyle.ALIGN_CENTER) );
				this.createCell((short)5, rowTitle, "", 
					this.getTitleStyle(workbook, 14, "", HSSFCellStyle.ALIGN_CENTER) );
				this.createCell((short)6, rowTitle, "", 
					this.getTitleStyle(workbook, 14, "", HSSFCellStyle.ALIGN_CENTER) );
				sheet.addMergedRegion(new Region(0, (short)4, 0, (short)6));
				
				// 设置终端所属营业所，业代线别等资料
				creatStoreSubrote(workbook, sheet, storeSubrote);
				
				// 2010-10-18 deli modify 若路线下不是所有终端都添加到任务中或者为“车销”时，不显示"终端路线是否合理"
				if ("1".equals(storeSubrote.get("IS_ALL")) && !"2".equals(storeSubrote.get("attSid"))) {
					
					creatStoreSubroteInput(workbook, sheet);
				}
				
				// 设置终端信息
				int sheetRowCount = creatProductInfo(workbook, sheet, projectSid, storeInfos,6);
				
				// 打印签字栏
				printSignature(sheetRowCount+4,workbook, sheet);
				
				// 列印主报表-end
				logger.info("稽核专员终端报表，列印主报表-end");
				
				// 列印终端明细报表 - start
				
				int sheetCount = 1;

				// sheet名称流水号
				int sheetNameNo = 1;
				
				logger.info("终端明细资料总笔数：" + detailReport.size());
				logger.info("终端明细报表列印 - start");
				for (int i=0; i<detailReport.size(); i++) {

					Map<String,Object> mapDetailReport = detailReport.get(i);
					
					// 终端明细-终端资料
					Map<String,Object> mapStore = mapDetailReport.get("storeInfo")!= null ? (Map<String,Object>)mapDetailReport.get("storeInfo") : null;
					
					// 稽核特陈真实性
					List<Map<String,Object>> lstSpeciaExhibit = mapDetailReport.get("speciaExhibit")!= null ? (List<Map<String,Object>>)mapDetailReport.get("speciaExhibit") : null;
					
					// 终端价格零售价格
					List<Map<String,Object>> lstTaskStore = mapDetailReport.get("taskStore")!= null ? (List<Map<String,Object>>)mapDetailReport.get("taskStore") : null;
					
					String storeName = "";

					if (mapStore != null) {
						storeName = mapStore.get("STORE_ID") != null ? mapStore.get("STORE_ID").toString() : "";				
					}			
					
					// sheet Name
					String detailsheetName = (sheetNameNo<10?"0"+sheetNameNo : sheetNameNo)+"-"+storeName;
					
					// 新建sheet
					HSSFSheet sheetDetail = workbook.createSheet(detailsheetName);
					workbook.setSheetName(sheetCount, detailsheetName);

					// 设置页眉页脚边距为 0
					sheetDetail.getPrintSetup().setHeaderMargin(0);
					sheetDetail.getPrintSetup().setFooterMargin(0);		
					
					// 设置页边距
					sheetDetail.setMargin(HSSFSheet.TopMargin, 0.36);
					sheetDetail.setMargin(HSSFSheet.BottomMargin, 0.36);
					sheetDetail.setMargin(HSSFSheet.LeftMargin, 0.1);
					sheetDetail.setMargin(HSSFSheet.RightMargin, 0.1);
					
					// 设置列宽
					// 营业所		
					sheetDetail.setColumnWidth((short) 0, (short) 1300);
					sheetDetail.setColumnWidth((short) 1, (short) 3000);
					
					// 营业所名称
					sheetDetail.setColumnWidth((short) 2, (short) 3000);
					sheetDetail.setColumnWidth((short) 3, (short) 2000);
					sheetDetail.setColumnWidth((short) 4, (short) 3500);
					
					// 业代线别
					sheetDetail.setColumnWidth((short) 5, (short) 2500);
					sheetDetail.setColumnWidth((short) 6, (short) 2000);
					
					// 业代线别名称
					sheetDetail.setColumnWidth((short) 7, (short) 3500);
					sheetDetail.setColumnWidth((short) 8, (short) 2500);
					
					// 业代姓名
					sheetDetail.setColumnWidth((short) 9, (short) 2500);
					sheetDetail.setColumnWidth((short) 10, (short) 1500);
					
					// 业代姓名名称
					sheetDetail.setColumnWidth((short) 11, (short) 1500);
					sheetDetail.setColumnWidth((short) 12, (short) 1500);
					sheetDetail.setColumnWidth((short) 13, (short) 1500);
					sheetDetail.setColumnWidth((short) 14, (short) 3000);
					
					// 设置标题
					HSSFRow rowTitleDetail = sheetDetail.createRow(0);
					this.createCell((short)4, rowTitleDetail, "终端店稽核表", 
						this.getTitleStyle(workbook, 14, "", HSSFCellStyle.ALIGN_CENTER) );	
					this.createCell((short)5, rowTitleDetail, "", 
						this.getTitleStyle(workbook, 14, "", HSSFCellStyle.ALIGN_CENTER) );
					this.createCell((short)6, rowTitleDetail, "", 
						this.getTitleStyle(workbook, 14, "", HSSFCellStyle.ALIGN_CENTER) );
					sheetDetail.addMergedRegion(new Region(0, (short)4, 0, (short)6));
					
					// 列印终端所属营业所，业代线别等资料
					creatStoreSubroteDetail(workbook, sheetDetail, storeSubrote);
					
					// 列印终端资料
					List<Map<String,Object>> lstStoreInfos = new ArrayList<Map<String,Object>>();
					lstStoreInfos.add(mapStore);
					int sheetStoreInfoRowCount = creatProductInfoDetail(workbook, sheetDetail, projectSid, lstStoreInfos,4);
					
					// 列印 "找不到终端    起止时间 ：讯息"
					creatStoreSubroteDetailInput(workbook, sheetDetail, sheetStoreInfoRowCount+1);
					
					// 列印"稽核特陈真实性"部分
					sheetStoreInfoRowCount = createSpeciaExhibit(workbook, sheetDetail, lstSpeciaExhibit,sheetStoreInfoRowCount+6);
					
					// 列印"稽核终端零售价格"
					sheetStoreInfoRowCount = createTaskStore(workbook, sheetDetail, lstTaskStore,sheetStoreInfoRowCount);
					
					// 打印签字栏
					printSignature(sheetStoreInfoRowCount+3,workbook, sheetDetail);
					
					sheetCount++;
					sheetNameNo++;
				}
				
				logger.info("终端明细报表列印 - end");
				
				workbook.write(out);

				out.flush();
				out.close();
				
				logger.info("稽核专员终端报表 : 结束创建excel文件");
			}
			else {
				
				logger.error("数据传送失败，请重新查询");
				logger.error("终端路线数据：" + storeSubrote + " 终端信息数据：" + storeInfos + " 终端明细数据：" + detailReport);
				
				throw new Exception();
			}
		} catch (Exception e) {
			
			logger.info("创建稽核专员终端报表excel文件过程中发生异常");
			throw e;
		}
	}

	/**
	 * <pre>
	 * 2010-3-24 Ryan
	 * 	列印"稽核终端零售价格".
	 * </pre>
	 * 
	 * @param workBook
	 * @param sheet
	 * @param lstSpeciaExhibit
	 * @param rowNo
	 */
	public int createTaskStore(HSSFWorkbook workBook, HSSFSheet sheet,List<Map<String, Object>> lstTaskStore,int rowNo) {

		logger.info("列印稽核终端零售价格: " + lstTaskStore.size());
		
		HSSFRow row = sheet.createRow(rowNo);
		//row.setHeightInPoints(30);

		this.createCell((short)0, row, "稽核终端零售价格", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );	
		
		// 补充空白单元格
		for (int i = 1; i < 14; i++) {
			this.createCell((short)i, row, "", 
				this.getTitleStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		}
		sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)13));
		
		rowNo++;
		HSSFRow row1 = sheet.createRow(rowNo);
		//row1.setHeightInPoints(30);
		
		// 序号
		this.createCell((short)0, row1, "序号", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		
		// 客户类型
		this.createCell((short)1, row1, "类型", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		
		// 品项
		this.createCell((short)2, row1, "品项", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );		
		for (int i=3; i<9; i++) {
			this.createCell((short)i, row1, "", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		}			
		sheet.addMergedRegion(new Region(rowNo, (short)2, rowNo, (short)8));
		
		// 2010-08-11 Deli add
		// 是否进店
		this.createCell((short)9, row1, "是否进店", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short)8, rowNo, (short)8));
		
		// 2010-11-10 Deli 去掉实际特陈
		// 实际特陈
//		this.createCell((short)9, row1, "实际特陈", 
//			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
//		sheet.addMergedRegion(new Region(rowNo, (short)9, rowNo, (short)9));
		// 2010-08-11 Deli add end
		
		// 销售价格
		this.createCell((short)10, row1, "销售价格", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short)11, row1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short)10, rowNo, (short)11));

		// Lucien 2010-06-03 Add:新增有价格标签
		this.createCell((short)12, row1, "有价格标签", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short)13, row1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short)12, rowNo, (short)13));
		
		rowNo++;
		
		if (lstTaskStore != null && lstTaskStore.size() > 0) {

			for (int i=0; i<lstTaskStore.size(); i++) {

				HSSFRow	row2= sheet.createRow(rowNo);
				//row2.setHeightInPoints(30);
				
				Map<String, Object> taskStoreMap = lstTaskStore.get(i);

				// 序号
				this.createCell((short)0, row2, i+1+"", 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
				
				// 客户类型
				String customerType = taskStoreMap.get("CUSTOMER_TYPE") != null ? taskStoreMap.get("CUSTOMER_TYPE").toString() : "";
			
				this.createCell((short)1, row2, customerType, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
				
				// 品项
				// 2011-03-03 Deli modify				
				// 品相名称为 “lv5Id-lv5Name(价格转换率 最小交易单位)”
				StringBuilder prodName = new StringBuilder()
					.append(taskStoreMap.get("PROD_ID"))
					.append("-")
					.append(taskStoreMap.get("PROD_NAME"))
					.append("(") 
					.append((taskStoreMap.get("PRICE_CONVERT_RATE") != null) ? taskStoreMap.get("PRICE_CONVERT_RATE").toString() : "")
					.append((taskStoreMap.get("PRICE_NAME") != null) ? taskStoreMap.get("PRICE_NAME").toString() : "")
					.append(")");
				
				this.createCell((short)2, row2, prodName.toString(), 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
				for (int j=3; j<9; j++) {
					this.createCell((short)j, row2, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
				}			
				sheet.addMergedRegion(new Region(rowNo, (short)2, rowNo, (short)8));
				
				// 2010-08-11 Deli add
				// 是否进店
				String isEnterStore = (String) taskStoreMap.get("IS_ENTERSTORE");
				
				if (isEnterStore != null) {
					
					isEnterStore = StringUtils.equals(isEnterStore, "1") ? "是" : "否";
				}
				
				this.createCell((short)9, row2, isEnterStore, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
				sheet.addMergedRegion(new Region(rowNo, (short)9, rowNo, (short)9));
				
				// 实际陈列
				// 2010-11-10 Deli 屏蔽实际陈列
//				String displayActual = (taskStoreMap.get("DISPLAY_ACTUAL") == null) ? null : taskStoreMap.get("DISPLAY_ACTUAL").toString();
//				
//				this.createCell((short)9, row2, displayActual, 
//					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_RIGHT));
//				sheet.addMergedRegion(new Region(rowNo, (short)9, rowNo, (short)9));
				
				// 2010-08-11 Deli add end
				
				// 销售价格
				this.createCell((short)10, row2, "", 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_RIGHT) );
				this.createCell((short)11, row2, "", 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_RIGHT) );
				sheet.addMergedRegion(new Region(rowNo, (short)10, rowNo, (short)11));
				
				// Lucien 2010-06-03 Add：新增有价格标签
				this.createCell((short)12, row2, "", 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
				this.createCell((short)13, row2, "", 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_RIGHT) );
				sheet.addMergedRegion(new Region(rowNo, (short)12, rowNo, (short)13));
				
				rowNo++;
			}
		}
	
		return rowNo;
	}
		
	/**
	 * <pre>
	 * 2010-3-24 Ryan
	 * 	列印"稽核特陈真实性".
	 * </pre>
	 * 
	 * @param workBook
	 * @param sheet
	 * @param lstSpeciaExhibit
	 * @param rowNo
	 */
	@SuppressWarnings("unchecked")
	public int createSpeciaExhibit(HSSFWorkbook workBook, HSSFSheet sheet,List<Map<String, Object>> lstSpeciaExhibit,int rowNo) {
		
		logger.info("列印稽核特陈真实性: " + lstSpeciaExhibit.size());
		
		HSSFRow row = sheet.createRow(rowNo);
		//row.setHeightInPoints(30);

		this.createCell((short)0, row, "稽核特陈真实性", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );	
		
		// 补充空白单元格
		for (int i = 1; i < 15; i++) {
			this.createCell((short)i, row, "", 
				this.getTitleStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		}
		
		sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)14));
		
		rowNo++;
		HSSFRow row1 = sheet.createRow(rowNo);
		//row1.setHeightInPoints(30);
		
		// 客户名称
		this.createCell((short)0, row1, "客户名称", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short)1, row1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)1));
		
		// 客户类型
		this.createCell((short)2, row1, "客户类型", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		
		// 填报人
		this.createCell((short)3, row1, "填报人", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short)4, row1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short)3, rowNo, (short)4));
		
		// 特陈有无
		this.createCell((short)5, row1, "特陈有无", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		
		// 位置
		this.createCell((short)6, row1, "位置", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		
		// 特陈形式
		this.createCell((short)7, row1, "特陈形式", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		
		// 特陈面积/面数
		this.createCell((short)8, row1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short)7, rowNo, (short)8));
		
		// 2013-04-01 mandy add 陈列差
		this.createCell((short)9, row1, "陈列差", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		
		// 2010-11-15 Deli add 特陈不达标
		this.createCell((short)10, row1, "特陈不达标", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		
		// 陈列品项 
		this.createCell((short)11, row1, "陈列品项 ", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short)12, row1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );		
		this.createCell((short)13, row1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short)14, row1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short)15, row1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short)11, rowNo, (short)15));

		rowNo++;

		if (lstSpeciaExhibit != null && lstSpeciaExhibit.size() > 0) {
			
			// 合并单元格所用的起始行
			int intMergeRowNumberStart = 0;
	
			for (int i=0; i<lstSpeciaExhibit.size(); i++) {
				
				Map<String, Object> speciaExhibitMap = lstSpeciaExhibit.get(i);
				
				// 获得客户类型
				String strType = "";
				if (speciaExhibitMap.get("type") != null) {
					strType = speciaExhibitMap.get("type").toString();
				}
				
				// 获得special
				TaskStoreSpecialExhibit taskStoreSpecialExhibit = speciaExhibitMap.get("special")!= null ? (TaskStoreSpecialExhibit)speciaExhibitMap.get("special") : null;
				
				// 获得陈列品项
				List<SpecialExhibitProduct> products = speciaExhibitMap.get("products")!= null ? (List<SpecialExhibitProduct>)speciaExhibitMap.get("products") : null;;
				
				// 取出 陈列品项为 业代填写 && 稽核专员,业代填写的为 products 中 productId != 0的，稽核专员的为全部，使用products本身。
				List<SpecialExhibitProduct> productCustomerPlan = new ArrayList<SpecialExhibitProduct>();
				for (int j=0; j<products.size(); j++) {
					
					SpecialExhibitProduct specialExhibitProduct = products.get(j);
					if (!"0".equals(specialExhibitProduct.getProductId())) {
						productCustomerPlan.add(specialExhibitProduct);
					}					
				}
				
				// 业代填写部分所使用的创建Excel行的对象
				HSSFRow	rowCustomerPlan= sheet.createRow(rowNo);
				//rowCustomerPlan.setHeightInPoints(30);
				
				intMergeRowNumberStart = rowNo;
				
				// 列印业代填写部分
				if (taskStoreSpecialExhibit != null) {

					// 客户名称
					String customerName = taskStoreSpecialExhibit.getCustomerId() != null ? taskStoreSpecialExhibit.getCustomerId() : "";
					if (taskStoreSpecialExhibit.getCustomerName() != null) {
						customerName+="-"+taskStoreSpecialExhibit.getCustomerName();
					}
					this.createCell((short)0, rowCustomerPlan, customerName, 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					this.createCell((short)1, rowCustomerPlan, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)1));
				
					// 客户类型
					this.createCell((short)2, rowCustomerPlan, strType, 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
				
					// 填报人:2010-07-20 Simonren modify "客户计划"-->"业代填写"
					this.createCell((short)3, rowCustomerPlan, "业代填写", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					this.createCell((short)4, rowCustomerPlan, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					sheet.addMergedRegion(new Region(rowNo, (short)3, rowNo, (short)4));
					
					// 特陈有无
					this.createCell((short)5, rowCustomerPlan, "-", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					
					// 位置
					String strLocationTypeName = taskStoreSpecialExhibit.getLocationTypeName();
					
					if ("1".equals(strLocationTypeName)) {
						strLocationTypeName = "主通道区";
					}
					else if ("2".equals(strLocationTypeName)) {
						strLocationTypeName = "卖场入口区";
					}
					else if ("3".equals(strLocationTypeName)) {
						strLocationTypeName = "收银台区";
					}
					else if ("4".equals(strLocationTypeName)) {
						strLocationTypeName = "未陈列";
					}
					
					this.createCell((short)6, rowCustomerPlan, strLocationTypeName, 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
				
					// 特陈形式-业代填写 Lucien 2010-06-22 更改特陈形式的取值方式
					String strDisplayTypeName = taskStoreSpecialExhibit.getDisplayTypeTblNamea();

					this.createCell((short)7, rowCustomerPlan, strDisplayTypeName, 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					
					// 特陈面积/面数displaySideCount
					String strDisplayTypeNameAcreage = taskStoreSpecialExhibit.getDisplayTypeName();
					String displayAcreage = "";
					
					// 2010-12-21 Deli 添加地堆(展架)特陈形式的判断
					if ("1".equals(strDisplayTypeNameAcreage) || "5".equals(strDisplayTypeNameAcreage)) {
						displayAcreage = taskStoreSpecialExhibit.getDisplayAcreage()+"㎡";
					}
					else if ("2".equals(strDisplayTypeNameAcreage)) {
						displayAcreage = taskStoreSpecialExhibit.getDisplaySideCount();
					}
					else {
						
						displayAcreage = taskStoreSpecialExhibit.getDisplayParamId();
					}
					
					this.createCell((short)8, rowCustomerPlan, displayAcreage, 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					
					// 2013-04-01 mandy add 陈列差
					this.createCell((short)9, rowCustomerPlan, "-", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					
					// 2010-11-16 Deli add 特陈不达标
					this.createCell((short)10, rowCustomerPlan, "-", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					
					// 陈列品项 -业代填写
					for (int j=0; j<productCustomerPlan.size(); j++) {
						
						SpecialExhibitProduct specialExhibitProduct = productCustomerPlan.get(j);
						
						// 判断若为第一次循环，则使用已创建好的行 rowCustomerPlan 进行单元格的建立，若不是第一次，则创建新行进行各单元格的赋值
						if (StringUtils.isBlank(specialExhibitProduct.getDateRoute())) { // 2010-09-19 deli add 只显示业代录入的品项
							
							if (j==0) {
								
								this.createCell((short)11, rowCustomerPlan, specialExhibitProduct.getProductName(), 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
								this.createCell((short)12, rowCustomerPlan, "", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
								this.createCell((short)13, rowCustomerPlan, "", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
								this.createCell((short)14, rowCustomerPlan, "", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
								this.createCell((short)15, rowCustomerPlan, "", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
								
								sheet.addMergedRegion(new Region(rowNo, (short)11, rowNo, (short)15));							
							}
							else {
								HSSFRow	rowCustomerPlanSpecial = sheet.createRow(rowNo);
								//rowCustomerPlanSpecial.setHeightInPoints(30);
								
								// 创建客户名称空白行
								this.createCell((short)0, rowCustomerPlanSpecial, " ", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
								this.createCell((short)1, rowCustomerPlanSpecial, " ", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
								sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)1));					
								
								// 客户类型
								this.createCell((short)2, rowCustomerPlanSpecial, " ", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
								
								// 填报人-业代填写
								this.createCell((short)3, rowCustomerPlanSpecial, " ", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
								this.createCell((short)4, rowCustomerPlanSpecial, " ", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );			
								sheet.addMergedRegion(new Region(rowNo, (short)3, rowNo, (short)4));	
								
								// 特陈有无 - 业代填写
								this.createCell((short)5, rowCustomerPlanSpecial, "", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
								
								// 位置-业代填写
								this.createCell((short)6, rowCustomerPlanSpecial, "", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
								
								// 特陈形式 - 业代填写
								this.createCell((short)7, rowCustomerPlanSpecial, "", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
								
								// 特陈面积，面数 - 业代填写
								this.createCell((short)8, rowCustomerPlanSpecial, "", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
								
								// 2013-04-01 add mandy 陈列差
								// 陈列差 - 业代填写
								this.createCell((short)9, rowCustomerPlanSpecial, "", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
								
								// 特陈不达标 - 业代填写
								this.createCell((short)10, rowCustomerPlanSpecial, "", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
								
								// 特陈形式-业代填写
								this.createCell((short)11, rowCustomerPlanSpecial, specialExhibitProduct.getProductName(), 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));		
								this.createCell((short)12, rowCustomerPlanSpecial, " ", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));							
								this.createCell((short)13, rowCustomerPlanSpecial, " ", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));							
								this.createCell((short)14, rowCustomerPlanSpecial, " ", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));							
								this.createCell((short)15, rowCustomerPlanSpecial, " ", 
									this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));				
								sheet.addMergedRegion(new Region(rowNo, (short)11, rowNo, (short)15));	
							}
						}
											
						rowNo++;						
					}
				
					// 合并单元格

					// 合并填报人-业代填写
					sheet.addMergedRegion(new Region(intMergeRowNumberStart, (short)3, rowNo-1, (short)4));
					
					// 特陈有无 - 业代填写
					sheet.addMergedRegion(new Region(intMergeRowNumberStart, (short)5, rowNo-1, (short)5));
					
					// 位置-业代填写
					sheet.addMergedRegion(new Region(intMergeRowNumberStart, (short)6, rowNo-1, (short)6));
					
					// 特陈形式 - 业代填写
					sheet.addMergedRegion(new Region(intMergeRowNumberStart, (short)7, rowNo-1, (short)7));
					
					// 特陈面积，面数 - 业代填写
					sheet.addMergedRegion(new Region(intMergeRowNumberStart, (short)8, rowNo-1, (short)8));
					
					// 2013-4-12 Mandy add 陈列差
					sheet.addMergedRegion(new Region(intMergeRowNumberStart, (short)9, rowNo-1, (short)9));
					
					// 2010-11-16 Deli add 特陈不达标 - 业代填写
					sheet.addMergedRegion(new Region(intMergeRowNumberStart, (short)10, rowNo-1, (short)10));
				}
			
				HSSFRow	rowCheckStaff= sheet.createRow(rowNo);
				//rowCheckStaff.setHeightInPoints(30);
			 
				if (taskStoreSpecialExhibit != null) {

					// 客户名称
					this.createCell((short)0, rowCheckStaff, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					this.createCell((short)1, rowCheckStaff, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)1));	
					
					// 客户类型					
					this.createCell((short)2, rowCheckStaff, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );					
					
					// 填报人
					this.createCell((short)3, rowCheckStaff, "稽核专员", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					this.createCell((short)4, rowCheckStaff, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					sheet.addMergedRegion(new Region(rowNo, (short)3, rowNo, (short)4));	
					
					// 特陈有无-稽核专员
					this.createCell((short)5, rowCheckStaff, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					
					// 位置-稽核专员				
					this.createCell((short)6, rowCheckStaff, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );

					// 特陈形式-稽核专员					
					this.createCell((short)7, rowCheckStaff, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					
					// 特陈面积/面数-稽核专员					
					this.createCell((short)8, rowCheckStaff, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					
					// 2013-04-01 mandy add 陈列差 
					this.createCell((short)9, rowCheckStaff, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
					
					// 2010-11-16 Deli add 特陈不达标 
					this.createCell((short)10, rowCheckStaff, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
				
					// 陈列品项 -稽核专员
					this.createCell((short)11, rowCheckStaff, "", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));							
					this.createCell((short)12, rowCheckStaff, " ", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
					this.createCell((short)13, rowCheckStaff, " ", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
					this.createCell((short)14, rowCheckStaff, " ", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
					this.createCell((short)15, rowCheckStaff, " ", 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
					sheet.addMergedRegion(new Region(rowNo, (short)11, rowNo, (short)15));	
				}

				// 合并客户名称
				sheet.addMergedRegion(new Region(intMergeRowNumberStart, (short)0, rowNo, (short)0));
				
				// 合并客户类型
				sheet.addMergedRegion(new Region(intMergeRowNumberStart, (short)2, rowNo, (short)2));
				rowNo++;
			}
		}
		
		return rowNo;
	}
	
	/**
	 * <pre>
	 * 2010-3-24 Ryan
	 * 	显示终端所属营业所，业代线别等资料.
	 * </pre>
	 * 
	 * @param workBook
	 * @param sheet
	 * @param customerInfo
	 */
	private void creatStoreSubrote(HSSFWorkbook workBook, HSSFSheet sheet, Map<String, Object> storeSubrote) {
		
		logger.info("显示终端所属营业所，业代线别等资料： " + storeSubrote.size());
		
		// 开始行
		int rowNo = 2;
		
		// 第1行 
		HSSFRow row = sheet.createRow(rowNo);
		//row.setHeightInPoints(30);
		
		this.createCell((short)0, row, "营业所：", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );	
		this.createCell((short)1, row, "", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );	
		sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)1));
		
		if (storeSubrote != null) {
			String branchName = storeSubrote.get("BRANCH_ID") != null ? 
					storeSubrote.get("BRANCH_ID").toString() : "" ;
			
			if (storeSubrote.get("BRANCH_NAME") != null) {
				branchName += "-" + storeSubrote.get("BRANCH_NAME").toString();
			}

			// 营业所编号-营业所名称
			this.createCell((short)2, row, branchName, 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
			this.createCell((short)3, row, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
			sheet.addMergedRegion(new Region(rowNo, (short)2, rowNo, (short)3));
		}
		
		// 业代线别：
		this.createCell((short)4, row, "业代线别：", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	

		if (storeSubrote != null) {
			String saleName = storeSubrote.get("SALE_NAME") != null ? 	storeSubrote.get("SALE_NAME").toString() : "";
		
			// 业代线别名称
			this.createCell((short)5, row, saleName, 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
		}
		
		// 业代姓名：
		this.createCell((short)6, row, "业代姓名：", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
		
		if (storeSubrote != null) {
			String empName = storeSubrote.get("EMP_ID") != null ? storeSubrote.get("EMP_ID").toString() : "";
			
			if (storeSubrote.get("EMP_NAME") != null ) {
				empName += "-" + storeSubrote.get("EMP_NAME").toString();
			}		
			
			// 业代姓名内容
			this.createCell((short)7, row,empName, 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
		}
		
		// 业代日期：
		this.createCell((short)8, row, "业代日期：", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	

		if (storeSubrote != null) {

			// 业代日期内容
			this.createCell((short)9, row, 
				storeSubrote.get("visitDate") != null ? storeSubrote.get("visitDate").toString() : "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
			this.createCell((short)10, row, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
			sheet.addMergedRegion(new Region(rowNo, (short)9, rowNo, (short)10));

		}
				
		rowNo++;
		HSSFRow row1 = sheet.createRow(rowNo);
		
		// 客户名称：
		this.createCell((short)0, row1, "客户名称：", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );	
		this.createCell((short)1, row1, "", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );	
		sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)1));

		if (storeSubrote != null) {

			String customerName = storeSubrote.get("CUSTOMER_ID") != null ? storeSubrote.get("CUSTOMER_ID").toString() : "";
			
			if (storeSubrote.get("CUSTOMER_NAME") != null ) {
				customerName += "-" + storeSubrote.get("CUSTOMER_NAME").toString();
			}	
			
			// 客户编号-客户名称
			this.createCell((short)2, row1, customerName, 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
			this.createCell((short)3, row1, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
			sheet.addMergedRegion(new Region(rowNo, (short)2, rowNo, (short)3));
		}
		
		// 未找到数量：
		this.createCell((short)4, row1, "未找到数量：", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	

		if (storeSubrote != null) {
			
			this.createCell((short)5, row1, storeSubrote.get("COUNT") != null ? storeSubrote.get("COUNT").toString() : "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
		}

		// 建立空白单元格的样式
		for (int i = 6; i < 11; i++) {
			this.createCell((short)i, row1, "", 
				this.getTitleStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		}
		
		sheet.addMergedRegion(new Region(rowNo, (short)6, rowNo, (short)10));
	}
	

	/**
	 * <pre>
	 * 2010-3-24 Ryan
	 * 	显示终端所属营业所，业代线别等资料.
	 * </pre>
	 * 
	 * @param workBook
	 * @param sheet
	 * @param customerInfo
	 */
	private void creatStoreSubroteDetail(HSSFWorkbook workBook, HSSFSheet sheet, Map<String, Object> storeSubrote) {
		
		logger.info("显示终端所属营业所，业代线别等资料: " + storeSubrote.size());
		
		// 开始行
		int rowNo = 1;
		
		// 第1行 
		HSSFRow row = sheet.createRow(rowNo);
		//row.setHeightInPoints(30);
		
		this.createCell((short)0, row, "营业所：", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );	
		this.createCell((short)1, row, "", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );	
		sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)1));
		
		if (storeSubrote != null) {
			String branchName = storeSubrote.get("BRANCH_ID") != null ? 
					storeSubrote.get("BRANCH_ID").toString() : "" ;
			
			if (storeSubrote.get("BRANCH_NAME") != null) {
				branchName += "-" + storeSubrote.get("BRANCH_NAME").toString();
			}

			// 营业所编号-营业所名称
			this.createCell((short)2, row, branchName, 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
			this.createCell((short)3, row, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
			this.createCell((short)4, row, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
			sheet.addMergedRegion(new Region(rowNo, (short)2, rowNo, (short)4));
		}
		
		// 业代线别：
		this.createCell((short)5, row, "业代线别：", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
		this.createCell((short)6, row, "", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
		sheet.addMergedRegion(new Region(rowNo, (short)5, rowNo, (short)6));

		if (storeSubrote != null) {
			String saleName = storeSubrote.get("SALE_NAME") != null ? 	storeSubrote.get("SALE_NAME").toString() : "";
		
			// 业代线别名称
			this.createCell((short)7, row, saleName, 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
			this.createCell((short)8, row, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
			sheet.addMergedRegion(new Region(rowNo, (short)7, rowNo, (short)8));
		}
		
		// 业代姓名：
		this.createCell((short)9, row, "业代姓名：", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
		this.createCell((short)10, row, "", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
		sheet.addMergedRegion(new Region(rowNo, (short)9, rowNo, (short)10));
		
		if (storeSubrote != null) {
			String empName = storeSubrote.get("EMP_ID") != null ? storeSubrote.get("EMP_ID").toString() : "";
			
			if (storeSubrote.get("EMP_NAME") != null ) {
				empName += "-" + storeSubrote.get("EMP_NAME").toString();
			}		
			
			// 业代姓名内容
			this.createCell((short)11, row,empName, 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
			this.createCell((short)12, row,"", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
			this.createCell((short)13, row,"", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
			sheet.addMergedRegion(new Region(rowNo, (short)11, rowNo, (short)13));
		}
		
		rowNo++;
		HSSFRow row1 = sheet.createRow(rowNo);
		
		// 业代日期：
		this.createCell((short)0, row1, "业代日期：", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
		this.createCell((short)1, row1, "", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
		sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)1));

		if (storeSubrote != null) {

			// 业代日期内容
			this.createCell((short)2, row1, 
				storeSubrote.get("visitDate") != null ? storeSubrote.get("visitDate").toString() : "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
			this.createCell((short)3, row1, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
			this.createCell((short)4, row1, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
			sheet.addMergedRegion(new Region(rowNo, (short)2, rowNo, (short)4));

		}
	
		// 客户名称：
		this.createCell((short)5, row1, "客户名称：", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
		this.createCell((short)6, row1, "", 
			this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );	
		sheet.addMergedRegion(new Region(rowNo, (short)5, rowNo, (short)6));

		if (storeSubrote != null) {

			String customerName = storeSubrote.get("CUSTOMER_ID") != null ? storeSubrote.get("CUSTOMER_ID").toString() : "";
			
			if (storeSubrote.get("CUSTOMER_NAME") != null ) {
				customerName += "-" + storeSubrote.get("CUSTOMER_NAME").toString();
			}	
			
			// 客户编号-客户名称
			this.createCell((short)7, row1, customerName, 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
			this.createCell((short)8, row1, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
			this.createCell((short)9, row1, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
			this.createCell((short)10, row1, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
			this.createCell((short)11, row1, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
			this.createCell((short)12, row1, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
			this.createCell((short)13, row1, "", 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
			sheet.addMergedRegion(new Region(rowNo, (short)7, rowNo, (short)13));
		}
	}
	
	/**
	 * <pre>
	 * 2010-3-24 Ryan
	 * 	输入区
	 * </pre>
	 * 
	 * @param workBook
	 * @param sheet
	 * @param customerInfo
	 */
	private void creatStoreSubroteInput(HSSFWorkbook workBook, HSSFSheet sheet) {
		
		// 开始行
		int rowNo = 5;
		HSSFRow	row = sheet.createRow(rowNo);
		
		// 终端路线是否合理
		this.createCell((short)0, row, "终端路线是否合理： □是□否 备注______________________________________________________", 
			this.getTitleStyle(workBook, 10, "", HSSFCellStyle.ALIGN_LEFT) );

		sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)6));
	}

	/**
	 * <pre>
	 * 2010-3-24 Ryan
	 * 	稽核终端明细表输入区。
	 * </pre>
	 * 
	 * @param workBook
	 * @param sheet
	 * @param customerInfo
	 */
	private void creatStoreSubroteDetailInput(HSSFWorkbook workBook, HSSFSheet sheet,int rowNo) {
	
		HSSFRow	row = sheet.createRow(rowNo);
		
		// 找不到终端"
		this.createCell((short)0, row, "□找不到终端, 原因 ﹍﹍﹍﹍﹍﹍﹍﹍﹍﹍", 
			this.getTitleStyle(workBook, 10, "", HSSFCellStyle.ALIGN_LEFT) );
		sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)13));		
		
		// Lucien Add:新增拖欠特陈费用及原因栏位
		rowNo++;
		HSSFRow	row2 = sheet.createRow(rowNo);
		
		this.createCell((short)0, row2, "□拖欠特陈费用, 原因 ﹍﹍﹍﹍﹍﹍﹍﹍﹍﹍ ", 
			this.getTitleStyle(workBook, 10, "", HSSFCellStyle.ALIGN_LEFT) );
		
		sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)13));
		
		// Lucien Add:有过期品及原因栏位
		rowNo++;
		HSSFRow	row3 = sheet.createRow(rowNo);
		
		this.createCell((short)0, row3, "□有过期品, 原因 ﹍﹍﹍﹍﹍﹍﹍﹍﹍﹍ ", 
			this.getTitleStyle(workBook, 10, "", HSSFCellStyle.ALIGN_LEFT) );
		
		sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)13));
		
		rowNo++;
		HSSFRow	row1 = sheet.createRow(rowNo);
		
		// 起止时间
		this.createCell((short)0, row1, "起止时间 : 从 ﹍﹍﹍﹍﹍﹍ 到 ﹍﹍﹍﹍﹍﹍ ", 
			this.getTitleStyle(workBook, 10, "", HSSFCellStyle.ALIGN_LEFT) );
		
		sheet.addMergedRegion(new Region(rowNo, (short)0, rowNo, (short)13));
	}
	
	/**
	 * <pre>
	 * 2010-3-24 Ryan
	 * 	终端信息.
	 * </pre>
	 * 
	 * @param workBook
	 * @param sheet
	 * @param projectSid
	 * @param storeInfos
	 * @param rowNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int creatProductInfo(HSSFWorkbook workBook,
	                             HSSFSheet sheet,
	                             String projectSid,
	                             List<Map<String, Object>> storeInfos,
	                             int rowNo) {
		// Title
		HSSFRow	rowTitle = sheet.createRow(rowNo);
		//rowTitle.setHeightInPoints(30);
		
		/**
		 * 2011-5-19 Deli add 为了减少以后添加栏位时所添加栏位后的cell索引手动修改，在此定义索引标示并且累加
		 */
		// 标题栏第一行的列索引
		int titleFirstIndex = 0;		
		
		// 标题栏第二行的列索引
		int titleSecondIndex = 0;	
		
		HSSFRow	rowTitle1 = sheet.createRow(rowNo+1);
		
		this.createCell((short) titleFirstIndex++, rowTitle, "顺序", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		
		this.createCell((short) titleFirstIndex++, rowTitle, "终端名称", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short) titleFirstIndex++, rowTitle, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short) titleFirstIndex++, rowTitle, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 3), rowNo+1, (short) (titleSecondIndex - 1)));
		
		// 2011-5-19 Deli add 终端属性（门店类型）
		this.createCell((short) titleFirstIndex++, rowTitle, "终端属性\n（门店类型）", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		
		this.createCell((short) titleFirstIndex++, rowTitle, "负责人", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		
		this.createCell((short) titleFirstIndex++, rowTitle, "电话", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		
		this.createCell((short) titleFirstIndex++, rowTitle, "地址", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleFirstIndex++, rowTitle, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleFirstIndex++, rowTitle, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 3), rowNo+1, (short) (titleSecondIndex - 1)));
		
		/*
		 * modify by Deli 2010-7-16: 添加"三级地"栏位。
		 */
		this.createCell((short) titleFirstIndex++, rowTitle, "三级地", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		
		/*
		 * modify by Simonren 2010-7-16: 添加"四级地"栏位。
		 */
		this.createCell((short) titleFirstIndex++, rowTitle, "四级地", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 2)));
		
		this.createCell((short) titleFirstIndex++, rowTitle, "是否找到", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		
		// 2010-08-27 Deli add
		this.createCell((short) titleFirstIndex++, rowTitle, "取消", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		
		this.createCell((short) titleFirstIndex++, rowTitle, "取消原因", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		// 2010-08-27 Deli add end
		// 2013-04-02 mirabelle update 大配送拆分为大配送，大配送休闲，大配送乳饮
		// 2010-06-05 Deli add 若projectSid=4则显示县城特陈
		// 2010-08-19 Deli modify 若为1之一则显示大配送特陈
		// 2011-04-11 Deli add 17(县城休闲)和18(县城乳饮)
//		if (StringUtils.equals("4", projectSid)
//				 || StringUtils.equals("1", projectSid)
//				 || StringUtils.equals("17", projectSid)
//				 || StringUtils.equals("18", projectSid)) {
		
	// 2013-04-22 mandy modify
		String[] projSids = this.getTaskStoreProjSids().split(",");
		
		if(ArrayUtils.contains(projSids, projectSid)) {
		
			this.createCell((short) titleFirstIndex++, rowTitle, (StringUtils.equals("1", projectSid) || StringUtils.equals("22", projectSid) || StringUtils.equals("23", projectSid)) ? "大配送特陈" : "县城特陈", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			this.createCell((short) titleSecondIndex++, rowTitle1, "", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
			sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		}
		else {
			
			this.createCell((short) titleFirstIndex, rowTitle, "乳品", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			this.createCell((short) (titleFirstIndex + 1), rowTitle, "", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			sheet.addMergedRegion(new Region(rowNo, (short) titleFirstIndex, rowNo, (short) (titleFirstIndex + 1)));
			
			this.createCell((short) titleFirstIndex, rowTitle1, "业代", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));

			this.createCell((short) (titleFirstIndex + 1), rowTitle1, "特陈", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		
			this.createCell((short) (titleFirstIndex + 2), rowTitle, "饮品", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			this.createCell((short) (titleFirstIndex + 3), rowTitle, "", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex + 2), rowNo, (short) (titleFirstIndex + 3)));

			this.createCell((short) (titleFirstIndex + 2), rowTitle1, "业代", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));

			this.createCell((short) (titleFirstIndex + 3), rowTitle1, "特陈", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			
			this.createCell((short) (titleFirstIndex + 4), rowTitle, "休闲", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			this.createCell((short) (titleFirstIndex + 5), rowTitle, "", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex + 4), rowNo, (short) (titleFirstIndex + 5)));

			this.createCell((short) (titleFirstIndex + 4), rowTitle1, "业代", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));

			this.createCell((short) (titleFirstIndex + 5), rowTitle1, "特陈", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			
			// 2011-01-05 Deli add 冰品
			this.createCell((short) (titleFirstIndex + 6), rowTitle, "冰品", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			this.createCell((short) (titleFirstIndex + 7), rowTitle, "", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex + 6), rowNo, (short) (titleFirstIndex + 7)));

			this.createCell((short) (titleFirstIndex + 6), rowTitle1, "业代", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));

			this.createCell((short) (titleFirstIndex + 7), rowTitle1, "特陈", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		}
		
		rowNo+=2;
		
		if (storeInfos != null && storeInfos.size() > 0) {
			for (int i = 0; i < storeInfos.size(); i++) {

				// 2011-5-19 Deli add 列索引
				int colIndex = 0;	
				
				HSSFRow	row= sheet.createRow(rowNo);
				//row.setHeightInPoints(30);
				Map<String, Object> storeInfo = storeInfos.get(i);
				
				// 拜访顺序
				String visitOrder = storeInfo.get("VISIT_ORDER") != null ? storeInfo.get("VISIT_ORDER").toString() : "";
				this.createCell((short) colIndex++, row, visitOrder, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
				
				// 终端名称
				String storeName = storeInfo.get("STORE_ID") != null ? storeInfo.get("STORE_ID").toString() : "";
				
				if (storeInfo.get("STORE_NAME") != null) {
					storeName = storeName + "-" + storeInfo.get("STORE_NAME").toString();
				}
				
				// 2010-09-26 deli add 业代路线上的终端店内只要有业代填写特陈，终端店名前后加特殊符“◆”
				
				String flag = (((storeInfo.get("COUNT") != null) && (Integer.parseInt(storeInfo.get("COUNT").toString()) > 0)) 
						|| (storeInfo.get("HAS_SPECIAL") != null)) ? "◆" : "";
				
				this.createCell((short) colIndex++, row, flag + storeName + flag, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
				this.createCell((short) colIndex++, row, "", 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
				this.createCell((short) colIndex++, row, "", 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
				sheet.addMergedRegion(new Region(rowNo, (short) (colIndex - 3), rowNo, (short) (colIndex - 1)));
				
				// 2011-5-19 Deli add 终端属性（门店类型）
				this.createCell((short) colIndex++, row, (String) storeInfo.get("STORE_TYPE"), 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
				
				// 负责人
				String storeOwenr = storeInfo.get("STORE_OWNER") != null ? storeInfo.get("STORE_OWNER").toString() : "";
				this.createCell((short) colIndex++, row, storeOwenr, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
				
				// 电话:2010-07-16 Simonren modify 显示全部电话
				String storePhone = StringUtils.replace((String) storeInfo.get("PHONE"), "<BR>", "\n");
				this.createCell((short) colIndex++, row, storePhone, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
				
				// 地址
				String strAddress = storeInfo.get("ADDRESS") != null ? storeInfo.get("ADDRESS").toString() : "";
				this.createCell((short) colIndex++, row, strAddress, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
				this.createCell((short) colIndex++, row, "", 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
				this.createCell((short) colIndex++, row, "", 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
				
				sheet.addMergedRegion(new Region(rowNo, (short) (colIndex - 3), rowNo, (short) (colIndex - 1)));
				
				// 三级地
				String thirdLvName = storeInfo.get("THIRD_LV_NAME") != null ? storeInfo.get("THIRD_LV_NAME").toString() : "";
				this.createCell((short) colIndex++, row, thirdLvName, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
				
				// 四级地
				String forthLvName = storeInfo.get("FORTH_LV_NAME") != null ? storeInfo.get("FORTH_LV_NAME").toString() : "";
				this.createCell((short) colIndex++, row, forthLvName, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
		
				// 是否找到
				this.createCell((short) colIndex++, row, "", 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
				
				// 2010-06-05 Deli add 
				// 取消
				this.createCell((short) colIndex++, row, "",
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
				
				// 取消原因				
				this.createCell((short) colIndex++, row, "",
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
				
				// 2013-04-02 mirabelle update 大配送拆分为大配送，大配送休闲，大配送乳饮
				// 2010-06-05 Deli add 若projectSid=4则显示县城特陈
				// 2010-09-27 deli add  大配送
				// 2011-04-11 Deli add 17(县城休闲)和18(县城乳饮)
//				if (StringUtils.equals("4", projectSid)
//						 || StringUtils.equals("1", projectSid)
//						 || StringUtils.equals("17", projectSid)
//						 || StringUtils.equals("18", projectSid)) {

				// 2013-04-22 mandy modify
//				String[] projSids = this.getTaskStoreProjSids().split(",");
				
				if(ArrayUtils.contains(projSids, projectSid)) {
					
					String count = storeInfo.get("COUNT").toString();
					
					// 县城特陈
					this.createCell((short) colIndex++, row, (StringUtils.equals("0", count) ? "" : count), 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
				}
				else {
					
					// 循环显示乳品，饮品，休闲的资料
					// 定义循环显示业代路线，特陈的初始单元格索引 
					short customerType = (short) colIndex++;
					short specialCount = (short) colIndex;
					
					List<Map<String,Object>> lstMap = (List<Map<String,Object>>)storeInfo.get("TYPE_SPECIAL");
					for (int j=0; j<lstMap.size(); j++) {
						
						Map<String,Object> mapTemp = lstMap.get(j);
						
						// 业代路线
						String strCustomerType = "";
						
						if ("Y".equals((String) mapTemp.get("STORE_DIVSION"))) {
							strCustomerType = "√";
						}
						
						this.createCell(customerType, row, strCustomerType, 
							this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
						
						// 特陈
						String strSpecialCount = mapTemp.get("SPECIAL_COUNT") != null ? mapTemp.get("SPECIAL_COUNT").toString() : "";
						this.createCell(specialCount, row, strSpecialCount, 
							this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
						
						customerType+=2;
						specialCount+=2;
					}
				}
				
				rowNo++;
			}		
		}
		
		return rowNo;
	}
	
	/**
	 * <pre>
	 * 2010-3-24 Ryan
	 * 	终端信息.
	 * </pre>
	 * 
	 * @param workBook
	 * @param sheet
	 * @param projectSid
	 * @param storeInfos
	 * @param rowNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int creatProductInfoDetail(HSSFWorkbook workBook,
	                                   HSSFSheet sheet,
	                                   String projectSid,
	                                   List<Map<String, Object>> storeInfos,
	                                   int rowNo) {
		
		// Title
		HSSFRow	rowTitle = sheet.createRow(rowNo);
		//rowTitle.setHeightInPoints(30);
		
		/**
		 * 2011-5-19 Deli add 为了减少以后添加栏位时所添加栏位后的cell索引手动修改，在此定义索引标示并且累加
		 */
		// 标题栏第一行的列索引
		int titleFirstIndex = 0;		
		
		// 标题栏第二行的列索引
		int titleSecondIndex = 0;		
		
		HSSFRow	rowTitle1 = sheet.createRow(rowNo+1);
		
		this.createCell((short) titleFirstIndex++, rowTitle, "顺序", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		
		this.createCell((short) titleFirstIndex++, rowTitle, "终端名称", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short) titleFirstIndex++, rowTitle, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );		
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 2), rowNo+1, (short) (titleSecondIndex - 1)));
		
		// 2011-5-19 Deli add 终端属性（门店类型）
		this.createCell((short) titleFirstIndex++, rowTitle, "终端属性\n（门店类型）", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		
		this.createCell((short) titleFirstIndex++, rowTitle, "负责人", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		
		this.createCell((short) titleFirstIndex++, rowTitle, "电话", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		
		this.createCell((short) titleFirstIndex++, rowTitle, "地址", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleFirstIndex++, rowTitle, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleFirstIndex++, rowTitle, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );		
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 3), rowNo+1, (short) (titleSecondIndex - 1)));		
		
		/*
		 * Deli 2010-7-16: 添加"三级地"栏位。
		 */
		this.createCell((short) titleFirstIndex++, rowTitle, "三级地", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		
		/*
		 * modify by Simonren 2010-7-16: 添加"四级地"栏位。
		 */
		this.createCell((short) titleFirstIndex++, rowTitle, "四级地", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		this.createCell((short) titleSecondIndex++, rowTitle1, "", 
			this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
		sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		
		// 2013-04-02 mirabelle update 大配送拆分为大配送，大配送休闲，大配送乳饮
		// 2010-06-05 Deli add 若projectSid=4则显示县城特陈 
		// 2010-09--27 add 大配送
		// 2011-04-11 Deli add 17(县城休闲)和18(县城乳饮)
//		if (StringUtils.equals("4", projectSid)
//				 || StringUtils.equals("1", projectSid)
//				 || StringUtils.equals("17", projectSid)
//				 || StringUtils.equals("18", projectSid)) {
		
		// 2013-04-22 mandy modify
		String[] projSids = this.getTaskStoreProjSids().split(",");
		
		if(ArrayUtils.contains(projSids, projectSid)) {
		
			this.createCell((short) titleFirstIndex++, rowTitle, (StringUtils.equals("1", projectSid) || StringUtils.equals("22", projectSid) || StringUtils.equals("23", projectSid)) ? "大配送特陈" : "县城特陈",
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			this.createCell((short) titleSecondIndex++, rowTitle1, "", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
			sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex - 1), rowNo+1, (short) (titleSecondIndex - 1)));
		}
		else {
			
			this.createCell((short) titleFirstIndex, rowTitle, "乳品", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			this.createCell((short) (titleFirstIndex + 1), rowTitle, "", 
				this.getGrayColorStyle(workBook, 11, "Y", HSSFCellStyle.ALIGN_CENTER));
			sheet.addMergedRegion(new Region(rowNo, (short) titleFirstIndex, rowNo, (short) (titleFirstIndex + 1)));
			
			this.createCell((short) titleFirstIndex, rowTitle1, "业代", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));

			this.createCell((short) (titleFirstIndex + 1), rowTitle1, "特陈", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		
			this.createCell((short) (titleFirstIndex + 2), rowTitle, "饮品", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			this.createCell((short)  (titleFirstIndex + 3), rowTitle, "", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex + 2), rowNo, (short) (titleFirstIndex + 3)));

			this.createCell((short) (titleFirstIndex + 2), rowTitle1, "业代", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));

			this.createCell((short) (titleFirstIndex + 3), rowTitle1, "特陈", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			
			this.createCell((short) (titleFirstIndex + 4), rowTitle, "休闲", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			this.createCell((short) (titleFirstIndex + 5), rowTitle, "", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex + 4), rowNo, (short) (titleFirstIndex + 5)));

			this.createCell((short) (titleFirstIndex + 4), rowTitle1, "业代", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));

			this.createCell((short) (titleFirstIndex + 5), rowTitle1, "特陈", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			
			// 2011-01-05 Deli add 冰品
			this.createCell((short) (titleFirstIndex + 6), rowTitle, "冰品", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			this.createCell((short) (titleFirstIndex + 7), rowTitle, "", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
			sheet.addMergedRegion(new Region(rowNo, (short) (titleFirstIndex + 6), rowNo, (short) (titleFirstIndex + 7)));

			this.createCell((short) (titleFirstIndex + 6), rowTitle1, "业代", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));

			this.createCell((short) (titleFirstIndex + 7), rowTitle1, "特陈", 
				this.getGrayColorStyle(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
		}
		
		rowNo+=2;
		
		if (storeInfos != null && storeInfos.size() > 0) {
			for (int i = 0; i < storeInfos.size(); i++) {
				
				// 2011-5-19 Deli add 列索引
				int colIndex = 0;
				
				HSSFRow	row= sheet.createRow(rowNo);
				//row.setHeightInPoints(30);
				Map<String, Object> storeInfo = storeInfos.get(i);
				
				// 拜访顺序
				String visitOrder = storeInfo.get("VISIT_ORDER") != null ? storeInfo.get("VISIT_ORDER").toString() : "";
				this.createCell((short) colIndex++, row, visitOrder, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
				
				// 终端名称
				String storeName = storeInfo.get("STORE_ID") != null ? storeInfo.get("STORE_ID").toString() : "";
				if (storeInfo.get("STORE_NAME") != null) {
					storeName = storeName + "-" + storeInfo.get("STORE_NAME").toString();
				}

				// 2010-09-26 deli add 业代路线上的终端店内只要有业代填写特陈，终端店名前后加特殊符“◆” 				
				String flag = (((storeInfo.get("COUNT") != null) && (Integer.parseInt(storeInfo.get("COUNT").toString()) > 0)) 
						|| (storeInfo.get("HAS_SPECIAL") != null)) ? "◆" : "";
				
				this.createCell((short) colIndex++, row, flag + storeName + flag, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
				this.createCell((short) colIndex++, row, "", 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );				
				sheet.addMergedRegion(new Region(rowNo, (short) (colIndex - 2), rowNo, (short) (colIndex - 1)));
				
				// 2011-5-19 Deli add 终端属性（门店类型）				
				this.createCell((short) colIndex++, row, (String) storeInfo.get("STORE_TYPE"), 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
				
				// 负责人
				String storeOwenr = storeInfo.get("STORE_OWNER") != null ? storeInfo.get("STORE_OWNER").toString() : "";
				this.createCell((short) colIndex++, row, storeOwenr, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER) );
				
				// 电话:2010-07-16 Simonren modify 显示全部电话
				String storePhone = StringUtils.replace((String) storeInfo.get("PHONE"), "<BR>", "\n");
				this.createCell((short) colIndex++, row, storePhone, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
				
				// 地址
				String strAddress = storeInfo.get("ADDRESS") != null ? storeInfo.get("ADDRESS").toString() : "";
				this.createCell((short) colIndex++, row, strAddress, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
				this.createCell((short) colIndex++, row, "", 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
				this.createCell((short) colIndex++, row, "", 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT));
				
				sheet.addMergedRegion(new Region(rowNo, (short) (colIndex - 3), rowNo, (short) (colIndex - 1)));
				
				// 2010-07-29 Deli add 显示三级地
				String thirdLvName = storeInfo.get("THIRD_LV_NAME") != null ? storeInfo.get("THIRD_LV_NAME").toString() : "";
				this.createCell((short) colIndex++, row, thirdLvName, 
					this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
				
				// 四级地:2010-07-16 Simonren add 显示四级地
				String forthLvName = storeInfo.get("FORTH_LV_NAME") != null ? storeInfo.get("FORTH_LV_NAME").toString() : "";
				this.createCell((short) colIndex++, row, forthLvName, 
				this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_LEFT) );
	
				// 2013-04-02 mirabelle update 大配送拆分为大配送，大配送休闲，大配送乳饮
				// 2010-06-05 Deli add 若projectSid=4则显示县城特陈
				// 2010-09-27 deli add  大配送
				// 2011-04-11 Deli add 17(县城休闲)和18(县城乳饮)
//				if (StringUtils.equals("4", projectSid)
//						 || StringUtils.equals("1", projectSid)
//						 || StringUtils.equals("17", projectSid)
//						 || StringUtils.equals("18", projectSid)) {

				// 2013-04-22 mandy modify
//				String[] projSids = this.getTaskStoreProjSids().split(",");
				
				if(ArrayUtils.contains(projSids, projectSid)) {
				
					String count = storeInfo.get("COUNT").toString();
					
					// 县城特陈
					this.createCell((short) colIndex++, row, (StringUtils.equals("0", count) ? "" : count), 
						this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
				}
				else {
					
					// 循环显示乳品，饮品，休闲的资料
					// 定义循环显示业代路线，特陈的初始单元格索引 
					short customerType = (short) colIndex++;
					short specialCount = (short) colIndex;
					
					List<Map<String,Object>> lstMap = (List<Map<String,Object>>)storeInfo.get("TYPE_SPECIAL");
					for (int j=0; j<lstMap.size(); j++) {
						
						Map<String,Object> mapTemp = lstMap.get(j);
						
					  // 业代路线
						String strCustomerType = "";
						
						if ("Y".equals((String) mapTemp.get("STORE_DIVSION"))) {
							strCustomerType = "√";
						}
						
						this.createCell(customerType, row, strCustomerType, 
							this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
						
						// 特陈
						String strSpecialCount = mapTemp.get("SPECIAL_COUNT") != null ? mapTemp.get("SPECIAL_COUNT").toString() : "";
						this.createCell(specialCount, row, strSpecialCount, 
							this.getTitleStyleWrap(workBook, 10, "Y", HSSFCellStyle.ALIGN_CENTER));
						
						customerType+=2;
						specialCount+=2;
					}
				}
				
				rowNo++;
			}		
		}
		
		return rowNo;
	}
	
	/**
	 * <pre>
	 * 2010-3-24 Ryan
	 * 	列印签字.
	 * </pre>
	 * 
	 * @param rowNo
	 * @param workBook
	 * @param sheet
	 * @return
	 */
	public void printSignature (int rowNo,HSSFWorkbook workBook, HSSFSheet sheet) {

		HSSFRow	row= sheet.createRow(rowNo);
		this.createCell((short)0, row, "签字：", 
			this.getTitleStyle(workBook, 10, "", HSSFCellStyle.ALIGN_LEFT) );
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

		// 設定字體的大小
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

		// 單元格線
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
	public HSSFCellStyle getTitleStyleWrap(HSSFWorkbook workBook, int fontSize, 
	                                   String strHaveBorder, short align) {

		HSSFCellStyle style = workBook.createCellStyle();

		style.setAlignment(align);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

		// 設定字體的大小
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

		// 單元格線
		style.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
		style.setWrapText(true);

		return style;
	}
	
	/**
	 * <pre>
	 * 2010-3-4 A5KittyGuo
	 * 	在POI中设置单元格颜色: 灰色,字体大小,对齐方式.
	 * </pre>
	 * 
	 * @param workBook
	 * @param fontSize
	 * @param strHaveBorder
	 * @param align
	 * @return
	 */
	public HSSFCellStyle getGrayColorStyle(HSSFWorkbook workBook, int fontSize, 
	  	                                   String strHaveBorder, short align) {

		// 自定义颜色:灰色
		HSSFPalette palette = workBook.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.GREY_50_PERCENT.index, (byte) 144, (byte) 144, (byte) 144);
		
		HSSFCellStyle style = workBook.createCellStyle();
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		style.setAlignment(align);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

		// 設定字體的大小
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

		// 單元格線
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
	 * @return 传回 taskStoreProjSids。
	 */
	public String getTaskStoreProjSids() {
	
		return ProjectConfig.getInstance().getString("gh0101.prod.taskStore.projectSid");
	}
	
	/**
	 * @param taskStoreProjSids 要设定的 taskStoreProjSids。
	 */
	public void setTaskStoreProjSids(String taskStoreProjSids) {
	
		this.taskStoreProjSids = taskStoreProjSids;
	}	
}