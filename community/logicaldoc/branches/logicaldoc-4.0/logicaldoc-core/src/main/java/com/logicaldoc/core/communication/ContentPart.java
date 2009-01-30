package com.logicaldoc.core.communication;

/**
 * 
 * @author Michael Scholz
 */
public class ContentPart {
	private int messageId;

	private String fileName;

	private String mimeType;

	private byte[] content;

	/** Creates a new instance of ContentPart */
	public ContentPart() {
		messageId = 0;
		mimeType = "";
		content = null;
	} // end ctor ContentPart

	public int getMessageId() {
		return messageId;
	} // end method getMessageId

	public String getFileName() {
		return fileName;
	} // end method getFileName

	public String getMimeType() {
		return mimeType;
	} // end method getMimeType

	public byte[] getContent() {
		return content;
	} // end method getContent

	public void setMessageId(int id) {
		messageId = id;
	} // end method setMessageId

	public void setFileName(String fname) {
		fileName = fname;
	} // end method setFileName

	public void setMimeType(String type) {
		mimeType = type;
	} // end method setMimeType

	public void setContent(byte[] cntnt) {
		content = cntnt;
	} // end method setContent

	public long getContentSize() {
		return content.length;
	} // end method getContentSize
} // end class ContentPart
