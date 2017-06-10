package com.want.batch.job.stock_collect.util.store;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.want.batch.job.stock_collect.companymgr.bo.CompanyInfoBO;
import com.want.batch.job.stock_collect.reportmgr.parseToExcel.StoreCollectReportExcel;
import com.want.batch.job.stock_collect.storemgr.dao.StoreDAO;
import com.want.batch.job.utils.ProjectConfig;

@Component
public class StoreCompanyParallelTask {
	private static final int linesPerPage = 65500;

	private static Logger logger = Logger
			.getLogger(StoreCompanyParallelTask.class);

	@Autowired
	private StoreDAO storeDAO;

	@Async
	public Future<Boolean> run(CompanyInfoBO company) {

		try {
			int company_sid = company.getSID();

			logger.info("COMPANY SID( " + company.getSID() + ")"
					+ company.getPIN_YIN() + "BEGIN TO GET.");

			String Pin_Yin = company.getPIN_YIN();
			ArrayList StoreList = storeDAO.getStoreCollect(company_sid, 0);
			logger.debug("COMPANY SID( " + (company.getSID()) + ")"
					+ company.getPIN_YIN() + "END TO GET.");
			String fileName = ProjectConfig.getInstance().getString(
					"store.report")
					+ "Store-company-" + Pin_Yin + ".xls";
			FileOutputStream fOut = new FileOutputStream(fileName);
			HSSFWorkbook wb = new HSSFWorkbook();
			int pages = this.getPageNumbers(StoreList);
			// logger.info("pages: "+ pages);
			for (int i = 0; i < pages; i++) {

				HSSFFont f = wb.createFont();
				// f.setFontName("GB-2312");
				f.setFontHeightInPoints((short) 12);

				HSSFCellStyle cs = wb.createCellStyle();
				cs.setFont(f);
				cs.setFillPattern(HSSFCellStyle.FINE_DOTS);

				HSSFSheet sheet = wb.createSheet();
				wb.setSheetName(i, (String.valueOf((i + 1))),
						HSSFWorkbook.ENCODING_COMPRESSED_UNICODE);
				StoreCollectReportExcel.setSheet(fileName, StoreList, cs,
						sheet, i);
			}
			wb.write(fOut);
			fOut.flush();
			fOut.close();

			logger.info("COMPANY SID( " + company.getSID() + ") " + fileName
					+ " Done.");
			return new AsyncResult<Boolean>(Boolean.TRUE);
		} catch (Exception e) {
			logger.error("Company " + company.getSID() + " fail, error: " + e.getMessage());
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
