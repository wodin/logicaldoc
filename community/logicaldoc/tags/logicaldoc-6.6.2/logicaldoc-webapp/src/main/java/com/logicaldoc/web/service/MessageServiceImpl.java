package com.logicaldoc.web.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.communication.MessageTemplate;
import com.logicaldoc.core.communication.MessageTemplateDAO;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.SystemMessageDAO;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.beans.GUIMessageTemplate;
import com.logicaldoc.gui.frontend.client.services.MessageService;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Implementation of the MessageService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MessageServiceImpl extends RemoteServiceServlet implements MessageService {

	private static Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

	private static final long serialVersionUID = 1L;

	@Override
	public void delete(String sid, long[] ids) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		Context context = Context.getInstance();
		SystemMessageDAO dao = (SystemMessageDAO) context.getBean(SystemMessageDAO.class);
		for (long id : ids) {
			dao.delete(id);
		}
	}

	@Override
	public GUIMessage getMessage(String sid, long messageId, boolean markAsRead) throws InvalidSessionException {
		UserSession user = SessionUtil.validateSession(sid);

		Context context = Context.getInstance();
		SystemMessageDAO dao = (SystemMessageDAO) context.getBean(SystemMessageDAO.class);
		SystemMessage message = dao.findById(messageId);
		dao.initialize(message);
		
		GUIMessage m = new GUIMessage();
		m.setId(message.getId());
		m.setSubject(message.getSubject());
		m.setConfirmation(message.getConfirmation() == 1);
		m.setMessage(message.getMessageText());
		m.setValidity(message.getDateScope());

		// If the case mark the message as read
		if (!message.wasReadBy(user.getUserName())) {
			Recipient rec=message.getRecipient(user.getUserName());
			rec.setRead(1);
			dao.store(message);

			// If required a notification message must be sent to the sender
			if (message.getConfirmation() == 1) {
				Date date = new Date();
				Recipient recipient = new Recipient();
				recipient.setName(message.getAuthor());
				recipient.setAddress(message.getAuthor());
				recipient.setType(Recipient.TYPE_SYSTEM);
				recipient.setRead(0);
				recipient.setMode("");
				Set<Recipient> recipients = new HashSet<Recipient>();
				recipients.add(recipient);
				SystemMessage sysmess = new SystemMessage();
				sysmess.setAuthor("SYSTEM");
				sysmess.setRecipients(recipients);
				sysmess.setSubject("Confirmation");
				sysmess.setMessageText("To: " + recipient.getName() + "\nMessage: " + message.getMessageText());
				sysmess.setSentDate(date);
				sysmess.setConfirmation(0);
				sysmess.setPrio(message.getPrio());
				sysmess.setDateScope(message.getDateScope());
				dao.store(sysmess);
			}
		}

		return m;
	}

	@Override
	public void save(String sid, GUIMessage message) throws InvalidSessionException {
		UserSession session = SessionUtil.validateSession(sid);
		Context context = Context.getInstance();
		SystemMessageDAO dao = (SystemMessageDAO) context.getBean(SystemMessageDAO.class);

		SystemMessage m = new SystemMessage();
		m.setAuthor(session.getUserName());
		m.setSentDate(new Date());
		m.setStatus(SystemMessage.STATUS_NEW);
		m.setType(SystemMessage.TYPE_SYSTEM);
		m.setLastNotified(new Date());
		m.setMessageText(message.getMessage());
		m.setSubject(message.getSubject());
		Recipient recipient = new Recipient();
		recipient.setName(message.getRecipient());
		recipient.setAddress(message.getRecipient());
		recipient.setType(Recipient.TYPE_SYSTEM);
		recipient.setMode("message");
		Set<Recipient> recipients = new HashSet<Recipient>();
		recipients.add(recipient);
		m.setRecipients(recipients);
		m.setDateScope(message.getValidity());
		m.setPrio(message.getPriority());
		if (message.isConfirmation())
			m.setConfirmation(1);
		else
			m.setConfirmation(0);
		dao.store(m);
	}

	@Override
	public GUIMessageTemplate[] loadTemplates(String sid, String language) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		Context context = Context.getInstance();

		try {
			MessageTemplateDAO dao = (MessageTemplateDAO) context.getBean(MessageTemplateDAO.class);

			List<GUIMessageTemplate> buf = new ArrayList<GUIMessageTemplate>();

			List<MessageTemplate> standardTemplates = dao.findByLanguage("en");
			Map<String, MessageTemplate> templates = new HashMap<String, MessageTemplate>();

			List<MessageTemplate> tmp = dao.findByLanguage(language);
			for (MessageTemplate m : tmp) {
				templates.put(m.getName(), m);
			}

			for (MessageTemplate test : standardTemplates) {
				MessageTemplate template = test;
				if (templates.containsKey(test.getName()))
					template = templates.get(test.getName());

				GUIMessageTemplate t = new GUIMessageTemplate();
				t.setId(template.getId());
				t.setLanguage(language);
				t.setName(template.getName());
				t.setSubject(template.getSubject());
				t.setBody(template.getBody());
				buf.add(t);
			}

			return buf.toArray(new GUIMessageTemplate[0]);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void saveTemplates(String sid, GUIMessageTemplate[] templates) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			Context context = Context.getInstance();
			MessageTemplateDAO dao = (MessageTemplateDAO) context.getBean(MessageTemplateDAO.class);

			for (GUIMessageTemplate t : templates) {
				MessageTemplate template = dao.findByNameAndLanguage(t.getName(), t.getLanguage());
				if (template == null || !template.getLanguage().equals(t.getLanguage()))
					template = new MessageTemplate();
				template.setName(t.getName());
				template.setLanguage(t.getLanguage());
				template.setSubject(t.getSubject());
				template.setBody(t.getBody());
				dao.store(template);
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void deleteTemplates(String sid, long[] ids) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			Context context = Context.getInstance();
			MessageTemplateDAO dao = (MessageTemplateDAO) context.getBean(MessageTemplateDAO.class);

			for (long id : ids) {
				if (id != 0L)
					dao.delete(id);
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}
}