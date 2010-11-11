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

	public static final int ADMINISTRATION = 2;

	public static final int SECURITY = 9;

	public static final int LAST_CHANGES = -2;

	public static final int CLIENTS = 3;

	public static final int IMPEX = 8;

	public static final int CUSTOM_ID = 17;

	public static final int METADATA = 25;

	public static final int SETTINGS = 24;

	public static final int WORKFLOW = 23;

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