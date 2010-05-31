package com.logicaldoc.gui.frontend.client.system;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * The bottom side of the general panel
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GeneralBottom extends HLayout {

	private TabSet tabs = new TabSet();

	public GeneralBottom() {
		setWidth100();
		setMembersMargin(10);

		Tab system = new Tab();
		system.setTitle(I18N.message("system"));

		HLayout form = new HLayout();
		form.setWidth(200);

		DynamicForm systemForm = new DynamicForm();
		systemForm.setWidth(300);
		systemForm.setColWidths(1, "*");

		StaticTextItem productName = ItemFactory.newStaticTextItem("productName", "", "<b>"
				+ Util.getContext().get("product_name") + "</b>");
		productName.setShouldSaveValue(false);
		productName.setWrapTitle(false);

		StaticTextItem version = ItemFactory.newStaticTextItem("version", "", I18N.message("version") + " "
				+ Util.getContext().get("product_release"));
		version.setShouldSaveValue(false);

		StaticTextItem vendor = ItemFactory.newStaticTextItem("vendor", "", "&copy; "
				+ Util.getContext().get("product_vendor"));
		vendor.setShouldSaveValue(false);

		StaticTextItem address = ItemFactory.newStaticTextItem("address", "", Util.getContext().get(
				"product_vendor_address"));
		address.setShouldSaveValue(false);

		StaticTextItem capAndCity = ItemFactory.newStaticTextItem("capAndCity", "", Util.getContext().get(
				"product_vendor_cap")
				+ "  " + Util.getContext().get("product_vendor_city"));
		capAndCity.setShouldSaveValue(false);

		StaticTextItem country = ItemFactory.newStaticTextItem("country", "", Util.getContext().get(
				"product_vendor_country"));
		country.setShouldSaveValue(false);

		DynamicForm supportForm = new DynamicForm();
		supportForm.setAlign(Alignment.LEFT);
		supportForm.setTitleOrientation(TitleOrientation.TOP);
		supportForm.setColWidths(1);
		supportForm.setWrapItemTitles(false);
		supportForm.setMargin(8);
		supportForm.setNumCols(1);

		LinkItem support = new LinkItem();
		support.setName(I18N.message("support"));
		support.setLinkTitle(Util.getContext().get("product_support"));
		support.setValue("mailto:" + Util.getContext().get("product_support") + "?subject=LogicalDOC Support - UUID("
				+ Util.getContext().get("id") + ")");
		support.setRequired(true);
		support.setShouldSaveValue(false);

		StaticTextItem installationID = ItemFactory.newStaticTextItem("", "installid", Util.getContext().get("id"));
		installationID.setRequired(true);
		installationID.setShouldSaveValue(false);

		systemForm.setItems(productName, version, vendor, address, capAndCity, country);

		supportForm.setItems(support, installationID);

		form.addMember(systemForm);
		form.addMember(supportForm);

		system.setPane(form);

		Tab sessions = new Tab();
		sessions.setTitle(I18N.message("sessions"));
		sessions.setPane(new SessionsPanel());

		tabs.setTabs(system, sessions);

		setMembers(tabs);
	}
}
