package com.logicaldoc.core.searchengine;

import java.util.Date;

/**
 * Search result
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 5.2
 */
public interface Hit {

	public long getDocId();

	// If the document is a shortcut, this is the original document id
	public Long getDocRef();

	public void setDocId(long docId);

	public String getTitle();

	public String getSummary();

	public String getType();

	public String getIcon();

	// The file size in bytes
	public long getSize();

	public int getScore();

	public Date getDate();

	public Date getCreation();

	public Date getSourceDate();

	public int getDocType();

	public String getSource();

	public String getPath();

	public long getFolderId();

	public void setTitle(String title);

	public void setDate(Date date);

	public void setCustomId(String customId);

	public String getCustomId();

	public void setCreation(Date creation);

	public void setSourceDate(Date date);

	public void setSize(long sze);

	public void setType(String typ);

	public void setSummary(String summary);

	public void setSource(String source);

	public void setPath(String path);

	public void setScore(int score);

	public void setIcon(String icon);

	public void setFolderId(long folderId);

	public void setDocRef(Long docRef);
}