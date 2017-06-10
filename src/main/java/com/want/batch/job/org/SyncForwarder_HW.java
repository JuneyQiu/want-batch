package com.want.batch.job.org;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.org.pojo.ForwarderInfo;

@Component
public class SyncForwarder_HW extends AbstractWantJob {

	@Override
	public void execute() throws SQLException {
		syncForwarderInfo();
	}

	private void syncForwarderInfo() throws SQLException {
		// 1、更新客户信息
		updateForwarder();
		// //2、同步新增的客户信息
		insertNewForwarder();
		// //3、同步客户的渠道信息
		insertForwarderProject();

	}

	private void insertNewForwarder() throws SQLException {
		StringBuffer sql = new StringBuffer(
				"INSERT INTO HW09.FORWARDER_INFO_TBL ");
		sql.append("SELECT HW09.FORWARDER_INFO_TBL_SEQ.NEXTVAL, A.BRANCH_SID,substr(A.ID,3,8),A.NAME,A.OWNER, A.ZIP1, A.PHONE1, NULL,NULL,");
		sql.append("A.MOBILE,NULL,NULL,NULL,A.ADDRESS1,NULL,A.STATUS,'MIS',SYSDATE,NULL,NULL,NULL,NULL,NULL,NULL,A.FORWARDER_ID_B FROM (");
		sql.append("SELECT C.SID AS BRANCH_SID ,A.ID, A.NAME,A.OWNER,SUBSTR(PHONE,0,INSTR(PHONE,'-')-1) AS ZIP1, ");
		sql.append("SUBSTR(PHONE,INSTR(PHONE,'-')+1,LENGTH(PHONE)) AS PHONE1, ");
		sql.append("A.MOBILE,A.ADDRESS1,ID_FRIEND AS FORWARDER_ID_B,'1' AS STATUS ");
		sql.append("from ICUSTOMER.CUSTOMER_INFO_TBL A INNER JOIN ICUSTOMER.FULL_MARKET_LEVEL_TEMP B ON SUBSTR(SUBCITY_NAME,0,4) = B.THIRD_ID ");
		sql.append("INNER JOIN HW09.BRANCH_INFO_TBL C ON B.BRANCH_ID = C.SAP_ID ");
		sql.append("LEFT JOIN HW09.FORWARDER_INFO_TBL D ON A.ID =  '00'|| D.FORWARDER_ID  ");
		sql.append("WHERE A.STATUS IS NULL AND A.NAME NOT LIKE '%样赠%' AND D.FORWARDER_ID IS NULL  AND  A.ID LIKE '00%' ) A");

		int insertCount = this.getHw09JdbcOperations().update(sql.toString());
		logger.info("insertNewForwarder count: " + insertCount);
	}

	private void updateForwarder() throws SQLException {
		Map<String, String> hwMap = getAllForwarderMap(); // 数据库里面已有的customer信息
		Map<String, ForwarderInfo> icustomerMap = getCustomerFromICustomer();
		List<ForwarderInfo> updateList = new ArrayList<ForwarderInfo>();
		List<String> replaceList = new ArrayList<String>();

		Iterator forwaraderIds = hwMap.keySet().iterator();
		while (forwaraderIds.hasNext()) {
			String forwaraderId = (String) forwaraderIds.next();
			ForwarderInfo forwarderInfo = icustomerMap.get(forwaraderId);
			if (null != forwarderInfo) {
				updateList.add(forwarderInfo);
			} else {
				replaceList.add(forwaraderId);
			}
		}

		String updateSql = "UPDATE  HW09.FORWARDER_INFO_TBL SET BRANCH_SID = ?,  FORWARDER_NAME = ?,  "
				+ "OWNER = ?, ZIP1 = ?,  PHONE1 = ?,  MOBILE1 = ?,  ADDRESS = ?, STATUS = ?, "
				+ "UPDATOR = 'mis', UPDATE_DATE = sysdate, FORWARDER_ID_B = ? WHERE  FORWARDER_ID = ? ";

		int i = 0;
		for (ForwarderInfo f : updateList) {
			this.getHw09JdbcOperations().update(
					updateSql,
					new Object[] { f.getBranchSid(), f.getName(), f.getOwner(),
							f.getZip1(), f.getPhone1(), f.getMobile(),
							f.getAddress1(), f.getStatus(),
							f.getForwarderIdB(), f.getId().substring(2) });
			i++;
		}

		logger.info("需修改客户笔数: " + updateList.size() + ", 实际修改客户笔数：" + i);

		String replaceSql = "UPDATE  HW09.FORWARDER_INFO_TBL SET STATUS = '0'  WHERE  FORWARDER_ID = ? ";

		int m = 0;
		for (String fid : replaceList) {
			String forwarderId =fid;
			if(fid.startsWith("00")){
				forwarderId= fid.substring(2, fid.length());
			}
			logger.info("汰换客户编码："+forwarderId);

			this.getHw09JdbcOperations().update(replaceSql,	new Object[] {forwarderId});
			m++;
		}

		logger.info("需修改客户汰换状态笔数: " + replaceList.size() + ", 实际修改客户笔数：" + m);

	}

