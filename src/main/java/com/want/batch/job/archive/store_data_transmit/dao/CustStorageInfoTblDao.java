package com.want.batch.job.archive.store_data_transmit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.want.batch.job.archive.store_data_transmit.pojo.CustStorageInfoTbl;

public class CustStorageInfoTblDao {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	public void transmitFromICustomerToHistory(String yearMonth,Connection iCustomerConn,Connection historyConn){
		int maxCount=1000;//批量迁移数据的最大数量
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		String sql="select a.sid,a.customer_id,a.project_sid,a.yearmonth,a.day,a.prod_id,a.prod_name,a.prod_spec,a.prod_price," +
				"a.qty_1,a.qty_2,a.qty_3,a.qty_4,a.qty_5,a.qty_6,a.qty_7,a.qty_8,a.qty_9,a.qty_10,a.qty_11,a.qty_12," +
				"a.total_qty,a.last_total_qty,a.send_total_qty,a.creator,a.create_date,a.updator,a.update_date,a.update_count," +
				"a.s_qty_1,a.s_qty_2,a.s_qty_3,a.s_qty_4,a.s_qty_5,a.s_qty_6,a.s_qty_7,a.s_qty_8,a.s_qty_9,a.s_qty_10,a.s_qty_11,a.s_qty_12," +
				"a.status,a.credit_id,a.ismark,a.confirm_date from customer_storage_info_tbl a where a.yearmonth=? ";

