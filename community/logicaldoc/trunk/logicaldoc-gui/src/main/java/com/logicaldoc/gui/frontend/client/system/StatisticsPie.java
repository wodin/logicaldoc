package com.logicaldoc.gui.frontend.client.system;

import com.rednels.ofcgwt.client.ChartWidget;
import com.rednels.ofcgwt.client.model.ChartData;
import com.rednels.ofcgwt.client.model.elements.PieChart;

/**
 * Visualizes some statistics as a pie chart
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class StatisticsPie extends ChartWidget {
	public StatisticsPie(String title) {
		super();
		ChartData cd = new ChartData(title, "font-size: 14px; font-family: Verdana; text-align: center;");
		cd.setBackgroundColour("#ffffff");

		PieChart pie = new PieChart();
		pie.setAlpha(0.3f);
		pie.setNoLabels(true);
		pie.setTooltip("#label# $#val#<br>#percent#");
		pie.setGradientFill(true);
		pie.setColours("#ff0000", "#00ff00", "#0000ff", "#ff9900", "#ff00ff");
		pie.addSlices(new PieChart.Slice(11000, "AU"));
		pie.addSlices(new PieChart.Slice(88000, "USA"));
		pie.addSlices(new PieChart.Slice(62000, "UK"));
		pie.addSlices(new PieChart.Slice(14000, "JP"));
		pie.addSlices(new PieChart.Slice(43000, "EU"));

		cd.addElements(pie);
		setSize("200", "200");
		setJsonData(cd.toString());
	}
}
