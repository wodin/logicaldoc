package com.logicaldoc.web.admin;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;

/**
 * Form for SMTP settings
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 2.0
 */
public class SmtpForm {
	protected static Log log = LogFactory.getLog(SmtpForm.class);

	private String password;

	private boolean deletePassword = false;

	private EMailSender getSender() {
		return (EMailSender) Context.getInstance().getBean(EMailSender.class);
	}

	public String getSenderAddress() {
		return getSender().getSender();
	}

	public void setSenderAddress(String sender) {
		getSender().setSender(sender);
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

				PropertiesBean pbean = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
				pbean.setProperty("smtp.host", sender.getHost());
				pbean.setProperty("smtp.port", Integer.toString(sender.getPort()));
				pbean.setProperty("smtp.username", StringUtils.isNotEmpty(sender.getUsername()) ? sender.getUsername()
						: "");
				pbean.setProperty("smtp.password", StringUtils.isNotEmpty(sender.getPassword()) ? sender.getPassword()
						: "");
				pbean.setProperty("smtp.sender", sender.getSender());
				if (StringUtils.isNotEmpty(password)) {
					pbean.setProperty("smtp.password", password);
					getSender().setPassword(password);
				} else if (deletePassword == true) {
					pbean.setProperty("smtp.password", "");
					getSender().setPassword("");
				}
				pbean.setProperty("smtp.authEncripted", sender.isAuthEncripted() ? "true" : "false");
				pbean.setProperty("smtp.connectionSecurity", Integer.toString(sender.getConnectionSecurity()));
				pbean.write();

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

	public boolean isEmptyPassword() {
		return (StringUtils.isEmpty(getSender().getPassword()) || deletePassword);
	}

	public String removePassword() {
		setPassword(null);
		deletePassword = true;
		// JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(),
		// "window.location.reload(false);");
		return null;
	}

	public int getConnectionSecurity() {
		return getSender().getConnectionSecurity();
	}

	public void setConnectionSecurity(int connectionSecurity) {
		getSender().setConnectionSecurity(connectionSecurity);
	}

	public boolean isAuthEncripted() {
		return getSender().isAuthEncripted();
	}

	public void setAuthEncripted(boolean authEncripted) {
		getSender().setAuthEncripted(authEncripted);
	}
}