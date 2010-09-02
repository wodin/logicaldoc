package com.logicaldoc.gui.frontend.client.impex.archives;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.ArchiveService;
import com.logicaldoc.gui.frontend.client.services.ArchiveServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This is the form used to create a new Archive
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ArchiveDialog extends Window {
	private ArchiveServiceAsync archiveService = (ArchiveServiceAsync) GWT.create(ArchiveService.class);

	private ValuesManager vm = new ValuesManager();

	private ExportArchivesList archivesPanel;

	public ArchiveDialog(ExportArchivesList archivesPanel) {
		this.archivesPanel = archivesPanel;

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClientEvent event) {
				destroy();
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("addarchive"));
		setWidth(280);
		setHeight(250);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		final DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setWidth(280);
		form.setMargin(5);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(1);

		SelectItem type = ItemFactory.newArchiveTypeSelector();

		TextItem name = ItemFactory.newSimpleTextItem("name", "name", null);
		name.setRequired(true);

		TextItem description = ItemFactory.newTextItem("description", "description", null);

		StaticTextItem creator = ItemFactory.newStaticTextItem("creator", "creator", Session.get().getUser()
				.getFullName());

		ButtonItem save = new ButtonItem();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {
					GUIArchive archive = new GUIArchive();
					if (vm.getValueAsString("archivetype") != null)
						archive.setType(Integer.parseInt(vm.getValueAsString("archivetype")));
					archive.setName(vm.getValueAsString("name"));
					archive.setDescription(vm.getValueAsString("description"));
					archive.setCreatorId(Session.get().getUser().getId());
					archive.setCreatorName(Session.get().getUser().getFullName());
					archive.setMode(GUIArchive.MODE_EXPORT);

					archiveService.save(Session.get().getSid(), archive, new AsyncCallback<GUIArchive>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
							destroy();
						}

						@Override
						public void onSuccess(GUIArchive result) {
							destroy();
							ArchiveDialog.this.archivesPanel.refresh();
						}
					});
				}
			}
		});

		if (Feature.visible(Feature.PAPER_DEMATERIALIZATION)) {
			form.setFields(creator, name, description, type, save);
			if (!Feature.enabled(Feature.PAPER_DEMATERIALIZATION))
				type.setDisabled(true);
		} else
			form.setFields(creator, name, description, save);
		addItem(form);
	}
}