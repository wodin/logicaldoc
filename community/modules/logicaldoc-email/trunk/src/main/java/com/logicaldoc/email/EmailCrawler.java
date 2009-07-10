package com.logicaldoc.email;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.filters.StringInputStream;

import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentLinkDAO;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.core.searchengine.store.Storer;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.email.dao.EmailAccountDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;

/**
 * This component downloads new e-mails from one or more e-mail accounts
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 */
public class EmailCrawler extends Task {
	public static final String NAME = "EmailCrawler";

	// The default username that owns downloaded documents
	private String defaultOwner = "_email";

	private EmailAccountDAO accountDao;

	private DocumentTemplateDAO templateDao;

	private MenuDAO menuDao;

	private SettingsConfig settingsConfig;

	private Storer storer;

	private DocumentDAO documentDao;

	private DocumentLinkDAO documentLinkDao;

	private HistoryDAO historyDao;

	private UserDAO userDao;

	private EmailCacheManager cacheManager;

	private Indexer indexer;

	private int imported = 0;

	private int errors = 0;

	private EmailCrawler() {
		super(NAME);
		log = LogFactory.getLog(EmailCrawler.class);
	}

	public Indexer getIndexer() {
		return indexer;
	}

	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
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

	public DocumentLinkDAO getDocumentLinkDao() {
		return documentLinkDao;
	}

	public void setDocumentLinkDao(DocumentLinkDAO documentLinkDao) {
		this.documentLinkDao = documentLinkDao;
	}

