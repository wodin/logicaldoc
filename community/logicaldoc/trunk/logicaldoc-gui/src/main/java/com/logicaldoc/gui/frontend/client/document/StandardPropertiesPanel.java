package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.DocumentObserver;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIRating;
import com.logicaldoc.gui.common.client.data.TagsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FocusEvent;
import com.smartgwt.client.widgets.form.fields.events.FocusHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyDownEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Shows document's standard properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class StandardPropertiesPanel extends DocumentDetailTab {
	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private DynamicForm form1 = new DynamicForm();

	private DynamicForm form2 = new DynamicForm();

	private VLayout container = new VLayout();

	private HLayout formsContainer = new HLayout();

	private ValuesManager vm = new ValuesManager();

	private Canvas path;

	protected DocumentObserver observer;

	public StandardPropertiesPanel(final GUIDocument document, ChangedHandler changedHandler, DocumentObserver observer) {
		super(document, changedHandler);
		this.observer = observer;
		setWidth100();
		setHeight100();
		container.setWidth100();
		container.setMembersMargin(5);
		addMember(container);

		DynamicForm path = new DynamicForm();

		LinkItem pathItem = ItemFactory.newLinkItem("path", document.getPathExtended());
		pathItem.setTitle(I18N.message("path"));
		pathItem.setValue(Util.contextPath() + "?docId=" + document.getId());
		pathItem.addChangedHandler(changedHandler);
		pathItem.setWidth(400);

		String downloadUrl = Util.contextPath() + "download?docId=" + document.getId();
		LinkItem download = ItemFactory.newLinkItem("download", downloadUrl);
		download.setTitle(I18N.message("download"));
		download.setValue(downloadUrl);
		download.addChangedHandler(changedHandler);
		download.setWidth(400);

		path.setItems(pathItem, download);

		formsContainer.setWidth100();
		formsContainer.setMembersMargin(10);

		container.setMembers(path, formsContainer);
		refresh();
	}

	private void refresh() {
		vm.clearErrors(false);

		if (form1 != null)
			form1.destroy();

		if (formsContainer.contains(form1))
			formsContainer.removeMember(form1);

		form1 = new DynamicForm();
		form1.setNumCols(2);
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.LEFT);
		form1.setWidth(300);

		StaticTextItem id = ItemFactory.newStaticTextItem("id", "id", Long.toString(document.getId()));

		DateTimeFormat formatter = DateTimeFormat.getFormat(I18N.message("format_date"));
		StaticTextItem creation = ItemFactory.newStaticTextItem("creation", "createdon",
				formatter.format((Date) document.getCreation()));

		StaticTextItem creator = ItemFactory.newStaticTextItem("creator", "creator", document.getCreator());

		StaticTextItem published = ItemFactory.newStaticTextItem("date", "publishedon",
				formatter.format((Date) document.getDate()));

		StaticTextItem size = ItemFactory.newStaticTextItem("size", "size", Util.formatSizeW7(document.getFileSize())
				+ " (" + Util.formatSizeBytes(document.getFileSize()) + ")");

		StaticTextItem publisher = ItemFactory.newStaticTextItem("publisher", "publisher", document.getPublisher());

		TextItem title = ItemFactory.newTextItem("title", "title", document.getTitle());
		title.addChangedHandler(changedHandler);
		title.setRequired(true);
		title.setWidth(200);
		title.setDisabled(!update || !document.getFolder().isRename());

		StaticTextItem wfStatus = ItemFactory.newStaticTextItem("wfStatus", "workflowstatus",
				document.getWorkflowStatus());

		StaticTextItem version = ItemFactory.newStaticTextItem("version", "fileversion", document.getFileVersion()
				+ " (" + document.getVersion() + ")");

		String comment = document.getComment();
		if (comment != null && !"".equals(comment)) {
			comment = Util.padLeft(comment, 35);
			version.setValue(version.getValue() + " (" + comment + ")");
		}

		StaticTextItem filename = ItemFactory.newStaticTextItem("fileName", "file", document.getFileName());

		if (Feature.enabled(Feature.WORKFLOW))
			form1.setItems(id, title, wfStatus, version, filename, size, creation, published, creator, publisher);
		else
			form1.setItems(id, title, version, filename, size, creation, published, creator, publisher);

		formsContainer.addMember(form1, 0);

		/*
		 * Prepare the second form for the tags
		 */
		prepareRightForm();
		formsContainer.addMember(form2, 1);
	}

	private void prepareRightForm() {
		if (formsContainer.contains(form2)) {
			formsContainer.removeMember(form2);
			form2.destroy();
		}

		form2 = new DynamicForm();
		form2.setValuesManager(vm);

		List<FormItem> items = new ArrayList<FormItem>();

		FormItemIcon ratingIcon = ItemFactory.newItemIcon("rating" + document.getRating() + ".png");
		StaticTextItem vote = ItemFactory.newStaticTextItem("vote", "vote", "");
		vote.setIcons(ratingIcon);
		vote.setIconWidth(88);
		vote.addIconClickHandler(new IconClickHandler() {
			public void onIconClick(IconClickEvent event) {
				documentService.getRating(Session.get().getSid(), document.getId(), new AsyncCallback<GUIRating>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIRating rating) {
						if (rating != null) {
							RatingDialog dialog = new RatingDialog(document.getRating(), rating, observer);
							dialog.show();
						}
					}
				});
			}
		});
		items.add(vote);

		TextItem customId = ItemFactory.newTextItem("customId", "customid", document.getCustomId());
		customId.addChangedHandler(changedHandler);
		customId.setRequired(true);
		customId.setDisabled(!update);
		items.add(customId);

		SelectItem language = ItemFactory.newLanguageSelector("language", false, false);
		language.addChangedHandler(changedHandler);
		language.setDisabled(!update);
		language.setValue(document.getLanguage());
		items.add(language);

		if (Feature.enabled(Feature.TAGS)) {
			String mode = Session.get().getInfo().getConfig("tag.mode");

			final FormItem tagItem;
			if ("preset".equals(mode)) {
				tagItem = new SelectItem("tag");
				tagItem.setOptionDataSource(new TagsDS(mode));
			} else {
				tagItem = new ComboBoxItem("tag");
				((ComboBoxItem) tagItem).setPickListWidth(250);
				((ComboBoxItem) tagItem).setHideEmptyPickList(true);
				((ComboBoxItem) tagItem).setOptionDataSource(new TagsDS(null));
				tagItem.setHint(I18N.message("pressentertoaddtag"));
			}

			tagItem.setValueField("word");
			tagItem.setTitle(I18N.message("tag"));
			tagItem.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					if (event.getItem().getValue() != null) {
						String value = event.getItem().getValue() + "";
						event.getItem().clearValue();
						event.getItem().setValue(value);
					}
				}
			});
			tagItem.setHintStyle("hint");
			tagItem.setDisabled(!update);

			if ("preset".equals(mode))
				tagItem.addChangedHandler(new ChangedHandler() {
					@Override
					public void onChanged(ChangedEvent event) {
						// In the preset mode at each selection immediately add
						// the tag
						if (event.getItem().getSelectedRecord() != null) {
							document.addTag(event.getItem().getSelectedRecord().getAttribute("word"));
							tagItem.clearValue();
							changedHandler.onChanged(null);
							refresh();
						}
					}
				});

			tagItem.addKeyDownHandler(new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					if (Constants.KEY_ENTER.equals(event.getKeyName().toLowerCase())) {
						document.addTag(tagItem.getValue().toString().trim());
						tagItem.clearValue();
						changedHandler.onChanged(null);
						refresh();
					}
				}
			});
			items.add(tagItem);
			FormItemIcon icon = ItemFactory.newItemIcon("delete.png");
			int i = 0;
			for (String str : document.getTags()) {
				final StaticTextItem tgItem = ItemFactory.newStaticTextItem("tag" + i++, "tag", str);
				if (update)
					tgItem.setIcons(icon);
				tgItem.addIconClickHandler(new IconClickHandler() {
					public void onIconClick(IconClickEvent event) {
						document.removeTag((String) tgItem.getValue());
						changedHandler.onChanged(null);

						// Mark the item as deleted
						tgItem.setTextBoxStyle("deletedItem");
						tgItem.setTitleStyle("deletedItem");
						tgItem.setIcons(ItemFactory.newItemIcon("blank.gif"));
					}
				});
				tgItem.setDisabled(!update);
				items.add(tgItem);
			}
		}

		form2.setItems(items.toArray(new FormItem[0]));
	}

	@SuppressWarnings("unchecked")
	public boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			document.setCustomId((String) values.get("customId"));
			document.setTitle((String) values.get("title"));
			document.setLanguage((String) values.get("language"));
		}
		return !vm.hasErrors();
	}
}