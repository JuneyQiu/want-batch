package com.want.batch.job.archive.store_data_transmit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.archive.store_data_transmit.pojo.SalesAreaRel;

public class SalesAreaRelDao {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	public void copySalesAreaRelFromICustomerToHistory(Connection iCustomerConn,Connection historyConn){
		int maxCount=1000;
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		String sql="select a.channel_id,a.channel_desc,a.prod_group_id,a.prod_group_desc,a.credit_id,a.credit_desc,a.status,a.divsion_sid " +
				" from SALES_AREA_REL a";
		
		try{
			if(deleteAllFromHistory(historyConn)==false) return;
			
			pstmt=iCustomerConn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			List<SalesAreaRel> sarList=new ArrayList<SalesAreaRel>();
			SalesAreaRel sar=null;
			while(rs.next()){
				sar=new SalesAreaRel();
				sar.setCHANNEL_ID(rs.getString("channel_id"));
				sar.setCHANNEL_DESC(rs.getString("channel_desc"));
				sar.setPROD_GROUP_ID(rs.getString("prod_group_id"));
				sar.setPROD_GROUP_DESC(rs.getString("prod_group_desc"));
				sar.setCREDIT_ID(rs.getString("credit_id"));
				sar.setCREDIT_DESC(rs.getString("credit_desc"));
				sar.setSTATUS(rs.getString("status"));
				sar.setDIVSION_SID(rs.getString("divsion_sid"));
				
				sarList.add(sar);
				if(sarList.size()>=maxCount){
					batchInsertIntoHistory(sarList,historyConn);
					sarList.clear();
				}
			}
			if(sarList.size()>0) batchInsertIntoHistory(sarList,historyConn);
		}catch(Exception e){
			logger.error("SalesAreaRelDao:copySalesAreaRelFromICustomerToHistory------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("SalesAreaRelDao:copySalesAreaRelFromICustomerToHistory--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("SalesAreaRelDao:copySalesAreaRelFromICustomerToHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
	public boolean batchInsertIntoHistory(List<SalesAreaRel> sarList,Connection historyConnParam){
		if(sarList==null||sarList.size()<=0) return false;
		PreparedStatement pstmt=null;

		String sql="insert into SALES_AREA_REL(Channel_Id,Channel_Desc,Prod_Group_Id,Prod_Group_Desc,Credit_Id,Credit_Desc,Status,Divsion_Sid) " +
				" values(?,?,?,?,?,?,?,?)";

		try{
			historyConnParam.setAutoCommit(false);
			pstmt=historyConnParam.prepareStatement(sql);
			for(int i=0;i<sarList.size();i++){
				SalesAreaRel sar=sarList.get(i);
				pstmt.setString(1,sar.getCHANNEL_ID());
				pstmt.setString(2,sar.getCHANNEL_DESC());
				pstmt.setString(3,sar.getPROD_GROUP_ID());
				pstmt.setString(4,sar.getPROD_GROUP_DESC());
				pstmt.setString(5,sar.getCREDIT_ID());
				pstmt.setString(6,sar.getCREDIT_DESC());
				pstmt.setString(7,sar.getSTATUS());
				pstmt.setString(8,sar.getDIVSION_SID());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			historyConnParam.commit();
			return true;
		}catch(Exception e){
			try{
				historyConnParam.rollback();
			}catch(SQLException e1){
				logger.error("SalesAreaRelDao:batchInsertIntoHistory------"+e1.getLocalizedMessage());
			}
			logger.error("SalesAreaRelDao:batchInsertIntoHistory------"+e.getLocalizedMessage());
			return false;
		}finally{
			try{
				historyConnParam.setAutoCommit(true);
			}catch(SQLException e){
				logger.error("SalesAreaRelDao:batchInsertIntoHistory------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("SalesAreaRelDao:batchInsertIntoHistory------"+e.getLocalizedMessage());
			}
		}
	}
	
	
	
	
	
	
	/**从SALES_AREA_REL表(Schema:ICustomer)中查出所有数据
	 * @param iCstomerConnParam
	 * @return
	 */
	public List<SalesAreaRel> findAllFromICustomer(Connection iCustomerConnParam){
		List<SalesAreaRel> list=new ArrayList<SalesAreaRel>();

		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="select a.channel_id,a.channel_desc,a.prod_group_id,a.prod_group_desc,a.credit_id,a.credit_desc,a.status,a.divsion_sid " +
				" from SALES_AREA_REL a";
		
		try{
			pstmt=iCustomerConnParam.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			SalesAreaRel sar=null;
			while(rs.next()){
				sar=new SalesAreaRel();
				sar.setCHANNEL_ID(rs.getString("channel_id"));
				sar.setCHANNEL_DESC(rs.getString("channel_desc"));
				sar.setPROD_GROUP_ID(rs.getString("prod_group_id"));
				sar.setPROD_GROUP_DESC(rs.getString("prod_group_desc"));
				sar.setCREDIT_ID(rs.getString("credit_id"));
				sar.setCREDIT_DESC(rs.getString("credit_desc"));
				sar.setSTATUS(rs.getString("status"));
				sar.setDIVSION_SID(rs.getString("divsion_sid"));
				list.add(sar);
			}
		}catch(Exception e){
			logger.error("SalesAreaRelDao:findAllFromICustomer------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("SalesAreaRelDao:findAllFromICustomer--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("SalesAreaRelDao:findAllFromICustomer--------------"+e.getLocalizedMessage());
			}
		}
		return list;
	}
	/**将数据保存到SALES_AREA_REL表(Schema:history)中
	 * @param sarList
	 */
	public void insertIntoHistory(SalesAreaRel sar,Connection historyConnParam){
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		String sql="insert into SALES_AREA_REL(Channel_Id,Channel_Desc,Prod_Group_Id,Prod_Group_Desc,Credit_Id,Credit_Desc,Status,Divsion_Sid) " +
				" values(?,?,?,?,?,?,?,?)";

		try{
			pstmt=historyConnParam.prepareStatement(sql);
			pstmt.setString(1,sar.getCHANNEL_ID());
			pstmt.setString(2,sar.getCHANNEL_DESC());
			pstmt.setString(3,sar.getPROD_GROUP_ID());
			pstmt.setString(4,sar.getPROD_GROUP_DESC());
			pstmt.setString(5,sar.getCREDIT_ID());
			pstmt.setString(6,sar.getCREDIT_DESC());
			pstmt.setString(7,sar.getSTATUS());
			pstmt.setString(8,sar.getDIVSION_SID());
			pstmt.execute();
		}catch(Exception e){
			logger.error("SalesAreaRelDao:insertIntoHistory------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("SalesAreaRelDao:insertIntoHistory--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("SalesAreaRelDao:insertIntoHistory--------------"+e.getLocalizedMessage());
			}
		}
	}

	/**将SALES_AREA_REL表(Schema:history)中的数据删光
	 * @param historyConnParam
	 */
	public boolean deleteAllFromHistory(Connection historyConnParam){
		PreparedStatement pstmt=null;
		String sql="delete from SALES_AREA_REL";
		
		try{
			pstmt=historyConnParam.prepareStatement(sql);
			pstmt.execute();
			return true;
		}catch(Exception e){
			logger.error("SalesAreaRelDao:deleteAllFromHistory------"+e.getLocalizedMessage());
			return false;
		}finally{
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("SalesAreaRelDao:deleteAllFromHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
}
