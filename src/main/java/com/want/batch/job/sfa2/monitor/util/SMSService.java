package com.want.batch.job.sfa2.monitor.util;
import java.util.Date;

import com.want.batch.job.utils.ProjectConfig;

public class SMSService{
	private static String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	"<ns0:MT_INPUT xmlns:ns0=\"http://pi.want-want.com/mas\">" +
	"<action>sendSM</action><content>{content}</content>" +
	"<mobiles>{mobile}</mobiles>" +
	"<sendTime/></ns0:MT_INPUT>";	
	
	 private static String masurl="http://10.0.0.157:50600/sap/xi/adapter_plain?namespace=http%3A//pi.want-want.com/mas&interface=SI_O_MAS&service=BS_SMS_P&party=&agency=&scheme=&QOS={qos}&sap-user=00127934&sap-password=123456&sap-client=820&sap-language=EN";


	//CLIENT执行慢，PI接到后会高优先尽快地去执行。
	public static final String QOS_BEST_EFFORT="BE";
	
	//默认此模式，CLIENT执行快，PI接到后会在序列中等待执行。	
	public static final String QOS_EXTACTLY_ONCE="EO";	
	
	public SMSService ()
	{
		masurl = masurl.replace("{qos}",QOS_EXTACTLY_ONCE);
	}
	
	public SMSService (String qos)
	{
		masurl = masurl.replace("{qos}",qos);
	}	
	
	public static void sendSMS(String content,String[] mobiles)
	{
		xml = xml.replace("{content}",content);
		for(int i=0;i<mobiles.length;i++)
		{			
			String msg = xml.replace("{mobile}", mobiles[i]);
			HttpUtil.postRequestWithoutCheck(masurl,msg,"UTF-8");
		}	
	}	
	
	public static String[] getMobiles(){
		String mobiles = ProjectConfig.getInstance().getString("sfa.db.monitor.sms");
		return mobiles.split(",");
	}
	
	public static void main(String[] args)
	{
		  System.out.println("start time:"+new Date());
		  String mobiles[] = {"18701733926"}; 
		  String content = "12121212。";
		  SMSService sms = new SMSService();
	      sms.sendSMS(content,mobiles);
		  System.out.println("end time:"+new Date());
	}
}
