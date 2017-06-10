/**
 * 
 */
package com.want.batch.job.monitor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.component.mail.MailService;
import com.want.data.pojo.ErrorInfo;

/**
 * @author Timothy
 */
@Component("iCustomerWebExceptionMonitorJob")
public class ICustomerWebExceptionMonitorJob extends AbstractWantJob {

	private final Map<String, String> funcUserMap = new HashMap<String, String>();

	private final String viewErrorUrl = "http://10.0.0.205:8680/Auto_Manager/Prod_errorInfo_servlet?id=";
	private final String subjectDatePattern = "yyyy-MM-dd";

	private final Set<String> coders = new HashSet<String>();
	private final Set<String> sas = new HashSet<String>();

	private final boolean ccSa = false;

	public ICustomerWebExceptionMonitorJob() {
		funcUserMap.put("/mn_form/%.action", "00114260");
		funcUserMap.put("/st_form/%.action", "00114260");
		funcUserMap.put("/bs_form/%.action", "00114260");
		funcUserMap.put("/system/%.action", "00114260");
		funcUserMap.put("/common/%.action", "00114260");
		funcUserMap.put("/customer/%.action", "00114260");
		funcUserMap.put("/mobile/%.action", "00114260");
		funcUserMap.put("/hr_form/%.action", "ZMZ");
		funcUserMap.put("/qt_form/%.action", "ZMZ");
		funcUserMap.put("/dt_form/%.action", "ZMZ");
		funcUserMap.put("/ww/%.action", "ZMZ");
		funcUserMap.put("/exchange/%.action", "ZMZ");
		funcUserMap.put("/order/%.action", "ZMZ");
		funcUserMap.put("/ks_form/%.action", "ZMZ");
		funcUserMap.put("/it_form/%.action", "00109667");
		funcUserMap.put("/gw_form/%.action", "00109667");
		funcUserMap.put("/xg_form/%.action", "00109667");
		funcUserMap.put("/cg_form/%.action", "00109667");
		funcUserMap.put("/org/%.action", "00109667");
		funcUserMap.put("/report/%.action", "00109667");
		funcUserMap.put("/sap/%.action", "00109667");
		funcUserMap.put("/qw-app/%", "VS");
	}

	@Override
	public void execute() {

		filter();
		prepareData();

		DateTime endDate = new DateTime().withHourOfDay(0).withMinuteOfHour(0)
				.withSecondOfMinute(0).withMillisOfSecond(0);
		DateTime startDate = (endDate.getDayOfWeek() == DateTimeConstants.WEDNESDAY) ? endDate
				.minusDays(5) : endDate.minusDays(2);
		String content = this.getMailContent(getErrors(startDate, endDate));

		sendMail(endDate, startDate, content);

//		sendNotPrincipalUri();
	}

	protected void sendMail(DateTime endDate, DateTime startDate, String content) {
		// for (String no : coders) {
		// this.getMailService().to(generateMailAddress(no));
		// }

		// 看是否需要抄送SA
		/*
		if (ccSa) {
			for (String no : sas) {
				this.getMailService().cc(generateMailAddress(no));
			}
		}
		*/
		
		MailService mailService = getMailService();
		ResourceBundle bundle = ResourceBundle.getBundle("project");
		StringTokenizer tos = new StringTokenizer(
				bundle.getString("web.exception.mail.to"), ",");
		while (tos.hasMoreElements()) {
			String to = tos.nextToken();
			mailService.to(generateMailAddress(to));
		}

		StringTokenizer ccs = new StringTokenizer(
				bundle.getString("web.exception.mail.cc"), ",");
		while (ccs.hasMoreElements()) {
			String cc = ccs.nextToken();
			mailService.cc(generateMailAddress(cc));
		}

		mailService.subject(
				String.format("生产环境异常报告(%s~%s)",
						startDate.toString(subjectDatePattern),
						endDate.toString(subjectDatePattern)))
		.content(content)

		.send();


		/*
		this.getMailService()

				.to(generateMailAddress("00082316"))
				// 王岳
				.to(generateMailAddress("00109667"))
				// 张玲
				.to(generateMailAddress("00114260"))
				// 于桂艳
				.to(generateMailAddress("00091897"))
				// 程雅婷
				.cc(generateMailAddress("00084420"))
				// Wendy
				.cc(generateMailAddress("00001936"))
				// David
				.cc(generateMailAddress("00127934"))
				// 陶杰
				.cc(generateMailAddress("00143479"))
				// jack
				// .cc(generateMailAddress("00110392")) // Ron
				.cc(generateMailAddress("00147111"))
				//陈焕
				.cc(generateMailAddress("00145727"))
				//谢吉桂
				.subject(
						String.format("生产环境异常报告(%s~%s)",
								startDate.toString(subjectDatePattern),
								endDate.toString(subjectDatePattern)))
				.content(content)

				.send();
			*/
	}

