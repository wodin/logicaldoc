package com.logicaldoc.gui.frontend.client.impex.archives;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.beans.GUISostConfig;
import com.logicaldoc.gui.common.client.data.ValidationDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.document.SignDialog;
import com.logicaldoc.gui.frontend.client.services.ArchiveService;
import com.logicaldoc.gui.frontend.client.services.ArchiveServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

/**
 * This tab is used to validate a given sost configuration.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class ValidationTab extends Tab {

	private ArchiveServiceAsync service = (ArchiveServiceAsync) GWT.create(ArchiveService.class);

	private ListGrid docsList;

	private IButton signButton = null;

	private IButton continueButton = null;

	private IButton endButton = null;

	private DynamicForm validationForm = null;

	private VLayout layout = null;

	private GUISostConfig config = null;

	private long archive;

	private ArchiveValidation window = null;

	private int tabPosition;

	private int num = 0;

	public ValidationTab(ArchiveValidation validation, int position, GUISostConfig sostConfig, long archiveId) {
		this.config = sostConfig;
		this.archive = archiveId;
		this.window = validation;
		this.tabPosition = position;

		setTitle(I18N.message("sostdoctype." + sostConfig.getDocumentType()));

		layout = new VLayout(15);
		layout.setMargin(20);

		refresh();
	}

	public void refresh() {
		if (signButton != null)
			layout.removeMember(signButton);
		if (validationForm != null)
			layout.removeMember(validationForm);
		if (continueButton != null)
			layout.removeMember(continueButton);
		if (endButton != null)
			layout.removeMember(endButton);

		setDisabled(window.getCurrentTabIndex() != tabPosition);

		signButton = new IButton();
		signButton.setTitle(I18N.message("sign"));
		signButton.setIcon(ItemFactory.newImgIcon("sign.png").getSrc());
		signButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final ListGridRecord[] records = docsList.getRecords();
				String ids = "";
				String names = "";
				for (ListGridRecord rec : records) {
					if (rec.getAttributeAsString("sign").equals("1")) {
						ids += "," + rec.getAttributeAsString("id");
						names += "," + rec.getAttributeAsString("title");
					}
				}
				if (ids.startsWith(","))
					ids = ids.substring(1);
				if (names.startsWith(","))
					names = names.substring(1);

				if (!ids.trim().isEmpty()) {
					final SignDialog dialog = new SignDialog(ids, names, true);
					dialog.show();
					dialog.addCloseClickHandler(new CloseClickHandler() {
						@Override
						public void onCloseClick(CloseClientEvent event) {
							ValidationTab.this.refresh();
							dialog.destroy();
						}
					});
				} else {
					SC.warn(I18N.message("nosignrequired"));
				}
			}
		});

		validationForm = new DynamicForm();
		validationForm.setWidth100();
		validationForm.setHeight(300);

		ListGridField title = new ListGridField("title", I18N.message("title"), 150);
		ListGridField number = new ListGridField("number", I18N.message("number"), 100);
		ListGridField date = new ListGridField("date", I18N.message("date"), 150);
		date.setAlign(Alignment.CENTER);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter());
		ListGridField error = new ListGridField("error", I18N.message("error"), 200);

		docsList = new ListGrid();
		docsList.setCanFreezeFields(true);
		docsList.setAutoFetchData(true);
		docsList.setShowHeader(true);
		docsList.setCanSelectAll(false);
		docsList.setSelectionType(SelectionStyle.NONE);
		docsList.setBorder("0px");
		docsList.setWidth100();
		docsList.setHeight(300);
		docsList.setShowAllRecords(true);
		docsList.setShowRecordComponents(true);
		docsList.setShowRecordComponentsByCell(true);
		docsList.setFields(title, number, date, error);
		docsList.setDataSource(new ValidationDS(config.getId(), archive));

		docsList.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				num = docsList.getTotalRows();
			}
		});

		// if (num > 0)
		validationForm.addChild(docsList);
		// else {
		StaticTextItem verifyMessage = ItemFactory.newStaticTextItem("verifyMessage", "",
				"<b>" + I18N.message("sostaction.success") + "</b>");
		verifyMessage.setShouldSaveValue(false);
		verifyMessage.setWrapTitle(false);
		validationForm.setItems(verifyMessage);
		// }

		// SC.warn("---- num: " + num);

		layout.addMember(signButton);
		layout.addMember(validationForm);

		if (tabPosition < 2) {
			continueButton = new IButton();
			continueButton.setTitle(I18N.message("continue"));
			continueButton.setDisabled(false);
			continueButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					window.setCurrentTabIndex(tabPosition + 1);
					window.getTabs().selectTab(tabPosition + 1);
					ValidationTab currentTab = ((ValidationTab) window.getTabs().getTab(tabPosition + 1));
					currentTab.refresh();
				}
			});
			layout.addMember(continueButton);
		} else {
			endButton = new IButton();
			endButton.setTitle(I18N.message("end"));
			endButton.setDisabled(false);
			endButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					window.destroy();
					service.setStatus(Session.get().getSid(), archive, GUIArchive.STATUS_CLOSED,
							new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									window.getArchivesList().refresh();
									window.getArchivesList().showDetails(archive, false);
								}
							});
				}
			});
			layout.addMember(endButton);
		}

		setPane(layout);
	}
}
