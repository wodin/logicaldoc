package com.logicaldoc.gui.frontend.client.system;

import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.rednels.ofcgwt.client.ChartWidget;
import com.rednels.ofcgwt.client.model.ChartData;
import com.rednels.ofcgwt.client.model.elements.PieChart;

/**
 * Visualizes some statistics as a pie chart
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class StatisticWidget extends ChartWidget {
	public StatisticWidget(String title, GUIParameter[] parameters) {
		super();
		ChartData cd = new ChartData(title, "font-size: 14px; font-family: Verdana; text-align: center;");
		cd.setBackgroundColour("#ffffff");

		PieChart pie = new PieChart();
		pie.setAlpha(0.3f);
		pie.setNoLabels(true);
		pie.setTooltip("#label# <br>#percent#");
		pie.setGradientFill(true);
		pie.setColours("#ff0000", "#00ff00", "#0000ff", "#ff9900", "#ff00ff");

		for (GUIParameter parameter : parameters) {
			if (parameter == null)
				break;
			pie.addSlices(new PieChart.Slice(Integer.parseInt(parameter.getValue()), parameter.toString()));
		}

		cd.addElements(pie);
		setSize("200", "200");

		setJsonData(cd.toString());
	}
}
