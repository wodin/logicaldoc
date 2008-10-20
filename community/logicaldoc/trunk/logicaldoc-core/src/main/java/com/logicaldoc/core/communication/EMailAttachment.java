package com.logicaldoc.core.communication;

import java.io.File;

/**
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 */
public class EMailAttachment {

	private String icon = "";

	private File file;

	private String mimeType = "";

	public EMailAttachment() {
	}

	public File getFile() {
		return file;
	}

	public String getIcon() {
		return icon;
	}

	public void setFile(File file) {
		this.file = file;
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
}
