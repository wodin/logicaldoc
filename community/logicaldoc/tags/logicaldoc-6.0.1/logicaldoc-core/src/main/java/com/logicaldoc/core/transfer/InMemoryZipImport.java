package com.logicaldoc.core.transfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.text.analyzer.AnalyzerManager;
import com.logicaldoc.core.text.parser.Parser;
import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.TagUtil;

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

	protected static Log logger = LogFactory.getLog(InMemoryZipImport.class);

	public InMemoryZipImport() {
	}

	public void process(File zipsource, Locale locale, Folder parent, long userId, Long templateId, String sessionId) {
		// process the files in the zip using UTF-8 encoding for file names
		process(zipsource, locale, parent, userId, templateId, "UTF-8", sessionId);
	}

	@SuppressWarnings({ "rawtypes" })
	public void process(File zipsource, Locale locale, Folder parent, long userId, Long templateId, String encoding,
			String sessionId) {
		this.zipFile = zipsource;
		this.locale = locale;
		this.templateId = templateId;
		this.sessionId = sessionId;

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		this.user = userDao.findById(userId);

		logger.debug("Using encoding: " + encoding);

		try {
			ZipFile zip = new ZipFile(zipFile, encoding);
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

		History transaction = new History();
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

			Set<String> tagSet = null;
			if (extractTags) {
				AnalyzerManager analyzer = (AnalyzerManager) Context.getInstance().getBean(AnalyzerManager.class);

				// also extract tags and save on document
				Parser parser = ParserFactory.getParser(filename);
				parser.setEncoding(zip.getEncoding());
				// This reader will be automatically closed by method
				// parser.readText
				parser.parse(stream);
				String words = parser.getTags();
				if (StringUtils.isEmpty(words)) {
					try {
						String text = parser.getContent();
						words = analyzer.getTermsAsString(tagsNumber, text, locale);
					} catch (Exception e) {
						logger.error("Error in text extraction from document", e);
					}
				}
				tagSet = TagUtil.extractTags(words);
			} else if (StringUtils.isNotEmpty(tags)) {
				tagSet = TagUtil.extractTags(tags);
			}

			// create a document
			DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			try {
				// Reopen the stream (the parser has closed it)
				stream = zip.getInputStream(ze);

				transaction.setEvent(History.EVENT_STORED);
				transaction.setComment("");
				transaction.setUser(user);

				Document doc = new Document();
				doc.setFileName(filename);
				doc.setLocale(locale);
				doc.setFolder(documentPath);
				doc.setTitle(doctitle);
				doc.setTags(tagSet);
				doc.setTemplateId(templateId);

				docManager.create(stream, doc, transaction, immediateIndexing);
			} catch (Exception e) {
				logger.error("InMemoryZipImport addEntry failed", e);
			} finally {
				stream.close();
			}
		}
	}
}