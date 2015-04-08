package com.logicaldoc.core.communication;

/**
 * @author Michael Scholz
 */
public class Recipient {
	public final static int TYPE_SYSTEM = 0;

	public final static int TYPE_EMAIL = 1;

	public final static String MODE_EMAIL_TO = "TO";

	public final static String MODE_EMAIL_CC = "CC";

	public final static String MODE_EMAIL_BCC = "BCC";

	// The login
	private String name = "";

	// The system login or the email address
	private String address = "";

	// The recipient mode (for the system message is not useful, for the email
	// can be To, CC, CCN, ecc.)
	private String mode = MODE_EMAIL_TO;

	// The recipient type (i.e. system, user, group, email)
	private int type = TYPE_SYSTEM;

	private int read = 0;

	public Recipient() {
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public void setName(String nme) {
		name = nme;
	}

	public void setAddress(String addr) {
		address = addr;
	}

	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof Recipient))
			return false;
		Recipient other = (Recipient) arg0;
		return other.getAddress().equals(address);
	}

	@Override
	public int hashCode() {
		return address.hashCode();
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Recipient cloned = new Recipient();
		cloned.setAddress(getAddress());
		cloned.setMode(getMode());
		cloned.setName(getName());
		cloned.setRead(getRead());
		cloned.setType(getType());
		return cloned;
	}

	@Override
	public String toString() {
		return address;
	}
}
