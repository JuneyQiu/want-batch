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
import com.want.batch.job.org.pojo.EmpInfo;
import com.want.batch.job.org.pojo.ForwarderInfo;

@Component
public class SyncEmp_HW extends AbstractWantJob {

	@Override
	public void execute() throws SQLException {
		syncEmpInfo();
	}

	private void syncEmpInfo() throws SQLException {
		//1、更新离职业代的状态
		updateLeavlEmpStatus();
		//2、更新只有一个营业所的业代信息
		updateEmp();
		//3、更新有多个营业所的业代信息
		updateZYEmp();
		//4、新增只有一个营业所的业代信息
		insertNewEmp();
		//5、新增只有多个营业所的业代信息
		insertZYEmp();
		
	}

	/**
	 * 新增业代
	 * 同步业务三网只有一个营业所的 业代、主任、所长
	 */
	private void insertNewEmp() {
		StringBuffer sql = new StringBuffer("INSERT INTO HW09.EMP_INFO_TBL  ");
		sql.append("SELECT HW09.EMP_INFO_TBL_SEQ.NEXTVAL,A.EMP_ID,A.EMP_NAME,A.EMP_TYPE_SID,null,null,A.MOBILE1,null,A.STATUS, ");
		sql.append("A.BRANCH_SID,'mis',sysdate,null,null,null,null,null,null,null,null ");
		sql.append("FROM (");
		sql.append("SELECT A.EMP_ID,A.EMP_NAME,C.TYPE_NAME ,D.SID AS EMP_TYPE_SID,A.EMP_MOBILE AS MOBILE1,'1' AS STATUS, F.SID AS BRANCH_SID ");
		sql.append("FROM ICUSTOMER.EMP A INNER JOIN ICUSTOMER.USER_INFO_TBL B ON A.EMP_ID = B.ACCOUNT ");
		sql.append("INNER JOIN ICUSTOMER.USER_TYPE_TBL C ON B.USER_TYPE_SID = C.SID ");
		sql.append("LEFT JOIN HW09.EMP_TYPE_TBL D ON C.TYPE_NAME = D.EMP_TYPE_NAME ");
		sql.append("INNER JOIN ICUSTOMER.BRANCH_EMP E ON A.EMP_ID = E.EMP_ID ");
		sql.append("INNER JOIN HW09.BRANCH_INFO_TBL F ON E.BRANCH_SAP_ID = F.SAP_ID AND F.STATUS='1' ");
		sql.append("LEFT JOIN HW09.EMP_INFO_TBL G ON A.EMP_ID = G.EMP_ID ");
		sql.append("WHERE C.SID in (2,5,6) and A.EMP_ID in (select emp_id from icustomer.branch_emp  group by emp_id having count(branch_sap_id)=1) ");
		sql.append("AND G.EMP_ID IS  NULL ) A");
		
		logger.info("insertNewEmp sql: "+sql.toString());
		int insertEmp = this.getHw09JdbcOperations().update(sql.toString());
		logger.info("insertNewEmp 新增业代个数："+insertEmp);
	}

	/**
	 * 新增业代（直营、现渠业代）
	 * 同步业务三网中有多个营业所的业代信息，取其营本所作为其营业所
	 */
	private void insertZYEmp() {
		StringBuffer sql = new StringBuffer("INSERT INTO HW09.EMP_INFO_TBL  ");
		sql.append("SELECT HW09.EMP_INFO_TBL_SEQ.NEXTVAL,A.EMP_ID,A.EMP_NAME,A.EMP_TYPE_SID,0,0,A.MOBILE1,null,A.STATUS, ");
		sql.append("A.BRANCH_SID,'mis',sysdate,null,null,null,null,null,null,null,null  ");
		sql.append("FROM ( ");
		sql.append("SELECT A.EMP_ID,A.EMP_NAME,C.TYPE_NAME ,D.SID AS EMP_TYPE_SID,A.EMP_MOBILE AS MOBILE1,'1' AS STATUS, F.SID AS BRANCH_SID , E.BRANCH_SAP_ID ");
		sql.append("FROM ICUSTOMER.EMP A INNER JOIN ICUSTOMER.USER_INFO_TBL B ON A.EMP_ID = B.ACCOUNT ");
		sql.append("INNER JOIN ICUSTOMER.USER_TYPE_TBL C ON B.USER_TYPE_SID = C.SID ");
		sql.append("LEFT JOIN HW09.EMP_TYPE_TBL D ON C.TYPE_NAME = D.EMP_TYPE_NAME  ");
		sql.append("INNER JOIN (select EMP_ID, min(BRANCH_SAP_ID) as BRANCH_SAP_ID from icustomer.branch_emp group by EMP_ID) E ON A.EMP_ID = E.EMP_ID ");
		sql.append("INNER JOIN HW09.BRANCH_INFO_TBL F ON E.BRANCH_SAP_ID = F.SAP_ID AND F.STATUS='1' ");
		sql.append("LEFT JOIN HW09.EMP_INFO_TBL G ON A.EMP_ID = G.EMP_ID ");
		sql.append("WHERE C.SID in (2) and A.EMP_ID in (select emp_id from icustomer.branch_emp  group by emp_id having count(branch_sap_id)>1) ");
		sql.append("AND G.EMP_ID IS  NULL ) A ");
		
		logger.info("insertZYEmp sql: "+sql.toString());
		int insertZYEmp = this.getHw09JdbcOperations().update(sql.toString());
		logger.info("insertZYEmp 新增业代个数："+insertZYEmp);
	}

