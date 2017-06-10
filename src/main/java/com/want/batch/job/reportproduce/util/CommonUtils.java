/**
 * 
 */
package com.want.batch.job.reportproduce.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.reportproduce.dao.SpecialDisplayActualDao;
import com.want.batch.job.reportproduce.pojo.Constant;
import com.want.component.mail.MailService;

/**
 * @author MandyZhang
 *
 */
@Component
public class CommonUtils {

	protected final Log logger = LogFactory.getLog(CommonUtils.class);
	
	@Autowired
	private MailService mailService;

	@Autowired
	public SpecialDisplayActualDao specialDisplayActualDao;
	
	/**
	 * <pre>
	 * 2013-6-21 Nash
	 * Mail 发送
	 * </pre>
	 * 
	 */
	public synchronized void sendMail(Map<String, Object> sendMailMap) {

		String sendTo = sendMailMap.get("EMP_EMAIL") == null ? "" : sendMailMap.get("EMP_EMAIL").toString();
		
		String createTime = sendMailMap.get("CREATE_DATE") == null ? "" : StringUtils.substringBefore(sendMailMap.get("CREATE_DATE").toString(), ".");
		String fileName = sendMailMap.get("FILE_NAME") == null ? "" : sendMailMap.get("FILE_NAME").toString();
		String reportName = sendMailMap.get("REPORT_NAME") == null ? "" :sendMailMap.get("REPORT_NAME").toString();
		String context = "";

		if (Constant.DIRECTIVE_STATUS_FINISH.equals(sendMailMap.get("STATUS"))) {

			context = "您" + createTime + "下载的" + fileName + "已生成完成，请及时下载";
		}
		else if (Constant.DIRECTIVE_STATUS_EXCEPTION.equals(sendMailMap.get("STATUS"))) {

			context = "您" + createTime + "下载的" + fileName + "生成时发生异常，请及时提报异常";
		}else {
			
			context = "您" + createTime + "下载的" + fileName + "生成时发生未知异常，请及时提报异常";
		}

		mailService.to(sendTo);
		mailService.subject(reportName + "的报表下载状态")
		.content(context)
		.send();
		
		logger.info(sendTo + "Mail 发送完成 !");
	}
	
	/**
	 * <pre>
	 * 2013-6-20 Nash
	 * 更新状态
	 * </pre>	
	 * 
	 *	<ol>
	 * 		<li>
	 *  	<li>
	 *	</ol>
	 * @param status
	 * @param sid
	 */
	public void updataReportStatus(String status, int sid, String exceptionReason) {

		this.specialDisplayActualDao.updataReportStatus(status, sid, exceptionReason);
	}
}
