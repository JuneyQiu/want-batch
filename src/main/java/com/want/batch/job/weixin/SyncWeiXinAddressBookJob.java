package com.want.batch.job.weixin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.lds.bo.Employee;
import com.want.batch.job.lds.bo.Organization;

@Component
public class SyncWeiXinAddressBookJob extends AbstractWantJob {

	@Autowired
	@Qualifier("wechatJdbcOperations")
	private SimpleJdbcTemplate webchatJdbcTemplate;
	@Autowired
	@Qualifier("cedatadevJdbcOperations")
	private SimpleJdbcTemplate cedatadevJdbcOperations;
	@Autowired
	@Qualifier("portalJdbcOperations")
	private SimpleJdbcTemplate portalJdbcOperations;

	// //David
	private static final String CORPID = "wxf0914a6fe4aab002";
	private static final String CORPSECRET = "gATJh2KyDKMhY0SFvgmeJQ35WNodoRdRhFGY6ac5oCjZUBYHElY9dnJM983qpwS8";

	private static final String WANTWANTGROUP_ORGID = "10000000";
	private static final String INSER_SQL = "insert into WECHAT.ORG_MAPPING(WW_ORG_ID,WECHAT_ORG_ID) values (?,?)";

	private static final String INSER_TAG_SQL = "insert into WECHAT.WECHAT_TAG(WECHAT_TAGID,HR_TAGID,TAGNAME,TYPE,PERMISSIONS_GROUP) values (?,?,?,?,?)";

	private String ACCESS_TOKEN = null;
	private final String requestAccesTokenURL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
	private final String requestCreateOrgURL = "https://qyapi.weixin.qq.com/cgi-bin/department/create?access_token=%s";
	private final String requestCreateEmpURL = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=%s";

	private final String requestUpdateEmpURL = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=%s";

	private final String requestFindAllEmpURL = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=%s&department_id=%s&fetch_child=%s&status=%s";

	private final String rquestFindAllOrgURL = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s";

	private final String requestUpdateOrgURL = "https://qyapi.weixin.qq.com/cgi-bin/department/update?access_token=%s";

	private final String requestDeleEmpURL = "https://qyapi.weixin.qq.com/cgi-bin/user/delete?access_token=%s&userid=%s";

	private final String requestCeateTag = "https://qyapi.weixin.qq.com/cgi-bin/tag/create?access_token=%s";

	private final String requestAddUsersFromTag = "https://qyapi.weixin.qq.com/cgi-bin/tag/addtagusers?access_token=%s";

	private final String requestGetTagList = "https://qyapi.weixin.qq.com/cgi-bin/tag/list?access_token=%s";

	private final String requestGetUsersByTagId = "https://qyapi.weixin.qq.com/cgi-bin/tag/get?access_token=%s&tagid=%s";

	private final String requestDelTagUsers = "https://qyapi.weixin.qq.com/cgi-bin/tag/deltagusers?access_token=%s";

	private final String requestDelTagById = "https://qyapi.weixin.qq.com/cgi-bin/tag/delete?access_token=%s&tagid=%s";

	private final String requestGetAllOrg = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s&id=%s";

	private final String requestDelOrg = "https://qyapi.weixin.qq.com/cgi-bin/department/delete?access_token=%s&id=%s";

	private boolean threadFinished = false;

	public void initAccesToken() {

		try {
			String requestUrl = String.format(requestAccesTokenURL, CORPID,
					CORPSECRET);

			JSONObject json = httpRequest(requestUrl, "GET", null);

			ACCESS_TOKEN = json.getString("access_token");

		} catch (Exception e) {
			logger.error(e.toString());
			try {

				Thread.sleep(60 * 1000);

				String requestUrl = String.format(requestAccesTokenURL, CORPID,
						CORPSECRET);

				JSONObject json = httpRequest(requestUrl, "GET", null);

				ACCESS_TOKEN = json.getString("access_token");

			} catch (InterruptedException e1) {
				logger.error(e.toString());
			}
		}

	}

	@Override
	public void execute() throws Exception {
		logger.info("****************微信 通讯录  同步开始****************");
		logger.info("code version： 2015-10-10 11:41");
		initAccesToken();

		Thread hreatThread = new Thread(new TokenThread());
		hreatThread.start();

		if (ACCESS_TOKEN != null) {
			
			
			//同步基本资料
			syncDelOrg();
			syncOrg();
			syncEmp();
			syncICusetomer();

			//删除所有tag
			delAllTag();
			
			//同步定制标签-上海总部
			addTag_WWZB();
			
			//同步icustomer中取得的经销商渠道标签
			crateTagId("ICUSTOMER", "David");
			addTagUsers("ICUSTOMER", "David");
			
			//同步HR系统中取得的员工岗位标签
			crateTagId("EMP", "David");
			addTagUsers("EMP", "David");
			
			//同步HR系统中取得的员工 类型标签 Modify by David Luo on 2015/8/19
			crateTagId("EMP_POS_TYPE", "David");
			addTagUsers("EMP_POS_TYPE", "David");
			
			
			
			threadFinished = true;
		}
		logger.info("****************微信 通讯录  同步完成****************");

	}

	public void addTag_WWZB(){
		
		JSONObject jsonTag = createWeiTag(472,"上海总部");
	
		if(jsonTag.getString("errcode").equals("0")){
			
			logger.info("添加上海总部标签成功, wechatTagId: "+jsonTag.getString("tagid"));
			//Modify by David 限制添加标签成员的数量
			
			ArrayList users = this.splitUser(200, getListUserIds_WWZB());
			ArrayList part_users = null;
			
			for(int j = 0 ; j < users.size();j++){
			part_users = (ArrayList)users.get(j);
			
			JSONObject json = createTagUsers(jsonTag.getString("tagid"),part_users);
			
			//Modify by David on 20151010错误日志
			if("0".equals(json.getString("errcode"))&& "ok".equals(json.getString("errmsg")))
			{
				logger.info("添加上海总部标签人员成功！ no:"+j);
			
			}
			//Modify by David on 20151010其他错误情况
			else if("0".equals(json.getString("errcode")) && !"ok".equals(json.getString("errmsg")) )
			{
				logger.info("部分添加上海总部标签人员失败!  no:"+j+", errorcode: "+json.get("errcode") +", errormsg："+json.get("errmsg")+", errorData："+json.get("invalidlist"));
			}
			else
				logger.info("全数添加上海总部标签人员失败!  no:"+j+", errorcode: "+json.get("errcode") +", errormsg："+json.get("errmsg")+"。");
			}
		}
		else
			logger.info("添加上海总部标签失败, errorcode: "+jsonTag.get("errcode") +", errormsg："+jsonTag.get("errmsg")+"。");
	}
	
	public List<String> getListUserIds_WWZB(){
		List<String> listUsers = new ArrayList<String>();
		String sql = "select EMP_ID from icustomernw.emp where EMP_AREA_ID='WWZB'";
		List<Map<String,Object>> list = cedatadevJdbcOperations.queryForList(sql);
		if(list!=null && list.size()>0){
			for(Map<String,Object> map:list){
				listUsers.add(map.get("EMP_ID").toString());
			}
		}
		logger.info("上海总部标签人数："+list.size());
		return listUsers;
		
	}
	
	/**
	 * 因为群发收件人数量限制，将收件人分组，send_limit 人一组，看最上面的限制
	 * @param reciver 
	 * @return
	 */
	private ArrayList splitUser(int send_limit, List<String> list2){
		ArrayList list =new ArrayList();
		ArrayList part_users = new ArrayList();
		int groupcount = 0;
		for(int i=0; i<list2.size() ; i++){
				groupcount++;
				part_users.add(list2.get(i));
			if(groupcount == send_limit || i==list2.size()-1){
				list.add(part_users);
				part_users.clear();
				groupcount=0;
			}
		}
		return list;
	}
	
	
	public JSONObject getAllTags() {

		String createRequestUrl = String
				.format(requestGetTagList, ACCESS_TOKEN);

		return httpRequest(createRequestUrl, "GET", null);
	}

	public JSONObject getUsersByTagId(String tagId) {

		String createRequestUrl = String.format(requestGetUsersByTagId,
				ACCESS_TOKEN, tagId);

		return httpRequest(createRequestUrl, "GET", null);
	}

	public JSONObject DelTagUsers(String wechatTagId, List<String> userList) {

		JSONObject jsonOrg = new JSONObject();

		jsonOrg.element("tagid", wechatTagId);// 唯一

		jsonOrg.element("userlist", userList);

		String createRequestUrl = String.format(requestDelTagUsers,
				ACCESS_TOKEN);

		return httpRequest(createRequestUrl, "POST", jsonOrg.toString());

	}

	public JSONObject delTagById(String tagId) {

		String createRequestUrl = String.format(requestDelTagById,
				ACCESS_TOKEN, tagId);

		return httpRequest(createRequestUrl, "GET", null);
	}

