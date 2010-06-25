package com.logicaldoc.gui.common.client;

import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.gui.common.client.beans.GUIInfo;

/**
 * Retrieves i18n resources
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Feature {

	public static final int SAVED_SEARCHES = 12;
	public static final int PREVIEW = 51;
	public static final int WORKFLOW_BASIC = 52;
	public static final int BOOKMARKS = 53;
	

	private static Set<String> features = new HashSet<String>();

	static public void init(GUIInfo info) {
		features.clear();
		for (String feature : info.getFeatures()) {
			features.add(feature);
		}
	}

	public static boolean enabled(int feature) {
		String key = "Feature_" + feature;
		return features.contains(key);
	}

	public static boolean visible(int feature) {
		String key = "Feature_" + feature;
		if (features.contains(key))
			return true;
		else
			return showDisabled();
	}

	/**
	 * Check if a disabled feature must be visible(ad disabled) or hidden
	 */
	public static boolean showDisabled() {
		return enabled(50);
	}
}