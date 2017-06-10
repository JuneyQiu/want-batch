package com.want.batch.job.business.newpromotional;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
*  发送短信
* <pre>
* 歷史紀錄：
* 2012-06-07  yu_chao.myc
* 	新建檔案
* </pre>
* 
* @author 
* <pre>
* SD
* 	
* PG
*	yu_chao.myc
* UT
*
* MA
* </pre>
* @version $Rev$
*
* <p/> $Id$
*
*/
public class SMSService {
	private Log logger=LogFactory.getLog(this.getClass());
	private String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			       + "<ns0:MT_INPUT xmlns:ns0=\"http://pi.want-want.com/mas\">"
			       + "<action>sendSM</action><content>{content}</content>"
			       + "<mobiles>{mobile}</mobiles>" 
			       + "<sendTime/></ns0:MT_INPUT>";
	/**
	 * <pre>
	 * 创建实体 
	 *    读取配置文件
	 *   2012-06-07 
	 *   yu_chao.myc
	 * </pre>
	 */
	public SMSService() {
		logger.info("-------------- Start sending text messages !");
	}

	/**
	 * 发送短信
	 * 2012-06-07 
	 * @param content
	 * @param mobiles
	 */
	public void sendSMS(String requestSendMessageUrl,String content, List<String> mobiles) {
		xml = xml.replace("{content}", content);
		for (String mobileNum : mobiles) {
			//更换mobile phone num
			String msg = xml.replace("{mobile}",mobileNum);
			logger.info("-------------send message to "+mobileNum);
			//请求webservice 并发送短信
			HttpUtil.postRequestWithoutCheck(requestSendMessageUrl, msg, "UTF-8");
		}
	}
}