	public void delAllTag() {

		JSONObject jAllTag = getAllTags();

		JSONArray jsonTagList = JSONArray.fromObject(jAllTag.get("taglist"));

		if (jAllTag.getString("errcode").equals("0")) {

			List<Map<String, Object>> listTags = (List<Map<String, Object>>) jsonTagList
					.toCollection(jsonTagList, Map.class);

			for (Map<String, Object> mapTag : listTags) {

				try {

					String wechatTagId = mapTag.get("tagid").toString();

					if (wechatTagId.equals("2") || wechatTagId.equals("3")
							|| wechatTagId.equals("4"))
						continue;

					// 根据tagId获取tag下的成员
					JSONObject jUsers = getUsersByTagId(wechatTagId);

					if (jUsers.getString("errcode").equals("0")) {

						logger.info(wechatTagId + " --获取成员列表成功");

						JSONArray jsonArrayUserList = JSONArray
								.fromObject(jUsers.get("userlist"));

						List<Map<String, Object>> listUsers = (List<Map<String, Object>>) jsonArrayUserList
								.toCollection(jsonArrayUserList, Map.class);

						// 如果标签下有成员列表
						if (listUsers.size() > 0) {

							List<String> listUserIdResult = new ArrayList<String>();

							for (Map<String, Object> mapUser : listUsers) {

								try {
									listUserIdResult.add(mapUser.get("userid")
											.toString());
								} catch (Exception e) {
									logger.error(e.toString());
								}

							}// for

							// 删除标签成员
							JSONObject jDelTagUser = DelTagUsers(wechatTagId,
									listUserIdResult);

							if (jDelTagUser.getString("errcode").equals("0")) {

								logger.info(wechatTagId + " --下的成员已删除");

								// 删除标签
								JSONObject jDelTagById = delTagById(wechatTagId);

								if (jDelTagById.getString("errcode")
										.equals("0")) {
									logger.info(wechatTagId + " --已删除");
									// 清空DB对照表
									String sql = "delete from WECHAT.WECHAT_TAG where WECHAT_TAGID=?";

							//		webchatJdbcTemplate.update(sql, wechatTagId);

							//		logger.info(wechatTagId + " -- 已删除");
								}else{
									logger.info(wechatTagId + " --删除失败");
								}
							}
						} else {

							JSONObject jDelTagById = delTagById(wechatTagId);

							if (jDelTagById.getString("errcode").equals("0")) {
								logger.info(wechatTagId + " -- 不删除DB");
								// 清空DB对照表
								String sql = "delete from WECHAT.WECHAT_TAG where WECHAT_TAGID=?";

					//			webchatJdbcTemplate.update(sql, wechatTagId);

					//			logger.info(wechatTagId + " -- 已删除");
							}
						}

					}

				} catch (Exception e) {
					logger.error(e.toString());
				}
			}
		}

		JSONObject jAllTag_end = getAllTags();

		JSONArray jsonTagList_end = JSONArray
				.fromObject(jAllTag.get("taglist"));

		logger.info("删除后 标签数量 -- " + jsonTagList_end.size());
	}

	public void crateTagId(String type, String permissionsGroup) {

		String logStr = "ICUSTOMER";
		String sql = "SELECT CREDIT_ID as TAG_ID, CREDIT_DESC as TAG_NAME FROM ICUSTOMER.SALES_AREA_REL where status=1 order by CREDIT_ID,CREDIT_DESC ";

		if (type.equals("EMP")) {
			logStr = "旺旺员工";
			//Modify by David Luo on 2015/9/29
			sql = " select POS_PROPERTY_ID as TAG_ID,POS_PROPERTY_NAME as TAG_NAME from HRORG.POSITION"
					+ " inner join HRORG.EMP_POSITION on  HRORG.POSITION.POS_ID=HRORG.EMP_POSITION.POS_ID "
					+ " where length(trim(nvl(POS_PROPERTY_ID,'')))>0 group by  POS_PROPERTY_ID ,POS_PROPERTY_NAME  order by POS_PROPERTY_ID ,POS_PROPERTY_NAME";

		}else if("EMP_POS_TYPE".equals(type)){
			//Modify by David Luo on 2015/9/29
			sql = " select  POS_PROPERTY_ID||'-'||POS_TYPE_ID as TAG_ID, POS_PROPERTY_NAME||'-'||POS_TYPE_NAME as TAG_NAME  from TEMPORG.POSITION_A "
				+ " inner join TEMPORG.EMP_POSITION on TEMPORG.POSITION_A.POS_ID=TEMPORG.EMP_POSITION.POS_ID "
				+ " where trim(POS_TYPE_NAME)<>' '"
				+ " group by  POS_PROPERTY_ID||'-'||POS_TYPE_ID,POS_PROPERTY_NAME||'-'||POS_TYPE_NAME order by POS_PROPERTY_ID||'-'||POS_TYPE_ID ";
		}

		Map<String, String> mapDBTag = getAllDBTag(sql);

		logger.info(logStr + " DB标签数： " + mapDBTag.size());

		for (Entry<String, String> entry : mapDBTag.entrySet()) {
			//Modify By David 20150814
			//查询此tag是否已经建立，如果已建立，则新建于wechat的tagid,就不重新建立
			//http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%A0%87%E7%AD%BE
			//wechat 标签id，整型，指定此参数时新增的标签会生成对应的标签id，不指定时则以目前最大的id自增。
			int wechat_tagid = getWechatTadIdByTagID(entry,type,permissionsGroup);
			logger.info("here**********************wechat_tagid:"+wechat_tagid + "      TAG_ID： " + entry.getKey());
			JSONObject returnJson = null;
			if(wechat_tagid!=0){
				//修改tag-name格式
				returnJson = createWeiTag(wechat_tagid,entry.getValue()+"("+entry.getKey()+")");
			}else{
				returnJson = createWeiTag(entry.getValue()+"("+entry.getKey()+")");
			}
		
			/*
			JSONObject returnJson = createWeiTag(entry.getKey() + "_"
					+ entry.getValue());
			
			if (returnJson.getString("errcode").equals("0")) {

				logger.info(" -- [sucess]  add " + logStr
						+ " new tag errorcode : "
						+ returnJson.getString("errcode") + ", tagId: "
						+ entry.getKey() + ", tagName: " + entry.getValue()
						+ ", weixin_tagId: " + returnJson.getString("tagid"));

				webchatJdbcTemplate.update(INSER_TAG_SQL,
						returnJson.getString("tagid"), entry.getKey(),
						entry.getValue(), type, permissionsGroup);

			} else
				logger.info(" -- [faild]  add " + logStr
						+ " new tag errorcode : "
						+ returnJson.getString("errcode") + "errorMessage : "
						+ returnJson.getString("errmsg") + ", tagId: "
						+ entry.getKey() + ", tagName: " + entry.getValue());

		}
 */
			//Modify By David 20150814
		
			if ("0".equals(returnJson.getString("errcode"))) {
				
				logger.info(" -- [sucess]  add " + logStr
						+ " new tag errorcode : "
						+ returnJson.getString("errcode") + ", tagId: "
						+ entry.getKey() + ", tagName: " + entry.getValue()
						+ ", weixin_tagId: " + returnJson.getString("tagid"));
				
				if(wechat_tagid==0){
					webchatJdbcTemplate.update(INSER_TAG_SQL,
							returnJson.getString("tagid"), entry.getKey(),
							entry.getValue(), type, permissionsGroup);
				}
			} else
				logger.info(" -- [faild]  add " + logStr
						+ " new tag errorcode : "
						+ returnJson.getString("errcode") + ", errorMessage : "
						+ returnJson.getString("errmsg") + ", tagId: "
						+ entry.getKey() + ", tagName: " + entry.getValue());

		}
		logger.info("添加完成..");

	}
	
	
	/**
	 * 使用旺旺的TAGID查询已经建立的WECHAT TAGID
	 * Modify by David on 20150814
	 * @param tag_id
	 * @return wechat_tagid
	 */
	public int getWechatTadIdByTagID(Entry<String, String> entry,String type,String permissionsGroup) {
		String logStr = "getWechatTadIdByTagID";
		String sql = "select WECHAT_TAGID from WECHAT.WECHAT_TAG where HR_TAGID='"+entry.getKey()+"' order by WECHAT_TAGID";
		
		int wechat_tagid = 0;
		try{
			wechat_tagid = webchatJdbcTemplate.queryForInt(sql);
			
			//如果查不到，为了保证DB中的ID是最大的，所以新建tag_id Modify by David on 20151008 ---------------start
			String sql_MaxID = " select max(WECHAT_TAGID)+1 from WECHAT.WECHAT_TAG ";
			if(wechat_tagid < 1 ){
				wechat_tagid = webchatJdbcTemplate.queryForInt(sql_MaxID);	
				logger.info(logStr + " 查不到 " + entry.getKey()+" 的ID，故新建："+wechat_tagid);
				webchatJdbcTemplate.update(INSER_TAG_SQL,
					wechat_tagid, entry.getKey(),
					entry.getValue(), type, permissionsGroup);
				logger.info(logStr + " 保存新建wechat_tagid："+wechat_tagid+" 成功！");
			}
			//如果查不到，新建tag_id Modify by David on 20151008 ---------------end
		}catch(DataAccessException daee){
			logger.equals(daee);
		}
		logger.info(logStr + "Wechat ID:"+wechat_tagid+", HR_ID:" + entry.getKey());
		return wechat_tagid;
	}
	
	

	public JSONObject createTagUsers(String wechatTagId, List<String> userList) {

		JSONObject jsonObj = new JSONObject();
		JSONObject jsonresult = null;
		
		ArrayList users = this.splitUser(200, userList);
		ArrayList part_users = null;
		
		for(int j = 0 ; j < users.size();j++){
			part_users = (ArrayList)users.get(j);
			
			jsonObj.element("tagid", wechatTagId);// 唯一
			jsonObj.element("userlist", part_users);
			jsonresult = httpRequest(String.format(requestAddUsersFromTag,
					ACCESS_TOKEN), "POST", jsonObj.toString());
			//Modify by David on 20151010
			if("0".equals(jsonresult.getString("errcode")) && "ok".equals(jsonresult.getString("errmsg")) )
			{
				logger.info("添加 "+wechatTagId+" 标签人员成功！ no:"+j);
			}
			//Modify by David on 20151010其他错误情况
			else if("0".equals(jsonresult.getString("errcode")) && !"ok".equals(jsonresult.getString("errmsg")) )
			{
				logger.info("部分添加 "+wechatTagId+" 标签人员失败!  no:"+j+", errorcode: "+jsonresult.get("errcode") +", errormsg："+jsonresult.get("errmsg")+", errorData："+jsonresult.get("invalidlist"));
			}
			else
			{
				logger.info("全数添加 "+wechatTagId+" 标签人员失败!  no:"+j+", errorcode: "+jsonresult.get("errcode") +", errormsg："+jsonresult.get("errmsg")+"。");
			}
			part_users.clear();
		}
		
		return jsonresult;

	}

