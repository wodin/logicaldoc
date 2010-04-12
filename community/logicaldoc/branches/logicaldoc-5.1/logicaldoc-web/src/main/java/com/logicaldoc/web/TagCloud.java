package com.logicaldoc.web;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import com.logicaldoc.util.Context;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.NavigationBean;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.search.TagsBean;

/**
 * This class is a TagCloud
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.5
 */
public class TagCloud extends com.logicaldoc.core.document.TagCloud {
	public TagCloud(String tag) {
		super(tag);
	}

	/**
	 * Handles the selection of this tagcloud. Display the pageContent
	 * "search/tags" and set the selected word on TagsBean.
	 */
	@SuppressWarnings("deprecation")
	public String select() {
		Application application = FacesContext.getCurrentInstance().getApplication();

		NavigationBean navigation = ((NavigationBean) application.createValueBinding("#{navigation}").getValue(
				FacesContext.getCurrentInstance()));

		PageContentBean content = new PageContentBean("message", "search/tags");
		content.setContentTitle(Messages.getMessage("tags"));
		StyleBean style = (StyleBean) Context.getInstance().getBean(StyleBean.class);
		content.setIcon(style.getImagePath("tags.png"));
		navigation.setSelectedPanel(content);

		TagsBean tagsBean = ((TagsBean) application.createValueBinding("#{tags}").getValue(
				FacesContext.getCurrentInstance()));
		tagsBean.select(this.getTag());
		return null;
	}
}