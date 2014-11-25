package com.logicaldoc.installer;

import com.izforge.izpack.panels.userinput.processorclient.ProcessingClient;
import com.izforge.izpack.panels.userinput.validator.Validator;

/**
 * Checks if the specified password is or is not suitable
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1.1
 */
public class PasswordWeeknessValidator implements Validator {

	@Override
	public boolean validate(ProcessingClient client) {
		try {
			String password = client.getText().trim();
			return password != null && password.trim().length() >= 6;
		} catch (Throwable t) {
			return false;
		}
	}
}