package com.logicaldoc.gui.frontend.client.menu;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.DocumentObserver;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.FolderObserver;
import com.logicaldoc.gui.common.client.PanelObserver;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUITenant;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.services.SecurityService;
import com.logicaldoc.gui.common.client.services.SecurityServiceAsync;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.document.grid.DocumentsGrid;
import com.logicaldoc.gui.frontend.client.dropbox.DropboxAuthorizationWizard;
import com.logicaldoc.gui.frontend.client.dropbox.DropboxDialog;
import com.logicaldoc.gui.frontend.client.gdocs.GDocsCreate;
import com.logicaldoc.gui.frontend.client.gdocs.GDocsEditor;
import com.logicaldoc.gui.frontend.client.gdocs.GDocsImport;
import com.logicaldoc.gui.frontend.client.gdocs.GDocsSettings;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.personal.ChangePassword;
import com.logicaldoc.gui.frontend.client.personal.MySignature;
import com.logicaldoc.gui.frontend.client.personal.Profile;
import com.logicaldoc.gui.frontend.client.personal.Subscriptions;
import com.logicaldoc.gui.frontend.client.personal.contacts.Contacts;
import com.logicaldoc.gui.frontend.client.search.SearchPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.DropboxService;
import com.logicaldoc.gui.frontend.client.services.DropboxServiceAsync;
import com.logicaldoc.gui.frontend.client.services.GDocsService;
import com.logicaldoc.gui.frontend.client.services.GDocsServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.logicaldoc.gui.frontend.client.services.TenantService;
import com.logicaldoc.gui.frontend.client.services.TenantServiceAsync;
import com.logicaldoc.gui.frontend.client.webcontent.WebcontentCreate;
import com.logicaldoc.gui.frontend.client.webcontent.WebcontentEditor;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
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
public class MainMenu extends ToolStrip implements FolderObserver, DocumentObserver, PanelObserver {
	protected SystemServiceAsync systemService = (SystemServiceAsync) GWT.create(SystemService.class);

	protected SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private SettingServiceAsync settingService = (SettingServiceAsync) GWT.create(SettingService.class);

	protected DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	protected GDocsServiceAsync gdocsService = (GDocsServiceAsync) GWT.create(GDocsService.class);

	protected DropboxServiceAsync dboxService = (DropboxServiceAsync) GWT.create(DropboxService.class);

	protected TenantServiceAsync tenantService = (TenantServiceAsync) GWT.create(TenantService.class);

	private boolean quickSearch = true;

	private boolean dropSpot = false;

	private HTMLFlow dropArea = new HTMLFlow();

	private static final String EMPTY_DIV = "<div style=\"margin-top:3px; width=\"80\"; height=\"20\"\" />";

	private ToolStripMenuButton tools;

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

		tools = getToolsMenu(Session.get().getCurrentFolder(), null);
		addMenuButton(tools);

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

		if (Feature.enabled(Feature.MULTI_TENANT)) {
			if (Session.get().getUser().isMemberOf("admin")
					&& Session.get().getUser().getTenantId() == Constants.TENANT_DEFAULTID) {
				SelectItem tenantItem = ItemFactory.newTenantSelector();
				tenantItem.setShowTitle(false);
				tenantItem.setValue(Long.toString(Session.get().getInfo().getTenant().getId()));
				tenantItem.addChangedHandler(new ChangedHandler() {

					@Override
					public void onChanged(ChangedEvent event) {
						long tenantId = Long.parseLong(event.getValue().toString());
						if (tenantId != Session.get().getInfo().getTenant().getId())
							tenantService.changeSessionTenant(Session.get().getSid(), tenantId,
									new AsyncCallback<GUITenant>() {

										@Override
										public void onSuccess(GUITenant tenant) {
											Session.get().getInfo().setTenant(tenant);
											Util.redirectToRoot();
										}

										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
										}
									});
					}
				});
				addFormItem(tenantItem);
			} else {
				Label tenantInfo = new Label(Session.get().getInfo().getTenant().getDisplayName());
				tenantInfo.setWrap(false);
				tenantInfo.setAutoWidth();
				addMember(tenantInfo);
			}