	private void filter() {

		String[] messageSqlConditions = new String[] {
				"%Connection % by peer%",
				"%weblogic.utils.NestedRuntimeException%",
				"%URLDecoder: Incomplete trailing escape%pattern",
				"%Cannot parse POST parameters%",
				"%org.apache.commons.fileupload.FileUploadException%" };

		for (String messageSqlCondition : messageSqlConditions) {
			String sql = "DELETE FROM RPTLOG.PROD_ERROR_INFO WHERE MESSAGE LIKE ?";
			int runResult = this.getDataMartJdbcOperations().update(sql,
					messageSqlCondition);
			logger.debug(String.format("Delete by %s : %s",
					messageSqlCondition, runResult));
		}

		// 删除url中存在 "%s"的状况
		this.getDataMartJdbcOperations()
				.update("DELETE FROM RPTLOG.PROD_ERROR_INFO WHERE SUBSTR(URI, LENGTH(URI) - 2, LENGTH(URI)) = '/%s'");

	}

	protected void prepareData() {

		// 重新导入 icustomer_functions
		this.getDataMartJdbcOperations()
				.update("DELETE FROM RPTLOG.ICUSTOMER_FUNCTIONS WHERE CODERS IS NULL AND SAS IS NULL AND USED IS NULL ");
		this.getDataMartJdbcOperations()
				.update(new StringBuilder()
						.append("INSERT INTO RPTLOG.ICUSTOMER_FUNCTIONS(SID, URI) ")
						.append("SELECT RPTLOG.ICUSTOMER_FUNCTIONS_SEQ.NEXTVAL, URI  ")
						.append("FROM( ")
						.append("  SELECT A.URI ")
						.append("  FROM RPTLOG.PROD_ERROR_INFO A LEFT JOIN RPTLOG.ICUSTOMER_FUNCTIONS B ON A.URI = B.URI ")
						.append("  WHERE B.URI IS NULL ")
						.append("  GROUP BY A.URI ").append(") A ").toString());

		for (Map.Entry<String, String> funcUserEntry : funcUserMap.entrySet()) {
			this.getDataMartJdbcOperations()
					.update("UPDATE RPTLOG.ICUSTOMER_FUNCTIONS SET CODERS = ? WHERE URI LIKE ?",
							funcUserEntry.getValue(), funcUserEntry.getKey());
		}
	}

	private String generateMailAddress(String no) {
		return no + "@want-want.com";
	}

