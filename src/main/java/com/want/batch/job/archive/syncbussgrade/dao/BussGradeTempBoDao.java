package com.want.batch.job.archive.syncbussgrade.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.archive.syncbussgrade.pojo.BussGradeTempBo;
import com.want.batch.job.archive.syncbussgrade.pojo.ProdInfo;
import com.want.batch.job.archive.syncbussgrade.pojo.SalesAreaRel;
import com.want.utils.SapDAO;

@Component
public class BussGradeTempBoDao {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	public SapDAO sapDAO;

	ProdInfoDao prodInfoDao=new ProdInfoDao();
	SalesAreaRelDao salesAreaRelDao=new SalesAreaRelDao();

	private void save(BussGradeTempBo bo,Connection connParam){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql="insert into buss_grade_temp_tbl(COMPANY_ID,DIVISION_ID,CUSTOMER_ACCOUNT,YEAR,MONTH,LINE_TYPE_ID" +
				",CHECKED_AMOUNT,UN_CHECKED_AMOUNT) values(?,?,?,?,?,?,?,?)";
		
		try{
			pstmt=connParam.prepareStatement(sql);
			pstmt.setString(1, bo.getCompanyId());
			pstmt.setString(2, bo.getBussDptId());
			pstmt.setString(3, bo.getCustomerAccount());
			pstmt.setString(4, bo.getYear());
			pstmt.setString(5, bo.getMonth());
			pstmt.setString(6, bo.getLineTypeId());
			pstmt.setLong(7, bo.getCheckedAmount());
			pstmt.setLong(8, bo.getUnCheckedAmount());
			pstmt.execute();
		}catch(Exception e){
			logger.error("BussGradeTempBoDao:save------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("BussGradeTempBoDao:save--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("BussGradeTempBoDao:save--------------"+e.getLocalizedMessage());
			}
		}
	}
	private void update(BussGradeTempBo bo,Connection connParam){
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql="update buss_grade_temp_tbl set CHECKED_AMOUNT=?,UN_CHECKED_AMOUNT=? where COMPANY_ID=? and DIVISION_ID=? " +
				" and CUSTOMER_ACCOUNT=? and YEAR=? and MONTH=? and LINE_TYPE_ID=?";
		
		try{
			pstmt=connParam.prepareStatement(sql);
			pstmt.setLong(1, bo.getCheckedAmount());
			pstmt.setLong(2, bo.getUnCheckedAmount());
			pstmt.setString(3, bo.getCompanyId());
			pstmt.setString(4, bo.getBussDptId());
			pstmt.setString(5, bo.getCustomerAccount());
			pstmt.setString(6, bo.getYear());
			pstmt.setString(7, bo.getMonth());
			pstmt.setString(8, bo.getLineTypeId());
			pstmt.execute();
		}catch(Exception e){
			logger.error("BussGradeTempBoDao:update------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("BussGradeTempBoDao:update--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("BussGradeTempBoDao:update--------------"+e.getLocalizedMessage());
			}
		}
	}
	public boolean saveOrUpdate(List<BussGradeTempBo> boList,Connection conn){
		try{
			for(int i=0;i<boList.size();i++){
				BussGradeTempBo bo=boList.get(i);
				try{
					BussGradeTempBo oldBo=queryByCompanyBussDptCustType2AndDate(bo,conn);
					if(oldBo==null) save(bo,conn);
					else update(bo,conn);
				}catch(Exception e){
					logger.error(e.getMessage());
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		return true;
	}
	private BussGradeTempBo queryByCompanyBussDptCustType2AndDate(BussGradeTempBo boParam,Connection connParam){
		BussGradeTempBo bo=null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql="select * from buss_grade_temp_tbl where COMPANY_ID=? and DIVISION_ID=? and   " +
				" CUSTOMER_ACCOUNT=? and YEAR=? and MONTH=? and LINE_TYPE_ID=? ";

		try{
			pstmt=connParam.prepareStatement(sql);
			pstmt.setString(1,boParam.getCompanyId());
			pstmt.setString(2,boParam.getBussDptId());
			pstmt.setString(3,boParam.getCustomerAccount());
			pstmt.setString(4,boParam.getYear());
			pstmt.setString(5,boParam.getMonth());
			pstmt.setString(6,boParam.getLineTypeId());
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				bo=new BussGradeTempBo();
				bo.setCompanyId(boParam.getCompanyId());
				bo.setBussDptId(boParam.getBussDptId());
				bo.setCustomerAccount(boParam.getCustomerAccount());
				bo.setYear(boParam.getYear());
				bo.setMonth(boParam.getMonth());
				bo.setLineTypeId(boParam.getLineTypeId());				
				bo.setCheckedAmount(Long.parseLong(rs.getString("CHECKED_AMOUNT")));
				bo.setUnCheckedAmount(Long.parseLong(rs.getString("UN_CHECKED_AMOUNT")));
			}
		}catch(Exception e){
			logger.error("BussGradeTempBoDao:queryByCompanyBussDptCustType2AndDate------------"+e.getMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("BussGradeTempBoDao:queryByCompanyBussDptCustType2AndDate--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("BussGradeTempBoDao:queryByCompanyBussDptCustType2AndDate--------------"+e.getLocalizedMessage());
			}
		}
		return bo;
	}
	//根据 分公司Id+事业部Id+客户编号+年+月从SAP查询业绩
	//日期格式为“######”
	public List<BussGradeTempBo> getBussGradeTempBoByCompanyBussDptCustAndDate(
			String companyId, String bussDptId, String custAccount,
			String year, String month,Connection conn) {
		List<BussGradeTempBo> boList=new ArrayList<BussGradeTempBo>();
		logger.info("from SyncBussGrade -> BussGradeTempBoDao:Step1->"+custAccount);
		try{
			SalesAreaRel salesAreaRel=salesAreaRelDao.getByDivsionSid(bussDptId,conn);
			salesAreaRel=(salesAreaRel==null)?new SalesAreaRel():salesAreaRel;
			/////////////生成查询起止时间-----------开始
			String yearMonthBeginDay=year+month+"01";
			Calendar cDate=new GregorianCalendar();
			//设置年月日，日随便设一个就可以
			cDate.set(Integer.parseInt(year),Integer.parseInt(month)-1,01);		
			//获得月度最后一天
			int lastday = cDate.getActualMaximum(Calendar.DAY_OF_MONTH);
			String yearMonthEndDay =year+month+lastday;
			/////////////生成查询起止时间-----------结束
			/////////////////////获得“开单已过账金额”---------开始
			HashMap<String,String> querydataforpo = new HashMap<String,String>(6);
			querydataforpo.put("ZVKORG", companyId+"1");
			querydataforpo.put("ZKUNNR", custAccount);
			querydataforpo.put("ZWADATF", yearMonthBeginDay);
			querydataforpo.put("ZWADATT", yearMonthEndDay);
			querydataforpo.put("ZVTWEG", salesAreaRel.getChannelId().trim());
			querydataforpo.put("ZSPART", salesAreaRel.getProdGroupId().trim());
			logger.info("from SyncBussGrade -> BussGradeTempBoDao:Step2->"+custAccount);
			ArrayList GZlist =getZHGZ(querydataforpo);
			logger.info("from SyncBussGrade -> BussGradeTempBoDao:Step3->"+custAccount);
			if(GZlist!=null &&GZlist.size()!=0){
				for(int i =0;i<GZlist.size();i++){
					HashMap temphm = (HashMap) GZlist.get(i);
					String prodId =temphm.get("MATNR").toString();
					ProdInfo prodInfo=prodInfoDao.queryById(prodId,conn);
					
					if(prodInfo!=null){
						BussGradeTempBo bo=new BussGradeTempBo();
						bo.setCompanyId(companyId);
						bo.setBussDptId(bussDptId);
						bo.setCustomerAccount(custAccount);
						bo.setYear(year);
						bo.setMonth(month);
						bo.setLineTypeId(prodInfo.getLineTypeId());
						bo.setCheckedAmount(Double.valueOf(temphm.get("KZWI1").toString()).longValue());
		
						boList.add(bo);
					}
				}
			}
			///////////////////获得“开单已过账金额”---------结束
			/////////////////////获得“开单未过账金额”---------开始
			logger.info("from SyncBussGrade -> BussGradeTempBoDao:Step4->"+custAccount);
			ArrayList WGZlist =getZHWGZ(querydataforpo);
			logger.info("from SyncBussGrade -> BussGradeTempBoDao:Step5->"+custAccount);
			if(WGZlist!=null &&WGZlist.size()!=0){
				for(int i =0;i<WGZlist.size();i++){
					HashMap temphm = (HashMap) WGZlist.get(i);
					String prodId =temphm.get("MATNR").toString();
					ProdInfo prodInfo=prodInfoDao.queryById(prodId,conn);
	
					if(prodInfo!=null){
						BussGradeTempBo bo=new BussGradeTempBo();
						bo.setCompanyId(companyId);
						bo.setBussDptId(bussDptId);
						bo.setCustomerAccount(custAccount);
						bo.setYear(year);
						bo.setMonth(month);
						bo.setLineTypeId(prodInfo.getLineTypeId());
						bo.setUnCheckedAmount(Double.valueOf(temphm.get("KZWI1").toString()).longValue());
		
						boList.add(bo);
					}
				}
			}
			logger.info("from SyncBussGrade -> BussGradeTempBoDao:Step6->"+custAccount);
		}catch(Exception e) {
			logger.error("BussGradeTempBoDao:getBussGradeTempBoByCompanyBussDptCustAndDate--------"+e.getLocalizedMessage());
		}
		/////////////////////获得“开单未过账金额”---------结束
		logger.info("from SyncBussGrade -> BussGradeTempBoDao:END->"+custAccount+"--------------------------------------------------------------");
		return parseBussGradeTempBoList(boList);
	}
	//按分公司Id+事业部Id+客户帐号+年份+月份+小批线分组合并
	private List<BussGradeTempBo> parseBussGradeTempBoList(List<BussGradeTempBo> list){
	//	logger.info("from SyncBussGrade -> BussGradeTempBoDao.parseBussGradeTempBoList:Start->");
		List<BussGradeTempBo> l=getCompDptCustYMType2List(list);
		for(int i=0;i<l.size();i++){
			BussGradeTempBo bo=l.get(i);
			List<BussGradeTempBo> rawBoList=getBussGradeTempBoFromList(list,bo);
			if(rawBoList!=null&&rawBoList.size()>0){
				for(int j=0;j<rawBoList.size();j++){
					BussGradeTempBo rawBo=rawBoList.get(j);
					bo.addCheckedAmount(rawBo.getCheckedAmount());
					bo.addUnCheckedAmount(rawBo.getUnCheckedAmount());
				}
			}
		}
	//	logger.info("from SyncBussGrade -> BussGradeTempBoDao.parseBussGradeTempBoList:end->");
		
		return l;
	}
	private List<BussGradeTempBo> getCompDptCustYMType2List(List<BussGradeTempBo> list){
		List<BussGradeTempBo> l=new ArrayList<BussGradeTempBo>();
		for(int i=0;i<list.size();i++){
			BussGradeTempBo bo=list.get(i);
			
			BussGradeTempBo temp=new BussGradeTempBo();
			temp.setCompanyId(bo.getCompanyId());
			temp.setBussDptId(bo.getBussDptId());
			temp.setCustomerAccount(bo.getCustomerAccount());
			temp.setLineTypeId(bo.getLineTypeId());
			temp.setYear(bo.getYear());
			temp.setMonth(bo.getMonth());
			
			List<BussGradeTempBo> rawTempList=getBussGradeTempBoFromList(l,temp);
			if(rawTempList==null||rawTempList.size()==0) l.add(temp);
		}
		return l;
	}
	private List<BussGradeTempBo> getBussGradeTempBoFromList(List<BussGradeTempBo> boList,BussGradeTempBo bo){
		List<BussGradeTempBo> l=new ArrayList<BussGradeTempBo>();
		for(int i=0;i<boList.size();i++){
			BussGradeTempBo tempBo=boList.get(i);
			if(tempBo.equals(bo)) l.add(tempBo);
		}
		return l;
	}
	public ArrayList getZHGZ(HashMap querydata) {
		return sapDAO.getSAPData4("ZRFC08","ZST08",querydata);
	  }
	public ArrayList getZHWGZ(HashMap querydata) {
		return sapDAO.getSAPData4("ZRFC08","ZST08_NO",querydata);
	}
}
