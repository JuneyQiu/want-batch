
// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.ghbatch.dao.SdActualDisplayDao;
import com.want.batch.job.ghbatch.dao.SdActualProdDao;
import com.want.batch.job.ghbatch.dao.GhSpecialExhibitProductDao;
import com.want.batch.job.ghbatch.dao.SpecialExhibitProductLogDao;
import com.want.batch.job.ghbatch.dao.GhTaskStoreSpecialExhibitDao;
import com.want.batch.job.ghbatch.dao.TaskStoreSpecialExhibitLogDao;
// ~ Comments
// ==================================================
@Component
public class GhTchJob extends AbstractWantJob {

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================
	
	@Autowired
	private SdActualDisplayDao sdActualDisplayDao;
	
	@Autowired
	private GhTaskStoreSpecialExhibitDao taskStoreSpecialExhibitDao;
	
	@Autowired
	private TaskStoreSpecialExhibitLogDao taskStoreSpecialExhibitLogDao;
	
	@Autowired
	private GhSpecialExhibitProductDao specialExhibitProductDao;
	
	@Autowired
	private SpecialExhibitProductLogDao specialExhibitProductLogDao;
	
	@Autowired
	private SdActualProdDao sdActualProdDao;

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	@Override
	public void execute() throws Exception {

		//	取得当前系统日期
		Calendar nowCal = Calendar.getInstance();
		String taskDate = "" + nowCal.get(Calendar.YEAR) 
			+ (nowCal.get(Calendar.MONTH) < 9 ? ("0" + (nowCal.get(Calendar.MONTH) + 1)) : (nowCal.get(Calendar.MONTH) + 1)) 
			+ (nowCal.get(Calendar.DATE) < 10 ? "0" + nowCal.get(Calendar.DATE) : nowCal.get(Calendar.DATE));
		
		// 取得稽核特陈的资料rs1，按照终端id，Store_display_sid排序
		List<Map<String, Object>> jhStoreDisplay = this.getTaskStoreSpecialExhibitDao().getJhStoreDisplayInfo(taskDate);
		
		// 循环遍历rs1取得终端id，每1000个终端一查
		// 用于存放终端去除重复
		Set<String> storeIdSet = new HashSet<String>();
		
		for (int i = 0; i < jhStoreDisplay.size(); i++) {
			
			storeIdSet.add((String)jhStoreDisplay.get(i).get("STORE_ID"));
		}
		
		// 将set转成list
		List<String> storeList = new ArrayList<String>(storeIdSet);
		
		int storeNumber = (int)Math.ceil(new BigDecimal(storeIdSet.toArray().length).divide(new BigDecimal(1000)).doubleValue());
		
		// 用于存储业代填写的特陈资料
		List<Map<String, Object>> ydDisplay = new ArrayList<Map<String,Object>>();
		
		StringBuilder storeIds = new StringBuilder();
		
		for (int i = 0; i < storeNumber; i++) {
			
			List<String> subList = null;
			
			// 如果特陈终端的个数大于1千，则每一个终端进行查询
			if (i != storeNumber - 1) {
				
				subList = storeList.subList(i * 1000, (i + 1) * 1000);
			}
			else {
				
				subList = storeList.subList(i * 1000, storeList.size());
			}
			
			// 根据终端id查询特陈系统的业代填写的特陈资料
			storeIds.append("'" + StringUtils.join(subList, "','") + "'");
			
			// 依据终端id取得特陈的资料rs2，按照终端id，Store_display_sid排序
			List<Map<String, Object>> everyYdDisplay = this.getSdActualDisplayDao().getYdDisplay(taskDate.substring(0,6), storeIds.toString());
			
			ydDisplay.addAll(everyYdDisplay);
			storeIds = new StringBuilder();
		}
		
		// 记录有陈列变为未陈列的sid值
		List<String> delList = new ArrayList<String>();
		
		// 记录未陈列改为有陈列的sid值
		List<Map<String, String>> addList = new ArrayList<Map<String, String>>();
		
		// 用于记录批量新增的稽核终端特陈信息
		List<Object[]> addStoreArgs = new ArrayList<Object[]>();
		
		/*
		 *  循环遍历rs2，在rs1中查找相同终端，相同Store_display_sid下的特陈位置是否有变动
		 *  将有陈列改为未陈列的sid记下，放入list1中，需要删除资料
		 *  将未陈列改为有陈列的终端记下，放入list2中，需要新增资料
		 */
		for (int i = 0; i < ydDisplay.size(); i++) {
			
			String ydStoreId = (String)ydDisplay.get(i).get("STORE_ID");
			String ydLocationSid = String.valueOf(ydDisplay.get(i).get("LOCATION_TYPE_SID"));
			String ydStoreDisplaySid = String.valueOf(ydDisplay.get(i).get("STORE_DISPLAY_SID"));
			// 用于记录终端的taskStoreListSid
//			String taskStoreListSid = "";
			
			// 用于记录是否有对应的稽核资料，>=1表示有稽核特陈，0表示有稽核终端，-1为初始值
//			int num = -1;
			
			for (int k = 0; k < jhStoreDisplay.size(); k++) {
				
				Map<String, Object> jhStoreDisplayMap = jhStoreDisplay.get(k);
				String jhStoreId = (String)jhStoreDisplayMap.get("STORE_ID");
				String jhStoreDisplaySid = String.valueOf(jhStoreDisplayMap.get("STORE_DISPLAY_SID"));
				
				if (jhStoreId.equals(ydStoreId)) {
					
//					num = 0;
//					taskStoreListSid = String.valueOf(jhStoreDisplayMap.get("TASK_STORE_LIST_SID"));
					
					// 有稽核特陈资料，业代填写的是4，删除
					if (jhStoreDisplaySid.equals(ydStoreDisplaySid) ) {
						
//						num++;
						
						if ("4".equals(ydLocationSid)) {
							
							delList.add(String.valueOf(jhStoreDisplayMap.get("SID")));
						}
						
						break;
					}
					// 无稽核特陈资料，业代填写的不是4，新增
//					else if (StringUtils.isEmpty(jhStoreDisplaySid) && !"4".equals(ydLocationSid)) {
//						
//						int sid = this.getTaskStoreSpecialExhibitDao().getSpecialDisplaySid();
//					
//						Map<String, String> addMap = new HashMap<String, String>();
//						addMap.put("SID", String.valueOf(sid));
//						addMap.put("ACTUAL_DISPLAY_SID", String.valueOf(ydDisplay.get(i).get("ACTUAL_DISPLAY_SID")));
//						addList.add(addMap);
//						
//						Object[] obj = new Object[16];
//						obj[0] = sid;
//						obj[1] = jhStoreDisplayMap.get("TASK_STORE_LIST_SID");
//						obj[2] = ydDisplay.get(i).get("ID");
//						obj[3] = ydDisplay.get(i).get("NAME");
//						obj[4] = ydDisplay.get(i).get("LOCATION_TYPE_SID");
//						obj[5] = ydDisplay.get(i).get("DISPLAY_TYPE_SID");
//						obj[6] = ydDisplay.get(i).get("DISPLAY_ACREAGE");
//						obj[7] = ydDisplay.get(i).get("DISPLAY_SIDECOUNT");
//						obj[8] = "sys";
//						obj[9] = new Date();
//						obj[10] = ydDisplay.get(i).get("DIVISION_SID");
//						obj[11] = ydDisplay.get(i).get("STORE_DISPLAY_SID");
//						obj[12] = ydDisplay.get(i).get("DISPLAY_PARAM_ID");
//						obj[13] = ydDisplay.get(i).get("SALES_INPUT_DATETIME");
//						obj[14] = ydDisplay.get(i).get("SPECIAL_POLICY_SID");
//						obj[15] = ydDisplay.get(i).get("ASSETS_ID");
//						addStoreArgs.add(obj);
//						num++;
//						break;
//					}
				}
			}
		
			// 如果num=0，说明有稽核终端，没有稽核特陈，需要新增
//			if (num == 0 && !"4".equals(ydLocationSid)) {
//				
//				int sid = this.getTaskStoreSpecialExhibitDao().getSpecialDisplaySid();
//				
//				Map<String, String> addMap = new HashMap<String, String>();
//				addMap.put("SID", String.valueOf(sid));
//				addMap.put("ACTUAL_DISPLAY_SID", String.valueOf(ydDisplay.get(i).get("ACTUAL_DISPLAY_SID")));
//				addList.add(addMap);
//				
//				Object[] obj = new Object[16];
//				obj[0] = sid;
//				obj[1] = taskStoreListSid;
//				obj[2] = ydDisplay.get(i).get("ID");
//				obj[3] = ydDisplay.get(i).get("NAME");
//				obj[4] = ydDisplay.get(i).get("LOCATION_TYPE_SID");
//				obj[5] = ydDisplay.get(i).get("DISPLAY_TYPE_SID");
//				obj[6] = ydDisplay.get(i).get("DISPLAY_ACREAGE");
//				obj[7] = ydDisplay.get(i).get("DISPLAY_SIDECOUNT");
//				obj[8] = "sys";
//				obj[9] = new Date();
//				obj[10] = ydDisplay.get(i).get("DIVISION_SID");
//				obj[11] = ydDisplay.get(i).get("STORE_DISPLAY_SID");
//				obj[12] = ydDisplay.get(i).get("DISPLAY_PARAM_ID");
//				obj[13] = ydDisplay.get(i).get("SALES_INPUT_DATETIME");
//				obj[14] = ydDisplay.get(i).get("SPECIAL_POLICY_SID");
//				obj[15] = ydDisplay.get(i).get("ASSETS_ID");
//				addStoreArgs.add(obj);
//			}
		}
		
		// 计算终端每1000笔分组的个数
		int delSize = (int)Math.ceil(new BigDecimal(delList.size()).divide(new BigDecimal(1000)).doubleValue());

		StringBuilder sid = new StringBuilder(); 
		
		for (int i = 0; i < delSize; i++) {
			
			List<String> subList = null;
			
			// 如果特陈终端的个数大于1千，则每一个终端进行查询
			if (i != storeNumber - 1) {
				
				subList = delList.subList(i * 1000, (i + 1) * 1000);
			}
			else {
				
				subList = delList.subList(i * 1000, delList.size());
			}
			
			// 根据终端id查询特陈系统的业代填写的特陈资料
			sid.append(StringUtils.join(subList, ","));
			
			// 移转稽核特陈资料
			this.getTasStoreSpecialExhibitLogDao().addSpecialLog(sid.toString());
			
			this.getTaskStoreSpecialExhibitDao().deleteSpecialDisplaySid(sid.toString());
			
			// 移转品项资料
			this.getSpecialExhibitProductLogDao().addSpecialProdLog(sid.toString());
			
			this.getSpecialExhibitProductDao().deleteSpecialProductBySid(sid.toString());
			
			sid = new StringBuilder(); 
		}
		
		
		// 根据list2的中的资料，查询特陈信息，新增至表TASK_STORE_SPECIAL_EXHIBIT,SPECIALEXHIBIT_PRODUCT中新增资料
		
//		List<String> addStoreDispalySids = new ArrayList<String>();
//		
//		// 取得addlist中的终端，去特陈查询
//		for (int i = 0; i < addList.size(); i++) {
//			
//			addStoreDispalySids.add(addList.get(i).get("ACTUAL_DISPLAY_SID"));
//		}
//		
//		int addSize = (int)Math.ceil(new BigDecimal(addList.size()).divide(new BigDecimal(1000)).doubleValue());
//
//		StringBuilder addActualDisplaySid = new StringBuilder(); 
//		List<Map<String, Object>> addProdInfo = new ArrayList<Map<String,Object>>();
//		
//		for (int i = 0; i < addSize; i++) {
//			
//			List<String> subList = null;
//			
//			// 如果特陈终端的个数大于1千，则每一个终端进行查询
//			if (i != addSize - 1) {
//				
//				subList = addStoreDispalySids.subList(i * 1000, (i + 1) * 1000);
//			}
//			else {
//				
//				subList = addStoreDispalySids.subList(i * 1000, addStoreDispalySids.size());
//			}
//			
//			addActualDisplaySid.append("'" + StringUtils.join(subList, "','") + "'");
//			
//			List<Map<String, Object>> everyAddProd = this.getSdActualProdDao().getSpecialProdByActualDisplaySid(addActualDisplaySid.toString());
//			addProdInfo.addAll(everyAddProd);
//			addActualDisplaySid = new StringBuilder(); 
//		}
//		
//		List<Object[]> addProdArgs = new ArrayList<Object[]>();
//		
//		// 循环遍历品项，将task_store_special_exhibit_sid放入
//		for (int i = 0; i < addList.size(); i++) {
//			
//			for (int j = 0; j < addProdInfo.size(); j++) {
//
//				if ((String.valueOf(addList.get(i).get("ACTUAL_DISPLAY_SID"))).equals(String.valueOf(addProdInfo.get(j).get("ACTUAL_DISPLAY_SID")))) {
//					
//					Object[] obj = new Object[3];
//					obj[0] = addList.get(i).get("SID"); 
//					obj[1] = addProdInfo.get(j).get("prodId");
//					obj[2] = addProdInfo.get(j).get("prodName");
//					addProdArgs.add(obj);
//				}
//			}
//		}
//
//		// 新增稽核特陈资料
//		this.getTaskStoreSpecialExhibitDao().addSpecialDisplay(addStoreArgs);
//		
//		// 新增稽核特陈品项
//		this.getSpecialExhibitProductDao().addSpecialProduct(addProdArgs);
		
	}

