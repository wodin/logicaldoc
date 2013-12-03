package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.DocumentObserver;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIRating;
import com.logicaldoc.gui.common.client.data.TagsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.MultiComboBoxLayoutStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.MultiComboBoxItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon.Picker;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
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

	protected DocumentObserver observer;

	protected MultiComboBoxItem tagItem = null;

	protected boolean tagsInitialized = false;

	public StandardPropertiesPanel(final GUIDocument document, ChangedHandler changedHandler, DocumentObserver observer) {
		super(document, changedHandler, null);
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

		StaticTextItem creation = ItemFactory.newStaticTextItem("creation", "createdon",
				I18N.formatDate((Date) document.getCreation()));

		StaticTextItem creator = ItemFactory.newStaticTextItem("creator", "creator", document.getCreator());

		StaticTextItem published = ItemFactory.newStaticTextItem("date", "publishedon",
				I18N.formatDate((Date) document.getDate()));

		StaticTextItem size = ItemFactory.newStaticTextItem("size", "size", Util.formatSizeW7(document.getFileSize())
				+ " (" + Util.formatSizeBytes(document.getFileSize()) + ")");

		StaticTextItem publisher = ItemFactory.newStaticTextItem("publisher", "publisher", document.getPublisher());

		TextItem title = ItemFactory.newTextItem("title", "title", document.getTitle());
		title.addChangedHandler(changedHandler);
		title.setRequired(true);
		title.setWidth(200);
		title.setDisabled(!updateEnabled || !document.getFolder().isRename());

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
		tagsInitialized = false;

		if (formsContainer.contains(form2)) {
			formsContainer.removeMember(form2);
			form2.destroy();
		}

		form2 = new DynamicForm();
		form2.setValuesManager(vm);

		List<FormItem> items = new ArrayList<FormItem>();

		StaticTextItem customId = ItemFactory.newStaticTextItem("customId", "customid", document.getCustomId());
		items.add(customId);

		FormItemIcon ratingIcon = ItemFactory.newItemIcon("rating" + document.getRating() + ".png");
		StaticTextItem vote = ItemFactory.newStaticTextItem("vote", "vote", "");
		vote.setIcons(ratingIcon);
		vote.setIconWidth(88);
		if (updateEnabled)
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
		vote.setDisabled(!updateEnabled);

		SelectItem language = ItemFactory.newLanguageSelector("language", false, false);
		language.addChangedHandler(changedHandler);
		language.setDisabled(!updateEnabled);
		language.setValue(document.getLanguage());
		items.add(language);

		if (Feature.enabled(Feature.TAGS)) {
			String mode = Session.get().getInfo().getConfig("tag.mode");

			final DataSource ds = new TagsDS(null);

			tagItem = new MultiComboBoxItem("tag", I18N.message("tag"));
			tagItem.setLayoutStyle(MultiComboBoxLayoutStyle.FLOW);
			tagItem.setWidth(200);
			tagItem.setMultiple(true);
			tagItem.setOptionDataSource(ds);
			tagItem.setValueField("word");
			tagItem.setDisplayField("word");

			tagItem.setValues((Object[]) document.getTags());
			tagItem.setDisabled(!updateEnabled);
			tagItem.addChangedHandler(new ChangedHandler() {

				@Override
				public void onChanged(ChangedEvent event) {
					/*
					 * At initialization time this method is invoked several
					 * times until when it contains all the tags of the document
					 */
					if (tagsInitialized)
						changedHandler.onChanged(null);
					else {
						if ((tagItem.getValues().length == 0 && document.getTags() == null)
								|| tagItem.getValues().length == document.getTags().length)
							// The item contains all the tags of the document,
							// so consider it as initialized
							tagsInitialized = true;
					}

				}
			});

			PickerIcon addPicker = new PickerIcon(new Picker("[SKIN]/actions/add.png"), new FormItemClickHandler() {
				public void onFormItemClick(FormItemIconClickEvent event) {
					LD.askforValue(I18N.message("newtag"), I18N.message("tag"), "", "200", new ValueCallback() {
						@Override
						public void execute(String value) {
							if (value == null)
								return;

							String input = value.trim().replaceAll(",", "");
							if (!"".equals(input)) {
								// Get the user's inputed tags, he may have
								// wrote more than one tag
								List<String> tags = new ArrayList<String>();
								String token = input.trim().replace(',', ' ');
								if (!"".equals(token)) {
									tags.add(token);

									// Put the new tag in the options
									Record record = new Record();
									record.setAttribute("word", token);
									ds.addData(record);
								}

								if (tags.isEmpty())
									return;

								// Add the old tags to the new ones
								String[] oldVal = tagItem.getValues();
								for (int i = 0; i < oldVal.length; i++)
									if (!tags.contains(oldVal[i]))
										tags.add(oldVal[i]);

								tagItem.setValues((Object[]) tags.toArray(new String[0]));
								changedHandler.onChanged(null);
							}
						}
					});
				}
			});
			addPicker.setWidth(16);
			addPicker.setHeight(16);
			addPicker.setPrompt(I18N.message("newtag"));

			if ("free".equals(mode))
				tagItem.setIcons(addPicker);

			tagItem.setDisabled(!updateEnabled);
			items.add(tagItem);
			if (document.getTags() == null || document.getTags().length == 0)
				tagsInitialized = true;

		}

		form2.setItems(items.toArray(new FormItem[0]));
	}

	@SuppressWarnings("unchecked")
	public boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			document.setTitle((String) values.get("title"));
			document.setLanguage((String) values.get("language"));
			document.setTags(tagItem.getValues());
		}
		return !vm.hasErrors();
	}
}