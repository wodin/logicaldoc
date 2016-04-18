package com.logicaldoc.gui.common.client;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.services.InfoService;
import com.logicaldoc.gui.common.client.services.InfoServiceAsync;
import com.logicaldoc.gui.common.client.services.SecurityService;
import com.logicaldoc.gui.common.client.services.SecurityServiceAsync;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.common.client.widgets.SessionTimeout;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.util.SC;

/**
 * Represents a client work session
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Session {
	private static Session instance;

	private InfoServiceAsync service = (InfoServiceAsync) GWT.create(InfoService.class);

	protected SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private GUIInfo info;

	private GUISession session;

	private GUIFolder currentFolder;

	private GUIDocument currentDocument;

	private Set<SessionObserver> sessionObservers = new HashSet<SessionObserver>();

	private Set<FolderObserver> folderObservers = new HashSet<FolderObserver>();

	private Set<DocumentObserver> documentObservers = new HashSet<DocumentObserver>();

	private Timer timer;

	private boolean showThumbnail = true;

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

	public void setUser(GUIUser user) {
		session.setUser(user);
	}

	public void init(final GUISession session) {

		// Retrieve again the Info from the server (may be it is enriched by the
		// enterprise)
		service.getInfo(session.getUser().getLanguage(), session.getInfo().getTenant().getName(),
				new AsyncCallback<GUIInfo>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.warn(caught.getMessage());
					}

					@Override
					public void onSuccess(GUIInfo info) {
						try {
							session.setInfo(info);
							Session.get().session = session;
							Session.get().info = session.getInfo();
							I18N.init(session);
							Menu.init(session.getUser());

							if (session.isLoggedIn()) {
								for (SessionObserver listener : sessionObservers) {
									listener.onUserLoggedIn(session.getUser());
								}
							}

							if (info.getSessionHeartbeat() > 0) {
								/*
								 * Create the timer that synchronize the session
								 * info
								 */
								timer = new Timer() {
									public void run() {
										service.getSessionInfo(Session.get().getSid(),
												new AsyncCallback<GUIParameter[]>() {
													@Override
													public void onFailure(Throwable caught) {
														onInvalidSession();
													}

													@Override
													public void onSuccess(GUIParameter[] parameters) {
														boolean validSession = true;
														if (parameters.length > 0) {
															GUIUser user = getUser();
															for (GUIParameter parameter : parameters) {
																if (parameter.getName().equals("messages"))
																	user.setMessages(Integer.parseInt(parameter
																			.getValue()));
																else if (parameter.getName().equals("workflows"))
																	user.setActiveTasks(Integer.parseInt(parameter
																			.getValue()));
																else if (parameter.getName().equals("events"))
																	user.setUpcomingEvents(Integer.parseInt(parameter
																			.getValue()));
																else if (parameter.getName().equals("valid"))
																	validSession = Boolean.parseBoolean(parameter
																			.getValue());
															}
														}

														if (!validSession)
															onInvalidSession();
													}
												});
									}
								};

								timer.scheduleRepeating(info.getSessionHeartbeat() * 1000);
							}
						} catch (Throwable caught) {
							onInvalidSession();
						}
					}
				});

	}

	public void onInvalidSession() {
		timer.cancel();
		SessionTimeout.get().show();
	}

	public void addSessionObserver(SessionObserver observer) {
		sessionObservers.add(observer);
	}

	public void addFolderObserver(FolderObserver observer) {
		folderObservers.add(observer);
	}

	public void addDocumentObserver(DocumentObserver observer) {
		documentObservers.add(observer);
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

	public void setCurrentDocument(GUIDocument document) {
		this.currentDocument = document;
		for (DocumentObserver listener : documentObservers) {
			listener.onDocumentSelected(document);
		}
	}

	public GUIInfo getInfo() {
		return info;
	}

	public void setInfo(GUIInfo info) {
		this.info = info;
	}

	public GUISession getSession() {
		return session;
	}

	public void setSession(GUISession session) {
		this.session = session;
	}

	public GUIDocument getCurrentDocument() {
		return currentDocument;
	}

	public String getTenantName() {
		return info.getTenant().getName();
	}

	public boolean isDefaultTenant() {
		return info.getTenant().getId() == Constants.TENANT_DEFAULTID;
	}

	public String getConfig(String name) {
		return info.getConfig(name);
	}

	public boolean isShowThumbnail() {
		return showThumbnail;
	}

	public void setShowThumbnail(boolean showThumbnail) {
		this.showThumbnail = showThumbnail;
	}

	public void logout() {
		securityService.logout(Session.get().getSid(), new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
				SC.warn(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				try {
					Offline.remove(Constants.COOKIE_SID);
				} catch (Throwable t) {

				}

				try {
					Session.get().close();
				} catch (Throwable t) {

				}
				
				Util.redirectToRoot();
			}
		});
	}
}