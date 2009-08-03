package com.logicaldoc.core.communication;

/**
 * SystemMessages are messages which an user only can send to other system
 * users.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public class SystemMessage extends Message {

	private String recipient = "";

	private int dateScope = 10;

	private int prio = 0;

	private int confirmation = 0; // 0 - false; 1 - true

	public SystemMessage() {
	}

	public String getRecipient() {
		return recipient;
	}

	public int getDateScope() {
		return dateScope;
	}

	public int getPrio() {
		return prio;
	}

	public int getConfirmation() {
		return confirmation;
	}

	public void setRecipient(String rec) {
		recipient = rec;
	}

	public void setDateScope(int scope) {
		dateScope = scope;
	}

	public void setPrio(int pri) {
		prio = pri;
	}

	public void setConfirmation(int conf) {
		confirmation = conf;
	}

}
