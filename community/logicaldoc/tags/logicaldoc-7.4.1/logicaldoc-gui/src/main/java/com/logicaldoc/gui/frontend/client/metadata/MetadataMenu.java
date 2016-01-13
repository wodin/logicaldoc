package com.logicaldoc.gui.frontend.client.metadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Menu;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUICustomId;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUISequence;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.metadata.form.FormsPanel;
import com.logicaldoc.gui.frontend.client.metadata.stamp.StampsPanel;
import com.logicaldoc.gui.frontend.client.services.CustomIdService;
import com.logicaldoc.gui.frontend.client.services.CustomIdServiceAsync;
import com.logicaldoc.gui.frontend.client.services.TagService;
import com.logicaldoc.gui.frontend.client.services.TagServiceAsync;
import com.logicaldoc.gui.frontend.client.workflow.WorkflowDesigner;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the administration document metadata and workflow menu
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class MetadataMenu extends VLayout {
	private CustomIdServiceAsync customIdService = (CustomIdServiceAsync) GWT.create(CustomIdService.class);

	private TagServiceAsync tagService = (TagServiceAsync) GWT.create(TagService.class);

	public MetadataMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button tags = new Button(I18N.message("tags"));
		tags.setWidth100();
		tags.setHeight(25);
		if (Feature.visible(Feature.TAGS_ADMIN)) {
			addMember(tags);
			if (!Feature.enabled(Feature.TAGS_ADMIN)) {
				tags.setDisabled(true);
				tags.setTooltip(I18N.message("featuredisabled"));
			}
		}
		tags.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				tagService.getSettings(Session.get().getSid(), new AsyncCallback<GUIParameter[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIParameter[] parameters) {
						AdminPanel.get().setContent(new TagsPanel(parameters));
					}
				});
			}
		});

		Button templates = new Button(I18N.message("templates"));
		templates.setWidth100();
		templates.setHeight(25);

		if (Feature.visible(Feature.TEMPLATE)) {
			addMember(templates);
			if (!Feature.enabled(Feature.TEMPLATE)) {
				templates.setDisabled(true);
				templates.setTooltip(I18N.message("featuredisabled"));
			}
		}
		templates.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new TemplatesPanel());
			}
		});

		Button barcode = new Button(I18N.message("barcodes"));
		barcode.setWidth100();
		barcode.setHeight(25);

		if (Feature.visible(Feature.BARCODES) && Menu.enabled(Menu.BARCODES)) {
			addMember(barcode);
			if (!Feature.enabled(Feature.BARCODES)) {
				barcode.setDisabled(true);
				barcode.setTooltip(I18N.message("featuredisabled"));
			}
		}
		barcode.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new BarcodesEnginePanel());
			}
		});

		Button customid = new Button(I18N.message("customid"));
		customid.setWidth100();
		customid.setHeight(25);

		if (Feature.visible(Feature.CUSTOMID) && Menu.enabled(Menu.CUSTOM_ID)) {
			addMember(customid);
			if (!Feature.enabled(Feature.CUSTOMID)) {
				customid.setDisabled(true);
				customid.setTooltip(I18N.message("featuredisabled"));
			}
		}

		customid.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				customIdService.load(Session.get().getSid(), new AsyncCallback<GUICustomId[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(final GUICustomId[] schemas) {
						customIdService.loadSequences(Session.get().getSid(), new AsyncCallback<GUISequence[]>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUISequence[] sequences) {
								AdminPanel.get().setContent(new CustomIdPanel(schemas, sequences));
							}
						});
					}
				});
			}
		});

		Button workflow = new Button(I18N.message("workflow"));
		workflow.setWidth100();
		workflow.setHeight(25);

		if (Feature.visible(Feature.WORKFLOW) && Menu.enabled(Menu.WORKFLOW)) {
			addMember(workflow);
			if (!Feature.enabled(Feature.WORKFLOW)) {
				workflow.setDisabled(true);
				workflow.setTooltip(I18N.message("featuredisabled"));
			}
		}
		workflow.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new WorkflowDesigner(new GUIWorkflow()));
			}
		});

		Button folderTemplates = new Button(I18N.message("foldertemplates"));
		folderTemplates.setWidth100();
		folderTemplates.setHeight(25);
		folderTemplates.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new FolderTemplatesPanel());
			}
		});

		if (Feature.visible(Feature.FOLDER_TEMPLATE)) {
			addMember(folderTemplates);
			if (!Feature.enabled(Feature.FOLDER_TEMPLATE)) {
				folderTemplates.setDisabled(true);
				folderTemplates.setTooltip(I18N.message("featuredisabled"));
			}
		}

		Button retentionPolicies = new Button(I18N.message("retentionpolicies"));
		retentionPolicies.setWidth100();
		retentionPolicies.setHeight(25);

		if (Feature.visible(Feature.RETENTION_POLICIES) && Menu.enabled(Menu.RETENTION_POLICIES)) {
			addMember(retentionPolicies);
			if (!Feature.enabled(Feature.RETENTION_POLICIES)) {
				retentionPolicies.setDisabled(true);
				retentionPolicies.setTooltip(I18N.message("featuredisabled"));
			}
		}
		retentionPolicies.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new RetentionPoliciesPanel());
			}
		});

		Button stamps = new Button(I18N.message("stamps"));
		stamps.setWidth100();
		stamps.setHeight(25);

		if (Feature.visible(Feature.STAMP) && Menu.enabled(Menu.STAMPS)) {
			addMember(stamps);
			if (!Feature.enabled(Feature.STAMP)) {
				stamps.setDisabled(true);
				stamps.setTooltip(I18N.message("featuredisabled"));
			}
		}
		stamps.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new StampsPanel());
			}
		});

		Button forms = new Button(I18N.message("forms"));
		forms.setWidth100();
		forms.setHeight(25);

		if (Feature.visible(Feature.FORM) && Menu.enabled(Menu.FORMS)) {
			addMember(forms);
			if (!Feature.enabled(Feature.FORM)) {
				forms.setDisabled(true);
				forms.setTooltip(I18N.message("featuredisabled"));
			}
		}
		forms.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new FormsPanel());
			}
		});
	}
}