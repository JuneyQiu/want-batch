package com.want.batch.job.monitor;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

/**
 * 
 * @author wangyicheng
 *
 *	表当天的数据和前一天作对比,将这个过程封装成一个类..
 *
 *  在sql.properties配置sql,在xml文件中配置bean,根据全局变量 sqlCount、selectSql选择sql是否需要配置参数,是否要做逻辑判断
 */
public class HRModel{

	private static final String YES="是";
	private static final String NO ="否";
	private static final String VERSION ="CURRENT_VER";
	private int sqlCount;
	private int selectSql;
	private List<String> listSql;
	private Map<String,SimpleJdbcOperations> mapPar;
	private static final String HISTORY = "history";
	private static final String TORY = "tody";
	private String tableName;	
	private int historyCount;
	private int todyCount;
	private String diversityRatio;
	private String yesOrNo;
	
	/* 
	 * key:代表当天or前一天
	 * value→Map<String,SimpleJdbcOperations>:
	 * 		每个map中key为sql,value为SimpleJdbcOperations(有些表对应的数据库不同,在xml中配对应的)
	 * 
	 * spring 加载启动后所有数据通过init已经初始化
	 */
	private Map<String,Map<String,SimpleJdbcOperations>> mapData;
	
	/*
	 * 初始化
	 */
	public void init(){
		Iterator<Entry<String,Map<String,SimpleJdbcOperations>>> itData = mapData.entrySet().iterator();
		while(itData.hasNext()){
			Entry<String,Map<String,SimpleJdbcOperations>> e = itData.next();
			Map<String,SimpleJdbcOperations> maps = e.getValue();
			Iterator<Entry<String, SimpleJdbcOperations>> it = maps.entrySet().iterator();
			Entry<String, SimpleJdbcOperations> ee = it.next();
			if(e.getKey().equals(HISTORY)){
				int n = -1;
				while(2>1){
					
					List<Map<String,Object>> listH = ee.getValue().queryForList(ee.getKey(),getYestedayDate(n));
					if(listH.size()>0){
						Map<String,Object> mapH = listH.get(0);
						historyCount = Integer.parseInt(mapH.entrySet().iterator().next().getValue().toString());
						break;
					}
					n--;
				}
				
			}
			if(e.getKey().equals(TORY)){
				if(sqlCount==1){
					int num = 0;
					if(listSql!=null && listSql.size()>0){
						for (int i = 0; i < listSql.size(); i++) {
							num+= ee.getValue().queryForInt(listSql.get(i));
						}
						todyCount = num;
					}
				}
				if(sqlCount==2){
					int countPar = ee.getValue().queryForInt(listSql.get(0),getParId(mapPar));
					int num=0;
					for (int i = 1; i < listSql.size(); i++) {
						num+= ee.getValue().queryForInt(listSql.get(i));
					}
					todyCount = countPar+num;
				}
				if(selectSql==1){
					List<Map<String,Object>> list = ee.getValue().queryForList(ee.getKey());
					for(Map<String,Object> mapFiled: list){
						
						if(mapFiled.get(VERSION)!=null){
							Integer version = Integer.parseInt(mapFiled.get(VERSION).toString());
							if(version==0){
								todyCount=0;
								break;
							}
							todyCount=version;
						}
					}
				}
				if(selectSql==2) todyCount = ee.getValue().queryForInt(ee.getKey(),getParId(mapPar));
				if(sqlCount==0 && selectSql==0) todyCount = ee.getValue().queryForInt(ee.getKey());
			}
			
			float r =(float)todyCount-historyCount;
			float rr = r/historyCount*100;
			String str = new java.text.DecimalFormat("0.00").format(rr);
			diversityRatio=str+"%";
			yesOrNo=getYestOrNo(rr);
		}	
	}
	
	/*
	 * 前一天的日期
	 */
	public static String getYestedayDate(int n){
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, n);
        String  yestedayDate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
        return yestedayDate;
		
	}
	/*
	 * 是否正确
	 */
	public static String getYestOrNo(float a){
		float f1 = 10.0f;
		float f2 = -10.0f;
		if(a>=f1 || a<=f2) return NO;
		else return YES;
	}
	
	public static int getParId(Map<String,SimpleJdbcOperations> mapPar){
		int id = 0;
		Iterator<Entry<String,SimpleJdbcOperations>> it = mapPar.entrySet().iterator();
		if(it.hasNext()){
			Entry<String, SimpleJdbcOperations> ee = it.next();
			
			List<Map<String,Object>> list = ee.getValue().queryForList(ee.getKey());
			for(Map<String,Object> mapFiled: list){
				
				if(mapFiled.get("CURRENT_VER")!=null){
					Integer version = Integer.parseInt(mapFiled.get("CURRENT_VER").toString());
					if(version==0){
						id=0;
						break;
					}
					id=version;
				}
			}//for
		}
		return id;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getHistoryCount() {
		return historyCount;
	}

	public void setHistoryCount(int historyCount) {
		this.historyCount = historyCount;
	}

	public int getTodyCount() {
		return todyCount;
	}

	public void setTodyCount(int todyCount) {
		this.todyCount = todyCount;
	}

	public String getDiversityRatio() {
		return diversityRatio;
	}

	public void setDiversityRatio(String diversityRatio) {
		this.diversityRatio = diversityRatio;
	}

	public String getYesOrNo() {
		return yesOrNo;
	}

	public void setYesOrNo(String yesOrNo) {
		this.yesOrNo = yesOrNo;
	}

	public Map<String, Map<String, SimpleJdbcOperations>> getMapData() {
		return mapData;
	}

	public void setMapData(Map<String, Map<String, SimpleJdbcOperations>> mapData) {
		this.mapData = mapData;
	}
	
	public int getSqlCount() {
		return sqlCount;
	}


	public void setSqlCount(int sqlCount) {
		this.sqlCount = sqlCount;
	}


	public List<String> getListSql() {
		return listSql;
	}


	public void setListSql(List<String> listSql) {
		this.listSql = listSql;
	}


	public int getSelectSql() {
		return selectSql;
	}


	public void setSelectSql(int selectSql) {
		this.selectSql = selectSql;
	}


	public Map<String, SimpleJdbcOperations> getMapPar() {
		return mapPar;
	}


	public void setMapPar(Map<String, SimpleJdbcOperations> mapPar) {
		this.mapPar = mapPar;
	}
}