	public EmailCacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(EmailCacheManager cacheManager) {
		this.cacheManager = cacheManager;
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

	public EmailAccountDAO getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(EmailAccountDAO accountDao) {
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

	public DocumentTemplateDAO getTemplateDao() {
		return templateDao;
	}

	public void setTemplateDao(DocumentTemplateDAO templateDao) {
		this.templateDao = templateDao;
	}

	/**
	 * Downloads all new mails from all accounts. The stored document will be
	 * owned by the specified username
	 * 
	 * @throws Exception
	 */
	public void receiveMails(String username) {
		Collection<EmailAccount> accounts = accountDao.findAll();
		size = 0;

		// First of all determine the task length
		for (EmailAccount account : accounts) {
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

		for (EmailAccount account : accounts) {
			if (account.getEnabled() == 0) {
				log.warn("Skip account " + account.getMailAddress() + " because disabled");
				continue;
			}
			try {
				log.info("Connect to " + account.getMailAddress());
				receive(account, defaultOwner);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Count all mails from the specified account
	 */
	private int countMails(EmailAccount account) throws MessagingException {
		int count = 0;
		Store store = null;
		Folder inbox = null;
		try {
			Session session = Session.getInstance(new Properties());
			store = session.getStore(account.getProvider());
			store.connect(account.getHost(), account.getPort(), account.getUserName(), account.getPassword());
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

	public void receive(EmailAccount account, String username) throws MessagingException, IOException {
		// Get a session. Use a blank Properties object.
		Session session = Session.getInstance(new Properties());
		Store store = session.getStore(account.getProvider());
		store.connect(account.getHost(), account.getUserName(), account.getPassword());

		EmailCache cache = cacheManager.getCache(account);
		cache.read();
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
				javax.mail.Message message = inbox.getMessage(i);
				try {
					message.setFlag(Flags.Flag.DELETED, account.getDeleteFromMailbox() > 0);
					String mailId;
					if (message.getHeader("Message-ID") != null)
						mailId = message.getHeader("Message-ID")[0];
					else {
						mailId = (message.getFrom()[0] != null ? message.getFrom()[0] : "")
								+ "."
								+ (message.getSentDate() != null ? message.getSentDate().getTime() : new Date()
										.getTime());
					}
					log.debug("Message with mailId: " + mailId);

					if (cache.containsKey(mailId)) {
						if (log.isDebugEnabled())
							log.debug("Skip message " + mailId + " because already fetched from " + account.toString());
						continue;
					} else {
						cache.put(mailId, new Date());
						cache.append();
					}

					dumpMessage(message, account, username, mailId);
					imported++;
				} catch (Throwable e) {
					errors++;
					log.error("Error on email " + message.getSubject(), e);
				} finally {
					next();
				}
			}
			inbox.close(true);
		}
		store.close();
	}

	private void dumpMessage(Message message, EmailAccount account, String username, String mailId) throws Exception {
		EMail email = new EMail();

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
			email.setSentDate(message.getSentDate());
		else
			email.setSentDate(new Date());
		email.setEmailId(mailId);
		email.setAccountId(account.getId());

		if (log.isDebugEnabled())
			log.debug("Store email " + email.getSubject());
		// Cleanup the mails directory
		File mailsdir = new File(settingsConfig.getValue("userdir") + "/mails/");
		if (mailsdir.exists())
			FileUtils.forceDelete(mailsdir);
		FileUtils.forceMkdir(mailsdir);
		File mailDir = new File(FilenameUtils.normalize(mailsdir + "/" + email.getId()));
		FileUtils.forceMkdir(mailDir);

		Object content = message.getContent();
		if (content instanceof java.lang.String) {
			File docFile = new File(mailDir, "email.txt");
			saveToFile(new StringInputStream((String) content), docFile);
			storeDocument(account, docFile, email);
		} else if (content instanceof Multipart) {
			Multipart mp = (Multipart) content;
			Collection<Document> attachments = new ArrayList<Document>();
			String emailBody = "";

			for (int j = 0; j < mp.getCount(); j++) {
				Part part = mp.getBodyPart(j);

				// Check if plain
				MimeBodyPart mbp = (MimeBodyPart) part;
				if (mbp.isMimeType("text/plain") || mbp.isMimeType("text/html")) {
					log.debug("Mime type is plain");
					if (mbp.getFileName() != null) {
						if (account.isAllowed(FilenameUtils.getExtension(mbp.getFileName()))) {
							File docFile = new File(mailDir, mbp.getFileName());
							saveToFile(part.getInputStream(), docFile);
							attachments.add(storeDocument(account, docFile, email));
						}
					} else
						emailBody += (String) mbp.getContent();
				} else {
					log.debug("Mime type is not plain");
					if (!StringUtils.isEmpty(part.getFileName())
							&& account.isAllowed(FilenameUtils.getExtension(part.getFileName()))) {

						// Special non-attachment cases here of
						// image/gif, text/html, ...
						File docFile = new File(mailDir, mbp.getFileName());
						saveToFile(part.getInputStream(), docFile);
						attachments.add(storeDocument(account, docFile, email));
					}
				}
			}
			File docFile = new File(mailDir, "email.txt");
			saveToFile(new StringInputStream((String) emailBody), docFile);
			Document bodyDoc = storeDocument(account, docFile, email);

			// Create body-attachment links
			for (Document attachment : attachments) {
				if (bodyDoc != null) {
					DocumentLink link = new DocumentLink();
					link.setDocument1(bodyDoc);
					link.setDocument2(attachment);
					link.setType("attachment");
					documentLinkDao.store(link);
				}
			}
		}

	}

	private void saveToFile(InputStream is, File docFile) throws FileNotFoundException, IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(docFile);
			int letter = 0;
			while ((letter = is.read()) != -1) {
				fos.write(letter);
			}
		} finally {
			if (is != null)
				is.close();
			if (fos != null)
				fos.close();
		}
	}

	/**
	 * Stores a document file in the archive
	 * 
	 * @param account
	 * @param file File to be stored
	 * @param email The email with the file to be stored
	 * @return The newly created document
	 * @throws Exception
	 */
	private Document storeDocument(EmailAccount account, File file, EMail email) throws Exception {
		User user = userDao.findByUserName(defaultOwner);

		DocumentTemplate template = templateDao.findByName("email");

		log.info("Store email document " + file);

		Menu folder = account.getTargetFolder();

		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		Document doc = null;
		String to = "";

		if (file.getName().equals("email.txt")) {
			log.info("Insert email document.");
			String srcAuthor = email.getAuthorAddress();
			if (email.getAuthor() != null) {
				srcAuthor = email.getAuthor() + " " + email.getAuthorAddress();
			}

			int fieldLength = 255;
			if ((srcAuthor != null) && (srcAuthor.length() > 255))
				srcAuthor = srcAuthor.substring(0, fieldLength);

			Date srcDate = email.getSentDate();

			Map<String, String> attributes = null;
			if (template != null && StringUtils.isEmpty(to)) {
				attributes = new HashMap<String, String>();
				attributes.put("from", StringUtils.substring(email.getAuthorAddress(), 0, 3999));
				for (int i = 0; i < email.getAddresses().length; i++) {
					if (email.getAddresses()[i] != null)
						attributes.put("to",
								(StringUtils.isNotEmpty(attributes.get("to")) ? (attributes.get("to") + ",") : "")
										+ email.getAddresses()[i].getAddress());
				}
				attributes.put("to", StringUtils.substring(attributes.get("to"), 0, 3999));
				attributes.put("subject", StringUtils.substring(email.getSubject(), 0, 3999));
			}
			doc = manager.create(file, file.getName(), folder, user, account.getLocale(), email.getSubject(), srcDate,
					account.getMailAddress(), srcAuthor, "", "", "", null, template != null ? template.getId() : null,
					attributes, false);
		} else {
			doc = manager.create(file, file.getName(), folder, user, account.getLocale(), false);
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
		setProgress(getSize());
		log.info("End email download");
		log.info("Email imported: " + imported);
		log.info("Errors: " + errors);
	}

	@Override
	public boolean isIndeterminate() {
		return false;
	}
}