package com.logicaldoc.core.communication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.communication.dao.EMailAccountDAO;
import com.logicaldoc.core.communication.dao.EMailDAO;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.core.searchengine.store.Storer;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;

/**
 * This component downloads new emails from one or more e-mail accounts
 * 
 * @author Michael Scholz, Marco Meschieri
 */
public class EMailReceiver extends Task {
	public static final String NAME = "EMailReceiver";

	// The default username that owns downloaded documents
	private String defaultOwner = "admin";

	private EMailAccountDAO accountDao;

	private EMailDAO emailDao;

	private MenuDAO menuDao;

	private SettingsConfig settingsConfig;

	private Storer storer;

	private DocumentDAO documentDao;

	private HistoryDAO historyDao;

	private Indexer indexer;

	private int imported = 0;

	private int errors = 0;

	private EMailReceiver() {
		super(NAME);
		log = LogFactory.getLog(EMailReceiver.class);
	}

	public Indexer getIndexer() {
		return indexer;
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	public DocumentDAO getDocumentDao() {
		return documentDao;
	}

	public void setDocumentDao(DocumentDAO documentDao) {
		this.documentDao = documentDao;
	}

	public HistoryDAO getHistoryDao() {
		return historyDao;
	}

	public void setHistoryDao(HistoryDAO historyDAO) {
		this.historyDao = historyDAO;
	}

	public MenuDAO getMenuDao() {
		return menuDao;
	}

	public void setMenuDao(MenuDAO menuDao) {
		this.menuDao = menuDao;
	}

	public Storer getStorer() {
		return storer;
	}

	public void setStorer(Storer storer) {
		this.storer = storer;
	}

	public EMailDAO getEmailDao() {
		return emailDao;
	}

	public void setEmailDao(EMailDAO emailDao) {
		this.emailDao = emailDao;
	}

	public EMailAccountDAO getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(EMailAccountDAO accountDao) {
		this.accountDao = accountDao;
	}

	public String getDefaultOwner() {
		return defaultOwner;
	}

	public void setDefaultOwner(String defaultOwner) {
		this.defaultOwner = defaultOwner;
	}

	public SettingsConfig getSettingsConfig() {
		return settingsConfig;
	}

	public void setSettingsConfig(SettingsConfig settingsConfig) {
		this.settingsConfig = settingsConfig;
	}

	/**
	 * Downloads all new mails from all accounts. The stored document will be
	 * owned by the specified username
	 * 
	 * @throws Exception
	 */
	public void receiveMails(String username) {
		Collection<EMailAccount> accounts = accountDao.findAll();
		size = 0;

		// First of all determine the task length
		for (EMailAccount account : accounts) {
			try {
				if (account.getEnabled() != 0) {
					size += countMails(account);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		}
		log.info("A total amount of " + size + " messages was found");

		for (EMailAccount account : accounts) {
			if (account.getEnabled() == 0) {
				log.warn("Skip account " + account.getMailAddress() + " because disabled");
				continue;
			}
			try {
				log.info("Connect to " + account.getMailAddress());
				receive(account, defaultOwner);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				setProgress(getSize());
			}
		}
	}

	/**
	 * Count all mails from the specified account
	 */
	private int countMails(EMailAccount account) throws MessagingException {
		int count = 0;
		Store store = null;
		Folder inbox = null;
		try {
			Session session = Session.getInstance(new Properties());
			store = session.getStore(account.getProvider());
			store.connect(account.getHost(), account.getAccountUser(), account.getAccountPassword());
			inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_ONLY);
			count = inbox.getMessageCount();
		} finally {
			if (inbox != null)
				inbox.close(true);
			if (store != null)
				store.close();
		}
		return count;
	}

	public void receive(EMailAccount account, String username) throws MessagingException {
		// Get a session. Use a blank Properties object.
		Session session = Session.getInstance(new Properties());
		Store store = session.getStore(account.getProvider());
		store.connect(account.getHost(), account.getAccountUser(), account.getAccountPassword());
		
//		log.info("account.getProvider(): " + account.getProvider());
//		log.info("account.getHost(): " + account.getHost());
//		log.info("account.getAccountUser(): " + account.getAccountUser());
//		log.info("account.getAccountPassword(): " + account.getAccountPassword());

		// Open Folder INBOX
		Folder inbox = store.getFolder("INBOX");
		if (inbox != null) {
			inbox.open(Folder.READ_ONLY);
			int count = inbox.getMessageCount();
			log.info(count + " total messages in " + account.getMailAddress());

			// fetch messages from server
			for (int i = 1; i <= count; i++) {
				if (interruptRequested)
					break;
				EMail email = new EMail();
				try {
					javax.mail.Message message = inbox.getMessage(i);
					message.setFlag(Flags.Flag.DELETED, account.getDeleteFromMailbox() > 0);					
					
//					Enumeration myEnum = message.getAllHeaders();
//					while (myEnum.hasMoreElements()) {
//						Header element = (Header) myEnum.nextElement();
//						log.info("header: " + element.getName() + ", " + element.getValue());
//					}
										
                    String mailId;
					if (message.getHeader("Message-ID") != null)
						mailId = message.getHeader("Message-ID")[0];
					else {
						mailId = message.getFrom()[0] + "." + message.getSentDate().getTime();
					}

					Collection<String> alreadyRetrievedIds = this.emailDao.collectEmailIds(account.getAccountId());
					if (alreadyRetrievedIds.contains(mailId)) {
						if (log.isDebugEnabled())
							log.debug("Skip message " + mailId + " because already fetched from " + account.toString());
						continue;
					}

					InternetAddress from = ((InternetAddress) message.getFrom()[0]);
					Address[] recipients = new Address[] {};
					try {
						recipients = message.getAllRecipients();
					} catch (AddressException e) {
						log.error(e);
					}

					// store message in database
					if (from != null) {
						email.setAuthor(from.getPersonal());
						email.setAuthorAddress(from.getAddress());
					}

					if (recipients != null)
						for (int j = 0; j < recipients.length; j++) {
							Address rec = recipients[j];
							Recipient recipient = new Recipient();
							recipient.setAddress(rec.toString());
							email.addRecipient(recipient);
						}

					email.setSubject(message.getSubject());
					email.setRead(0);
					email.setUserName(username);
					email.setFolder("inbox");
					if (message.getSentDate() != null)
						email.setSentDate(String.valueOf(message.getSentDate().getTime()));
					else
						email.setSentDate(String.valueOf(new Date().getTime()));
					email.setEmailId(mailId);
					email.setAccountId(account.getAccountId());
					getEmailDao().store(email);

					if (log.isDebugEnabled())
						log.debug("Store email " + email.getSubject());
					// Cleanup the mails directory
					File mailsdir = new File(settingsConfig.getValue("userdir") + "/mails/");
					if (mailsdir.exists())
						FileUtils.forceDelete(mailsdir);
					FileUtils.forceMkdir(mailsdir);
					dumpPart(message, 0, account, email, null);
					imported++;
				} catch (Throwable e) {
					errors++;
					log.error("Error on email " + email.getSubject(), e);
				} finally {
					next();
				}
			}
			inbox.close(true);
		}
		store.close();
	}

	private Document dumpPart(Part p, int partCount, EMailAccount account, EMail email, Menu parent)
			throws MessagingException, Exception {
		//TODO reimplement using the concept of document link for attachments
		
		
		String mailsdir = settingsConfig.getValue("userdir") + "/mails/";
		File mailDir = new File(FilenameUtils.normalize(mailsdir + "/" + email.getId()));
		FileUtils.forceMkdir(mailDir);

//		if (p.isMimeType("multipart/*")) {
//			Multipart mp = (Multipart) p.getContent();
//			int count = mp.getCount();
//
//			int partId = 0;
//			boolean textBodyFound = false;
//
//			Menu mailMenu = null;
//
//			// Search for text mail body
//			for (int i = 0; i < count; i++) {
//				Part part = mp.getBodyPart(i);
//				if (StringUtils.isEmpty(part.getFileName()) && part.getContentType().startsWith("text/plain")) {
//					mailMenu = dumpPart(mp.getBodyPart(i), partId++, account, email, null);
//					textBodyFound = true;
//				}
//			}
//
//			// Search for html mail body
//			for (int i = 0; i < count && !textBodyFound; i++) {
//				Part part = mp.getBodyPart(i);
//				if (StringUtils.isEmpty(part.getFileName()) && part.getContentType().startsWith("text/html")
//						&& !textBodyFound) {
//					// This is an HTML-only mail
//					mailMenu = dumpPart(mp.getBodyPart(i), partId++, account, email, null);
//				}
//			}
//
//			// Dump other parts skipping not-allowed extensions
//			for (int i = 0; i < count; i++) {
//				Part part = mp.getBodyPart(i);
//				if (!StringUtils.isEmpty(part.getFileName())
//						&& account.isAllowed(FilenameUtils.getExtension(part.getFileName()))) {
//					dumpPart(mp.getBodyPart(i), partId++, account, email, mailMenu);
//				}
//			}
//		} else {
//			Attachment attachment = new Attachment();
//			String cType = p.getContentType();
//			String filename = p.getFileName();
//			String docName = filename;
//
//			// Check if this is the email body or an attachment
//			if (StringUtils.isEmpty(filename)) {
//				filename = "email";
//
//				if (cType.startsWith("text/plain")) {
//					filename += ".mail";
//				}
//
//				if (cType.startsWith("text/html")) {
//					filename += ".html";
//				}
//				docName = StringUtils.abbreviate(email.getSubject(), 100);
//			}
//
//			int end = cType.indexOf(";");
//			String mimeType = "";
//
//			if (end != -1) {
//				mimeType = cType.substring(0, cType.indexOf(";"));
//			} else {
//				mimeType = cType;
//			}
//
//			InputStream is = p.getInputStream();
//			File file = new File(mailDir, filename);
//			FileOutputStream fos = new FileOutputStream(file);
//			int letter = 0;
//
//			while ((letter = is.read()) != -1) {
//				fos.write(letter);
//			}
//
//			is.close();
//			fos.close();
//
//			String icon = "";
//			if (mimeType.equals("text/plain") || mimeType.equals("text/rtf") || mimeType.equals("application/msword")
//					|| mimeType.equals("application/vnd.sun.xml.writer")) {
//				icon = "textdoc.gif";
//			} else if (mimeType.equals("application/msexcel") || mimeType.equals("application/vnd.sun.xml.calc")) {
//				icon = "tabledoc.gif";
//			} else if (mimeType.equals("application/mspowerpoint")
//					|| mimeType.equals("application/vnd.sun.xml.impress")) {
//				icon = "presentdoc.gif";
//			} else if (mimeType.equals("application/pdf")) {
//				icon = "pdf.gif";
//			} else if (mimeType.equals("text/html")) {
//				icon = "internet.gif";
//			} else {
//				icon += "document.gif";
//			}
//
//			attachment.setIcon(icon);
//			attachment.setMimeType(mimeType);
//			attachment.setFilename(filename);
//			email.addAttachment(partCount, attachment);
//
//			Menu parentMenu = parent;
//			if (parentMenu == null)
//				parentMenu = account.getTargetFolder();
//
//			return storeDocument(account, parentMenu, file, docName, email);
//		}
		return null;
	}

	/**
	 * Stores a document file in the archive
	 * 
	 * @param account
	 * @param file
	 *            File to be stored
	 * @param folder
	 *            The folder in which the document must be created, if null
	 *            account target folder is used
	 * @param docName
	 *            Name of the document to be created
	 * @param srcDate
	 * @return The newly created document
	 * @throws Exception
	 */
	private Document storeDocument(EMailAccount account, Menu folder, File file, String docName, EMail email)
			throws Exception {
		
		log.info("Store email document " + file);

		Menu parent = account.getTargetFolder();
		if (folder != null)
			parent = folder;

		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		Document doc = null;
		
		if (file.getName().startsWith("email")) {

			log.info("Insert email document.");
			
			String srcAuthor = email.getAuthorAddress();
			if (email.getAuthor() != null) {
				srcAuthor = email.getAuthor() + " " + email.getAuthorAddress();
			}
			
			int fieldLength = 255;
			if ((srcAuthor != null) && (srcAuthor.length() > 255))
				srcAuthor = srcAuthor.substring(0, fieldLength);

			Date srcDate = email.getSentDateAsDate();
			
            doc = manager.create(file, parent, defaultOwner, account.getLanguage(), docName, srcDate, account.getMailAddress(), srcAuthor, "", "", "", null);
		} else {
			doc = manager.create(file, parent, defaultOwner, account.getLanguage());
		}

		return doc;
	}

	/**
	 * Downloads all new mails from all accounts. The stored document will be
	 * owned by the specified default owner
	 * <p>
	 * 
	 * @see com.logicaldoc.core.task.Task#runTask()
	 */
	@Override
	protected void runTask() throws Exception {
		imported = 0;
		errors = 0;
		log.info("Start email download from all accounts");
		receiveMails(defaultOwner);
		log.info("End email download");
		log.info("Email imported: " + imported);
		log.info("Errors: " + errors);
	}

	@Override
	public boolean isIndeterminate() {
		return false;
	}
}