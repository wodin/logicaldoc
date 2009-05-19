package com.logicaldoc.web.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.logicaldoc.util.io.FileUtil;
import com.logicaldoc.web.SessionManagement;

/**
 * This converter automatically detects the proper way to render a file size
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class FileSizeConverter implements Converter {

	public FileSizeConverter() {
	}

	public Object getAsObject(FacesContext context, UIComponent component, String arg2) {
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component, Object object) {
		String str = null;

		if (object != null)
			str = object.toString();
		long size = 0;
		try {
			size = Long.parseLong(str);
		} catch (Throwable e) {
		}
	
		return FileUtil.getDisplaySize(size, SessionManagement.getLanguage()).trim();
	}
}