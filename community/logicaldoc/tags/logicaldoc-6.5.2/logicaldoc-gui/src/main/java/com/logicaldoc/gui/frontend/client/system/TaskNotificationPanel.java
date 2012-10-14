package com.logicaldoc.gui.frontend.client.system;

import java.util.ArrayList;
import java.util.List;

import com.logicaldoc.gui.common.client.beans.GUITask;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows notification settings for a task
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.1
 */
public class TaskNotificationPanel extends VLayout {
	private VLayout notificationsPane = new VLayout();

	private DynamicForm notificationsForm;

	private ChangedHandler changedHandler;

	private GUITask task;

	public TaskNotificationPanel(GUITask task, ChangedHandler changedHandler) {
		setWidth100();
		this.changedHandler = changedHandler;
		this.task = task;
		setMembers(notificationsPane);
		refreshNotifications();
	}

	private void refreshNotifications() {
		if (notificationsForm != null && notificationsPane.contains(notificationsForm)) {
			notificationsPane.removeMember(notificationsForm);
			notificationsForm.destroy();
		}

		notificationsForm = new DynamicForm();
		notificationsForm.setColWidths(1, "*");
		notificationsForm.setMargin(3);

		List<FormItem> items = new ArrayList<FormItem>();

		// Enable/Disable notifications
		CheckboxItem sendReport = new CheckboxItem();
		sendReport.setName("sendReport");
		sendReport.setTitle(I18N.message("sendactivityreport"));
		sendReport.setRedrawOnChange(true);
		sendReport.setWidth(50);
		sendReport.setValue(task.isSendActivityReport());
		sendReport.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				task.setSendActivityReport("true".equals(notificationsForm.getValue("sendReport").toString()));

				// Notify the external handler
				changedHandler.onChanged(event);
			}
		});

		items.add(sendReport);

		final SelectItem user = ItemFactory.newUserSelector("notificationUsers", "user");
		user.setHintStyle("hint");
		user.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (user.getSelectedRecord() == null)
					return;
				GUIUser u = new GUIUser();
				u.setId(Long.parseLong(user.getSelectedRecord().getAttribute("id")));
				u.setUserName(user.getSelectedRecord().getAttribute("username"));
				task.addReportRecipient(u);
				user.clearValue();
				refreshNotifications();

				// Notify the external handler
				changedHandler.onChanged(event);
			}
		});
		items.add(user);

		FormItemIcon icon = ItemFactory.newItemIcon("delete.png");
		int i = 0;

		for (GUIUser u : task.getReportRecipients()) {
			final StaticTextItem usrItem = ItemFactory.newStaticTextItem("usr" + i++, "user", u.getUserName());
			usrItem.setIcons(icon);
			usrItem.addIconClickHandler(new IconClickHandler() {
				public void onIconClick(IconClickEvent event) {
					task.removeNotifiedUser((String) usrItem.getValue());
					changedHandler.onChanged(null);
					refreshNotifications();
				}
			});
			items.add(usrItem);
		}

		notificationsForm.setItems(items.toArray(new FormItem[0]));
		notificationsPane.setMembers(notificationsForm);
	}
}