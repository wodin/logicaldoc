package com.logicaldoc.gui.frontend.client.metadata.stamp;

import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIStamp;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.StampService;
import com.logicaldoc.gui.frontend.client.services.StampServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ColorItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel collects details about a stamp
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.3
 */
public class StampDetailsPanel extends VLayout {
	private GUIStamp stamp;

	private Layout propertiesTabPanel;

	private HLayout savePanel;

	private StampServiceAsync service = (StampServiceAsync) GWT.create(StampService.class);

	private TabSet tabSet = new TabSet();

	private StampsPanel stampsPanel;

	private ValuesManager vm = new ValuesManager();

	private DynamicForm form1 = null;

	private DynamicForm form2 = null;

	private VLayout image = new VLayout();

	private StampUsersPanel usersPanel;

	public StampDetailsPanel(StampsPanel stampsPanel) {
		super();

		this.stampsPanel = stampsPanel;
		setHeight100();
		setWidth100();
		setMembersMargin(10);

		savePanel = new HLayout();
		savePanel.setHeight(20);
		savePanel.setVisible(false);
		savePanel.setStyleName("warn");
		savePanel.setWidth100();
		Button saveButton = new Button(I18N.message("save"));
		saveButton.setAutoFit(true);
		saveButton.setMargin(2);
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave();
			}
		});
		saveButton.setLayoutAlign(VerticalAlignment.CENTER);

		HTMLPane spacer = new HTMLPane();
		spacer.setContents("<div>&nbsp;</div>");
		spacer.setWidth("70%");
		spacer.setOverflow(Overflow.HIDDEN);

		Img closeImage = ItemFactory.newImgIcon("delete.png");
		closeImage.setHeight("16px");
		closeImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (stamp.getId() != 0) {
					service.getStamp(Session.get().getSid(), stamp.getId(), new AsyncCallback<GUIStamp>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(GUIStamp stamp) {
							setStamp(stamp);
						}
					});
				} else {
					GUIStamp newStamp = new GUIStamp();
					setStamp(newStamp);
				}
				savePanel.setVisible(false);
			}
		});
		closeImage.setCursor(Cursor.HAND);
		closeImage.setTooltip(I18N.message("close"));
		closeImage.setLayoutAlign(Alignment.RIGHT);
		closeImage.setLayoutAlign(VerticalAlignment.CENTER);

		savePanel.addMember(saveButton);
		savePanel.addMember(spacer);
		savePanel.addMember(closeImage);
		addMember(savePanel);
	}

	private void refresh() {
		vm.clearErrors(false);
		vm.clearValues();
		vm.resetValues();

		if (form1 != null)
			form1.destroy();

		if (form2 != null)
			form2.destroy();

		if (savePanel != null)
			savePanel.setVisible(false);

		/*
		 * Prepare the standard properties tab
		 */
		if (tabSet != null)
			removeMember(tabSet);

		tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight100();

		Tab propertiesTab = new Tab(I18N.message("properties"));
		propertiesTabPanel = new HLayout();
		propertiesTabPanel.setAlign(VerticalAlignment.TOP);
		propertiesTabPanel.setWidth100();
		propertiesTabPanel.setHeight100();
		propertiesTab.setPane(propertiesTabPanel);
		tabSet.addTab(propertiesTab);

		if (stamp.getId() != 0L) {
			Tab usersTab = new Tab(I18N.message("users"));
			usersPanel = new StampUsersPanel(stamp.getId());
			usersPanel.setHeight100();
			usersTab.setPane(usersPanel);
			tabSet.addTab(usersTab);
		}

		addMember(tabSet);

		ChangedHandler changedHandler = new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				onModified();
			}
		};

		form1 = new DynamicForm();
		form1.setNumCols(2);
		form1.setTitleOrientation(TitleOrientation.TOP);
		form1.setValuesManager(vm);

		form2 = new DynamicForm();
		form2.setNumCols(1);
		form2.setTitleOrientation(TitleOrientation.TOP);
		form2.setValuesManager(vm);

		TextItem name = ItemFactory.newSimpleTextItem("name", "name", stamp.getName());
		name.addChangedHandler(changedHandler);
		name.setRequired(true);
		name.setDisabled(stamp.getId() != 0L);

		TextItem exprx = ItemFactory.newTextItem("exprx", "exprx", stamp.getExprX());
		exprx.addChangedHandler(changedHandler);
		exprx.setWidth(250);

		TextItem expry = ItemFactory.newTextItem("expry", "expry", stamp.getExprY());
		expry.addChangedHandler(changedHandler);
		expry.setWidth(250);

		TextAreaItem description = ItemFactory.newTextAreaItem("description", "description", stamp.getDescription());
		description.addChangedHandler(changedHandler);
		description.setWidth("250");

		final TextAreaItem text = ItemFactory.newTextAreaItem("text", "text", stamp.getText());
		text.addChangedHandler(changedHandler);
		text.setWidth("250");

		final ColorItem color = ItemFactory.newColorItem("color", "color", stamp.getColor());
		color.addChangedHandler(changedHandler);

		final RadioGroupItem type = ItemFactory.newRadioGroup("type", "type");
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("" + GUIStamp.TYPE_IMAGE, I18N.message("image"));
		map.put("" + GUIStamp.TYPE_TEXT, I18N.message("text"));
		type.setValueMap(map);
		type.setValue("" + stamp.getType());

		SpinnerItem rotation = ItemFactory.newSpinnerItem("rotation", "rotation", stamp.getRotation(), 0, 360);
		rotation.addChangedHandler(changedHandler);

		SpinnerItem opacity = ItemFactory.newSpinnerItem("opacity", "opacity", stamp.getOpacity(), 1, 100);
		opacity.addChangedHandler(changedHandler);

		SpinnerItem page = ItemFactory.newSpinnerItem("page", "page", stamp.getPage(), 1, 9999);
		page.addChangedHandler(changedHandler);

		final SpinnerItem size = ItemFactory.newSpinnerItem("size", "size", stamp.getSize(), 1, 9999);
		page.addChangedHandler(changedHandler);

		form1.setItems(name, type, page, exprx, rotation, expry, opacity, description);

		form2.setItems(text, color, size);

		// For the spinners we need to manually update the VM or the widget will
		// not be refreshed
		vm.setValue("rotation", stamp.getRotation());
		vm.setValue("opacity", stamp.getOpacity());

		propertiesTabPanel.setMembers(form1, form2, image);

		ChangedHandler typeChangedhandler = new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (type.getValue().toString().equals("" + GUIStamp.TYPE_IMAGE)) {
					text.hide();
					color.hide();
					size.hide();
					if (stamp.getId() != 0L)
						image.show();
				} else {
					text.show();
					color.show();
					size.show();
					image.hide();
				}
			}
		};
		type.addChangedHandler(typeChangedhandler);
		type.addChangedHandler(changedHandler);
		typeChangedhandler.onChanged(null);

		if (type.getValue().toString().equals("" + GUIStamp.TYPE_IMAGE))
			refreshStampImage();
	}

	static String stampImageUrl(long stampId) {
		return Util.contextPath() + "/stampimage/" + stampId + "?sid=" + Session.get().getSid() + "&random="
				+ new Date().getTime();
	}

	public GUIStamp getStamp() {
		return stamp;
	}

	public void setStamp(GUIStamp stamp) {
		this.stamp = stamp;
		refresh();
	}

	public void onModified() {
		savePanel.setVisible(true);
	}

	public void onSave() {
		if (vm.validate()) {
			stamp.setName(vm.getValueAsString("name"));
			stamp.setType(Integer.parseInt(vm.getValueAsString("type")));
			stamp.setExprX(vm.getValueAsString("exprx"));
			stamp.setExprY(vm.getValueAsString("expry"));
			stamp.setRotation(Integer.parseInt(vm.getValueAsString("rotation")));
			stamp.setOpacity(Integer.parseInt(vm.getValueAsString("opacity")));
			stamp.setSize(Integer.parseInt(vm.getValueAsString("size")));
			
			stamp.setDescription(vm.getValueAsString("description"));
			stamp.setColor(vm.getValueAsString("color"));
			stamp.setText(vm.getValueAsString("text"));

			service.save(Session.get().getSid(), stamp, new AsyncCallback<GUIStamp>() {
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUIStamp newStamp) {
					savePanel.setVisible(false);
					if (stamp.getId() == 0L)
						stampsPanel.init();
					else if (newStamp != null) {
						stampsPanel.updateRecord(newStamp);
						stampsPanel.showStampDetails(newStamp);
					}
					
				}
			});
		}
	}

	void refreshStampImage() {
		if (image != null && propertiesTabPanel.contains(image)) {
			propertiesTabPanel.removeMember(image);
			image.destroy();
		}

		if (stamp.getId() == 0L)
			return;

		image = new VLayout();
		image.setAlign(VerticalAlignment.TOP);
		image.setMargin(1);
		image.setMembersMargin(1);
		String html = "<img border='0' alt='' height='100%' title='' src='" + stampImageUrl(stamp.getId()) + "' />";
		HTMLFlow img = new HTMLFlow(html);
		img.setHeight100();
		img.setWidth100();

		IButton uploadStamp = new IButton(I18N.message("uploadstamp"));
		uploadStamp.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				StampUploader uploader = new StampUploader(stamp.getId(), StampDetailsPanel.this);
				uploader.show();
			}
		});
		uploadStamp.setDisabled(stamp.getId() == 0L);

		image.setMembers(uploadStamp, img);
		propertiesTabPanel.addMember(image);

		stampsPanel.updateRecord(stamp);
	}
}