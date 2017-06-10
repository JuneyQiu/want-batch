package com.want.batch.job.archive.store_data_transmit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.archive.store_data_transmit.pojo.ProdCustRel;

public class ProdCustRelDao {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	public void copyProdCustRelFromICustomerToHistory(Connection iCustomerConn,Connection historyConn){
		int maxCount=1000;
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		String sql="select a.customer_id,a.company_id,a.matnr,a.maktx,a.normt,a.kbetr,a.kkber,a.kkbtx,a.record_date from PROD_CUSTOMER_REL a";
		
		try{
			if(deleteAllFromHistory(historyConn)==false) return;

			pstmt=iCustomerConn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			List<ProdCustRel> pcrList=new ArrayList<ProdCustRel>();
			ProdCustRel pcr=null;
			while(rs.next()){
				pcr=new ProdCustRel();
				pcr.setCUSTOMER_ID(rs.getString("customer_id"));
				pcr.setCOMPANY_ID(rs.getString("company_id"));
				pcr.setMATNR(rs.getString("matnr"));
				pcr.setMAKTX(rs.getString("maktx"));
				pcr.setNORMT(rs.getString("normt"));
				pcr.setKBETR(rs.getString("kbetr"));
				pcr.setKKBER(rs.getString("kkber"));
				pcr.setKKBTX(rs.getString("kkbtx"));
				pcr.setRECORD_DATE(rs.getString("record_date"));
				
				pcrList.add(pcr);
				if(pcrList.size()>=maxCount){
					batchInsertIntoHistory(pcrList,historyConn);
					pcrList.clear();
				}
			}
			if(pcrList.size()>0) batchInsertIntoHistory(pcrList,historyConn);
		}catch(Exception e){
			logger.error("ProdCustRelDao:copyProdCustRelFromICustomerToHistory------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("ProdCustRelDao:copyProdCustRelFromICustomerToHistory--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("ProdCustRelDao:copyProdCustRelFromICustomerToHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
	public boolean batchInsertIntoHistory(List<ProdCustRel> pcrList,Connection historyConnParam){
		if(pcrList==null||pcrList.size()<=0) return false;
		PreparedStatement pstmt=null;

		String sql="insert into PROD_CUSTOMER_REL(Customer_Id,Company_Id,Matnr,Maktx,Normt,Kbetr,Kkber,Kkbtx,Record_Date) " +
				"values(?,?,?,?,?,?,?,?,?)";

		try{
			historyConnParam.setAutoCommit(false);
			pstmt=historyConnParam.prepareStatement(sql);
			for(int i=0;i<pcrList.size();i++){
				ProdCustRel pcr=pcrList.get(i);
				pstmt.setString(1,pcr.getCUSTOMER_ID());
				pstmt.setString(2,pcr.getCOMPANY_ID());
				pstmt.setString(3,pcr.getMATNR());
				pstmt.setString(4,pcr.getMAKTX());
				pstmt.setString(5,pcr.getNORMT());
				pstmt.setString(6,pcr.getKBETR());
				pstmt.setString(7,pcr.getKKBER());
				pstmt.setString(8,pcr.getKKBTX());
				pstmt.setString(9,pcr.getRECORD_DATE());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			historyConnParam.commit();
			return true;
		}catch(Exception e){
			try{
				historyConnParam.rollback();
			}catch(SQLException e1){
				logger.error("ProdCustRelDao:batchInsertIntoHistory------"+e1.getLocalizedMessage());
			}
			logger.error("ProdCustRelDao:batchInsertIntoHistory------"+e.getLocalizedMessage());
			return false;
		}finally{
			try{
				historyConnParam.setAutoCommit(true);
			}catch(SQLException e){
				logger.error("ProdCustRelDao:batchInsertIntoHistory------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("ProdCustRelDao:batchInsertIntoHistory------"+e.getLocalizedMessage());
			}
		}
	}
	
	
	
	
	
	
	
	
	
	/**从PROD_CUSTOMER_REL表(Schema:ICustomer)中查出所有数据
	 * @param iCstomerConnParam
	 * @return
	 */
	public List<ProdCustRel> findAllFromICustomer(Connection iCustomerConnParam){
		List<ProdCustRel> list=new ArrayList<ProdCustRel>();

		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="select a.customer_id,a.company_id,a.matnr,a.maktx,a.normt,a.kbetr,a.kkber,a.kkbtx,a.record_date from PROD_CUSTOMER_REL a";
		
		try{
			pstmt=iCustomerConnParam.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			ProdCustRel pcr=null;
			while(rs.next()){
				pcr=new ProdCustRel();
				pcr.setCUSTOMER_ID(rs.getString("customer_id"));
				pcr.setCOMPANY_ID(rs.getString("company_id"));
				pcr.setMATNR(rs.getString("matnr"));
				pcr.setMAKTX(rs.getString("maktx"));
				pcr.setNORMT(rs.getString("normt"));
				pcr.setKBETR(rs.getString("kbetr"));
				pcr.setKKBER(rs.getString("kkber"));
				pcr.setKKBTX(rs.getString("kkbtx"));
				pcr.setRECORD_DATE(rs.getString("record_date"));
				list.add(pcr);
			}
		}catch(Exception e){
			logger.error("ProdCustRelDao:findAllFromICustomer------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("ProdCustRelDao:findAllFromICustomer--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("ProdCustRelDao:findAllFromICustomer--------------"+e.getLocalizedMessage());
			}
		}
		return list;
	}
	/**将数据保存到PROD_CUSTOMER_REL表(Schema:history)中
	 * @param pcrList
	 */
	public void insertIntoHistory(ProdCustRel pcr,Connection historyConnParam){
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		String sql="insert into PROD_CUSTOMER_REL(Customer_Id,Company_Id,Matnr,Maktx,Normt,Kbetr,Kkber,Kkbtx,Record_Date) " +
				"values(?,?,?,?,?,?,?,?,?)";

		try{
			pstmt=historyConnParam.prepareStatement(sql);			
			pstmt.setString(1,pcr.getCUSTOMER_ID());
			pstmt.setString(2,pcr.getCOMPANY_ID());
			pstmt.setString(3,pcr.getMATNR());
			pstmt.setString(4,pcr.getMAKTX());
			pstmt.setString(5,pcr.getNORMT());
			pstmt.setString(6,pcr.getKBETR());
			pstmt.setString(7,pcr.getKKBER());
			pstmt.setString(8,pcr.getKKBTX());
			pstmt.setString(9,pcr.getRECORD_DATE());
			pstmt.execute();
		}catch(Exception e){
			logger.error("ProdCustRelDao:insertIntoHistory------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("ProdCustRelDao:insertIntoHistory--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("ProdCustRelDao:insertIntoHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
	/**将PROD_CUSTOMER_REL表(Schema:history)中的数据删光
	 * @param historyConnParam
	 */
	public boolean deleteAllFromHistory(Connection historyConnParam){
		PreparedStatement pstmt=null;
		String sql="delete from PROD_CUSTOMER_REL";
		
		try{
			pstmt=historyConnParam.prepareStatement(sql);
			pstmt.execute();
			return true;
		}catch(Exception e){
			logger.error("ProdCustRelDao:deleteAllFromHistory------"+e.getLocalizedMessage());
			return false;
		}finally{
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("ProdCustRelDao:deleteAllFromHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
}
