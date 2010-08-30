package com.logicaldoc.gui.frontend.client.document;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to visualize the details of a selected workflow.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class WorkflowDetailsDialog extends Window {

	private GUIWorkflow workflow = null;

	public WorkflowDetailsDialog(GUIWorkflow workflow) {
		this.workflow = workflow;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("workflow"));
		setWidth(600);
		setHeight(400);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		VLayout layout = new VLayout(10);
		layout.setMargin(25);

		DynamicForm workflowForm = new DynamicForm();
		workflowForm.setWidth(300);
		workflowForm.setColWidths(1, "*");

		StaticTextItem workflowTitle = ItemFactory.newStaticTextItem("workflow", "", "<b>" + I18N.message("workflow")
				+ "</b>");
		workflowTitle.setShouldSaveValue(false);
		workflowTitle.setWrapTitle(false);

		StaticTextItem workflowName = ItemFactory.newStaticTextItem("workflowName", I18N.message("name"),
				workflow.getName());
		workflowName.setShouldSaveValue(false);

		StaticTextItem workflowDescription = ItemFactory.newStaticTextItem("workflowDescription", I18N.message("description"),
				workflow.getDescription());
		workflowDescription.setShouldSaveValue(false);

		StaticTextItem address = ItemFactory.newStaticTextItem("address", "", Session.get().getInfo()
				.getVendorAddress());
		address.setShouldSaveValue(false);

		StaticTextItem capAndCity = ItemFactory.newStaticTextItem("capAndCity", "", Session.get().getInfo()
				.getVendorCap()
				+ "  " + Session.get().getInfo().getVendorCity());
		capAndCity.setShouldSaveValue(false);

		StaticTextItem country = ItemFactory.newStaticTextItem("country", "", Session.get().getInfo()
				.getVendorCountry());
		country.setShouldSaveValue(false);

		workflowForm.setItems(workflowTitle, workflowName, workflowDescription, address, capAndCity, country);

		layout.addMember(workflowForm);

		addChild(layout);
	}
}
