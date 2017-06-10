package com.want.batch.job.monitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.want.batch.job.AbstractWantJob;
import com.want.component.mail.MailService;

/**
 * 
 * @author wangyicheng
 * 
 *HR数据
 */
public class HRMonitorJob extends AbstractWantJob{
	private static final String MAIL_TITLE ="人事数据巡检 ： ";
	private static final String RESULTSTR = "RESULT";
	private static final String RESULTNAME = "计算结果";
	private static final String TDSTRFIRST = "<td rowspan='%s'>%s</td>";
	private static final String TDSTR = "<td>%s</td>";
	private static final String TR1 ="<tr>";
	private static final String TR2 ="</tr>";
	private static final String PROPERTIESNAME = "project";
	private static final String ADDRESSEE = "project.batch.monitor.mail.hr.to";
	private static final String CC = "project.batch.monitor.mail.hr.cc";
	
	/*
	 * 每个key代表一个源数据,对应value为对应的数据结果
	 * 这里使用TreeMap为了对Map进行排序
	 * mapAndSum → 将要求和的对象集放在一个集合,进行排列组合
	 * mapOhter →  不求和的对象集
	 */
	private TreeMap<String,List<HRModel>> mapAndSum;
	private TreeMap<String,List<HRModel>> mapOhter;
	/* 求和的列数*/
	private int sumCount;
	
	/*存储当天数据*/
	private Map<String,String> mapInsert;
	/*历史数据存放在同一个database*/
	private SimpleJdbcOperations hJdbc;
	
	@Override
	public void execute() throws Exception {
		
		initHR(mapAndSum);
		initHR(mapOhter);
		/*邮件整体*/		
		String content = getContent();
		/*邮件结果集*/
		StringBuilder resultContent = new StringBuilder();
		/*对data进行求和、组合*/
		TreeMap<String,List<HRModel>> mapR = combinationMap();
		/*组合StringBuilder 并赋值*/
		assignment(resultContent,mapR);
		/*将非求和的对象集组装进resultContent*/
		assignment(resultContent, mapOhter);
		/*存储当天的数据*/
		insertCurrentData(mapR,mapOhter);
	
		/*获取properties文件,该文件里封装了邮件接受者*/
		ResourceBundle bundle = ResourceBundle.getBundle(PROPERTIESNAME);
		/*邮件title*/
		String subject = String.format(MAIL_TITLE+HRModel.getYestedayDate(-1)+"--"+
							new SimpleDateFormat("yyyyMMdd").format(new Date()));
		/*获取邮件发送factroy*/
		MailService mailService = getMailService();
		/*解析邮件接受者*/
		StringTokenizer tos = new StringTokenizer(
				bundle.getString(ADDRESSEE), ",");
		while (tos.hasMoreElements()) {
			String to = tos.nextToken();
			mailService.to(to);
		}
		/*解析邮件抄送对象*/
		StringTokenizer ccs = new StringTokenizer(
				bundle.getString(CC), ",");
		while (ccs.hasMoreElements()) {
			String cc = ccs.nextToken();
			mailService.cc(cc);
		}
		/*发送邮件*/
		mailService.subject(subject)
				.content(String.format(content,resultContent))
				.send();
	}
	
	/*
	 *给邮件结果集赋值 
	 */
	public void assignment(StringBuilder sb,TreeMap<String,List<HRModel>> tMap){
		Iterator<Entry<String, List<HRModel>>> it = tMap.entrySet().iterator();

		while(it.hasNext()){
			Entry<String, List<HRModel>> entry = it.next();
			String dataSource = entry.getKey();
			
			List<HRModel> listValue = entry.getValue();
			for (int i = 0; i < listValue.size(); i++) {
				HRModel hModel = listValue.get(i);
				if(i==0){
					sb.append(TR1);
					sb.append(String.format(TDSTRFIRST,listValue.size(),dataSource,dataSource));
					sb.append(String.format(TDSTR,hModel.getTableName()));
					sb.append(String.format(TDSTR,hModel.getHistoryCount()));
					sb.append(String.format(TDSTR,hModel.getTodyCount()));
					sb.append(String.format(TDSTR,hModel.getDiversityRatio()));
					sb.append(String.format(TDSTR,hModel.getYesOrNo()));
					sb.append(TR2);
					continue;
				}
					sb.append(TR1);
					sb.append(String.format(TDSTR,hModel.getTableName()));
					sb.append(String.format(TDSTR,hModel.getHistoryCount()));
					sb.append(String.format(TDSTR,hModel.getTodyCount()));
					sb.append(String.format(TDSTR,hModel.getDiversityRatio()));
					sb.append(String.format(TDSTR,hModel.getYesOrNo()));
					sb.append(TR2);
			}	
		}
	}
	
