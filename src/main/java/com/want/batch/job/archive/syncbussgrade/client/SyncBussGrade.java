package com.want.batch.job.archive.syncbussgrade.client;

import java.sql.Connection;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.archive.syncbussgrade.pojo.BussGradeCustomerBo;
import com.want.batch.job.archive.syncbussgrade.pojo.BussGradeTempBo;
import com.want.batch.job.archive.syncbussgrade.service.BussGradeService;
import com.want.batch.job.archive.syncbussgrade.util.Toolkit;

/**
 * @author 00078588
 * 同步客户业绩数据
 * 从SAP取数据存入ICUSTOMER.buss_grade_temp_tbl
 */
@Component
public class SyncBussGrade extends AbstractWantJob {
	protected final Log logger = LogFactory.getLog(this.getClass());
	public Connection conn = null;
	
	@Autowired
	public BussGradeService businessGradeService;

	@Override
	public void execute(){
		try{
			conn = getICustomerConnection();
			long startTimeMillis=System.currentTimeMillis();
			logger.info("SyncBussGrade:execute------开始导入......");
			///////////////////////////////////////////////////
			Calendar c=Calendar.getInstance();
			String curYear=c.get(Calendar.YEAR)+"";
			String curMonth=Toolkit.formatData(c.get(Calendar.MONTH)+1,"00");
			//得到所有客户
			logger.info("SyncBussGrade:execute------查询所有客户");
			List<BussGradeCustomerBo> bussGradeCustomerBoList=businessGradeService.searchAllBussGradeCustomerBo(conn);
			logger.info("SyncBussGrade:execute------导入每个客户的业绩，客户数量："+bussGradeCustomerBoList.size());
			for(int i=0;i<bussGradeCustomerBoList.size();i++){
				if(i%500==0)logger.info("SyncBussGrade:execute--------已完成数量："+i+"， 耗时："+Toolkit.timeTransfer(System.currentTimeMillis()-startTimeMillis));
				
				BussGradeCustomerBo bussGradeCustomerBo=bussGradeCustomerBoList.get(i);
			//	logger.info("SyncBussGrade:"+bussGradeCustomerBo.getCustAccount());
				//从SAP取得数据
				List<BussGradeTempBo> bussGradeTempBoList=businessGradeService.getBussGradeTempBoByCompanyBussDptCustAndDate(
						bussGradeCustomerBo.getCompanyId(), bussGradeCustomerBo.getBussDptId(), bussGradeCustomerBo.getCustAccount(), 
						curYear, curMonth,conn);
			//	logger.info("SyncBussGrade:businessGradeService.saveOrUpdate-Start");
				businessGradeService.saveOrUpdate(bussGradeTempBoList,conn);
			//	logger.info("SyncBussGrade:businessGradeService.saveOrUpdate-end");
			}
			///////////////////////////////////////////////////
			logger.info("SyncBussGrade:execute------导入完成");
			long endTimeMillis=System.currentTimeMillis();
			logger.info("SyncBussGrade:execute------共耗时："+Toolkit.timeTransfer(endTimeMillis-startTimeMillis));
		}catch(Exception e){
			logger.error("SyncBussGrade:execute------"+e.getLocalizedMessage());
		}finally{
			close(conn);
		}
	}
}
