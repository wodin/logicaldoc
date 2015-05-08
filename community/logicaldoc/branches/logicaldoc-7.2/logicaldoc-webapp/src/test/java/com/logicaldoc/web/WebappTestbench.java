package com.logicaldoc.web;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class WebappTestbench {
	public static void main(String[] args) throws IOException {

		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Documents", new Double(43.2));
		dataset.setValue("Users", new Double(10.0));
		dataset.setValue("Full-text", new Double(27.5));
		dataset.setValue("Import", new Double(15.5));
		dataset.setValue(" ", new Double(0.0));
		dataset.setValue("  ", new Double(0.0));
		dataset.setValue("   ", new Double(0.0));
		dataset.setValue("    ", new Double(0.0));

		JFreeChart chart = ChartFactory.createPieChart("Repository", dataset, true, false, false);
		chart.setBorderVisible(false);
		chart.getTitle().setPaint(new Color(125, 125, 125));

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelFont(new Font("Arial", Font.PLAIN, 12));
		plot.setNoDataMessage("No data available");
		plot.setCircular(true);
		plot.setBackgroundPaint(Color.WHITE);
		plot.setLabelGap(0.02);
		plot.setOutlinePaint(null);
		plot.setLabelGenerator(null);
		plot.setIgnoreNullValues(false);
		
		chart.getLegend().setBorder(0, 0, 0, 0);

		File file=new File("C:\\tmp\\chart.png");
		ChartUtilities.saveChartAsPNG(file, chart, 250, 250);
		
	}
}
