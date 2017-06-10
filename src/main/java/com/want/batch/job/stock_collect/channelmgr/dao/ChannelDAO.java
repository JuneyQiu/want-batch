package com.want.batch.job.stock_collect.channelmgr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.stock_collect.channelmgr.bo.StoreChannelBO;
import com.want.batch.job.stock_collect.util.DateFormater;

@Component
public class ChannelDAO {

	// log4j
	private static Logger logger = Logger.getLogger(ChannelDAO.class);

	@Autowired
	public DataSource hw09DataSource;

	public ChannelDAO() {
	}

	/**
	 * ************************************ 得到�\uFFFD有渠道的SID
	 * 
	 * @return ArrayList
	 * @throws SQLException
	 */
	public ArrayList getAllProject() throws SQLException {
		ArrayList projectlist = new ArrayList();
		String sql = "select SID from PROJECT_INFO_TBL where STATUS=1";
		Connection conn = hw09DataSource.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			projectlist.add(rs.getString("SID"));
		}
		rs.close();
		stmt.close();
		conn.close();
		return projectlist;
	}

	public HashMap<Integer, List<Integer>> getStoreProjects(int companySid,
			int branchSid) throws SQLException {
		HashMap<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		String sql = "select distinct b.store_sid, b.project_sid from store_project_tbl b join store_detail_temp c on c.store_sid=b.store_sid and c.company_sid=? and c.branch_sid=? where b.STATUS=1 ";
		Connection conn = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, companySid);
		pstmt.setInt(2, branchSid);

		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			Integer sid = new Integer(rs.getInt("store_sid"));
			Integer pid = new Integer(rs.getInt("project_sid"));
			List<Integer> projects = map.get(sid);
			if (projects == null) {
				projects = new ArrayList<Integer>();
				projects.add(pid);
				map.put(sid, projects);
			} else
				projects.add(pid);
		}
		rs.close();
		pstmt.close();
		conn.close();
		return map;
	}

	public HashMap<String, String> getRouts(int companySid, int branchSid)
			throws SQLException {
		DateFormater df = new DateFormater();
		HashMap<String, String> routes = new HashMap<String, String>();

		String sql = "select distinct c.project_sid, d.sid, c.route_name  FROM route_info_tbl c INNER JOIN subroute_info_tbl b "
				+ "ON c.sid = b.route_sid "
				+ " INNER JOIN SUBROUTE_STORE_TBL a "
				+ "ON b.sid = a.subroute_sid "
				+ "INNER JOIN STORE_INFO_TBL d "
				+ "ON a.store_sid = d.sid "
				+ "JOIN STORE_DETAIL_TEMP e "
				+ "ON a.store_sid = e.store_sid and e.company_sid=? and e.branch_sid=? "
				+ " WHERE yearmonth = ? ";

		Connection conn = hw09DataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, companySid);
		pstmt.setInt(2, branchSid);
		pstmt.setString(3, df.getCurrentYearMonth());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			int pid = rs.getInt("project_sid");
			int sid = rs.getInt("sid");
			String key = Integer.toString(sid) + Integer.toString(pid);
			String value = routes.get(key);
			if (value == null)
				routes.put(key, rs.getString("route_name") + ";");
			else
				routes.put(key, value + rs.getString("route_name") + ";");
		}
		rs.close();
		pstmt.close();
		conn.close();
		return routes;
	}

	private String getRoutByStoreAndProject(HashMap<String, String> routes,
			int sid, int pid) {
		return routes.get(Integer.toString(sid) + Integer.toString(pid));
	}

	public StoreChannelBO getStore_Channel(HashMap<String, String> routes,
			int v_store_sid, List<Integer> projects) {
		// String sql =
		// "select store_sid,project_sid from store_project_tbl where STATUS = 1 and store_sid="
		// + v_store_sid;
		// try {
		// PreparedStatement pstmt = conn.prepareStatement(sql);
		// ResultSet rs = pstmt.executeQuery();

		StoreChannelBO scb = new StoreChannelBO();

		if (projects != null) {
			for (Integer project : projects) {
				int v_project_sid = project.intValue();
				//配送共线
				if(v_project_sid == 1){
					scb.setHW01(true);
					scb.setAREA001(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				//配送休闲
				else if(v_project_sid == 22){
					scb.setHW22(true);
					scb.setAREA022(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				//配送乳饮
				else if(v_project_sid == 23){
					scb.setHW23(true);
					scb.setAREA023(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				//县城共线
				else if(v_project_sid == 4){
					scb.setHW04(true);
					scb.setAREA004(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				//县城休闲
				else if(v_project_sid == 17){
					scb.setHW17(true);
					scb.setAREA017(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
		        // 县城乳饮
				else if(v_project_sid == 18){
					scb.setHW18(true);
					scb.setAREA018(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				//饮品
				else if(v_project_sid == 10){
					scb.setHW10(true);
					scb.setAREA010(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				// 乳品共线
				else if(v_project_sid == 12 ){
					scb.setHW12(true);
					scb.setAREA012(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				//乳品利乐
				else if(v_project_sid == 27){
					scb.setHW27(true);
					scb.setAREA027(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				//乳品铁罐
				else if(v_project_sid == 28){
					scb.setHW28(true);
					scb.setAREA027(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}

				// 通路终端
				else if (v_project_sid == 11) {
					scb.setHW011(true);
				} else if (v_project_sid == 14) {
					scb.setHW14(true);
					scb.setAREA014(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				// 糕饼膨化
				else if (v_project_sid == 15) {
					scb.setHW15(true);
					scb.setAREA015(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				// 米果炒货
				else if (v_project_sid == 25) {
					scb.setHW25(true);
					scb.setAREA025(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				// 糖果果冻
				else if (v_project_sid == 26) {
					scb.setHW26(true);
					scb.setAREA026(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				// 冰品终端
				else if (v_project_sid == 16) {
					scb.setHW016(true);
					scb.setAREA016(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				// 现渠终端
				else if (v_project_sid == 20) {
					scb.setHW020(true);
					scb.setAREA020(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				// 直营终端
				else if (v_project_sid == 24) {
					scb.setHW024(true);
					scb.setAREA024(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				// 哎呦点心终端
				else if (v_project_sid == 30) {
					scb.setHW30(true);
					scb.setAREA030(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				// 特通终端
				else if (v_project_sid == 31) {
					scb.setHW31(true);
					scb.setAREA031(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				// 配送一线
				else if (v_project_sid == 32) {
					scb.setHW32(true);
					scb.setAREA032(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				// 配送二线
				else if (v_project_sid == 33) {
					scb.setHW33(true);
					scb.setAREA033(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				// 配送三线
				else if (v_project_sid == 34) {
					scb.setHW34(true);
					scb.setAREA034(this.getRoutByStoreAndProject(routes,
							v_store_sid, v_project_sid));
				}
				// System.out.println("结束if 判断："+sFormat.format(new Date()));
			}
		}
		// rs.close();
		// pstmt.close();
		return scb;
		// } catch (SQLException ex) {
		// LOG4J
		// logger.error(ex.getMessage());
		// ex.printStackTrace();
		// return null;
		// }
	}

}
