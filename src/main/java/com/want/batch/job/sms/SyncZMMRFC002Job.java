package com.want.batch.job.sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.sms.util.SapReutnSMSTableName;
import com.want.batch.job.sms.util.SapSMSFuncName;
import com.want.utils.SapDAO;

/**
 * 
 * @author yu_chao.myc
 * @date 2011-11-30
 * @decription 同步sap接口数据到本地表
 * 
 */
@Component
public class SyncZMMRFC002Job extends AbstractWantJob {

	@Autowired
	public SapDAO sapDAO;

	// 清空sap_sendsms_data
	private String delSql = " delete from SAP_SENDSMS_DATA ";
	// 写入本地表sap_sendsms_data
	private StringBuilder insertSql = new StringBuilder()
			.append(" insert into SAP_SENDSMS_DATA ")
			.append(" ( VBELN ,	VBELN2 , TKNUM,	KUNNR ,	TELF2 ,	VTWEG , ")
			.append("  PLATE ,	TELEPHONE ,	KKBER  ) values ")
			.append(" ( :VBELN,:VBELN2,:TKNUM,:KUNNR,:TELF2,:VTWEG,:PLATE,:TELEPHONE,:KKBER)");

	@Override
	public void execute() {
		// 从sap同步数据到本地库
		syncSapSMSData();
	}

	@SuppressWarnings("unchecked")
	private void syncSapSMSData() {
		HashMap<String, String> querydata = new HashMap<String, String>();
		ArrayList<Map<String, String>> arrayList = new ArrayList<Map<String, String>>();
		// 传入参数
		querydata.put("I_PDT", DateFormatUtils.format(new Date(), "yyyyMMdd"));
		// 每天同步之前 先 清空 SAP_SENDSMS_DATA表
		this.getiCustomerJdbcOperations().update(delSql);
		// 请求sap接口
		arrayList = sapDAO.getSAPData(SapSMSFuncName.FUNC_NAME.getFunc_name(),
				SapReutnSMSTableName.TABLE_NAME.getTableName(), querydata);
		if (!arrayList.isEmpty()) {
			int i = 0;
			for (Map<String, String> map : arrayList) {
				i += this.getiCustomerJdbcOperations().update(
						insertSql.toString(), map);
			}
			logger.info("---------- 增加到表 SAP_SENDSMS_DATA ---" + i + "笔数据！");
		}
	}
}