	private String getMailContent(List<ErrorInfo> errors) {
		String content = new StringBuilder()
				.append("<html><body><table border='1'>").append("<tr>")
				.append("<th width='30px'>序号</th>")
				.append("<th width='170px'>发生次数</th>")
				.append("<th width='150px'>所属应用</th>")
				.append("<th width='150px'>负责人</th>")
				.append("<th width='200px'>功能URI</th>")
				.append("<th width='350px'>Exception</th>")
				.append("<th width='50px'>处理状态</th>").append("</tr>%s")
				.append("</table></body></html>").toString();

		List<String> errorContents = new ArrayList<String>();

		for (int i = 0; i < errors.size(); i++) {
			ErrorInfo error = errors.get(i);
			String url = StringUtils.defaultIfEmpty(error.getUri(), "All ... ");
			errorContents
					.add(new StringBuilder()
							.append("<tr>")
							.append(String.format("<td>%s</td>", i + 1))
							.append(String.format("<td>%s</td>", this
									.generateCountLink(error.getSidLink(),
											error.getCount())))
							.append(String.format("<td>%s</td>",
									(url.indexOf("action") != -1) ? "BPM"
											: (url.equals("All ... ")) ? "其他"
													: "业务三网"))
							.append(String.format("<td>%s</td>", StringUtils
									.defaultString(error.getPrincipalName())))
							.append(String.format("<td>%s</td>", StringUtils
									.defaultIfEmpty(error.getUri(), "All ... ")))
							.append(String.format("<td>%s</td>",
									error.getMessage()))
							.append("<td>&nbsp;</td>").append("</tr>")
							.toString());
		}
		;

		return String
				.format(content, StringUtils.join(errorContents.toArray()));
	}

