package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.data.UsersDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ItemClickEvent;
import com.smartgwt.client.widgets.menu.events.ItemClickHandler;

public class SupervisorSelector extends StaticTextItem {

	private Long supervisorId;

	private Menu menu = new Menu();

	public SupervisorSelector() {
		setName("supervisor");
		setTitle(I18N.message("supervisor"));
		setValue("");
		setRedrawOnChange(true);
		setValueField("id");
		setDisplayField("username");

		menu.setCanSelectParentItems(true);
		menu.setDataSource(UsersDS.get());
		menu.setWidth(130);
		menu.addItemClickHandler(new ItemClickHandler() {
			public void onItemClick(ItemClickEvent event) {
				MenuItem item = event.getItem();
				setSupervisor(new Long(item.getAttributeAsString("id")), item.getAttributeAsString("username"));
			}
		});

		FormItemIcon picker = new FormItemIcon();
		addIconClickHandler(new IconClickHandler() {
			public void onIconClick(IconClickEvent event) {
				// menu.showContextMenu();
				final Window window = new Window();
				window.setTitle(I18N.message("supervisor"));
				window.setWidth(250);
				window.setHeight(200);
				window.setCanDragResize(true);
				// window.setIsModal(true);
				// window.setShowModalMask(true);
				window.centerInPage();

				DynamicForm form = new DynamicForm();
				form.setTitleOrientation(TitleOrientation.TOP);
				form.setNumCols(1);
				final ComboBoxItem user = ItemFactory.newUserSelector("user", "user");
				
				user.addChangedHandler(new ChangedHandler() {
					@Override
					public void onChanged(ChangedEvent event) {
						setSupervisor(new Long(user.getSelectedRecord().getAttribute("id")), user.getSelectedRecord()
								.getAttribute("username"));
						window.destroy();
					}
				});

				form.setFields(user);

				window.addItem(form);
				window.show();
			}
		});

		FormItemIcon icon = new FormItemIcon();
		icon.setSrc("[SKIN]/actions/remove.png");
		addIconClickHandler(new IconClickHandler() {
			public void onIconClick(IconClickEvent event) {
				setValue("");
				supervisorId = null;
				redraw();
			}
		});

		setIcons(picker, icon);
	}

	public void setSupervisor(Long supervisorId, String name) {
		this.supervisorId = supervisorId;
		setValue(name);
		redraw();
	}

	public Long getSupervisorId() {
		return supervisorId;
	}

	public String getSupervisorName() {
		return (String) getValue();
	}
}
