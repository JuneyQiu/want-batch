package com.want.batch.job.archive.storemgr.client;

import java.sql.Connection;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.archive.storemgr.service.SdActualUnCheckRptService;

@Component
public class SyncSdActualUnCheckRpt extends AbstractWantJob {
	protected final Log logger = LogFactory.getLog(this.getClass());
	public Connection conn = null;

	@Override
	public void execute() {
		try{
			conn = getICustomerConnection();
			logger.info("SyncSdActualUnCheckRpt:execute------start");
			Calendar g=Calendar.getInstance();
			g.add(Calendar.DAY_OF_MONTH,-1);
			
			SdActualUnCheckRptService.getInstance().transmitToSdActualNotCheckRptTbl(g.getTime(),conn);

			// Timothy: 移除这个呼叫，应该是错误的..，待余威确认。
//			SdActualUnCheckRptService.getInstance().transmitToSdActualNotCheckRptTbl(new Date(),conn);
			logger.info("SyncSdActualUnCheckRpt:execute------end");
		}catch(Exception e){
			logger.error("SyncSdActualUnCheckRpt:execute------"+e.getLocalizedMessage());
		}finally{
			close(conn);
		}
	}
}
