package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.gwt.visualization.client.visualizations.PieChart.Options;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Visualizes some statistics as a pie chart
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class StatisticWidget extends VLayout {
	public StatisticWidget(String title, GUIParameter[] parameters) {
		super();

		setAutoWidth();
		setAutoHeight();

		// Defining the options
		Options options = Options.create();
		options.setWidth(300);
		options.setHeight(200);
		options.set3D(true);
		options.setTitle(title);

		// Setup the table with data
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Title");
		data.addColumn(ColumnType.NUMBER, "Number");
		data.addColumn(ColumnType.STRING, "Label");

		int rowCount = 0;
		for (int i = 0; i < parameters.length; i++)
			if (parameters[i] != null)
				rowCount++;
		data.addRows(rowCount);

		int row = 0;
		for (int i = 0; i < parameters.length; i++) {
			GUIParameter parameter = parameters[i];
			if (parameter == null)
				continue;
			data.setValue(row, 0, parameter.toString());
			data.setValue(row, 1, new Integer(parameter.getValue()));
			row++;
		}

		// Create a pie chart visualization.
		PieChart pie = new PieChart(data, options);
		addMember(pie);
	}
}
