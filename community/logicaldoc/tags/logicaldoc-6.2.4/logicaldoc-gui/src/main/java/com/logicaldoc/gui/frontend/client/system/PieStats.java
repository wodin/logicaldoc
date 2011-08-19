package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.i18n.client.NumberFormat;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Shows the charts pies.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class PieStats extends HLayout {

	private static final int STATS_REPOSITORY = 0;

	private static final int STATS_DOCUMENTS = 1;

	private static final int STATS_FOLDERS = 2;

	public PieStats(GUIParameter[][] parameters) {
		super();
		setWidth100();
		setHeight(350);

		setAlign(VerticalAlignment.TOP);

		VLayout repository = new VLayout();
		repository.addMember(new StatisticWidget(I18N.message("repository"), parameters[0]));
		repository.addMember(prepareLegend(parameters[0], STATS_REPOSITORY));
		addMember(repository);
		VLayout documents = new VLayout();
		documents.addMember(new StatisticWidget(I18N.message("documents"), parameters[1]));
		documents.addMember(prepareLegend(parameters[1], STATS_DOCUMENTS));
		addMember(documents);
		VLayout folders = new VLayout();
		folders.addMember(new StatisticWidget(I18N.message("folders"), parameters[2]));
		folders.addMember(prepareLegend(parameters[2], STATS_FOLDERS));
		addMember(folders);
	}

	private DynamicForm prepareLegend(GUIParameter[] parameters, int type) {
		NumberFormat fmt = NumberFormat.getFormat("###");

		// Calculate the total value
		double count = 0;
		for (GUIParameter parameter : parameters) {
			if (parameter == null)
				break;

			count += fmt.parse(parameter.getValue());
		}

		DynamicForm systemForm = new DynamicForm();
		systemForm.setWidth("90%");
		systemForm.setColWidths(100, "*");
		systemForm.setTitleOrientation(TitleOrientation.LEFT);
		systemForm.setWrapItemTitles(false);
		systemForm.setNumCols(2);
		systemForm.setHeight(80);

		systemForm.setLayoutAlign(Alignment.CENTER);
		systemForm.setLayoutAlign(VerticalAlignment.TOP);
		systemForm.setAlign(Alignment.CENTER);

		StaticTextItem[] items = new StaticTextItem[9];

		int i = 0;
		for (GUIParameter parameter : parameters) {
			if (parameter == null)
				break;

			StaticTextItem item = ItemFactory.newStaticTextItem(parameter.getName(), parameter.toString(), null);
			if (type == STATS_REPOSITORY)
				item.setValue(Util.formatSize(fmt.parse(parameter.getValue())) + " ( "
						+ Util.formatPercentage((fmt.parse(parameter.getValue()) * 100 / count), 2) + " )");
			else if (type == STATS_DOCUMENTS)
				item.setValue(parameter.getValue() + " " + I18N.message("documents").toLowerCase() + " " + "( "
						+ Util.formatPercentage((fmt.parse(parameter.getValue()) * 100 / count), 2) + " )");
			else if (type == STATS_FOLDERS)
				item.setValue(parameter.getValue() + " " + I18N.message("folders").toLowerCase() + " " + " ( "
						+ Util.formatPercentage((fmt.parse(parameter.getValue()) * 100 / count), 2) + " )");

			item.setRequired(true);
			item.setShouldSaveValue(false);
			items[i] = item;
			i++;
		}

		StaticTextItem total = ItemFactory.newStaticTextItem("total", "total", null);
		if (type == STATS_REPOSITORY)
			total.setValue(Util.formatSize(count));
		else if (type == STATS_DOCUMENTS)
			total.setValue((int) count + " " + I18N.message("documents").toLowerCase());
		else if (type == STATS_FOLDERS)
			total.setValue((int) count + " " + I18N.message("folders").toLowerCase());
		total.setRequired(true);
		total.setShouldSaveValue(false);
		items[i++] = total;

		// Fill empty rows
		for (; i < 9; i++) {
			StaticTextItem item = ItemFactory.newStaticTextItem("_" + i, " ", null);
			item.setShowTitle(false);
			items[i] = item;
		}

		systemForm.setItems(items);
		return systemForm;
	}
}
