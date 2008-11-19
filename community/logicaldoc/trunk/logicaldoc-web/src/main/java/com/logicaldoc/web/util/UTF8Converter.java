package com.logicaldoc.web.util;

import java.io.UnsupportedEncodingException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class UTF8Converter implements Converter {

	public UTF8Converter() {
	}

	public Object getAsObject(FacesContext context, UIComponent component, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component, Object object) {

		String utfs = null;

		if (object != null)
			utfs = object.toString();

		if (utfs != null) {
			try {
				utfs = new String(utfs.getBytes(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return utfs;
	}

}
