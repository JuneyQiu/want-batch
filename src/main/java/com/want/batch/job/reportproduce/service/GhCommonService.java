/**
 * 
 */
package com.want.batch.job.reportproduce.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.directbatch.dao.RouteInfoDao;
import com.want.batch.job.ghbatch.dao.TaskListDao;
import com.want.batch.job.reportproduce.dao.DisplayBadDao;
import com.want.batch.job.reportproduce.dao.DivsionDao;
import com.want.batch.job.reportproduce.dao.EmpInfoDao;
import com.want.batch.job.reportproduce.dao.ProdGroupDao;
import com.want.batch.job.reportproduce.dao.SpecialExhibitProductDao;
import com.want.batch.job.reportproduce.dao.SpecialSubstandardDao;
import com.want.batch.job.reportproduce.dao.StoreInfoDao;
import com.want.batch.job.reportproduce.dao.SubRouteStoreDao;
import com.want.batch.job.reportproduce.dao.SubrouteInfoDao;
import com.want.batch.job.reportproduce.dao.TaskStoreListDao;
import com.want.batch.job.reportproduce.dao.TaskStorePriceDao;
import com.want.batch.job.reportproduce.dao.TaskStoreSpecialExhibitDao;
import com.want.batch.job.reportproduce.dao.TaskStoreSubrouteDao;
import com.want.batch.job.reportproduce.pojo.Constant;
import com.want.batch.job.reportproduce.pojo.SpecialExhibitProduct;
import com.want.batch.job.reportproduce.pojo.TaskStoreSpecialExhibit;
import com.want.batch.job.utils.ProjectConfig;

/**
 * @author MandyZhang
 *
 */
@Component
public class GhCommonService {

	
	protected final Log logger = LogFactory.getLog(GhCommonService.class);
	
	@Autowired
	public TaskStoreSubrouteDao taskStoreSubrouteDao;
	
	@Autowired
	public TaskStoreListDao taskStoreListDao;
	
	@Autowired
	public EmpInfoDao empInfoDao;
	
	@Autowired
	public RouteInfoDao routeInfoDao;
	
	@Autowired
	public SubRouteStoreDao subRouteStoreDao;
	
	@Autowired
	public SubrouteInfoDao SubrouteInfoDao;
	
	@Autowired
	public StoreInfoDao storeInfoDao;
	
	@Autowired
	public TaskStoreSpecialExhibitDao taskStoreSpecialExhibitDao;
	
	@Autowired
	public SpecialExhibitProductDao specialExhibitProductDao;
	
//	@Autowired
//	private TaskStoreSpecialPictureDao taskStoreSpecialPictureDao;
	
	@Autowired
	public SpecialSubstandardDao specialSubstandardDao;
	
	@Autowired
	public DisplayBadDao displayBadDao;
	
	@Autowired
	public DivsionDao divsionDao;
	
	@Autowired
	public TaskStorePriceDao taskStorePriceDao;
	
	@Autowired
	public ProdGroupDao prodGroupDao;
	
	@Autowired
	public TaskListDao taskListDao;
	
