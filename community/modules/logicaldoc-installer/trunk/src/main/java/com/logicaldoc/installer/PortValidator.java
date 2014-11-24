package com.logicaldoc.installer;

import com.izforge.izpack.panels.userinput.processorclient.ProcessingClient;
import com.izforge.izpack.panels.userinput.validator.Validator;
import com.logicaldoc.installer.util.PortUtil;


/**
 * Checks if the specified port is available or not.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.8.1
 */
public class PortValidator implements Validator {

	@Override
	public boolean validate(ProcessingClient client) {
		try {
			String port = client.getText().trim();
			return PortUtil.available(Integer.parseInt(port));
		} catch (Throwable t) {
			return false;
		}
	}
}