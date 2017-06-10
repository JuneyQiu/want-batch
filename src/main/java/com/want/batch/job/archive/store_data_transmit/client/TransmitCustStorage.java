package com.want.batch.job.archive.store_data_transmit.client;

import java.sql.Connection;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.archive.store_data_transmit.service.StoreTransmitService;
import com.want.batch.job.archive.store_data_transmit.util.Toolkit;

/**
 * @author 00078588 库存历史数据转储
 */
@Component
public class TransmitCustStorage extends AbstractWantJob {
	protected final Log logger = LogFactory.getLog(this.getClass());
	public Connection iCustomerConn = null;
	public Connection historyConn = null;

	@Override
	public void execute() {
		iCustomerConn = getICustomerConnection();
		historyConn = getHistoryConnection();

		logger.info("TransmitCustStorage:execute---------start");
		transmitCustStorage(iCustomerConn, historyConn);
		copyCustomerStoreMarkTbl(iCustomerConn, historyConn);
		copyProdCustomerRelTbl(iCustomerConn, historyConn);
		copyProdSendCountTbl(iCustomerConn, historyConn);
		copySalesAreaRelTbl(iCustomerConn, historyConn);
		logger.info("TransmitCustStorage:execute---------end");
		close(iCustomerConn);
		close(historyConn);
	}

	// 复制SALES_AREA_REL表的数据
	private void copySalesAreaRelTbl(Connection custConn, Connection hisConn) {
		long startTimeMillis = System.currentTimeMillis();
		logger.info("TransmitCustStorage:execute---------开始复制SALES_AREA_REL表的数据");
		StoreTransmitService.getInstance()
				.copySalesAreaRelFromICustomerToHistory(custConn, hisConn);
		long endTimeMillis = System.currentTimeMillis();
		logger.info("TransmitCustStorage:execute---------复制SALES_AREA_REL表的数据完成");
		logger.info("TransmitCustStorage:execute---------耗时："
				+ Toolkit.timeTransfer(endTimeMillis - startTimeMillis));
	}

	// 复制PROD_SENDCOUNT_TBL表的数据
	private void copyProdSendCountTbl(Connection custConn, Connection hisConn) {
		long startTimeMillis = System.currentTimeMillis();
		logger.info("TransmitCustStorage:execute---------开始复制PROD_SENDCOUNT_TBL表的数据");
		StoreTransmitService.getInstance()
				.copyProdSendCountTblFromICustomerToHistory(custConn, hisConn);
		long endTimeMillis = System.currentTimeMillis();
		logger.info("TransmitCustStorage:execute---------复制PROD_SENDCOUNT_TBL表的数据完成");
		logger.info("TransmitCustStorage:execute---------耗时："
				+ Toolkit.timeTransfer(endTimeMillis - startTimeMillis));
	}

	// 复制PROD_CUSTOMER_REL表的数据
	private void copyProdCustomerRelTbl(Connection custConn, Connection hisConn) {
		long startTimeMillis = System.currentTimeMillis();
		logger.info("TransmitCustStorage:execute---------开始复制PROD_CUSTOMER_REL表的数据");
		StoreTransmitService.getInstance()
				.copyProdCustRelFromICustomerToHistory(custConn, hisConn);
		long endTimeMillis = System.currentTimeMillis();
		logger.info("TransmitCustStorage:execute---------复制PROD_CUSTOMER_REL表的数据完成");
		logger.info("TransmitCustStorage:execute---------耗时："
				+ Toolkit.timeTransfer(endTimeMillis - startTimeMillis));
	}

	// 复制CUSTOMER_STORE_MARK表的数据
	private void copyCustomerStoreMarkTbl(Connection custConn,
			Connection hisConn) {
		long startTimeMillis = System.currentTimeMillis();
		logger.info("TransmitCustStorage:execute---------开始复制CUSTOMER_STORE_MARK表的数据");
		StoreTransmitService.getInstance()
				.copyCustStoreMarkFromICustomerToHistory(custConn, hisConn);
		long endTimeMillis = System.currentTimeMillis();
		logger.info("TransmitCustStorage:execute---------复制CUSTOMER_STORE_MARK表的数据完成");
		logger.info("TransmitCustStorage:execute---------耗时："
				+ Toolkit.timeTransfer(endTimeMillis - startTimeMillis));
	}

	// 转移customer_storage_info_tbl表的历史数据
	private void transmitCustStorage(Connection custConn, Connection hisConn) {
		String yearMonth = getYearMonth();
		long startTimeMillis = System.currentTimeMillis();
		logger.info("TransmitCustStorage:execute---------开始转移customer_storage_info_tbl表"
				+ yearMonth + "的数据");
		StoreTransmitService.getInstance().transmitFromICustomerToHistory(
				yearMonth, custConn, hisConn);
		long endTimeMillis = System.currentTimeMillis();
		logger.info("TransmitCustStorage:execute---------转移customer_storage_info_tbl表"
				+ yearMonth + "的数据完成");
		logger.info("TransmitCustStorage:execute---------耗时："
				+ Toolkit.timeTransfer(endTimeMillis - startTimeMillis));
	}

	/**
	 * 返回要转移数据的年月
	 * 
	 * @return yyyyMM
	 */
	private static String getYearMonth() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;

		month -= 5;// 5个月前
		if (month <= 0) {
			year -= 1;
			month += 12;
		}
		return year + "" + (month < 10 ? "0" + month : month);
	}
}