	public void addTagUsers(String type, String permissions) {

		logger.info(" ------ " + type + ",开始向Tag添加成员");

		Map<String, List<String>> mapTagUsers = getTagDataStructure(type,
				permissions);

		logger.info("mapTagUsers size： " + mapTagUsers.size());

		for (Entry<String, List<String>> entry : mapTagUsers.entrySet()) {

			try {

				if (entry.getValue().size() > 0) {

					JSONObject returnJson = createTagUsers(entry.getKey(),
							entry.getValue());

					if (returnJson.getString("errcode").equals("0"))
						logger.info(" -- [sucess]  add " + type
								+ " users from tag errorcode : "
								+ returnJson.getString("errcode")
								+ ", wechatTagId: " + entry.getKey()
								+ ", userList: " + entry.getValue());

					else
						logger.info(" -- [faild]  add " + type
								+ " users from tag errorcode : "
								+ returnJson.getString("errcode")
								+ "errorMessage : "
								+ returnJson.getString("errmsg")
								+ ", wechatTagId: " + entry.getKey()
								+ ", userList: " + entry.getValue());

				}

			} catch (Exception e) {
				logger.error(e.toString());
			}

		}

		logger.info(" ------ " + type + ",添加Tag完成");

	}

	public Map<String, List<String>> getTagDataStructure(String type,
			String permissions) {

		Map<String, List<String>> mapResult = new HashMap<String, List<String>>();

		List<String[]> listTagEmps = getTagIdEmpIdByDB(type);

		List<Map<String, String>> listWechatTableData = getWechatDB(type,
				permissions);

		Map<String, String> mapHRKey = listWechatTableData.get(1);

		for (Entry<String, String> entry : mapHRKey.entrySet()) {

			String hrTagId = entry.getKey();

			List<String> listUsers = new ArrayList<String>();

			mapResult.put(entry.getValue(), listUsers);

			for (String[] tagEmpArrays : listTagEmps) {

				try {

					if (hrTagId.equalsIgnoreCase(tagEmpArrays[0])) {
						listUsers.add(tagEmpArrays[1]);
					}

				} catch (Exception e) {
					logger.error(e.toString());
				}

			}

			logger.info("wechatTagId: " + entry.getValue() + ", HRtagId: "
					+ hrTagId + " , userSize: " + listUsers.size());

		}

		return mapResult;

	}

	public List<String[]> getTagIdEmpIdByDB(String type) {

		List<String[]> listDB = new ArrayList<String[]>();

		SimpleJdbcTemplate jdbcTemplate = portalJdbcOperations;
		String sql = "select EMP_ID,POS_PROPERTY_ID  as TAG_ID"
				+ " from  HRORG.POSITION a inner join HRORG.EMP_POSITION b on a.pos_id=b.pos_id and b.MASTER_POS=1";

		if (type.equals("ICUSTOMER")) {

			//jdbcTemplate = cedatadevJdbcOperations;
			jdbcTemplate = this.getCedatadevJdbcOperations();
			sql = "select CUSTOMER_ID as EMP_ID,CREDIT_ID as TAG_ID from icustomer.DIVSION_SALES_CUSTOMER_REL"
					+ " group by CUSTOMER_ID,CREDIT_ID"
					+ " union select CUSTOMER_ID,CREDIT_ID from icustomer.DIVSION_SALES_CUSTOMER_KA"
					+ " group by CUSTOMER_ID,CREDIT_ID";
		}else if("EMP_POS_TYPE".equals(type)){
			//Adding by David Luo on 2015/8/19
			jdbcTemplate = portalJdbcOperations;
			sql = "select a.EMP_ID as EMP_ID,   POS_PROPERTY_ID||'-'||POS_TYPE_ID   as TAG_ID from TEMPORG.EMP a "
					+ " inner join TEMPORG.EMP_POSITION b on a.EMP_ID=b.EMP_ID and b.MASTER_POS='1'"
					+ " inner join TEMPORG.POSITION_A c on b.POS_ID=c.POS_ID and trim(c.POS_TYPE_NAME)<>' '";
		}
		
		List<Map<String, Object>> listValue = jdbcTemplate.queryForList(sql);

		for (Map<String, Object> map : listValue) {

			try {

				String[] strArrays = { map.get("TAG_ID").toString(),
						map.get("EMP_ID").toString() };

				listDB.add(strArrays);

			} catch (Exception e) {
				logger.error("tagId or empId is null : " + e.toString());
			}

		}
		logger.info("listDB size: " +listDB.size() );
		return listDB;
	}

	public List<Map<String, String>> getWechatDB(String type, String permissions) {

		String sql = "select * from WECHAT.WECHAT_TAG where TYPE=? and PERMISSIONS_GROUP=?";

		List<Map<String, String>> listWechat = new ArrayList<Map<String, String>>();
		Map<String, String> mapWechatKey = new HashMap<String, String>();
		Map<String, String> mapHRKey = new HashMap<String, String>();

		listWechat.add(mapWechatKey);
		listWechat.add(mapHRKey);

		List<Map<String, Object>> listValue = webchatJdbcTemplate.queryForList(
				sql, type, permissions);

		for (Map<String, Object> map : listValue) {

			try {

				mapWechatKey.put(map.get("WECHAT_TAGID").toString(),
						map.get("HR_TAGID").toString());
				mapHRKey.put(map.get("HR_TAGID").toString(),
						map.get("WECHAT_TAGID").toString());

			} catch (Exception e) {

				logger.error("tagId or wechatId is null : " + e.toString());
			}

		}

		return listWechat;
	}

	public JSONObject createWeiTag(String tagName) {

		JSONObject jsonOrg = new JSONObject();

		jsonOrg.element("tagname", tagName);

		String createRequestUrl = String.format(requestCeateTag, ACCESS_TOKEN);

		return httpRequest(createRequestUrl, "POST", jsonOrg.toString());

	}
	
	public JSONObject createWeiTag(int wechat_tagid, String tagName) {

		JSONObject jsonOrg = new JSONObject();

		jsonOrg.element("tagname", tagName);
		jsonOrg.element("tagid", wechat_tagid);

		String createRequestUrl = String.format(requestCeateTag, ACCESS_TOKEN);

		return httpRequest(createRequestUrl, "POST", jsonOrg.toString());

	}

	public Map<String, String> getAllDBTag(String sql) {

		Map<String, String> mapTag = new HashMap<String, String>();

		List<Map<String, Object>> mapValue = portalJdbcOperations
				.queryForList(sql);

		for (Map<String, Object> map : mapValue) {

			try {
				mapTag.put(map.get("TAG_ID").toString(), map.get("TAG_NAME")
						.toString());
			} catch (Exception e) {
				logger.error("tagId or tagName is null : " + e.toString());
			}

		}

		return mapTag;
	}

