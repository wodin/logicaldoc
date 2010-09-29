package com.logicaldoc.gui.common.client;

import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.gui.common.client.beans.GUIInfo;

/**
 * Stores the enabled features
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Feature {

	public static final int IMPORT_REMOTE_FOLDERS = 1;

	public static final int ARCHIVES = 4;
	
	public static final int TAGS = 5;

	public static final int DIGITAL_SIGN = 7;
	
	public static final int PDF = 8;

	public static final int RSS = 9;

	public static final int EMAIL_IMPORT = 10;

	public static final int ACTIVEDIR = 11;

	public static final int SAVED_SEARCHES = 12;

	public static final int MESSAGES = 13;
	
	public static final int INCREMENTAL_ARCHIVES = 14;
	
	public static final int PAPER_DEMATERIALIZATION = 14;

	public static final int SCAN = 16;

	public static final int AUDIT = 17;

	public static final int WORKFLOW = 19;
	
	public static final int OFFICE = 20;

	public static final int SHOW_LICENSEE = 40;
	
	public static final int SHOW_DISABLED = 50;
	
	public static final int PREVIEW = 51;

	public static final int BOOKMARKS = 53;

	public static final int CUSTOMID = 54;

	public static final int LDAP = 55;

	public static final int TEMPLATE = 56;

	public static final int CLIENT_TOOLS = 57;

	public static final int WEBSERVICE = 58;

	public static final int WEBDAV = 59;

	public static final int SHARE_DISCOVERY = 60;

	public static final int IMPORT_LOCAL_FOLDERS = 61;
	
	public static final int PARAMETRIC_SEARCHES = 62;

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
		return enabled(SHOW_DISABLED);
	}
	
	/**
	 * Check if the licensee must be shown
	 */
	public static boolean showLicensee() {
		return enabled(SHOW_DISABLED);
	}	
}