	/**
	 * <pre>
	 * 2010-3-17 Deli
	 * 	查询终端线路
	 * </pre>
	 * 
	 * @param taskDetailId
	 *          行程编号
	 * @param flag
	 *          若为1查询未找到的,若为2查询未提交的
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getStoreSubrote(String taskDetailId, String flag) {
		
		try {
			// 查询终端线路清单
			Map<String, Object> storeSubrote = taskStoreSubrouteDao.getStoreSubrote(taskDetailId);
			
			// 总数
			int total = 0;

			// 未找到的
			int sum = 0;
			
			if (null != storeSubrote) {
			
				total = taskStoreListDao.getStoreCount((null == storeSubrote.get("SID")) ? null : storeSubrote
					.get("SID")
						.toString(),
					"");

				sum = taskStoreListDao.getStoreCount((null == storeSubrote.get("SID")) ? null : storeSubrote
					.get("SID")
						.toString(),
					flag);
				
				storeSubrote.put("COUNT", sum + "/" + total);

				Map<String, Object> saleName = empInfoDao.getSaleTpyeName((null == storeSubrote.get("EMP_ID")) ? null
						: storeSubrote.get("EMP_ID").toString());
				
				// 查询业代线别
				// 2010-06-13 Deli add saleName为null时的判断
				storeSubrote.put("SALE_NAME", (null == saleName) ? "" : saleName.get("SALE_TYPE_NAME"));
				
				// 2013-04-10 mirabele modify 补充县城休闲，县城乳饮
				// 2013-04-02 mirabelle modify 补充当大配送拆分为大配送休闲，大配送乳饮后的project_sid in (22,23)
				// 2013-02-27 mirabelle modify 强网休闲事业部16拆分为37,38,projectSids：14-->25,26;2013-03-06 14-->15,21,25,26
				// String projectSids = "4,10,12,14"; // 2010-06-04 Deli modify 添加4(县城)查询条件 
				//String projectSids = "4,10,12,1,2,3,6,9,25,26,15,21"; // 2010-08-19 Deli modify 添加1,2,3,6,9(大配送)查询条件
				// mandy modify 2013-04-16
				String projectSids = ProjectConfig.getInstance().getString("gh0101.customerType.projectSid");
				
				List<Map<String, Object>> empProjectNameList = routeInfoDao
					.findProjectNameByEmpId((null == storeSubrote.get("EMP_ID")) ? null : storeSubrote.get("EMP_ID").toString(),
						projectSids);
				
				List<String> projectNames = new ArrayList<String>();
				for (Map<String, Object> map : empProjectNameList) {
					projectNames.add(map.get("PROJECT_NAME").toString());
				}
				
				// 查询业代线别
				storeSubrote.put("SALE_NAME", StringUtils.join(projectNames, " + "));

				// 查询业代线路名称和拜访日期
				if (null != storeSubrote.get("SUBROUTE_SID")) {
					
					Map<String, Object> subrout = subRouteStoreDao.getSubRouteById(new Integer(storeSubrote
						.get("SUBROUTE_SID")
							.toString()));
					
					// 避免报表中格式化拜访日期,在此格式化
					if (null != subrout) {

						storeSubrote.put("subRouteName", subrout.get("SUBROUTE_NAME"));

						storeSubrote.put("visitDate", new SimpleDateFormat("yyyy-MM-dd").format(subrout.get("VISIT_DATE")));

						// 2010-05-27 Deli add SUBROUTE_ATT_SID(线路类型)栏位
						storeSubrote.put("attSid", subrout.get("SUBROUTE_ATT_SID"));
					}
					
					// Deli add 2010-06-04  根据线路SID关联查询projectSid
					Integer projectSid = SubrouteInfoDao.getProjectSidByRouteSid(new Integer(storeSubrote
						.get("SUBROUTE_SID")
						.toString()));
					
					storeSubrote.put("projectSid", projectSid);
					
					// end
				}
				
				return storeSubrote;
			}
			
			// 2010-11-26 Deli add 若为null，则提示信息
			else {
				
				logger.error("请确认是否有维护此业代与营业所关系");
			}
		} 
		catch (Exception e) {
			
			logger.error(Constant.generateExceptionMessage(e));
		}
		
		return null;
	}
	
	/**
	 * <pre>
	 * 2010-3-18 Deli
	 * 	查询终端负责人,电话和地址
	 * </pre>
	 * 
	 * @param taskDetailId
	 *          行程编号
	 * @param projectSid
	 * 					projectSid
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getStoreInfos(String taskDetailId, String projSid) {
		
		List<Map<String, Object>> storeInfos = taskStoreListDao.getStoreInfos(taskDetailId);

		List<Map<String, Object>> stores = new ArrayList<Map<String, Object>>();

		if (!storeInfos.isEmpty()) {
			
			for (Map<String, Object> map : storeInfos) {
				
				String storeId = (null == map.get("STORE_ID")) ? null : map.get("STORE_ID").toString();
				String subroutSid = (null == map.get("SUBROUTE_SID")) ? null : map.get("SUBROUTE_SID").toString();

				Map<String, Object> storeInfo = storeInfoDao.getStoreInfoByStoreId(storeId, subroutSid);
				/*
				 * modify by Wendy 2010-6-2: 因线路资料在制定完行程后会有异动的可能，所以会有查询不到资料的问题。 此处添加不为空的判断。
				 */
				if (storeInfo != null) {
					
					map.put("VISIT_ORDER", storeInfo.get("VISIT_ORDER"));
					map.put("STORE_OWNER", storeInfo.get("STORE_OWNER"));
					
					/*
					 * modify by Simonren 2010-7-16: 显示全部电话，在此组成显示字串。
					 */
					String phone1 = (String) storeInfo.get("PHONE1");
					String phone2 = (String) storeInfo.get("PHONE2");
					String storeMobile1 = (String) storeInfo.get("STORE_MOBILE1");
					String storeMobile2 = (String) storeInfo.get("STORE_MOBILE2");
					StringBuilder phoneStr = new StringBuilder();
					
					if (!StringUtils.equals("-", StringUtils.trim(phone1))) {
						
						phoneStr.append(phone1).append("<BR>");
					}
					
					if (!StringUtils.equals("-", StringUtils.trim(phone2))) {
											
						phoneStr.append(phone2).append("<BR>");
					}
					
					if (StringUtils.isNotBlank(storeMobile1)) {
						
						phoneStr.append(storeMobile1).append("<BR>");
					}
					
					if (StringUtils.isNotBlank(storeMobile2)) {
						
						phoneStr.append(storeMobile2);
					}
					
					String phone = phoneStr.toString();
					
					if (StringUtils.endsWith(phone, "<BR>")) {
						
						phone = StringUtils.removeEnd(phone, "<BR>");
					}
					
					map.put("PHONE", phone);
					map.put("ADDRESS", storeInfo.get("ADDRESS"));
					map.put("FORTH_LV_NAME", storeInfo.get("FORTH_LV_NAME"));
					map.put("THIRD_LV_NAME", storeInfo.get("THIRD_LV_NAME")); // Deli 2010-07-29 add
					
					// 2011-5-18 Deli add STORE_TYPE
					map.put("STORE_TYPE", storeInfo.get("STORE_TYPE"));
				}
				else if (storeInfo == null) {

					map.put("VISIT_ORDER", "99");
					map.put("ADDRESS", "此终端已移除");
				}
				
