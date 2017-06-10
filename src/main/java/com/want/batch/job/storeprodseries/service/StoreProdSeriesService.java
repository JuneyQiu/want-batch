/**
 * 
 */
package com.want.batch.job.storeprodseries.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.reportproduce.pojo.Constant;
import com.want.batch.job.storeprodseries.dao.StoreProdSeriesDao;

/**
 * @author MandyZhang
 *
 */
@Component
public class StoreProdSeriesService {

	protected final Log logger = LogFactory.getLog(StoreProdSeriesService.class);
	
	@Autowired
	public StoreProdSeriesDao storeProdSeriesDao;
	
	@Autowired
	public DataSource sfa2DataSource;
	
	// 数据分批次插入的笔数
	public final static double DATA_NUMBER=60000;
	
	/**
	 * 获取原数据，将数据插入181中
	 * @throws Exception 
	 */
	public void insertStoreProdSeries(String yearMonth) throws Exception {
		
		logger.info("操作" + yearMonth + "的数据");
		
		Connection conn = null ;
		ResultSet rs = null;
		
		try {
			
			logger.info("删除数据 startTime" + new Date());
			this.storeProdSeriesDao.deleteStoreProdSeries(yearMonth);
			logger.info("删除数据 endTime" + new Date());
			
			logger.info("获取数据源");
			conn = sfa2DataSource.getConnection();
			
			logger.info("开始执行查询方法>>>>>>>>>>startTime" + new Date());
			
			rs = this.storeProdSeriesDao.findStoreProdSeries(conn, yearMonth);
			logger.info("查询方法执行结束>>>>>>>>>>endTime" + new Date());
			
			int count  = 0;
			
			List<Object[]> batchArgs = new ArrayList<Object[]>();
			
			logger.info("开始循环执行插入 startTime" + new Date());
			while (rs.next()) {
				
				Object[] obj = new Object[8];
				obj[0] = yearMonth + "01";
				obj[1] = "1";
				obj[2] = rs.getString("COMPANY_ID");
				obj[3] = rs.getString("BARNCH_ID");
				obj[4] = rs.getString("LV_5_ID");
				obj[5] = "00" + rs.getString("STORE_AREA_ID");
				obj[6] = rs.getString("RECOMMEND_ID");
				obj[7] = rs.getInt("EXHBIT_STANDARD");
				
				batchArgs.add(obj);
				count ++;
				
				// 每60000笔，批量新增一次
				if(batchArgs.size() == DATA_NUMBER) {
					
					this.storeProdSeriesDao.insertStoreProdSeries(batchArgs);
					
					batchArgs.clear();
				}
			}
			
			// 如果还有未新增的数据（不足60000笔的情况），则新增这些数据
			if(batchArgs.size() > 0) {
				
				this.storeProdSeriesDao.insertStoreProdSeries(batchArgs);
			}
			
			logger.info("循环执行插入 结束endTime" + new Date());
			
			logger.info("一共有  " + count + "  笔数据进行了插入");
		} 
		catch (Exception e) {
			
			logger.error(Constant.generateExceptionMessage(e));
			throw e;
		}
		finally {
			
			if(rs != null) {
				
				rs.close();
			}
			
			if(conn != null) {
				
				conn.close();
			}
		}
	}
}
