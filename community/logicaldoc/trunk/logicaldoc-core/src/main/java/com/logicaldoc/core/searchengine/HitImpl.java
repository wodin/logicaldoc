package com.logicaldoc.core.searchengine;

import java.io.Serializable;
import java.util.Date;

import com.logicaldoc.core.document.AbstractDocument;
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

	private String customId;

	private long size = 0;

	private Long docRef;

	private Date date = new Date();

	private Date sourceDate = null;

	private Date creation = null;

	private int score = 0;

	private String source;

	private String path;

	private String comment;

	private String folderName;

	private int published = 1;

	private String language;
	
	private String content;

	public HitImpl() {
	}

	@Override
	public long getDocId() {
		return docId;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getIcon() {
		String icon = IconSelector.selectIcon("");
		try {
			icon = IconSelector.selectIcon(getType());
		} catch (Exception e) {
		}
		return icon;
	}

	@Override
	public long getSize() {
		return size;
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public void setDocRef(Long docRef) {
		this.docRef = docRef;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void setSummary(String summ) {
		summary = summ;
	}

	@Override
	public void setDocId(long docId) {
		this.docId = docId;
	}

	@Override
	public void setType(String typ) {
		type = typ;
	}

	@Override
	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public void setDate(Date d) {
		date = d;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public Date getSourceDate() {
		return sourceDate;
	}

	@Override
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
	public Long getDocRef() {
		return docRef;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getFolderId() {
		return folderId;
	}

	public void setFolderId(long folderId) {
		this.folderId = folderId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	@Override
	public int getPublished() {
		return published;
	}

	@Override
	public void setPublished(int published) {
		this.published = published;
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public void setLanguage(String language) {
		this.language = language;
	}

	public static Hit fromDocument(AbstractDocument doc) {
		Hit hit = new HitImpl();
		hit.setComment(doc.getComment());
		hit.setCreation(doc.getCreation());
		hit.setCustomId(doc.getCustomId());
		hit.setDate(doc.getDate());
		hit.setDocId(doc.getId());
		hit.setDocRef(doc.getDocRef());
		hit.setFolderId(doc.getFolder().getId());
		hit.setFolderName(doc.getFolder().getName());
		hit.setLanguage(doc.getLanguage());
        hit.setPublished(doc.getPublished());
		hit.setSource(doc.getSource());
		hit.setSourceDate(doc.getSourceDate());
		hit.setTitle(doc.getTitle());
		return hit;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}