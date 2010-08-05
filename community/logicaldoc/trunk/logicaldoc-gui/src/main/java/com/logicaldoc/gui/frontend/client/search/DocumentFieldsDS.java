package com.logicaldoc.gui.frontend.client.search;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.OperatorId;

/**
 * Fake Datasource to populate a filter builder for parametric searches.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentFieldsDS extends DataSource {

	public DocumentFieldsDS() {
		setTitleField("title");
		setRecordXPath("/list/field");
		DataSourceTextField title = new DataSourceTextField("title", I18N.message("title"));
		title.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS, OperatorId.NOT_EQUAL);

		DataSourceIntegerField id = new DataSourceIntegerField("id", I18N.message("id"));
		id.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN, OperatorId.EQUALS, OperatorId.NOT_EQUAL);

		DataSourceTextField author = new DataSourceTextField("author", I18N.message("author"));
		author.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceTextField object = new DataSourceTextField("object", I18N.message("object"));
		object.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceTextField type = new DataSourceTextField("type", I18N.message("type"));
		type.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS, OperatorId.NOT_EQUAL);

		DataSourceTextField coverage = new DataSourceTextField("coverage", I18N.message("coverage"));
		coverage.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceTextField customId = new DataSourceTextField("customId", I18N.message("customid"));
		customId.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceTextField version = new DataSourceTextField("version", I18N.message("version"));
		version.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceTextField publisher = new DataSourceTextField("publisher", I18N.message("publisher"));
		publisher.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceTextField creator = new DataSourceTextField("creator", I18N.message("creator"));
		creator.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceTextField recipient = new DataSourceTextField("recipient", I18N.message("recipient"));
		recipient.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceTextField source = new DataSourceTextField("source", I18N.message("source"));
		source.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceTextField sourceid = new DataSourceTextField("sourceid", I18N.message("sourceid"));
		sourceid.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceIntegerField size = new DataSourceIntegerField("size");
		size.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN, OperatorId.EQUALS, OperatorId.NOT_EQUAL);

		DataSourceDateTimeField date = new DataSourceDateTimeField("date");
		date.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);

		DataSourceDateTimeField lastModified = new DataSourceDateTimeField("lastModified");
		lastModified.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);

		DataSourceDateTimeField published = new DataSourceDateTimeField("published");
		published.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);

		DataSourceDateTimeField created = new DataSourceDateTimeField("created");
		created.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);

		DataSourceTextField filename = new DataSourceTextField("filename");
		filename.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		setFields(author, coverage, id, title, object, size, publisher, version, date, lastModified, published,
				created, creator, customId, filename, recipient, source, sourceid, type);
		setClientOnly(true);
		// setDataURL("data/documents.xml?sid=" + Session.get().getSid());
	}
}