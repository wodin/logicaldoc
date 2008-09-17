package com.logicaldoc.core.searchengine;

import java.util.Date;

/**
 * Search result
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.5
 */
public interface Result {
	public Integer getDocId();

	public String getName();

	public String getSummary();

	public String getPath();

	public int getMenuId();

	public String getType();

	public String getIcon();

	public long getSize();

	public Integer getScore();

	public Integer getRed();

	public boolean isRelevant(SearchOptions opt, String sourceDate);

	public int getLengthCategory();

	public Date getDate();

	public int getDateCategory();

	public Date getSourceDate();

	public int getDocType();

	public void setIcon(String icon);

	public void setMenuId(int menuId);

	public void setName(String name);

	public void setPath(String path);

	public void setDate(Date date);

	public void setSize(int sze);

	public void setType(String typ);

	public void setSummary(String summary);

	public void createScore(float score);
}