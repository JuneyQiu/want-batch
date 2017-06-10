package com.want.batch.job.monitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.want.batch.job.AbstractWantJob;
import com.want.component.mail.MailService;

public class RebuildIndexJob34 extends AbstractWantJob{
	private static final String PROPERTIESNAME = "project";
	private static final String SCHEMAPARAMETER = "rebuildIndex.schedule.schedule.parameter.34";
	private static final String SELECTINDEX_SQL = "select INDEX_NAME from all_indexes where table_owner='%s' and num_rows>%s";//REBUILDINDEX_SQL
	private static final String REBUILDINDEX_SQL = "ALTER INDEX %s REBUILD NOLOGGING";
	private static final String ALERTLOG = "ALTER INDEX %s LOGGING";
	private static final String  INDEXNAME = "INDEX_NAME";
	
	private static final String ADDRESSEE = "project.rebuildIndex.mail.hr.to.week";
	private static final String CC = "project.rebuildIndex.mail.hr.cc.week";
	private static final String MAIL_TITLE = "重建索引报告(生产环境)";
	
	/*数据源 */
	private Map<String, SimpleJdbcOperations> mapJdbc;
	
	/*打印错误信息*/
	private String errorMessage = "";
	@Override
	public void execute() throws Exception {
		List<SchemaMessage> listSchema = initSchemaMessage();
		logger.info(String.format("prepare rebuild index,schema:%s",getAllSchemas(listSchema)));
		for (int i = 0; i < listSchema.size(); i++) {
			SchemaMessage sm = listSchema.get(i);
			String [] indexNames = sm.getIndexNameForSchema();
			if(indexNames!=null){
				logger.info(String.format("%s 有%s个大于%s的indexName.......",sm.getSchema(),indexNames.length,sm.getNumRows()));
				for (int j = 0; j < indexNames.length; j++) {
					try {
						sm.jdbc.update(String.format(REBUILDINDEX_SQL, indexNames[j]));
						logger.info(String.format("%s-%s rebuild index 成功~~~~~",sm.getSchema(),indexNames[j]));
						
						sm.jdbc.update(String.format(ALERTLOG, indexNames[j]));//
						logger.info(String.format("%s-%s  LOGGING 成功~~~~~",sm.getSchema(),indexNames[j]));//
						
					} catch (Exception se) {
						logger.info(String.format("%s-%s rebuild index 失败~~~~~",sm.getSchema(),indexNames[j]));
						
						logger.info(String.format("%s-%s  LOGGING 失败~~~~~",sm.getSchema(),indexNames[j]));//
						
						logger.error(se);
					}
				}
			}
		}
		
		//发送邮件
		/*获取properties文件,该文件里封装了邮件接受者*/
		ResourceBundle bundle = ResourceBundle.getBundle(PROPERTIESNAME);
		
		
		
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
		
		String subject = MAIL_TITLE;;
		String content = new StringBuilder()
		.append("<html><body> <span style='color:#228B22'> Dear All:</span> <br> <hr>" +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='color:#228B22'><span style='color:red'>生产环境</span>重建索引已完成，请知悉!</span>" +
				"<br><span style='color:#228B22'>重建范围：10.0.0.34(ICUSTOMER,HW09)</span>"+
				"</body></html>")
		.toString();
		
		
		/*发送邮件*/
		mailService.subject(subject).content(content).send();
		
	}
	/*拿到每个SchemaName*/
	public String getAllSchemas(List<SchemaMessage> listSchema){
		String str="";
		for (int i = 0; i < listSchema.size(); i++) {
			str+=listSchema.get(i).getSchema()+",";
		}
		return str;
	}
	
	/*
	 * 获取要rebuild index的schame对象集
	 */
	public List<SchemaMessage> initSchemaMessage(){
		
		List<String> listSchamePars = getListPropertiesMessage();
		List<SchemaMessage> listSchemaMessage = new ArrayList<SchemaMessage>();
		
		/*factory...*/
		for (int i = 0; i < listSchamePars.size(); i++) {
			String par = listSchamePars.get(i);
			String [] arrs = par.split("_");
			SchemaMessage sm = new SchemaMessage(arrs[0]+"_"+arrs[1], arrs[1],Integer.parseInt(arrs[2]));
			Iterator<Entry<String, SimpleJdbcOperations>> it = mapJdbc.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, SimpleJdbcOperations> entry = it.next();
				String key = entry.getKey();
				SimpleJdbcOperations value = entry.getValue();
				if(sm.getJdbcType().equals(key)){
					sm.setJdbc(value);
					break;
				}
			}//while
			listSchemaMessage.add(sm);
		}//for
		return listSchemaMessage;
	}
	
	/*
	 * 从properties中获取schema信息
	 */
	public List<String> getListPropertiesMessage(){
		ResourceBundle bundle = ResourceBundle.getBundle(PROPERTIESNAME);
		StringTokenizer spMessage = new StringTokenizer(bundle.getString(SCHEMAPARAMETER), ",");
		List<String> listSchamePars = new ArrayList<String>();
		while (spMessage.hasMoreElements()) {
			//System.err.println(spMessage.nextToken());
			listSchamePars.add(spMessage.nextToken());
		}
		return listSchamePars;
	}
	
	/*
	 * 创建sql
	 */
	public String createSql(String schema,int numRows){
		return String.format(SELECTINDEX_SQL,schema,numRows);
	}
	
	public Map<String, SimpleJdbcOperations> getMapJdbc() {
		return mapJdbc;
	}
	public void setMapJdbc(Map<String, SimpleJdbcOperations> mapJdbc) {
		this.mapJdbc = mapJdbc;
	}
	
	/**
	 * 
	 * @author 00159184
	 *	SchemaMessage
	 */
	class SchemaMessage{
		private String jdbcType;
		private String schema;
		private int numRows;
		private SimpleJdbcOperations jdbc;
		
		
		public SchemaMessage(String jdbcType,String schema,int numRows) {
			this.jdbcType = jdbcType;
			this.schema = schema;
			this.numRows = numRows;
		}
		
		/*
		 * 查询符合条件的索引
		 */
		public String[] getIndexNameForSchema() throws Exception{
			String [] indexNames = null;
			String sql = createSql(this.schema,this.numRows);
			List<Map<String,Object>> listIndexNames = this.jdbc.queryForList(sql);
			if(listIndexNames==null || listIndexNames.size()==0) return null;
			indexNames  = new String[listIndexNames.size()];
			for (int i = 0; i < listIndexNames.size(); i++) {
				indexNames[i]=listIndexNames.get(i).get(INDEXNAME).toString();		
			}
			return indexNames;
		}
		public String getJdbcType() {
			return jdbcType;
		}
		public void setJdbcType(String jdbcType) {
			this.jdbcType = jdbcType;
		}
		public String getSchema() {
			return schema;
		}
		public void setSchema(String schema) {
			this.schema = schema;
		}

		public int getNumRows() {
			return numRows;
		}

		public void setNumRows(int numRows) {
			this.numRows = numRows;
		}

		public SimpleJdbcOperations getJdbc() {
			return jdbc;
		}

		public void setJdbc(SimpleJdbcOperations jdbc) {
			this.jdbc = jdbc;
		}
	}	
}
