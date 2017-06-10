package com.want.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IMetaData;
import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.Function; 

@Component
public class SapDAO {

	private static final Log logger = LogFactory.getLog(SapDAO.class);

	@Autowired
	public SapDataSource sapDataSource;

	public SapDAO() {
	}

	/**
	 * 提供远程呼叫SAP BAPI 程序功能, 只需要传入远程功能名称,表名称,查询条件 ,会将查询到的结果以数组方式回传. 查询条件用 HashMap
	 * 传入, 以 key-value 方式传入, 条件必须匹配对应 远程功能 (SAP FUNCTION) 的条件.
	 * 
	 * @param function_name
	 *            String , RFC 功能名称
	 * @param result_table_name
	 *            String, 结果集（表）名称
	 * @param query_hm
	 *            HashMap, 查询条件
	 * @return ArrayList, 返回数组
	 */
	public ArrayList getSAPData(String function_name, String result_table_name,
			HashMap query_hm) {

		ArrayList resultList = new ArrayList();
		JCO.Function function = null;
		JCO.Table DATA = null;
		JCO.ParameterList input = null;

		JCO.Client mConnection = null;
		JCO.Repository mRepository = null;

		try {
			mConnection = sapDataSource.createConnection();
			mRepository = new JCO.Repository("WWBAPI", mConnection);
			logger.info("SAP连接 " + mConnection.getASHost() + " 成功");
			function = this.createFunction(mRepository,
					function_name.toUpperCase());
			if (function == null) {
				System.out.println("Function:" + function_name.toUpperCase()
						+ " not found in SAP.");
				throw new Exception("Function:" + function_name.toUpperCase()
						+ " not found in SAP.");
			}

			DATA = function.getTableParameterList().getTable(
					result_table_name.toUpperCase());
			input = function.getImportParameterList();
			Iterator key_it = query_hm.keySet().iterator();
			while (key_it.hasNext()) {
				String key = (String) key_it.next();
				input.setValue(query_hm.get(key), key);
			}

			mConnection.execute(function);
			IMetaData imd = DATA.getMetaData();
			String fields[] = new String[imd.getFieldCount()];
			for (int j = 0; j < imd.getFieldCount(); j++) {
				fields[j] = imd.getName(j);
			}

			for (int i = 0; i < DATA.getNumRows(); i++) {
				DATA.setRow(i);
				HashMap datahm = new HashMap(imd.getFieldCount());
				for (int z = 0; z < fields.length; z++) {
					datahm.put(fields[z], DATA.getString(fields[z]));
				}
				resultList.add(datahm);
			}
		} catch (Exception ex) {
			logger.error("call sap error >>> ", ex);
			resultList = null;
		} finally {
			mRepository = null;
			sapDataSource.close(mConnection);

		}
		return resultList;
	}