	public void syncEmp() {

		Map<String, Employee> mapAllEmpsByDB = getMapEmpByDB();

		logger.info("DB当前总人数：" + mapAllEmpsByDB.size());

		Map<String, Employee> mapAllEmpByWeiXin = getMapEmpByJson("80521", "1","0");

		logger.info("微信通讯录当前总人数：" + mapAllEmpByWeiXin.size());

		/**
		 * 删除DB中没有的员工
		 */

		List<String> listRemoveKey = new ArrayList<String>();
		for (Entry<String, Employee> entry : mapAllEmpByWeiXin.entrySet()) {

			Employee emp_weixin = entry.getValue();

			if (!mapAllEmpsByDB.containsKey(emp_weixin.getId())) {

				listRemoveKey.add(emp_weixin.getId());

				JSONObject json = delWeiXinEmp(emp_weixin.getId());

				if (json.getString("errcode").equals("0"))
					logger.info(" -- sucess del leave emp errorcode : "
							+ json.getString("errcode") + ", empId: "
							+ emp_weixin.getId());
				else
					logger.info(" -- faild  del leave emp errorcode : "
							+ json.getString("errcode") + ", empId: "
							+ emp_weixin.getId());

			}

		}

		System.out.println(listRemoveKey.size());

		for (String empId : listRemoveKey) {
			mapAllEmpByWeiXin.remove(empId);
		}

		logger.info("删除key后的mapAllEmpByWeiXin ：" + mapAllEmpByWeiXin.size());

		List<Employee> listAddEmp = new ArrayList<Employee>();

		for (Entry<String, Employee> entry : mapAllEmpsByDB.entrySet()) {

			Employee emp_db = entry.getValue();

			if (!mapAllEmpByWeiXin.containsKey(emp_db.getId())) {
				listAddEmp.add(emp_db);
			}
		}

		logger.info("需新增员工：" + listAddEmp.size());

		for (Employee emp_db : listAddEmp) {

			if (emp_db.getOrgIdList() == null)
				continue;

			if (emp_db.getOrgIdList().size() <= 0)
				continue;

			if (getStrUtil(emp_db.getEmail()) == null)
				emp_db.setEmail(emp_db.getId() + "@want-want.com");

			if (getStrUtil(emp_db.getMobile()) == null)
				emp_db.setMobile(emp_db.getId() + "000");

			if (getStrUtil(emp_db.getJobName()) == null)
				emp_db.setJobName(emp_db.getId() + " position is null");

			JSONObject json = createWeiXinEmp(emp_db);

			if (json.getString("errcode").equals("0"))
				logger.info(" -- sucess  add emp errorcode : "
						+ json.getString("errcode") + ", empId: "
						+ emp_db.getId() + ", empName: " + emp_db.getName()
						+ ", orgIdlist: " + emp_db.getOrgIdList()
						+ ", errmsg: " + json.getString("errmsg"));
			else
				logger.info(" -- faild  add emp errorcode : "
						+ json.getString("errcode") + ", empId: "
						+ emp_db.getId() + ", empName: " + emp_db.getName()
						+ ", orgIdlist: " + emp_db.getOrgIdList()
						+ ", errmsg: " + json.getString("errmsg"));

		}

		List<Employee> listUpdateEmp = new ArrayList<Employee>();
		mapAllEmpByWeiXin = getMapEmpByJson("80521", "1", "0");

		logger.info("新增后微信通讯录当前总人数：" + mapAllEmpByWeiXin.size());

		for (Entry<String, Employee> entry : mapAllEmpByWeiXin.entrySet()) {

			Employee emp_weixin = entry.getValue();

			try {
				if (mapAllEmpsByDB.containsKey(emp_weixin.getId())) {

					Employee emp_db = mapAllEmpsByDB.get(emp_weixin.getId());

					if (getStrUtil(emp_db.getEmail()) == null)
						emp_db.setEmail(emp_db.getId() + "@want-want.com");

					
					if (getStrUtil(emp_db.getMobile()) == null)
						emp_db.setMobile(emp_db.getId() + "000");
					

					if (getStrUtil(emp_db.getJobName()) == null)
						emp_db.setJobName(emp_db.getId() + " position is null");

					boolean flag_orgList = getDiffrent(
							emp_weixin.getOrgIdList(), emp_db.getOrgIdList());

					if (emp_weixin.getName().trim()
							.equalsIgnoreCase(emp_db.getName().trim()) == false
							|| emp_weixin
									.getJobName()
									.trim()
									.equalsIgnoreCase(
											emp_db.getJobName().trim()) == false
							|| emp_weixin.getEmail().trim()
									.equalsIgnoreCase(emp_db.getEmail().trim()) == false
							//增加员工电话变更
							||	emp_weixin.getMobile().trim().equalsIgnoreCase(emp_db.getMobile().trim())== false
									|| flag_orgList == false) {

						logger.info(" -- weixin , empId: " + emp_weixin.getId()
								+ ", empName: " + emp_weixin.getName()
								+ ", jobName: " + emp_weixin.getJobName()
								+ ", email: " + emp_weixin.getEmail()
								+ ", orgList: " + emp_weixin.getOrgIdList());

						logger.info(" -- db     , empId: " + emp_db.getId()
								+ ", empName: " + emp_db.getName()
								+ ", jobName: " + emp_db.getJobName()
								+ ", email: " + emp_db.getEmail()
								+ ", orgList: "
								+ emp_db.getOrgIdList().toString());

						listUpdateEmp.add(emp_db);

					}

				}
			} catch (Exception e) {
				logger.info("invalid weixin employ: " + (emp_weixin.getId() != null? emp_weixin.getId() : emp_weixin));
			}

		}// for

		logger.info("需要更新的Emp： " + listUpdateEmp.size());

		for (Employee emp_db : listUpdateEmp) {

			JSONObject json = updateWeiXinEmp(emp_db, 1);

			if (json.getString("errcode").equals("0"))
				logger.info(" -- sucess  update emp errorcode : "
						+ json.getString("errcode") + ", empId: "
						+ emp_db.getId() + ", empName: " + emp_db.getName()
						+ ", orgIdlist: " + emp_db.getOrgIdList()
						+ ", errmsg: " + json.getString("errmsg"));
			else
				logger.info(" -- faild  update emp errorcode : "
						+ json.getString("errcode") + ", empId: "
						+ emp_db.getId() + ", empName: " + emp_db.getName()
						+ ", orgIdlist: " + emp_db.getOrgIdList()
						+ ", errmsg: " + json.getString("errmsg"));
		}

		logger.info("同步人员完成...");
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void syncICusetomer() {

		Map<String, Employee> mapAllEmpsByDB = getMapIcustomerDB();

		logger.info("DB当前总的经销商人数：" + mapAllEmpsByDB.size());

		Map<String, Employee> mapAllEmpByWeiXin = getMapEmpByJson("92142", "1",
				"0");
		logger.info("微信通讯录当前总的经销商人数：" + mapAllEmpByWeiXin.size());

		/**
		 * 删除DB中没有的员工
		 */

		List<String> listRemoveKey = new ArrayList<String>();
		for (Entry<String, Employee> entry : mapAllEmpByWeiXin.entrySet()) {

			Employee emp_weixin = entry.getValue();

			if (!mapAllEmpsByDB.containsKey(emp_weixin.getId())) {

				listRemoveKey.add(emp_weixin.getId());

				JSONObject json = delWeiXinEmp(emp_weixin.getId());

				if (json.getString("errcode").equals("0"))
					logger.info(" -- sucess del leave emp errorcode : "
							+ json.getString("errcode") + ", empId: "
							+ emp_weixin.getId());
				else
					logger.info(" -- faild  del leave emp errorcode : "
							+ json.getString("errcode") + ", empId: "
							+ emp_weixin.getId());

			}

		}

		System.out.println(listRemoveKey.size());

		for (String empId : listRemoveKey) {
			mapAllEmpByWeiXin.remove(empId);
		}

		logger.info("删除key后的mapAllEmpByWeiXin ：" + mapAllEmpByWeiXin.size());

		List<Employee> listAddEmp = new ArrayList<Employee>();

		for (Entry<String, Employee> entry : mapAllEmpsByDB.entrySet()) {

			Employee emp_db = entry.getValue();

			if (!mapAllEmpByWeiXin.containsKey(emp_db.getId())) {
				listAddEmp.add(emp_db);
			}
		}

		logger.info("需新增经销商：" + listAddEmp.size());

		for (Employee emp_db : listAddEmp) {

			if (getStrUtil(emp_db.getEmail()) == null)
				emp_db.setEmail(emp_db.getId() + "@want-want.com");

			if (getStrUtil(emp_db.getMobile()) == null)
				emp_db.setMobile(emp_db.getId() + "000");

			if (getStrUtil(emp_db.getJobName()) == null)
				emp_db.setJobName(emp_db.getId() + " position is null");

			JSONObject json = createWeiXinEmp(emp_db);

			if (json.getString("errcode").equals("0"))
				logger.info(" -- sucess  add icustomer errorcode : "
						+ json.getString("errcode") + ", empId: "
						+ emp_db.getId() + ", empName: " + emp_db.getName()
						+ ", orgIdlist: " + emp_db.getOrgIdList()
						+ ", errmsg: " + json.getString("errmsg"));
			else
				logger.info(" -- faild  add icustomer errorcode : "
						+ json.getString("errcode") + ", empId: "
						+ emp_db.getId() + ", empName: " + emp_db.getName()
						+ ", orgIdlist: " + emp_db.getOrgIdList()
						+ ", errmsg: " + json.getString("errmsg"));

		}

		mapAllEmpByWeiXin = getMapEmpByJson("92142", "1", "0");

		logger.info("新增后微信通讯录当前总人数：" + mapAllEmpByWeiXin.size());

		List<Employee> listUpdateEmp = new ArrayList<Employee>();

		logger.info("新增后微信通讯录当前总人数：" + mapAllEmpByWeiXin.size());

		for (Entry<String, Employee> entry : mapAllEmpByWeiXin.entrySet()) {

			try {

				Employee emp_weixin = entry.getValue();

				if (mapAllEmpsByDB.containsKey(emp_weixin.getId())) {

					Employee emp_db = mapAllEmpsByDB.get(emp_weixin.getId());

					if (getStrUtil(emp_db.getEmail()) == null)
						emp_db.setEmail(emp_db.getId() + "@want-want.com");

					if (getStrUtil(emp_db.getMobile()) == null)
						emp_db.setMobile(emp_db.getId() + "000");

					// if(getStrUtil(emp_db.getJobName())==null)
					// emp_db.setJobName(emp_db.getId()+" position is null");

					if (emp_weixin.getName().trim()
							.equalsIgnoreCase(emp_db.getName().trim()) == false
							// ||
							// emp_weixin.getJobName().trim().equalsIgnoreCase(emp_db.getJobName().trim())==false
							|| emp_weixin.getEmail().trim()
									.equalsIgnoreCase(emp_db.getEmail().trim()) == false
							|| emp_weixin.getMobile().trim()
									.equalsIgnoreCase(emp_db.getMobile()) == false) {

						logger.info(" -- weixin , empId: " + emp_weixin.getId()
								+ ", empName: " + emp_weixin.getName()
								// + ", jobName: " + emp_weixin.getJobName()
								+ ", email: " + emp_weixin.getEmail()
								+ ", mobile: " + emp_weixin.getMobile());

						logger.info(" -- db     , empId: " + emp_db.getId()
								+ ", empName: " + emp_db.getName()
								// + ", jobName: "+emp_db.getJobName()
								+ ", email: " + emp_db.getEmail()
								+ ", moblie: " + emp_db.getMobile());

						listUpdateEmp.add(emp_db);

					}

				}

			} catch (Exception e) {
				logger.error(e.toString());
			}

		}// for

		logger.info("需要更新的经销商： " + listUpdateEmp.size());

		for (Employee emp_db : listUpdateEmp) {

			JSONObject json = updateWeiXinEmp(emp_db, 1);

			if (json.getString("errcode").equals("0"))
				logger.info(" -- sucess  update icustomer errorcode : "
						+ json.getString("errcode") + ", empId: "
						+ emp_db.getId() + ", empName: " + emp_db.getName()
						+ ", errmsg: " + json.getString("errmsg"));
			else
				logger.info(" -- faild  update icustomer errorcode : "
						+ json.getString("errcode") + ", empId: "
						+ emp_db.getId() + ", empName: " + emp_db.getName()
						+ ", errmsg: " + json.getString("errmsg"));
		}

	}

	public void syncOrg() {

		/**
		 * 新增
		 */
		Map<String, Organization> mapOrgDB = getMapOrgByDB();// 数据库获取的所有Org
		Map<String, String> mapOrgMapping = getOrgMappingWeiXinOrgIds("1"); // 映射表所有Org
		List<Organization> listAdd = new ArrayList<Organization>();
		logger.info("新增前mapOrgMapping： " + mapOrgMapping.size());

		for (Entry<String, Organization> entry : mapOrgDB.entrySet()) {

			Organization org_db = entry.getValue();

			if (!mapOrgMapping.containsKey(org_db.getId())) {

				listAdd.add(org_db);

				logger.info("需要新增的组织有: " + org_db.getId());
			}
		}

		logger.info("共需新增 -- " + listAdd.size());

		/** 排序 level 等级从高到低 */
		sortAddOrg(listAdd);

		logger.info("开始新增 -- ");

		for (Organization org_db : listAdd) {

			try {

				if (mapOrgMapping.containsKey(org_db.getParentDept())) {

					String orgName = org_db.getName();

					String parent_wechat_orgId = mapOrgMapping.get(org_db
							.getParentDept());

					JSONObject returnJson = createWeiXinOrg(orgName,
							parent_wechat_orgId);

					if (returnJson.getString("errcode").equals("0")) {

						logger.info(" -- sucess  add org new errorcode : "
								+ returnJson.getString("errcode") + ", orgId: "
								+ org_db.getId() + ", orgName: "
								+ org_db.getName() + ", weixinId: "
								+ returnJson.getString("id"));

						webchatJdbcTemplate.update(INSER_SQL, org_db.getId(),
								returnJson.getString("id"));

						mapOrgMapping = getOrgMappingWeiXinOrgIds("1"); // 有增
																		// 重新获取

					} else
						logger.info(" -- faild  add org new errorcode : "
								+ returnJson.getString("errcode") + ", orgId: "
								+ org_db.getId() + ", orgName: "
								+ org_db.getName() + ", weixinId: "
								+ returnJson.getString("id"));

				}

				else
					logger.info("新增Org 的上层组织无法在mapping表中找到:　orgId : "
							+ org_db.getId() + ", parentId: "
							+ org_db.getParentDept());

			} catch (Exception e) {
				logger.error(e.toString());
			}

		}// for

		logger.info("新增后 mapOrgMapping size： " + mapOrgMapping.size());

		List<Organization> listUpdateOrg = new ArrayList<Organization>();

		Map<String, String> mapOrgMapping_wechat = getOrgMappingWeiXinOrgIds("0");

		List<Organization> listAllOrgByWeiXin = getListAllOrgFromWeiXin();

		for (Organization org_weixin : listAllOrgByWeiXin) {

			try {

				if (org_weixin.getId().equals("80521"))
					continue;

				String orgId_weixin_db = "";

				if (mapOrgMapping_wechat.containsKey(org_weixin.getId()))
					orgId_weixin_db = mapOrgMapping_wechat.get(org_weixin
							.getId());
				else {
					logger.info(" 246 count -- " + org_weixin.getId()
							+ " 在DBMapping中找不到");
					continue;
				}

				if (mapOrgDB.containsKey(orgId_weixin_db)) {

					Organization org_db = mapOrgDB.get(orgId_weixin_db);

					if (!org_weixin.getName().trim()
							.equalsIgnoreCase(org_db.getName().trim())
							|| !mapOrgMapping_wechat.get(
									org_weixin.getParentDept().trim())
									.equalsIgnoreCase(org_db.getParentDept())) {

						logger.info(" -- weixin , orgId: "
								+ mapOrgMapping_wechat.get(org_weixin.getId())
								+ ", orgName: "
								+ org_weixin.getName()
								+ ", parentId: "
								+ mapOrgMapping_wechat.get(org_weixin
										.getParentDept().trim()));
						logger.info(" -- db     , orgId: " + org_db.getId()
								+ ", orgName: " + org_db.getName()
								+ ", parentId: " + org_db.getParentDept());
						listUpdateOrg.add(org_db);
					}
				} else {
					logger.info("[不要的组织？]不存在在人事系统中的组织： " + orgId_weixin_db);
				}

			} catch (Exception e) {
				logger.error(e.toString());
			}

		}// for

		logger.info("需要更新的组织： " + listUpdateOrg.size());

		mapOrgMapping = getOrgMappingWeiXinOrgIds("1");
		// 开始更新
		for (Organization org_db : listUpdateOrg) {

			if (mapOrgMapping.containsKey(org_db.getId())
					&& mapOrgMapping.containsKey(org_db.getParentDept())) {

				Organization org = new Organization();

				org.setId(mapOrgMapping.get(org_db.getId()));
				org.setName(org_db.getName());
				org.setParentDept(mapOrgMapping.get(org_db.getParentDept()));

				JSONObject returnJson = updateWeiXinOrg(org);

				if (returnJson.getString("errcode").equals("0"))
					logger.info(" -- sucess  update org orgId:  " + org.getId()
							+ ", orgName: " + org.getName());

				else
					logger.info(" -- faild  update org org orgId : "
							+ org.getId() + ", orgName: " + org.getName());

			} else {
				logger.info("更新组织过程中不存在在mappiong表中的: " + org_db.getId());
			}
		}

		logger.info("更新组织完成...");
	}

	public List<Organization> getListAllOrgFromWeiXin() {

		List<Organization> listOrgs = new ArrayList<Organization>();

		JSONObject jAll = getAllOrgFromWeiXin();

		JSONArray jsonArray = JSONArray.fromObject(jAll.get("department"));

		String errorCode = jAll.getString("errcode");
		String errMsg = jAll.getString("errmsg");

		if (errorCode.equals("0") && errMsg.equals("ok")) {

			List<Map<String, Object>> listJson = (List<Map<String, Object>>) jsonArray
					.toCollection(jsonArray, Map.class);

			for (Map<String, Object> map : listJson) {

				Organization org = new Organization();

				if (map.get("id") != null)
					org.setId(map.get("id").toString());

				if (map.get("name") != null)
					org.setName(map.get("name").toString());

				if (map.get("parentid") != null)
					org.setParentDept(map.get("parentid").toString());

				listOrgs.add(org);
			}
		}

		return listOrgs;

	}

	public void sortAddOrg(List<Organization> listAdd) {
		Comparator<Organization> comparator = new Comparator<Organization>() {
			public int compare(Organization o1, Organization o2) {

				int o1Level = Integer.parseInt(o1.getOrgLevel());
				int o2Level = Integer.parseInt(o2.getOrgLevel());
				if (o1Level > o2Level)
					return 1;
				else if (o1Level == o2Level)
					return 0;
				else
					return -1;
			}
		};

		Collections.sort(listAdd, comparator);

		Collections.reverse(listAdd);

	}

	public Map<String, Organization> getMapOrgByDB() {

		Map<String, Organization> mapOrgs = new HashMap<String, Organization>();

		String sql = "select ORG_ID,ORG_NAME,ORG_PARENT_DEPT,ORG_LEVEL from HRORG.ORGANIZATION where ORG_ID NOT IN ('00000000')";

		List<Map<String, Object>> list = portalJdbcOperations.queryForList(sql);

		for (Map<String, Object> map : list) {

			Organization org = new Organization();

			org.setId(map.get("ORG_ID").toString());
			org.setName(map.get("ORG_NAME").toString());
			org.setParentDept(map.get("ORG_PARENT_DEPT").toString());
			org.setOrgLevel(map.get("ORG_LEVEL").toString());

			mapOrgs.put(map.get("ORG_ID").toString(), org);

		}

		return mapOrgs;

	}

	public JSONObject getAllEmpFromWeiXin(String orgId, String fetch_child,
			String status) {

		String createRequestUrl = String.format(requestFindAllEmpURL,
				ACCESS_TOKEN, orgId, fetch_child, status);

		return httpRequest(createRequestUrl, "GET", null);
	}

	public JSONObject getAllOrgFromWeiXin() {

		String createRequestUrl = String.format(rquestFindAllOrgURL,
				ACCESS_TOKEN);

		return httpRequest(createRequestUrl, "GET", null);
	}

	public Map<String, Employee> getMapEmpByJson(String orgId,
			String fetch_child, String status) {

		Map<String, Employee> mapEmps = new HashMap<String, Employee>();

		// JSONObject jAll = getAllEmpFromWeiXin("91803","1","0");
		JSONObject jAll = getAllEmpFromWeiXin(orgId, fetch_child, status);

		JSONArray jsonArray = JSONArray.fromObject(jAll.get("userlist"));

		String errorCode = jAll.getString("errcode");
		String errMsg = jAll.getString("errmsg");

		if (errorCode.equals("0") && errMsg.equals("ok")) {

			List<Map<String, Object>> listJson = (List<Map<String, Object>>) jsonArray
					.toCollection(jsonArray, Map.class);

			for (Map<String, Object> map : listJson) {

				Employee emp = new Employee();

				if (map.get("userid") != null)
					emp.setId(map.get("userid").toString());

				if (map.get("name") != null)
					emp.setName(map.get("name").toString());

				if (map.get("position") != null)
					emp.setJobName(map.get("position").toString());

				if (map.get("mobile") != null)
					emp.setMobile(map.get("mobile").toString());

				if (map.get("email") != null)
					emp.setEmail(map.get("email").toString());

				if (map.get("department") != null) {

					ArrayList<Integer> listOrg = (ArrayList<Integer>) map
							.get("department");

					List<String> orgList = new ArrayList<String>();

					for (int orgIdInteger : listOrg) {
						orgList.add(orgIdInteger + "");
					}

					emp.setOrgIdList(orgList);

				}

				mapEmps.put(emp.getId(), emp);

			}// for

		}

		return mapEmps;
	}

	public Map<String, Employee> getMapIcustomerDB() {

		Map<String, Employee> mapDB = new HashMap<String, Employee>();

		/*
		 * String sql =
		 * "select i.ACCOUNT,i.PASSWORD,i.USER_NAME,i.MOBILE,i.STATUS,t.TYPE_NAME from ICUSTOMER.USER_INFO_TBL i , ICUSTOMER.USER_TYPE_TBL t "
		 * +" where i.USER_TYPE_SID=t.SID AND i.USER_TYPE_SID in ('1','10')";
		 */

		// String sql = "select * from  icustomer.CUSTOMER_INFO_TBL  a"
		// +" inner join ("
		// +" select CUSTOMER_ID  from  icustomernw.CUSTOMER_PRODUCT_REL  group by CUSTOMER_ID having count(PRODUCT_ID)>3) b"
		// +" on a.ID=b.CUSTOMER_ID"
		// +" where a.ID = a.ID_FRIEND and a.STATUS is null";
		//排除直营现渠经销商 CREDIT_ID not in ('CCB','CD8')    Modify by David Luo on 20150827
		/* Modify by David on 20151118
		String sql = "SELECT customer.ID ,customer.NAME,customer.owner,customer.MOBILE"
				+ " FROM ICUSTOMERNW.SALES_RESULTS_DETAIL_BW  sales"
				+ " inner join ICUSTOMERNW.CUSTOMER customer on customer.ID=substr(sales.CUSTOMER_ID,6,length(sales.CUSTOMER_ID))"
				+ " inner join  ICUSTOMERNW.EMP_CUSTOMER_DIVSION_REL  ECDL on CUSTOMER.ID = ECDL.CUSTOMER_ID and ECDL.CREDIT_ID not in ('CCB','CD8') "
				+ " where sales.YEARMONTH between to_char(add_months(sysdate,-3),'yyyymm') and to_char(add_months(sysdate,-1),'yyyymm') and customer.status is null"
				+ " group by customer.ID ,customer.NAME,customer.owner,customer.MOBILE";
*/
		String sql = " SELECT customer.ID ,customer.NAME,customer.owner,customer.MOBILE "+
						" FROM ICUSTOMERNW.CUSTOMER customer "+
						" inner join  ICUSTOMERNW.EMP_CUSTOMER_DIVSION_REL  ECDL on CUSTOMER.ID = ECDL.CUSTOMER_ID "+
						" where customer.status is null  and ECDL.CREDIT_ID not in ('CCB','CD8') " +
						" group by customer.ID ,customer.NAME,customer.owner,customer.MOBILE ";
		
		List<Map<String, Object>> list = cedatadevJdbcOperations
				.queryForList(sql);

		for (Map<String, Object> map : list) {
			Employee employee = new Employee();

			employee.setId(map.get("ID") == null ? null : map.get("ID")
					.toString());

			employee.setName(map.get("NAME") == null ? "" : map.get("NAME")
					.toString());

			employee.setMobile(map.get("MOBILE") == null ? "" : map.get(
					"MOBILE").toString());

			List<String> listOrg = new ArrayList<String>();
			listOrg.add("92142");
			employee.setOrgIdList(listOrg);

			mapDB.put(map.get("ID").toString(), employee);
		}

		return mapDB;
	}

	public Map<String, Employee> getMapEmpByDB() {

		/** 旺旺组织号、微信组织好对照表 */
		Map<String, String> mapOrgMapping = getOrgMappingWeiXinOrgIds("1");

		Map<String, Employee> mapAllEmps = new HashMap<String, Employee>();

		String sql = " SELECT e.EMP_ID,e.EMP_NAME,e_p.JOB_NAME,e.EMP_EMAIL,e.EMP_MOBILE,e_p.POS_ID,p.ORG_ID"
				+ " FROM HRORG.EMP e,HRORG.POSITION p,"
				+ " ("
				+ " select EMP_ID,POS_ID,JOB_NAME from HRORG.EMP_POSITION where POS_ID in"
				+ " ( select POS_ID from HRORG.EMP_POSITION group by POS_ID having count(POS_ID) = 1)"
				// +" and MASTER_POS = 1"
				+ " ) e_p"
		//		+ " where e.EMP_ID = e_p.EMP_ID and  e_p.POS_ID=p.POS_ID";
		//Modify by david on 20150818 排除台湾与非本业
		+ " where e.EMP_ID = e_p.EMP_ID and  e_p.POS_ID=p.POS_ID and EMP_INDUSTRY=1 and EMP_AREA_ID<>'TWZB'";
		List<Map<String, Object>> list = portalJdbcOperations.queryForList(sql);

		for (Map<String, Object> map : list) {

			if (map.get("ORG_ID").toString().equals("00000000"))
				continue;

			Employee emp = new Employee();

			String key = map.get("EMP_ID").toString();
			emp.setId(key);

			emp.setName(map.get("EMP_NAME").toString());

			emp.setJobName(map.get("JOB_NAME").toString());

			if (map.get("EMP_EMAIL") != null)
				emp.setEmail(map.get("EMP_EMAIL").toString());

			if (map.get("EMP_MOBILE") != null)
				emp.setMobile(map.get("EMP_MOBILE").toString());

			// emp.setOrgIdList(Arrays.asList(new
			// String[]{map.get("ORG_ID").toString()}));

			List<String> listOrgIds = new ArrayList<String>();

			String wwoa_org_id = map.get("ORG_ID").toString();

			if (mapOrgMapping.containsKey(wwoa_org_id)) {
				listOrgIds.add(mapOrgMapping.get(wwoa_org_id));
			} else
				logger.info(" -员工组织不存在在 mapping表中 ： empId: " + emp.getId()
						+ ", orgId" + wwoa_org_id);

			emp.setOrgIdList(listOrgIds);

			Employee returnEmp = mapAllEmps.put(key, emp);

			/* 一人多岗位 */
			if (returnEmp != null) {
				emp.getOrgIdList().addAll(returnEmp.getOrgIdList());
			}

		}// for

		return mapAllEmps;

	}

	public Map<String, String> getOrgMappingWeiXinOrgIds(String type) {

		Map<String, String> mapOrgMapping = new HashMap<String, String>();

		String sql = "select WW_ORG_ID,WECHAT_ORG_ID from WECHAT.ORG_MAPPING where WW_ORG_ID NOT IN ('00000000')";

		List<Map<String, Object>> listOrgMapping = webchatJdbcTemplate
				.queryForList(sql);

		for (Map<String, Object> map : listOrgMapping) {

			if (type.endsWith("1"))
				mapOrgMapping.put(map.get("WW_ORG_ID").toString(),
						map.get("WECHAT_ORG_ID").toString());
			else
				mapOrgMapping.put(map.get("WECHAT_ORG_ID").toString(),
						map.get("WW_ORG_ID").toString());

		}

		return mapOrgMapping;
	}

//	public void createOrgAll() throws Exception {
//		webchatJdbcTemplate.update("delete from wechat.ORG_MAPPING");
//
//		Map<String, String> mapError = new HashMap<String, String>();
//
//		createOrgTree(WANTWANTGROUP_ORGID, "1", mapError);
//
//		logger.info(" ** 同步组织已完成 ,其中失败的组织有：" + mapError.size());
//
//		if (mapError.size() > 0) {
//
//			logger.info(" ** 开始重新创建失败的Org!");
//
//			Map<String, String> mapResendError = new HashMap<String, String>();
//
//			for (Entry<String, String> entry : mapError.entrySet()) {
//				createOrgTree(entry.getKey(), entry.getValue(), mapResendError);
//			}
//
//			logger.info(" ** 重新创建后当前失败的Org：" + mapResendError.size());
//
//		}
//	}

	/*
	public void createICustomerAll() throws Exception {

		List<Employee> listAllIcustomer = ldapService.getLDAPListIcustomer();

		List<Employee> listError = addEmpMehotd(listAllIcustomer);

		if (listError.size() > 0) {

			for (int i = 0; i < listError.size(); i++) {

				Employee emp = listError.get(i);

				if (emp.getBirthday() != null
						&& emp.getBirthday().indexOf("mobile") != -1)
					emp.setMobile(emp.getId() + "000");

				if (emp.getBirthday() != null
						&& emp.getBirthday().indexOf("email") != -1)
					emp.setEmail(emp.getId() + "@want-want.com");

			}

			logger.info(" ** 开始添加 失败的人员  ** ");

			List<Employee> listErrorResend = addEmpMehotd(listError);

			logger.info(" ** 重新添加后当前失败的Emp：" + listErrorResend.size());
		}

	}
	*/
	
	
//	public void createEmpAll() throws Exception {
//		/**
//		 * 获取LDS所有旺旺员工(对象中封装orgIdList)
//		 */
//		List<Employee> listAllEmps = searchEmpInfosFormLds(getMappingOrgMap());
//
//		List<Employee> listError = addEmpMehotd(listAllEmps);
//
//		logger.info(" ** 人员添加完成, 添加失败的人员 有: " + listError.size());
//
//		if (listError.size() > 0) {
//
//			for (int i = 0; i < listError.size(); i++) {
//
//				Employee emp = listError.get(i);
//
//				if (emp.getBirthday() != null
//						&& emp.getBirthday().indexOf("mobile") != -1)
//					emp.setMobile(emp.getId() + "000");
//
//				if (emp.getBirthday() != null
//						&& emp.getBirthday().indexOf("email") != -1)
//					emp.setEmail(emp.getId() + "@want-want.com");
//
//			}
//
//			logger.info(" ** 开始添加 失败的人员  ** ");
//
//			List<Employee> listErrorResend = addEmpMehotd(listError);
//
//			logger.info(" ** 重新添加后当前失败的Emp：" + listErrorResend.size());
//		}
//
//	}

	public List<Employee> addEmpMehotd(List<Employee> listAllEmps) {

		List<Employee> listError = new ArrayList<Employee>();

		for (int i = 0; i < listAllEmps.size(); i++) {

			Employee emp = listAllEmps.get(i);

			try {

				if (emp.getOrgIdList() == null)
					continue;
				if (emp.getOrgIdList().size() <= 0)
					continue;

				if (getStrUtil(emp.getEmail()) == null)
					emp.setEmail(emp.getId() + "@want-want.com");

				if (getStrUtil(emp.getMobile()) == null)
					emp.setMobile(emp.getId() + "000");

				if (getStrUtil(emp.getJobName()) == null)
					emp.setJobName(emp.getId() + " position is null");

				JSONObject json = createWeiXinEmp(emp);

				if (json.getString("errcode").equals("0")) {

					logger.info(" -- sucess  errorcode : "
							+ json.getString("errcode") + ", empId: "
							+ emp.getId() + ", orgIdlist: "
							+ emp.getOrgIdList() + ", errmsg: "
							+ json.getString("errmsg"));

				} else {

					emp.setBirthday(json.getString("errmsg"));

					listError.add(emp);

					logger.info(" -- faild  errorcode : "
							+ json.getString("errcode") + ", empId: "
							+ emp.getId() + ", orgIdlist: "
							+ emp.getOrgIdList() + ", errmsg: "
							+ json.getString("errmsg"));

					/**
					 * 如果超过频率 睡眠1h
					 */
					if (json.getString("errmsg").indexOf(
							"api freq out of limit") != -1) {

						logger.info(" ~~ZZZ ~~~ 已超过微信限定频率, 开始睡眠 ~~ ");

						Thread.sleep(3600 * 1000);

						logger.info(" ~~ =_= ~~~ 线程睡醒开始重新试图 请求 微信平台 ~~ ");

					}
				}

			} catch (Exception e) {

				listError.add(emp);

				logger.info(e.toString());

			}

		}

		return listError;

	}

	public String getStrUtil(String str) {
		if (str == null)
			return null;
		else if (str.trim().equals("") == true)
			return null;
		else
			return str.trim();

	}

	public Map<String, String> getMappingOrgMap() {

		Map<String, String> mappingOrgMap = null;

		String sql = "select * from wechat.ORG_MAPPING";

		List<Map<String, Object>> list = webchatJdbcTemplate.queryForList(sql);

		if (list != null && list.size() > 0) {

			mappingOrgMap = new HashMap<String, String>();

			for (Map<String, Object> map : list) {

				mappingOrgMap.put(map.get("WW_ORG_ID").toString(),
						map.get("WECHAT_ORG_ID").toString());

			}

			logger.info("ORG_MAPPING size：" + mappingOrgMap.size());
		}

		return mappingOrgMap;
	}

	/*
	public List<Employee> searchEmpInfosFormLds(
			Map<String, String> mappingOrgMap) throws NamingException,
			IOException {

		List<Employee> listEmp = ldapService.getLDAPMaps();

		if (listEmp != null && listEmp.size() > 0) {

			logger.info("LDS 中旺旺集团员工数量: " + listEmp.size());

			for (Employee emp : listEmp) {

				List<String> memeberOfList = emp.getMemberOfList();

				if (memeberOfList != null && memeberOfList.size() > 0) {

					List<String> orgIdList = new ArrayList<String>();

					for (String memeberOf : memeberOfList) {

						try {

							String orgId = ldapService.getOrgIdByPos(memeberOf);

							if (orgId != null) {

								if (mappingOrgMap.containsKey(orgId))
									orgIdList.add(mappingOrgMap.get(orgId));
							}

						} catch (Exception e) {
							logger.error(memeberOf + " -- " + e.toString());
						}

					}// for

					emp.setOrgIdList(orgIdList);
				}
			}
		}

		return listEmp;
	}
	*/

//	public void createOrgTree(String orgId, String parentId,
//			Map<String, String> mapError) throws NamingException,
//			OrganizationNotFoundException {
//
//		try {
//
//			Organization orgParent = ldapService.getOrganizationById(orgId,
//					"weixin");
//
//			JSONObject returnJson = createWeiXinOrg(orgParent.getName(),
//					parentId);
//
//			if (returnJson.getString("errcode").equals("0")) {
//
//				logger.info(" -- sucess  errorcode : "
//						+ returnJson.getString("errcode") + ", orgId: "
//						+ orgParent.getId() + ", orgName: "
//						+ orgParent.getName() + ", weixinId: "
//						+ returnJson.getString("id"));
//
//				webchatJdbcTemplate.update(INSER_SQL, orgId,
//						returnJson.getString("id"));
//
//				List<String> listMemeber = orgParent.getMember();
//				if (listMemeber != null) {
//
//					for (String member : listMemeber) {
//						// CN=11060001,CN=上海总部,CN=Organizations,CN=Groups,CN=PartitionWWLDS1,DC=want-want,DC=com
//						String childOrgId = member.split(",")[0].substring(
//								"CN=".length(), member.split(",")[0].length())
//								.trim();
//
//						createOrgTree(childOrgId, returnJson.getString("id"),
//								mapError);
//
//					}
//
//				}
//
//			} else {
//
//				mapError.put(orgId, parentId);
//
//				logger.info(" -- faild  errorcode : "
//						+ returnJson.getString("errcode") + ", orgId: "
//						+ orgParent.getId() + ", orgName: "
//						+ orgParent.getName() + ", weixinId: "
//						+ returnJson.getString("id"));
//
//				if (returnJson.getString("errmsg").indexOf(
//						"api freq out of limit") != -1) {
//
//					logger.info(" ~~ZZZ ~~~ 已超过微信限定频率, 开始睡眠 ~~ ");
//
//					Thread.sleep(3600 * 1000);
//
//					logger.info(" ~~ =_= ~~~ 线程睡醒开始重新试图 请求 微信平台 ~~ ");
//
//				}
//
//			}
//
//		} catch (Exception e) {
//
//			mapError.put(orgId, parentId);
//
//			logger.error(e.toString());
//
//		}
//
//	}

	/**
	 * 更新组织
	 * 
	 * @param org
	 * @return
	 */
	public JSONObject updateWeiXinOrg(Organization org) {

		JSONObject jsonOrg = new JSONObject();

		jsonOrg.element("id", org.getId());// 唯一

		jsonOrg.element("name", org.getName());

		jsonOrg.element("parentid", org.getParentDept());

		jsonOrg.element("order", "1");

		String createRequestUrl = String.format(requestUpdateOrgURL,
				ACCESS_TOKEN);

		return httpRequest(createRequestUrl, "POST", jsonOrg.toString());

	}

	/**
	 * 更新 成员
	 * 
	 * @param emp
	 * @param leaveEmp
	 * @return
	 */
	public JSONObject updateWeiXinEmp(Employee emp, int isLeave) {

		JSONObject jsonOrg = new JSONObject();

		jsonOrg.element("userid", emp.getId());// 唯一

		jsonOrg.element("name", emp.getName());

		jsonOrg.element("department", emp.getOrgIdList());

		jsonOrg.element("position", emp.getJobName());

		jsonOrg.element("mobile", emp.getMobile());// 唯一
		jsonOrg.element("email", emp.getEmail()); // 唯一

		/** 是否禁用 （离职禁用） */
		jsonOrg.element("enable", isLeave);

		String createRequestUrl = String.format(requestUpdateEmpURL,
				ACCESS_TOKEN);

		return httpRequest(createRequestUrl, "POST", jsonOrg.toString());

	}

	public JSONObject createWeiXinOrg(String orgName, String parentId) {

		JSONObject jsonOrg = new JSONObject();

		jsonOrg.element("name", orgName);
		jsonOrg.element("parentid", parentId);
		jsonOrg.element("order", "1");

		String createRequestUrl = String.format(requestCreateOrgURL,
				ACCESS_TOKEN);

		return httpRequest(createRequestUrl, "POST", jsonOrg.toString());

	}

	public JSONObject createWeiXinEmp(Employee emp) {

		JSONObject jsonOrg = new JSONObject();

		jsonOrg.element("userid", emp.getId());// 唯一

		jsonOrg.element("name", emp.getName());
		jsonOrg.element("department", emp.getOrgIdList());
		jsonOrg.element("position", emp.getJobName());

		jsonOrg.element("mobile", emp.getMobile());// 唯一
		jsonOrg.element("email", emp.getEmail()); // 唯一

		String createRequestUrl = String.format(requestCreateEmpURL,
				ACCESS_TOKEN);

		return httpRequest(createRequestUrl, "POST", jsonOrg.toString());

	}

	public JSONObject delWeiXinEmp(String empId) {

		String createRequestUrl = String.format(requestDeleEmpURL,
				ACCESS_TOKEN, empId);

		return httpRequest(createRequestUrl, "GET", null);

	}

	/**
	 * 
	 * 发起https请求并获取结果
	 * 
	 * 
	 * 
	 * @param requestUrl
	 *            请求地址
	 * 
	 * @param requestMethod
	 *            请求方式（GET、POST）
	 * 
	 * @param outputStr
	 *            提交的数据
	 * 
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */

	public JSONObject httpRequest(String requestUrl, String requestMethod,
			String outputStr) {

		JSONObject jsonObject = null;

		StringBuffer buffer = new StringBuffer();

		try {

			// 创建SSLContext对象，并使用我们指定的信任管理器初始化

			TrustManager[] tm = { new WantWantX509TrustManager() };

			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");

			sslContext.init(null, tm, new java.security.SecureRandom());

			// 从上述SSLContext对象中得到SSLSocketFactory对象

			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);

			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
					.openConnection();
			
			//设定超时时间 Modify by david Luo on 20151112
			httpUrlConn.setConnectTimeout(3*60*1000);
			
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);

			httpUrlConn.setDoInput(true);

			httpUrlConn.setUseCaches(false);

			// 设置请求方式（GET/POST）

			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// 当有数据需要提交时

			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();

				// 注意编码格式，防止中文乱码

				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.flush();
				outputStream.close();

			}