	/**
	 * 更新业务三网只有一个营业所的业代
	 */
	private void updateEmp() throws SQLException {
		Map<String, String> hwMap = getEmpFromHW(); // 数据库里面已有的customer信息
		Map<String, EmpInfo> empMap = getEmpFromIcustomer();
		List<EmpInfo> updateList = new ArrayList<EmpInfo>();
		Iterator empIds = hwMap.keySet().iterator();
		while (empIds.hasNext()) {
			String empId = (String) empIds.next();
			EmpInfo empInfo = empMap.get(empId);
			if (null != empInfo) {
				updateList.add(empInfo);
			}
		}
		String updateSql =" UPDATE  HW09.EMP_INFO_TBL SET EMP_NAME = ?,EMP_TYPE_SID = ?, MOBILE1 = ?, STATUS = ?, BRANCH_SID = ? WHERE EMP_ID= ? ";
		int i = 0;
		for (EmpInfo e : updateList) {
			this.getHw09JdbcOperations().update(
					updateSql,
					new Object[] {e.getEmpName(),e.getEmpTypeSid(),e.getMobile(),e.getStatus(),e.getBranchSid(),e.getEmpId()});
			i++;
		}
		logger.info("需修改业代笔数: " + updateList.size() + ", 实际修改业代笔数：" + i);
	}
	
	/**
	 * 更新业务三网有多个营业所的业代
	 * @throws SQLException
	 */
	private void updateZYEmp() throws SQLException {
		Map<String, String> hwMap = getEmpFromHW(); // 数据库里面已有的customer信息
		Map<String, EmpInfo> empMap = getZYEmpFromIcustomer();
		List<EmpInfo> updateList = new ArrayList<EmpInfo>();
		Iterator empIds = hwMap.keySet().iterator();
		while (empIds.hasNext()) {
			String empId = (String) empIds.next();
			EmpInfo empInfo = empMap.get(empId);
			if (null != empInfo) {
				updateList.add(empInfo);
			}
		}
		String updateSql =" UPDATE  HW09.EMP_INFO_TBL SET EMP_NAME = ?,EMP_TYPE_SID = ?, MOBILE1 = ?, STATUS = ?, BRANCH_SID = ? WHERE EMP_ID= ? ";
		int i = 0;
		for (EmpInfo e : updateList) {
			this.getHw09JdbcOperations().update(
					updateSql,
					new Object[] {e.getEmpName(),e.getEmpTypeSid(),e.getMobile(),e.getStatus(),e.getBranchSid(),e.getEmpId()});
			i++;
		}
		logger.info("需修改业代笔数: " + updateList.size() + ", 实际修改业代笔数：" + i);
	}


	/**
	 * 更新合旺离职业代的状态
	 */
	private void updateLeavlEmpStatus(){
		StringBuffer sql =new StringBuffer();
		sql.append(" UPDATE  HW09.EMP_INFO_TBL SET  STATUS = '0'  WHERE SID IN (");
		sql.append("SELECT A.SID FROM HW09.EMP_INFO_TBL A LEFT JOIN ICUSTOMER.EMP B ON A.EMP_ID = B.EMP_ID ");
		sql.append("WHERE B.EMP_ID IS NULL AND A.STATUS <> '0' and A.EMP_ID NOT LIKE '%XX%' AND LENGTH(A.EMP_ID)=8 )");
		int i = this.getHw09JdbcOperations().update(sql.toString());
		
		logger.info("业代状态为离职的数量位: " + i);
	}
	
