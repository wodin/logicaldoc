package com.logicaldoc.gui.frontend.client.menu;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.FolderObserver;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.services.SecurityService;
import com.logicaldoc.gui.common.client.services.SecurityServiceAsync;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.frontend.client.personal.ChangePassword;
import com.logicaldoc.gui.frontend.client.personal.MySignature;
import com.logicaldoc.gui.frontend.client.personal.Profile;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;

/**
 * Main program menu
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MainMenu extends ToolStrip implements FolderObserver {
	protected SystemServiceAsync systemService = (SystemServiceAsync) GWT.create(SystemService.class);

	protected SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private SettingServiceAsync settingService = (SettingServiceAsync) GWT.create(SettingService.class);

	private boolean quickSearch = true;

	private boolean dropSpot = false;

	private HTMLFlow dropArea = new HTMLFlow();

	private static final String EMPTY_DIV = "<div style=\"margin-top:3px; width=\"80\"; height=\"20\"\" />";

	public MainMenu(boolean quickSearch) {
		this(quickSearch, false);
	}

	public MainMenu(boolean quickSearch, boolean dropSpot) {
		super();

		this.quickSearch = quickSearch;
		this.dropSpot = dropSpot;

		setWidth100();

		ToolStripMenuButton menu = getFileMenu();
		addMenuButton(menu);

		addMenuButton(getPersonalMenu());

		if (Session.get().getUser().isMemberOf("admin")) {
			menu = getToolsMenu();
			addMenuButton(menu);
		}

		addMenuButton(getHelpMenu());
		addFill();

		dropArea.setContents(EMPTY_DIV);
		dropArea.setWidth(81);
		if (dropSpot && com.logicaldoc.gui.common.client.Menu.enabled(com.logicaldoc.gui.common.client.Menu.DOCUMENTS)) {
			if (Feature.enabled(Feature.DROP_SPOT))
				dropArea.setTooltip(I18N.message("dropfiles"));
			else
				dropArea.setTooltip(I18N.message("featuredisabled"));
			dropArea.setAlign(Alignment.CENTER);
			addMember(dropArea);
		}
		addFill();

		Label userInfo = new Label(I18N.message("loggedin") + " <b>" + Session.get().getUser().getUserName() + "</b>");
		userInfo.setWrap(false);

		addMember(userInfo);
		addSeparator();

		if (quickSearch)
			addFormItem(new SearchBox());

		Session.get().addFolderObserver(this);
		onFolderSelected(Session.get().getCurrentFolder());
	}

	private ToolStripMenuButton getFileMenu() {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		MenuItem dropSpotItem = new MenuItem("Drop Spot");
		dropSpotItem.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				Util.openDropSpot();
			}
		});

		MenuItem exitItem = new MenuItem(I18N.message("exit"));
		exitItem.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				LD.ask(I18N.message("question"), I18N.message("confirmexit"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
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
									
									Session.get().close();
									String base = GWT.getHostPageBaseURL();
									Util.redirect(base
											+ (base.endsWith("/") ? GWT.getModuleName() + ".jsp" : "/"
													+ GWT.getModuleName() + ".jsp"));
								}
							});
						}
					}
				});
			}
		});

		if (Feature.enabled(Feature.DROP_SPOT)
				&& !"embedded".equals(Session.get().getInfo().getConfig("gui.dropspot.mode"))
				&& com.logicaldoc.gui.common.client.Menu.enabled(com.logicaldoc.gui.common.client.Menu.DOCUMENTS))
			menu.setItems(dropSpotItem, exitItem);
		else
			menu.setItems(exitItem);
		ToolStripMenuButton menuButton = new ToolStripMenuButton(I18N.message("file"), menu);
		menuButton.setWidth(100);
		return menuButton;
	}

	private ToolStripMenuButton getToolsMenu() {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		MenuItem develConsole = new MenuItem(I18N.message("develconsole"));
		develConsole.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				SC.showConsole();
			}
		});

		MenuItem registration = new MenuItem(I18N.message("registration"));
		registration.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				settingService.loadSettingsByNames(Session.get().getSid(), new String[] { "reg.name", "reg.email",
						"reg.organization", "reg.website" }, new AsyncCallback<GUIParameter[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIParameter[] reg) {
						String[] values = new String[reg.length];
						for (int j = 0; j < reg.length; j++) {
							values[j] = reg[j].getValue();
						}
						Registration r = new Registration(values);
						r.show();
					}
				});
			}
		});

		if (Session.get().isDevel())
			menu.setItems(develConsole, registration);
		else
			menu.setItems(registration);

		ToolStripMenuButton menuButton = new ToolStripMenuButton(I18N.message("tools"), menu);
		menuButton.setWidth(100);
		return menuButton;
	}

	private ToolStripMenuButton getPersonalMenu() {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		MenuItem profile = new MenuItem(I18N.message("profile"));
		profile.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				securityService.getUser(Session.get().getSid(), Session.get().getUser().getId(),
						new AsyncCallback<GUIUser>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIUser user) {
								Profile profile = new Profile(user);
								profile.show();
							}
						});
			}
		});

		MenuItem changePswd = new MenuItem(I18N.message("changepassword"));
		changePswd.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				ChangePassword cp = new ChangePassword(Session.get().getUser(), null);
				cp.show();
			}
		});
		changePswd.setEnabled(!(Session.get().isDemo() && Session.get().getUser().getId() == 1));

		MenuItem mySignature = new MenuItem(I18N.message("mysignature"));
		mySignature.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				securityService.getUser(Session.get().getSid(), Session.get().getUser().getId(),
						new AsyncCallback<GUIUser>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIUser user) {
								MySignature mysign = new MySignature(user, false);
								mysign.show();
							}
						});
			}
		});

		MenuItem removeCookies = new MenuItem(I18N.message("removecookies"));
		removeCookies.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				try {
					Offline.remove(Constants.COOKIE_HITSLIST);
				} catch (Throwable t) {

				}

				try {
					Offline.remove(Constants.COOKIE_DOCSLIST);
				} catch (Throwable t) {

				}

				try {
					Offline.remove(Constants.COOKIE_DOCSLIST_MAX);
				} catch (Throwable t) {

				}

				try {
					Offline.remove(Constants.COOKIE_DOCSMENU_W);
				} catch (Throwable t) {

				}
				try {
					Offline.remove(Constants.COOKIE_PASSWORD);
				} catch (Throwable t) {

				}
				try {
					Offline.remove(Constants.COOKIE_SAVELOGIN);
				} catch (Throwable t) {

				}
				try {
					Offline.remove(Constants.COOKIE_USER);
				} catch (Throwable t) {

				}
				try {
					Offline.remove(Constants.COOKIE_SID);
				} catch (Throwable t) {

				}
				Log.info(I18N.message("cookiesremoved"), null);
			}
		});

		MenuItem subscriptions = new MenuItem(I18N.message("subscriptions"));
		subscriptions.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				Subscriptions s = new Subscriptions();
				s.show();
			}
		});

		List<MenuItem> items = new ArrayList<MenuItem>();
		items.add(profile);
		items.add(changePswd);

		if (Feature.enabled(Feature.DIGITAL_SIGN))
			items.add(mySignature);
		else
			menu.setItems(profile, changePswd, removeCookies);

		if (Feature.enabled(Feature.AUDIT)
				&& com.logicaldoc.gui.common.client.Menu.enabled(com.logicaldoc.gui.common.client.Menu.SUBSCRIPTIONS))
			items.add(subscriptions);

		items.add(removeCookies);

		menu.setItems(items.toArray(new MenuItem[0]));

		ToolStripMenuButton menuButton = new ToolStripMenuButton(I18N.message("personal"), menu);
		menuButton.setWidth(100);
		return menuButton;
	}

	private ToolStripMenuButton getHelpMenu() {

		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		MenuItem documentation = new MenuItem(I18N.message("documentation"));
		documentation.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				Window.open(Session.get().getInfo().getHelp() + "?lang=" + I18N.getLocale(), "_blank",
						"location=no,status=no,toolbar=no,menubar=no,resizable=yes,scrollbars=yes");
			}
		});
		menu.addItem(documentation);

		MenuItem bugReport = new MenuItem(I18N.message("bug.report"));
		bugReport.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				Window.open(Session.get().getInfo().getBugs(), "_blank",
						"location=no,status=no,toolbar=no,menubar=no,resizable=yes,scrollbars=yes");
			}
		});
		if (Session.get().getInfo().getBugs() != null && !"-".equals(Session.get().getInfo().getBugs()))
			menu.addItem(bugReport);

		MenuItem forum = new MenuItem(I18N.message("forum"));
		forum.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				Window.open(Session.get().getInfo().getForum(), "_blank",
						"location=no,status=no,toolbar=no,menubar=no,resizable=yes,scrollbars=yes");
			}
		});
		if (Session.get().getInfo().getForum() != null && !"-".equals(Session.get().getInfo().getForum()))
			menu.addItem(forum);

		MenuItem about = new MenuItem(I18N.message("about") + " " + Session.get().getInfo().getProduct());
		about.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				AboutDialog dialog = new AboutDialog();
				dialog.show();
			}
		});
		menu.addItem(about);

		ToolStripMenuButton menuButton = new ToolStripMenuButton(I18N.message("help"), menu);
		menuButton.setWidth(100);
		return menuButton;
	}

	@Override
	public void onFolderSelected(GUIFolder folder) {
		if (Feature.visible(Feature.DROP_SPOT)
				&& "embedded".equals(Session.get().getInfo().getConfig("gui.dropspot.mode"))) {
			if (folder != null && folder.hasPermission(Constants.PERMISSION_WRITE)
					&& Feature.enabled(Feature.DROP_SPOT)) {
				if (dropArea.getContents().equals(EMPTY_DIV)) {
					String tmp = "<div style=\"z-index:-100;margin-top:3px; width=\"80\"; height=\"20\"\"><applet name=\"DropApplet\" archive=\""
							+ Util.contextPath()
							+ "applet/logicaldoc-enterprise-core.jar\"  code=\"com.logicaldoc.enterprise.upload.DropApplet2\" width=\"80\" height=\"20\">";
					tmp += "<param name=\"uploadUrl\" value=\"" + Util.contextPath()
							+ "servlet.gupld?new_session=true&sid=" + Session.get().getSid() + "\" />";
					tmp += "</applet></div>";
					dropArea.setContents(tmp);
				}
			} else {
				dropArea.setContents(EMPTY_DIV);
			}
		} else {
			dropArea.setContents(EMPTY_DIV);
		}
	}

	@Override
	public void onFolderSaved(GUIFolder folder) {
		// Do nothing
	}
}
