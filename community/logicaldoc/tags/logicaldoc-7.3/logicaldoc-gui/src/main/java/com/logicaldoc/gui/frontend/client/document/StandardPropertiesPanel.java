package com.logicaldoc.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
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
import com.logicaldoc.gui.common.client.widgets.ImageLightbox;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.MultiComboBoxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Shows document's standard properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class StandardPropertiesPanel extends DocumentDetailTab {
	private static final int DEFAULT_ITEM_WIDTH = 250;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private DynamicForm form1 = new DynamicForm();

	private DynamicForm form2 = new DynamicForm();

	private Layout tile = null;

	private VLayout container = new VLayout();

	private HLayout columns = new HLayout();

	private ValuesManager vm = new ValuesManager();

	protected DocumentObserver observer;

	protected MultiComboBoxItem tagItem = null;

	public StandardPropertiesPanel(final GUIDocument document, ChangedHandler changedHandler, DocumentObserver observer) {
		super(document, changedHandler, null);
		this.observer = observer;
		setWidth100();
		setHeight100();
		container.setWidth100();
		container.setMembersMargin(5);
		addMember(container);

		columns.setWidth100();
		columns.setMembersMargin(10);

		container.setMembers(columns);
		refresh();
	}

	private void refresh() {
		vm.clearErrors(false);

		if (form1 != null)
			form1.destroy();

		if (columns.contains(form1))
			columns.removeMember(form1);

		form1 = new DynamicForm();
		form1.setNumCols(2);
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.LEFT);
		form1.setWidth(DEFAULT_ITEM_WIDTH);

		StaticTextItem id = ItemFactory.newStaticTextItem("id", "id", Long.toString(document.getId()));
		if (!Long.toString(document.getId()).equals(document.getCustomId())) {
			id.setTooltip(Long.toString(document.getId()) + " (" + document.getCustomId() + ")");
			id.setValue(Util.padLeft(Long.toString(document.getId()) + " (" + document.getCustomId() + ")", 35));
		}

		if (document.getDocRef() != null)
			id.setTooltip(I18N.message("thisisalias"));

		StaticTextItem creation = ItemFactory.newStaticTextItem(
				"creation",
				"createdon",
				Util.padLeft(
						I18N.formatDate((Date) document.getCreation()) + " " + I18N.message("by") + " "
								+ document.getCreator(), 40));
		creation.setTooltip(I18N.formatDate((Date) document.getCreation()) + " " + I18N.message("by") + " "
				+ document.getCreator());

		StaticTextItem published = ItemFactory.newStaticTextItem(
				"date",
				"publishedon",
				Util.padLeft(
						I18N.formatDate((Date) document.getDate()) + " " + I18N.message("by") + " "
								+ document.getPublisher(), 40));
		published.setTooltip(I18N.formatDate((Date) document.getDate()) + " " + I18N.message("by") + " "
				+ document.getPublisher());

		StaticTextItem size = ItemFactory.newStaticTextItem("size", "size", Util.formatSizeW7(document.getFileSize())
				+ " (" + Util.formatSizeBytes(document.getFileSize()) + ")");

		TextItem title = ItemFactory.newTextItem("title", "title", document.getTitle());
		title.addChangedHandler(changedHandler);
		title.setRequired(true);
		title.setWidth(DEFAULT_ITEM_WIDTH);
		title.setDisabled(!updateEnabled || !document.getFolder().isRename());

		StaticTextItem filename = ItemFactory.newStaticTextItem("fileName", "file",
				Util.padLeft(document.getFileName(), 39));
		filename.setWrap(false);
		filename.setWidth(DEFAULT_ITEM_WIDTH);
		filename.setTooltip(document.getFileName());

		StaticTextItem wfStatus = ItemFactory.newStaticTextItem("wfStatus", "workflowstatus",
				document.getWorkflowStatus());

		StaticTextItem version = ItemFactory.newStaticTextItem("version", "fileversion", document.getFileVersion()
				+ " (" + document.getVersion() + ")");
		version.setValue(version.getValue());
		String comment = document.getComment();
		if (comment != null && !"".equals(comment))
			version.setTooltip(comment);

		LinkItem folder = ItemFactory.newLinkItem("folder", Util.padLeft(document.getPathExtended(), 40));
		folder.setTitle(I18N.message("folder"));
		folder.setValue(Util.contextPath() + "?" + Constants.FOLDER_ID + "=" + document.getFolder().getId());
		folder.setTooltip(document.getPathExtended());
		folder.setWrap(false);
		folder.setWidth(DEFAULT_ITEM_WIDTH);

		String downloadUrl = Util.contextPath() + "download?" + Constants.DOC_ID + "=" + document.getId();
		String documentUrl = Util.contextPath() + "?" + Constants.DOC_ID + "=" + document.getId();
		String perma = "<a href='" + downloadUrl + "'>" + I18N.message("download") + "</a> | <a href='" + documentUrl
				+ "'>" + I18N.message("details") + "</a>";
		StaticTextItem permaLink = ItemFactory.newStaticTextItem("permalink", "permalink", perma);

		if (Feature.enabled(Feature.WORKFLOW))
			form1.setItems(id, title, filename, folder, size, version, wfStatus, creation, published, permaLink);
		else
			form1.setItems(id, title, filename, folder, size, version, creation, published, permaLink);

		columns.addMember(form1);

		/*
		 * Prepare the second form for the tags
		 */
		prepareRightForm();

		/*
		 * Prepare the tile
		 */
		if (document.getId() != 0L)
			prepareTile();
	}

	private void prepareTile() {
		try {
			if (tile != null && columns.contains(tile)) {
				columns.removeMember(tile);
				tile.destroy();
			}

			tile = new HLayout();
			tile.setMembersMargin(1);
			tile.setAlign(Alignment.RIGHT);
			tile.setOverflow(Overflow.HIDDEN);

			if (Session.get().isShowThumbnail()) {
				tile.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						ImageLightbox lightbox = new ImageLightbox(document);
						lightbox.show();
					}
				});

				String html = "<img border='0' alt='' title='' src='"
						+ Util.tileUrl(Session.get().getSid(), document.getId(), null) + "' height='"
						+ Session.get().getConfig("gui.tile.size") + "px' style='float:right;' align='right'/>";
				HTMLFlow tileImage = new HTMLFlow(html);

				Img closeTileImage = new Img("[SKIN]/headerIcons/close.gif", 16, 16);
				closeTileImage.setShowRollOver(true);
				closeTileImage.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						Session.get().setShowThumbnail(false);
						prepareTile();
					}
				});

				tile.setMembers(tileImage, closeTileImage);
			} else {
				IButton showThumbnail = new IButton(I18N.message("showthumbnail"));
				showThumbnail.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						Session.get().setShowThumbnail(true);
						prepareTile();
					}
				});
				tile.setMembers(showThumbnail);
			}

			columns.addMember(tile);
		} catch (Throwable t) {
			Log.error(t.getMessage(), null, t);
		}
	}

	private void prepareRightForm() {
		if (columns.contains(form2)) {
			columns.removeMember(form2);
			form2.destroy();
		}

		form2 = new DynamicForm();
		form2.setValuesManager(vm);

		List<FormItem> items = new ArrayList<FormItem>();

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
			String mode = Session.get().getConfig("tag.mode");
			final DataSource ds = new TagsDS(null, true);

			tagItem = ItemFactory.newMultiComboBoxItem("tag", "tag", ds, (Object[]) document.getTags());
			tagItem.setPrompt(I18N.message("typeatag"));
			tagItem.setValueField("word");
			tagItem.setDisplayField("word");
			tagItem.setDisabled(!updateEnabled);
			tagItem.addChangedHandler(changedHandler);

			final TextItem newTagItem = ItemFactory.newTextItem("newtag", "newtag", null);
			newTagItem.setRequired(false);
			newTagItem.addKeyPressHandler(new KeyPressHandler() {
				@Override
				public void onKeyPress(KeyPressEvent event) {
					if (newTagItem.validate() && newTagItem.getValue() != null && event.getKeyName() != null
							&& "enter".equals(event.getKeyName().toLowerCase())) {
						String input = newTagItem.getValueAsString().trim();
						newTagItem.clearValue();

						if (!"".equals(input)) {
							String[] tokens = input.split("\\,");

							int min = Integer.parseInt(Session.get().getConfig("tag.minsize"));
							int max = Integer.parseInt(Session.get().getConfig("tag.maxsize"));
							boolean containsInvalid = false;
							List<String> tags = new ArrayList<String>();
							for (String token : tokens) {
								String t = token.trim();

								if (t.length() < min || t.length() > max) {
									containsInvalid = true;
									continue;
								}

								tags.add(t);

								// Put the new tag in the options
								Record record = new Record();
								record.setAttribute("index", t);
								record.setAttribute("word", t);
								ds.addData(record);

								// Add the old tags to the new ones
								String[] oldVal = tagItem.getValues();
								for (int i = 0; i < oldVal.length; i++)
									if (!tags.contains(oldVal[i]))
										tags.add(oldVal[i]);

								// Update the tag item and trigger the change
								tagItem.setValues((Object[]) tags.toArray(new String[0]));
								changedHandler.onChanged(null);
							}

							if (containsInvalid)
								SC.warn(I18N.message("sometagaddedbecauseinvalid"));
						}
					}
				}
			});

			items.add(tagItem);
			if ("free".equals(mode) && updateEnabled)
				items.add(newTagItem);
		}

		form2.setItems(items.toArray(new FormItem[0]));
		columns.addMember(form2);
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