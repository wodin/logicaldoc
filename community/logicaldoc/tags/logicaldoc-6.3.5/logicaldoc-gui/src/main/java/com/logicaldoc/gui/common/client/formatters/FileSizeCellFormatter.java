package com.logicaldoc.gui.common.client.formatters;

import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Utility formatter for those cells that contains file sizes
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FileSizeCellFormatter implements CellFormatter {
	@Override
	public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
		if (value == null)
			return null;
		if (value instanceof Long)
			return Util.formatSizeKB(((Long) value).doubleValue());
		else if (value instanceof Integer)
			return Util.formatSizeKB(((Integer) value).doubleValue());
		else
			return Util.formatSizeKB(0L);
	}
}
