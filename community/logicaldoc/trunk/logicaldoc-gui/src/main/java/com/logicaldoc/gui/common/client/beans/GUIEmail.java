package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * GUI representation of an e-mail to be sent
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIEmail implements Serializable {

	private static final long serialVersionUID = 1L;

	private String recipients;

	private String cc;

	private String subject;

	private boolean sendAsTicket = false;

	private boolean zipCompression = false;
	
	private boolean pdfConversion = false;

	private String message;

	private GUIUser user;

	private long[] docIds;

	public GUIEmail() {
	}

	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public boolean isSendAsTicket() {
		return sendAsTicket;
	}

	public void setSendAsTicket(boolean sendAsTicket) {
		this.sendAsTicket = sendAsTicket;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public GUIUser getUser() {
		return user;
	}

	public void setUser(GUIUser user) {
		this.user = user;
	}

	public long[] getDocIds() {
		return docIds;
	}

	public void setDocIds(long[] docIds) {
		this.docIds = docIds;
	}

	public boolean isZipCompression() {
		return zipCompression;
	}

	public void setZipCompression(boolean zipCompression) {
		this.zipCompression = zipCompression;
	}

	public boolean isPdfConversion() {
		return pdfConversion;
	}

	public void setPdfConversion(boolean pdfConversion) {
		this.pdfConversion = pdfConversion;
	}
}
