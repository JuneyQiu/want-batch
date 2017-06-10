package com.want.batch.job.archive.store_data_transmit.service;

import java.sql.Connection;

import com.want.batch.job.archive.store_data_transmit.dao.CustStorageInfoTblDao;
import com.want.batch.job.archive.store_data_transmit.dao.CustStoreMarkDao;
import com.want.batch.job.archive.store_data_transmit.dao.ProdCustRelDao;
import com.want.batch.job.archive.store_data_transmit.dao.ProdSendCountTblDao;
import com.want.batch.job.archive.store_data_transmit.dao.SalesAreaRelDao;

public class StoreTransmitService {
	private static StoreTransmitService instance=null;
	private static CustStorageInfoTblDao custStorageInfoTblDao=null;

	private static CustStoreMarkDao custStoreMarkDao=null;
	private static ProdCustRelDao prodCustRelDao=null;
	private static ProdSendCountTblDao prodSendCountTblDao=null;
	private static SalesAreaRelDao salesAreaRelDao=null;

	public static StoreTransmitService getInstance(){
		if(instance==null) instance=new StoreTransmitService();
		return instance;
	}
	private StoreTransmitService(){}
	
	private static CustStorageInfoTblDao getCustStorageInfoTblDao(){
		if(custStorageInfoTblDao==null) custStorageInfoTblDao=new CustStorageInfoTblDao();
		return custStorageInfoTblDao;
	}

	private static CustStoreMarkDao getCustStoreMarkDao(){
		if(custStoreMarkDao==null) custStoreMarkDao=new CustStoreMarkDao();
		return custStoreMarkDao;
	}
	private static ProdCustRelDao getProdCustRelDao(){
		if(prodCustRelDao==null) prodCustRelDao=new ProdCustRelDao();
		return prodCustRelDao;
	}
	private static ProdSendCountTblDao getProdSendCountTblDao(){
		if(prodSendCountTblDao==null) prodSendCountTblDao=new ProdSendCountTblDao();
		return prodSendCountTblDao;
	}
	private static SalesAreaRelDao getSalesAreaRelDao(){
		if(salesAreaRelDao==null) salesAreaRelDao=new SalesAreaRelDao();
		return salesAreaRelDao;
	}
	
	
	public void transmitFromICustomerToHistory(String yearMonth,Connection iCustomerConn,Connection historyConn){
		getCustStorageInfoTblDao().transmitFromICustomerToHistory(yearMonth,iCustomerConn,historyConn);
	}
	public void copyCustStoreMarkFromICustomerToHistory(Connection iCustomerConn,Connection historyConn){
		getCustStoreMarkDao().copyCustStoreMarkFromICustomerToHistory(iCustomerConn,historyConn);
	}
	public void copyProdCustRelFromICustomerToHistory(Connection iCustomerConn,Connection historyConn){
		getProdCustRelDao().copyProdCustRelFromICustomerToHistory(iCustomerConn,historyConn);
	}
	public void copyProdSendCountTblFromICustomerToHistory(Connection iCustomerConn,Connection historyConn){
		getProdSendCountTblDao().copyProdSendCountTblFromICustomerToHistory(iCustomerConn,historyConn);
	}
	public void copySalesAreaRelFromICustomerToHistory(Connection iCustomerConn,Connection historyConn){
		getSalesAreaRelDao().copySalesAreaRelFromICustomerToHistory(iCustomerConn,historyConn);
	}
	
	
	
}
