package com.logicaldoc.gui.frontend.client.folder;

import java.util.Map;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

/**
 * Shows the folder's quotas
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.4.3
 */
public class FolderQuotaPanel extends FolderDetailTab {

	private DynamicForm form = new DynamicForm();

	private ValuesManager vm = new ValuesManager();

	private boolean update = false;

	public FolderQuotaPanel(GUIFolder folder, ChangedHandler changedHandler) {
		super(folder, changedHandler);
		setWidth100();
		setHeight100();
		setMembersMargin(20);
		update = folder.isWorkspace() && Session.get().getUser().isMemberOf("admin");
		refresh();
	}

	private void refresh() {
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

		TextItem documentsQuota = ItemFactory.newLongItem("documentsquota", "documentsquota", folder.getQuotaDocs());
		documentsQuota.setDisabled(!update);
		if (update)
			documentsQuota.addChangedHandler(changedHandler);

		TextItem sizeQuota = ItemFactory.newLongItem("sizequota", "sizequota", folder.getQuotaSize());
		sizeQuota.setHint("MB");
		sizeQuota.setWidth(120);
		sizeQuota.setDisabled(!update);
		if (update)
			sizeQuota.addChangedHandler(changedHandler);

		StaticTextItem size = ItemFactory.newStaticTextItem("ssize", "size", Util.formatSizeW7(folder.getSizeTotal()));
		size.setWrap(false);

		StaticTextItem documents = ItemFactory.newStaticTextItem("documents", "documents",
				Util.formatLong(folder.getDocumentsTotal()));

		documentsQuota.setDisabled(!update);
		sizeQuota.setDisabled(!update);

		form.setItems(documentsQuota, documents, sizeQuota, size);
		addMember(form);
	}

	boolean validate() {
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) vm.getValues();

		vm.validate();
		if (!vm.hasErrors()) {
			if (values.get("documentsquota") == null)
				folder.setQuotaDocs(null);
			else
				folder.setQuotaDocs(new Long(values.get("documentsquota").toString()));

			if (values.get("sizequota") == null)
				folder.setQuotaSize(null);
			else
				folder.setQuotaSize(new Long(values.get("sizequota").toString()));
		}
		return !vm.hasErrors();
	}
}