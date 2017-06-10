package com.want.batch.job.audit;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

/**
 * @description:稽核报表-进店品相报表定时任务
 * 
 * @author : Jacky
 * @date ：2011-3-9
 */
@Component
public class AuditJob extends AbstractWantJob {

	private static final String queryProdInfoSql = "select * from icustomer.audit_store_special_name b where b.task_date = to_date(?,'yyyy-mm-dd')";

	/*
	 * 更换新的 稽核特陈表数据 audit_store_special02_rpt
	 */
	private static final String queryProdIdSql = "select a.sid from icustomer.audit_store_special02_rpt a where a.task_date = to_date(?,'yyyy-mm-dd')";

	// private static final String updateProdIdSql =
	// "update icustomer.audit_store_special_rpt a set a.display_prod_cust= :kh ,a.display_prod_zy= :zy ,a.display_prod_yd= :yd,a.display_prod_zr= :zr  where a.sid = :SID and a.task_date = to_date('2011-03-23','yyyy-mm-dd') ";
	private static final String insertProdIdSql = "insert into ICUSTOMER.AUDIT_STORE_SPECIAL_PROD a (a.SID, a.TASK_DATE, a.PROD_NAME_KH, a.PROD_NAME_YD, a.PROD_NAME_ZY, a.PROD_NAME_ZR) values  (:SID,to_date(:dateStr,'yyyy-mm-dd'),:kh,:yd,:zy,:zr) ";

	private static final String KH = "kh"; // 客户
	private static final String ZY = "zy"; // 专员
	private static final String YD = "yd"; // 业代
	private static final String ZR = "zr"; // 主任
	private static String dateStr = "2013-01-24";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void execute() {
		long b = System.currentTimeMillis();
		dateStr = this.getYesterday();
		// Date date = new Date();
		// 1.查询所有指定时间内的品行信息稽核
		List<Map<String, Object>> prodInfoList = this.getDataMartJdbcOperations().queryForList(queryProdInfoSql, dateStr);

		// 2.查询所有需要更新的品行sid
		List<Map<String, Object>> prodIdList = this.getDataMartJdbcOperations().queryForList(queryProdIdSql, dateStr);
		Map[] maps = new HashMap[prodIdList.size()];// 设置参数值
		// 方便快速寻找定位sid 并将参数设置到Map里面去
		Map<Long, Map<String, Object>> map = new HashMap<Long, Map<String, Object>>();
		for (int i = 0; i < prodIdList.size(); i++) {
			Map<String, Object> _m = prodIdList.get(i);
			Long sid = ((BigDecimal) _m.get("sid")).longValue();
			// _m.put(DATE, dateStr);
			map.put(sid, _m);
			maps[i] = _m; // 设置到参数里面去
		}

		Map<Long, List<String>> mapKH = new HashMap<Long, List<String>>();
		Map<Long, List<String>> mapZY = new HashMap<Long, List<String>>();
		Map<Long, List<String>> mapYD = new HashMap<Long, List<String>>();
		Map<Long, List<String>> mapZR = new HashMap<Long, List<String>>();
		// 3.集合分成4个类型（客户，业代，专员，主任）的集合
		for (Map<String, Object> _m : prodInfoList) {
			String empType = (String) _m.get("emp_type");
			Long prodSid = ((BigDecimal) _m.get("prod_sid")).longValue();
			String prodName = (String) _m.get("prod_name");
			if (KH.equals(empType)) {
				this.setProdName(mapKH, prodSid, prodName);
			} else if (ZY.equals(empType)) {
				this.setProdName(mapZY, prodSid, prodName);
			} else if (YD.equals(empType)) {
				this.setProdName(mapYD, prodSid, prodName);
			} else if (ZR.equals(empType)) {
				this.setProdName(mapZR, prodSid, prodName);
			}
		}

		// 4 循环将品项值设置到sid Map 里面去
		for (Entry<Long, Map<String, Object>> e : map.entrySet()) {
			Long sid = e.getKey();
			if (mapKH.containsKey(sid)) {
				e.getValue().put(KH, StringUtils.join(mapKH.get(sid), ","));
			}
			if (mapZY.containsKey(sid)) {
				e.getValue().put(ZY, StringUtils.join(mapZY.get(sid), ","));
			}
			if (mapYD.containsKey(sid)) {
				e.getValue().put(YD, StringUtils.join(mapYD.get(sid), ","));
			}
			if (mapZR.containsKey(sid)) {
				e.getValue().put(ZR, StringUtils.join(mapZR.get(sid), ","));
			}
		}

		// 循环增加 没有key的
		for (Map m : maps) {
			m.put("dateStr", dateStr);
			if (!m.containsKey(KH)) {
				m.put(KH, "");
			}
			if (!m.containsKey(ZY)) {
				m.put(ZY, "");
			}
			if (!m.containsKey(YD)) {
				m.put(YD, "");
			}
			if (!m.containsKey(ZR)) {
				m.put(ZR, "");
			}

			m.put(KH, subStr1000(String.valueOf(m.get(KH))));
			m.put(ZY, subStr1000(String.valueOf(m.get(ZY))));
			m.put(YD, subStr1000(String.valueOf(m.get(YD))));
			m.put(ZR, subStr1000(String.valueOf(m.get(ZR))));
			// 将字符串都转化成Clob
			// m.put(KH, this.StringToClob(String.valueOf(m.get(KH))));
			// m.put(ZY, this.StringToClob(String.valueOf(m.get(ZY))));
			// m.put(YD, this.StringToClob(String.valueOf(m.get(YD))));
			// m.put(ZR, this.StringToClob(String.valueOf(m.get(ZR))));
		}
		// 5 批量插入
		this.getDataMartJdbcOperations().batchUpdate(insertProdIdSql, maps);
		this.logger.info(("完成了，耗时：" + (System.currentTimeMillis() - b)));
	}

	// 设置值到map里面去
	private void setProdName(Map<Long, List<String>> map, Long prodSid, String prodName) {
		if (map.containsKey(prodSid)) {
			map.get(prodSid).add(prodName);
		} else {
			List<String> _list = new ArrayList<String>();
			_list.add(prodName);
			map.put(prodSid, _list);
		}
	}

	// 获取昨天的日期
	private String getYesterday() {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(new java.util.Date());
		cal1.add(Calendar.DATE, -1);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(cal1.getTime());
	}

	public String subStr1000(String str) {
		if (str != null && str.length() >= 1000) {
			str = str.substring(0, 1000);
		}
		return str;
	}

	// 字符串转化成Clob
	public Clob StringToClob(String str) {
		Clob c = null;
		try {
			c = new SerialClob("abc ".toCharArray());
		} catch (SerialException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return c;
	}
}
