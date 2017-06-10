package com.want.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.sap.mw.jco.JCO;

public class SapQuery {

	private JCO.Client mConnection = null;
	private JCO.Repository mRepository = null;
	private JCO.Table DATA = null;
	
	private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	public SapQuery(JCO.Client mConnection, JCO.Repository mRepository, JCO.Table DATA) {
		this.mConnection = mConnection;
		this.mRepository = mRepository;
		this.DATA = DATA;
	}

	public int getNumRows() {
		return DATA.getNumRows();
	}
	
	public void setIndex(int index) {
        DATA.setRow(index);
	}
	
	public String getString(String field) {
        return DATA.getString(field);
	}
	
	public byte[] getBytes(String field) {
		return DATA.getByteArray(field);
	}
	
	public Timestamp getTimestamp(String field) throws ParseException {
		return new Timestamp(format.parse(getString("CR_DATE")).getTime());
	}
	
	public void close() {
		this.DATA.clear();
		this.mRepository = null;
		JCO.releaseClient(this.mConnection);
	}
}
