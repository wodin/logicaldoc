package com.logicaldoc.gui.frontend.client.workflow;

import java.util.Map;

import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * A box displaying a single workflow primitive
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WorkflowState extends VStack {

	protected Label title;

	protected HLayout commands = new HLayout();

	private WorkflowDesigner designer = null;

	private GUIWFState wfState = null;

	private ValuesManager vm = new ValuesManager();

	public WorkflowState(WorkflowDesigner designer, GUIWFState wfState) {
		this.designer = designer;
		this.wfState = wfState;
		setHeight(40);
		setWidth(150);
		setBorder("1px solid #dddddd");
		setCanDrag(true);
		setCanDrop(true);
		setDragType("state");

		if (designer.getWorkflow() != null && designer.getWorkflow().getStartStateId().equals(wfState.getId())) {
			setBorder("1px dotted #00ff00");
		}

		title = new Label(this.wfState.getName());
		addMember(title);
		title.setHeight(21);
		title.setWrap(false);
		title.setIcon(this.wfState.getIcon());
		title.setContents(this.wfState.getName());

		commands.setHeight(12);
		commands.setWidth(1);
		commands.setAlign(Alignment.RIGHT);
		addMember(commands);

		HTML delete = new HTML("<a href='#'>" + I18N.message("ddelete").toLowerCase() + "</a>");
		delete.setWidth("1px");
		delete.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				getDesigner().onStateDelete(getWfState());
			}
		});
		commands.addMember(delete);

		HTML edit = new HTML("&nbsp;&nbsp;<a href='#'>" + I18N.message("edit").toLowerCase() + "</a>");
		edit.setWidth("1px");
		edit.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				getDesigner().onStateSelect(getWfState());
			}
		});
		commands.addMember(edit);

		if (getWfState().getType() == GUIWFState.TYPE_TASK) {
			HTML addTransition = new HTML("&nbsp;&nbsp;<a href='#'>" + I18N.message("addtransition").toLowerCase()
					+ "</a>");
			addTransition.setWidth("1px");
			addTransition.setWordWrap(false);
			addTransition.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
				@Override
				public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
					final Window window = new Window();
					window.setTitle(I18N.message("addtransition"));
					window.setWidth(250);
					window.setHeight(200);
					window.setCanDragResize(true);
					window.setIsModal(true);
					window.setShowModalMask(true);
					window.centerInPage();

					DynamicForm form = new DynamicForm();
					form.setTitleOrientation(TitleOrientation.TOP);
					form.setNumCols(1);
					form.setValuesManager(vm);
					TextItem name = ItemFactory.newTextItem("name", "name", null);
					name.setRequired(true);

					SubmitItem saveButton = new SubmitItem("save", I18N.message("save"));
					saveButton.setAlign(Alignment.LEFT);
					saveButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							final Map<String, Object> values = vm.getValues();
							if (vm.validate()) {
								getDesigner().onAddTransition(getWfState(), null, (String) values.get("name"));
							}
							window.destroy();
						}
					});

					form.setFields(name, saveButton);

					window.addItem(form);
					window.show();
				}
			});
			commands.addMember(addTransition);
		}
	}

	public WorkflowDesigner getDesigner() {
		return designer;
	}

	public GUIWFState getWfState() {
		return wfState;
	}
}