package com.logicaldoc.gui.common.client;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.services.InfoService;
import com.logicaldoc.gui.common.client.services.InfoServiceAsync;
import com.logicaldoc.gui.common.client.util.WindowUtils;

/**
 * Represents a client work session
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Session {
	private static Session instance;

	private InfoServiceAsync service = (InfoServiceAsync) GWT.create(InfoService.class);

	private GUIInfo info;

	private GUISession session;

	private GUIFolder currentFolder;

	private GUIWorkflow currentWorkflow = null;

	private Set<SessionObserver> sessionObservers = new HashSet<SessionObserver>();

	private Set<FolderObserver> folderObservers = new HashSet<FolderObserver>();

	private Timer timer;

	public static Session get() {
		if (instance == null)
			instance = new Session();
		return instance;
	}

	public boolean isDemo() {
		return "demo".equals(info.getRunLevel());
	}

	public boolean isDevel() {
		return "devel".equals(info.getRunLevel());
	}

	public String getSid() {
		if (session != null)
			return session.getSid();
		else
			return null;
	}

	public String getIncomingMessage() {
		if (session != null)
			return session.getIncomingMessage();
		else
			return null;
	}

	public void close() {
		session = null;
		sessionObservers.clear();
		if (timer != null)
			timer.cancel();
	}

	public GUIUser getUser() {
		return session.getUser();
	}

	public void init(GUISession session) {
		try {
			this.session = session;
			I18N.init(session);
			WindowUtils.setSid(session.getSid(), I18N.message("leavingpage"));
			Menu.init(session.getUser());
			if (session.isLoggedIn()) {
				for (SessionObserver listener : sessionObservers) {
					listener.onUserLoggedIn(session.getUser());
				}
			}

			if (info.getSessionHeartbeat() > 0) {
				/*
				 * Create the timer that synchronize the session info
				 */
				timer = new Timer() {
					public void run() {
						service.getSessionInfo(Session.get().getSid(), new AsyncCallback<GUIParameter[]>() {
							@Override
							public void onFailure(Throwable caught) {
								if (isDevel())
									Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIParameter[] parameters) {
								if (parameters.length > 0) {
									GUIUser user = getUser();
									for (GUIParameter parameter : parameters) {
										if (parameter.getName().equals("messages"))
											user.setMessages(Integer.parseInt(parameter.getValue()));
										else if (parameter.getName().equals("workflows"))
											user.setActiveTasks(Integer.parseInt(parameter.getValue()));
									}
								}
							}
						});
					}
				};

				timer.scheduleRepeating(info.getSessionHeartbeat() * 1000);
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
		WindowUtils.setTitle(Session.get().getInfo(), folder.getPathExtended());
		for (FolderObserver listener : folderObservers) {
			listener.onFolderSelected(folder);
		}
	}

	public GUIInfo getInfo() {
		return info;
	}

	public void setInfo(GUIInfo info) {
		this.info = info;
	}

	public GUIWorkflow getCurrentWorkflow() {
		return currentWorkflow;
	}

	public void setCurrentWorkflow(GUIWorkflow currentWorkflow) {
		this.currentWorkflow = currentWorkflow;
	}
}