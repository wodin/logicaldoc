package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle menus. It is based on Xml parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MenusDS extends DataSource {

	public MenusDS() {
		setTitleField("name");
		setRecordXPath("/list/menu");
		DataSourceTextField name = new DataSourceTextField("name", I18N.message("name"), 255);

		DataSourceTextField id = new DataSourceTextField("id", I18N.message("id"));
		id.setPrimaryKey(true);
		id.setRequired(true);
		
		DataSourceIntegerField position = new DataSourceIntegerField("position", I18N.message("position"));
		
		setFields(name, id, position);

		setDataURL("data/menues.xml?sid=" + Session.get().getSid());
		setClientOnly(false);
	}
}