package com.logicaldoc.web.admin;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SMTPConfig;

import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;

/**
 * Form for SMTP settings
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since ###release###
 */
public class SmtpForm {
	protected static Log log = LogFactory.getLog(SmtpForm.class);

	private String password;

	private boolean deletePassword = false;

	private EMailSender getSender() {
		return (EMailSender) Context.getInstance().getBean(EMailSender.class);
	}

	public String getDefaultAddress() {
		return getSender().getDefaultAddress();
	}

	public void setDefaultAddress(String defaultAddress) {
		getSender().setDefaultAddress(defaultAddress);
	}

	public String getHost() {
		return getSender().getHost();
	}

	public void setHost(String host) {
		getSender().setHost(host);
	}

	public String getPassword() {
		if (password == null)
			return getSender().getPassword();
		else
			return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return getSender().getPort();
	}

	public void setPort(int port) {
		getSender().setPort(port);
	}

	public String getUsername() {
		return getSender().getUsername();
	}

	public void setUsername(String username) {
		getSender().setUsername(username);
	}

	public String save() {
		if (SessionManagement.isValid()) {
			try {
				EMailSender sender = getSender();

				SMTPConfig config = new SMTPConfig();
				config.setHost(sender.getHost());
				config.setPort(Integer.toString(sender.getPort()));
				config.setUsername(sender.getUsername());
				
				if (StringUtils.isNotEmpty(password)) {
					config.setPassword(password);
				} else if (deletePassword == true) {
					config.setPassword(null);
				}
				
				config.setDefaultAddress(sender.getDefaultAddress());
				config.write();
				deletePassword = false;
				Messages.addLocalizedInfo("msg.action.savesettings");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.savesettings");
			}

			return null;
		} else {
			return "login";
		}
	}

	public boolean isEmptyPassword(){
		return (StringUtils.isEmpty(getSender().getPassword()) || deletePassword);
	}
	
	public String removePassword() {
		setPassword(null);
		deletePassword = true;
		//JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "window.location.reload(false);");
		return null;
	}
}