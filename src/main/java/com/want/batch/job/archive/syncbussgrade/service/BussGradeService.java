package com.want.batch.job.archive.syncbussgrade.service;

import java.sql.Connection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.archive.syncbussgrade.dao.BussGradeCustomerBoDao;
import com.want.batch.job.archive.syncbussgrade.dao.BussGradeTempBoDao;
import com.want.batch.job.archive.syncbussgrade.pojo.BussGradeCustomerBo;
import com.want.batch.job.archive.syncbussgrade.pojo.BussGradeTempBo;

@Component
public class BussGradeService {

	@Autowired
	private BussGradeCustomerBoDao bussGradeCustomerBoDao;

	@Autowired
	private BussGradeTempBoDao bussGradeTempBoDao;

	private BussGradeService(){}
	
	public List<BussGradeCustomerBo> searchAllBussGradeCustomerBo(Connection conn){
		return bussGradeCustomerBoDao.loadAll_new(conn);
	}
	//根据 分公司Id+事业部Id+客户编号+年+月从SAP查询业绩
	//日期格式为“######”
	public List<BussGradeTempBo> getBussGradeTempBoByCompanyBussDptCustAndDate(
			String companyId, String bussDptId, String custAccount,
			String year, String month,Connection conn) {
		return bussGradeTempBoDao.getBussGradeTempBoByCompanyBussDptCustAndDate(companyId, bussDptId, 
				custAccount, year, month,conn);
	}
	//保存或更新数据到buss_grade_temp_tml表中
	public boolean saveOrUpdate(List<BussGradeTempBo> boList,Connection conn){
		return bussGradeTempBoDao.saveOrUpdate(boList,conn);
	}
}
