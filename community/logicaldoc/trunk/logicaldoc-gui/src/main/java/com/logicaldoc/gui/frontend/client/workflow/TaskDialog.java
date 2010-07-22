package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This is the form used for the workflow task setting.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class TaskDialog extends Window {

	public TaskDialog() {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("editworkflowstate", I18N.message("task")));
		setWidth(290);
		setHeight(350);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		DynamicForm taskForm = new DynamicForm();
		taskForm.setTitleOrientation(TitleOrientation.TOP);
		taskForm.setNumCols(1);
		TextItem taskName = ItemFactory.newTextItem("taskName", "name", null);
		taskName.setRequired(true);
		TextAreaItem taskDescr = ItemFactory.newTextAreaItem("taskDescr", "description", null);
		taskDescr.setWrapTitle(false);
		taskForm.setFields(taskName, taskDescr);
		addItem(taskForm);

		DynamicForm escalationFormItem = new DynamicForm();
		escalationFormItem.setTitleOrientation(TitleOrientation.TOP);
		StaticTextItem escalation = ItemFactory.newStaticTextItem("escalationManagement", "",
				"<b>" + I18N.message("escalationmanagement") + "</b>");
		escalation.setShouldSaveValue(false);
		escalation.setWrapTitle(false);
		escalation.setWrap(false);
		escalationFormItem.setItems(escalation);
		addItem(escalationFormItem);

		DynamicForm escalationForm = new DynamicForm();
		escalationForm.setTitleOrientation(TitleOrientation.LEFT);
		escalationForm.setNumCols(4);
		escalationForm.setColWidths("35", "35", "50", "130");
		SpinnerItem duedateTimeItem = new SpinnerItem();
		duedateTimeItem.setTitle(I18N.message("duedate"));
		duedateTimeItem.setDefaultValue(0);
		duedateTimeItem.setMin(0);
		duedateTimeItem.setStep(1);
		duedateTimeItem.setWidth(50);
		SelectItem duedateTime = ItemFactory.newTimeSelector("duedateTime", "");

		SpinnerItem remindTimeItem = new SpinnerItem();
		remindTimeItem.setTitle(I18N.message("remindtime"));
		remindTimeItem.setDefaultValue(0);
		remindTimeItem.setMin(0);
		remindTimeItem.setStep(1);
		remindTimeItem.setWidth(50);
		SelectItem remindTime = ItemFactory.newTimeSelector("remindTime", "");
		escalationForm.setFields(duedateTimeItem, duedateTime, remindTimeItem, remindTime);
		addItem(escalationForm);

		DynamicForm separator1Form = new DynamicForm();
		separator1Form.setHeight(15);

		DynamicForm participantsForm = new DynamicForm();
		participantsForm.setTitleOrientation(TitleOrientation.TOP);
		participantsForm.setNumCols(1);
		ComboBoxItem participants = ItemFactory.newUserSelector("participants", "");
		participants.setTitle("<b>" + I18N.message("participants") + "</b>");
		participants.setTitleOrientation(TitleOrientation.TOP);
		participantsForm.setItems(participants);
		addItem(participantsForm);

		DynamicForm separator2Form = new DynamicForm();
		separator2Form.setHeight(15);

		DynamicForm transitionsForm = new DynamicForm();
		transitionsForm.setTitleOrientation(TitleOrientation.TOP);
		transitionsForm.setNumCols(1);
		ComboBoxItem transitions = ItemFactory.newUserSelector("transitions", "");
		transitions.setTitle("<b>" + I18N.message("transitions") + "</b>");
		transitions.setTitleOrientation(TitleOrientation.TOP);
		transitionsForm.setItems(transitions);
		addItem(transitionsForm);

		DynamicForm buttonForm = new DynamicForm();
		ButtonItem saveItem = new ButtonItem();
		saveItem.setTitle(I18N.message("save"));
		saveItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				destroy();
			}
		});
		buttonForm.setItems(saveItem);
		addItem(buttonForm);
	}

}
