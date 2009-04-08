package com.logicaldoc.web.document;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailAttachment;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.DownloadTicket;
import com.logicaldoc.core.security.User;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.MimeTypeConfig;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.PageContentBean;

/**
 * This form is used to send emails
 * 
 * @author Michael Scholz
 * @author Matteo Caruso - Logical Objects
 * @since 1.0
 */
public class EMailForm {
	protected static Log log = LogFactory.getLog(EMailForm.class);

	private String author;

	private String recipient;

	private String subject;

	private String text;

	private Document selectedDocument;

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

	public void reset() {
		author = "";
		recipient = "";
		subject = "";
		text = "";
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
		if (SessionManagement.isValid()) {
			try {
				User user = SessionManagement.getUser();
				email = new EMail();

				email.setAccountId(-1);
				email.setAuthor(user.getUserName());
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

				if (getSelectedDocument() != null)
					createAttachment(email);

				try {
					EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
					sender.send(email);
					Messages.addLocalizedInfo("email.sent");
				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
					Messages.addLocalizedError("email.error");
				}
				setAuthor(user.getEmail());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("email.error");
			}
		} else {
			return "login";
		}

		documentNavigation.setSelectedPanel(new PageContentBean("documents"));
		reset();

		return null;
	}

	private void createAttachment(EMail email) throws IOException {
		EMailAttachment att = new EMailAttachment();
		att.setIcon(selectedDocument.getIcon());
		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		File file = manager.getDocumentFile(selectedDocument);
		att.setFile(file);
		att.setFileName(selectedDocument.getFileName());
		String extension = selectedDocument.getFileExtension();
		MimeTypeConfig mtc = (MimeTypeConfig) Context.getInstance().getBean(MimeTypeConfig.class);
		String mimetype = mtc.getMimeApp(extension);

		if ((mimetype == null) || mimetype.equals("")) {
			mimetype = "application/octet-stream";
		}

		att.setMimeType(mimetype);

		if (att != null) {
			email.addAttachment(2, att);
		}
	}

	public void setDocumentNavigation(DocumentNavigation documentNavigation) {
		this.documentNavigation = documentNavigation;
	}
}