package com.logicaldoc.gui.frontend.client.search;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
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

	public DocumentFieldsDS(GUITemplate template) {
		setClientOnly(true);

		/*
		 * Define default fields
		 */
		DataSourceTextField title = new DataSourceTextField("title", I18N.message("title"));
		title.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS, OperatorId.NOT_EQUAL);

		DataSourceIntegerField id = new DataSourceIntegerField("id", I18N.message("id"));
		id.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN, OperatorId.EQUALS, OperatorId.NOT_EQUAL);

		DataSourceTextField author = new DataSourceTextField("sourceAuthor", I18N.message("author"));
		author.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceTextField object = new DataSourceTextField("object", I18N.message("object"));
		object.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceTextField sourceType = new DataSourceTextField("sourceType", I18N.message("type"));
		sourceType.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceTextField extension = new DataSourceTextField("type", I18N.message("fileext"));
		extension.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

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

		DataSourceIntegerField fileSize = new DataSourceIntegerField("fileSize", I18N.message("size"));
		fileSize.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceDateTimeField sourceDate = new DataSourceDateTimeField("sourceDate", I18N.message("date"));
		sourceDate.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);

		DataSourceDateTimeField lastModified = new DataSourceDateTimeField("lastModified", I18N.message("lastmodified"));
		lastModified.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);

		DataSourceDateTimeField published = new DataSourceDateTimeField("date", I18N.message("publishedon"));
		published.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);

		DataSourceDateTimeField created = new DataSourceDateTimeField("creation", I18N.message("createdon"));
		created.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);

		DataSourceTextField filename = new DataSourceTextField("filename", I18N.message("filename"));
		filename.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceIntegerField rating = new DataSourceIntegerField("rating", I18N.message("rating"));
		rating.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN, OperatorId.EQUALS, OperatorId.NOT_EQUAL);

		DataSourceTextField tags = new DataSourceTextField("tags", I18N.message("tags"));
		sourceid.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS);

		DataSourceTextField comment = new DataSourceTextField("comment", I18N.message("comment"));
		comment.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceTextField wfStatus = new DataSourceTextField("workflowStatus", I18N.message("workflowstatus"));
		wfStatus.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);

		DataSourceDateTimeField startPublishing = new DataSourceDateTimeField("startPublishing",
				I18N.message("startpublishing"));
		startPublishing.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);

		DataSourceDateTimeField stopPublishing = new DataSourceDateTimeField("stopPublishing",
				I18N.message("stoppublishing"));
		stopPublishing.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);

		DataSourceIntegerField publishedStatus = new DataSourceIntegerField("published", I18N.message("published"));
		publishedStatus.setValidOperators(OperatorId.EQUALS, OperatorId.NOT_EQUAL);

		setFields(author, coverage, id, title, object, fileSize, publisher, version, sourceDate, lastModified,
				published, created, creator, customId, filename, extension, recipient, source, sourceid, sourceType,
				rating, tags, comment, wfStatus, publishedStatus, startPublishing, stopPublishing);

		/*
		 * Define extended attributes
		 */
		if (template != null && template.getAttributes() != null)
			for (GUIExtendedAttribute att : template.getAttributes()) {
				DataSourceField field = null;
				String name = "_" + att.getName().replaceAll(" ", Constants.BLANK_PLACEHOLDER);
				String titl = att.getLabel() + " (" + template.getName() + ")";
				if (att.getType() == GUIExtendedAttribute.TYPE_DATE) {
					field = new DataSourceDateTimeField();
					field.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);
					name = name + Constants.BLANK_PLACEHOLDER + "type:" + GUIExtendedAttribute.TYPE_DATE;
				} else if (att.getType() == GUIExtendedAttribute.TYPE_DOUBLE) {
					field = new DataSourceFloatField();
					field.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN, OperatorId.EQUALS,
							OperatorId.NOT_EQUAL);
					name = name + Constants.BLANK_PLACEHOLDER + "type:" + GUIExtendedAttribute.TYPE_DOUBLE;
				} else if (att.getType() == GUIExtendedAttribute.TYPE_INT) {
					field = new DataSourceIntegerField();
					field.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN, OperatorId.EQUALS,
							OperatorId.NOT_EQUAL);
					name = name + Constants.BLANK_PLACEHOLDER + "type:" + GUIExtendedAttribute.TYPE_INT;
				} else if (att.getType() == GUIExtendedAttribute.TYPE_BOOLEAN) {
					field = new DataSourceIntegerField();
					field.setValidOperators(OperatorId.EQUALS);
					name = name + Constants.BLANK_PLACEHOLDER + "type:" + GUIExtendedAttribute.TYPE_BOOLEAN;
				} else {
					field = new DataSourceTextField();
					field.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
							OperatorId.NOT_EQUAL);
					if (att.getEditor() == GUIExtendedAttribute.EDITOR_DEFAULT)
						name = name + Constants.BLANK_PLACEHOLDER + "type:" + GUIExtendedAttribute.TYPE_STRING;
					else
						name = name + Constants.BLANK_PLACEHOLDER + "type:" + GUIExtendedAttribute.TYPE_STRING_PRESET;
				}

				field.setName(name);
				field.setTitle(titl);
				addField(field);
			}
	}
}