		try{
			pstmt=iCustomerConn.prepareStatement(sql);
			pstmt.setString(1,yearMonth);
			rs=pstmt.executeQuery();
			
			List<CustStorageInfoTbl> csitList=new ArrayList<CustStorageInfoTbl>();
			CustStorageInfoTbl csit=null;
			
			while(rs.next()){
				csit=new CustStorageInfoTbl()
					.initSID(rs.getLong("sid"))
					.initCUSTOMER_ID(rs.getString("customer_id"))
					.initPROJECT_SID(rs.getLong("project_sid"))
					.initYEARMONTH(rs.getString("yearmonth"))
					.initDAY(rs.getString("day"))
					.initPROD_ID(rs.getString("prod_id"))
					.initPROD_NAME(rs.getString("prod_name"))
					.initPROD_SPEC(rs.getString("prod_spec"))
					.initPROD_PRICE(rs.getDouble("prod_price"))
					.initQTY_1(rs.getInt("qty_1"))
					.initQTY_2(rs.getInt("qty_2"))
					.initQTY_3(rs.getInt("qty_3"))
					.initQTY_4(rs.getInt("qty_4"))
					.initQTY_5(rs.getInt("qty_5"))
					.initQTY_6(rs.getInt("qty_6"))
					.initQTY_7(rs.getInt("qty_7"))
					.initQTY_8(rs.getInt("qty_8"))
					.initQTY_9(rs.getInt("qty_9"))
					.initQTY_10(rs.getInt("qty_10"))
					.initQTY_11(rs.getInt("qty_11"))
					.initQTY_12(rs.getInt("qty_12"))
					.initTOTAL_QTY(rs.getInt("total_qty"))
					.initLAST_TOTAL_QTY(rs.getInt("last_total_qty"))
					.initSEND_TOTAL_QTY(rs.getInt("send_total_qty"))
					.initCREATOR(rs.getString("creator"))
					.initCREATE_DATE(rs.getDate("create_date"))
					.initUPDATOR(rs.getString("updator"))
					.initUPDATE_DATE(rs.getDate("update_date"))
					.initUPDATE_COUNT(rs.getInt("update_count"))
					.initS_QTY_1(rs.getInt("s_qty_1"))
					.initS_QTY_2(rs.getInt("s_qty_2"))
					.initS_QTY_3(rs.getInt("s_qty_3"))
					.initS_QTY_4(rs.getInt("s_qty_4"))
					.initS_QTY_5(rs.getInt("s_qty_5"))
					.initS_QTY_6(rs.getInt("s_qty_6"))
					.initS_QTY_7(rs.getInt("s_qty_7"))
					.initS_QTY_8(rs.getInt("s_qty_8"))
					.initS_QTY_9(rs.getInt("s_qty_9"))
					.initS_QTY_10(rs.getInt("s_qty_10"))
					.initS_QTY_11(rs.getInt("s_qty_11"))
					.initS_QTY_12(rs.getInt("s_qty_12"))
					.initSTATUS(rs.getString("status"))
					.initCREDIT_ID(rs.getString("credit_id"))
					.initISMARK(rs.getString("ismark"))
					.initCONFIRM_DATE(rs.getDate("confirm_date"));

				csitList.add(csit);
				if(csitList.size()>=maxCount){
					if(batchSaveToHistory(csitList,historyConn)) batchDeleteFromICustomer(csitList,iCustomerConn);
					csitList.clear();
				}
			}
			if(csitList.size()>0&&batchSaveToHistory(csitList,historyConn)) batchDeleteFromICustomer(csitList,iCustomerConn);
		}catch(Exception e){
			logger.error("CustStorageInfoTblDao:transmitFromICustomerToHistory----------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:transmitFromICustomerToHistory--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:transmitFromICustomerToHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
	public boolean batchSaveToHistory(List<CustStorageInfoTbl> csitList,Connection historyConnParam){
		PreparedStatement pstmt=null;

		String sql="insert into customer_storage_info_tbl(sid,project_sid,prod_name,prod_spec,prod_price," +
				"qty_1,qty_2,qty_3,qty_4,qty_5,qty_6,qty_7,qty_8,qty_9,qty_10,qty_11,qty_12,total_qty,last_total_qty," +
				"send_total_qty,creator,create_date,updator,update_date,update_count,s_qty_1,s_qty_2,s_qty_3,s_qty_4," +
				"s_qty_5,s_qty_6,s_qty_7,s_qty_8,s_qty_9,s_qty_10,s_qty_11,s_qty_12,status,credit_id,ismark,confirm_date," +
				"customer_id,yearmonth,day,prod_id) values(customer_storage_info_tbl_sid.nextval,?,?,?,?,?,?,?,?,?,?,?,?," +
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try{
			historyConnParam.setAutoCommit(false);
			pstmt=historyConnParam.prepareStatement(sql);
			for(int i=0;i<csitList.size();i++){
				initPstmt(pstmt,csitList.get(i));
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			historyConnParam.commit();
			return true;
		}catch(Exception e){
			try{
				historyConnParam.rollback();
			}catch(SQLException e1){
				logger.error("CustStorageInfoTblDao:batchSaveToHistory----------"+e1.getLocalizedMessage());
			}
			logger.error("CustStorageInfoTblDao:batchSaveToHistory----------"+e.getLocalizedMessage());
			return false;
		}finally{
			try{
				historyConnParam.setAutoCommit(true);
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:batchSaveToHistory----------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:batchSaveToHistory----------"+e.getLocalizedMessage());
			}
		}
	}
	public void batchDeleteFromICustomer(List<CustStorageInfoTbl> csitList,Connection iCustomerConnParam){
		PreparedStatement pstmt=null;
		String sql="delete from customer_storage_info_tbl a where a.sid=?";

		try{
			pstmt=iCustomerConnParam.prepareStatement(sql);
			for(int i=0;i<csitList.size();i++){
				pstmt.setLong(1,csitList.get(i).getSID());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}catch(Exception e){
			logger.error("CustStorageInfoTblDao:batchDeleteFromICustomer--------"+e.getLocalizedMessage());
		}finally{
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:batchDeleteFromICustomer----------"+e.getLocalizedMessage());
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 从customer_storage_info_tbl表(Schema:ICustomer)中删除数据
	 */
	public void deleteFromICustomer(List<CustStorageInfoTbl> csitList,Connection iCustomerConnParam){
		if(csitList==null||csitList.size()<=0) return;

		try{
			for(int i=0;i<csitList.size();i++){
				deleteFromICustomerBySid(csitList.get(i).getSID(),iCustomerConnParam);
			}
		}catch(Exception e){
			logger.error("CustStorageInfoTblDao:deleteFromICustomer------"+e.getLocalizedMessage());
		}
	}
	public void deleteFromICustomerBySid(long sid,Connection iCustomerConnParam){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="delete from customer_storage_info_tbl a where a.sid=?";
		
		try{
			pstmt=iCustomerConnParam.prepareStatement(sql);
			pstmt.setLong(1,sid);
			pstmt.execute();
		}catch(Exception e){
			logger.error("CustStorageInfoTblDao:deleteFromICustomerBySid--------------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:deleteFromICustomerBySid--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:deleteFromICustomerBySid--------------"+e.getLocalizedMessage());
			}
		}
	}
	/**从customer_storage_info_tbl表(Schema:ICustomer)中按年月查出库存数据
	 * @param year
	 * @param month
	 * @return
	 */
	public List<CustStorageInfoTbl> queryFromICustomerByYearAndMonth(String year,String month,Connection iCustomerConnParam){
		List<CustStorageInfoTbl> csitList=new ArrayList<CustStorageInfoTbl>();

		PreparedStatement pstmt=null;
		ResultSet rs=null;

		String sql="select a.sid,a.customer_id,a.project_sid,a.yearmonth,a.day,a.prod_id,a.prod_name,a.prod_spec,a.prod_price," +
				"a.qty_1,a.qty_2,a.qty_3,a.qty_4,a.qty_5,a.qty_6,a.qty_7,a.qty_8,a.qty_9,a.qty_10,a.qty_11,a.qty_12," +
				"a.total_qty,a.last_total_qty,a.send_total_qty,a.creator,a.create_date,a.updator,a.update_date,a.update_count," +
				"a.s_qty_1,a.s_qty_2,a.s_qty_3,a.s_qty_4,a.s_qty_5,a.s_qty_6,a.s_qty_7,a.s_qty_8,a.s_qty_9,a.s_qty_10,a.s_qty_11,a.s_qty_12," +
				"a.status,a.credit_id,a.ismark,a.confirm_date from customer_storage_info_tbl a where a.yearmonth=? ";

		try{
			pstmt=iCustomerConnParam.prepareStatement(sql);
			pstmt.setString(1,year+""+month);
			rs=pstmt.executeQuery();
			
			CustStorageInfoTbl csit=null;
			while(rs.next()){
				csit=new CustStorageInfoTbl();
				
				csit.setSID(rs.getLong("sid"));
				csit.setCUSTOMER_ID(rs.getString("customer_id"));
				csit.setPROJECT_SID(rs.getLong("project_sid"));
				csit.setYEARMONTH(rs.getString("yearmonth"));
				csit.setDAY(rs.getString("day"));
				csit.setPROD_ID(rs.getString("prod_id"));
				csit.setPROD_NAME(rs.getString("prod_name"));
				csit.setPROD_SPEC(rs.getString("prod_spec"));
				csit.setPROD_PRICE(rs.getDouble("prod_price"));
				csit.setQTY_1(rs.getInt("qty_1"));
				csit.setQTY_2(rs.getInt("qty_2"));
				csit.setQTY_3(rs.getInt("qty_3"));
				csit.setQTY_4(rs.getInt("qty_4"));
				csit.setQTY_5(rs.getInt("qty_5"));
				csit.setQTY_6(rs.getInt("qty_6"));
				csit.setQTY_7(rs.getInt("qty_7"));
				csit.setQTY_8(rs.getInt("qty_8"));
				csit.setQTY_9(rs.getInt("qty_9"));
				csit.setQTY_10(rs.getInt("qty_10"));
				csit.setQTY_11(rs.getInt("qty_11"));
				csit.setQTY_12(rs.getInt("qty_12"));
				csit.setTOTAL_QTY(rs.getInt("total_qty"));
				csit.setLAST_TOTAL_QTY(rs.getInt("last_total_qty"));
				csit.setSEND_TOTAL_QTY(rs.getInt("send_total_qty"));
				csit.setCREATOR(rs.getString("creator"));
				csit.setCREATE_DATE(rs.getDate("create_date"));
				csit.setUPDATOR(rs.getString("updator"));
				csit.setUPDATE_DATE(rs.getDate("update_date"));
				csit.setUPDATE_COUNT(rs.getInt("update_count"));
				csit.setS_QTY_1(rs.getInt("s_qty_1"));
				csit.setS_QTY_2(rs.getInt("s_qty_2"));
				csit.setS_QTY_3(rs.getInt("s_qty_3"));
				csit.setS_QTY_4(rs.getInt("s_qty_4"));
				csit.setS_QTY_5(rs.getInt("s_qty_5"));
				csit.setS_QTY_6(rs.getInt("s_qty_6"));
				csit.setS_QTY_7(rs.getInt("s_qty_7"));
				csit.setS_QTY_8(rs.getInt("s_qty_8"));
				csit.setS_QTY_9(rs.getInt("s_qty_9"));
				csit.setS_QTY_10(rs.getInt("s_qty_10"));
				csit.setS_QTY_11(rs.getInt("s_qty_11"));
				csit.setS_QTY_12(rs.getInt("s_qty_12"));
				csit.setSTATUS(rs.getString("status"));
				csit.setCREDIT_ID(rs.getString("credit_id"));
				csit.setISMARK(rs.getString("ismark"));
				csit.setCONFIRM_DATE(rs.getDate("confirm_date"));
				
				csitList.add(csit);
			}
			
		}catch(Exception e){
			logger.error("CustStorageInfoTblDao:queryFromICustomerByYearAndMonth--------------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:queryFromICustomerByYearAndMonth--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:queryFromICustomerByYearAndMonth--------------"+e.getLocalizedMessage());
			}
		}		
		return csitList;
	}
	
	/**将数据保存到customer_storage_info_tbl表(Schema:history)中
	 * @param csitList
	 */
	public void saveOrUpdateToHistory(List<CustStorageInfoTbl> csitList,Connection historyConnParam){
		if(csitList==null||csitList.size()<=0) return;

		try{
			for(int i=0;i<csitList.size();i++){
				saveOrUpdateToHistory(csitList.get(i),historyConnParam);
			}
		}catch(Exception e){
			logger.error("CustStorageInfoTblDao:saveOrUpdateToHistory------"+e.getLocalizedMessage());
		}
	}
	public void saveToHistory(List<CustStorageInfoTbl> csitList,Connection historyConnParam){
		if(csitList==null||csitList.size()<=0) return;

		try{
			for(int i=0;i<csitList.size();i++){
				saveToHistory(csitList.get(i),historyConnParam);
			}
		}catch(Exception e){
			logger.error("CustStorageInfoTblDao:saveToHistory------"+e.getLocalizedMessage());
		}
	}
	public void saveOrUpdateToHistory(CustStorageInfoTbl csit,Connection historyConnParam){
		//根据客户编号+年月+日+货料代号查询，看是否有以存在的记录
		//存在就更新，不存在就插入
		try{
			if(isExit(csit,historyConnParam)){
				updateToHistory(csit,historyConnParam);
			}else{
				saveToHistory(csit,historyConnParam);
			}
		}catch(Exception e){
			logger.error("CustStorageInfoTblDao:saveOrUpdateToHistory------"+e.getLocalizedMessage());
		}
	}
	public boolean isExit(CustStorageInfoTbl csit,Connection historyConnParam) throws Exception{
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		//根据客户编号+年月+日+货料代号查询
		String sql="select a.sid from customer_storage_info_tbl a where a.customer_id=? and a.yearmonth=? and a.day=? and a.prod_id=?";
		try{
			pstmt=historyConnParam.prepareStatement(sql);
			pstmt.setString(1,csit.getCUSTOMER_ID());
			pstmt.setString(2,csit.getYEARMONTH());
			pstmt.setString(3,csit.getDAY());
			pstmt.setString(4,csit.getPROD_ID());
			rs=pstmt.executeQuery();
			
			if(rs.next()) return true;
			else return false;
		}catch(Exception e){
			logger.error("CustStorageInfoTblDao:isExit------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:isExit--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:isExit--------------"+e.getLocalizedMessage());
			}
		}
		throw new Exception("CustStorageInfoTblDao:isExit------数据库异常!");
	}

	public void updateToHistory(CustStorageInfoTbl csit,Connection historyConnParam){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		String sql="update customer_storage_info_tbl a  " +
				"set a.project_sid=?,a.prod_name=?,a.prod_spec=?,a.prod_price=?," +
				"a.qty_1=?,a.qty_2=?,a.qty_3=?,a.qty_4,a.qty_5=?,a.qty_6=?,a.qty_7=?,a.qty_8=?,a.qty_9=?,a.qty_10=?,a.qty_11=?,a.qty_12=?," +
				"a.total_qty=?,a.last_total_qty=?,a.send_total_qty=?,a.creator=?,a.create_date=?,a.updator=?,a.update_date=?,a.update_count=?," +
				"a.s_qty_1=?,a.s_qty_2=?,a.s_qty_3=?,a.s_qty_4=?,a.s_qty_5=?,a.s_qty_6=?,a.s_qty_7=?,a.s_qty_8=?,a.s_qty_9=?,a.s_qty_10=?," +
				"a.s_qty_11=?,a.s_qty_12=?,a.status=?,a.credit_id=?,a.ismark=?,a.confirm_date=? where a.customer_id=? and a.yearmonth=? and a.day=?" +
				" and a.prod_id=?";

		try{
			pstmt=historyConnParam.prepareStatement(sql);			
			initPstmt(pstmt,csit);
			pstmt.execute();
		}catch(Exception e){
			logger.error("CustStorageInfoTblDao:updateToHistory------"+e.getLocalizedMessage());
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:updateToHistory--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:updateToHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
	public boolean saveToHistory(CustStorageInfoTbl csit,Connection historyConnParam){
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		String sql="insert into customer_storage_info_tbl(sid,project_sid,prod_name,prod_spec,prod_price," +
				"qty_1,qty_2,qty_3,qty_4,qty_5,qty_6,qty_7,qty_8,qty_9,qty_10,qty_11,qty_12,total_qty,last_total_qty," +
				"send_total_qty,creator,create_date,updator,update_date,update_count,s_qty_1,s_qty_2,s_qty_3,s_qty_4," +
				"s_qty_5,s_qty_6,s_qty_7,s_qty_8,s_qty_9,s_qty_10,s_qty_11,s_qty_12,status,credit_id,ismark,confirm_date," +
				"customer_id,yearmonth,day,prod_id) values(customer_storage_info_tbl_sid.nextval,?,?,?,?,?,?,?,?,?,?,?,?," +
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try{
			pstmt=historyConnParam.prepareStatement(sql);			
			initPstmt(pstmt,csit);
			pstmt.execute();
			return true;
		}catch(Exception e){
			logger.error("CustStorageInfoTblDao:saveToHistory------"+e.getLocalizedMessage());
			return false;
		}finally{
			try{
				if(rs!=null) rs.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:saveToHistory--------------"+e.getLocalizedMessage());
			}
			try{
				if(pstmt!=null) pstmt.close();
			}catch(SQLException e){
				logger.error("CustStorageInfoTblDao:saveToHistory--------------"+e.getLocalizedMessage());
			}
		}
	}
	//创建PreparedStatement，调用此方法时一定不能将sql语句中字段的顺序打乱
	private void initPstmt(PreparedStatement pstmt,CustStorageInfoTbl csit) throws Exception{
		pstmt.setLong(1,csit.getPROJECT_SID());
		pstmt.setString(2,csit.getPROD_NAME());
		pstmt.setString(3,csit.getPROD_SPEC());
		pstmt.setDouble(4,csit.getPROD_PRICE());
		pstmt.setInt(5,csit.getQTY_1());
		pstmt.setInt(6,csit.getQTY_2());
		pstmt.setInt(7,csit.getQTY_3());
		pstmt.setInt(8,csit.getQTY_4());
		pstmt.setInt(9,csit.getQTY_5());
		pstmt.setInt(10,csit.getQTY_6());
		pstmt.setInt(11,csit.getQTY_7());
		pstmt.setInt(12,csit.getQTY_8());
		pstmt.setInt(13,csit.getQTY_9());
		pstmt.setInt(14,csit.getQTY_10());
		pstmt.setInt(15,csit.getQTY_11());
		pstmt.setInt(16,csit.getQTY_12());
		pstmt.setInt(17,csit.getTOTAL_QTY());
		pstmt.setInt(18,csit.getLAST_TOTAL_QTY());
		pstmt.setInt(19,csit.getSEND_TOTAL_QTY());
		pstmt.setString(20,csit.getCREATOR());
		pstmt.setDate(21,(java.sql.Date)csit.getCREATE_DATE());
		pstmt.setString(22,csit.getUPDATOR());
		pstmt.setDate(23,(java.sql.Date)csit.getUPDATE_DATE());
		pstmt.setInt(24,csit.getUPDATE_COUNT());
		pstmt.setInt(25,csit.getS_QTY_1());
		pstmt.setInt(26,csit.getS_QTY_2());
		pstmt.setInt(27,csit.getS_QTY_3());
		pstmt.setInt(28,csit.getS_QTY_4());
		pstmt.setInt(29,csit.getS_QTY_5());
		pstmt.setInt(30,csit.getS_QTY_6());
		pstmt.setInt(31,csit.getS_QTY_7());
		pstmt.setInt(32,csit.getS_QTY_8());
		pstmt.setInt(33,csit.getS_QTY_9());
		pstmt.setInt(34,csit.getS_QTY_10());
		pstmt.setInt(35,csit.getS_QTY_11());
		pstmt.setInt(36,csit.getS_QTY_12());
		pstmt.setString(37,csit.getSTATUS());
		pstmt.setString(38,csit.getCREDIT_ID());
		pstmt.setString(39,csit.getISMARK());
		pstmt.setDate(40,(java.sql.Date)csit.getCONFIRM_DATE());

		pstmt.setString(41,csit.getCUSTOMER_ID());
		pstmt.setString(42,csit.getYEARMONTH());
		pstmt.setString(43,csit.getDAY());
		pstmt.setString(44,csit.getPROD_ID());		
	}
}
