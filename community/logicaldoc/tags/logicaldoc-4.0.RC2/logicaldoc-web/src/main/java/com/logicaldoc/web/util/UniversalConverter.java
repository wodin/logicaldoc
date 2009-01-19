package com.logicaldoc.web.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.logicaldoc.util.CharsetDetector;

/**
 * This converter automatically detects the charset and converts the string
 * using the proper encoding.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.0
 */
public class UniversalConverter implements Converter {

	public UniversalConverter() {
	}

	public Object getAsObject(FacesContext context, UIComponent component, String arg2) {
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component, Object object) {
		String str = null;

		if (object != null)
			str = object.toString();

		if (str != null) {
			str = CharsetDetector.convert(str);
		}
		return str;
	}
}