			// 将返回的输入流转换成字符串

			InputStream inputStream = httpUrlConn.getInputStream();

			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String str = null;

			while ((str = bufferedReader.readLine()) != null) {

				buffer.append(str);

			}

			bufferedReader.close();
			
			inputStreamReader.close();

			// 释放资源

			inputStream.close();

			inputStream = null;

			httpUrlConn.disconnect();

			jsonObject = JSONObject.fromObject(buffer.toString());

		} catch (ConnectException ce) {
			logger.error("****************          Weixin server connection timed out.");
			logger.error(ce.toString());
			//System.out.println("Weixin server connection timed out.");
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return jsonObject;

	}

	/**
	 * 
	 * @author 00159184
	 * 
	 */
	class TokenThread implements Runnable {

		@Override
		public void run() {

			logger.info("守护线程已开启...");

			while (true) {

				if (threadFinished == true) {
					logger.info(" ** 结束守护线程!");

					break;
				}

				try {

					String requestUrl = String.format(requestAccesTokenURL,
							CORPID, CORPSECRET);

					JSONObject json = httpRequest(requestUrl, "GET", null);

					ACCESS_TOKEN = json.getString("access_token");

					logger.info(new Date() + "守护线程成功获取 ACCESS_TOKEN -- "
							+ ACCESS_TOKEN);

					/*
					 * int expires = Integer
					 * .parseInt(json.getString("expires_in"));
					 * 
					 * Thread.sleep((expires-1800) * 1000);
					 */

					Thread.sleep((7200 - 2500) * 1000);

				} catch (Exception e) {
					logger.error(e.toString());

					try {
						Thread.sleep(20 * 1000);
					} catch (InterruptedException e1) {

						logger.error(e.toString());
					}
				}

			}
		}

	}

	public void delOrg() {

		String url = "https://qyapi.weixin.qq.com/cgi-bin/department/delete?access_token=%s&id=%s";

		String sql = "select * from wechat.ORG_MAPPING order by WECHAT_ORG_ID desc";

		List<Map<String, Object>> list = webchatJdbcTemplate.queryForList(sql);

		for (Map<String, Object> map : list) {

			try {

				String orgId = map.get("WECHAT_ORG_ID").toString();
				;

				String requestUrl = String.format(url, ACCESS_TOKEN, orgId);

				JSONObject json = httpRequest(requestUrl, "GET", null);

				if (json != null
						&& json.getString("errcode").equalsIgnoreCase("0")) {

					logger.info(orgId + " --del");

				} else if (json != null
						&& json.getString("errmsg").indexOf(
								"api freq out of limit") != -1) {

					logger.info(" ~~ZZZ ~~~ 已超过微信限定频率, 开始睡眠 ~~ ");

					Thread.sleep(3600 * 1000);

					logger.info(" ~~ =_= ~~~ 线程睡醒开始重新试图 请求 微信平台 ~~ ");
				}

			} catch (Exception e) {
				logger.error(e.toString());
			}

		}// for

	}

	public void delEmp() {

		boolean flag = false;

		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/delete?access_token=%s&userid=%s";

		String sql = "select * from hrorg.emp";

		List<Map<String, Object>> list = this.getPortalJdbcOperations()
				.queryForList(sql);

		for (int i = list.size() - 1; i > 0; i--) {

			Map<String, Object> map = list.get(i);

			try {

				String empId = map.get("EMP_ID").toString();

				if (empId.equals("00023386"))
					flag = true;

				if (flag == false)
					continue;

				String requestUrl = String.format(url, ACCESS_TOKEN, empId);

				JSONObject json = httpRequest(requestUrl, "GET", null);

				if (json != null
						&& json.getString("errcode").equalsIgnoreCase("0")) {

					logger.info(empId + " -- del");

				} else if (json != null
						&& json.getString("errmsg").indexOf(
								"api freq out of limit") != -1) {
					logger.info(" ~~ZZZ ~~~ 已超过微信限定频率, 开始睡眠 ~~ ");

					Thread.sleep(3600 * 1000);

					logger.info(" ~~ =_= ~~~ 线程睡醒开始重新试图 请求 微信平台 ~~ ");
				}

			} catch (Exception e) {

				logger.error(e.toString());

			}
		}
	}

	/*
	 * 比较两个集合中是否有不同的元素
	 */
	private boolean getDiffrent(Collection<String> list1,
			Collection<String> list2) {

		if (list1 == null && list2 == null)
			return true;
		if (list1 == null && list2 != null)
			return false;
		if (list1 != null && list2 == null)
			return false;

		/* 如果元素数量不等直接判定为异常账号 */
		if (list1.size() != list2.size())
			return false;

		boolean flag = true;

		Map<String, Integer> map = new HashMap<String, Integer>();

		for (String string : list1) {
			map.put(string, 1);
		}

		for (String string : list2) {
			Integer value = map.get(string);
			if (value == null) {
				flag = false;
				break;
			}

		}
		return flag;
	}

	public void syncDelOrg() {

		Map<String, Organization> mapOrgDB = getMapOrgByDB();// 数据库获取的所有Org

		Map<String, String> mapOrgMapping = getOrgMappingWeiXinOrgIds("2"); // 获取映射表

		Map<String, String> mapOrgMapping_key = getOrgMappingWeiXinOrgIds("1"); // 获取映射表

		JSONObject jUsers = getAllOrgIdsByWechat("80521");

		if (jUsers.getString("errcode").equals("0")) {

			List<String> listWechatOrgIds = new ArrayList<String>();

			JSONArray jsonArrayOrgList = JSONArray.fromObject(jUsers
					.get("department"));

			List<Map<String, Object>> listOrgWechat = (List<Map<String, Object>>) jsonArrayOrgList
					.toCollection(jsonArrayOrgList, Map.class);

			logger.info("当前微信通讯录共有组织数：" + listOrgWechat.size());

			List<String> listRemove = new ArrayList<String>();

			for (Map<String, Object> mapWechat : listOrgWechat) {

				try {
					if (mapOrgMapping.containsKey(mapWechat.get("id")
							.toString()))
						listWechatOrgIds.add(mapOrgMapping.get(mapWechat.get(
								"id").toString()));

					else {

						logger.info("☆☆☆☆☆  微信通讯录有 但是 映射表没有的 orgId: "
								+ mapWechat.get("id").toString() + "orgName："
								+ mapWechat.get("name").toString());

						listRemove.add(mapWechat.get("id").toString());
					}

				} catch (Exception e) {
					logger.error(e.toString());
				}

			}// for

			for (String orgId : listWechatOrgIds) {

				if (!mapOrgDB.containsKey(orgId)) {

					try {
						listRemove.add(mapOrgMapping_key.get(orgId));
					} catch (Exception e) {
						logger.error(e.toString());
					}
				}

			} // for

			logger.info("需要删除的组织 -- " + listRemove.size());

			Comparator<String> comparator = new Comparator<String>() {

				public int compare(String s1, String s2) {

					int t1 = Integer.parseInt(s1);
					int t2 = Integer.parseInt(s2);

					if (t1 > t2)
						return -1;
					else if (t1 == t2)
						return 0;
					else
						return 1;

				}
			};

			Collections.sort(listRemove, comparator);

			for (String wechatId : listRemove) {

				try {

					JSONObject jsObject = delOrgById(wechatId);

					String sql = "delete from ORG_MAPPING where WECHAT_ORG_ID=?";

					webchatJdbcTemplate.update(sql, wechatId);

					if (jsObject.getString("errcode").equals("0"))
						logger.info("删除org [成功] orgId -- " + wechatId);
					else
						logger.info("删除org [失败] orgId -- " + wechatId
								+ ", errorMessage： "
								+ jsObject.getString("errmsg"));

				} catch (Exception e) {
					logger.error(e.toString());
				}

			}// for

		}

	}

	public JSONObject getAllOrgIdsByWechat(String orgId) {

		String createRequestUrl = String.format(requestGetAllOrg, ACCESS_TOKEN,
				orgId);

		return httpRequest(createRequestUrl, "GET", null);
	}

	public JSONObject delOrgById(String orgId) {

		String createRequestUrl = String.format(requestDelOrg, ACCESS_TOKEN,
				orgId);

		return httpRequest(createRequestUrl, "GET", null);
	}

	public SimpleJdbcTemplate getWebchatJdbcTemplate() {
		return webchatJdbcTemplate;
	}

	public void setWebchatJdbcTemplate(SimpleJdbcTemplate webchatJdbcTemplate) {
		this.webchatJdbcTemplate = webchatJdbcTemplate;
	}

	public SimpleJdbcTemplate getCedatadevJdbcOperations() {
		return cedatadevJdbcOperations;
	}
	
	
	public void setCedatadevJdbcOperations(
			SimpleJdbcTemplate cedatadevJdbcOperations) {
		this.cedatadevJdbcOperations = cedatadevJdbcOperations;
	}

	/*
	public void createOrgTree(String orgId, String parentId,
			Map<String, String> mapError) throws NamingException,
			OrganizationNotFoundException {

		try {

			Organization orgParent = ldapService.getOrganizationById(orgId,
					"weixin");

			JSONObject returnJson = createWeiXinOrg(orgParent.getName(),
					parentId);

			if (returnJson.getString("errcode").equals("0")) {

				logger.info(" -- sucess  errorcode : "
						+ returnJson.getString("errcode") + ", orgId: "
						+ orgParent.getId() + ", orgName: "
						+ orgParent.getName() + ", weixinId: "
						+ returnJson.getString("id"));

				webchatJdbcTemplate.update(INSER_SQL, orgId,
						returnJson.getString("id"));

				List<String> listMemeber = orgParent.getMember();
				if (listMemeber != null) {

					for (String member : listMemeber) {
						// CN=11060001,CN=上海总部,CN=Organizations,CN=Groups,CN=PartitionWWLDS1,DC=want-want,DC=com
						String childOrgId = member.split(",")[0].substring(
								"CN=".length(), member.split(",")[0].length())
								.trim();

						createOrgTree(childOrgId, returnJson.getString("id"),
								mapError);

					}

				}

			} else {

				mapError.put(orgId, parentId);

				logger.info(" -- faild  errorcode : "
						+ returnJson.getString("errcode") + ", orgId: "
						+ orgParent.getId() + ", orgName: "
						+ orgParent.getName() + ", weixinId: "
						+ returnJson.getString("id"));

				if (returnJson.getString("errmsg").indexOf(
						"api freq out of limit") != -1) {

					logger.info(" ~~ZZZ ~~~ 已超过微信限定频率, 开始睡眠 ~~ ");

					Thread.sleep(3600 * 1000);

					logger.info(" ~~ =_= ~~~ 线程睡醒开始重新试图 请求 微信平台 ~~ ");

				}

			}

		} catch (Exception e) {

			mapError.put(orgId, parentId);

			logger.error(e.toString());

		}
	}
	*/


	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();

		list.add("1005");

		list.add("99988");

		list.add("11");

		list.add("1007");

		Comparator<String> comparator = new Comparator<String>() {
			public int compare(String s1, String s2) {

				int o1Level = Integer.parseInt(s1);
				int o2Level = Integer.parseInt(s2);
				if (o1Level > o2Level)
					return -1;
				else if (o1Level == o2Level)
					return 0;
				else
					return 1;
			}
		};

		Collections.sort(list, comparator);

		System.out.println(list);
	}
}
