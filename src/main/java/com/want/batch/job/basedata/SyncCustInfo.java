package com.want.batch.job.basedata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;
import com.want.utils.SapDAO;
import com.want.utils.SapUtils;
//import com.want.component.sap.old.sapDAO;
//import com.want.component.sap.old.SAPUtil;

/**
 * @author 00078588 从sap同步客户类型、客户组、客户组与客户类型关系等数据
 */
@Component
public class SyncCustInfo extends AbstractWantJob {

	@Autowired
	public SapDAO sapDAO;

	@Override
	public void execute() throws SQLException {
		syncCustGrp();
		syncCustTypeAndGift();
		syncCustGrpAndCustType();
		syncCustType();
		updateCustomer();

		// Jack: move SyncCustomerInfo.synccustomerinfo_WAREHOUSE() and
		// SyncCustomerInfo.synccustomerinfo_cussystem()
		// into the Job
		synccustomerinfo_WAREHOUSE();
		synccustomerinfo_cussystem();
	}

	// 同步客户类型
	private void syncCustType() throws SQLException {
		// sapDAO sapdao = new sapDAO();
		ArrayList list = sapDAO.getSAPData("ZRFC25", "ZT_30", new HashMap(2));

		if (list == null || list.size() == 0) {
			String msg = "读取 ZRFC25 ZT_31 无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		String deleteSql = "delete from CUSTOMER_TYPE";
		getiCustomerJdbcOperations().update(deleteSql);
		logger.info("完成: " + deleteSql);

		String insertSql = " insert into CUSTOMER_TYPE(CUSTOMER_TYPE_ID,CUSTOMER_TYPE_DESC,CREATE_DATE) values(?,?,sysdate)";
		int[] inserts = getiCustomerJdbcOperations().batchUpdate(insertSql,
				SapUtils.getMapValue(list, "ZCUST_TYID", "ZCUST_TYDES"));
		logger.info("新增笔数: " + inserts.length + ", " + insertSql);
	}

	// 同步客户组与客户类型关系
	private void syncCustGrpAndCustType() throws SQLException {
		// sapDAO sapdao = new sapDAO();
		ArrayList list = sapDAO.getSAPData("ZRFC25", "ZT_31", new HashMap(5));

		if (list == null || list.size() == 0) {
			String msg = "读取 ZRFC25 ZT_31 无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		String deleteSql = "delete from CUS_TYPE_GRP23_REL";
		getiCustomerJdbcOperations().update(deleteSql);
		logger.info("完成: " + deleteSql);

		String insertSql = " insert into CUS_TYPE_GRP23_REL(CUSTOMER_TYPE_ID,SALE_CHANNEL,PROD_GROUP,CUSTOMER_GROUP2,CUSTOMER_GROUP3,CREATE_DATE) "
				+ " values(?,?,?,?,?,sysdate)";
		ArrayList<Object[]> params = new ArrayList<Object[]>();
		for (int i = 0; i < list.size(); i++) {
			HashMap temphm = (HashMap) list.get(i);
			String custGrp2Id = (String) temphm.get("KVGR2");
			String custGrp3Id = (String) temphm.get("KVGR3");
			String p4 = (custGrp2Id == null || custGrp2Id.trim().equals("")) ? " "
					: custGrp2Id;
			String p5 = (custGrp3Id == null || custGrp3Id.trim().equals("")) ? " "
					: custGrp3Id;
			params.add(new Object[] { temphm.get("ZCUST_TYID"),
					temphm.get("VTWEG"), temphm.get("SPART"), p4, p5 });
		}
		int[] inserts = getiCustomerJdbcOperations().batchUpdate(insertSql,
				params);
		logger.info("新增笔数: " + inserts.length + ", " + insertSql);
	}

	// 同步礼包客户类型对照关系
	private void syncCustTypeAndGift() throws SQLException {
		// sapDAO sapdao = new sapDAO();
		ArrayList list = sapDAO.getSAPData("ZRFC25", "ZT_32", new HashMap(4));
		logger.info("读取比数: " + list.size());

		if (list == null || list.size() == 0) {
			String msg = "读取 ZRFC25 ZT_32 无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		String deleteSql = "delete from CUS_TYPE_GRP4_REL";
		getiCustomerJdbcOperations().update(deleteSql);
		logger.info("完成: " + deleteSql);

		String insertSql = " insert into CUS_TYPE_GRP4_REL(CUSTOMER_TYPE_ID,SALE_CHANNEL,PROD_GROUP,CUSTOMER_GROUP4,CREATE_DATE) "
				+ "values(?,?,?,?,sysdate) ";
		int[] inserts = getiCustomerJdbcOperations().batchUpdate(
				insertSql,
				SapUtils.getMapValue(list, "ZCUST_TYID", "VTWEG", "SPART",
						"KVGR4"));
		logger.info("新增笔数: " + inserts + ", " + insertSql);
	}

	// 同步客户组
	private void syncCustGrp() throws SQLException {

		// sapDAO sapdao = new sapDAO();
		ArrayList list1 = sapDAO.getSAPData("ZRFC25", "ZT_V1", new HashMap(2));
		ArrayList list2 = sapDAO.getSAPData("ZRFC25", "ZT_V2", new HashMap(2));
		ArrayList list3 = sapDAO.getSAPData("ZRFC25", "ZT_V3", new HashMap(2));
		ArrayList list4 = sapDAO.getSAPData("ZRFC25", "ZT_V4", new HashMap(2));

		if (list1 == null || list1.size() == 0) {
			String msg = "读取 ZRFC25 ZT_V1 无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		if (list2 == null || list2.size() == 0) {
			String msg = "读取 ZRFC25 ZT_V2 无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		if (list3 == null || list3.size() == 0) {
			String msg = "读取 ZRFC25 ZT_V3 无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		if (list4 == null || list4.size() == 0) {
			String msg = "读取 ZRFC25 ZT_V4 无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		logger.info("读取比数: " + list1.size() + list2.size() + list3.size()
				+ list4.size());

		String deleteSql = "delete from CUSTOMER_GROUP ";
		getiCustomerJdbcOperations().update(deleteSql);
		logger.info("完成: " + deleteSql);

		String insertSql = "insert into CUSTOMER_GROUP(CUSTOMER_GROUP_ID,CUSTOMER_GROUP_NAME,GROUP_ID,CREATE_DATE) values(?,?,?,sysdate)";
		ArrayList<String> ids = new ArrayList<String>();
		ArrayList<Object[]> params = new ArrayList<Object[]>();
		for (int i = 0; i < list1.size(); i++) {
			HashMap temphm = (HashMap) list1.get(i);
			String id = (String) temphm.get("KVGR1");
			if (!ids.contains(id)) {
				ids.add(id);
				params.add(new Object[] { id, temphm.get("BEZEI"), "1" });
			} else 
				logger.error("customer_group duplicate " + id);
		}

		for (int i = 0; i < list2.size(); i++) {
			HashMap temphm = (HashMap) list2.get(i);
			String id = (String) temphm.get("KVGR2");
			if (!ids.contains(id)) {
				ids.add(id);
				params.add(new Object[] { id, temphm.get("BEZEI"), "2" });
			} else 
				logger.error("customer_group duplicate " + id);
		}

		for (int i = 0; i < list3.size(); i++) {
			HashMap temphm = (HashMap) list3.get(i);
			String id = (String) temphm.get("KVGR3");
			if (!ids.contains(id)) {
				ids.add(id);
				params.add(new Object[] { id, temphm.get("BEZEI"), "3" });
			} else 
				logger.error("customer_group duplicate " + id);
		}

		for (int i = 0; i < list4.size(); i++) {
			HashMap temphm = (HashMap) list4.get(i);
			String id = (String) temphm.get("KVGR4");
			if (!ids.contains(id)) {
				ids.add(id);
				params.add(new Object[] { id, temphm.get("BEZEI"), "4" });
			} else 
				logger.error("customer_group duplicate " + id);
		}
		int[] inserts = getiCustomerJdbcOperations().batchUpdate(insertSql,
				params);
		logger.info("新增笔数: " + inserts.length + ", " + insertSql);
	}

	private static final String ADD_CUSTOMER_SQL = "insert into CUSTOMER_INFO_TBL "
			+ " (SID, ID, NAME, SHORT_NAME, WS_ID, OWNER, MOBILE, PHONE, CITY_NAME, SUBCITY_NAME, ADDRESS2, "
			+ " ADDRESS1, STATUS, CATEGORY, DESCRIBE, ID_FRIEND, BOSS_NAME, BOSS_PHONE)"
			+ " values (CUSTOMER_INFO_TBL_SID.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_CUSTOMER_SQL = "update CUSTOMER_INFO_TBL set "
			+ " ID=?, NAME=?, SHORT_NAME=?, WS_ID=?, OWNER=?, MOBILE=?, PHONE=?, CITY_NAME=?, SUBCITY_NAME=?, ADDRESS2=?, "
			+ " ADDRESS1=?, STATUS=?, CATEGORY=?, DESCRIBE=?, ID_FRIEND=?, BOSS_NAME=?, BOSS_PHONE=?"
			+ " where SID=?";

	private void updateCustomer() throws SQLException {
		Map<String, String> oldMap = getAllCustomerMap(); // 数据库里面已有的customer信息
		Map<String, Object[]> sapList = getCustomerFromSAP(); // 从SAP取得的customer信息

		List<Object[]> addList = new ArrayList<Object[]>(); // 要增加的customer列表
		List<Object[]> updateList = new ArrayList<Object[]>(); // 要修改的customer列表

		Iterator customerIds = sapList.keySet().iterator();
		while (customerIds.hasNext()) {
			String customerId = (String) customerIds.next();
			String oldSid = oldMap.get(customerId);
			if (oldSid == null)
				addList.add(sapList.get(customerId));
			else {
				Object[] originalValue = sapList.get(customerId);
				Object[] updateValue = Arrays.copyOf(originalValue,
						originalValue.length + 1);
				updateValue[originalValue.length] = myParseInt(oldSid);
				updateList.add(updateValue);
			}
		}

		int[] inserts = getiCustomerJdbcOperations().batchUpdate(
				ADD_CUSTOMER_SQL, addList);
		logger.info("新增客户笔数: " + inserts.length);

		int[] updates = getiCustomerJdbcOperations().batchUpdate(
				UPDATE_CUSTOMER_SQL, updateList);
		logger.info("修改客户笔数: " + updates.length);

	}

	private String changeCustomerIdTo10(String customerId) {
		if (customerId == null)
			return "";
		customerId = customerId.trim();
		return (customerId.length() == 8) ? ("00" + customerId) : customerId;
	}

	private Integer myParseInt(String str) {
		if (str == null || "".equals(str.trim()))
			return -1;
		int num = 0;
		try {
			num = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			num = -1;
		}
		return new Integer(num);
	}

	private Map<String, String> getAllCustomerMap() throws SQLException {
		Map<String, String> map = new HashMap<String, String>();
		Set<String> set = new HashSet<String>();

		String sql = "select sid, id from CUSTOMER_INFO_TBL";
		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			int sid = rs.getInt("sid");
			String id = rs.getString("id");
			String customerId = changeCustomerIdTo10(id);
			if (customerId.startsWith("0011") && set.add(customerId)) // 0011打头
																		// &&
																		// 避免重复的客户编号
				map.put(customerId, String.valueOf(sid)); // key: 0011001581,
															// value: sid
		}
		rs.close();
		pstmt.close();
		conn.close();
		return map;
	}

	private Map<String, Object[]> getCustomerFromSAP() {
		List list = sapDAO.getSAPData3("ZRFC07", "ZST07");
		int length = (list == null || list.isEmpty()) ? 0 : list.size();

		if (list == null || list.size() == 0) {
			String msg = "读取 ZRFC07 ZST07 无数据";
			logger.error(msg);
			throw new WantBatchException(new WantBatchException(msg));
		}

		Map<String, Object[]> customers = new HashMap<String, Object[]>();

		for (int i = 0; i < length; i++) {
			Map tempMap = (Map) list.get(i);
			String customerId = String.valueOf(tempMap.get("KUNNR")).trim();
			customerId = changeCustomerIdTo10(customerId);
			Object[] entry = new Object[17];

			entry[0] = customerId;
			entry[1] = String.valueOf(tempMap.get("NAME1")).trim();
			entry[2] = String.valueOf(tempMap.get("SORT1")).trim();
			entry[3] = String.valueOf(tempMap.get("SORT2")).trim();
			entry[4] = String.valueOf(tempMap.get("NAME2")).trim();
			entry[5] = String.valueOf(tempMap.get("TELF2")).trim();
			entry[6] = String.valueOf(tempMap.get("TELF1")).trim();
			entry[7] = String.valueOf(tempMap.get("CITY1")).trim();
			entry[8] = String.valueOf(tempMap.get("CITY2")).trim();
			entry[9] = String.valueOf(tempMap.get("STREET")).trim(); // 送货地址ok
			entry[10] = String.valueOf(tempMap.get("STR_SUPPL1")).trim();// 收信地址ok
			entry[11] = String.valueOf(tempMap.get("AUFSD")).trim();
			entry[12] = String.valueOf(tempMap.get("KUKLA")).trim();
			entry[13] = String.valueOf(tempMap.get("VTEXT")).trim();
			entry[14] = String.valueOf(tempMap.get("KUNN2")).trim(); // 业务伙伴的客户号
			entry[15] = String.valueOf(tempMap.get("ZNAME1")).trim(); // 老板姓名add
																		// 76072
																		// 20101231
			entry[16] = String.valueOf(tempMap.get("ZTELF1")).trim(); // 老板电话add
																		// 76072
																		// 20101231

			if (customerId.startsWith("0011")) {
				if (!customers.containsKey(customerId)) // 避免重复的客户编号
					customers.put(customerId, entry);
				else {
					logger.info("重复的 = " + customerId);
				}
			}
		}

		return customers;
	}

	private void synccustomerinfo_WAREHOUSE() throws SQLException {
		List list = sapDAO.getSAPData3("ZRFC07", "ZST07");

		if (list == null || list.size() == 0) {
			String msg = "读取 ZRFC07 ZST07 无数据";
			logger.error(msg);
			// throw new WantBatchException(new WantBatchException(msg));
		}

		ArrayList<Object[]> params = new ArrayList<Object[]>();
		for (int i = 0; i < list.size(); i++) {
			HashMap temphm = (HashMap) list.get(i);
			String id = String.valueOf(temphm.get("KUNNR")).trim();
			String warehouse = String.valueOf("Z25".equals((String) temphm
					.get("BRAN1")) ? "0" : "1");
			params.add(new Object[] { warehouse, id });
		}

		int[] updates = getiCustomerJdbcOperations().batchUpdate(
				"update CUSTOMER_INFO_TBL set WAREHOUSE=? where ID=?", params);
		logger.info("修改笔数: " + updates.length);
	}

	private void synccustomerinfo_cussystem() throws SQLException {
		List list = sapDAO.getSAPData3("ZRFC12_1", "ZST012");

		if (list == null || list.size() == 0) {
			String msg = "读取 ZRFC12_1 ZST012 无数据";
			logger.error(msg);
			// throw new WantBatchException(new WantBatchException(msg));
		}

		ArrayList<Object[]> params = new ArrayList<Object[]>();
		for (int i = 0; i < list.size(); i++) {
			HashMap temphm = (HashMap) list.get(i);
			String id = ((String) temphm.get("KUNNR")).trim();
			String customerSystem = ((String) temphm.get("PLTYP")).trim() + "-"
					+ ((String) temphm.get("PTEXT")).trim();
			params.add(new Object[] { customerSystem, id });
		}

		int[] updates = getiCustomerJdbcOperations().batchUpdate(
				"update CUSTOMER_INFO_TBL set CUSTOMER_SYSTEM=? where ID=?",
				params);
		logger.info("修改笔数: " + updates.length);
	}
}
