package com.logicaldoc.core.communication;

/**
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 */
public class EMailAttachment {

	private String icon = "";

	private byte[] data;

	private String mimeType = "";

	private String fileName = "";

	public EMailAttachment() {
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String string) {
		mimeType = string;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
