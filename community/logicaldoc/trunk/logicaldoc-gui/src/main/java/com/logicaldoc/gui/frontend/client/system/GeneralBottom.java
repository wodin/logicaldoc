package com.logicaldoc.gui.frontend.client.system;

import com.logicaldoc.gui.common.client.I18N;
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
		system.setTitle(I18N.getMessage("system"));

		HLayout form = new HLayout();
		form.setWidth(200);

		DynamicForm systemForm = new DynamicForm();
		systemForm.setWidth(300);
		systemForm.setColWidths(1, "*");

		StaticTextItem productName = new StaticTextItem();
		productName.setName("productName");
		productName.setShouldSaveValue(false);
		productName.setTitle("");
		productName.setWrapTitle(false);
		productName.setValue("<b>" + Util.getContext().get("product_name") + "</b>");

		StaticTextItem version = new StaticTextItem();
		version.setName("version");
		version.setTitle("");
		version.setShouldSaveValue(false);
		version.setValue(I18N.getMessage("version") + " " + Util.getContext().get("product_release"));

		StaticTextItem vendor = new StaticTextItem();
		vendor.setName("vendor");
		vendor.setTitle("");
		vendor.setShouldSaveValue(false);
		vendor.setValue("&copy; " + Util.getContext().get("product_vendor"));

		StaticTextItem address = new StaticTextItem();
		address.setName("address");
		address.setTitle("");
		address.setShouldSaveValue(false);
		address.setValue(Util.getContext().get("product_vendor_address"));

		StaticTextItem capAndCity = new StaticTextItem();
		capAndCity.setName("capAndCity");
		capAndCity.setTitle("");
		capAndCity.setShouldSaveValue(false);
		capAndCity.setValue(Util.getContext().get("product_vendor_cap") + "  "
				+ Util.getContext().get("product_vendor_city"));

		StaticTextItem country = new StaticTextItem();
		country.setName("country");
		country.setTitle("");
		country.setShouldSaveValue(false);
		country.setValue(Util.getContext().get("product_vendor_country"));

		DynamicForm supportForm = new DynamicForm();
		supportForm.setAlign(Alignment.LEFT);
		supportForm.setTitleOrientation(TitleOrientation.TOP);
		supportForm.setColWidths(1);
		supportForm.setWrapItemTitles(false);
		supportForm.setMargin(8);
		supportForm.setNumCols(1);

		LinkItem support = new LinkItem();
		support.setName(I18N.getMessage("support"));
		support.setLinkTitle(Util.getContext().get("product_support"));
		support.setValue("mailto:" + Util.getContext().get("product_support") + "?subject=LogicalDOC Support - UUID("
				+ Util.getContext().get("id") + ")");
		support.setRequired(true);
		support.setShouldSaveValue(false);

		StaticTextItem installationID = new StaticTextItem();
		installationID.setName("");
		installationID.setTitle(I18N.getMessage("installid"));
		installationID.setValue(Util.getContext().get("id"));
		installationID.setRequired(true);
		installationID.setShouldSaveValue(false);

		systemForm.setItems(productName, version, vendor, address, capAndCity, country);

		supportForm.setItems(support, installationID);

		form.addMember(systemForm);
		form.addMember(supportForm);

		system.setPane(form);

		Tab sessions = new Tab();
		sessions.setTitle(I18N.getMessage("sessions"));
		sessions.setPane(new SessionsPanel());

		tabs.setTabs(system, sessions);

		setMembers(tabs);
	}
}
