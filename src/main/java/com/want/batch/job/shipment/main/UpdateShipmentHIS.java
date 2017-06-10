package com.want.batch.job.shipment.main;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.shipment.bo.VtwegSpartBO;
import com.want.batch.job.shipment.dao.UpdateHeWangShipmentHIS;
import com.want.batch.job.shipment.dao.VtwegSpartDAO;

/**
 * 
 * 把上月的出货放到HIS表中去 每月月初执行
 * */
@Component
public class UpdateShipmentHIS {
	protected final Log logger = LogFactory.getLog(UpdateShipmentHIS.class);

	@Autowired
	public VtwegSpartDAO vtwegSpartDAO;

	@Autowired
	public UpdateHeWangShipmentHIS updateHeWangShipmentHIS;

	public UpdateShipmentHIS() {
	}

	public static SimpleDateFormat sFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public void runStockHis() throws SQLException {
		logger.info("---start : 时间:"
				+ sFormat.format(new java.util.Date()));

		/** 1. 抓取销售范围和产品组 * */

		ArrayList<VtwegSpartBO> list = vtwegSpartDAO.getVtwegSpart();

		/** 2. 循环抓取所有销售范围和产品组的出货数据 * */
		for (int i = 0; i < list.size(); i++) {
			VtwegSpartBO vb = list.get(i);
			/** 3. 根据客户编码抓取出货数 * */
			if (vb.getFORWARDER_ID() != null) {
				updateHeWangShipmentHIS
						.getShipment(vb.getVTWEG(), vb.getSPART(),
								vb.getCOMPANY_ID(), vb.getFORWARDER_ID());
			}
			/** 4. 根据销售范围和产品组抓取所有客户所有品项出货数 * */
			else {
				updateHeWangShipmentHIS.getShipment(vb.getVTWEG(),
						vb.getSPART());
			}
		}
		// logger.info("---start : 时间:" + StartTime);
		logger.info("---over : 时间:"
				+ sFormat.format(new java.util.Date()));

	}
}
