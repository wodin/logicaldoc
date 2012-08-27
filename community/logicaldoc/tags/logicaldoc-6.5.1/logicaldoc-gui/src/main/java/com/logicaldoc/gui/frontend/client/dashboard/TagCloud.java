package com.logicaldoc.gui.frontend.client.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.http.client.URL;
import com.logicaldoc.gui.common.client.beans.GUITag;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This represent a tag cloud using a 3-D ball.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagCloud extends VLayout {

	private VLayout container;

	private List<GUITag> tags;

	private int maxNumberOfTags;// the number of tags shown in the cloud.

	private HTMLFlow html = null;

	public TagCloud() {
		tags = new ArrayList<GUITag>();
	}

	/**
	 * Set the whole list of tags given in parameter to be the current tags
	 * list.
	 * 
	 * @param tags
	 */
	public void setTags(List<GUITag> tags) {
		if (this.tags == null)
			this.tags = new ArrayList<GUITag>();
		this.tags.clear();
		if (tags != null)
			this.tags.addAll(tags);
	}

	/**
	 * Retrieve the list of tags in the cloud.
	 * 
	 * @return
	 */
	public List<GUITag> getTags() {
		return tags;
	}

	/**
	 * Add a word to the tagcloud list.
	 * 
	 * @param word
	 */
	public void addWord(GUITag word) {
		boolean exist = false;
		for (GUITag t : tags) {
			if (t.getTag().equalsIgnoreCase(word.getTag())) {
				t.setCount(t.getCount() + 1);
				exist = true;
			}
		}
		if (!exist)
			tags.add(word);
		refresh();
	}

	/**
	 * Refresh the display of the tag cloud. Usually used after an adding or
	 * deletion of word.
	 */
	public void refresh() {
		String tcloud = "<tags>";
		for (GUITag w : tags) {
			tcloud += "<a href='" + URL.encode(w.getLink()) + "' style='" + (w.getScale() + 4)
					+ "' color='0x000000' hicolor='0x314976'>" + w.getTag() + "</a>";
			;
		}
		tcloud += "</tags>";
		tcloud = "tcolor=0x111111&tcolor2=0x336699&hicolor=0x&tspeed=100&distr=true&mode=both&tagcloud=" + tcloud;

		if (container != null)
			removeMember(container);
		container = new VLayout();
		container.setWidth("95%");
		container.setHeight(getHeight() - 20);
		addMember(container);

		html = new HTMLFlow(Util.flashHTML("tagcloud.swf", getWidth() - 40, getHeight() - 40, tcloud));
		container.addMember(html);
	}

	public int getMaxNumberOfWords() {
		return maxNumberOfTags;
	}

	public void setMaxNumberOfWords(int numberOfWords) {
		this.maxNumberOfTags = numberOfWords;
	}
}