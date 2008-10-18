package com.logicaldoc.web.document;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DownloadTicket;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;
import com.logicaldoc.util.io.FileUtil;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.PageContentBean;

/**
 * This form is used to send emails
 * 
 * TODO reimplement since now documents don't have associated menu
 * 
 * @author Michael Scholz, Marco Meschieri
 * @version $Id: EMailForm.java,v 1.2 2006/09/03 16:24:37 marco Exp $
 * @since 1.0
 */
public class EMailForm {
	protected static Log log = LogFactory.getLog(EMailForm.class);

	private String author;

	private String recipient;

	private String subject;

	private String text;

	private Document selectedDocument;

	private Collection<Menu> attachments = new ArrayList<Menu>();

	private DownloadTicket downloadTicket;

	private DocumentNavigation documentNavigation;

	public EMailForm() {
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Collection<Menu> getAttachments() {
		return attachments;
	}

	public void setAttachments(Collection<Menu> attachments) {
		this.attachments = attachments;
	}

	public void reset() {
		author = "";
		recipient = "";
		subject = "";
		text = "";
		attachments.clear();
		downloadTicket = null;
		selectedDocument = null;
	}

	public Document getSelectedDocument() {
		return selectedDocument;
	}

	public void setSelectedDocument(Document selectedDocument) {
		this.selectedDocument = selectedDocument;
	}

	public DownloadTicket getDownloadTicket() {
		return downloadTicket;
	}

	public void setDownloadTicket(DownloadTicket downloadTicket) {
		this.downloadTicket = downloadTicket;
	}

	public String toString() {
		return (new ReflectionToStringBuilder(this) {
			protected boolean accept(java.lang.reflect.Field f) {
				return super.accept(f);
			}
		}).toString();
	}

	public String send() {
		EMail email;
		SettingsConfig conf = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
		String udir = conf.getValue("userdir");
		String maildir = udir + "/mails/";
		if (SessionManagement.isValid()) {
			try {
				User user = SessionManagement.getUser();
				email = new EMail();

				email.setAccountId(-1);
				email.setAuthor(user.getUserName());
				// email.setAuthorAddress(user.getEmail());
				email.setAuthorAddress(getAuthor());

				Recipient recipient = new Recipient();
				recipient.setAddress(getRecipient());
				email.addRecipient(recipient);
				email.setFolder("outbox");
				email.setMessageText(getText());
				email.setRead(1);
				email.setSentDate(String.valueOf(new Date().getTime()));
				email.setSubject(getSubject());
				email.setUserName(user.getUserName());

				File mailDir = new File(maildir + String.valueOf(email.getId()) + "/");
				FileUtils.forceMkdir(mailDir);				
				FileUtil.writeFile(email.getMessageText(), maildir + "email.mail");
				int i = 2;

				for (Menu menu : attachments) {
					createAttachment(email, menu.getId(), i++);
				}

				try {
					EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
					sender.send(email);
					Messages.addLocalizedInfo("msg.action.saveemail");
				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
					Messages.addLocalizedError("errors.action.saveemail");
				} finally {
					FileUtils.forceDelete(new File(maildir));
				}

				setAuthor(user.getEmail());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.saveemail");
			}
		} else {
			return "login";
		}

		documentNavigation.setSelectedPanel(new PageContentBean("documents"));
		reset();

		return null;
	}

	private void createAttachment(EMail email, long menuId, int partid) {
		// TODO reimplement
		// Attachment att = new Attachment();
		// MenuDAO menuDao = (MenuDAO)
		// Context.getInstance().getBean(MenuDAO.class);
		// Menu menu = menuDao.findByPrimaryKey(menuId);
		//
		// if (menuDao.isReadEnable(menu.getId(), email.getUserName())) {
		// att.setIcon(menu.getIcon());
		// att.setFilename(menu.getRef());
		//
		// String extension =
		// menu.getRef().substring(menu.getRef().lastIndexOf(".") + 1);
		// MimeTypeConfig mtc = (MimeTypeConfig)
		// Context.getInstance().getBean(MimeTypeConfig.class);
		// String mimetype = mtc.getMimeApp(extension);
		//
		// if ((mimetype == null) || mimetype.equals("")) {
		// mimetype = "application/octet-stream";
		// }
		//
		// att.setMimeType(mimetype);
		//
		// SettingsConfig conf = (SettingsConfig)
		// Context.getInstance().getBean(SettingsConfig.class);
		// String docdir = conf.getValue("docdir");
		// String doc = docdir + "/" + menu.getPath() + "/" + menu.getId() + "/"
		// + menu.getRef();
		// String userdir = conf.getValue("userdir");
		// String mail = userdir + "/mails/" +
		// String.valueOf(email.getMessageId()) + "/";
		// FileBean.createDir(mail);
		// FileBean.copyFile(doc, mail + menu.getRef());
		//			
		// if (att != null) {
		// email.addAttachment(partid, att);
		// }
		// }
	}

	public void setDocumentNavigation(DocumentNavigation documentNavigation) {
		this.documentNavigation = documentNavigation;
	}
}