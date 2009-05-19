package com.logicaldoc.web.util;

import java.io.UnsupportedEncodingException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * This converter automatically detects the charset and converts the string
 * using the proper encoding.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.0
 */
public class UTFConverter implements Converter {

	public UTFConverter() {
	}

	public Object getAsObject(FacesContext context, UIComponent component, String arg2) {
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component, Object object) {
		String str = null;

		if (object != null)
			str = object.toString();

		if (str != null) {
			try {
				str = new String(str.getBytes(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return str;
	}
}
