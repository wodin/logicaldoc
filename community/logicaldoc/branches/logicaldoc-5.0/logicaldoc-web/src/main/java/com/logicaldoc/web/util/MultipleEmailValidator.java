package com.logicaldoc.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import com.logicaldoc.web.i18n.Messages;

/**
 * A JSF compliant validator that validates a comma separated list of email
 * addresses
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class MultipleEmailValidator extends EmailValidator {
	public MultipleEmailValidator() {
		super();
	}

	public void validate(FacesContext facesContext, UIComponent uIComponent, Object value) throws ValidatorException {

		// Get the component's contents and cast it to a String
		String enteredEmail = (String) value;
		enteredEmail = enteredEmail.toLowerCase();

		List<String> recipients = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(enteredEmail, ", ;", false);
		recipients.clear();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (!validateEmail(token)) {
				FacesMessage message = new FacesMessage();
				message.setDetail(Messages.getMessage("errors.email"));
				message.setSummary(Messages.getMessage("errors.email"));
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(message);
			}
		}
	}
}
