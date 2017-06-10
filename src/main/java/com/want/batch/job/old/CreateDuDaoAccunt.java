// ~ Package Declaration
// ==================================================

package com.want.batch.job.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

// ~ Comments
// ==================================================

/**
 * 分公司督导资料同步.
 * 
 * <table>
 * <tr>
 * <th>日期</th>
 * <th>变更说明</th>
 * </tr>
 * <tr>
 * <td>2015-7-27</td>
 * <td>mirabelle新建</td>
 * </tr>
 * </table>
 * 
 *@author mirabelle
 */
@Component
public class CreateDuDaoAccunt extends AbstractWantJob {

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/*
	 * 2015-7-27 D11-mirabelle
	 * 
	 * @see com.want.batch.job.WantJob#execute()
	 */
	@Override
	public void execute() throws Exception {

		// 取得督导
		String sqlDuDao = new StringBuilder()
			.append("	SELECT")
			.append("		EMP_POSITION.EMP_ID,")
			.append("   EMP.EMP_NAME,")
			.append("		ORGANIZATION_B.ORG_ID,")
			.append("		ORGANIZATION_B.ORG_NAME")
			.append("	FROM")
			.append("		EMP_POSITION")
			.append("			INNER JOIN POSITION_B")
			.append("				ON EMP_POSITION.POS_ID=POSITION_B.POS_ID ")
//			.append("   and POSITION_B.POS_NAME like '%促销督导%'")
			.append("					AND POSITION_B.POS_TYPE_ID='" + RoleConstant.DUDAO + "'")
			.append("					AND POSITION_B.POS_PROPERTY_ID='" + RoleConstant.SANZHUANGYINGXIAO + "'")
			.append("			INNER JOIN ORGANIZATION_B")
			.append("				ON POSITION_B.ORG_ID=ORGANIZATION_B.ORG_ID")
			.append("			INNER JOIN EMP")
			.append("       ON EMP_POSITION.EMP_ID=EMP.EMP_ID")
			.toString();
		
		// 取得useR_info_tbl中是否有这个人
		String sqlUser = "SELECT COUNT(*) USERCOUNT FROM USER_INFO_TBL WHERE ACCOUNT=?";
		
		// 向user_info_tbl中新增
		String sqlInsertUser = new StringBuilder()
			.append("	INSERT INTO USER_INFO_TBL ")
			.append(" (SID,ACCOUNT,PASSWORD,USER_NAME,USER_TYPE_SID,EMAIL,MOBILE,AREA_CODE,PHONE_NUM,STATUS,")
			.append(" UPDATE_DATE,UPDATOR,CREATE_DATE,CREATOR,LAST_LOGIN_TIME,LAST_LOGIN_ADDS,CHANNEL_SID)  ")
		  .append(" select USER_INFO_TBL_SID.nextval,?,USER_INFO_TBL_SID.currval,?,33, null,null,null,null,1,")
		  .append("null,null,sysdate,'mis',null,null,1  from dual ")
		  .toString();
		
		// update user_info_tbl
		String sqlUpdateUser = "UPDATE  USER_INFO_TBL SET STATUS=1, USER_TYPE_SID=33, CHANNEL_SID=1, UPDATE_DATE=SYSDATE, UPDATOR='MIS' WHERE ACCOUNT=?";
		
		// select company id
		String selectCompanyId = new StringBuilder()
			.append("	SELECT")
			.append("		DISTINCT COMPANY_SAP_ID")
			.append("	FROM")
			.append("		COMPANY_BRANCH")
			.append("	WHERE")
			.append("		COMPANY_HR_ID = ?")
			.toString();
		
		// delete company_emp
		String delCompanyEmp = " DELETE FROM COMPANY_EMP WHERE EMP_ID=?";
		
		// insert company_emp
		String insertCompanyEmp = "INSERT INTO COMPANY_EMP (COMPANY_SAP_ID,EMP_ID) VALUES (?,?)";
		
		// delete USER_DIVISION
		String delUserDivision = "DELETE FROM USER_DIVISION WHERE USER_ID = ?";
		
		// insert USER_DIVISION
		String insertUserDivision = "INSERT INTO USER_DIVISION (USER_ID,DIVISION_ID,CREATE_USER,CREATE_DATE,UPDATE_USER,UPDATE_DATE) VALUES (?,14,'MIS-SYS',SYSDATE,null,null)";
		
		// delete AUTH_USER_GROUP_REL
		String delUserGroup = "DELETE AUTH_USER_GROUP_REL WHERE USER_SID =(SELECT SID FROM USER_INFO_TBL WHERE ACCOUNT = ?)";
		
		// insert AUTH_USER_GROUP_REL
		String insertUserGroup = "INSERT INTO AUTH_USER_GROUP_REL(USER_SID, USERGROUP_SID, CREATE_USER, CREATE_DATE)  SELECT SID ,'3019', 'mis', SYSDATE  FROM  USER_INFO_TBL WHERE ACCOUNT =? ";
		
		Connection conn = getICustomerConnection();
		PreparedStatement pstmt = conn.prepareStatement(sqlDuDao);
		ResultSet rs = pstmt.executeQuery();
		List<Map<String, String>> empLst = new ArrayList<Map<String, String>>();
		
		while (rs.next()) {
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("EMP_ID", rs.getString("EMP_ID"));
			map.put("EMP_NAME", rs.getString("EMP_NAME"));
			map.put("ORG_ID", rs.getString("ORG_ID"));
			map.put("ORG_NAME", rs.getString("ORG_NAME"));
			empLst.add(map);
		}

		rs.close();
		pstmt.close();
		
		for (int i = 0; i < empLst.size(); i++) {
			
			Map<String, String> empMap = empLst.get(i);
			
			// 判断user_info_tbl中是否有这个人
			pstmt = conn.prepareStatement(sqlUser);
			pstmt.setString(1, empMap.get("EMP_ID"));
			rs = pstmt.executeQuery();
			int countUser = 0;
			
			if (rs.next()) {
			
				countUser = rs.getInt("USERCOUNT");
			}
			
			rs.close();
			pstmt.close();
			
			// 如果员工不存在，新增员工
			if(countUser == 0) {
				
				pstmt = conn.prepareStatement(sqlInsertUser);
				pstmt.setString(1, empMap.get("EMP_ID"));
				pstmt.setString(2, empMap.get("EMP_NAME"));
				pstmt.executeUpdate();
			}
			else {
				
				pstmt = conn.prepareStatement(sqlUpdateUser);
				pstmt.setString(1, empMap.get("EMP_ID"));
				pstmt.executeUpdate();
			}
			
			pstmt.close();
			
			// 取得人员对应的分公司，先删后增
			pstmt = conn.prepareStatement(delCompanyEmp);
			pstmt.setString(1, empMap.get("EMP_ID"));
			pstmt.executeUpdate();
			pstmt.close();
			
			// 查询出人员对应的分公司
			String orgId = this.getCompanyId(empMap.get("ORG_ID"), "分公司", conn);
			
			String companyId = ""; 
			pstmt = conn.prepareStatement(selectCompanyId);
			pstmt.setString(1, orgId);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				
				companyId = rs.getString("COMPANY_SAP_ID");
			}
			
			logger.debug("CreateDuDaoAccunt--" + new Date() + ";人员所属的分公司;EMP_ID:" + empMap.get("EMP_ID") + ";orgId:" + orgId + ";companyId:" + companyId);
			rs.close();
			pstmt.close();
			
			// 插入人员对应的分公司
			pstmt = conn.prepareStatement(insertCompanyEmp);
			pstmt.setString(1, companyId);
			pstmt.setString(2, empMap.get("EMP_ID"));
			pstmt.executeUpdate();
			pstmt.close();
			
			// 新增人员渠道，先删后增
			pstmt = conn.prepareStatement(delUserDivision);
			pstmt.setString(1, empMap.get("EMP_ID"));
			pstmt.executeUpdate();
			pstmt.close();
			
			// 新增人员渠道
			pstmt = conn.prepareStatement(insertUserDivision);
			pstmt.setString(1, empMap.get("EMP_ID"));
			pstmt.executeUpdate();
			pstmt.close();
			
			// 维护权限,先删后增
			pstmt = conn.prepareStatement(delUserGroup);
			pstmt.setString(1, empMap.get("EMP_ID"));
			pstmt.executeUpdate();
			pstmt.close();
			
			pstmt = conn.prepareStatement(insertUserGroup);
			pstmt.setString(1, empMap.get("EMP_ID"));
			pstmt.executeUpdate();
			pstmt.close();
		}
	}

	/**
	 * <pre>
	 * 2015-7-28 mirabelle
	 * 递归取得分公司.
	 * </pre>	
	 * 
	 * @param org_id
	 * @param key_word
	 * @param conn
	 * @return org_id
	 * @throws SQLException
	 */
	public String getCompanyId(String org_id, String key_word, Connection conn) throws SQLException {

		String target_id = "";
		String org_name = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String searchOrg = "select a.ORG_ID as ORG_ID, a.ORG_NAME as ORG_NAME from ORGANIZATION_B a  where a.ORG_ID=? ";
		pstmt = conn.prepareStatement(searchOrg);
		pstmt.setString(1, org_id);
		rs = pstmt.executeQuery();
		
		if (rs.next()) {
			target_id = rs.getString("ORG_ID");
			org_name = rs.getString("ORG_NAME");
		}
		
		rs.close();
		pstmt.close();

		String searchParentOrg = new StringBuilder()
			.append("	select")
			.append("		a.ORG_ID as ORG_ID,")
			.append("		a.ORG_NAME as ORG_NAME")
			.append("	from")
			.append("		ORGANIZATION_B a")
			.append("			inner join ORGANIZATION_B b")
			.append("				on a.ORG_ID=b.ORG_PARENT_DEPT")
			.append("	where b.ORG_ID=?")
			.toString();
		
		if (org_name != null && org_name.lastIndexOf(key_word) < 0) {
			
			pstmt = conn.prepareStatement(searchParentOrg);
			pstmt.setString(1, org_id);
			rs = pstmt.executeQuery();
		
			if (rs.next()) {
			
				String r_org_id = rs.getString("ORG_ID");
				org_name = rs.getString("ORG_NAME");
				target_id = getCompanyId(r_org_id, key_word, conn);
			}
			
			rs.close();
			pstmt.close();
		}
		
		return target_id;
	}
}
