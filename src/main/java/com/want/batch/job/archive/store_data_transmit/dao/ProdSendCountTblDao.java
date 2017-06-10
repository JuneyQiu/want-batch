package com.want.batch.job.archive.store_data_transmit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.archive.store_data_transmit.pojo.ProdSendCountTbl;

public class ProdSendCountTblDao {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	public void copyProdSendCountTblFromICustomerToHistory(Connection iCustomerConn,Connection historyConn){
		int maxCount=1000;
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		String sql="select a.customer_id,a.check_date,a.prod_id,a.prod_sendqty from PROD_SENDCOUNT_TBL a";
		
		try{
			if(deleteAllFromHistory(historyConn)==false) return;

			pstmt=iCustomerConn.prepareStatement(sql);
			rs=pstmt.executeQuery();

			List<ProdSendCountTbl> psctList=new ArrayList<ProdSendCountTbl>();
			ProdSendCountTbl psct=null;
			while(rs.next()){
				psct=new ProdSendCountTbl();
				psct.setCUSTOMER_ID(rs.getString("customer_id"));
				psct.setCHECK_DATE(rs.getString("check_date"));
				psct.setPROD_ID(rs.getString("prod_id"));
				psct.setPROD_SENDQTY(rs.getInt("prod_sendqty"));

				psctList.add(psct);
				if(psctList.size()>=maxCount){
					batchInsertIntoHistory(psctList,historyConn);
					psctList.clear();
				}
			}
			if(psctList.size()>0) batchInsertIntoHistory(psctList,historyConn);
		}catch(Exception e){
			logger.error("ProdSendCountTblDao:copyProdSendCountTblFromICustomerToHistory------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("ProdSendCountTblDao:copyProdSendCountTblFromICustomerToHistory--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("ProdSendCountTblDao:copyProdSendCountTblFromICustomerToHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
	public boolean batchInsertIntoHistory(List<ProdSendCountTbl> psctList,Connection historyConnParam){
		if(psctList==null||psctList.size()<=0) return false;
		PreparedStatement pstmt=null;

		String sql="insert into PROD_SENDCOUNT_TBL(CUSTOMER_ID,CHECK_DATE,PROD_ID,PROD_SENDQTY) values(?,?,?,?)";

		try{
			historyConnParam.setAutoCommit(false);
			pstmt=historyConnParam.prepareStatement(sql);
			for(int i=0;i<psctList.size();i++){
				ProdSendCountTbl psct=psctList.get(i);
				pstmt.setString(1,psct.getCUSTOMER_ID());
				pstmt.setString(2,psct.getCHECK_DATE());
				pstmt.setString(3,psct.getPROD_ID());
				pstmt.setInt(4,psct.getPROD_SENDQTY());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			historyConnParam.commit();
			return true;
		}catch(Exception e){
			try{
				historyConnParam.rollback();
			}catch(SQLException e1){
				logger.error("ProdSendCountTblDao:batchInsertIntoHistory------"+e1.getLocalizedMessage());
			}
			logger.error("ProdSendCountTblDao:batchInsertIntoHistory------"+e.getLocalizedMessage());
			return false;
		}finally{
			try{
				historyConnParam.setAutoCommit(true);
			}catch(SQLException e){
				logger.error("ProdSendCountTblDao:batchInsertIntoHistory------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("ProdSendCountTblDao:batchInsertIntoHistory------"+e.getLocalizedMessage());
			}
		}
	}
	
	
	
	
	
	
	
	/**从PROD_SENDCOUNT_TBL表(Schema:ICustomer)中查出所有数据
	 * @param iCstomerConnParam
	 * @return
	 */
	public List<ProdSendCountTbl> findAllFromICustomer(Connection iCustomerConnParam){
		List<ProdSendCountTbl> list=new ArrayList<ProdSendCountTbl>();

		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="select a.customer_id,a.check_date,a.prod_id,a.prod_sendqty from PROD_SENDCOUNT_TBL a";
		
		try{
			pstmt=iCustomerConnParam.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			ProdSendCountTbl psct=null;
			while(rs.next()){
				psct=new ProdSendCountTbl();
				psct.setCUSTOMER_ID(rs.getString("customer_id"));
				psct.setCHECK_DATE(rs.getString("check_date"));
				psct.setPROD_ID(rs.getString("prod_id"));
				psct.setPROD_SENDQTY(rs.getInt("prod_sendqty"));
				list.add(psct);
			}
		}catch(Exception e){
			logger.error("ProdSendCountTblDao:findAllFromICustomer------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("ProdSendCountTblDao:findAllFromICustomer--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("ProdSendCountTblDao:findAllFromICustomer--------------"+e.getLocalizedMessage());
			}
		}
		return list;
	}
	/**将数据保存到PROD_SENDCOUNT_TBL表(Schema:history)中
	 * @param csitList
	 */
	public void insertIntoHistory(ProdSendCountTbl psct,Connection historyConnParam){
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		String sql="insert into PROD_SENDCOUNT_TBL(CUSTOMER_ID,CHECK_DATE,PROD_ID,PROD_SENDQTY) values(?,?,?,?)";

		try{
			pstmt=historyConnParam.prepareStatement(sql);
			pstmt.setString(1,psct.getCUSTOMER_ID());
			pstmt.setString(2,psct.getCHECK_DATE());
			pstmt.setString(3,psct.getPROD_ID());
			pstmt.setInt(4,psct.getPROD_SENDQTY());
			pstmt.execute();
		}catch(Exception e){
			logger.error("ProdSendCountTblDao:insertIntoHistory--------------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("ProdSendCountTblDao:insertIntoHistory--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("ProdSendCountTblDao:insertIntoHistory--------------"+e.getLocalizedMessage());
			}
		}
	}

	/**将PROD_SENDCOUNT_TBL表(Schema:history)中的数据删光
	 * @param historyConnParam
	 */
	public boolean deleteAllFromHistory(Connection historyConnParam){
		PreparedStatement pstmt=null;
		String sql="delete from PROD_SENDCOUNT_TBL";
		
		try{
			pstmt=historyConnParam.prepareStatement(sql);
			pstmt.execute();
			return true;
		}catch(Exception e){
			logger.error("ProdSendCountTblDao:deleteAllFromHistory------"+e.getLocalizedMessage());
			return false;
		}finally{
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("ProdSendCountTblDao:deleteAllFromHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
}