	/**
	 * 提供远程呼叫SAP BAPI 程序功能, 只需要传入远程功能名称,表名称,查询条件 ,会将查询到的结果以数组方式回传. 查询条件用 HashMap
	 * 传入, 以 key-value 方式传入, 条件必须匹配对应 远程功能 (SAP FUNCTION) 的条件. qlist
	 * 的第一位是字段名称（index=0）,2第二位开始才是数值(index>=1)
	 * 
	 * @param function_name
	 *            String , RFC 功能名称
	 * @param result_table_name
	 *            String, 结果集（表）名称
	 * @param query_hm
	 *            HashMap, 查询条件
	 * @return ArrayList, 返回数组
	 */
	public ArrayList getSAPData2(String function_name,
			String result_table_name, HashMap query_hm) {

		ArrayList resultList = new ArrayList();
		JCO.Function function = null;
		JCO.Table DATA = null;
		JCO.Table codes = null;
		JCO.ParameterList input = null;
		JCO.Client mConnection = null;
		JCO.Repository mRepository = null;

		try {
			mConnection = sapDataSource.createConnection();
			mRepository = new JCO.Repository("WWBAPI", mConnection);
			logger.info("SAP连接 " + mConnection.getASHost() + " 成功");

			function = this.createFunction(mRepository,
					function_name.toUpperCase());
			if (function == null) {
				System.out.println("Function:" + function_name.toUpperCase()
						+ " not found in SAP.");
				throw new Exception("Function:" + function_name.toUpperCase()
						+ " not found in SAP.");
			}
			String table_name = "";
			Iterator it = query_hm.keySet().iterator();
			while (it.hasNext()) {
				table_name = (String) it.next();
				if (table_name.indexOf("L_") != -1) {
					codes = function.getTableParameterList().getTable(
							table_name.substring(2).toUpperCase());
					ArrayList qlist = (ArrayList) query_hm.get(table_name);
					// qlist 的第一位是字段名称（index=0）,2第二位开始才是数值(index>=1)
					if (qlist != null && qlist.size() > 0) {
						codes.appendRows(qlist.size() - 1);
						for (int x = 1; x < qlist.size(); x++) {
							codes.setRow(x - 1);
							codes.setValue((String) qlist.get(x),
									(String) qlist.get(0));
						}
					}
					query_hm.remove(table_name);
					break;
				}
			}

			DATA = function.getTableParameterList().getTable(
					result_table_name.toUpperCase());
			input = function.getImportParameterList();
			Iterator key_it = query_hm.keySet().iterator();
			while (key_it.hasNext()) {
				String key = (String) key_it.next();
				if (key.indexOf("L_") == -1)
					input.setValue(query_hm.get(key), key);
			}

			mConnection.execute(function);
			IMetaData imd = DATA.getMetaData();
			String fields[] = new String[imd.getFieldCount()];
			for (int j = 0; j < imd.getFieldCount(); j++) {
				fields[j] = imd.getName(j);
			}

			for (int i = 0; i < DATA.getNumRows(); i++) {
				DATA.setRow(i);
				HashMap datahm = new HashMap(imd.getFieldCount());
				for (int z = 0; z < fields.length; z++) {
					datahm.put(fields[z], DATA.getString(fields[z]));
				}
				resultList.add(datahm);
			}
			DATA.clear();
		} catch (Exception ex) {
			logger.error("call sap error >>> ", ex);
			resultList = null;
		} finally {
			mRepository = null;
			sapDataSource.close(mConnection);
		}
		return resultList;
	}

	/**
	 * 提供远程呼叫SAP BAPI 程序功能, 只需要传入远程功能名称,表名称 ,会将结果以数组方式回传. 查询条件用 HashMap 传入, 以
	 * key-value 方式传入, 条件必须匹配对应 远程功能 (SAP FUNCTION) 的条件.
	 * 
	 * @param function_name
	 *            String , RFC 功能名称
	 * @param result_table_name
	 *            String, 结果集（表）名称
	 * @return ArrayList, 返回数组
	 */
	public ArrayList getSAPData3(String function_name, String result_table_name) {

		ArrayList resultList = new ArrayList();
		JCO.Function function = null;
		JCO.Table DATA = null;
		JCO.ParameterList input = null;
		JCO.Client mConnection = null;
		JCO.Repository mRepository = null;

		try {
			mConnection = sapDataSource.createConnection();
			mRepository = new JCO.Repository("WWBAPI", mConnection);
			logger.info("SAP连接 " + mConnection.getASHost() + " 成功");

			function = this.createFunction(mRepository,
					function_name.toUpperCase());
			if (function == null) {
				System.out.println("Function:" + function_name.toUpperCase()
						+ " not found in SAP.");
				throw new Exception("Function:" + function_name.toUpperCase()
						+ " not found in SAP.");
			}

			DATA = function.getTableParameterList().getTable(
					result_table_name.toUpperCase());
			mConnection.execute(function);
			IMetaData imd = DATA.getMetaData();
			String fields[] = new String[imd.getFieldCount()];
			for (int j = 0; j < imd.getFieldCount(); j++) {
				fields[j] = imd.getName(j);
			}

			for (int i = 0; i < DATA.getNumRows(); i++) {
				DATA.setRow(i);
				HashMap datahm = new HashMap(imd.getFieldCount());
				for (int z = 0; z < fields.length; z++) {
					datahm.put(fields[z], DATA.getString(fields[z]));
				}
				resultList.add(datahm);
			}
		} catch (Exception ex) {
			logger.error("call sap error >>> ", ex);
			resultList = null;
		} finally {
			mRepository = null;
			sapDataSource.close(mConnection);
		}
		return resultList;
	}

