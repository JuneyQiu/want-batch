package com.want.batch.job.stock_collect.util.store;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.want.batch.job.stock_collect.reportmgr.parseToExcel.StoreCollectReportExcel;
import com.want.batch.job.stock_collect.storemgr.dao.StoreDAO;
import com.want.batch.job.utils.ProjectConfig;

@Component
public class StoreBranchParallelTask {
	int linesPerPage = 65500;
	// log4j
	static Logger logger = Logger.getLogger(StoreBranchParallelTask.class.getName());

	@Autowired
	public DataSource hw09DataSource;

	@Autowired
	public StoreDAO storeDAO;

	@Async
	public Future<Boolean> run(Integer sid) {
		try {
			ArrayList StoreList = null;
			StoreCollectReportExcel sre = new StoreCollectReportExcel();

			// log4j
			logger.debug("Begin branch " + sid);
			StoreList = storeDAO.getStoreCollect(0, sid);
			logger.debug("Branch " + sid + " data ready");

			String fileName = ProjectConfig.getInstance().getString(
					"store.report")
					+ "Store-branch-" + sid + ".xls";

			FileOutputStream fOut = new FileOutputStream(fileName);

			HSSFWorkbook wb = new HSSFWorkbook();
			int pages = this.getPageNumbers(StoreList);

			for (int i = 0; i < pages; i++) {

				HSSFFont f = wb.createFont();
				f.setFontHeightInPoints((short) 12);

				HSSFCellStyle cs = wb.createCellStyle();
				cs.setFont(f);
				cs.setFillPattern(HSSFCellStyle.FINE_DOTS);

				HSSFSheet sheet = wb.createSheet();
				wb.setSheetName(i, (String.valueOf((i + 1))),
						HSSFWorkbook.ENCODING_COMPRESSED_UNICODE);
				// logger.debug("开始生成报表");
				sre.setSheet(fileName, StoreList, cs, sheet, i);
				// logger.debug("报表生成结束");
			}
			wb.write(fOut);
			fOut.flush();
			fOut.close();
			wb = null;
			StoreList.clear();
			logger.info("Complete branch " + sid + ", " + fileName);
			return new AsyncResult<Boolean>(Boolean.TRUE);
		} catch (Exception e) {
			logger.info("Branch " + sid + " fail, error: " + e.getMessage());
			return new AsyncResult<Boolean>(Boolean.FALSE);
		}
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
}
