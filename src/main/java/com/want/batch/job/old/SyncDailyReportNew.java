package com.want.batch.job.old;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2010</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
@Component
public class SyncDailyReportNew extends AbstractWantJob {

    private int cityDays = 7;
    private int townDays = 7;
    
    @Autowired
    public SyncDailyReportRevert syncDailyReportRevert;

	@Override
    public void execute() {
    	DateTime dateTime = new DateTime();
    	String currentMonth = dateTime.toString("yyyyMM");
    	
    	//syncDailyReport(dateTime, false, true);
    	
    	int days = (cityDays > townDays) ? cityDays : townDays;
    	for (int i = 0; i < days && currentMonth.equals(dateTime.toString("yyyyMM")); i++) {
    		
        	syncDailyReport(dateTime, i < cityDays, i < townDays);
        	
        	// 执行下一天的日报
    		dateTime = dateTime.plusDays(1);
    	}
    }

	private void syncDailyReport(DateTime dateTime, boolean city, boolean town) {
		
		syncDailyReportRevert.set(dateTime.toString("yyyyMM"), dateTime.toString("dd"), dateTime.toString("dd"));
		syncDailyReportRevert.setDelete(true);
		syncDailyReportRevert.setCity(city);
		syncDailyReportRevert.setTown(town);

		//syncDailyReportRevert.setMailService(this.getMailService());
		//syncDailyReportRevert.setiCustomerJdbcOperations(this.getiCustomerJdbcOperations());
		//syncDailyReportRevert.setDataMartJdbcOperations(this.getDataMartJdbcOperations());
		syncDailyReportRevert.setBatchStatusFuncId(String.format("syncDailyReport(%s)", dateTime.toString("dd")));
		syncDailyReportRevert.executeBatch();
	}
	
	public void setCityDays(int cityDays) {
		this.cityDays = cityDays;
	}

	public void setTownDays(int townDays) {
		this.townDays = townDays;
	}

}
