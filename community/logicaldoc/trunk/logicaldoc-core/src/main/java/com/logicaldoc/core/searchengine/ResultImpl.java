package com.logicaldoc.core.searchengine;

import java.io.Serializable;
import java.util.Date;

import com.logicaldoc.core.util.IconSelector;

/**
 * Basic implementation of a <code>Result</code>
 * 
 * @author Michael Scholz, Marco Meschieri, Alessandro Gasparini
 */
public class ResultImpl implements Serializable, Result {
	private static final long serialVersionUID = 1L;

	private Long docId = new Long(0);

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

	private Integer length = new Integer(0);

	private Integer score = new Integer(0);

	private Integer red = new Integer(0);

	private String source;

	private String path;

	public ResultImpl() {
	}

	/**
	 * @see com.logicaldoc.core.searchengine.search.Result#getDocId()
	 */
	public long getDocId() {
		return docId;
	}

	public void setDocid(long docId) {
		this.docId = docId;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.search.Result#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.search.Result#getSummary()
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.search.Result#getType()
	 */
	public String getType() {
		return type;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.search.Result#getIcon()
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
	 * @see com.logicaldoc.core.searchengine.search.Result#getSize()
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.search.Result#getGreen()
	 */
	public Integer getScore() {
		return score;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.search.Result#getRed()
	 */
	public Integer getRed() {
		return red;
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

	public void createScore(float score) {
		float temp = score * 100;
		int tgreen = Math.round(temp);

		if (tgreen < 1) {
			tgreen = 1;
		}

		this.score = new Integer(tgreen);
		temp = 100 - (score * 100);

		int tred = Math.round(temp);

		if (tred > 99) {
			tred = 99;
		}

		red = new Integer(tred);
	}

	/**
	 * @see com.logicaldoc.core.searchengine.search.Result#isRelevant(com.logicaldoc.core.searchengine.search.SearchOptions,
	 *      java.util.Date)
	 */
	public boolean isRelevant(SearchOptions opt) {
		boolean result = true;

		if ((opt.getFormat() != null) && !opt.getFormat().equals("all")) {
			if (!type.toLowerCase().equals(opt.getFormat())) {
				result = false;
			}
		}

		if (opt.getSizeMin() != null && size < opt.getSizeMin().longValue())
			result = false;

		if (opt.getSizeMax() != null && size > opt.getSizeMax().longValue())
			result = false;

		if (opt.getCreationFrom() != null) {
			if (creation.before(opt.getCreationFrom()))
				result = false;
		}

		if (opt.getCreationTo() != null) {
			if (creation.after(opt.getDateTo()))
				result = false;
		}

		if (opt.getDateTo() != null) {
			if (date.after(opt.getDateTo()))
				result = false;
		}

		if (opt.getDateFrom() != null && date != null) {
			if (date.before(opt.getDateFrom()))
				result = false;
		}

		if (opt.getSourceDateFrom() != null && sourceDate != null) {
			if (sourceDate.before(opt.getSourceDateFrom()))
				result = false;
		}

		if (opt.getSourceDateTo() != null && sourceDate != null) {
			if (sourceDate.after(opt.getSourceDateTo()))
				result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.search.Result#getLengthCategory()
	 */
	public int getLengthCategory() {
		int len = length.intValue();

		if (len > 60000) {
			return 5;
		}

		if (len > 18000) {
			return 4;
		}

		if (len > 3000) {
			return 3;
		}

		if (len > 600) {
			return 2;
		}

		return 1;
	}

	public void setDate(Date d) {
		date = d;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.search.Result#getDate()
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.search.Result#getDateCategory()
	 */
	public int getDateCategory() {
		long diff = new Date().getTime() - date.getTime();
		long days = diff / 1000 / 60 / 60 / 24; // 1000-sec , 60-min , 60-h ,
		// 24-day

		if (days < 8) {
			return 0;
		}

		if (days < 29) {
			return 1;
		}

		if (days < 92) {
			return 2;
		}

		if (days < 366) {
			return 3;
		}

		return 4;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.search.Result#getSourceDate()
	 */
	public Date getSourceDate() {
		return sourceDate;
	}

	public void setSourceDate(Date sourceDate) {
		this.sourceDate = sourceDate;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.search.Result#getDocType()
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
	 * @see com.logicaldoc.core.searchengine.Result#getSource()
	 */
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @see com.logicaldoc.core.searchengine.Result#getPath()
	 */
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}