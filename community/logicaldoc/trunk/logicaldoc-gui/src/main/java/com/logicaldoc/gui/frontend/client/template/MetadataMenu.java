package com.logicaldoc.gui.frontend.client.template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Menu;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUICustomId;
import com.logicaldoc.gui.common.client.beans.GUISequence;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.search.TagsForm;
import com.logicaldoc.gui.frontend.client.services.CustomIdService;
import com.logicaldoc.gui.frontend.client.services.CustomIdServiceAsync;
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

	public MetadataMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button tags = new Button(I18N.message("tags"));
		tags.setWidth100();
		tags.setHeight(25);
		if (Feature.visible(Feature.TAGS)) {
			addMember(tags);
			if (!Feature.enabled(Feature.TAGS)) {
				tags.setDisabled(true);
				tags.setTooltip(I18N.message("featuredisabled"));
			}
		}
		tags.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new TagsForm(true));
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
				AdminPanel.get().setContent(new WorkflowDesigner(new GUIWorkflow(), false));
			}
		});
	}
}
