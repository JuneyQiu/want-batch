package com.want.batch.job.stock_collect.reportmgr.parseToExcel;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.want.batch.job.stock_collect.channelmgr.bo.StoreChannelBO;
import com.want.batch.job.stock_collect.storemgr.bo.DirectBussinessBO;
import com.want.batch.job.stock_collect.storemgr.bo.StoreViewBO;
import com.want.batch.job.stock_collect.util.store.StoreCompanyParallelTask;

public class StoreCollectReportExcel {
	public StoreCollectReportExcel() {
	}

	private static int linesPerPage = 65500;
	private static Logger logger = Logger
	.getLogger(StoreCollectReportExcel.class);
	public static void setSheet(String title,  ArrayList list, HSSFCellStyle cs,
			HSSFSheet sheet, int page) {
		HSSFRow headerRow = sheet.createRow(0);
		createStringCell(headerRow, (short) 0, "分公司", cs);
		createStringCell(headerRow, (short) 1, "営业所", cs);
		createStringCell(headerRow, (short) 2, "三级地区", cs);
		createStringCell(headerRow, (short) 3, "城区乡镇", cs);
		createStringCell(headerRow, (short) 4, "终端编码", cs);
		createStringCell(headerRow, (short) 5, "终端名称", cs);
		createStringCell(headerRow, (short) 6, "面积", cs);
		createStringCell(headerRow, (short) 7, "地址", cs);
		createStringCell(headerRow, (short) 8, "地址1", cs);
		createStringCell(headerRow, (short) 9, "老板", cs);
		createStringCell(headerRow, (short) 10, "性别", cs);
		createStringCell(headerRow, (short) 11, "电话", cs);
		createStringCell(headerRow, (short) 12, "手机", cs);
		createStringCell(headerRow, (short) 13, "邮政编码", cs);
		createStringCell(headerRow, (short) 14, "有无冰箱", cs);
		createStringCell(headerRow, (short) 15, "终端类别", cs);
		createStringCell(headerRow, (short) 16, "备注", cs);
		createStringCell(headerRow, (short) 17, "营业状态", cs);
		createStringCell(headerRow, (short) 18, "标准市场", cs);
		createStringCell(headerRow, (short) 19, "建档日期", cs);
		createStringCell(headerRow, (short) 20, "最后更新日期", cs);
		createStringCell(headerRow, (short) 21, "位置类型", cs);
		createStringCell(headerRow, (short) 22, "销售方式", cs);
		createStringCell(headerRow, (short) 23, "收银机台数", cs);
		createStringCell(headerRow, (short) 24, "批发功能", cs);
		createStringCell(headerRow, (short) 25, "我司店招", cs);
		createStringCell(headerRow, (short) 26, "店招提供时间", cs);
		createStringCell(headerRow, (short) 27, "配送共线", cs);
		createStringCell(headerRow, (short) 28, "配送共线区域归属", cs);
		createStringCell(headerRow, (short) 29, "配送休闲", cs);
		createStringCell(headerRow, (short) 30, "配送休闲区域归属", cs);
		createStringCell(headerRow, (short) 31, "配送乳饮", cs);
		createStringCell(headerRow, (short) 32, "配送乳饮区域归属", cs);
		createStringCell(headerRow, (short) 33, "配送一线", cs);
		createStringCell(headerRow, (short) 34, "配送一线区域归属", cs);
		createStringCell(headerRow, (short) 35, "配送二线", cs);
		createStringCell(headerRow, (short) 36, "配送二线区域归属", cs);
		createStringCell(headerRow, (short) 37, "配送三线", cs);
		createStringCell(headerRow, (short) 38, "配送三线区域归属", cs);
		createStringCell(headerRow, (short) 39, "县城共线", cs);
		createStringCell(headerRow, (short) 40, "县城共线区域归属", cs);
		createStringCell(headerRow, (short) 41, "县城休闲", cs);
		createStringCell(headerRow, (short) 42, "县城休闲区域归属", cs);
		createStringCell(headerRow, (short) 43, "县城乳饮", cs);
		createStringCell(headerRow, (short) 44, "县城乳饮区域归属", cs);
		createStringCell(headerRow, (short) 45, "城区饮品", cs);
		createStringCell(headerRow, (short) 46, "饮品区域归属", cs);
		createStringCell(headerRow, (short) 47, "乳品共线", cs);
		createStringCell(headerRow, (short) 48, "乳品区域归属", cs);
		createStringCell(headerRow, (short) 49, "乳品利乐", cs);
		createStringCell(headerRow, (short) 50, "乳品利乐区域归属", cs);
		createStringCell(headerRow, (short) 51, "乳品铁罐", cs);
		createStringCell(headerRow, (short) 52, "乳品铁罐区域归属", cs);
		createStringCell(headerRow, (short) 53, "通路发展", cs);
		createStringCell(headerRow, (short) 54, "糕饼膨化", cs);
		createStringCell(headerRow, (short) 55, "糕饼膨化区域归属", cs);
		createStringCell(headerRow, (short) 56, "米果炒货", cs);
		createStringCell(headerRow, (short) 57, "米果炒货区域归属", cs);
		createStringCell(headerRow, (short) 58, "糖果果冻", cs);
		createStringCell(headerRow, (short) 59, "糖果果冻区域归属", cs);
		createStringCell(headerRow, (short) 60, "哎呦点心", cs);
		createStringCell(headerRow, (short) 61, "哎呦点心区域归属", cs);
		createStringCell(headerRow, (short) 62, "乳饮冰品", cs);
		createStringCell(headerRow, (short) 63, "冰品线区域归属", cs);
		createStringCell(headerRow, (short) 64, "现渠", cs);
		createStringCell(headerRow, (short) 65, "现渠区域归属", cs);
		createStringCell(headerRow, (short) 66, "直营", cs);
		createStringCell(headerRow, (short) 67, "直营区域归属", cs);
		createStringCell(headerRow, (short) 68, "特通", cs);
		createStringCell(headerRow, (short) 69, "特通区域归属", cs);
		createStringCell(headerRow, (short) 70, "冰箱数量", cs);
		createStringCell(headerRow, (short) 71, "旺旺配发冰箱数量", cs);
		createStringCell(headerRow, (short) 72, "经度", cs);
		createStringCell(headerRow, (short) 73, "纬度", cs);
		createStringCell(headerRow, (short) 74, "客户企业别", cs);
		createStringCell(headerRow, (short) 75, "统仓编码", cs);
		createStringCell(headerRow, (short) 76, "交易客户编码", cs);
		createStringCell(headerRow, (short) 77, "客户终端编码", cs);
		createStringCell(headerRow, (short) 78, "直营门店类型", cs);
		createStringCell(headerRow, (short) 79, "新终端编码", cs);
		int limit = (page + 1) * linesPerPage;
		limit = Math.min(limit, list.size());
		for (int i = page * linesPerPage; i < limit; i++) {
			HSSFRow dataRow = sheet.createRow(i + 1 - (page * linesPerPage));
			StoreViewBO vo = (StoreViewBO) list.get(i);
			createStringCell(dataRow, (short) 0, vo.getCOMPANY_NAME(),cs);
			createStringCell(dataRow, (short) 1, vo.getBRANCH_NAME(),cs);
			createStringCell(dataRow, (short) 2, vo.getTHIRD_LV_NAME(),cs);
			createStringCell(dataRow, (short) 3, vo.getFORTH_LV_NAME(),cs);
			createStringCell(dataRow, (short) 4, vo.getSTORE_ID(),cs);
			createStringCell(dataRow, (short) 5, vo.getSTORE_NAME(),cs);
			createStringCell(dataRow, (short) 6, vo.getACREAGE(),cs);
			createStringCell(dataRow, (short) 7, vo.getADDRESS(),cs);
			createStringCell(dataRow, (short) 8, vo.getADDRESS_NEW(),cs);
			createStringCell(dataRow, (short) 9 , vo.getSTORE_OWNER(),cs);
			createStringCell(dataRow, (short) 10 , vo.getOWNER_GENDER(),cs);
			createStringCell(dataRow, (short) 11, "-".equals(vo.getPHONE1())? "":vo.getPHONE1(),cs);
			createStringCell(dataRow, (short) 12, vo.getSTORE_MOBILE1(),cs);
			createStringCell(dataRow, (short) 13, vo.getPOST_CODE(),cs);
			createStringCell(dataRow, (short) 14, vo.getREFRIGERATOR(),cs);
			createStringCell(dataRow, (short) 15, vo.getSTORE_TYPE(),cs);
			createStringCell(dataRow, (short) 16, vo.getSTORE_DESC(),cs);
			createStringCell(dataRow, (short) 17, vo.getStatus(),cs);
			createStringCell(dataRow, (short) 18, vo.getMARKET_NAME(),cs);
			createStringCell(dataRow, (short) 19, vo.getCREATE_DATE(),cs);
			createStringCell(dataRow, (short) 20, vo.getUPDATE_DATE(),cs);
			String locationType="";
			if("0".equals(vo.getLOCATION_TYPE())){
				locationType="商圈";
			}else if("1".equals(vo.getLOCATION_TYPE())){
				locationType="社区";
			}else if("2".equals(vo.getLOCATION_TYPE())){
				locationType="学校";
			}else if("3".equals(vo.getLOCATION_TYPE())){
				locationType="其他";
			}
			createStringCell(dataRow, (short) 21, locationType,cs);
			String saleType="";
			if("0".equals(vo.getSALE_TYPE())){
				saleType="现销";
			}else if("1".equals(vo.getSALE_TYPE())){
				saleType="赊销";
			}
			createStringCell(dataRow, (short) 22, saleType,cs);
			createStringCell(dataRow, (short) 23, vo.getCASHREGISTER_AMOUNT(),cs);
			String  isWholeSale="";
			if("0".equals(vo.getIS_WHOLESALE())){
				isWholeSale="否";
			}else if("1".equals(vo.getIS_WHOLESALE())){
				isWholeSale="是";
			}
			createStringCell(dataRow, (short) 24, isWholeSale,cs);
			String dianZhao="";
			if("0".equals(vo.getDIANZHAO())){
				dianZhao="否";
			}else if("1".equals(vo.getDIANZHAO())){
				dianZhao="是";
			}
			createStringCell(dataRow, (short) 25, dianZhao,cs);
			createStringCell(dataRow, (short) 26, vo.getPROVIDE_DATE(),cs);
			StoreChannelBO scb = vo.getScb();
			createStringCell(dataRow, (short) 27, scb.isHW01() ? "√" : "",cs);
			createStringCell(dataRow, (short) 28, scb.getAREA001(),cs);
			createStringCell(dataRow, (short) 29, scb.isHW22() ? "√" : "",cs);
			createStringCell(dataRow, (short) 30, scb.getAREA022(),cs);
			createStringCell(dataRow, (short) 31, scb.isHW23() ? "√" : "",cs);
			createStringCell(dataRow, (short) 32, scb.getAREA023(),cs);
			createStringCell(dataRow, (short) 33, scb.isHW32() ? "√" : "",cs);
			createStringCell(dataRow, (short) 34, scb.getAREA032(),cs);
			createStringCell(dataRow, (short) 35, scb.isHW33() ? "√" : "",cs);
			createStringCell(dataRow, (short) 36, scb.getAREA033(),cs);
			createStringCell(dataRow, (short) 37, scb.isHW34() ? "√" : "",cs);
			createStringCell(dataRow, (short) 38, scb.getAREA034(),cs);
			createStringCell(dataRow, (short) 39, scb.isHW04() ? "√" : "",cs);
			createStringCell(dataRow, (short) 40, scb.getAREA004(),cs);
			createStringCell(dataRow, (short) 41, scb.isHW17() ? "√" : "",cs);
			createStringCell(dataRow, (short) 42, scb.getAREA017(),cs);
			createStringCell(dataRow, (short) 43, scb.isHW18() ? "√" : "",cs);
			createStringCell(dataRow, (short) 44, scb.getAREA018(),cs);
			createStringCell(dataRow, (short) 45, scb.isHW10() ? "√" : "",cs);
			createStringCell(dataRow, (short) 46, scb.getAREA010(),cs);
			createStringCell(dataRow, (short) 47, scb.isHW12() ? "√" : "",cs);
			createStringCell(dataRow, (short) 48, scb.getAREA012(),cs);
			createStringCell(dataRow, (short) 49, scb.isHW27() ? "√" : "",cs);
			createStringCell(dataRow, (short) 50, scb.getAREA027() ,cs);
			createStringCell(dataRow, (short) 51, scb.isHW28() ? "√" : "",cs);
			createStringCell(dataRow, (short) 52, scb.getAREA028() ,cs);
			createStringCell(dataRow, (short) 53, scb.isHW011() ? "√" : "",cs);
			createStringCell(dataRow, (short) 54, scb.isHW15() ? "√" : "",cs);
			createStringCell(dataRow, (short) 55, scb.getAREA015(),cs);
			createStringCell(dataRow, (short) 56, scb.isHW25() ? "√" : "",cs);
			createStringCell(dataRow, (short) 57, scb.getAREA025(),cs);
			createStringCell(dataRow, (short) 58, scb.isHW26() ? "√" : "",cs);
			createStringCell(dataRow, (short) 59, scb.getAREA026(),cs);
			createStringCell(dataRow, (short) 60, scb.isHW30() ? "√" : "",cs);
			createStringCell(dataRow, (short) 61, scb.getAREA030(),cs);		
			createStringCell(dataRow, (short) 62, scb.isHW016() ? "√" : "",cs);
			createStringCell(dataRow, (short) 63, scb.getAREA016(),cs);
			createStringCell(dataRow, (short) 64, scb.isHW020() ? "√" : "",cs);
			createStringCell(dataRow, (short) 65, scb.getAREA020(),cs);
			createStringCell(dataRow, (short) 66, scb.isHW024() ? "√" : "",cs);
			createStringCell(dataRow, (short) 67, scb.getAREA024(),cs);
			createStringCell(dataRow, (short) 68, scb.isHW31() ? "√" : "",cs);
			createStringCell(dataRow, (short) 69, scb.getAREA031(),cs);
			createStringCell(dataRow, (short) 70, String.valueOf((vo.getFRIDGE_COUNT() != null) ? vo.getFRIDGE_COUNT(): ""),cs);
			createStringCell(dataRow, (short) 71, String.valueOf((vo.getWANT_FRIDGE_COUNT() != null) ? vo.getWANT_FRIDGE_COUNT() : ""),cs);
			createStringCell(dataRow, (short) 72, (null==vo.getLONGITUDE())? "" : vo.getLONGITUDE()+"",cs);
			createStringCell(dataRow, (short) 73, (null==vo.getLATITUDE())? "" : vo.getLATITUDE()+"",cs);
			DirectBussinessBO directBo = vo.getDirectBo();
			if(null != directBo){
				createStringCell(dataRow, (short) 74, directBo.getBussinessId()+"-"+directBo.getBussinessName(),cs);
				createStringCell(dataRow, (short) 75, directBo.getKeyAccountId(),cs);
				createStringCell(dataRow, (short) 76, directBo.getErpId(),cs);
				createStringCell(dataRow, (short) 77, directBo.getCUSTSTORE_ID(),cs);
				createStringCell(dataRow, (short) 78, directBo.getRcStoreType(),cs);
			}
			createStringCell(dataRow, (short) 79, vo.getMDM_STORE_ID(),cs);
			if (i > 0) {
				list.set(i - 1, null);
			}
		}
		int rowNum=sheet.getLastRowNum();//获得总行数
		logger.info("插入excel的总行数rowNum :"+rowNum);
	}

	  private int getPageNumbers(ArrayList list) {
			if (list != null && list.size() > linesPerPage) {
				int segments = list.size() / linesPerPage;
				if ((list.size() % linesPerPage) > 0) {
					segments = segments + 1;
				}
				return segments;
			}
			return 1;
		}
	private static void createStringCell(HSSFRow row, short index,
			String value, HSSFCellStyle cs) {
		HSSFCell cell = row.createCell(index);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cs.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cell.setCellValue(value);

	}
	private static void createIntCell(HSSFRow row, short index, int value,
			HSSFCellStyle cs) {
		HSSFCell cell = row.createCell(index);
		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(value);
		// cell.setCellStyle(cs);
	}

	private static void createNumericCell(HSSFRow row, short index,
			BigDecimal value, HSSFCellStyle cs) {
		HSSFCell cell = row.createCell(index);
		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(value.doubleValue());
		// cell.setCellStyle(cs);
	}

}
