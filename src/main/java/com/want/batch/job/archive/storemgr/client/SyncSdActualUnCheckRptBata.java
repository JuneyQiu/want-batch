package com.want.batch.job.archive.storemgr.client;

import java.sql.Connection;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.archive.storemgr.service.SdActualUnCheckRptService;

@Component
public class SyncSdActualUnCheckRptBata extends AbstractWantJob {
	protected final Log logger = LogFactory.getLog(this.getClass());
	public Connection conn = null;

	@Override
	public void execute() {
		try{
			conn = getICustomerConnection();
			logger.info("SyncSdActualUnCheckRptBata:execute------start");
			Calendar g=Calendar.getInstance();
			g.add(Calendar.DAY_OF_MONTH,-1);
			
			SdActualUnCheckRptService.getInstance().transmitToSdActualNotCheckRptBataTbl(g.getTime(),conn);

			logger.info("SyncSdActualUnCheckRptBata:execute------end");
		}catch(Exception e){
			logger.error("SyncSdActualUnCheckRptBata:execute------"+e.getLocalizedMessage());
		}finally{
			close(conn);
		}
	}
}
