package com.logicaldoc.web.communication;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.dao.SystemMessageDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * A system message editing form
 * 
 * @author Marco Meschieri - Logical Objects
 * @version $Id: MessageForm.java,v 1.2 2006/08/31 15:31:18 marco Exp $
 * @since ###release###
 */
public class MessageForm {
	protected static Log log = LogFactory.getLog(MessageForm.class);

	private SystemMessage message = new SystemMessage();

	private boolean readOnly = true;

	private boolean confirmation = false;

	private String recipientInserted = "";
	
	private UIInput recipientInsertedInput = null;

	public SystemMessage getMessage() {
		return message;
	}

	public void setMessage(SystemMessage message) {
		this.message = message;
		confirmation = message.getConfirmation() != 0;
		recipientInserted = "";
		FacesUtil.forceRefresh(recipientInsertedInput);
	}

	public String back() {
		MessagesRecordsManager manager = ((MessagesRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"messagesRecordsManager", FacesContext.getCurrentInstance(), log));
		manager.listMessages();

		return null;
	}

	public String insert() {
		return null;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean edit) {
		this.readOnly = edit;
	}

	public boolean isConfirmation() {
		return confirmation;
	}

	public void setConfirmation(boolean confirmation) {
		this.confirmation = confirmation;

		if (confirmation && (message != null)) {
			message.setConfirmation(1);
		} else if (!confirmation && (message != null)) {
			message.setConfirmation(0);
		}
	}

	public String save() {
		if (SessionManagement.isValid()) {
			try {
				UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
				User user = udao.findByUserName(recipientInserted);
				if (user != null) {
					message.setAuthor(SessionManagement.getUsername());
					message.setSentDate(new Date());
					message.setStatus(SystemMessage.STATUS_NEW);
					message.setType(SystemMessage.TYPE_SYSTEM);
					message.setLastNotified(new Date());
					Recipient recipient = new Recipient();
					recipient.setName(recipientInserted);
					recipient.setAddress(recipientInserted);
					recipient.setType(Recipient.TYPE_SYSTEM);
					recipient.setMode("message");
					Set<Recipient> recipients = new HashSet<Recipient>();
					recipients.add(recipient);
					message.setRecipients(recipients);
					SystemMessageDAO smdao = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);
					boolean stored = smdao.store(message);

					if (!stored) {
						Messages.addLocalizedError("errors.action.savesysmess");
					} else {
						Messages.addLocalizedInfo("msg.action.savesysmess");
					}
				} else {
					String id = FacesUtil.findParameterEndingWithId("messageForm:recipient", FacesContext
							.getCurrentInstance());
					Messages.addLocalizedError("errors.action.usernotexists", id);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.savesysmess");
			}
		} else {
			return "login";
		}

		return back();
	}

	public String getRecipientInserted() {
		return recipientInserted;
	}

	public void setRecipientInserted(String recipientInserted) {
		this.recipientInserted = recipientInserted;
	}

	public UIInput getRecipientInsertedInput() {
		return recipientInsertedInput;
	}

	public void setRecipientInsertedInput(UIInput recipientInsertedInput) {
		this.recipientInsertedInput = recipientInsertedInput;
	}
}
