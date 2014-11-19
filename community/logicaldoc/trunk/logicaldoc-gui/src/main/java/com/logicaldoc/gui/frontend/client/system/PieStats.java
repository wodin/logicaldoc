package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Image;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
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
		setHeight100();

		setAlign(VerticalAlignment.TOP);

		VLayout repository = new VLayout();
		try {
			// Convert all in MBytes
			GUIParameter[] params = new GUIParameter[parameters[0].length];
			for (int i = 0; i < params.length; i++) {
				params[i] = new GUIParameter();
				params[i].setName(parameters[0][i].getName());
				long val = Long.parseLong(parameters[0][i].getValue()) / 1024 / 1024;
				params[i].setValue(Long.toString(val));
			}
		} catch (Throwable t) {
		}	
			
		repository.addMember(new Image(Util.contextPath()+"stat?sid="+Session.get().getSid()+"&chart=repository"));
		
		HLayout spacer = new HLayout();
		spacer.setHeight(15);
		repository.addMember(spacer);
		
		try {
			repository.addMember(prepareLegend(parameters[0], STATS_REPOSITORY));
			addMember(repository);
		} catch (Throwable t) {
		}

		VLayout documents = new VLayout();
		documents.addMember(new Image(Util.contextPath()+"stat?sid="+Session.get().getSid()+"&chart=documents"));
	
		spacer = new HLayout();
		spacer.setHeight(15);
		documents.addMember(spacer);
		
		
		try {
			documents.addMember(prepareLegend(parameters[1], STATS_DOCUMENTS));
			addMember(documents);
		} catch (Throwable t) {

		}

		VLayout folders = new VLayout();
		folders.addMember(new Image(Util.contextPath()+"stat?sid="+Session.get().getSid()+"&chart=folders"));

		spacer = new HLayout();
		spacer.setHeight(15);
		folders.addMember(spacer);
		
		try {
			folders.addMember(prepareLegend(parameters[2], STATS_FOLDERS));
			addMember(folders);
		} catch (Throwable t) {

		}
	}

	private DynamicForm prepareLegend(GUIParameter[] parameters, int type) {
		NumberFormat fmt = NumberFormat.getFormat("########");

		// Calculate the total value
		double count = 0;
		for (GUIParameter parameter : parameters) {
			if (parameter == null)
				break;

			try {
				count += fmt.parse(parameter.getValue());
			} catch (Throwable t) {
				Log.info("error in " + parameter + " " + parameter.getValue(), null);
			}
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
			if (type == STATS_REPOSITORY) {
				item.setValue(Util.formatSize(fmt.parse(parameter.getValue())) + " ( "
						+ Util.formatPercentage((fmt.parse(parameter.getValue()) * 100 / count), 2) + " )");
			} else if (type == STATS_DOCUMENTS)
				item.setValue(parameter.getValue() + " " + I18N.message("documents").toLowerCase() + " " + "( "
						+ Util.formatPercentage((fmt.parse(parameter.getValue()) * 100 / count), 2) + " )");
			else if (type == STATS_FOLDERS)
				item.setValue(parameter.getValue() + " " + I18N.message("folders").toLowerCase() + " " + " ( "
						+ Util.formatPercentage((fmt.parse(parameter.getValue()) * 100 / count), 2) + " )");

			item.setRequired(true);
			item.setShouldSaveValue(false);
			item.setWrap(false);
			item.setWrapTitle(false);
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
