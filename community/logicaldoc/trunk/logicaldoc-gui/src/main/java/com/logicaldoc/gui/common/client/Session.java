package com.logicaldoc.gui.common.client;

import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;

/**
 * Represents a client work session
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Session {
	private static Session instance;

	private GUISession session;

	private GUIFolder currentFolder;

	private Set<SessionObserver> sessionObservers = new HashSet<SessionObserver>();

	private Set<FolderObserver> folderObservers = new HashSet<FolderObserver>();

	private String language;

	public static Session get() {
		if (instance == null)
			instance = new Session();
		return instance;
	}

	public String getSid() {
		return session.getSid();
	}

	public void close() {
		session = null;
		sessionObservers.clear();
		instance = null;
	}

	public GUIUser getUser() {
		return session.getUser();
	}

	public void init(GUISession session) {
		this.session = session;
		setLanguage(session.getUser().getLanguage());
		if (session.isLoggedIn()) {
			for (SessionObserver listener : sessionObservers) {
				listener.onUserLoggedIn(session.getUser());
			}
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
		return session.getFeatures();
	}

	public boolean isFeatureEnabled(String feature) {
		for (String f : session.getFeatures()) {
			if (f.equals(feature))
				return true;
		}
		return false;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}