	/**
	 * 业务三网只有一个营业所的 业代、主任、所长
	 * @return
	 */
	private Map<String, EmpInfo> getEmpFromIcustomer() {
		Map<String, EmpInfo> map = new HashMap<String, EmpInfo>();
		StringBuffer sql = getEmpSqlFromIcustomer()
		.append("WHERE C.SID in (2,5,6) and A.EMP_ID in (select emp_id from icustomer.branch_emp  group by emp_id having count(branch_sap_id)=1) ");
		logger.info("getEmpFromIcustomer: " + sql.toString());
		List<Map<String, Object>> list = this.getHw09JdbcOperations()
				.queryForList(sql.toString());
		for (Map<String, Object> m : list) {
			EmpInfo e = new EmpInfo();
			e.setEmpId(m.get("EMP_ID").toString().trim());
			e.setEmpName(m.get("EMP_NAME").toString().trim());
			e.setEmpTypeSid(Long.parseLong(m.get("EMP_TYPE_SID").toString()
					.trim()));
			e.setBranchSid(Long
					.parseLong(m.get("BRANCH_SID").toString().trim()));
			e.setMobile((null == m.get("MOBILE1")) ? "" : m.get("MOBILE1")
					.toString().trim());
			e.setStatus(m.get("STATUS").toString().trim());
			map.put(e.getEmpId(), e);
		}
		return map;
	}
	
	/**
	 * 业务三网中有多个营业所的业代信息
	 * @return
	 */
	private Map<String, EmpInfo> getZYEmpFromIcustomer() {
		Map<String, EmpInfo> map = new HashMap<String, EmpInfo>();
		StringBuffer sql = getEmpSqlFromIcustomer()
		.append("WHERE C.SID in (2) and A.EMP_ID in (select emp_id from icustomer.branch_emp  group by emp_id having count(branch_sap_id)>1) ");
		List<Map<String, Object>> list = this.getHw09JdbcOperations()
				.queryForList(sql.toString());
		for (Map<String, Object> m : list) {
			EmpInfo e = new EmpInfo();
			e.setEmpId(m.get("EMP_ID").toString().trim());
			e.setEmpName(m.get("EMP_NAME").toString().trim());
			e.setEmpTypeSid(Long.parseLong(m.get("EMP_TYPE_SID").toString()
					.trim()));
			e.setBranchSid(Long
					.parseLong(m.get("BRANCH_SID").toString().trim()));
			e.setMobile((null == m.get("MOBILE1")) ? "" : m.get("MOBILE1")
					.toString().trim());
			e.setStatus(m.get("STATUS").toString().trim());
			map.put(e.getEmpId(), e);
		}
		return map;
	}


	/**
	 * 获取合旺正常在职的业代
	 * @return
	 */
	private Map<String, String> getEmpFromHW() {
		Map<String, String> map = new HashMap<String, String>();
		String sql = "SELECT SID , EMP_ID FROM HW09.EMP_INFO_TBL WHERE STATUS='1' and EMP_ID  not like '%XX%' and length(emp_id)=8";
		List<Map<String, Object>> list = this.getHw09JdbcOperations()
				.queryForList(sql.toString());
		for (Map<String, Object> m : list) {
			map.put(m.get("EMP_ID").toString().trim(), m.get("SID").toString()
					.trim());
		}
		return map;
	}
	
	private StringBuffer getEmpSqlFromIcustomer(){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.EMP_ID,A.EMP_NAME,C.TYPE_NAME ,D.SID AS EMP_TYPE_SID,A.EMP_MOBILE AS MOBILE1,'1' AS STATUS, F.SID AS BRANCH_SID ");
		sql.append("FROM ICUSTOMER.EMP A INNER JOIN ICUSTOMER.USER_INFO_TBL B ON A.EMP_ID = B.ACCOUNT ");
		sql.append("INNER JOIN ICUSTOMER.USER_TYPE_TBL C ON B.USER_TYPE_SID = C.SID ");
		sql.append("LEFT JOIN HW09.EMP_TYPE_TBL D ON C.TYPE_NAME = D.EMP_TYPE_NAME ");
		sql.append("INNER JOIN ICUSTOMER.BRANCH_EMP E ON A.EMP_ID = E.EMP_ID ");
		sql.append("INNER JOIN HW09.BRANCH_INFO_TBL F ON E.BRANCH_SAP_ID = F.SAP_ID AND F.STATUS='1' ");
		sql.append("INNER JOIN HW09.EMP_INFO_TBL G ON A.EMP_ID = G.EMP_ID ");
		
		return sql;
	}
}
