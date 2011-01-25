package com.logicaldoc.gui.frontend.client.security;

import java.util.Map;

import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Shows user's quota settings and values.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class UserQuotaPanel extends HLayout {
	private DynamicForm form1 = new DynamicForm();

	private ValuesManager vm = new ValuesManager();

	private GUIUser user;

	private ChangedHandler changedHandler;

	public UserQuotaPanel(GUIUser user, ChangedHandler changedHandler) {
		this.user = user;
		this.changedHandler = changedHandler;

		setWidth100();
		setHeight100();
		setMembersMargin(20);
		refresh();
	}

	private void refresh() {
		boolean readonly = (changedHandler == null);
		vm.clearValues();
		vm.clearErrors(false);

		if (form1 != null)
			form1.destroy();

		if (contains(form1))
			removeChild(form1);
		form1 = new DynamicForm();
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.TOP);

		IntegerItem quota = ItemFactory.newIntegerItem("quota", "quota", null);
		quota.setRequired(false);
		quota.setValue(user.getQuota() >= 0 ? user.getQuota() / (1024 * 1024) : 0);
		quota.setHint("MB");
		if (!readonly)
			quota.addChangedHandler(changedHandler);

		IntegerItem quotaCount = ItemFactory.newIntegerItem("quotaCount", "quotacount", null);
		quotaCount.setRequired(false);
		quotaCount.setDisabled(true);
		quotaCount.setValue(user.getQuotaCount() / (1024 * 1024));
		quotaCount.setHint("MB");

		form1.setItems(quota, quotaCount);
		addMember(form1);

	}

	@SuppressWarnings("unchecked")
	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			long quota;
			if (values.get("quota") instanceof String)
				quota = Integer.parseInt((String) values.get("quota"));
			else
				quota = (Integer) values.get("quota");
			user.setQuota(quota * (1024 * 1024));
		}
		return !vm.hasErrors();
	}
}
