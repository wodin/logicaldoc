package com.logicaldoc.web.communication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.dao.SystemMessageDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.StyleBean;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.NavigationBean;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.util.FacesUtil;

/**
 * <p>
 * The <code>MessagesRecordsManager</code> class is responsible for constructing
 * the list of <code>SystemMessage</code> beans which will be bound to a
 * ice:dataTable JSF component.
 * </p>
 * <p>
 * Large data sets could be handle by adding a ice:dataPaginator. Alternatively
 * the dataTable could also be hidden and the dataTable could be added to
 * scrollable ice:panelGroup.
 * </p>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class MessagesRecordsManager {
	protected static Log log = LogFactory.getLog(MessagesRecordsManager.class);

	private List<SystemMessage> messages = new ArrayList<SystemMessage>();

	private PageContentBean selectedPanel = new PageContentBean("list");

	private NavigationBean navigation;

	private MessageForm form;

	public MessagesRecordsManager() {
		messages.clear();
	}

	public PageContentBean getSelectedPanel() {
		return selectedPanel;
	}

	public void setSelectedPanel(PageContentBean panel) {
		this.selectedPanel = panel;
	}

	/**
	 * Reloads the messages list
	 */
	public String listMessages() {
		SystemMessageDAO smdao = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);
		messages = smdao.findByRecipient(SessionManagement.getUsername(), SystemMessage.TYPE_SYSTEM, null);
		smdao.deleteExpiredMessages(SessionManagement.getUsername());

		PageContentBean content = new PageContentBean("messages", "communication/messages");
		content.setContentTitle(Messages.getMessage("menu.readmessages"));
		StyleBean style = (StyleBean) Context.getInstance().getBean(StyleBean.class);
		content.setIcon(style.getImagePath("message.png"));
		navigation.setSelectedPanel(content);

		setSelectedPanel(new PageContentBean("list"));

		return null;
	}

	/**
	 * Shows the message insert form
	 */
	public String addMessage() {
		NavigationBean navigation = ((NavigationBean) FacesUtil.accessBeanFromFacesContext("navigation", FacesContext
				.getCurrentInstance(), log));

		StyleBean style = (StyleBean) Context.getInstance().getBean(StyleBean.class);
		PageContentBean content = new PageContentBean("message", "communication/messages");
		content.setContentTitle(Messages.getMessage("menu.createmessage"));
		content.setIcon(style.getImagePath("message.png"));
		navigation.setSelectedPanel(content);

		// Initialize the form
		form.setReadOnly(false);
		form.setMessage(new SystemMessage());

		setSelectedPanel(new PageContentBean("insert"));

		return null;
	}

	/**
	 * Cleans up the resources used by this class. This method could be called
	 * when a session destroyed event is called.
	 */
	public void dispose() {
		messages.clear();
	}

	/**
	 * Gets the list of SystemMessages which will be used by the ice:dataTable
	 * component.
	 */
	public Collection<SystemMessage> getMessages() {
		if (messages.size() == 0) {
			listMessages();
		}

		return messages;
	}

	public String selectMessage() {
		Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		SystemMessage record = (SystemMessage) map.get("messageRecord");
		SystemMessageDAO smdao = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);
		smdao.initialize(record);
		if (record.getRead() == 0) {
			record.setRead(1);
			smdao.store(record);

			if (record.getConfirmation() == 1) {
				Date date = new Date();
				Recipient recipient = new Recipient();
				recipient.setName(record.getAuthor());
				recipient.setAddress(record.getAuthor());
				recipient.setType(Recipient.TYPE_SYSTEM);
				recipient.setMode("");
				Set<Recipient> recipients = new HashSet<Recipient>();
				recipients.add(recipient);
				SystemMessage sysmess = new SystemMessage();
				sysmess.setAuthor("SYSTEM");
				sysmess.setRecipients(recipients);
				sysmess.setSubject("Confirmation");
				sysmess.setMessageText("To: " + recipient.getName() + "\nMessage: " + record.getMessageText());
				sysmess.setSentDate(date);
				sysmess.setRead(0);
				sysmess.setConfirmation(0);
				sysmess.setPrio(record.getPrio());
				sysmess.setDateScope(record.getDateScope());
				smdao.store(sysmess);
			}
		}

		// Initialize the form
		MessageForm form = ((MessageForm) FacesUtil.accessBeanFromFacesContext("messageForm", FacesContext
				.getCurrentInstance(), log));
		form.setReadOnly(true);
		form.setMessage(record);

		selectedPanel = new PageContentBean("view");

		return null;
	}

	public String deleteMessage() {
		Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		SystemMessage record = (SystemMessage) map.get("messageRecord");

		if (SessionManagement.isValid()) {
			try {
				SystemMessageDAO smDao = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);
				boolean deleted = smDao.delete(record.getId());

				if (!deleted) {
					Messages.addLocalizedError("errors.action.deletesysmess");
				} else {
					Messages.addLocalizedInfo("msg.action.deletesysmess");
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.deletesysmess");
			}
		} else {
			return "login";
		}

		listMessages();

		return null;
	}

	public int getToBeReadCount() {
		SystemMessageDAO smdao = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);
		int smcount = smdao.getCount(SessionManagement.getUsername(), SystemMessage.TYPE_SYSTEM, null);

		return smcount;
	}

	public int getCount() {
		if (messages.size() == 0) {
			listMessages();
		}

		return messages.size();
	}

	public void setNavigation(NavigationBean navigation) {
		this.navigation = navigation;
	}

	public void setForm(MessageForm form) {
		this.form = form;
	}
}
