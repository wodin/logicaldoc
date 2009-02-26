package com.logicaldoc.core.searchengine;

import java.util.Date;

/**
 * Search result
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.5
 */
public interface Result {
	public long getDocId();

	public void setDocId(long docId);
	
	public String getTitle();

	public String getSummary();

	public String getType();

	public String getIcon();

	//The file size in bytes
	public long getSize();

	public Integer getScore();

	public Integer getRed();

	public boolean isRelevant(SearchOptions opt);

	public int getLengthCategory();

	public Date getDate();
	
	public Date getCreation();

	public int getDateCategory();

	public Date getSourceDate();

	public int getDocType();

	public void setTitle(String title);

	public void setDate(Date date);
	
	public void setCustomId(String customId);
	
	public String getCustomId();
	
	public void setCreation(Date creation);

	public void setSourceDate(Date date);
	
	public void setSize(long sze);

	public void setType(String typ);

	public void setSummary(String summary);

	public void createScore(float score);
}