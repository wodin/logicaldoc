package com.logicaldoc.web.util;

import com.logicaldoc.web.i18n.Messages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;


/**
 * A JSF compliant validator that validates email addresses
 *
 * @author Marco Meschieri - Logical Objects
 * @version $Id: EmailValidator.java,v 1.1 2006/08/23 16:22:57 marco Exp $
 * @since ###release###
 */
public class EmailValidator implements Validator {
    public EmailValidator() {
        super();
    }

    public void validate(FacesContext facesContext, UIComponent uIComponent,
        Object value) throws ValidatorException {
        // Get the component's contents and cast it to a String
        String enteredEmail = (String) value;

        // Set the email pattern string
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");

        // Match the given string with the pattern
        Matcher m = p.matcher(enteredEmail);

        // Check whether match is found
        boolean matchFound = m.matches();

        if (!matchFound) {
            FacesMessage message = new FacesMessage();
            message.setDetail(Messages.getMessage("errors.email"));
            message.setSummary(Messages.getMessage("errors.email"));
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }
}