			addSeparator();
		}

		if (quickSearch)
			addFormItem(new SearchBox());

		Session.get().addFolderObserver(this);
		Session.get().addDocumentObserver(this);

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
									Util.redirectToRoot();
								}
							});
						}
					}
				});
			}
		});

		if (Feature.enabled(Feature.DROP_SPOT) && !"embedded".equals(Session.get().getConfig("gui.dropspot.mode"))
				&& com.logicaldoc.gui.common.client.Menu.enabled(com.logicaldoc.gui.common.client.Menu.DOCUMENTS))
			menu.setItems(dropSpotItem, exitItem);
		else
			menu.setItems(exitItem);
		ToolStripMenuButton menuButton = new ToolStripMenuButton(I18N.message("file"), menu);
		menuButton.setWidth(100);
		return menuButton;
	}

	private MenuItem getWebContentMenuItem(GUIFolder folder, final GUIDocument document) {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		final MenuItem edit = new MenuItem(I18N.message("editdoc"));
		edit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				if (document == null)
					return;

				final DocumentsGrid grid;
				if (MainPanel.get().isOnSearchTab())
					grid = SearchPanel.get().getDocumentsGrid();
				else
					grid = DocumentsPanel.get().getDocumentsGrid();

				if (document.getStatus() == 0) {
					// Need to checkout first
					documentService.checkout(Session.get().getSid(), document.getId(), new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Void result) {
							grid.markSelectedAsCheckedOut();
							Session.get().getUser().setCheckedOutDocs(Session.get().getUser().getCheckedOutDocs() + 1);
							Log.info(I18N.message("documentcheckedout"), null);

							WebcontentEditor popup = new WebcontentEditor(document, grid);
							popup.show();
						}
					});
				} else {
					SC.warn(I18N.message("event.locked"));
				}
			}
		});

		final MenuItem create = new MenuItem(I18N.message("createdoc"));
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				final DocumentsGrid grid;
				if (MainPanel.get().isOnSearchTab())
					grid = SearchPanel.get().getDocumentsGrid();
				else
					grid = DocumentsPanel.get().getDocumentsGrid();

				WebcontentCreate wcCreate = new WebcontentCreate(grid);
				wcCreate.show();
			}
		});

		menu.setItems(edit, create);
		edit.setEnabled(document != null && document.getImmutable() == 0 && folder != null && folder.isDownload()
				&& folder.isWrite() && Util.isWebContentFile(document.getFileName())
				&& Feature.enabled(Feature.WEBCONTENT));
		create.setEnabled(folder != null && folder.isDownload() && folder.isWrite()
				&& Feature.enabled(Feature.WEBCONTENT) && MainPanel.get().isOnDocumentsTab());

		MenuItem webcontentItems = new MenuItem(I18N.message("webcontent"));
		webcontentItems.setSubmenu(menu);

		return webcontentItems;
	}

	private MenuItem getDropboxMenuItem(GUIFolder folder, final GUIDocument document) {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		final MenuItem exportTo = new MenuItem(I18N.message("exporttodropbox"));
		exportTo.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				dboxService.isConnected(Session.get().getSid(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Boolean connected) {
						if (!connected)
							dboxService.startAuthorization(Session.get().getSid(), new AsyncCallback<String>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(String authorizationUrl) {
									DropboxAuthorizationWizard wizard = new DropboxAuthorizationWizard(authorizationUrl);
									wizard.show();
								}
							});
						else {
							DropboxDialog dialog = new DropboxDialog(true);
							dialog.show();
						}
					}
				});
			}
		});

		final MenuItem importFrom = new MenuItem(I18N.message("importfromdropbox"));
		importFrom.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				dboxService.isConnected(Session.get().getSid(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Boolean connected) {
						if (!connected)
							dboxService.startAuthorization(Session.get().getSid(), new AsyncCallback<String>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(String authorizationUrl) {
									DropboxAuthorizationWizard wizard = new DropboxAuthorizationWizard(authorizationUrl);
									wizard.show();
								}
							});
						else {
							DropboxDialog dialog = new DropboxDialog(false);
							dialog.show();
						}
					}
				});
			}
		});

		menu.setItems(exportTo, importFrom);

		exportTo.setEnabled(folder != null && folder.isDownload() && Feature.enabled(Feature.DROPBOX));
		importFrom.setEnabled(folder != null && folder.isWrite() && Feature.enabled(Feature.DROPBOX)
				&& MainPanel.get().isOnDocumentsTab());

		MenuItem dropboxItem = new MenuItem(I18N.message("dropbox"));
		dropboxItem.setSubmenu(menu);

		return dropboxItem;
	}

	private MenuItem getGDocsMenuItem(GUIFolder folder, final GUIDocument document) {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		final MenuItem edit = new MenuItem(I18N.message("editingdocs"));
		edit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				if (document == null)
					return;

				final DocumentsGrid grid;
				if (MainPanel.get().isOnSearchTab())
					grid = SearchPanel.get().getDocumentsGrid();
				else
					grid = DocumentsPanel.get().getDocumentsGrid();

				if (document.getStatus() == 0) {
					// Need to checkout first
					documentService.checkout(Session.get().getSid(), document.getId(), new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Void result) {
							grid.markSelectedAsCheckedOut();
							Session.get().getUser().setCheckedOutDocs(Session.get().getUser().getCheckedOutDocs() + 1);
							Log.info(I18N.message("documentcheckedout"), null);

							ContactingServer.get().show();
							gdocsService.upload(Session.get().getSid(), document.getId(), new AsyncCallback<String>() {
								@Override
								public void onFailure(Throwable caught) {
									ContactingServer.get().hide();
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(String resourceId) {
									ContactingServer.get().hide();
									if (resourceId == null) {
										Log.error(I18N.message("gdocserror"), null, null);
										return;
									}
									document.setExtResId(resourceId);
									grid.updateExtResId(resourceId);
									GDocsEditor popup = new GDocsEditor(document, grid);
									popup.show();
								}
							});
						}
					});
				} else {
					if (document.getStatus() == 1 && document.getExtResId() != null) {
						GDocsEditor popup = new GDocsEditor(document, grid);
						popup.show();
					} else {
						SC.warn(I18N.message("event.locked"));
					}
				}
			}
		});

		final MenuItem create = new MenuItem(I18N.message("createdoc"));
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				GDocsCreate popup = new GDocsCreate();
				popup.show();
			}
		});
		final MenuItem importDocs = new MenuItem(I18N.message("importfromgdocs"));
		importDocs.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				GDocsImport popup = new GDocsImport();
				popup.show();
			}
		});
		final MenuItem exportDocs = new MenuItem(I18N.message("exporttogdocs"));
		exportDocs.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				DocumentsGrid grid = DocumentsPanel.get().getDocumentsGrid();
				final long[] ids = grid.getSelectedIds();

				ContactingServer.get().show();
				gdocsService.exportDocuments(Session.get().getSid(), ids, new AsyncCallback<String[]>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
						ContactingServer.get().hide();
					}

					@Override
					public void onSuccess(String[] settings) {
						ContactingServer.get().hide();
						Log.info(I18N.message("gddocsexportok"), null);
					}
				});
			}
		});
		final MenuItem account = new MenuItem(I18N.message("googleaccount"));
		account.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				gdocsService.loadSettings(Session.get().getSid(), new AsyncCallback<String[]>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(String[] settings) {
						GDocsSettings dialog = new GDocsSettings(settings);
						dialog.show();
					}
				});
			}
		});

		menu.setItems(edit, create, importDocs, exportDocs, account);

		edit.setEnabled(document != null && document.getImmutable() == 0 && folder != null && folder.isDownload()
				&& folder.isWrite() && Feature.enabled(Feature.GDOCS));
		create.setEnabled(folder != null && folder.isWrite() && Feature.enabled(Feature.GDOCS)
				&& MainPanel.get().isOnDocumentsTab());
		importDocs.setEnabled(folder != null && folder.isDownload() && folder.isWrite()
				&& Feature.enabled(Feature.GDOCS) && MainPanel.get().isOnDocumentsTab());
		exportDocs.setEnabled(folder != null && folder.isDownload() && Feature.enabled(Feature.GDOCS));
		account.setEnabled(Feature.enabled(Feature.GDOCS));

		MenuItem gdocsItem = new MenuItem(I18N.message("googledocs"));
		gdocsItem.setSubmenu(menu);

		return gdocsItem;
	}

	private MenuItem getOfficeMenuItem(final GUIDocument document) {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		final MenuItem edit = new MenuItem(I18N.message("editwithoffice"));
		edit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				if (document == null)
					return;

				WindowUtils.openUrl("ldedit:" + GWT.getHostPageBaseURL() + "ldedit?action=edit&sid="
						+ Session.get().getSid() + "&docId=" + document.getId());
			}
		});

		menu.setItems(edit);

		edit.setEnabled(document != null && document.getImmutable() == 0 && document.getFolder() != null
				&& document.getFolder().isDownload() && document.getFolder().isWrite()
				&& Feature.enabled(Feature.OFFICE) && Util.isOfficeFile(document.getFileName()));

		MenuItem officeItem = new MenuItem(I18N.message("microsoftoffice"));
		officeItem.setSubmenu(menu);

		return officeItem;
	}

	private ToolStripMenuButton getToolsMenu(GUIFolder folder, GUIDocument document) {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		if (folder == null && document != null)
			folder = document.getFolder();

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

		if (document != null || folder != null) {
			if (Feature.enabled(Feature.DROPBOX)
					&& com.logicaldoc.gui.common.client.Menu.enabled(com.logicaldoc.gui.common.client.Menu.DROPBOX))
				menu.addItem(getDropboxMenuItem(folder, document));
			if (Feature.enabled(Feature.GDOCS)
					&& com.logicaldoc.gui.common.client.Menu.enabled(com.logicaldoc.gui.common.client.Menu.GDOCS))
				menu.addItem(getGDocsMenuItem(folder, document));
			if (Feature.enabled(Feature.OFFICE)
					&& com.logicaldoc.gui.common.client.Menu.enabled(com.logicaldoc.gui.common.client.Menu.OFFICE))
				menu.addItem(getOfficeMenuItem(document));
			if (Feature.enabled(Feature.WEBCONTENT)
					&& com.logicaldoc.gui.common.client.Menu.enabled(com.logicaldoc.gui.common.client.Menu.WEBCONTENT))
				menu.addItem(getWebContentMenuItem(folder, document));
		}

		if (Session.get().getUser().isMemberOf("admin")) {
			if (Session.get().isDevel()) {
				menu.addItem(develConsole);
			}
			menu.addItem(registration);
		}

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

		MenuItem contacts = new MenuItem(I18N.message("contacts"));
		contacts.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				Contacts.get().show();
			}
		});

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
					Cookies.removeCookie(Constants.COOKIE_SID);
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

		if (com.logicaldoc.gui.common.client.Menu.enabled(com.logicaldoc.gui.common.client.Menu.CONTACTS))
			items.add(contacts);

		if (Feature.enabled(Feature.DIGITAL_SIGN))
			items.add(mySignature);

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
							+ "applet/logicaldoc-enterprise-core.jar\"  code=\"com.logicaldoc.enterprise.upload.DropApplet2\" width=\"80\" height=\"20\" mayscript>";
					tmp += "<param name=\"baseUrl\" value=\"" + Util.contextPath() + "\" />";
					tmp += "<param name=\"sid\" value=\"" + Session.get().getSid() + "\" />";
					tmp += "<param name=\"language\" value=\"" + I18N.getDefaultLocaleForDoc() + "\" />";
					tmp += "<param name=\"sizeMax\" value=\""
							+ Long.parseLong(Session.get().getInfo().getConfig("upload.maxsize"));
					tmp += "<param name=\"disallow\" value=\"" + Session.get().getConfig("upload.disallow") + "\" />";
					tmp += "</applet></div>";
					dropArea.setContents(tmp);
				}
			} else {
				dropArea.setContents(EMPTY_DIV);
			}
		} else {
			dropArea.setContents(EMPTY_DIV);
		}

		if (tools != null)
			removeMember(tools);

		tools = getToolsMenu(folder, null);
		addMember(tools, 2);
	}

	@Override
	public void onDocumentSelected(GUIDocument document) {
		if (tools != null)
			removeMember(tools);

		tools = getToolsMenu(document.getFolder(), document);
		addMember(tools, 2);
	}

	@Override
	public void onFolderSaved(GUIFolder folder) {
		// Do nothing
	}

	@Override
	public void onDocumentSaved(GUIDocument document) {
		// Do nothing
	}

	@Override
	public void onTabSeleted(String panel) {
		if ("documents".equals(panel)) {
			onFolderSelected(Session.get().getCurrentFolder());
		} else {
			if (tools != null)
				removeMember(tools);

			tools = getToolsMenu(null, null);
			addMember(tools, 2);
		}
	}
}
