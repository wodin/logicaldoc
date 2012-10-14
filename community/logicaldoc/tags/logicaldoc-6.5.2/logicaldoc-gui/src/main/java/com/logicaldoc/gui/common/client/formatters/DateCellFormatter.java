package com.logicaldoc.gui.common.client.formatters;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Utility formatter for those cells that contains dates
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DateCellFormatter implements CellFormatter {
	private String format = "";

	@Override
	public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
		if (value == null)
			return null;
		DateTimeFormat formatter = DateTimeFormat.getFormat(format);

		return formatter.format((Date) value);
	}

	public DateCellFormatter(boolean shortFormat) {
		if (shortFormat)
			format = I18N.message("format_dateshort");
		else
			format = I18N.message("format_date");
	}
}
