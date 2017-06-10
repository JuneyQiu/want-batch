package com.want.batch.job.archive.syncbussgrade.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.archive.syncbussgrade.pojo.ProdInfo;

public class ProdInfoDao {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	//根据产品编号获得产品
	public ProdInfo queryById(String prodInfoId,Connection connParam){
		ProdInfo info=null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql="select a.PROD_ID,a.NAME1,substr(a.GROUP_TYPE_ID,1,6) as LINE_TYPE_ID from prod_info_tbl a where a.PROD_ID=?";
		try{
			pstmt=connParam.prepareStatement(sql);
			pstmt.setString(1,prodInfoId);
			rs = pstmt.executeQuery();

			if(rs.next()){
				info=new ProdInfo();
				info.setProdId(rs.getString("PROD_ID"));
				info.setProdName(rs.getString("NAME1"));
				info.setLineTypeId(rs.getString("LINE_TYPE_ID"));
			}
		}catch(Exception e){
			logger.error("ProdInfoDao:queryById----------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("ProdInfoDao:queryById--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("ProdInfoDao:queryById--------------"+e.getLocalizedMessage());
			}
		}
		return info;
	}

}