	/**
	 * 提供远程呼叫SAP BAPI 程序功能, 只需要传入远程功能名称,表名称,查询条件 ,会将查询到的结果以数组方式回传. 查询条件用 HashMap
	 * 传入, 以 key-value 方式传入, 条件必须匹配对应 远程功能 (SAP FUNCTION) 的条件. qlist
	 * 的每一项是一个HashMap
	 * 
	 * @param function_name
	 *            String , RFC 功能名称
	 * @param result_table_name
	 *            String, 结果集（表）名称
	 * @param query_hm
	 *            HashMap, 查询条件
	 * @return ArrayList, 返回数组
	 */
	public ArrayList getSAPData4(String function_name,
			String result_table_name, HashMap query_hm) {

		ArrayList resultList = new ArrayList();
		JCO.Function function = null;
		JCO.Table DATA = null;
		JCO.Table codes = null;
		JCO.ParameterList input = null;
		JCO.Client mConnection = null;
		JCO.Repository mRepository = null;

		try {
			mConnection = sapDataSource.createConnection();
			mRepository = new JCO.Repository("WWBAPI", mConnection);

			function = this.createFunction(mRepository,
					function_name.toUpperCase());
			if (function == null) {
				System.out.println("Function:" + function_name.toUpperCase()
						+ " not found in SAP.");
				throw new Exception("Function:" + function_name.toUpperCase()
						+ " not found in SAP.");
			}
			String table_name = "";
			Iterator it = query_hm.keySet().iterator();

			while (it.hasNext()) {
				table_name = (String) it.next();

				if (table_name.indexOf("L_") != -1) {
					codes = function.getTableParameterList().getTable(
							table_name.substring(2).toUpperCase());

					/*
					 * ArrayList qlist = (ArrayList) query_hm.get(table_name);
					 * // qlist 的第一位是字段名称（index=0）,2第二位开始才是数值(index>=1) if
					 * (qlist != null && qlist.size() > 0) {
					 * codes.appendRows(qlist.size() - 1); for (int x = 1; x <
					 * qlist.size(); x++) { codes.setRow(x - 1);
					 * codes.setValue((String) qlist.get(x), (String)
					 * qlist.get(0)); } }
					 */

					ArrayList qlist2 = (ArrayList) query_hm.get(table_name);

					if (qlist2 != null && qlist2.size() > 0) {
						codes.appendRows(qlist2.size());

						for (int x = 0; x < qlist2.size(); x++) {
							HashMap lineMap = (HashMap) qlist2.get(x);
							codes.setRow(x);
							Iterator it2 = lineMap.keySet().iterator();
							while (it2.hasNext()) {
								String key = (String) it2.next();
								codes.setValue((String) lineMap.get(key), key);
							}

						}
					}

					query_hm.remove(table_name);
					break;
				}

			}

			DATA = function.getTableParameterList().getTable(
					result_table_name.toUpperCase());
			input = function.getImportParameterList();
			Iterator key_it = query_hm.keySet().iterator();
			while (key_it.hasNext()) {
				String key = (String) key_it.next();
				if (key.indexOf("L_") == -1)
					input.setValue(query_hm.get(key), key);
			}

			mConnection.execute(function);
			IMetaData imd = DATA.getMetaData();
			String fields[] = new String[imd.getFieldCount()];
			for (int j = 0; j < imd.getFieldCount(); j++) {
				fields[j] = imd.getName(j);
			}

			for (int i = 0; i < DATA.getNumRows(); i++) {
				DATA.setRow(i);
				HashMap datahm = new HashMap(imd.getFieldCount());
				for (int z = 0; z < fields.length; z++) {
					datahm.put(fields[z], DATA.getString(fields[z]));
				}
				resultList.add(datahm);
			}
		} catch (Exception ex) {
			logger.error("call sap error >>> ", ex);
			resultList = null;
		} finally {
			mRepository = null;
			sapDataSource.close(mConnection);
		}
		return resultList;
	}

