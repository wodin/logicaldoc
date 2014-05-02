package com.logicaldoc.gui.frontend.client.tenant;

import java.util.Map;

import com.logicaldoc.gui.common.client.beans.GUITenant;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Shows document's standard properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TenantQuotaPanel extends HLayout {
	private DynamicForm form = new DynamicForm();

	private ValuesManager vm = new ValuesManager();

	private GUITenant tenant;

	private ChangedHandler changedHandler;

	private VLayout layout = new VLayout();

	public TenantQuotaPanel(GUITenant tenant, ChangedHandler changedHandler) {
		if (tenant == null) {
			setMembers(TenantsPanel.SELECT_TENANT);
		} else {
			this.tenant = tenant;
			this.changedHandler = changedHandler;
			setWidth100();
			setHeight100();
			setMembersMargin(20);
			layout.setWidth(300);
			refresh();
		}
	}

	public void refresh() {
		boolean readonly = (changedHandler == null);
		vm.clearValues();
		vm.clearErrors(false);

		if (form != null)
			form.destroy();

		if (contains(form))
			removeChild(form);

		form = new DynamicForm();
		form.setValuesManager(vm);
		form.setWrapItemTitles(false);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(2);

		layout.addMember(form, 1);

		TextItem users = ItemFactory.newIntegerItem("users", "users", tenant.getMaxUsers());
		users.setDisabled(readonly);
		if (!readonly)
			users.addChangedHandler(changedHandler);

		TextItem sessions = ItemFactory.newIntegerItem("sessions", "sessions", tenant.getMaxSessions());
		sessions.setDisabled(readonly);
		if (!readonly)
			sessions.addChangedHandler(changedHandler);

		TextItem documents = ItemFactory.newLongItem("documents", "documents", tenant.getMaxRepoDocs());
		documents.setDisabled(readonly);
		if (!readonly)
			documents.addChangedHandler(changedHandler);

		TextItem size = ItemFactory.newLongItem("size", "size", tenant.getMaxRepoSize());
		size.setHint("MB");
		size.setDisabled(readonly);
		if (!readonly)
			size.addChangedHandler(changedHandler);

		form.setItems(users, sessions, documents, size);
		addMember(layout);
	}

	@SuppressWarnings("unchecked")
	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			if (values.get("documents") == null)
				tenant.setMaxRepoDocs(null);
			else
				tenant.setMaxRepoDocs(new Long(values.get("documents").toString()));

			if (values.get("size") == null)
				tenant.setMaxRepoSize(null);
			else
				tenant.setMaxRepoSize(new Long(values.get("size").toString()));

			if (values.get("users") == null)
				tenant.setMaxUsers(null);
			else
				tenant.setMaxUsers(new Integer(values.get("users").toString()));

			if (values.get("sessions") == null)
				tenant.setMaxSessions(null);
			else
				tenant.setMaxSessions(new Integer(values.get("sessions").toString()));
		}

		return !vm.hasErrors();
	}
}