package com.logicaldoc.core.transfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.commons.io.FilenameUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentEvent;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderHistory;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;

/**
 * This is an import utilities that imports documents stored in a zip archive.
 * The entire import process is followed in memory, to replicate correctly the
 * names of directories and documents when they contain native characters. All
 * folders in the zip will be replicated. Also, if required the parsing of
 * documents is executed for the extraction of the tags of the documents.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.5.2
 */
public class InMemoryZipImport extends ZipImport {

	protected static Logger logger = LoggerFactory.getLogger(InMemoryZipImport.class);

	public InMemoryZipImport(Document docVo) {
		super(docVo);
	}

	public void process(File zipsource, Folder parent, long userId, String sessionId) {
		// process the files in the zip using UTF-8 encoding for file names
		process(zipsource, parent, userId, "UTF-8", sessionId);
	}

	@SuppressWarnings({ "rawtypes" })
	public void process(File zipsource, Folder parent, long userId, String encoding, String sessionId) {
		this.zipFile = zipsource;
		this.sessionId = sessionId;

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		this.user = userDao.findById(userId);

		logger.debug("Using encoding: " + encoding);
		ZipFile zip = null;
		try {
			zip = new ZipFile(zipFile, encoding);
			Enumeration zipEntries = zip.getEntries();
			ZipEntry zipe = null;
			while (zipEntries.hasMoreElements()) {
				zipe = (ZipEntry) zipEntries.nextElement();
				try {
					// Avoid import of unnecessary folders or file
					if (zipe.isDirectory() && zipe.getName().startsWith("__MAC")) {
						continue;
					} else if (!zipe.isDirectory() && (zipe.getName().startsWith(".") || zipe.getName().contains("/."))) {
						continue;
					}
					addEntry(zip, zipe, parent);
				} catch (IOException e) {
					logger.warn("InMemoryZipImport unable to import ZIP entry", e);
				}
			}
		} catch (IOException e) {
			logger.error("InMemoryZipImport process failed", e);
		} finally {
			if (zip != null)
				try {
					zip.close();
				} catch (IOException e) {
					logger.error("InMemoryZipImport error closing zip file", e);
				}
		}

		if (isNotifyUser())
			sendNotificationMessage();
	}

	/**
	 * Stores a file in the repository of LogicalDOC and inserts some
	 * information in the database of LogicalDOC (folder, document, filename,
	 * title, tags, templateid, created user, locale)
	 * 
	 * @param zis
	 * @param file
	 * @param ze
	 * @throws IOException
	 * @throws ZipException
	 */
	protected void addEntry(ZipFile zip, ZipEntry ze, Folder parent) throws ZipException, IOException {
		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

		FolderHistory transaction = new FolderHistory();
		transaction.setSessionId(sessionId);
		transaction.setUser(user);

		if (ze.isDirectory()) {
			// creates a logicaldoc folder
			String folderPath = FilenameUtils.getFullPathNoEndSeparator(ze.getName());
			dao.createPath(parent, folderPath, transaction);
		} else {
			InputStream stream = zip.getInputStream(ze);
			File docFile = new File(ze.getName());
			String filename = docFile.getName();
			String doctitle = FilenameUtils.getBaseName(filename);

			// ensure to have the proper folder to upload the file into
			String folderPath = FilenameUtils.getFullPathNoEndSeparator(ze.getName());
			Folder documentPath = dao.createPath(parent, folderPath, transaction);

			// create a document
			DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			try {
				Document doc = (Document) docVo.clone();
				doc.setTitle(doctitle);
				doc.setFileName(filename);
				doc.setFolder(documentPath);

				// Reopen the stream (the parser has closed it)
				stream = zip.getInputStream(ze);

	
				History history = new History();
				history.setEvent(DocumentEvent.STORED.toString());
				history.setComment("");
				history.setUser(user);
				transaction.setSessionId(sessionId);
				docManager.create(stream, doc, history);
			} catch (Exception e) {
				logger.error("InMemoryZipImport addEntry failed", e);
			} finally {
				stream.close();
			}
		}
	}
}