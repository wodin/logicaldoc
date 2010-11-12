package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle rights on a folder. It is based on Xml parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class RightsDS extends DataSource {
	public RightsDS(long id, boolean folder) {
		setTitleField("entity");
		setRecordXPath("/list/right");
		DataSourceTextField entity = new DataSourceTextField("entity");
		DataSourceTextField entityId = new DataSourceTextField("entityId");
		entityId.setPrimaryKey(true);
		DataSourceBooleanField read = new DataSourceBooleanField("read");
		DataSourceBooleanField write = new DataSourceBooleanField("write");
		DataSourceBooleanField delete = new DataSourceBooleanField("delete");
		DataSourceBooleanField add = new DataSourceBooleanField("add");
		DataSourceBooleanField workflow = new DataSourceBooleanField("workflow");
		DataSourceBooleanField sign = new DataSourceBooleanField("sign");
		DataSourceBooleanField _import = new DataSourceBooleanField("import");
		DataSourceBooleanField export = new DataSourceBooleanField("export");
		DataSourceBooleanField immutable = new DataSourceBooleanField("immutable");
		DataSourceBooleanField rename = new DataSourceBooleanField("rename");
		DataSourceBooleanField security = new DataSourceBooleanField("security");
		DataSourceBooleanField archive = new DataSourceBooleanField("archive");
		DataSourceBooleanField download = new DataSourceBooleanField("download");
		DataSourceTextField type = new DataSourceTextField("type");

		setFields(entityId, entity, read, write, delete, add, workflow, sign, _import, export, immutable, rename,
				security, archive, type, download);
		setClientOnly(true);
		setDataURL("data/rights.xml?sid=" + Session.get().getSid() + "&" + (folder ? "folderId" : "menuId") + "=" + id
				+ "&locale=" + I18N.getLocale());
	}
}