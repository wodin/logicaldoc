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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.dao.SystemMessageDAO;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderHistory;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.util.UserUtil;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.io.ZipUtil;

/**
 * This is an import utilities that imports documents stored in a zip archive.
 * All folders in the zip will be replicated.
 * 
 * @author Sebastian Stein
 * @author Matteo Caruso - Logical Objects
 */
public class ZipImport {

	protected User user;

	protected static Log logger = LogFactory.getLog(ZipImport.class);

	protected File zipFile;

	private boolean notifyUser = true;

	protected String sessionId = null;

	protected Document docVo = null;

	/**
	 * Constructor.
	 * 
	 * @param docVo Value object for the common documents metadata.
	 */
	public ZipImport(Document docVo) {
		this.docVo = docVo;
	}

	public void process(File zipsource, Folder parent, long userId, String sessionId) {
		this.zipFile = zipsource;
		this.sessionId = sessionId;

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		this.user = userDao.findById(userId);

		File dir = UserUtil.getUserResource(userId, "unzip");
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
		ZipUtil.unzip(zipFile.getPath(), dir.getPath());
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

	public void process(String zipsource, Locale locale, Folder parent, long userId, Long templateId, String sessionId) {
		File srcfile = new File(zipsource);
		process(srcfile, parent, userId, sessionId);
	}

	/**
	 * Stores a file in the repository of logicaldoc and inserts some
	 * information in the database of logicaldoc (folder, document, version,
	 * history, searchdocument).
	 * 
	 * @param file
	 * @param parent
	 */
	protected void addEntry(File file, Folder parent) {
		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		String folderName = file.getName();
		FolderHistory transaction = new FolderHistory();
		transaction.setUser(user);
		transaction.setSessionId(sessionId);

		if (file.isDirectory()) {
			// creates a logicaldoc folder
			Folder folder = dao.create(parent, folderName, transaction);

			File[] files = file.listFiles();

			for (int i = 0; i < files.length; i++) {
				addEntry(files[i], folder);
			}
		} else {
			// creates a document
			DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			try {
				History history = new History();
				history.setEvent(History.EVENT_STORED);
				history.setComment("");
				history.setUser(user);
				history.setSessionId(sessionId);

				Document doc = (Document) docVo.clone();
				doc.setFolder(parent);

				docManager.create(file, doc, history);
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
		recipient.setMode("message");
		Set<Recipient> recipients = new HashSet<Recipient>();
		recipients.add(recipient);
		SystemMessage sysmess = new SystemMessage();
		sysmess.setAuthor("SYSTEM");
		sysmess.setRecipients(recipients);
		ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", user.getLocale());
		sysmess.setSubject(bundle.getString("zip.import.subject"));
		String message = bundle.getString("zip.import.body");
		String body = MessageFormat.format(message, new Object[] { zipFile.getName() });
		sysmess.setMessageText(body);
		sysmess.setSentDate(now);
		sysmess.setRead(0);
		sysmess.setConfirmation(0);
		sysmess.setPrio(0);
		sysmess.setDateScope(1);
		smdao.store(sysmess);
	}

	public boolean isNotifyUser() {
		return notifyUser;
	}

	public void setNotifyUser(boolean notifyUser) {
		this.notifyUser = notifyUser;
	}
}