	/**
	 * @return 传回 sdActualDisplayDao。
	 */
	public SdActualDisplayDao getSdActualDisplayDao() {

		return sdActualDisplayDao;
	}

	/**
	 * @param sdActualDisplayDao
	 *          要设定的 sdActualDisplayDao。
	 */
	public void setSdActualDisplayDao(SdActualDisplayDao sdActualDisplayDao) {

		this.sdActualDisplayDao = sdActualDisplayDao;
	}

	/**
	 * @return 传回 tasStoreSpecialExhibitDao。
	 */
	public GhTaskStoreSpecialExhibitDao getTaskStoreSpecialExhibitDao() {

		return taskStoreSpecialExhibitDao;
	}

	/**
	 * @param tasStoreSpecialExhibitDao
	 *          要设定的 tasStoreSpecialExhibitDao。
	 */
	public void setTasStoreSpecialExhibitDao(GhTaskStoreSpecialExhibitDao taskStoreSpecialExhibitDao) {

		this.taskStoreSpecialExhibitDao = taskStoreSpecialExhibitDao;
	}

	/**
	 * @return 传回 tasStoreSpecialExhibitLogDao。
	 */
	public TaskStoreSpecialExhibitLogDao getTasStoreSpecialExhibitLogDao() {

		return taskStoreSpecialExhibitLogDao;
	}

