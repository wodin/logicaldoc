package com.logicaldoc.core;

import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.communication.Recipient;

public class EmailDummy {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		EMailSender sender = prepareEmailSender();

		EMail eml = new EMail();
		eml.setSubject("HTML test");
		eml.setHtml(true);
		Set<Recipient> recipients = new HashSet<Recipient>();

		eml.getImages().add("http://www.logicaldoc.com/templates/theme326/images/LogicalDOC_logo_website.gif");

		
		// Notify the general report
		Recipient rec = new Recipient();
		rec.setAddress("m.meschieri@logicalobjects.com");
		recipients.add(rec);
		eml.setRecipients(recipients);
		eml.setMessageText("<span style='color:red'>Test</span> di <b>HTML</b>\ncippa"
				+"<br/><image src='cid:image_1' />");
		
		sender.send(eml);
	}

	protected static EMailSender prepareEmailSender() {
		EMailSender sender = new EMailSender();
		
		return sender;
	}
}
