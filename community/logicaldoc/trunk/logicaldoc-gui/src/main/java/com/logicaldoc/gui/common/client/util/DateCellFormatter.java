package com.logicaldoc.gui.common.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Utility formatter for those cells that contains dates
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DateCellFormatter implements CellFormatter {
	@Override
	public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
		if (value == null)
			return null;
		DateTimeFormat formatter = DateTimeFormat.getFormat(I18N.getFormat(Constants.FORMAT_DATE));
		return formatter.format((Date) value);
	}
}