	/**
	 * sj 促销品读信息
	 * 
	 * @param function_name
	 * @param result_table_name
	 * @param query_hm
	 * @return
	 */
	public ArrayList getSAPData5(String function_name,
			String result_table_name, HashMap query_hm) {

		ArrayList<Object> resultList = new ArrayList<Object>();
		JCO.Function function = null;
		JCO.Table DATA = null;
		JCO.Table codes = null;
		JCO.ParameterList input = null;
		JCO.Client mConnection = null;
		JCO.Repository mRepository = null;

		try {
			mConnection = sapDataSource.createConnection();
			mRepository = new JCO.Repository("WWBAPI", mConnection);
			System.out.println("SAP连接成功");

			function = this.createFunction(mRepository,
					function_name.toUpperCase());
			if (function == null) {
				System.out.println("Function:" + function_name.toUpperCase()
						+ " not found in SAP.");
				throw new Exception("Function:" + function_name.toUpperCase()
						+ " not found in SAP.");
			}
			String table_name = "";
			Iterator it = query_hm.keySet().iterator();
			while (it.hasNext()) {
				table_name = (String) it.next();
				if (table_name.indexOf("L_") != -1) {
					codes = function.getTableParameterList().getTable(
							table_name.substring(2).toUpperCase());
					ArrayList qlist = (ArrayList) query_hm.get(table_name);
					// qlist 的第一位是字段名称（index=0）,2第二位开始才是数值(index>=1)
					if (qlist != null && qlist.size() > 0) {
						codes.appendRows(qlist.size() - 1);
						for (int x = 1; x < qlist.size(); x++) {
							codes.setRow(x - 1);
							codes.setValue((String) qlist.get(x),
									(String) qlist.get(0));
						}
					}
					query_hm.remove(table_name);
					break;
				}
			}

			DATA = function.getTableParameterList().getTable(
					result_table_name.toUpperCase());
			input = function.getImportParameterList();
			Iterator key_it = query_hm.keySet().iterator();
			while (key_it.hasNext()) {
				String key = (String) key_it.next();
				if (key.indexOf("L_") == -1)
					input.setValue(query_hm.get(key), key);
			}

			mConnection.execute(function);
			IMetaData imd = DATA.getMetaData();

			for (int i = 0; i < DATA.getNumRows(); i++) {
				DATA.setRow(i);
				HashMap datahm = new HashMap(imd.getFieldCount());
				datahm.put("MEINS", DATA.getString("MEINS"));
				datahm.put("ZMT_TIX", DATA.getString("ZMT_TIX"));
				datahm.put("MATNR", DATA.getString("MATNR"));
				datahm.put("MAKTX", DATA.getString("MAKTX"));
				datahm.put("MSEHL", DATA.getString("MSEHL"));
				datahm.put("CR_DATE", DATA.getString("CR_DATE"));
				datahm.put("ZMT_PIC", DATA.getByteArray("ZMT_PIC"));
				resultList.add(datahm);
			}

		} catch (Exception ex) {
			logger.error("call sap error >>> ", ex);
			resultList = null;
		} finally {
			mRepository = null;
			sapDataSource.close(mConnection);
		}
		return resultList;
	}

