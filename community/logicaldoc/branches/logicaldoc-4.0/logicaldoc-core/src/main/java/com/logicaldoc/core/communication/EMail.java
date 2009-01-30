package com.logicaldoc.core.communication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.InternetAddress;

/**
 * @author Michael Scholz
 * @author Marco Meschieri - Logical Objects
 */
public class EMail extends Message {

	private String authorAddress = "";

	private String userName = "";

	private String folder = "";

	// Refers to the original email id
	private String emailId = "";

	// The e-mail account used to fetch this message
	private long accountId;

	private Set<Recipient> recipients = new HashSet<Recipient>();

	private Map<Integer, EMailAttachment> attachments = new HashMap<Integer, EMailAttachment>();

	public EMail() {
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public String getAuthorAddress() {
		return authorAddress;
	}

	public String getUserName() {
		return userName;
	}

	public Set<Recipient> getRecipients() {
		return recipients;
	}

	public void setAuthorAddress(String address) {
		authorAddress = address;
	}

	public void setUserName(String uname) {
		userName = uname;
	}

	public void addRecipient(Recipient rec) {
		recipients.add(rec);
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String string) {
		folder = string;
	}

	public Map<Integer, EMailAttachment> getAttachments() {
		return attachments;
	}

	public EMailAttachment getAttachment(int partId) {
		return attachments.get(partId);
	}

	public void addAttachment(int partId, EMailAttachment attachment) {
		attachments.put(partId, attachment);
	}

	public InternetAddress[] getAddresses() throws Exception {
		InternetAddress[] recs = new InternetAddress[recipients.size()];
		Iterator<Recipient> iter = recipients.iterator();
		int i = 0;

		while (iter.hasNext()) {
			Recipient rec = iter.next();
			recs[i] = new InternetAddress(rec.getAddress());
			i++;
		}

		return recs;
	}

	public int getAttachmentCount() {
		return attachments.size();
	}

	public void setRecipients(Set<Recipient> recipients) {
		this.recipients = recipients;
	}

	public void setAttachments(Map<Integer, EMailAttachment> attachments) {
		this.attachments = attachments;
	}
}
