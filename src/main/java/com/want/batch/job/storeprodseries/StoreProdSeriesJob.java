/**
 * 
 */
package com.want.batch.job.storeprodseries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.storeprodseries.dao.SchedulerExcutionflagDao;
import com.want.batch.job.storeprodseries.service.StoreProdSeriesService;

/**
 * @author MandyZhang
 * 
 * 查询65 DB数据源，将查询结果插入181DB STORE_PROD_SERIES表中
 * 该排程每月的第一天执行一次
 *
 */
@Component
public class StoreProdSeriesJob extends AbstractWantJob {

	@Autowired
	public StoreProdSeriesService storeProdSeriesService;
	
	@Autowired
	public SchedulerExcutionflagDao schedulerExcutionflagDao;
	
	public static final String SCHEDULEREXCUTIONFLAG_EXCUTE_FLAG_Y = "Y";
	public static final String SCHEDULEREXCUTIONFLAG_EXCUTE_FLAG_N = "N";
	
	@Override
	public void execute() throws Exception {
		
		// 判断该排程是否执行
		Map<String, Object> map = this.schedulerExcutionflagDao.getFlagByFunctionId("StoreProdSeriesJob");
		
		Calendar nowCal = Calendar.getInstance();
		Date nowDate = nowCal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		
		// 当每月1号时排程自动执行，否则，每天执行时先查询db是否执行的标记
		if("01".equals(sdf.format(nowDate))) {
			
			logger.info("StoreProdSeriesJob:startTime >>>>>>>" + new Date());
			
			this.storeProdSeriesService.insertStoreProdSeries(new SimpleDateFormat("yyyyMM").format(nowDate));
			
			logger.info("StoreProdSeriesJob:endTime >>>>>>>>>>>" + new Date());
			
			// 排程执行完成，修改排程状态为“N”
			this.schedulerExcutionflagDao.updateByFunctionId("StoreProdSeriesJob");
		}
		else {
		
			if(map.get("EXCUTE_FLAG").equals(SCHEDULEREXCUTIONFLAG_EXCUTE_FLAG_Y)) {
			
				logger.info("StoreProdSeriesJob:startTime >>>>>>>" + new Date());
				
				this.storeProdSeriesService.insertStoreProdSeries(map.get("PARAM").toString());
				
				logger.info("StoreProdSeriesJob:endTime >>>>>>>>>>>" + new Date());
				
				// 排程执行完成，修改排程状态为“N”
				this.schedulerExcutionflagDao.updateByFunctionId("StoreProdSeriesJob");
			}
		}
	}
}
