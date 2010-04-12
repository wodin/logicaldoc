package com.logicaldoc.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.logicaldoc.web.i18n.Messages;

/**
 * A JSF compliant validator that validates strings allowing only letters,
 * numbers, and '-' '_' special characters.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class SimpleStringValidator implements Validator {
	public SimpleStringValidator() {
		super();
	}

	public void validate(FacesContext facesContext, UIComponent uIComponent, Object value) throws ValidatorException {
		// Get the component's contents and cast it to a String
		String str = (String) value;
		
		// Set the email pattern string
		Pattern p = Pattern.compile("[\\w\\-]*");

		// Match the given string with the pattern
		Matcher m = p.matcher(str);

		// Check whether match is found
		boolean matchFound = m.matches();

		if (!matchFound) {
			FacesMessage message = new FacesMessage();
			message.setDetail(Messages.getMessage("validator.simplestring.error"));
			message.setSummary(Messages.getMessage("validator.simplestring.error"));
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
	}
}
