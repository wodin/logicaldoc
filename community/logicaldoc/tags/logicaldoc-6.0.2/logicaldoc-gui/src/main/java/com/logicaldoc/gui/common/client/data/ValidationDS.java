package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class ValidationDS extends DataSource {
	public ValidationDS(long genericId, long archiveId) {
		setTitleField("title");
		setRecordXPath("/list/validationentry");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setHidden(true);
		id.setRequired(true);
		DataSourceTextField title = new DataSourceTextField("title");
		DataSourceTextField number = new DataSourceTextField("number");
		DataSourceDateTimeField date = new DataSourceDateTimeField("date");
		DataSourceTextField error = new DataSourceTextField("error");
		DataSourceTextField sign = new DataSourceTextField("sign");
		DataSourceImageField icon = new DataSourceImageField("icon");
		setFields(id, icon, title, number, date, error, sign);
		setDataURL("data/validationentries.xml?sid=" + Session.get().getSid() + "&genericId=" + genericId
				+ "&archiveId=" + archiveId + "&locale=" + I18N.getLocale());
		setClientOnly(true);
	}
}