	private Map<String, ForwarderInfo> getCustomerFromICustomer() {
		Map<String, ForwarderInfo> map = new HashMap<String, ForwarderInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT C.SID AS BRANCH_SID ,A.ID, A.NAME,A.OWNER,SUBSTR(PHONE,0,INSTR(PHONE,'-')-1) AS ZIP1, ");
		sql.append("SUBSTR(PHONE,INSTR(PHONE,'-')+1,LENGTH(PHONE)) AS PHONE1,A.MOBILE, ");
		sql.append("A.ADDRESS1,ID_FRIEND AS FORWARDER_ID_B,DECODE(TRIM(A.STATUS),'01','0','1') AS STATUS ");
		sql.append("from ICUSTOMER.CUSTOMER_INFO_TBL A INNER JOIN ICUSTOMER.FULL_MARKET_LEVEL_TEMP B ON SUBSTR(SUBCITY_NAME,0,4) = B.THIRD_ID ");
		sql.append("INNER JOIN HW09.BRANCH_INFO_TBL C ON B.BRANCH_ID = C.SAP_ID ");
		sql.append("WHERE  A.NAME NOT LIKE '%样赠%' ");
		List<Map<String, Object>> list = this.getHw09JdbcOperations()
				.queryForList(sql.toString());
		for (Map<String, Object> m : list) {
			ForwarderInfo f = new ForwarderInfo();
			f.setId(m.get("ID").toString().trim());
			f.setAddress1((null == m.get("ADDRESS1")) ? "" : m.get("ADDRESS1")
					.toString().trim());
			f.setBranchSid(Long
					.parseLong(m.get("BRANCH_SID").toString().trim()));
			f.setForwarderIdB((null == m.get("FORWARDER_ID_B")) ? "" : m
					.get("FORWARDER_ID_B").toString().trim());
			f.setMobile((null == m.get("MOBILE")) ? "" : m.get("MOBILE")
					.toString().trim());
			f.setName(m.get("NAME").toString().trim());
			f.setOwner((null == m.get("OWNER")) ? "" : m.get("OWNER")
					.toString().trim());
			f.setPhone1((null == m.get("PHONE1")) ? "" : m.get("PHONE1")
					.toString().trim());
			f.setStatus(m.get("STATUS").toString().trim());
			f.setZip1((null == m.get("ZIP1")) ? "" : m.get("ZIP1").toString()
					.trim());
			map.put(f.getId(), f);
		}
		return map;
	}

	private Map<String, String> getAllForwarderMap() {
		Map<String, String> map = new HashMap<String, String>();
		String sql = "SELECT SID , FORWARDER_ID as FORWARDER_ID FROM HW09.FORWARDER_INFO_TBL WHERE STATUS='1' and FORWARDER_NAME not like '%样赠%'";
		List<Map<String, Object>> list = this.getHw09JdbcOperations()
				.queryForList(sql.toString());
		for (Map<String, Object> m : list) {
			if(("00"+m.get("FORWARDER_ID").toString().trim()).length()>10){
				logger.info("FORWARDER_ID: "+"00"+m.get("FORWARDER_ID").toString().trim());
			}
			map.put("00"+m.get("FORWARDER_ID").toString().trim(), m.get("SID")
					.toString().trim());
		}
		return map;
	}

	private void insertForwarderProject() {
		String sql = "INSERT INTO  HW09.FORWARDER_PROJECT_TBL VALUES(  ?,  ? )";
		// 获取所有的客户信息
		Map<String, String> fmap = getAllForwarderMap();
		Set<String> set = fmap.keySet();
		for (String s : set) {
			String fsid = fmap.get(s);
			// 获取合旺原有的客户渠道
			Set<String> oldProjectSid = getForwarderProjectFromHw(fsid);
			// 获取icustomer中的客户渠道
			Set<String> newProjectSid = getForwarderProjectFromCustomer(fsid);
			// 合并客户的渠道
			newProjectSid.addAll(oldProjectSid);
			newProjectSid.removeAll(oldProjectSid);
			for (String psid : newProjectSid) {
				this.getHw09JdbcOperations().update(sql,
						new Object[] { fsid, psid });
			}
			logger.debug("id: " + s + " ,sid: " + fsid + ", 渠道新增了："
					+ newProjectSid.size());
		}

	}

	private Set<String> getForwarderProjectFromCustomer(String forwarderSid) {
		Set<String> projectsids = new HashSet<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT  DISTINCT A.SID,D.PROJECT_SID ");
		sql.append("FROM HW09.FORWARDER_INFO_TBL A ");
		sql.append("INNER JOIN ICUSTOMER.DIVSION_SALES_CUSTOMER_REL B ON '00'|| A.FORWARDER_ID = B.CUSTOMER_ID ");
		sql.append("INNER JOIN ICUSTOMER.SALES_AREA_REL C ON B.CREDIT_ID = C.CREDIT_ID ");
		sql.append("INNER JOIN ICUSTOMER.DIVSION_PROJECT_REL D ON C.DIVSION_SID = D.DIVSION_SID WHERE A.SID = ? ");

		List<Map<String, Object>> list = this.getHw09JdbcOperations()
				.queryForList(sql.toString(), new Object[] { forwarderSid });
		for (Map<String, Object> map : list) {
			projectsids.add(map.get("PROJECT_SID").toString());
		}
		return projectsids;
	}

	private Set<String> getForwarderProjectFromHw(String forwarderSid) {
		Set<String> projectsids = new HashSet<String>();
		String sql = "SELECT FORWARDER_SID,PROJECT_SID FROM HW09.FORWARDER_PROJECT_TBL WHERE FORWARDER_SID = ? ";
		List<Map<String, Object>> list = this.getHw09JdbcOperations()
				.queryForList(sql.toString(), new Object[] { forwarderSid });
		for (Map<String, Object> map : list) {
			projectsids.add(map.get("PROJECT_SID").toString());
		}
		return projectsids;
	}
}
