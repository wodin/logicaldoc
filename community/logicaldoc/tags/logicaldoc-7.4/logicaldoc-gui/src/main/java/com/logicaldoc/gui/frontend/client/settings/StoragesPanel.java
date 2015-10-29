package com.logicaldoc.gui.frontend.client.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This class shows the storages list and informations.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class StoragesPanel extends VLayout {

	public static final int OPERATION_NONE = 0;

	public static final int OPERATION_ADD = 1;

	public static final int OPERATION_CUMPUTESIZE = 2;

	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private ValuesManager vm = null;

	private DynamicForm storesForm = null;

	private GUIParameter[] parameters = null;

	private String selectedWriteTo = "";

	private RadioGroupItem writeTo = null;

	private RadioGroupItem compression = null;

	private IButton addStorageButton = null;

	private IButton computeSizeButton = null;

	private List<TextItem> storeItems = null;

	private DynamicForm optionsForm = null;

	private LinkedHashMap<String, String> storagesMap = null;

	public StoragesPanel(GUIParameter[] params, ValuesManager valueManager) {
		this.parameters = params;
		this.vm = valueManager;

		setWidth100();
		setHeight100();
		setMembersMargin(5);
		setMargin(5);

		for (GUIParameter param : this.parameters) {
			if (param == null)
				continue;

			if (param.getName().equals("store.write")) {
				selectedWriteTo = "store." + param.getValue() + ".dir";
			}
		}

		HLayout buttons = new HLayout(10);

		addStorageButton = new IButton();
		addStorageButton.setTitle(I18N.message("addnewstorage"));
		addStorageButton.setAutoFit(true);
		addStorageButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				reloadStorages(parameters, selectedWriteTo, true);
			}
		});

		if (!Feature.enabled(Feature.MULTI_STORAGE)) {
			addStorageButton.setDisabled(true);
			addStorageButton.setTooltip(I18N.message("featuredisabled"));
		}

		computeSizeButton = new IButton();
		computeSizeButton.setTitle(I18N.message("computesize"));
		computeSizeButton.setAutoFit(true);
		computeSizeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				computeStoragesSize(parameters, selectedWriteTo);
			}
		});

		if (Feature.visible(Feature.MULTI_STORAGE))
			buttons.setMembers(addStorageButton, computeSizeButton);
		else
			buttons.setMembers(computeSizeButton);
		addMember(buttons, 0);

		storeItems = new ArrayList<TextItem>();
		storagesMap = new LinkedHashMap<String, String>();
		int i = 1;
		List<GUIParameter> storages = Arrays.asList(parameters);
		Collections.sort(storages, new Comparator<GUIParameter>() {
			@Override
			public int compare(GUIParameter o1, GUIParameter o2) {
				if (o1 != null && o2 != null)
					return o1.getName().compareTo(o2.getName());
				else
					return 0;
			}
		});
		for (GUIParameter f : storages) {
			if (f == null || f.getValue().trim().isEmpty())
				continue;

			if (!f.getName().endsWith("write") && !f.getName().endsWith("compress")) {
				TextItem item = ItemFactory.newTextItem(f.getName(), "Storage " + i, f.getValue());
				item.setRequired(true);
				item.setWidth(250);
				storeItems.add(item);

				storagesMap.put("store." + i + ".dir", "Storage " + i);
				i++;
			}
		}

		reloadStorages(this.parameters, selectedWriteTo, false);
	}

	private void reloadStorages(GUIParameter[] params, String selectedItemName, boolean addStorage) {
		if (optionsForm != null) {
			removeMember(optionsForm);
			removeMember(storesForm);
			optionsForm.destroy();
			storesForm.destroy();
		}

		optionsForm = new DynamicForm();
		optionsForm.setWidth(300);
		optionsForm.setColWidths(1, "*");
		optionsForm.setValuesManager(vm);
		optionsForm.setTitleOrientation(TitleOrientation.LEFT);
		addMember(optionsForm, 1);

		writeTo = new RadioGroupItem("writeto", I18N.message("writeto"));
		writeTo.setVertical(false);
		writeTo.setShowTitle(true);
		writeTo.setWrap(false);
		writeTo.setWrapTitle(false);

		compression = ItemFactory.newYesNoRadioItem("compression", "compression");
		if (!Feature.enabled(Feature.COMPRESSED_REPO)) {
			compression.setDisabled(true);
			compression.setTooltip(I18N.message("featuredisabled"));
		}

		// Initialize the radio button
		for (GUIParameter p : params) {
			if (p == null)
				continue;

			if ("store.compress".equals(p.getName()))
				compression.setValue(p.getValue());
		}

		storesForm = new DynamicForm();
		storesForm.setWidth(350);
		storesForm.setColWidths(1, "*");
		storesForm.setValuesManager(vm);
		storesForm.setTitleOrientation(TitleOrientation.LEFT);

		final List<TextItem> newStoreItems = new ArrayList<TextItem>();

		for (TextItem item : storeItems) {
			newStoreItems.add(item);
		}

		if (addStorage) {
			TextItem item = ItemFactory.newTextItem("store." + (newStoreItems.size() + 1) + ".dir", "Storage "
					+ (newStoreItems.size() + 1), "");
			item.setWidth(250);
			newStoreItems.add(item);

			storagesMap.put("store." + (newStoreItems.size()) + ".dir", "Storage " + (newStoreItems.size()));
		}

		writeTo.setDefaultValue(selectedWriteTo);
		writeTo.setValueMap(storagesMap);

		if (Feature.visible(Feature.COMPRESSED_REPO))
			optionsForm.setItems(writeTo, compression);
		else
			optionsForm.setItems(writeTo);

		storeItems = new ArrayList<TextItem>();
		for (TextItem textItem : newStoreItems) {
			storeItems.add(textItem);
		}

		storesForm.setFields(storeItems.toArray(new FormItem[0]));
		addMember(storesForm, 2);
	}

	private void computeStoragesSize(GUIParameter[] repos, String selectedItemName) {
		ContactingServer.get().show();

		service.computeStoragesSize(Session.get().getSid(), new AsyncCallback<GUIParameter[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIParameter[] params) {
				ContactingServer.get().hide();

				for (GUIParameter param : params) {
					for (TextItem item : storeItems) {
						String itemName = item.getName().replaceAll("_", ".").trim();
						String paramName = param.getName().trim();

						if (itemName.equals(paramName))
							item.setHint(Util.formatSizeW7(new Float(param.getValue())));
					}
				}
			}
		});
	}
}
