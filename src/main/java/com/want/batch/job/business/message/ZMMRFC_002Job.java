package com.want.batch.job.business.message;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.utils.Toolkit;
import com.want.utils.SapDAO;

@Component("messageZMMRFC_002Job")
public class ZMMRFC_002Job extends AbstractWantJob {

	@Autowired
	public SapDAO sapDAO;

	@Override
	public void execute() {
		try {
			/** 每天凌晨1点跑 **/
			HashMap querydata = new HashMap(9);
			String function_name = "ZMMRFC_002";
			String result = "T_DATA";
			querydata.put("I_PDT", Toolkit.dateToString(new Date(), "yyyyMMdd"));//只同步当天
			ArrayList list = sapDAO.getSAPData2(function_name, result, querydata);
			for (int i = 0; i < list.size(); i++) {
				HashMap datahm = (HashMap) list.get(i);
				saveMessage(datahm);
			}
		} catch (Exception e) {
			logger.error("数据库异常！", e);
			throw new WantBatchException(e);
		}
	}
	
	public void saveMessage(HashMap data) throws DataAccessException, ParseException{
		String delete = "delete from SAP_SENDSMS_DATA";
		String insert = "insert into SAP_SENDSMS_DATA(VBELN,VBELN2,TKNUM,KUNNR,TELF2,VT,PLATE,TELEPHONE,KKBE) values" +
        		"(?,?,?,?,?,?,?,?,?)";
		logger.debug(this.getiCustomerJdbcOperations().update(delete, new Object[]{}));
		logger.debug(this.getiCustomerJdbcOperations().update(insert, new Object[]{
				data.get("VBELN").toString(),
				data.get("VBELN2").toString(),
				data.get("TKNUM").toString(),
				data.get("KUNNR").toString(),
				data.get("TELF2").toString(),
				data.get("VT").toString(),
				data.get("PLATE").toString(),
				data.get("TELEPHONE").toString(),
				data.get("KKBE").toString()
			}));
	}

}
