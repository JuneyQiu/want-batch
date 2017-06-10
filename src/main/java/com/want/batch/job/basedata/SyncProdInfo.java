package com.want.batch.job.basedata;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;
import com.want.utils.SapDAO;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2010
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
@Component
public class SyncProdInfo extends AbstractWantJob {

	protected final Log logger = LogFactory.getLog(SyncProdInfo.class);

	@Autowired
	public SapDAO sapDAO;

	@Override
	public void execute() throws SQLException {
		logger.info("prod info sync start!");
		syncProdGroup();
		syncProdInfo();
		logger.info("prod info sync end!");
	}
	public void syncProdGroup() throws SQLException {
		// 建立查询条件
		ArrayList list = sapDAO.getSAPData3("ZRFC13_1", "ZST013_1"); // 成品
		logger.info(">>>>>>接口ZRFC13_1，返回ZST013_1记录条数：" + list.size()
				+ "<<<<<<<<");

		if (list == null || list.size() == 0) {
			String msg = "读取 ZRFC13_1，返回ZST013 无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		String deletesqlcmd = "delete from PROD_GROUP_TBL";
		String insertsqlcmd = "insert into PROD_GROUP_TBL(PROD_ID,PROD_NAME,PROD_LEVEL) values(?,?,?)";

		getiCustomerJdbcOperations().update(deletesqlcmd);
		logger.info("完成: " + deletesqlcmd);

		ArrayList<Object[]> params = new ArrayList<Object[]>();
		for (int l = 0; l < list.size(); l++) {
			HashMap temphm = (HashMap) list.get(l);
			String ID = (String) temphm.get("PRODH");

			if (ID.startsWith("D")) {
				int level = ID.length() / 3;
				ID = SyncProdInfo.parseMP(ID);
				params.add(new Object[] { ID, temphm.get("VTEXT"), new Integer(level)});
			}
		}
		int[] inserts = getiCustomerJdbcOperations().batchUpdate(insertsqlcmd, params);
		logger.info("完成: " + inserts.length + ", " + insertsqlcmd);
	}
	
	public void syncProdInfo() throws SQLException {
		// 建立查询条件
		HashMap queryMap = new HashMap();
		queryMap.put("MTART", "FERT"); // 成品
		ArrayList list = sapDAO.getSAPData2("ZRFC13_1", "ZST013", queryMap); // 成品
		logger.info(">>>>>>接口ZRFC13_1，返回ZST013记录条数：" + list.size() + "<<<<<<<<");

		if (list == null || list.size() == 0) {
			String msg = "读取 ZRFC13_1，返回ZST013 无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		String deletesqlcmd1 = "delete from PROD_INFO_TBL";
		getiCustomerJdbcOperations().update(deletesqlcmd1);
		logger.info("完成: " + deletesqlcmd1);

		String insertsqlcmd1 = "insert into PROD_INFO_TBL(PROD_ID,NAME1,NAME2,SPEC_TASTE,MATERIAL_TYPE,BASE_UNIT,GROUP_TYPE_ID,LV_5_ID) values(?,?,?,?,?,?,?,?)";

		ArrayList<Object[]> params = new ArrayList<Object[]>();
		for (int l = 0; l < list.size(); l++) {
			HashMap temphm = (HashMap) list.get(l);
			String groupType = "";
			if (((String) temphm.get("PRDHA")).length() > 9) {
				groupType = ((String) temphm.get("PRDHA")).substring(0, 9);
			}
			String lv_5Lid = (String) temphm.get("PRDHA");

			groupType = SyncProdInfo.parseMP(groupType);
			lv_5Lid = SyncProdInfo.parseMP(lv_5Lid);
			params.add(new Object[] { temphm.get("MATNR"), temphm.get("MAKTX"), temphm.get("FERTH"), temphm.get("NORMT"), "1", temphm.get("MSEHT"), groupType, lv_5Lid});
		}
		int[] inserts = getiCustomerJdbcOperations().batchUpdate(insertsqlcmd1, params);
		logger.info("完成: " + inserts.length + ", " + insertsqlcmd1);
	}
	
	private static String parseMP(String ori) {
		Calendar c110228 = Calendar.getInstance();
		c110228.clear();
		c110228.set(2011, 1, 28, 0, 1);
		if (System.currentTimeMillis() > c110228.getTimeInMillis()) {
			return ori;
		} else {
			return ori.replace('M', 'P');
		}
	}

}
