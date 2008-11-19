package com.logicaldoc.web.util;

import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.web.i18n.Messages;

public class RequiredConverter implements Converter {

	protected static Log log = LogFactory.getLog(RequiredConverter.class);

	public final Object getAsObject(final FacesContext facesContext, final UIComponent component, final String value) {

		if (StringUtils.isNotEmpty(value)) {
			return value;
		}

		FacesMessage message = new FacesMessage();
		message.setDetail(Messages.getMessage("errors.field.required"));
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		facesContext.addMessage(component.getClientId(facesContext), message);

		Iterator<FacesMessage> iter = facesContext.getMessages();
		boolean addError = true;
		String strConvert = Messages.getMessage("javax.faces.component.UIInput.CONVERSION");

		while (iter.hasNext()) {
			FacesMessage str = iter.next();

			if (str.getDetail().equals(strConvert)) {
				addError = false;
				break;
			}
		}

		if (addError) {
			throw new ConverterException(new FacesMessage(strConvert));
		} else {
			return null;
		}
	}

	public final String getAsString(final FacesContext facesContext, final UIComponent component, final Object object)
			throws ConverterException {

		if (object != null) {
			if (object instanceof Integer) {
				return ((Integer) object).toString();
			}
		}
		return (String) object;
	}
}
