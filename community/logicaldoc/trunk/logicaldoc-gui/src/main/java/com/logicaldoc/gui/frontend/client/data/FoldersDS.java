package com.logicaldoc.gui.frontend.client.data;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle folders and sub-folders structures. It is based on Xml
 * parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FoldersDS extends DataSource {
	public FoldersDS() {
		setID("FoldersDS");
		setTitleField("name");
		setRecordXPath("/list/folder");
		DataSourceTextField nameField = new DataSourceTextField("name", I18N.getMessage("name"), 255);

		DataSourceTextField folderId = new DataSourceTextField("id", I18N.getMessage("id"));
		folderId.setPrimaryKey(true);
		folderId.setRequired(true);

		DataSourceTextField parentId = new DataSourceTextField("parent", "Parent ID");
		parentId.setRequired(true);
		parentId.setForeignKey("FoldersDS.id");
		parentId.setRootValue("-1");

		DataSourceBooleanField add = new DataSourceBooleanField(Constants.PERMISSION_ADD);
		DataSourceBooleanField delete = new DataSourceBooleanField(Constants.PERMISSION_DELETE);

		setFields(nameField, folderId, parentId, add, delete);

		setDataURL("data/folders.xml?sid=" + Session.getInstance().getSid());
		setClientOnly(false);
	}
}