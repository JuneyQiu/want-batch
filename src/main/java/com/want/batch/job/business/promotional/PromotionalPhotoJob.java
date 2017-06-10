package com.want.batch.job.business.promotional;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.utils.SapDAO;
import com.want.utils.SapQuery;

/**
 * @author 00078460 从sap同步促销品照片数据
 * 
 */ 
@Component("promotionalPhotoJob")
public class PromotionalPhotoJob extends AbstractWantJob {

	@Autowired
	public SapDAO sapDAO;

	@Override
	public void execute() throws Exception {
		logger.info(this.getMemoryInfo());
		/** 每天同步一次 **/
		HashMap<String, String> querydata = new HashMap<String, String>();
		SapQuery query = sapDAO.getSapQuery("ZRFCPH_MAT_MULT", "O_TRUC",
				querydata);

		if (query.getNumRows() > 0) {
			logger.info("ZRFCPH_MAT_MULT datas >>> " + query.getNumRows());
			this.getiCustomerJdbcOperations().update("delete from PROMOTIONAL_INFO");

			int batchSize = 100;
			ArrayList<Object[]> batchList = new ArrayList<Object[]>();
			
			for (int i = 0; i < query.getNumRows(); i++) {
				query.setIndex(i);
				batchList.add(new Object[] { query.getString("MATNR"),
						query.getString("MAKTX"), query.getString("MSEHL"),
						query.getString("ZMT_TIX"),
						query.getTimestamp("CR_DATE"),
						query.getBytes("ZMT_PIC") });
				
				if ((i%batchSize) == 0 || i == (query.getNumRows()-1)) {
					this.getiCustomerJdbcOperations().batchUpdate("insert into PROMOTIONAL_INFO(PROMOTIONAL_ID,NAME,UNIT,USED_PRINCIPLES,CREATE_DATE,PIC) values(?,?,?,?,?,?)", batchList);
					batchList = new ArrayList<Object[]>();
					logger.info("Insert PROMOTIONAL_INFO >>> " + i);
				}
			}
		}
		
		query.close();
	}
}