	public SapQuery getSapQuery(String function_name, String result_table_name,
			HashMap query_hm) throws Exception {
		JCO.Client mConnection = sapDataSource.createConnection();
		JCO.Repository mRepository = new JCO.Repository("WWBAPI", mConnection);
		logger.info("SAP连接 " + mConnection.getASHost() + " 成功");

		JCO.Function function = this.createFunction(mRepository,
				function_name.toUpperCase());
		if (function == null) {
			System.out.println("Function:" + function_name.toUpperCase()
					+ " not found in SAP.");
			throw new Exception("Function:" + function_name.toUpperCase()
					+ " not found in SAP.");
		}
		
		String table_name = "";
		Iterator it = query_hm.keySet().iterator();
		while (it.hasNext()) {
			table_name = (String) it.next();
			if (table_name.indexOf("L_") != -1) {
				JCO.Table codes = function.getTableParameterList().getTable(
						table_name.substring(2).toUpperCase());
				ArrayList qlist = (ArrayList) query_hm.get(table_name);
				// qlist 的第一位是字段名称（index=0）,2第二位开始才是数值(index>=1)
				if (qlist != null && qlist.size() > 0) {
					codes.appendRows(qlist.size() - 1);
					for (int x = 1; x < qlist.size(); x++) {
						codes.setRow(x - 1);
						codes.setValue((String) qlist.get(x),
								(String) qlist.get(0));
					}
				}
				query_hm.remove(table_name);
				break;
			}
		}

		JCO.Table DATA = function.getTableParameterList().getTable(
				result_table_name.toUpperCase());
		JCO.ParameterList input = function.getImportParameterList();
		Iterator key_it = query_hm.keySet().iterator();
		while (key_it.hasNext()) {
			String key = (String) key_it.next();
			if (key.indexOf("L_") == -1)
				input.setValue(query_hm.get(key), key);
		}
		
		mConnection.execute(function);
		return new SapQuery(mConnection, mRepository, DATA);
	}

	/**
	 * 提供远程呼叫SAP BAPI 程序功能, 不返回任何值。 目前用在 “客户订货申请抛转到SAP开立SO订单” 时，把勾打上。
	 */
	public void callSAP(String function_name, String result_table_name,
			HashMap query_hm) {
		JCO.Function function = null;
		JCO.ParameterList input = null;
		function_name = function_name.toUpperCase();

		JCO.Client mConnection = null;
		JCO.Repository mRepository = null;

		try {
			mConnection = sapDataSource.createConnection();
			mRepository = new JCO.Repository("WWBAPI", mConnection);
			logger.info("SAP连接 " + mConnection.getASHost() + " 成功");

			function = this.createFunction(mRepository, function_name);

			if (function == null) {
				System.out.println("Function:" + function_name
						+ " not found in SAP.");
				throw new Exception("Function:" + function_name
						+ " not found in SAP.");
			}

			// function.getTableParameterList().getTable(result_table_name.toUpperCase());
			input = function.getImportParameterList();
			Iterator key_it = query_hm.keySet().iterator();
			while (key_it.hasNext()) {
				String key = (String) key_it.next();
				input.setValue(query_hm.get(key), key);
			}

			mConnection.execute(function);

		} catch (Exception ex) {
			logger.error("call sap error >>> ", ex);
		} finally {
			mRepository = null;
			sapDataSource.close(mConnection);
		}
	}

	public String getASHost() throws SapComponentException {
		JCO.Client conn = sapDataSource.createConnection();
		String host = conn.getASHost();
		JCO.releaseClient(conn);
		return host;
	}

	/**
	 * 使用提供的名称建立功能
	 * 
	 * @param name
	 *            String 功能名称
	 * @return Function 功能实例
	 * @throws Exception
	 *             抛出异常
	 */
	private Function createFunction(JCO.Repository repository, String name)
			throws Exception {
		try {
			IFunctionTemplate ft = repository.getFunctionTemplate(name
					.toUpperCase());
			if (ft == null) {
				return null;
			}
			return ft.getFunction();
		} catch (Exception ex) {
			throw new Exception("Problem retrieving JCO.Function object.");
		}
	}
}
