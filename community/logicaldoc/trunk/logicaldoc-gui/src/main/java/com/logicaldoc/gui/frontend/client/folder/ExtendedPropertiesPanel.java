package com.logicaldoc.gui.frontend.client.folder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

/**
 * Shows document's standard properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ExtendedPropertiesPanel extends FolderDetailTab {
	private DynamicForm form1 = new DynamicForm();

	private DynamicForm form2 = new DynamicForm();

	private ValuesManager vm = new ValuesManager();

	private GUIExtendedAttribute[] currentExtAttributes = null;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

	private boolean update = false;

	public ExtendedPropertiesPanel(GUIFolder folder, ChangedHandler changedHandler) {
		super(folder, changedHandler);
		setWidth100();
		setHeight100();
		setMembersMargin(20);
		update = folder.hasPermission(Constants.PERMISSION_RENAME);
		refresh();
	}

	private void refresh() {
		vm.clearValues();
		vm.clearErrors(false);

		if (form1 != null)
			form1.destroy();

		if (contains(form1))
			removeChild(form1);
		form1 = new DynamicForm();
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.TOP);
		form1.setWidth("1%");
		List<FormItem> items = new ArrayList<FormItem>();

		SelectItem templateItem = ItemFactory.newTemplateSelector(false, null);
		templateItem.addChangedHandler(changedHandler);
		templateItem.setMultiple(false);
		templateItem.setDisabled(!update);
		if (folder.getTemplateId() != null)
			templateItem.setValue(folder.getTemplateId().toString());

		templateItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (event.getValue() != null && !"".equals(event.getValue().toString())) {
					folder.setAttributes(new GUIExtendedAttribute[0]);
					prepareExtendedAttributes(new Long(event.getValue().toString()));
				} else {
					folder.setAttributes(new GUIExtendedAttribute[0]);
					prepareExtendedAttributes(null);

				}
			}
		});

		ButtonItem applyMetadata = new ButtonItem(I18N.message("applytosubfolders"));
		applyMetadata.setAutoFit(true);
		applyMetadata.setEndRow(true);
		applyMetadata.setDisabled(!update);
		applyMetadata.setColSpan(1);
		applyMetadata.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ContactingServer.get().show();
				folderService.applyMetadata(Session.get().getSid(), folder.getId(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						ContactingServer.get().hide();
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void v) {
						ContactingServer.get().hide();
					}
				});
			}
		});

		if (Feature.visible(Feature.TEMPLATE)) {
			items.add(templateItem);
			items.add(applyMetadata);
			if (!Feature.enabled(Feature.TEMPLATE)) {
				templateItem.setDisabled(true);
				templateItem.setTooltip(I18N.message("featuredisabled"));
			}
		}

		form1.setItems(items.toArray(new FormItem[0]));
		addMember(form1);

		if (Feature.enabled(Feature.TEMPLATE))
			prepareExtendedAttributes(folder.getTemplateId());
	}

	/*
	 * Prepare the second form for the extended attributes
	 */
	private void prepareExtendedAttributes(Long templateId) {
		if (form2 != null)
			form2.destroy();
		if (contains(form2))
			removeChild(form2);
		form2 = new DynamicForm();
		form2.setValuesManager(vm);
		form2.setTitleOrientation(TitleOrientation.TOP);
		form2.clearValues();
		form2.clear();
		form2.setWidth100();
		addMember(form2);

		if (templateId == null)
			return;

		documentService.getAttributes(Session.get().getSid(), templateId, new AsyncCallback<GUIExtendedAttribute[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIExtendedAttribute[] result) {
				currentExtAttributes = result;
				List<FormItem> items = new ArrayList<FormItem>();
				for (GUIExtendedAttribute att : result) {
					if (att.getType() == GUIExtendedAttribute.TYPE_STRING) {
						FormItem item = ItemFactory.newStringItemForExtendedAttribute(att);
						if (folder.getValue(att.getName()) != null)
							item.setValue((String) folder.getValue(att.getName()));
						item.addChangedHandler(changedHandler);
						item.setDisabled(!update);
						items.add(item);
					} else if (att.getType() == GUIExtendedAttribute.TYPE_INT) {
						IntegerItem item = ItemFactory.newIntegerItemForExtendedAttribute(att.getName(),
								att.getLabel(), null);
						if (folder.getValue(att.getName()) != null)
							item.setValue((Long) folder.getValue(att.getName()));
						item.setRequired(att.isMandatory());
						item.addChangedHandler(changedHandler);
						item.setDisabled(!update);
						items.add(item);
					} else if (att.getType() == GUIExtendedAttribute.TYPE_DOUBLE) {
						FloatItem item = ItemFactory.newFloatItemForExtendedAttribute(att.getName(), att.getLabel(),
								null);
						if (folder.getValue(att.getName()) != null)
							item.setValue((Double) folder.getValue(att.getName()));
						item.setRequired(att.isMandatory());
						item.addChangedHandler(changedHandler);
						item.setDisabled(!update);
						items.add(item);
					} else if (att.getType() == GUIExtendedAttribute.TYPE_DATE) {
						final DateItem item = ItemFactory.newDateItemForExtendedAttribute(att.getName(), att.getLabel());
						if (folder.getValue(att.getName()) != null)
							item.setValue((Date) folder.getValue(att.getName()));
						item.setRequired(att.isMandatory());
						item.addChangedHandler(changedHandler);
						item.addKeyPressHandler(new KeyPressHandler() {
							@Override
							public void onKeyPress(KeyPressEvent event) {
								if ("backspace".equals(event.getKeyName().toLowerCase())
										|| "delete".equals(event.getKeyName().toLowerCase())) {
									item.clearValue();
									item.setValue((Date) null);
									changedHandler.onChanged(null);
								} else {
									changedHandler.onChanged(null);
								}
							}
						});
						item.setDisabled(!update);
						items.add(item);
					}
				}
				form2.setItems(items.toArray(new FormItem[0]));
			}
		});
	}

	@SuppressWarnings("unchecked")
	public boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {

			if (Feature.enabled(Feature.TEMPLATE)) {
				if (values.get("template") == null || "".equals(values.get("template").toString()))
					folder.setTemplateId(null);
				else {
					folder.setTemplateId(Long.parseLong(values.get("template").toString()));
				}
				for (String name : values.keySet()) {
					if (name.startsWith("_")) {
						Object val = values.get(name);
						String nm = name.substring(1).replaceAll(Constants.BLANK_PLACEHOLDER, " ");
						if (val != null) {
							folder.setValue(nm, val);
						} else {
							for (GUIExtendedAttribute extAttr : currentExtAttributes) {
								if (extAttr.getName().equals(nm)) {
									if (extAttr.getType() == GUIExtendedAttribute.TYPE_INT) {
										folder.getExtendedAttribute(nm).setIntValue(null);
										break;
									} else if (extAttr.getType() == GUIExtendedAttribute.TYPE_DOUBLE) {
										folder.getExtendedAttribute(nm).setDoubleValue(null);
										break;
									} else if (extAttr.getType() == GUIExtendedAttribute.TYPE_DATE) {
										folder.getExtendedAttribute(nm).setDateValue(null);
										break;
									} else {
										folder.setValue(nm, "");
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		return !vm.hasErrors();
	}
}