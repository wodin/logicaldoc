package com.logicaldoc.gui.frontend.client.search;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
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
		title.setEditorType(ItemFactory.newTextItem("title", I18N.message("title"), null));

		DataSourceIntegerField id = new DataSourceIntegerField("id", I18N.message("id"));
		id.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN, OperatorId.EQUALS, OperatorId.NOT_EQUAL);
		id.setEditorType(ItemFactory.newIntegerItem("id", I18N.message("id"), null));

		DataSourceTextField author = new DataSourceTextField("sourceAuthor", I18N.message("author"));
		author.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		author.setEditorType(ItemFactory.newTextItem("sourceAuthor", I18N.message("author"), null));

		DataSourceTextField object = new DataSourceTextField("object", I18N.message("object"));
		object.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		object.setEditorType(ItemFactory.newTextItem("object", I18N.message("object"), null));

		DataSourceTextField sourceType = new DataSourceTextField("sourceType", I18N.message("type"));
		sourceType.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		sourceType.setEditorType(ItemFactory.newTextItem("sourceType", I18N.message("type"), null));

		DataSourceTextField extension = new DataSourceTextField("type", I18N.message("fileext"));
		extension.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		extension.setEditorType(ItemFactory.newTextItem("type", I18N.message("fileext"), null));

		DataSourceTextField coverage = new DataSourceTextField("coverage", I18N.message("coverage"));
		coverage.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		coverage.setEditorType(ItemFactory.newTextItem("coverage", I18N.message("coverage"), null));

		DataSourceTextField customId = new DataSourceTextField("customId", I18N.message("customid"));
		customId.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		customId.setEditorType(ItemFactory.newTextItem("customId", I18N.message("customid"), null));

		DataSourceTextField version = new DataSourceTextField("version", I18N.message("version"));
		version.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		version.setEditorType(ItemFactory.newTextItem("version", I18N.message("version"), null));

		DataSourceTextField publisher = new DataSourceTextField("publisher", I18N.message("publisher"));
		publisher.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		publisher.setEditorType(ItemFactory.newTextItem("publisher", I18N.message("publisher"), null));

		DataSourceTextField creator = new DataSourceTextField("creator", I18N.message("creator"));
		creator.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		creator.setEditorType(ItemFactory.newTextItem("creator", I18N.message("creator"), null));

		DataSourceTextField recipient = new DataSourceTextField("recipient", I18N.message("recipient"));
		recipient.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		recipient.setEditorType(ItemFactory.newTextItem("recipient", I18N.message("recipient"), null));

		DataSourceTextField source = new DataSourceTextField("source", I18N.message("source"));
		source.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		source.setEditorType(ItemFactory.newTextItem("source", I18N.message("source"), null));

		DataSourceTextField sourceid = new DataSourceTextField("sourceid", I18N.message("sourceid"));
		sourceid.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		sourceid.setEditorType(ItemFactory.newTextItem("sourceid", I18N.message("sourceid"), null));

		DataSourceIntegerField fileSize = new DataSourceIntegerField("fileSize", I18N.message("size"));
		fileSize.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		fileSize.setEditorType(ItemFactory.newTextItem("fileSize", I18N.message("size"), null));

		DataSourceDateTimeField sourceDate = new DataSourceDateTimeField("sourceDate", I18N.message("date"));
		sourceDate.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);
		sourceDate.setEditorType(ItemFactory.newDateItem("sourceDate", I18N.message("date")));

		DataSourceDateTimeField lastModified = new DataSourceDateTimeField("lastModified", I18N.message("lastmodified"));
		lastModified.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);
		lastModified.setEditorType(ItemFactory.newDateItem("lastModified", I18N.message("lastmodified")));

		DataSourceDateTimeField published = new DataSourceDateTimeField("date", I18N.message("publishedon"));
		published.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);
		published.setEditorType(ItemFactory.newDateItem("date", I18N.message("publishedon")));

		DataSourceDateTimeField created = new DataSourceDateTimeField("creation", I18N.message("createdon"));
		created.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);
		created.setEditorType(ItemFactory.newDateItem("creation", I18N.message("createdon")));

		DataSourceTextField filename = new DataSourceTextField("filename", I18N.message("filename"));
		filename.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		filename.setEditorType(ItemFactory.newTextItem("filename", I18N.message("filename"), null));

		DataSourceIntegerField rating = new DataSourceIntegerField("rating", I18N.message("rating"));
		rating.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN, OperatorId.EQUALS, OperatorId.NOT_EQUAL);
		rating.setEditorType(ItemFactory.newTextItem("rating", I18N.message("rating"), null));

		DataSourceTextField tags = new DataSourceTextField("tags", I18N.message("tags"));
		sourceid.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS);
		sourceid.setEditorType(ItemFactory.newTextItem("tags", I18N.message("tags"), null));

		DataSourceTextField comment = new DataSourceTextField("comment", I18N.message("comment"));
		comment.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		comment.setEditorType(ItemFactory.newTextItem("comment", I18N.message("comment"), null));

		DataSourceTextField wfStatus = new DataSourceTextField("workflowStatus", I18N.message("workflowstatus"));
		wfStatus.setValidOperators(OperatorId.ICONTAINS, OperatorId.INOT_CONTAINS, OperatorId.EQUALS,
				OperatorId.NOT_EQUAL);
		wfStatus.setEditorType(ItemFactory.newTextItem("workflowStatus", I18N.message("workflowstatus"), null));

		DataSourceDateTimeField startPublishing = new DataSourceDateTimeField("startPublishing",
				I18N.message("startpublishing"));
		startPublishing.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);
		startPublishing.setEditorType(ItemFactory.newDateItem("startPublishing", I18N.message("startpublishing")));

		DataSourceDateTimeField stopPublishing = new DataSourceDateTimeField("stopPublishing",
				I18N.message("stoppublishing"));
		stopPublishing.setValidOperators(OperatorId.GREATER_THAN, OperatorId.LESS_THAN);
		stopPublishing.setEditorType(ItemFactory.newDateItem("stopPublishing", I18N.message("stoppublishing")));

		DataSourceIntegerField publishedStatus = new DataSourceIntegerField("published", I18N.message("published"));
		publishedStatus.setValidOperators(OperatorId.EQUALS, OperatorId.NOT_EQUAL);
		publishedStatus.setEditorType(ItemFactory.newIntegerItem("published", I18N.message("published"), null));

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
					field.setEditorType(ItemFactory.newDateItem(name, titl));
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
					name = name + Constants.BLANK_PLACEHOLDER + "type:" + GUIExtendedAttribute.TYPE_STRING;
				}

				field.setName(name);
				field.setTitle(titl);
				addField(field);
			}
	}
}