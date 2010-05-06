package com.logicaldoc.core.searchengine;

import java.io.Serializable;
import java.util.Date;

import com.logicaldoc.core.util.IconSelector;

/**
 * Basic implementation of a <code>Result</code>
 * 
 * @author Michael Scholz, Marco Meschieri, Alessandro Gasparini
 */
public class HitImpl implements Serializable, Hit {
	private static final long serialVersionUID = 1L;

	private long docId = 0;

	private long folderId = 0;

	private String title = "";

	private String summary = "";

	private String type = "";

	private String icon = "";

	private String customId;

	private long size = 0;

	private long docRef = 0;

	private Date date = new Date();

	private Date sourceDate = null;

	private Date creation = null;

	private int score = 0;

	private String source;

	private String path;

	public HitImpl() {
	}

	/**
	 * @see com.logicaldoc.core.searchengine.Hit.Result#getDocId()
	 */
	public long getDocId() {
		return docId;
	}

	public void setDocid(long docId) {
		this.docId = docId;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.Hit.Result#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.Hit.Result#getSummary()
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.Hit.Result#getType()
	 */
	public String getType() {
		return type;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.Hit.Result#getIcon()
	 */
	public String getIcon() {
		String icon = IconSelector.selectIcon("");
		try {
			icon = IconSelector.selectIcon(getType());
		} catch (Exception e) {
		}
		return icon;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.Hit.Result#getSize()
	 */
	public long getSize() {
		return size;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setDocRef(long docRef) {
		this.docRef = docRef;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSummary(String summ) {
		summary = summ;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public void setType(String typ) {
		type = typ;
		icon = "";

		if (type.equals("PDF")) {
			icon = "pdf.gif";
		} else if (type.equals("DOC") || type.equals("DOT") || type.equals("RTF") || type.equals("SXW")
				|| type.equals("TXT") || type.equals("WPD") || type.equals("KWD") || type.equals("ABW")
				|| type.equals("ZABW") || type.equals("ODT")) {
			icon = "textdoc.gif";
		} else if (type.equals("XLS") || type.equals("XLT") || type.equals("SXC") || type.equals("DBF")
				|| type.equals("KSP") || type.equals("ODS") || type.equals("ODB")) {
			icon = "tabledoc.gif";
		} else if (type.equals("PPT") || type.equals("PPS") || type.equals("POT") || type.equals("SXI")
				|| type.equals("KPR") || type.equals("ODP")) {
			icon = "presentdoc.gif";
		} else if (type.equals("APF") || type.equals("BMP") || type.equals("JPEG") || type.equals("DIB")
				|| type.equals("GIF") || type.equals("JPG") || type.equals("PSD") || type.equals("TIF")
				|| type.equals("TIFF")) {
			icon = "picture.gif";
		} else if (type.equals("HTM") || type.equals("HTML") || type.equals("XML")) {
			icon = "internet.gif";
		} else {
			icon = "document.gif";
		}
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setDate(Date d) {
		date = d;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.Hit.Result#getDate()
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.Hit.Result#getSourceDate()
	 */
	public Date getSourceDate() {
		return sourceDate;
	}

	public void setSourceDate(Date sourceDate) {
		this.sourceDate = sourceDate;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.Hit.Result#getDocType()
	 */
	public int getDocType() {
		if (type.equals("PDF") || type.equals("DOC") || type.equals("TXT") || type.equals("RTF") || type.equals("HTML")
				|| type.equals("HTM") || type.equals("SXW") || type.equals("WPD") || type.equals("PS")
				|| type.equals("KWD")) {
			return 0;
		}

		if (type.equals("XLS") || type.equals("SXC") || type.equals("KSP")) {
			return 1;
		}

		if (type.equals("PPT") || type.equals("PPS") || type.equals("SXI") || type.equals("KPR")) {
			return 2;
		}

		return 3;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	@Override
	public String getCustomId() {
		return customId;
	}

	@Override
	public void setCustomId(String customId) {
		this.customId = customId;
	}

	@Override
	public long getDocRef() {
		return docRef;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.Hit#getSource()
	 */
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.Hit#getPath()
	 */
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public long getFolderId() {
		return folderId;
	}

	public void setFolderId(long folderId) {
		this.folderId = folderId;
	}
}