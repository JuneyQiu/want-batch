package com.want.batch.job.archive.store_data_transmit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.archive.store_data_transmit.pojo.CustStoreMark;

public class CustStoreMarkDao {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	public void copyCustStoreMarkFromICustomerToHistory(Connection iCustomerConn,Connection historyConn){
		int maxCount=1000;
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		String sql="select a.customer_id,a.company_id,a.channel_id,a.prod_group_id,a.credit_id,a.product_id,a.ismark," +
				"a.record_date from CUSTOMER_STORE_MARK a";
		
		try{
			if(deleteAllFromHistory(historyConn)==false) return;

			pstmt=iCustomerConn.prepareStatement(sql);
			rs=pstmt.executeQuery();

			List<CustStoreMark> csmList=new ArrayList<CustStoreMark>();
			CustStoreMark csm=null;
			while(rs.next()){
				csm=new CustStoreMark();

				csm.setCUSTOMER_ID(rs.getString("customer_id"));
				csm.setCOMPANY_ID(rs.getString("company_id"));
				csm.setCHANNEL_ID(rs.getString("channel_id"));
				csm.setPROD_GROUP_ID(rs.getString("prod_group_id"));
				csm.setCREDIT_ID(rs.getString("credit_id"));
				csm.setPRODUCT_ID(rs.getString("product_id"));
				csm.setISMARK(rs.getString("ismark"));
				csm.setRECORD_DATE(rs.getString("record_date"));
				
				csmList.add(csm);
				if(csmList.size()>=maxCount){
					batchInsertIntoHistory(csmList,historyConn);
					csmList.clear();
				}
			}
			if(csmList.size()>0) batchInsertIntoHistory(csmList,historyConn);
		}catch(Exception e){
			logger.error("CustStoreMarkDao:copyCustStoreMarkFromICustomerToHistory------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("CustStoreMarkDao:copyCustStoreMarkFromICustomerToHistory--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("CustStoreMarkDao:copyCustStoreMarkFromICustomerToHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
	public boolean batchInsertIntoHistory(List<CustStoreMark> csmList,Connection historyConnParam){
		if(csmList==null||csmList.size()<=0) return false;
		PreparedStatement pstmt=null;

		String sql="insert into CUSTOMER_STORE_MARK(Customer_Id,Company_Id,Channel_Id,Prod_Group_Id,Credit_Id,Product_Id," +
				"Ismark,Record_Date) values(?,?,?,?,?,?,?,?)";

		try{
			historyConnParam.setAutoCommit(false);
			pstmt=historyConnParam.prepareStatement(sql);
			for(int i=0;i<csmList.size();i++){
				CustStoreMark csm=csmList.get(i);
				pstmt.setString(1,csm.getCUSTOMER_ID());
				pstmt.setString(2,csm.getCOMPANY_ID());
				pstmt.setString(3,csm.getCHANNEL_ID());
				pstmt.setString(4,csm.getPROD_GROUP_ID());
				pstmt.setString(5,csm.getCREDIT_ID());
				pstmt.setString(6,csm.getPRODUCT_ID());
				pstmt.setString(7,csm.getISMARK());
				pstmt.setString(8,csm.getRECORD_DATE());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			historyConnParam.commit();
			return true;
		}catch(Exception e){
			try{
				historyConnParam.rollback();
			}catch(SQLException e1){
				logger.error("CustStoreMarkDao:batchInsertIntoHistory------"+e1.getLocalizedMessage());
			}
			logger.error("CustStoreMarkDao:batchInsertIntoHistory------"+e.getLocalizedMessage());
			return false;
		}finally{
			try{
				historyConnParam.setAutoCommit(true);
			}catch(SQLException e){
				logger.error("CustStoreMarkDao:batchInsertIntoHistory------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("CustStoreMarkDao:batchInsertIntoHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**从CUSTOMER_STORE_MARK表(Schema:ICustomer)中查出所有数据
	 * @param iCstomerConnParam
	 * @return
	 */
	public List<CustStoreMark> findAllFromICustomer(Connection iCustomerConnParam){
		List<CustStoreMark> list=new ArrayList<CustStoreMark>();

		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="select a.customer_id,a.company_id,a.channel_id,a.prod_group_id,a.credit_id,a.product_id,a.ismark," +
				"a.record_date from CUSTOMER_STORE_MARK a";
		
		try{
			pstmt=iCustomerConnParam.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			CustStoreMark csm=null;
			while(rs.next()){
				csm=new CustStoreMark();

				csm.setCUSTOMER_ID(rs.getString("customer_id"));
				csm.setCOMPANY_ID(rs.getString("company_id"));
				csm.setCHANNEL_ID(rs.getString("channel_id"));
				csm.setPROD_GROUP_ID(rs.getString("prod_group_id"));
				csm.setCREDIT_ID(rs.getString("credit_id"));
				csm.setPRODUCT_ID(rs.getString("product_id"));
				csm.setISMARK(rs.getString("ismark"));
				csm.setRECORD_DATE(rs.getString("record_date"));
				
				list.add(csm);
			}
		}catch(Exception e){
			logger.error("CustStoreMarkDao:findAllFromICustomer------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("CustStoreMarkDao:findAllFromICustomer--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("CustStoreMarkDao:findAllFromICustomer--------------"+e.getLocalizedMessage());
			}
		}
		return list;
	}
	/**将数据保存到CUSTOMER_STORE_MARK表(Schema:history)中
	 * @param csitList
	 */
	public void insertIntoHistory(CustStoreMark csm,Connection historyConnParam){
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		String sql="insert into CUSTOMER_STORE_MARK(Customer_Id,Company_Id,Channel_Id,Prod_Group_Id,Credit_Id,Product_Id," +
				"Ismark,Record_Date) values(?,?,?,?,?,?,?,?)";

		try{
			pstmt=historyConnParam.prepareStatement(sql);			
			pstmt.setString(1,csm.getCUSTOMER_ID());
			pstmt.setString(2,csm.getCOMPANY_ID());
			pstmt.setString(3,csm.getCHANNEL_ID());
			pstmt.setString(4,csm.getPROD_GROUP_ID());
			pstmt.setString(5,csm.getCREDIT_ID());
			pstmt.setString(6,csm.getPRODUCT_ID());
			pstmt.setString(7,csm.getISMARK());
			pstmt.setString(8,csm.getRECORD_DATE());
			pstmt.execute();
		}catch(Exception e){
			logger.error("CustStoreMarkDao:insertIntoHistory------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("CustStoreMarkDao:insertIntoHistory--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("CustStoreMarkDao:insertIntoHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
	/**将CUSTOMER_STORE_MARK表(Schema:history)中的数据删光
	 * @param historyConnParam
	 */
	public boolean deleteAllFromHistory(Connection historyConnParam){
		PreparedStatement pstmt=null;
		String sql="delete from CUSTOMER_STORE_MARK";
		
		try{
			pstmt=historyConnParam.prepareStatement(sql);
			pstmt.execute();
			return true;
		}catch(Exception e){
			logger.error("CustStoreMarkDao:deleteAllFromHistory------"+e.getLocalizedMessage());
			return false;
		}finally{
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("CustStoreMarkDao:deleteAllFromHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
}