				String taskStoreListSid = (null != map.get("SID")) ? map.get("SID").toString() : null;
				
				// 2010-06-04 Deli add 若projSid为4,则查询该终端下的特陈数量,反之则分别查询每个事业部下特陈数量
				// 2011-04-11 Deli add 若projSid为1或4或17或18时,则查询该终端下的特陈数量,反之则分别查询每个事业部下特陈数量
				// 2013-04-20 mandy update
				String[] projSids = ProjectConfig.getInstance().getString("gh0101.prod.taskStore.projectSid").split(",");
				
				if (ArrayUtils.contains(projSids, projSid)) {
					
					List<Map<String, Object>> counts = taskStoreListDao.getSpecialCount(storeId, taskStoreListSid, "4");
					
					if ((null != counts) && !counts.isEmpty()) {
						
						map.put("COUNT", counts.get(0).get("COUNT"));
					}
				}
				else {
					
					// 查询终端对应区域的事业部;2013-02-27 mirabelle modify 强网休闲16拆分为37,38；故14-->15,21,25,26
//					String projectSids = "10, 12, 16, 25, 26,15,21"; // 2010-12-30 Deli modify 添加16(冰品)查询条件
					// mandy update 2013-04-20
					String projectSids = ProjectConfig.getInstance().getString("gh0101.prod.routeInfo.projectSid");
					
					List<Map<String, Object>> projectsSid = routeInfoDao.getProjectsByStoreId(storeId, projectSids);

					// add by Wendy：因目前稽核系统在处理时，对应的事业部为01：乳品，02：饮品，03：休闲，所以要把查询出来的渠道SID转换一下。
					for (Map<String, Object> projectSidMap : projectsSid) {

						String projectSid = projectSidMap.get("PROJECT_SID").toString();

						// 城区饮品
						if ("10".equals(projectSid)) {
							projectSidMap.put("CUSTOMER_TYPE", "02");
						}

						// 强网-乳品
						if ("12".equals(projectSid) || "27".equals(projectSid) || "28".equals(projectSid)) {
							projectSidMap.put("CUSTOMER_TYPE", "01");
						}

						// 强网-休闲 ;2013-02-27 mirabelle modify 14-->25,26;2013-03-06 14-->25,26,15,21
						// 2013-07-30 mandy add 29 -- >休闲点心
						if ("25".equals(projectSid) || "26".equals(projectSid) || "15".equals(projectSid) || "21".equals(projectSid) || "29".equals(projectSid)) {
							projectSidMap.put("CUSTOMER_TYPE", "03");
						}
						
						// 强网-冰品 2011-01-06 Deli add
						if ("16".equals(projectSid)) {
							projectSidMap.put("CUSTOMER_TYPE", "04");
						}
						
						// 2011-01-24 Deli add 标示终端所属这个事业部
						projectSidMap.put("STORE_DIVSION", "Y");
					}

					// 把转换过的事业部存入到[客户类型]里进行结果集的处理
					List<Map<String, Object>> types = projectsSid;
					
					List<Map<String, Object>> count = taskStoreListDao.getSpecialCount(storeId, taskStoreListSid, "");
					
					for (Map<String, Object> countMap : count) {

						String divsionSid = (null != countMap.get("DIVSION_SID")) ? countMap.get("DIVSION_SID").toString() : null;

						if ("15".equals(divsionSid) || "39".equals(divsionSid) || "40".equals(divsionSid)) {

							countMap.put("CUSTOMER_TYPE", "01");
						}

						else if ("17".equals(divsionSid)) {

							countMap.put("CUSTOMER_TYPE", "02");
						}
						// 2013-02-27 mirabelle modify 16-->37,38;2013-03-06 mirabelle modify 16-->34,36,37,38
						// 2013-07-30 mandy add 29 -- >休闲点心
						else if ("37".equals(divsionSid) || "38".equals(divsionSid) || "34".equals(divsionSid) || "36".equals(divsionSid) || "41".equals(divsionSid)) {

							countMap.put("CUSTOMER_TYPE", "03");
						}
						
						// 2010-12-30 deli add
						else if ("27".equals(divsionSid)) {
							
							countMap.put("CUSTOMER_TYPE", "04");
						}
						
						// 2010-10-12 deli add 作为是否在终端名称两侧显示特殊符号的标示
						map.put("HAS_SPECIAL", "1");
					}

					List<Map<String, Object>> customerTypes = new ArrayList<Map<String, Object>>();

					// 创建一个数组作为循环条件,使查询出的客户类型和业代路线长度够四位,便于前台显示
					// 将查询出的客户类型放到长度为四的list中
					for (String customerType : Arrays.asList("01", "02", "03", "04")) {

						boolean flag = true;

						for (int i = 0; i < types.size(); i++) {

							if (customerType.equals(types.get(i).get("CUSTOMER_TYPE"))) {

								flag = false;

								customerTypes.add(types.get(i));

								break;
							}

							// 若不符合01,02,03,或04,则新增一笔空的map
							if ((i == types.size() - 1) && flag) {

								Map<String, Object> emptyeMap = new HashMap<String, Object>();
								emptyeMap.put("CUSTOMER_TYPE", customerType);

								customerTypes.add(emptyeMap);
							}
						}

						// 如果查询到的事业部为空，则新增一笔空的map
						if (types.size() == 0) {

							Map<String, Object> emptyeMap = new HashMap<String, Object>();
							emptyeMap.put("CUSTOMER_TYPE", customerType);

							customerTypes.add(emptyeMap);
						}
					}					

					// map.put("CUSTOMER_TYPE", this.getCustomerCustomertypeDao().getCustomerTypesByCustomerId((null == storeInfo
					// .get("FORWARDER_ID")) ? null : storeInfo.get("FORWARDER_ID").toString()));

					List<Map<String, Object>> customerSpecialTypes = new ArrayList<Map<String, Object>>();
					
					// 将业代路线合并到客户类型list中
					for (Map<String, Object> map2 : customerTypes) {

						for (Map<String, Object> counMap : count) {

							// 2010-06-05 Deli modiy 添加"CUSTOMER_TYPE"的非空判断
							// 2010-12-30 Deli add 若为27，即强网冰品时添加其数量
							if ((null != map2.get("CUSTOMER_TYPE")) 
									&& (null != counMap.get("CUSTOMER_TYPE"))
									&& map2.get("CUSTOMER_TYPE").toString().equals(counMap.get("CUSTOMER_TYPE").toString())) {

								map2.put("SPECIAL_COUNT", counMap.get("COUNT"));
							}
						}

						customerSpecialTypes.add(map2);
					}

					map.put("TYPE_SPECIAL", customerSpecialTypes);
				}
				
