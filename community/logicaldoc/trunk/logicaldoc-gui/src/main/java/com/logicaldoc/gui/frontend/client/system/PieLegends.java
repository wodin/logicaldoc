package com.logicaldoc.gui.frontend.client.system;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;


/**
 * Shows the charts legends
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class PieLegends extends HLayout {

	public PieLegends() {
		super();
		setMembersMargin(30);
		setHeight(100);
		setWidth100();

		addMember(prepareLegend());
		addMember(prepareLegend());
		addMember(prepareLegend());
	}

	private DynamicForm prepareLegend() {
		DynamicForm systemForm = new DynamicForm();
		systemForm.setWidth("33%");
		systemForm.setColWidths(25, "*");

		StaticTextItem productName = new StaticTextItem();
		productName.setName("");
		productName.setTitle("");
		productName.setValue("<b>" + Util.getContext().get("product_name") + "</b>");

		StaticTextItem version = new StaticTextItem();
		version.setName("");
		version.setTitle("");
		version.setValue(I18N.getMessage("version") + " " + Util.getContext().get("product_release"));

		StaticTextItem vendor = new StaticTextItem();
		vendor.setName("");
		vendor.setTitle("");
		vendor.setValue("&copy; " + Util.getContext().get("product_vendor"));

		StaticTextItem address = new StaticTextItem();
		address.setName("");
		address.setTitle("");
		address.setValue(Util.getContext().get("product_vendor_address"));

		StaticTextItem capAndCity = new StaticTextItem();
		capAndCity.setName("");
		capAndCity.setTitle("");
		capAndCity.setValue(Util.getContext().get("product_vendor_cap") + "  "
				+ Util.getContext().get("product_vendor_city"));

		StaticTextItem country = new StaticTextItem();
		country.setName("");
		country.setTitle("");
		country.setValue(Util.getContext().get("product_vendor_country"));

		systemForm.setItems(productName, version, vendor, address, capAndCity, country);
		return systemForm;
	}

}
