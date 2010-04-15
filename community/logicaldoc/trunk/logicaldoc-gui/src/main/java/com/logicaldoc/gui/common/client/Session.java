package com.logicaldoc.gui.common.client;

import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIUser;

/**
 * Represents a client work session
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Session {
	private static Session instance;

	private GUIUser user;

	private GUIFolder currentFolder;

	private Set<SessionObserver> sessionObservers = new HashSet<SessionObserver>();

	private Set<FolderObserver> folderObservers = new HashSet<FolderObserver>();

	public static Session getInstance() {
		if (instance == null)
			instance = new Session();
		return instance;
	}

	public String getSid() {
		return user.getSid();
	}

	public void close() {
		user = null;
		sessionObservers.clear();
		instance = null;
	}

	public GUIUser getUser() {
		return user;
	}

	public void setUser(GUIUser user) {
		this.user = user;
		for (SessionObserver listener : sessionObservers) {
			listener.onUserLoggedIn(user);
		}
	}

	public void addSessionObserver(SessionObserver observer) {
		sessionObservers.add(observer);
	}

	public void addFolderObserver(FolderObserver observer) {
		folderObservers.add(observer);
	}

	public GUIFolder getCurrentFolder() {
		return currentFolder;
	}

	public void setCurrentFolder(GUIFolder folder) {
		this.currentFolder = folder;
		for (FolderObserver listener : folderObservers) {
			listener.onFolderSelect(folder);
		}
	}

	public String[] getFeatures() {
		return user.getFeatures();
	}

	public boolean isFeatureEnabled(String feature) {
		for (String f : user.getFeatures()) {
			if (f.equals(feature))
				return true;
		}
		return false;
	}
}