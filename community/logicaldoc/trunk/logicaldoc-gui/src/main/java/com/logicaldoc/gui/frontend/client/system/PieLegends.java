package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Shows the charts legends
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class PieLegends extends HLayout {

	private SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);

	public PieLegends() {
		super();
		setMembersMargin(20);
		setWidth100();

		service.getStatistics(Session.get().getSid(), new AsyncCallback<GUIParameter[][]>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIParameter[][] parameters) {
				addMember(prepareLegend(parameters[0], 1));
				addMember(prepareLegend(parameters[1], 2));
				addMember(prepareLegend(parameters[2], 3));
			}
		});
	}

	private DynamicForm prepareLegend(GUIParameter[] parameters, int type) {

		// Calculate the total value
		double count = 0;
		for (GUIParameter parameter : parameters) {
			if (parameter == null)
				break;

			count += Double.parseDouble(parameter.getValue());
		}

		DynamicForm systemForm = new DynamicForm();
		systemForm.setWidth("33%");
		systemForm.setColWidths(25, "*");
		systemForm.setTitleOrientation(TitleOrientation.LEFT);
		systemForm.setWrapItemTitles(false);
		systemForm.setMargin(8);
		systemForm.setNumCols(2);

		StaticTextItem[] items = null;
		if (type == 1)
			// Repository
			items = new StaticTextItem[9];
		else if (type == 2)
			// Documents
			items = new StaticTextItem[4];
		else if (type == 3)
			// Folders
			items = new StaticTextItem[4];

		int i = 0;
		for (GUIParameter parameter : parameters) {
			if (parameter == null)
				break;

			StaticTextItem item = new StaticTextItem();
			item.setName(parameter.getName());
			item.setTitle(parameter.toString());
			if (type == 1)
				item.setValue(Util.formatSize(Long.parseLong(parameter.getValue())) + " ( "
						+ Util.formatPercentage((Double.parseDouble(parameter.getValue()) * 100 / count), 2) + " )");
			else if (type == 2)
				item.setValue(parameter.getValue() + " " + I18N.getMessage("documents").toLowerCase() + " " + "( "
						+ Util.formatPercentage((Double.parseDouble(parameter.getValue()) * 100 / count), 2) + " )");
			else if (type == 3)
				item.setValue(parameter.getValue() + " " + I18N.getMessage("folders").toLowerCase() + " " + " ( "
						+ Util.formatPercentage((Double.parseDouble(parameter.getValue()) * 100 / count), 2) + " )");

			item.setRequired(true);
			item.setShouldSaveValue(false);
			items[i] = item;
			i++;
		}

		StaticTextItem total = new StaticTextItem();
		total.setName("total");
		total.setTitle(I18N.getMessage("total"));
		if (type == 1)
			total.setValue(Util.formatSize(count));
		else if (type == 2)
			total.setValue((int) count + " " + I18N.getMessage("documents").toLowerCase());
		else if (type == 3)
			total.setValue((int) count + " " + I18N.getMessage("folders").toLowerCase());
		total.setRequired(true);
		total.setShouldSaveValue(false);
		items[i] = total;

		systemForm.setItems(items);

		return systemForm;
	}
}