	/*
	 * 重新组合源数据集:并列求和,并组装进原数据集中
	 */
	public TreeMap<String,List<HRModel>> combinationMap(){
		TreeMap<String,List<HRModel>> mapClone = new TreeMap<String, List<HRModel>>();
		mapClone.putAll(mapAndSum);
		
		/*算法,求和*/
		int[] countHistory = new int[this.sumCount];
		int[] countTody = new int[this.sumCount];
		String[] tableName = new String[this.sumCount];
		Iterator<Entry<String, List<HRModel>>> it = mapClone.entrySet().iterator();
		int flag = 0;
		while(it.hasNext()){
			flag++;
			Entry<String, List<HRModel>> entry = it.next();
			List<HRModel> list = entry.getValue();
			
			for (int i = 0; i < list.size(); i++) {	
				countTody[i]+=list.get(i).getTodyCount();
				countHistory[i]+=list.get(i).getHistoryCount();
				if(flag==1){
					/*求和后的源数据对象的tableName通过截取字符串 动态生成*/
					tableName[i] = RESULTSTR+list.get(i).getTableName().substring(list.get(i).getTableName().indexOf("_"));
				}
			}
		}//while()
		
		/*重新组合源数据对象集*/
		List<HRModel> listResult = new ArrayList<HRModel>(); 
		for (int i = 0; i < tableName.length; i++) {
			HRModel bModel = new HRModel();
			bModel.setHistoryCount(countHistory[i]);
			bModel.setTodyCount(countTody[i]);
			bModel.setTableName(tableName[i]);
			
			float r =(float)countTody[i]-countHistory[i];
			float rr = r/countHistory[i]*100;
			String str = new java.text.DecimalFormat("0.00").format(rr);
			bModel.setDiversityRatio(str+"%");
			bModel.setYesOrNo(HRModel.getYestOrNo(rr));
			listResult.add(bModel);
			
		}
		mapClone.put(RESULTNAME,listResult);
		return mapClone;
		
	}//method

	/*
	 * 存储当天的信息
	 */
	public void insertCurrentData(TreeMap<String,List<HRModel>> map1,TreeMap<String,List<HRModel>> map2){
		map1.putAll(map2);
		Iterator<Entry<String, List<HRModel>>> it = map1.entrySet().iterator();
		
		
		while(it.hasNext()){
			Entry<String, List<HRModel>> entry = it.next();
			String dataSource = entry.getKey();
			List<HRModel> list = entry.getValue();
		
			Iterator<Entry<String,String>> itInsert = mapInsert.entrySet().iterator();
			while(itInsert.hasNext()){
				Entry<String,String> entryInsert = itInsert.next();
				if(dataSource.equals(entryInsert.getKey())){
					Object[] obj = new Object[list.size()+1];
					for (int i = 0; i < list.size(); i++) {
						HRModel hModel = list.get(i);
						obj[i]=hModel.getTodyCount();
					}
					//存储日期→ 当天
					obj[list.size()]= new SimpleDateFormat("yyyyMMdd").format(new Date());
					List<Object[]> parameters = new ArrayList<Object[]>();
					parameters.add(obj);
					hJdbc.batchUpdate(entryInsert.getValue(),parameters);
					break;
				}
			}
		}
	}
	
	/*
	 * 创建邮件内容的主体架构
	 */
	public String getContent(){
		String content = new StringBuilder()
		.append("<html><body><table border='1' bgcolor='#F2F2F2' style=text-align:center>").append("<tr bgcolor='#308DBB'>")
		.append("<td><b>源数据</b></td>")
		.append("<td><b>需检查的table</b></td>")
		.append("<td><b>前一日的数量</b></td>")
		.append("<td><b>今日的数量</b></td>")
		.append("<td><b>差异比例</b></td>")
		.append("<td><b>数据是否正确 </b></td>")
		.append("</tr>%s")
		.append("</table></body></html>").toString();
		return content;
	}
	
	
	private void initHR(TreeMap<String,List<HRModel>> mapinit){
		Iterator<Entry<String, List<HRModel>>> it = mapinit.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, List<HRModel>> entry = it.next();
			List<HRModel> list = entry.getValue();
			for (int i = 0; i < list.size(); i++) {
				HRModel hr = list.get(i);
				hr.init();
			}
		}
			
	}

	public TreeMap<String, List<HRModel>> getMapAndSum() {
		return mapAndSum;
	}


	public void setMapAndSum(TreeMap<String, List<HRModel>> mapAndSum) {
		this.mapAndSum = mapAndSum;
	}


	public TreeMap<String, List<HRModel>> getMapOhter() {
		return mapOhter;
	}


	public void setMapOhter(TreeMap<String, List<HRModel>> mapOhter) {
		this.mapOhter = mapOhter;
	}

	public int getSumCount() {
		return sumCount;
	}

	public void setSumCount(int sumCount) {
		this.sumCount = sumCount;
	}

	public Map<String, String> getMapInsert() {
		return mapInsert;
	}

	public void setMapInsert(Map<String, String> mapInsert) {
		this.mapInsert = mapInsert;
	}

	public SimpleJdbcOperations gethJdbc() {
		return hJdbc;
	}

	public void sethJdbc(SimpleJdbcOperations hJdbc) {
		this.hJdbc = hJdbc;
	}

	
}
