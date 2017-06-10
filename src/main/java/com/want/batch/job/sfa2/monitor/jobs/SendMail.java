package com.want.batch.job.sfa2.monitor.jobs;

import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.component.mail.MailService;
@Component
public class SendMail {
	private static final String MAIL_TITLE = "SFA DB Monitor ";
	private static final String PROPERTIESNAME = "project";
	private static final String ADDRESSEE = "sfa.db.monitor.mail.hr.to";
	private static final String CC = "sfa.db.monitor.mail.hr.cc";
	
	@Autowired
	private MailService mailService;
	
	public void send(String warning){
		/*邮件整体*/		
		String content = getContent(warning);
		
		/*邮件title*/
		
		String subject = MAIL_TITLE;;
		
		/*获取properties文件,该文件里封装了邮件接受者*/
		ResourceBundle bundle = ResourceBundle.getBundle(PROPERTIESNAME);
		
		/*获取邮件发送factroy*/
		
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
				.content(String.format(content))
				.send();
	}
	
	/*
	 * 创建邮件内容的主体架构
	 */
	public String getContent(String warning){
		String content = new StringBuilder()
		.append("<html><body> <span style='color:#228B22'> Dear All:</span> <br> <hr>" +
				"<table border='1' bgcolor='#F2F2F2' style=text-align:center>").append("<tr>")
		.append("<td colspan='2' bgcolor='#FF6347' ><b>"+warning+"</b></td>")
		.append("</tr>")
		.append("</table></body></html>").toString();
		
		
		return content;
	
	}

	protected MailService getMailService() {
		return mailService;
	}
	
	protected void setMailService(MailService mailService) {
		this.mailService = mailService;
	}
}
