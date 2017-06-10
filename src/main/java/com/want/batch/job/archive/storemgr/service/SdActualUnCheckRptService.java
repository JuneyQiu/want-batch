package com.want.batch.job.archive.storemgr.service;

import java.sql.Connection;
import java.util.Date;

import com.want.batch.job.archive.storemgr.dao.SdActualUnCheckRptDao;


public class SdActualUnCheckRptService {
	private static SdActualUnCheckRptService instance=null;
	private static SdActualUnCheckRptDao sdActualUnCheckRptDao=null;
	
	private SdActualUnCheckRptService(){}
	public static SdActualUnCheckRptService getInstance(){
		if(instance==null) instance=new SdActualUnCheckRptService();
		return instance;
	}
	
	private SdActualUnCheckRptDao getSdActualUnCheckRptDao(){
		if(sdActualUnCheckRptDao==null) sdActualUnCheckRptDao=new SdActualUnCheckRptDao();
		return sdActualUnCheckRptDao;
	}
	
	public void transmitToSdActualNotCheckRptTbl(Date date,Connection conn){
		getSdActualUnCheckRptDao().transmitToSdActualNotCheckRptTbl(date,conn);
	}
	
	/**
	 * <pre>
	 * 2014-9-22 mirabelle
	 * bata版.
	 * </pre>	
	 * 
	 * @param date
	 * @param conn
	 */
	public void transmitToSdActualNotCheckRptBataTbl(Date date,Connection conn){
		getSdActualUnCheckRptDao().transmitToSdActualNotCheckRptBataTbl(date,conn);
	}
}
