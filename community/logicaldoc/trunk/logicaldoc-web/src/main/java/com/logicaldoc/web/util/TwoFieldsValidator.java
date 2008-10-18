package com.logicaldoc.web.util;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.logicaldoc.web.i18n.Messages;

/**
 * A JSF compliant validator that validates 2 fields
 * 
 * @author Jesus Marin - APL software
 * @version $Id: TwoFieldsValidator.java,v 1.1 2006/08/23 16:22:57 jesusapl Exp $
 * @since 3.0
 */
public class TwoFieldsValidator implements Validator {

	public TwoFieldsValidator() {
		super();
	}

	/**
	 * @see javax.faces.validator.Validator#validate(javax.faces.context.FacesContext,
	 *      javax.faces.component.UIComponent, java.lang.Object)
	 */
	public void validate(final FacesContext facesContext, final UIComponent uIComponent, final Object value)
			throws ValidatorException {
		// Get the component's contents and cast it to a String

		if (value == null) {
			return;
		}

		Integer upperLimit = null;
		Integer lowerLimit = null;
		Long temp = null;

		temp = (Long) value;

		if (temp != null) {
			upperLimit = Integer.valueOf(temp.intValue());
		}

		String lowerId = (String) uIComponent.getAttributes().get("lowerId");

		temp = (Long) ((UIInput) FacesUtil.findComponentEndingWithId(lowerId, facesContext)).getValue();

		if (temp != null) {
			lowerLimit = Integer.valueOf(temp.intValue());
		}

		if ((upperLimit == 0) && (lowerLimit != 0)) {
			UIInput thisValue;
			thisValue = (UIInput) uIComponent;
			thisValue.setSubmittedValue(lowerLimit.toString());
			((UIInput) uIComponent).setSubmittedValue(lowerLimit.toString());
			((UIInput) uIComponent).setValue(lowerLimit.toString());

			return;
		}

		if (lowerLimit.longValue() > upperLimit.longValue()) {
			FacesMessage message = new FacesMessage();
			message.setDetail(Messages.getMessage("errors.val.twonumbers"));
			message.setSummary(Messages.getMessage("errors.val.twonumbers"));
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
	}
}