				stores.add(map);
			}
		}
		
		// 依拜访顺序排序
		Map<Integer, Map<String, Object>> results = new TreeMap<Integer, Map<String, Object>>();

		int index = 0;
		int indexPadSize = String.valueOf(stores.size()).length();
		int orderPadSize = 5;

		for (Map<String, Object> store : stores) {

			int compareOrder = 0;
			if (store.get("VISIT_ORDER") != null) {

				compareOrder = Integer.valueOf(StringUtils.leftPad(store.get("VISIT_ORDER").toString(), orderPadSize, "0")
						+ StringUtils.leftPad(String.valueOf(index++), indexPadSize, "0"));
			}

			this.logger
				.debug(String.format("Compare order is [%s] & order is [%s] ", compareOrder, store.get("VISIT_ORDER")));

			results.put(compareOrder, store);
		}

		this.logger.debug("Store  size >>> " + stores.size());
		this.logger.debug("Result size >>> " + results.size());

		List<Integer> visitOrders = new ArrayList<Integer>(results.keySet());
		Collections.sort(visitOrders);

		List<Map<String, Object>> resultStores = new ArrayList<Map<String, Object>>();

		for (Integer visitOrder : visitOrders) {
			resultStores.add(results.get(visitOrder));
		}

		return resultStores;
	}
	
	/**
	 * <pre>
	 * 2010-3-20 Deli
	 * 	查询稽核终端特陈
	 * </pre>
	 * 
	 * @param taskStoreListId
	 *          终端清单表SID
	 * @return
	 */
	public List<Map<String, Object>> getSpecialExhibit(String taskStoreListId) {
		
		// 根据终端清单表SID查询行程类别-终端特陈
		List<TaskStoreSpecialExhibit> specials = taskStoreSpecialExhibitDao
				.findTaskStoreSpecialExhibit(new Integer(taskStoreListId));

		List<Map<String, Object>> specialExhibit = new ArrayList<Map<String, Object>>();
		
		for (TaskStoreSpecialExhibit taskStoreSpecialExhibit : specials) {
			
			Map<String, Object> map = new HashMap<String, Object>();

			// 根据特陈编号查询品项清单
			List<SpecialExhibitProduct> products = specialExhibitProductDao
					.findSpecialExhibitProduct(taskStoreSpecialExhibit.getId());

			// 查看图片id
//			List<TaskStoreSpecialPicture> pictureIds = taskStoreSpecialPictureDao
//					.findReferencePictures(taskStoreSpecialExhibit.getId());
//
//			StringBuilder ids = new StringBuilder();
//
//			for (TaskStoreSpecialPicture taskStoreSpecialPicture : pictureIds) {
//
//				ids.append(taskStoreSpecialPicture.getTaskPictureSid());
//				ids.append(",");
//			}
//
//			// 去掉最后的","
//			if (ids.length() > 0) {
//
//				ids.deleteCharAt(ids.length() - 1);
//			}
			
			/*
			 *  2010-11-15 Deli add 
			 *  1.查询特陈面数
			 *  2.查询特陈面数结果
			 *  3.查询特陈不达标内容
			 */
			if (StringUtils.isNotEmpty(taskStoreSpecialExhibit.getDisplaySideCount())) {
				
				taskStoreSpecialExhibit.setDisplaySideCount(specialSubstandardDao
					.getDisplayPramName(taskStoreSpecialExhibit.getDisplayTypeName(), taskStoreSpecialExhibit.getDisplaySideCount()));
			}
			
			if (StringUtils.isNotEmpty(taskStoreSpecialExhibit.getDisplaySideCountResult())) {
				
				taskStoreSpecialExhibit.setDisplaySideCountResult(specialSubstandardDao
					.getDisplayPramName(taskStoreSpecialExhibit.getDisplayTypeNameResult(), taskStoreSpecialExhibit.getDisplaySideCountResult()));
			}
			
			if (StringUtils.isNotEmpty(taskStoreSpecialExhibit.getDisplayParamId())) {
				
				taskStoreSpecialExhibit.setDisplayParamId(specialSubstandardDao
					.getDisplayPramName(taskStoreSpecialExhibit.getDisplayTypeName(), taskStoreSpecialExhibit.getDisplayParamId()));
			}
			
			if (StringUtils.isNotEmpty(taskStoreSpecialExhibit.getDisplayParamIdResult())) {
				
				taskStoreSpecialExhibit.setDisplayParamIdResult(specialSubstandardDao
					.getDisplayPramName(taskStoreSpecialExhibit.getDisplayTypeNameResult(), taskStoreSpecialExhibit.getDisplayParamIdResult()));
			}
			
			map.put("substandard", specialSubstandardDao.getSustandardsBySpecialId(taskStoreSpecialExhibit.getId()));
			
			// 2013-3-29 add mandy 查询陈列差
			map.put("displaybad", displayBadDao.getDisplayBadBySpecialId(taskStoreSpecialExhibit.getId()));

			// 将终端特陈,客户类型,品项清单封装到一个map中
			map.put("special", taskStoreSpecialExhibit);
			
			map.put("type", divsionDao.findById(taskStoreSpecialExhibit.getDivsionSid()));
			map.put("products", products);
//			map.put("ids", ids);
			
			specialExhibit.add(map);
		}
		
		return specialExhibit;
	}
	
	/**
	 * <pre>
	 * 2010-3-18 Deli
	 * 	稽核终端零售价清单
	 * </pre>
	 * 
	 * @param taskStoreListId
	 * @return
	 */
	public List<Map<String, Object>> getTaskStoreList(String taskStoreListId) {

		List<Map<String, Object>> storePriceList = taskStorePriceDao.getTaskStoreList(taskStoreListId);

		for (Map<String, Object> storePriceListMap : storePriceList) {

			storePriceListMap.put("CUSTOMER_TYPE", prodGroupDao.fingProdGroupById(storePriceListMap.get("PROD_ID").toString()));
		}

		return storePriceList;
	}
	
	/**
	 * <pre>
	 * 2010-3-29 Ryan
	 * 	查询任务表findById
	 * </pre>
	 * 
	 * @param taskListId
	 *          任务编号
	 * @return TaskList
	 */
	public List<Map<String, Object>> findTaskListById(int taskListId) {

		return taskListDao.findTaskListById(taskListId);
	}
}
