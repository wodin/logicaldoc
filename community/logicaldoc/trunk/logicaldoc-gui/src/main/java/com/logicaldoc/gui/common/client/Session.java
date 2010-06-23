package com.logicaldoc.gui.common.client;

import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;

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
		try {
			this.session = session;
			I18N.setLocale(session.getUser().getLanguage());
			I18N.initBundle(session.getBundle());
			if (session.isLoggedIn()) {
				for (SessionObserver listener : sessionObservers) {
					listener.onUserLoggedIn(session.getUser());
				}
			}
		} catch (Throwable caught) {
			Log.serverError(caught);
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
}