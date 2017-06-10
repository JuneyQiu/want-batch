package com.want.batch.job.archive.syncbussgrade.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.archive.syncbussgrade.pojo.SalesAreaRel;

public class SalesAreaRelDao {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	public SalesAreaRel getByDivsionSid(String divsionSid,Connection connParam){
		SalesAreaRel salesAreaRel=null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select * from sales_area_rel where divsion_sid=?";

		try{
			pstmt = connParam.prepareStatement(sql);			
			pstmt.setString(1,divsionSid);
			rs = pstmt.executeQuery();
			if(rs.next()){
				salesAreaRel=new SalesAreaRel();

				salesAreaRel.setChannelId(rs.getString("CHANNEL_ID"));
				salesAreaRel.setChannelDesc(rs.getString("CHANNEL_DESC"));
				salesAreaRel.setProdGroupId(rs.getString("PROD_GROUP_ID"));
				salesAreaRel.setProdGroupDesc(rs.getString("PROD_GROUP_DESC"));
				salesAreaRel.setCreditId(rs.getString("CREDIT_ID"));
				salesAreaRel.setCreditDesc(rs.getString("CREDIT_DESC"));
				salesAreaRel.setStatus(rs.getString("STATUS"));
				salesAreaRel.setDivsionSid(rs.getString("DIVSION_SID"));
			}
		}catch(Exception e){
			logger.error("SalesAreaRelDao:getByDivsionSid--------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("SalesAreaRelDao:getByDivsionSid--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("SalesAreaRelDao:getByDivsionSid--------------"+e.getLocalizedMessage());
			}
		}
		return salesAreaRel;
	}
}
