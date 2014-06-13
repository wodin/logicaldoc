package com.logicaldoc.gui.frontend.client.tenant;

import java.util.Map;

import com.logicaldoc.gui.common.client.beans.GUITenant;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
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
		vm.clearErrors(true);
		vm = new ValuesManager();

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

		TextItem usersQuota = ItemFactory.newIntegerItem("usersquota", "usersquota", tenant.getMaxUsers());
		usersQuota.setDisabled(readonly);
		if (!readonly)
			usersQuota.addChangedHandler(changedHandler);

		TextItem sessionsQuota = ItemFactory.newIntegerItem("sessionsquota", "sessionsquota", tenant.getMaxSessions());
		sessionsQuota.setDisabled(readonly);
		if (!readonly)
			sessionsQuota.addChangedHandler(changedHandler);

		TextItem documentsQuota = ItemFactory.newLongItem("documentsquota", "documentsquota", tenant.getMaxRepoDocs());
		documentsQuota.setDisabled(readonly);
		if (!readonly)
			documentsQuota.addChangedHandler(changedHandler);

		TextItem sizeQuota = ItemFactory.newLongItem("sizequota", "sizequota", tenant.getMaxRepoSize());
		sizeQuota.setHint("MB");
		sizeQuota.setWidth(120);
		sizeQuota.setDisabled(readonly);
		if (!readonly)
			sizeQuota.addChangedHandler(changedHandler);

		StaticTextItem size = ItemFactory.newStaticTextItem("ssize", "size", Util.formatSizeW7(tenant.getSize()));
		size.setWrap(false);

		StaticTextItem documents = ItemFactory.newStaticTextItem("documents", "documents",
				Util.formatLong(tenant.getDocuments()));
		StaticTextItem sessions = ItemFactory.newStaticTextItem("sessions", "sessions",
				Util.formatLong(tenant.getSessions()));
		StaticTextItem users = ItemFactory.newStaticTextItem("users", "users", Util.formatLong(tenant.getUsers()));

		form.setItems(usersQuota, users, sessionsQuota, sessions, documentsQuota, documents, sizeQuota, size);
		addMember(layout);
	}

	@SuppressWarnings("unchecked")
	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			if (values.get("documentsquota") == null)
				tenant.setMaxRepoDocs(null);
			else
				tenant.setMaxRepoDocs(new Long(values.get("documentsquota").toString()));

			if (values.get("sizequota") == null)
				tenant.setMaxRepoSize(null);
			else
				tenant.setMaxRepoSize(new Long(values.get("sizequota").toString()));

			if (values.get("usersquota") == null)
				tenant.setMaxUsers(null);
			else
				tenant.setMaxUsers(new Integer(values.get("usersquota").toString()));

			if (values.get("sessionsquota") == null)
				tenant.setMaxSessions(null);
			else
				tenant.setMaxSessions(new Integer(values.get("sessionsquota").toString()));
		}

		return !vm.hasErrors();
	}
}