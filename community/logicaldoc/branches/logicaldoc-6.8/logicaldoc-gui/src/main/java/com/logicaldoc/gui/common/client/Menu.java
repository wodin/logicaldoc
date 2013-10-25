package com.logicaldoc.gui.common.client;

import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.gui.common.client.beans.GUIUser;

/**
 * Stores the accessible menues
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Menu {
	public static final long LAST_CHANGES = -2;

	public static final long ADMINISTRATION = 2;

	public static final long DOCUMENTS = 1500;

	public static final long SEARCH = 1510;

	public static final long DASHBOARD = 1520;

	public static final long CONTACTS = 1530;

	public static final long SECURITY = 9;

	public static final long CLIENTS = 3;

	public static final long SETTINGS = 7;

	public static final long IMPEX = 8;

	public static final long CUSTOM_ID = 17;

	public static final long METADATA = 25;

	public static final long WORKFLOW = 23;

	public static final long BARCODES = 30;

	public static final long REPORTS = 16;

	public static final long OFFICE = -1090;

	public static final long CLUSTERING = -1110;

	public static final long SUBSCRIPTIONS = -1120;

	public static final long CALENDAR_REPORT = -2060;

	public static final long GDOCS = -2080;

	private static Set<Long> menues = new HashSet<Long>();

	static public void init(GUIUser user) {
		menues.clear();
		for (long menu : user.getMenues()) {
			menues.add(menu);
		}
	}

	public static boolean enabled(long menu) {
		return menues.contains(menu);
	}
}