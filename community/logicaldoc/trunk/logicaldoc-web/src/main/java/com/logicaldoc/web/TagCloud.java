package com.logicaldoc.web;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.NavigationBean;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.search.KeywordsBean;

/**
 * This class is a TagCloud
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @version $Id:$
 * @since 3.5
 */
public class TagCloud {

	private String keyword;

	private Integer occurence;

	private int scale;

	public TagCloud(String keyword, int occurence) {
		this.keyword = keyword;
		this.occurence = occurence;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getOccurence() {
		return occurence;
	}

	public void setOccurence(Integer occurence) {
		this.occurence = occurence;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	/**
	 * Handles the selection of this tagcloud. Display the pageContent
	 * "search/tags" and set the selected word on KeywordsBean.
	 */
	public String select() {
		Application application = FacesContext.getCurrentInstance().getApplication();

		NavigationBean navigation = ((NavigationBean) application.createValueBinding("#{navigation}").getValue(
				FacesContext.getCurrentInstance()));

		PageContentBean content = new PageContentBean("message", "search/tags");
		content.setContentTitle(Messages.getMessage("db.tags"));
		content.setIcon(StyleBean.getImagePath("tags.png"));
		navigation.setSelectedPanel(content);

		KeywordsBean keywordsBean = ((KeywordsBean) application.createValueBinding("#{keywords}").getValue(
				FacesContext.getCurrentInstance()));
		keywordsBean.select(this.keyword);

		return null;
	}
}