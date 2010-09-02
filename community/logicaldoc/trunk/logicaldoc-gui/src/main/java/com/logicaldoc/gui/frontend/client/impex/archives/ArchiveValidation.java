package com.logicaldoc.gui.frontend.client.impex.archives;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.services.ArchiveService;
import com.logicaldoc.gui.frontend.client.services.ArchiveServiceAsync;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This popup window is used to start an archive validation.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class ArchiveValidation extends Window {

	private ArchiveServiceAsync service = (ArchiveServiceAsync) GWT.create(ArchiveService.class);

	private VLayout layout = null;

	private TabSet tabs = new TabSet();

	private Tab workflowSettings = null;

	private Tab workflowAssignment = null;

	private Tab chooseWorkflow = null;

	private ListGrid deployedWorkflowsList;

	private ListGrid docsAppendedList;

	private String wflName = "";

	private String wflDescription = "";

	private String docIds = "";

	private IButton startWorkflow = null;

	private GUIArchive selectedArchive = null;

	private ValuesManager vm = new ValuesManager();

	private DataSource datasource = null;

	private VLayout workflowSettingsLayout = null;

	private DynamicForm workflowSettingsForm = new DynamicForm();;

	private TextAreaItem wflDescriptionItem = null;

	private Layout wflLayout = null;

	public ArchiveValidation(GUIArchive archive) {
		this.selectedArchive = archive;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("startworkflow"));
		setWidth(600);
		setHeight(400);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		layout = new VLayout(20);
		layout.setMargin(25);

		refreshTabs("", "", 0);
	}

	public void refreshTabs(String workflowName, String workflowDescription, int selectedTab) {
		
	}
}
