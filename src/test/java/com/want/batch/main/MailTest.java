package com.want.batch.main;

import org.junit.Test;

import com.want.component.mail.MailService;

public class MailTest extends AbstractWantMainTest {

	@Test
	public void testSend() {
		MailService mailService = applicationContext.getBean("mailService", MailService.class);
		
		mailService
			.to("chen_yi4@want-want.com")
			.cc("song_wenlei@want-want.com", "wu_li@want-want.com")
			.subject("Test send mail... ")
			.content("test mail ... ")
			.send();
	}
}
