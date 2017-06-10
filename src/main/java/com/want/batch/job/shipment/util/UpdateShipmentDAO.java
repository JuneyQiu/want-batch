package com.want.batch.job.shipment.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.shipment.bo.VtwegSpartBO;
import com.want.batch.job.shipment.dao.RunLastMonthBaseStock;
import com.want.batch.job.shipment.dao.UpdateHeWangShipment;
import com.want.batch.job.shipment.dao.VtwegSpartDAO;
import com.want.batch.job.shipment.main.UpdateShipmentHIS;

/**
 * 抓取本月出货数,并同步到 WSIM_TC_TBL 表中
 * 
 * 
 * */
@Component
public class UpdateShipmentDAO extends AbstractWantJob {

	protected final Log logger = LogFactory.getLog(UpdateShipmentDAO.class);
	
	@Autowired
	public UpdateHeWangShipment updateHeWangShipment;
	
	@Autowired
	public VtwegSpartDAO vtwegSpartDAO;
	
	@Autowired
	public UpdateShipmentHIS updateShipmentHIS;
	
	@Autowired
	public CheckStockBaseNewFWD checkStockBaseNewFWD;
	
	@Autowired
	public UpdateStockTask updateStockTask;
	
	@Autowired
	public RunLastMonthBaseStock runLastMonthBaseStock;
	
	@Autowired
	public CheckStockBase checkStockBase;

	public static SimpleDateFormat sFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static DateFormater df = new DateFormater();
	public static String last_yearmonth = df.getLastYearMonth(df
			.getCurrentYearMonth());
	public static String before_yearmonth = df.getLastYearMonth(last_yearmonth);

	@Override
	public void execute() throws Exception {
		logger.info("---start : 时间:"
				+ sFormat.format(new java.util.Date()));
		String today = dFormat.format(new java.util.Date());
		String day = today.substring(8, 10);

		/** 每月前两天都运行抓取上月出货到历史表 */
		if ("01".equals(day)) {
			updateShipmentHIS.runStockHis();
		}
		/** 如果是每月1号 */
		if ("01".equals(day)) {
			logger.info("------开始做库存结转-------");

			// 结转配送客户////////////////////////////////////////////
//			runLastMonthBaseStock.delNextMonthStockHis(last_yearmonth);
//			runLastMonthBaseStock.insertWSIM_HIS(before_yearmonth);
//			runLastMonthBaseStock.doPeiSong(last_yearmonth, before_yearmonth);
//			// ///////////////////////////////////////////////////////

			runLastMonthBaseStock.run();
			checkStockBase.checkStockBase(); // 同步所有库存基数
			logger.info("------ 库存结转完成 -------");
		}
		logger.info("------开始抓取本月出货-------");
		/** 1. 抓取销售范围和产品组 * */

		logger.info("-----首先清空本月出货记录------");
		updateHeWangShipment.emptyShipMent();

		ArrayList<VtwegSpartBO> list = vtwegSpartDAO.getVtwegSpart();

		/** 2. 循环抓取所有销售范围和产品组的出货数据 * */
		for (int i = 0; i < list.size(); i++) {
			VtwegSpartBO vb = list.get(i);
			/** 3. 根据客户编码抓取出货数 * */
			if (vb.getFORWARDER_ID() != null) {
				updateHeWangShipment.getShipment(vb.getVTWEG(), vb.getSPART(),
						vb.getCOMPANY_ID(), vb.getFORWARDER_ID());
				logger.info("根据客户编码抓取出货数");
			}
			/** 4. 根据销售范围和产品组抓取所有客户所有品项出货数 * */
			else {
				updateHeWangShipment.getShipment(vb.getVTWEG(), vb.getSPART());
				logger.info("根据销售范围和产品组抓取所有客户所有品项出货数");
			}
		}
		logger.info("------ 本月出货抓取完成 ------");
		// logger.info("---start : 时间:" + StartTime);

		checkStockBaseNewFWD.checkStockBase(); // 同步昨日新增和修改过的配送乳饮客户库存基数

		logger.info("------ 开始同步当前库存 -------");
		updateStockTask.updateStock();
		logger.info("------ 当前库存同步完成 -------");
		logger.info("---over : 时间:"
				+ sFormat.format(new java.util.Date()));

	}

}