	/**
	 * @param tasStoreSpecialExhibitLogDao
	 *          要设定的 tasStoreSpecialExhibitLogDao。
	 */
	public void setTasStoreSpecialExhibitLogDao(TaskStoreSpecialExhibitLogDao tasStoreSpecialExhibitLogDao) {

		this.taskStoreSpecialExhibitLogDao = tasStoreSpecialExhibitLogDao;
	}

	/**
	 * @return 传回 specialExhibitProductDao。
	 */
	public GhSpecialExhibitProductDao getSpecialExhibitProductDao() {

		return specialExhibitProductDao;
	}

	/**
	 * @param specialExhibitProductDao
	 *          要设定的 specialExhibitProductDao。
	 */
	public void setSpecialExhibitProductDao(GhSpecialExhibitProductDao specialExhibitProductDao) {

		this.specialExhibitProductDao = specialExhibitProductDao;
	}

	/**
	 * @return 传回 specialExhibitProductLogDao。
	 */
	public SpecialExhibitProductLogDao getSpecialExhibitProductLogDao() {

		return specialExhibitProductLogDao;
	}

	/**
	 * @param specialExhibitProductLogDao
	 *          要设定的 specialExhibitProductLogDao。
	 */
	public void setSpecialExhibitProductLogDao(SpecialExhibitProductLogDao specialExhibitProductLogDao) {

		this.specialExhibitProductLogDao = specialExhibitProductLogDao;
	}

	/**
	 * @return 传回 sdActualProdDao。
	 */
	public SdActualProdDao getSdActualProdDao() {

		return sdActualProdDao;
	}

	/**
	 * @param sdActualProdDao
	 *          要设定的 sdActualProdDao。
	 */
	public void setSdActualProdDao(SdActualProdDao sdActualProdDao) {

		this.sdActualProdDao = sdActualProdDao;
	}

}
