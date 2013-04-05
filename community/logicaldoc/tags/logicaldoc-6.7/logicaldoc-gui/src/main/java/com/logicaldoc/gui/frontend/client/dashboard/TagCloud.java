package com.logicaldoc.gui.frontend.client.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.logicaldoc.gui.common.client.beans.GUITag;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This represent a tag cloud using a 3-D ball.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagCloud extends VLayout {

	private List<GUITag> tags;

	private int maxNumberOfTags;// the number of tags shown in the cloud.

	private HTMLFlow container = null;

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

		if (container != null)
			removeMember(container);

		container = new HTMLFlow() {

			@Override
			public String getInnerHTML() {
				return "<iframe src='tagcloud/cloud.jsp' style='border: 0px solid white; width:100%; height:"+TagCloud.this.getHeight()+";' height='"+TagCloud.this.getHeight()+"' scrolling='no'>";
			}

		};

		addMember(container);
	}

	public int getMaxNumberOfWords() {
		return maxNumberOfTags;
	}

	public void setMaxNumberOfWords(int numberOfWords) {
		this.maxNumberOfTags = numberOfWords;
	}
}