	private List<ErrorInfo> getGroupErrors(DateTime startDate, DateTime endDate) {

		return this
				.getDataMartJdbcOperations()
				.query(new StringBuilder()
						.append("SELECT MESSAGE, LENGTH(TRACE) TRACE_LENGTH, URI, COUNT(1) ")
						.append("FROM RPTLOG.PROD_ERROR_INFO ")
						.append("WHERE CREATE_DATE BETWEEN ? AND ? ")
						.append("  AND MESSAGE NOT LIKE '%Connection reset by peer%' ")
						.append("  AND MESSAGE NOT LIKE '%weblogic.utils.NestedRuntimeException%' ")
						.append("  AND MESSAGE NOT LIKE 'java.lang.IllegalArgumentException: URLDecoder: Incomplete trailing escape (%) pattern' ")
						.append("  AND server_ip IN ('icustomer.want-want.com','isales.want-want.com','isalesaudit.want-want.com','bpm.want-want.com','bpm.want-want.com','bpm') AND server_port in (80, 443) ")
						.append("GROUP BY MESSAGE, LENGTH(TRACE), URI ")
						.append("ORDER BY COUNT(1) DESC").toString(),
						new RowMapper<ErrorInfo>() {

							@Override
							public ErrorInfo mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								ErrorInfo error = new ErrorInfo();

								error.setMessage(rs.getString("MESSAGE"));
								error.setUri(rs.getString("URI"));
								error.setTraceLength(rs.getInt("TRACE_LENGTH"));

								return error;
							}
						}, startDate.toDate(), endDate.toDate());
	}

	private List<ErrorInfo> getErrors(DateTime startDate, DateTime endDate) {

		List<ErrorInfo> errors = this.getGroupErrors(startDate, endDate);

		fullNormalErrors(startDate, endDate, errors);
		fullConnectionResetByPeerErrors(startDate, endDate, errors);
		fullCannotParsePOSTParametersErrors(startDate, endDate, errors);

		return errors;
	}

	private void fullConnectionResetByPeerErrors(DateTime startDate,
			DateTime endDate, List<ErrorInfo> errors) {

		for (Map<String, Object> errorMap : this
				.getDataMartJdbcOperations()
				.queryForList(
						new StringBuilder()
								.append("SELECT MESSAGE, MIN(SID) MIN_SID, MAX(SID) MAX_SID ")
								.append("FROM RPTLOG.PROD_ERROR_INFO ")
								.append("WHERE CREATE_DATE BETWEEN ? AND ? AND MESSAGE LIKE ? ")
								.append("GROUP BY MESSAGE ").toString(),
						startDate.toDate(), endDate.toDate(),
						"%Connection reset by peer%")) {

			ErrorInfo error = new ErrorInfo();

			error.addSidLink(generateSidLink(errorMap.get("MIN_SID")));
			error.addSidLink(generateSidLink(errorMap.get("MAX_SID")));
			error.setMessage(errorMap.get("MESSAGE").toString());

			errors.add(error);

			// 此部分有数据余威处理
			error.setPrincipalNo("00078588");
			this.coders.add(error.getPrincipalNo());
			error.setPrincipalName(this.getPrincipalName(error.getPrincipalNo()));
		}
	}

	private void fullCannotParsePOSTParametersErrors(DateTime startDate,
			DateTime endDate, List<ErrorInfo> errors) {

		for (Map<String, Object> errorMap : this
				.getDataMartJdbcOperations()
				.queryForList(
						new StringBuilder()
								.append("SELECT SUBSTR(MESSAGE, 0, 80) || 'URL...' MESSAGE, MIN(SID) MIN_SID, MAX(SID) MAX_SID ")
								.append("FROM RPTLOG.PROD_ERROR_INFO ")
								.append("WHERE CREATE_DATE BETWEEN ? AND ? AND MESSAGE LIKE ? ")
								.append("GROUP BY SUBSTR(MESSAGE, 0, 80) || 'URL...' ")
								.toString(), startDate.toDate(),
						endDate.toDate(), "%Cannot parse POST parameters%")) {

			ErrorInfo error = new ErrorInfo();

			error.addSidLink(generateSidLink(errorMap.get("MIN_SID")));
			error.addSidLink(generateSidLink(errorMap.get("MAX_SID")));
			error.setMessage(errorMap.get("MESSAGE").toString());

			errors.add(error);

			// 此部分有数据余威处理
			error.setPrincipalNo("00078588");
			this.coders.add(error.getPrincipalNo());
			error.setPrincipalName(this.getPrincipalName(error.getPrincipalNo()));
		}
	}

	private void fullNormalErrors(DateTime startDate, DateTime endDate,
			List<ErrorInfo> errors) {

		for (ErrorInfo error : errors) {
			for (Map<String, Object> sids : this
					.getDataMartJdbcOperations()
					.queryForList(
							new StringBuilder()
									.append("SELECT A.SID, B.SAS SAS, B.CODERS CODERS ")
									.append("FROM RPTLOG.PROD_ERROR_INFO A ")
									.append("  LEFT JOIN RPTLOG.ICUSTOMER_FUNCTIONS B ON A.URI = B.URI ")
									.append("WHERE CREATE_DATE BETWEEN ? AND ? ")
									.append("  AND MESSAGE = ? AND A.URI = ? AND LENGTH(TRACE) = ? ")
									.append("ORDER BY MESSAGE ").toString(),
							startDate.toDate(), endDate.toDate(),
							error.getMessage(), error.getUri(),
							error.getTraceLength())) {

				error.addSidLink(generateSidLink(sids.get("SID")));

				String coders = StringUtils.defaultString((String) sids
						.get("CODERS"));
				for (String coder : StringUtils.split(coders, ",")) {
					this.coders.add(StringUtils.trim(coder));
					if (StringUtils.isEmpty(error.getPrincipalName())) {

						if (StringUtils.isNotEmpty(coder)) {

							if ("VS".equalsIgnoreCase(coder)) {
								error.setPrincipalName("昱胜");
							}else if ("ZMZ".equalsIgnoreCase(coder)) {
								error.setPrincipalName("赵明主");
							}
							else {
								error.setPrincipalName(getPrincipalName(coder));
							}
						}
					}
				}

				String sas = StringUtils
						.defaultString((String) sids.get("SAS"));
				for (String sa : StringUtils.split(sas, ",")) {
					this.sas.add(StringUtils.trim(sa));
				}
			}
		}
	}

	private <T> String generateSidLink(T sid) {
		return String.format("<a href='%s%s'>%s</a>", viewErrorUrl, sid, sid);
	}

	private String generateCountLink(String sidLink, int count) {
		return StringUtils.replace(sidLink,
				StringUtils.substringBetween(sidLink, ">", "</a>") + "</",
				String.valueOf(count) + "</");
	}

	private String getPrincipalName(String no) {
		List<Map<String, Object>> principal = this
				.getDataMartJdbcOperations()
				.queryForList(
						"SELECT EMP_NAME NAME FROM BASERPT.EMP WHERE EMP_ID = ?",
						no);
		if (principal.size() > 0) {
			return principal.get(0).get("NAME").toString();
		}
		return null;
	}

}
