package com.logicaldoc.gui.common.client.data;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all import folders. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ImportFoldersDS extends DataSource {
	private static ImportFoldersDS instance;

	private static ImportFoldersDS instanceWithEmpty;

	private ImportFoldersDS(boolean withEmpty) {
		setTitleField("template");
		setRecordXPath("/list/folder");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		DataSourceTextField src = new DataSourceTextField("src");
		DataSourceTextField type = new DataSourceTextField("type");
		DataSourceTextField provider = new DataSourceTextField("provider");
		DataSourceImageField enabled = new DataSourceImageField("eenabled");

		setFields(id, src, type, provider, enabled);
		setDataURL("data/importfolders.xml");
		setClientOnly(true);
	}

	public static ImportFoldersDS get() {
		if (instance == null)
			instance = new ImportFoldersDS(false);
		return instance;
	}

	public static ImportFoldersDS getInstanceWithEmpty() {
		if (instanceWithEmpty == null)
			instanceWithEmpty = new ImportFoldersDS(true);
		return instanceWithEmpty;
	}
}