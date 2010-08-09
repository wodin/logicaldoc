package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to handle impex archives grid lists. It is based on Xml parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ArchivesDS extends DataSource {
	public ArchivesDS(int mode) {
		setTitleField("name");
		setRecordXPath("/list/archive");

		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setHidden(true);
		id.setRequired(true);

		DataSourceTextField name = new DataSourceTextField("name");
		DataSourceTextField status = new DataSourceTextField("status");
		DataSourceImageField statusicon = new DataSourceImageField("statusicon");
		DataSourceTextField type = new DataSourceTextField("type");
		DataSourceTextField typelabel = new DataSourceTextField("typelabel");
		DataSourceFloatField size = new DataSourceFloatField("size");
		DataSourceTextField creator = new DataSourceTextField("creator");
		DataSourceTextField closer = new DataSourceTextField("closer");
		DataSourceDateTimeField created = new DataSourceDateTimeField("created");

		setFields(id, name, size, closer, creator, type, typelabel, status, statusicon, created);
		setClientOnly(true);
		setDataURL("data/archives.xml?sid=" + Session.get().getSid() + "&mode=" + mode);
	}
}