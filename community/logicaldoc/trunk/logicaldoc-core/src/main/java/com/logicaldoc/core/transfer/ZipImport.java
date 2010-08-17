package com.logicaldoc.core.transfer;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.dao.SystemMessageDAO;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.FolderDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.text.analyzer.AnalyzerManager;
import com.logicaldoc.core.text.parser.Parser;
import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.TagUtil;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.io.ZipUtil;

/**
 * This is an import utilities that imports documents stored in a zip archive.
 * All folders in the zip will be replicated.
 * 
 * @author Sebastian Stein
 * @author Matteo Caruso - Logical Objects
 */
public class ZipImport {

	protected Locale locale;

	protected Long templateId = null;

	protected User user;

	protected static Log logger = LogFactory.getLog(ZipImport.class);

	protected boolean extractTags = false;

	protected boolean immediateIndexing = false;

	protected int tagsNumber = 3;

	protected String tags;

	protected File zipFile;

	private boolean notifyUser = true;

	protected String sessionId = null;

	public ZipImport() {
	}

	public boolean isImmediateIndexing() {
		return immediateIndexing;
	}

	/**
	 * This flag controls if newly imported documents must be immediately
	 * indexed
	 */
	public void setImmediateIndexing(boolean immediateIndexing) {
		this.immediateIndexing = immediateIndexing;
	}

	public void process(File zipsource, Locale locale, Menu parent, long userId, Long templateId, String sessionId) {
		this.zipFile = zipsource;
		this.locale = locale;
		this.templateId = templateId;
		this.sessionId = sessionId;

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		this.user = userDao.findById(userId);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		String userpath = conf.getPropertyWithSubstitutions("conf.userdir");

		if (!userpath.endsWith("_")) {
			userpath += "_";
		}

		userpath += userId + "_" + "unzip";
		File dir = new File(userpath);
		if (dir.exists()) {
			try {
				FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
			}
		}

		try {
			FileUtils.forceMkdir(dir);
		} catch (IOException e) {
		}
		ZipUtil.unzip(zipFile.getPath(), userpath);
		File[] files = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			addEntry(files[i], parent);
		}

		try {
			FileUtils.deleteDirectory(dir);
		} catch (IOException e) {
		}

		if (notifyUser)
			sendNotificationMessage();
	}

	public void process(String zipsource, Locale locale, Menu parent, long userId, Long templateId, String sessionId) {
		File srcfile = new File(zipsource);
		process(srcfile, locale, parent, userId, templateId, sessionId);
	}

	/**
	 * Stores a file in the repository of logicaldoc and inserts some
	 * information in the database of logicaldoc (menu, document, version,
	 * history, searchdocument).
	 * 
	 * @param file
	 * @param parent
	 */
	protected void addEntry(File file, Menu parent) {
		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		String menuName = file.getName();
		History transaction = new History();
		transaction.setUser(user);
		transaction.setSessionId(sessionId);

		if (file.isDirectory()) {
			// creates a logicaldoc folder

			Menu menu = dao.create(parent, menuName, transaction);

			File[] files = file.listFiles();

			for (int i = 0; i < files.length; i++) {
				addEntry(files[i], menu);
			}
		} else {
			Set<String> tagSet = null;
			if (extractTags) {
				AnalyzerManager analyzer = (AnalyzerManager) Context.getInstance().getBean(AnalyzerManager.class);

				// also extract tags and save on document
				Parser parser = ParserFactory.getParser(file, file.getName(), locale, null);
				parser.parse(file);
				String words = parser.getTags();
				if (StringUtils.isEmpty(words)) {
					try {
						words = analyzer.getTermsAsString(tagsNumber, parser.getContent(), locale);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
				tagSet = TagUtil.extractTags(words);
			} else if (StringUtils.isNotEmpty(tags)) {
				tagSet = TagUtil.extractTags(tags);
			}

			// creates a document
			DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			try {
				transaction.setEvent(History.EVENT_STORED);
				transaction.setComment("");
				transaction.setUser(user);

				Document doc = new Document();
				doc.setFileName(file.getName());
				doc.setLocale(locale);
				doc.setFolder(parent);
				doc.setTags(tagSet);
				doc.setTemplateId(templateId);
				docManager.create(file, doc, transaction, immediateIndexing);
			} catch (Exception e) {
				logger.error("InMemoryZipImport addEntry failed", e);
			}
		}
	}

	/**
	 * Sends a system message to the user that imported the zip.
	 * 
	 * @param archive
	 */
	protected void sendNotificationMessage() {
		SystemMessageDAO smdao = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);
		Date now = new Date();
		Recipient recipient = new Recipient();
		recipient.setName(user.getUserName());
		recipient.setAddress(user.getUserName());
		recipient.setType(Recipient.TYPE_SYSTEM);
		recipient.setMode("");
		Set<Recipient> recipients = new HashSet<Recipient>();
		recipients.add(recipient);
		SystemMessage sysmess = new SystemMessage();
		sysmess.setAuthor("SYSTEM");
		sysmess.setRecipients(recipients);
		ResourceBundle bundle = ResourceBundle.getBundle("i18n/application", user.getLocale());
		sysmess.setSubject(bundle.getString("zip.import.subject"));
		String message = bundle.getString("zip.import.body");
		String body = MessageFormat.format(message, new String[] { zipFile.getName() });
		sysmess.setMessageText(body);
		sysmess.setSentDate(now);
		sysmess.setRead(0);
		sysmess.setConfirmation(0);
		sysmess.setPrio(0);
		sysmess.setDateScope(1);
		smdao.store(sysmess);
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public boolean isExtractTags() {
		return extractTags;
	}

	public void setExtractTags(boolean extractTags) {
		this.extractTags = extractTags;
	}

	/**
	 * The number of auto-extracted tags
	 */
	public int getTagsNumber() {
		return tagsNumber;
	}

	public void setTagsNumber(int tagsNumber) {
		this.tagsNumber = tagsNumber;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public boolean isNotifyUser() {
		return notifyUser;
	}

	public void setNotifyUser(boolean notifyUser) {
		this.notifyUser = notifyUser;
	}
}