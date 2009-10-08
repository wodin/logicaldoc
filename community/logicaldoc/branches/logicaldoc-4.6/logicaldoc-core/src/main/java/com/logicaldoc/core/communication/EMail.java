package com.logicaldoc.core.communication;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.mail.internet.AddressException;
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

	private Set<Recipient> recipientsCC = new HashSet<Recipient>();

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
		return getAddresses(recipients);
	}

	public InternetAddress[] getAddressesCC() throws Exception {
		return getAddresses(recipientsCC);
	}

	private InternetAddress[] getAddresses(Collection<Recipient> recipients) throws AddressException {
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

	public void parseRecipients(String str) {
		xx(str, recipients);
	}

	public void parseRecipientsCC(String str) {
		xx(str, recipientsCC);
	}

	private void xx(String str, Collection<Recipient> recipients) {
		StringTokenizer st = new StringTokenizer(str.trim().toLowerCase(), ", ;", false);
		recipients.clear();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			Recipient recipient = new Recipient();
			recipient.setAddress(token);
			recipient.setName(token);
			recipients.add(recipient);
		}
	}

	public Set<Recipient> getRecipientsCC() {
		return recipientsCC;
	}

	public void setRecipientsCC(Set<Recipient> recipientsCC) {
		this.recipientsCC = recipientsCC;
	}
}
