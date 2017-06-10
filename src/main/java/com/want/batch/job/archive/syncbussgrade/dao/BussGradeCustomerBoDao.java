package com.want.batch.job.archive.syncbussgrade.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.want.batch.job.archive.syncbussgrade.pojo.BussGradeCustomerBo;

@Component
public class BussGradeCustomerBoDao {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	public List<BussGradeCustomerBo> loadAll_new(Connection conn){
		return searchByConditionStr("",conn);
	}

	private List<BussGradeCustomerBo> searchByConditionStr(String where_sql,Connection conn){
		List<BussGradeCustomerBo> bussGradeCustomerList=new ArrayList<BussGradeCustomerBo>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql="select distinct a.customer_id,a.branch_id,b.divsion_sid,c.company_id from knvv a,sales_area_rel b,company_branch_view c " +
				" where trim(a.sales_channel)=trim(b.channel_id) and trim(a.product)=trim(b.prod_group_id) and b.divsion_sid is not null " +
				" and a.branch_id=c.branch_id and type='1'  "+where_sql;

		try{
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				BussGradeCustomerBo bussGradeCustomer=new BussGradeCustomerBo();

				bussGradeCustomer.setCompanyId(rs.getString("company_id"));
				bussGradeCustomer.setBussPlaceId(rs.getString("branch_id"));
				bussGradeCustomer.setBussDptId(rs.getString("divsion_sid"));
				bussGradeCustomer.setCustAccount(rs.getString("customer_id"));
				bussGradeCustomerList.add(bussGradeCustomer);
			}
		}catch(Exception e){
			logger.error("BussGradeCustomerBoDao:searchByConditionStr--------------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("BussGradeCustomerBoDao:searchByConditionStr--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("BussGradeCustomerBoDao:searchByConditionStr--------------"+e.getLocalizedMessage());
			}
		}
		return bussGradeCustomerList;
	}
}
