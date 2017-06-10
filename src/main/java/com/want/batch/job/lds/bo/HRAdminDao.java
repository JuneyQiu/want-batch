package com.want.batch.job.lds.bo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

/**
 * @author 00110392
 * 
 */
public class HRAdminDao implements HRDao {


	private static final Log logger = LogFactory.getLog(HRAdminDao.class);

	@Autowired
	private DataSource portalDataSource; // 注入

	@Autowired
	private DataSource iCustomerNWDataSource;

	@Autowired
	private SimpleJdbcOperations iCustomerNWJdbcOperations;

	@Autowired
	private JdbcTemplate portalJdbcTemplate; // 注入

	private static Map<String, Organization> AREA_MAP = null;
	private static Map<String, Position> SUPPORT_MAP = null;

	
	private Map<String, Organization> getAreaMap() {

		if (AREA_MAP != null)
			return AREA_MAP;

		// org_area_id <> ' '代表最大的此组织是area。 而 org_id='660'是投资管理中心。
		String sql = "select org_id,org_name, org_area_id from hrorg.organization where org_area_id <> ' '"
				+ "  or ((org_id like '2%' or org_id like '3%') and org_id like '%0000') or org_id='660' order by org_id";
		AREA_MAP = new HashMap<String, Organization>();

		try {
			List<Map<String, Object>> listO = portalJdbcTemplate.queryForList(sql);

			if (listO.size() > 0) {
				for (int i = 0; i < listO.size(); i++) {
					Map<String, Object> maps = listO.get(i);
					String orgId = (String) maps.get("org_id");
					Organization organization = new Organization();
					organization.setId(orgId);
					organization.setName((String) maps.get("org_name"));
					organization.setAreaId((String) maps.get("org_area_id"));
					organization.setDn(Constants.LDAP_ATTRIBUTE_CN + "=" + orgId + "," + LDAP.orgOU);
					AREA_MAP.put(orgId, organization);
				}

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return AREA_MAP;
	}
	
	private static Map<String, Organization> _ORGS = null;
	
	public Collection<Organization> getOrganizations(String... ids) { 
		if (_ORGS == null)
			prepareOrganizations();
		
		Collection<Organization> orgs = new ArrayList<Organization>();
		
		if (ids != null && ids.length > 0)
			for (String id: ids) {
				Organization org = _ORGS.get(id);
				if (org != null)
					orgs.add(org);
			}
		
		return orgs;
	}

	private static final String ORG_SQL = 
			"SELECT substr(a.org_id,1,4)||'0000' head_id, a.org_id, a.org_name, a.org_parent_dept, substr(a.org_parent_dept,1,4)||'0000' parent_head_id, a.org_level, b.org_id child_org_id FROM hrorg.organization a " +
			" LEFT JOIN hrorg.organization b ON b.org_parent_dept=a.org_id " + " WHERE a.org_id != '00000000' " +
			" ORDER BY a.org_id";
	
	private void prepareOrganizations() {
		
		_ORGS = new HashMap<String, Organization>();
		
		Map<?, ?> areaMap = getAreaMap();

		try {

			List<Map<String, Object>> listO = portalJdbcTemplate.queryForList(ORG_SQL);

			if (listO.size() > 0) {
				for (int i = 0; i < listO.size(); i++) {
					Map<String, Object> maps = listO.get(i);

					String orgId = (String) maps.get("org_id");

					if (_ORGS.get(orgId) == null) {
						String headId = (String) maps.get("head_id");
						String parentHeadId = (String) maps.get("parent_head_id");
						if ((orgId.startsWith("2") || orgId.startsWith("3")) && !parentHeadId.startsWith("1")
								&& !headId.equals(parentHeadId))
							headId = parentHeadId;
						if (orgId != null && orgId.startsWith("1"))
							headId = "10000000";
						if (orgId != null && !orgId.startsWith("TW") && !orgId.startsWith("1") && !orgId.startsWith("2")
								&& !orgId.startsWith("3"))
							headId = "660";

						if (headId != null && areaMap.get(headId) != null) {
							Organization organization = new Organization();
							organization.setId(orgId);
							organization.setDn(Constants.LDAP_ATTRIBUTE_CN + "=" + orgId + "," + LDAP.orgOU);
							organization.setName((String) maps.get("org_name"));
							organization.setOrgLevel((String) maps.get("org_level"));
							Organization area = (Organization) areaMap.get(headId);
							organization.setOu("CN=" + orgId + "," + area.getOu());
							organization.setAreaId(area.getAreaId());
							organization.setParentDept((String) maps.get("org_parent_dept"));
							_ORGS.put(orgId, organization);
						}
					}

					Organization organization = _ORGS.get(orgId);
					if (organization.getSubOrgIds() == null)
						organization.setSubOrgIds(new ArrayList<String>());
					String child = (String) maps.get("child_org_id");
					if (child != null)
						organization.getSubOrgIds().add(child);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Collection<Position> getPositionsByOrg(String... ids) {
		if (_ORG_POSES == null)
			preparePosition();
		
		Collection<Position> poss = new ArrayList<Position>();
		
		if (ids != null && ids.length > 0)
			for (String id: ids) {
				Collection<Position> list = _ORG_POSES.get(id);
				if (list != null)
					poss.addAll(list);
			}
		
		return poss;

	}

	private static Map<String, Position> _POSES = null;
	
	private static Map<String, Collection<Position>> _ORG_POSES = null;
	
	//private Map<String, Position> _POSES = null;
	
	public Collection<Position> getPositionsById(String... ids) {
		if (_POSES == null)
			preparePosition();
		
		Collection<Position> poss = new ArrayList<Position>();
		
		if (ids != null && ids.length > 0)
			for (String id: ids) {
				Position pos = _POSES.get(id);
				if (pos != null)
					poss.add(pos);
			}
		
		return poss;
	}
	
	private static final String POS_SQL = 
			"SELECT distinct substr(organization.org_id,1,4)||'0000' head_id, pos.pos_id, pos.pos_name, pos.org_id, dept_director_flag, substr(org_parent_dept,1,4)||'0000' parent_head_id, " +
			" division_director_flag, top_director_flag, master_pos, director_emp_id, emp_pos.director_pos_id, pos.POS_PROPERTY_ID,pos.POS_PROPERTY_NAME,pos.DIRECTOR_POS_ID pos_DIRECTOR_POS_ID " +
			" FROM hrorg.position pos,hrorg.emp_position emp_pos,hrorg.organization " +
			" WHERE pos.pos_id = emp_pos.pos_id AND pos.pos_id != '00000000'" +
			" AND pos.org_id = organization.org_id " +
			" ORDER BY pos.pos_id ";


	private void preparePosition() {
		_POSES = new HashMap<String, Position>();
		_ORG_POSES = new HashMap<String, Collection<Position>>();
		
		Map<String, Position> supportMap = getSupportMap();

		Map<String, Organization> areaMap = getAreaMap();

		try {
			Connection conn = portalDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(POS_SQL);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {

				String posId = rs.getString("pos_id");
				String headId = rs.getString("head_id");
				String parentHeadId = rs.getString("parent_head_id");
				String orgId = rs.getString("org_id");

				if ((orgId.startsWith("2") || orgId.startsWith("3")) && !parentHeadId.startsWith("1")
						&& !headId.equals(parentHeadId))
					headId = parentHeadId;

				if (orgId != null && orgId.startsWith("1"))
					headId = "10000000";
				if (orgId != null && orgId.startsWith("TW") && orgId.length() > 3)
					headId = orgId.substring(0, 3);
				if (orgId != null && !orgId.startsWith("TW") && !orgId.startsWith("1") && !orgId.startsWith("2")
						&& !orgId.startsWith("3")) {
					headId = "660";
				}
				if (headId != null && areaMap.get(headId) != null) {
					
					Position position = _POSES.get(posId);
					if (position == null) {
						position = new Position();
						_POSES.put(posId, position);
					} else
						continue;

					Collection<Position> poses = _ORG_POSES.get(orgId);
					if (poses == null) {
						poses = new ArrayList<Position>();
						_ORG_POSES.put(orgId, poses);
						poses.add(position);
					}
					
					position.setDn(Constants.LDAP_ATTRIBUTE_CN + "=" + posId + "," + LDAP.posOU);
					position.setId(posId);
					position.setName(rs.getString("pos_name"));
					position.setOrgId(orgId);
					position.setMasterId(rs.getString("master_pos"));

					/*
					 * 添加PropertyId,PropertyName
					 */
					position.setPos_property_id(rs.getString("POS_PROPERTY_ID"));
					position.setPos_property_name(rs.getString("POS_PROPERTY_NAME"));
					position.setDirector_pos_id(rs.getString("pos_DIRECTOR_POS_ID"));

					// 判断部门主管
					String deptDirectorFlag = rs.getString("dept_director_flag");
					if (deptDirectorFlag != null && deptDirectorFlag.equals("1"))
						position.setDepartmentDirector(true);
					else
						position.setDepartmentDirector(false);
					// 判断单位主管
					String divisionDirectorFlag = rs.getString("division_director_flag");
					if (divisionDirectorFlag != null && divisionDirectorFlag.equals("1"))
						position.setDivisionDirector(true);
					else
						position.setDivisionDirector(false);
					// 判断单位最高主管
					String topDirectorFlag = rs.getString("top_director_flag");
					if (topDirectorFlag != null && topDirectorFlag.equals("1"))
						position.setTopDirector(true);
					else
						position.setTopDirector(false);
					position.setSupervisor(rs.getString("director_emp_id"));
					position.setSupervisorPosId(rs.getString("director_pos_id"));
					Organization area = (Organization) areaMap.get(headId);
					position.setOu("CN=" + posId + "," + area.getOu());
					if ("660".equals(headId))
						position.setAreaCode("WWZB"); // 非本业视同总部
					else
						position.setAreaCode(area.getAreaId());

					if (supportMap.containsKey(posId)) {
						Position support = supportMap.get(posId);
						position.setSupportEmpId(support.getSupportEmpId());
						position.setSupportPosId(support.getSupportPosId());
					}					
				}

			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e1) {
			logger.error(e1.getMessage());
		}
	}

	private static Map<String, Employee> _EMPS = null;
	
	private static Map<String, List<Employee>> _POS_EMPS = null;

	
	private static final String TW_EMP_SQL = 
			"SELECT emp.EMP_PHONE_EXT telephoneNumber,emppos.MASTER_POS,substr(pos.org_id,1,4)||'0000' head_id, substr(org_parent_dept,1,4)||'0000' parent_head_id, emp.emp_id id, emp.emp_name name, emp.emp_mobile mobile, emp.emp_gender gender, emp.emp_email mail, emp.emp_caste_id jobGrade, " +
			" emppos.job_name jobName, pos.pos_name position, org.org_name department,emppos.pos_id posId, pos.org_id orgId, emppos.director_emp_id manager, emp.emp_onboard_date1 onboardDate, " +
			" emp.emp_class_id, emp.emp_sub_class_id, emp.emp_area_id, emp.emp_sub_area_id, '' empAreaName,'' empSubAreaName,'' empClassName, '' empSubClassName FROM hrorg.emp emp, hrorg.emp_position emppos, " +
			" hrorg.position pos, hrorg.organization org WHERE emp.emp_id = emppos.emp_id AND emppos.pos_id = pos.pos_id AND pos.org_id = org.org_id and emp.emp_caste_id != ' '" +
			" AND (pos.org_id like 'TW%')" +
			" ORDER BY emp.emp_id, master_pos";

	private static final String IM_EMP_SQL = 
			"SELECT emp.EMP_PHONE_EXT telephoneNumber,emppos.MASTER_POS,substr(pos.org_id,1,4)||'0000' head_id, substr(org_parent_dept,1,4)||'0000' parent_head_id, emp.emp_id id, emp.emp_name name, emp.emp_mobile mobile, emp.emp_gender gender, emp.emp_email mail, emp.emp_caste_id jobGrade, " +
			" emppos.job_name jobName, pos.pos_name position, org.org_name department,emppos.pos_id posId, pos.org_id orgId, emppos.director_emp_id manager, emp.emp_onboard_date1 onboardDate, " +
			" emp.emp_class_id, emp.emp_sub_class_id, emp.emp_area_id, emp.emp_sub_area_id, '' empAreaName,'' empSubAreaName,'' empClassName, '' empSubClassName FROM hrorg.emp emp, hrorg.emp_position emppos, " +
			" hrorg.position pos, hrorg.organization org WHERE emp.emp_id = emppos.emp_id AND emppos.pos_id = pos.pos_id AND pos.org_id = org.org_id and emp.emp_caste_id != ' '" +
			" AND (pos.org_id not like 'TW%') AND  (pos.org_id not like '1%') AND  (pos.org_id not like '2%') AND (pos.org_id not like '3%')" +
			" ORDER BY emp.emp_id, master_pos";

	private static final String CN_EMP_SQL = 
			"SELECT emp.EMP_PHONE_EXT telephoneNumber,emppos.MASTER_POS,substr(pos.org_id,1,4)||'0000' head_id, substr(org_parent_dept,1,4)||'0000' parent_head_id, emp.emp_id id, emp.emp_name name, emp.emp_mobile mobile, emp.emp_gender gender, emp.emp_email mail, " +
			" emp.emp_caste_id jobGrade, emppos.job_name jobName, pos.pos_name position, org.org_name department," +
			" emppos.pos_id posId, pos.org_id orgId, emppos.director_emp_id manager, emp.emp_onboard_date1 onboardDate, class.emp_class_name empClassName, subclass.emp_sub_class_name empSubClassName, " +
			" emp.emp_class_id, emp.emp_sub_class_id, emp.emp_area_id, emp.emp_sub_area_id, " +
			" hrscope.scopedesc empAreaName, hrsubscope.NAME1 empSubAreaName " +
			" FROM hrorg.emp emp, hrorg.emp_position emppos, hrorg.position pos, hrorg.organization org, temporg.emp_class class, temporg.emp_sub_class subclass," +
			" temporg.oa_hrscope hrscope, temporg.ZHR_WERKS_COMPANYCODE hrsubscope " +
			" WHERE emp.emp_id = emppos.emp_id AND emppos.pos_id = pos.pos_id AND pos.org_id = org.org_id and emp.emp_caste_id != ' ' " +
			" AND class.emp_class_id = emp.emp_class_id AND subclass.emp_sub_class_id = emp.emp_sub_class_id " +
			" AND hrscope.scopeid = emp.emp_area_id AND hrsubscope.WERKS = emp.emp_sub_area_id " +
			" AND (pos.org_id like '1%' OR pos.org_id like '2%' OR pos.org_id like '3%')" +
			" ORDER BY emp.emp_id, master_pos";
	
	public List<Employee> getEmployeesByPosition(String... ids) {
		if (_POS_EMPS == null)
			prepareEmployee();
		
		List<Employee> emps = new ArrayList<Employee>();
		
		if (ids != null && ids.length > 0)
			for (String id: ids) {
				List<Employee> list = _POS_EMPS.get(id);
				if (list != null)
					emps.addAll(list);
			}
		
		return emps;
	}

	public List<Employee> getEmployeesById(String... ids) {
		if (_EMPS == null)
			prepareEmployee();
		
		List<Employee> emps = new ArrayList<Employee>();
		
		if (ids != null && ids.length > 0)
			for (String id: ids) {
				Employee emp = _EMPS.get(id);
				if (emp != null)
					emps.add(emp);
			}
		
		return emps;
	}

	private void prepareEmployee() {
		Map<String, Organization> areaMap = getAreaMap();
		
		_EMPS = new HashMap<String, Employee>();
		_POS_EMPS = new HashMap<String, List<Employee>>();

		try {
			Connection conn = portalDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(CN_EMP_SQL);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				processEmployeeList(areaMap, rs);
			rs.close();
			pstmt.close();
			
			pstmt = conn.prepareStatement(TW_EMP_SQL);
			rs = pstmt.executeQuery();
			while (rs.next())
				processEmployeeList(areaMap, rs);
			rs.close();
			pstmt.close();

			pstmt = conn.prepareStatement(IM_EMP_SQL);
			rs = pstmt.executeQuery();
			while (rs.next())
				processEmployeeList(areaMap, rs);
			rs.close();
			pstmt.close();

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processEmployeeList(Map<String, Organization> areaMap, ResultSet rs) throws SQLException {
		
		Employee employee = genEmployee(areaMap, rs);

		if (employee != null && employee.getId() != null) {
			if (!_EMPS.containsKey(employee.getId()))
				_EMPS.put(employee.getId(), employee);
				
			List<Employee> emps = _POS_EMPS.get(employee.getPosition());
			if (emps == null) {
				emps = new ArrayList<Employee>();
				_POS_EMPS.put(employee.getPosition(), emps);
				emps.add(employee);
			}
		}
	}

	private Employee genEmployee(Map<String, Organization> areaMap, ResultSet rs) throws SQLException {

		Employee employee = new Employee();
		employee.setId(rs.getString("id"));
		employee.setDn(Constants.LDAP_ATTRIBUTE_CN + "=" + employee.getId() + "," + LDAP.employeeOU);
		employee.setName(rs.getString("name"));
		employee.setEmail(rs.getString("mail"));
		employee.setMobile(rs.getString("mobile"));
		// 新增分机号
		employee.setTelephoneNumber(rs.getString("telephoneNumber"));
		employee.setGender(rs.getString("gender"));// 性别
		employee.setJobGrade(rs.getString("jobGrade"));
		employee.setJobName(rs.getString("jobName"));
		employee.setSupervisor(rs.getString("manager"));
		employee.setOnboardDate(rs.getString("onboardDate"));
		employee.setEmpClassName(rs.getString("empClassName"));
		employee.setEmpSubClassName(rs.getString("empSubClassName"));
		employee.setEmpAreaName(rs.getString("empAreaName"));
		employee.setEmpSubAreaName(rs.getString("empSubAreaName"));
		employee.setEmpSubAreaCode(rs.getString("emp_sub_area_id"));
		employee.setEmpClassCode(rs.getString("emp_class_id"));
		employee.setEmpSubClassCode(rs.getString("emp_sub_class_id"));
		employee.setPosition(rs.getString("posId"));
		employee.setOrganization(rs.getString("orgId"));
		//employee.setPhoto(getEmployeePhoto(employee.getId()));

		/*
		 * 主岗副岗
		 */
		employee.setMasterId(rs.getString("MASTER_POS"));

		String empId = rs.getString("id");
		String headId = rs.getString("head_id");
		String orgId = rs.getString("orgId");
		String parentHeadId = rs.getString("parent_head_id");

		if ((orgId.startsWith("2") || orgId.startsWith("3")) && !parentHeadId.startsWith("1")
				&& !headId.equals(parentHeadId))
			headId = parentHeadId;

		if (orgId != null && orgId.startsWith("1"))
			headId = "10000000";
		if (orgId != null && orgId.startsWith("TW") && orgId.length() > 3)
			headId = orgId.substring(0, 3);
		if (orgId != null && !orgId.startsWith("TW") && !orgId.startsWith("1") && !orgId.startsWith("2")
				&& !orgId.startsWith("3"))
			headId = "660";
		if (headId != null && areaMap.get(headId) != null) {
			employee.setOu("CN=" + empId);
		} else {
			System.out.println("无法确认 Employee ID (" + employee.getId() == null ? null
					: employee.getId() + ") ou for headId:" + headId);
		}
		return employee;
	}

	public List<Customer> getCustomerList(String... ids) {

		StringBuffer sql = new StringBuffer();
		// sql.append("SELECT ID, NAME FROM ICUSTOMERNW.CUSTOMER WHERE STATUS IS
		// NULL");
		sql.append(
				"select distinct substr(t.ID,3)||'-1' as ID, ID as CID, NAME from ICUSTOMERNW.CUSTOMER t where t.STATUS is null");
		if (ids.length > 0) {
			sql.append(" AND (");
			for (int i = 0; i < ids.length; i++)
				if ((i + 1) == ids.length)
					sql.append("ID='" + ids[i] + "' ");
				else
					sql.append("ID='" + ids[i] + "' OR ");

			sql.append(") ");
		}

		sql.append(" ORDER BY ID ");

		List<Customer> list = new ArrayList<Customer>();

		try {
			Connection conn = iCustomerNWDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Customer cust = new Customer();
				String id = rs.getString("ID");
				cust.setDn(Constants.LDAP_ATTRIBUTE_CN + "=" + id + "," + LDAP.customerOU);
				cust.setId(id);
				cust.setCustomerId(rs.getString("CID"));
				cust.setAddress(rs.getString("NAME"));
				if (!list.contains(cust))
					list.add(cust);
			}

			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e1) {
			logger.error(e1.getMessage());
		}

		return list;
	}

	public List<String> getRetiredCustomerList() {

		List<String> list = new ArrayList<String>();

		try {
			Connection conn = iCustomerNWDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("select distinct substr(ID,3)||'-1' as ID from ICUSTOMERNW.CUSTOMER where STATUS is not null");
			ResultSet rs = pstmt.executeQuery();

			while (rs.next())
				list.add(Constants.LDAP_ATTRIBUTE_CN + "=" + rs.getString("ID") + "," + LDAP.customerOU);

			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e1) {
			logger.error(e1.getMessage());
		}

		return list;
	}

	/*
	 * 查询181 HRORG EMP_POSITION_SUPPORT 志愿岗信息
	 */
	private Map<String, Position> getSupportMap() {

		if (SUPPORT_MAP != null)
			return SUPPORT_MAP;

		SUPPORT_MAP = new HashMap<String, Position>();

		String sql = "select POS_ID,EMP_ID,DIRECTOR_EMP_ID,DIRECTOR_POS_ID from HRORG.EMP_POSITION_SUPPORT";

		try {
			Connection conn = portalDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {

				Position pos = new Position();

				String posId = rs.getString("POS_ID");
				String empId = rs.getString("EMP_ID");
				String support_director_EmpId = rs.getString("DIRECTOR_EMP_ID");
				String support_director_posId = rs.getString("DIRECTOR_POS_ID");

				pos.setId(posId);
				pos.setSupportEmpId(support_director_EmpId);
				pos.setSupportPosId(support_director_posId);
				SUPPORT_MAP.put(posId, pos);
			}

			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e1) {
			logger.error(e1.getMessage());
		}

		return SUPPORT_MAP;

	}

	/**
	 * 
	 * @return
	 */
	public List<String> getHrObjectList(String areaId) {
		String sql = "select org_id,org_name, org_area_id from hrorg.organization where org_area_id='" + areaId
				+ "' order by org_id";

		List<Map<String, Object>> listMapArea = portalJdbcTemplate.queryForList(sql);

		List<String> listArea = new ArrayList<String>();

		if (listMapArea != null && listMapArea.size() > 0) {
			for (int i = 0; i < listMapArea.size(); i++) {
				Map<String, Object> mapArea = listMapArea.get(i);
				listArea.add((String) mapArea.get("org_id"));
			}
		}
		return listArea;
	}

	/**
	 * 
	 * 获取非自定义组 人员集合
	 * 
	 * @param groupName
	 * @return
	 */
	public List<String> getAPEmpList(String groupName) {

		List<String> listEmpAll = new ArrayList<String>();

		String sql = "select GROUP_NAME,GROUP_SQL,GROUP_SCHEMAS,SEL_COLUMN from HRORG.AP_GROUP where GROUP_NAME=?";

		List<Map<String, Object>> listGroupInfo = portalJdbcTemplate.queryForList(sql, groupName);

		if (listGroupInfo != null && listGroupInfo.size() > 0) {

			for (Map<String, Object> map : listGroupInfo) {

				String empSql = map.get("GROUP_SQL").toString();

				String schemas = map.get("GROUP_SCHEMAS").toString();

				String column_empId = map.get("SEL_COLUMN").toString();

				listEmpAll.addAll(getEmpListBySql(empSql, column_empId, schemas));
			}

		}

		return listEmpAll;
	}

	/**
	 * 获取非自定组 组名集
	 * 
	 * @return
	 */
	public List<Group> getAPGroupNames(String... names) {

		List<Group> listNames = new ArrayList<Group>();

		StringBuffer sql = new StringBuffer("select DISTINCT GROUP_NAME from  HRORG.AP_GROUP");

		if (names.length > 0) {
			sql.append(" WHERE (");
			for (int i = 0; i < names.length; i++)
				if ((i + 1) == names.length)
					sql.append("GROUP_NAME='" + names[i] + "' ");
				else
					sql.append("GROUP_NAME='" + names[i] + "' OR ");

			sql.append(") ");
		}

		List<Map<String, Object>> list = portalJdbcTemplate.queryForList(sql.toString());

		if (list != null && list.size() > 0) {

			for (Map<String, Object> map : list) {
				Group group = new Group();
				group.setId((String) map.get("GROUP_NAME"));
				group.setDn(Constants.LDAP_ATTRIBUTE_CN + "=" + group.getId() + "," + LDAP.apOU);
				listNames.add(group);
			}
		}

		return listNames;
	}

	private List<String> getEmpListBySql(String sql, String column_empId, String schemas) {

		List<String> listEmp = new ArrayList<String>();

		List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();

		if (schemas.equals("181"))
			listResult = portalJdbcTemplate.queryForList(sql);

		if (schemas.equals("236"))
			listResult = iCustomerNWJdbcOperations.queryForList(sql);

		if (listResult.size() > 0) {
			for (int i = 0; i < listResult.size(); i++) {
				Map<String, Object> map = listResult.get(i);
				String empId = (String) map.get(column_empId);
				if (empId != null)
					listEmp.add(empId);
			}
		}

		return listEmp;
	}

	/**
	 * 000 获取自定组 组名集
	 * 
	 * @return
	 */
	public List<Group> getCustomizedAPGroupNames(String... names) {

		List<Group> listNames = new ArrayList<Group>();

		StringBuffer sql = new StringBuffer("select DISTINCT GROUP_NAME from  HRORG.IWANTWANT_GROUP");

		if (names.length > 0) {
			sql.append(" WHERE (");
			for (int i = 0; i < names.length; i++)
				if ((i + 1) == names.length)
					sql.append("GROUP_NAME='" + names[i] + "' ");
				else
					sql.append("GROUP_NAME='" + names[i] + "' OR ");

			sql.append(") ");
		}

		List<Map<String, Object>> list = portalJdbcTemplate.queryForList(sql.toString());

		if (list != null && list.size() > 0) {

			for (Map<String, Object> map : list) {

				Group group = new Group();
				group.setId((String) map.get("GROUP_NAME"));
				group.setDn(Constants.LDAP_ATTRIBUTE_CN + "=" + group.getId() + "," + LDAP.apOU);
				listNames.add(group);
			}

		}

		return listNames;
	}

	/**
	 * 000 获取自定义组 人员集合
	 * 
	 * @param groupName
	 * @return
	 */
	public List<String> getCustomizedAPEmpList(String groupName) {

		List<String> listEmp = new ArrayList<String>();

		String sql = "select EMP_ID,GROUP_NAME from HRORG.IWANTWANT_GROUP where GROUP_NAME=?";

		List<Map<String, Object>> listGroupInfo = portalJdbcTemplate.queryForList(sql, groupName);

		if (listGroupInfo != null && listGroupInfo.size() > 0) {

			for (Map<String, Object> map : listGroupInfo) {

				// Employee employee = new Employee();

				// employee.setId(map.get("EMP_ID") == null ? null : map.get(
				// "EMP_ID").toString());
				String empId = (String) map.get("EMP_ID");
				if (empId != null)
					listEmp.add(empId);
			}

		}

		return listEmp;
	}
	
	/**
	 * 获取DB中所有的人事范围code
	 * 
	 * @return
	 */
	public List<HrGroup> getListAreasIds(String... names) {
		StringBuffer sql = new StringBuffer("select distinct EMP_AREA_ID from HRORG.EMP");
		
		if (names.length > 0) {
			sql.append(" WHERE (");
			for (int i = 0; i < names.length; i++)
				if ((i + 1) == names.length)
					sql.append("EMP_AREA_ID='" + names[i] + "' ");
				else
					sql.append("EMP_AREA_ID='" + names[i] + "' OR ");

			sql.append(") ");
		}


		List<Map<String, Object>> listMapArea = portalJdbcTemplate.queryForList(sql.toString());

		List<HrGroup> listAreas = new ArrayList<HrGroup>();

		if (listMapArea != null && listMapArea.size() > 0) {
			for (int i = 0; i < listMapArea.size(); i++) {
				Map<String, Object> mapArea = listMapArea.get(i);
				HrGroup group = new HrGroup();
				group.setId((String)mapArea.get("EMP_AREA_ID"));
				group.setDn(Constants.LDAP_ATTRIBUTE_CN + "=" + group.getId() + "," + LDAP.hrOU);
				listAreas.add(group);
			}
		}

		return listAreas;
	}


	@SuppressWarnings("unchecked")
	public byte[] getEmployeePhoto(String empId) {

		byte[] photo = null;
		
		try {
			Connection conn = portalDataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("SELECT IMG_CONTENT FROM HRORG.EMG_IMAGE WHERE IMG_EMP_ID=?");
			pstmt.setString(1, empId);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next())
				photo = rs.getBytes("IMG_CONTENT");
			
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return photo;
	}
}