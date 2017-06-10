
// ~ Package Declaration
// ==================================================

package com.want.batch.job.regulardeltchinfor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.regulardeltchinfor.dao.RegularDelTchInfoDao;
import com.want.batch.job.regulardeltchinfor.service.RegularDelTchInfoService;
import com.want.batch.job.storeprodseries.service.StoreProdSeriesService;


// ~ Comments
// ==================================================

@Component
public class RegularDelTchInfoJob extends AbstractWantJob {

  //~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================
	
	protected final Log logger = LogFactory.getLog(StoreProdSeriesService.class);
	
	@Autowired
	public RegularDelTchInfoService RegularDelTchInfoService;
	
	@Autowired
	public RegularDelTchInfoDao RegularDelTchInfoDao;

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================
	
	@Override
	public void execute() throws Exception {
		
		SimpleDateFormat dft = new SimpleDateFormat("yyyyMM");
	  Date beginDate = new Date();
	  Calendar date = Calendar.getInstance();
	  date.setTime(beginDate);
 	  date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
		
 	  logger.debug("开始执行@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+ date.getTime());
 	 
		this.RegularDelTchInfoService.deleteAndInsertImperfectTchInfo(dft.format(date.getTime()));
	}
}
