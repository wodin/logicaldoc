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

import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.text.analyzer.AnalyzerManager;
import com.logicaldoc.core.text.parser.Parser;
import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.TagUtil;

/**
 * This is an import utilities that imports documents stored in a zip archive.
 * All folders in the zip will be replicated.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.5.2
 */
public class InMemoryZipImport extends ZipImport {

	protected static Log logger = LogFactory.getLog(InMemoryZipImport.class);

	public InMemoryZipImport() {
	}
	
	public void process(File zipsource, Locale locale, Menu parent, long userId, Long templateId) {
		// process the files in the zip using UTF-8 encoding for file names
		process(zipsource, locale, parent, userId, templateId, "UTF-8");
	}

	public void process(File zipsource, Locale locale, Menu parent, long userId, Long templateId, String encoding) {
		
		this.locale = locale;
		this.templateId = templateId;
		
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		this.user = userDao.findById(userId);

		System.out.println("Using encoding: " + encoding);
		
		try {
			ZipFile zip = new ZipFile(zipsource, encoding);
			Enumeration zipEntries = zip.getEntries();
			ZipEntry zipe = null;
			while (zipEntries.hasMoreElements()) {
				zipe = (ZipEntry) zipEntries.nextElement();
				addEntry(zip, zipe, parent);
			}
		} catch (IOException e) {
			logger.error("InMemoryZipImport process failed", e);
		}
	}


	/**
	 * Stores a file in the repository of logicaldoc and inserts some
	 * information in the database of logicaldoc (menu, document, version,
	 * history, searchdocument).
	 * 
	 * @param zis
	 * 
	 * @param file
	 * @param parent
	 * @param encoding 
	 * @throws IOException 
	 * @throws ZipException 
	 */
	protected void addEntry(ZipFile zip, ZipEntry ze, Menu parent) throws ZipException, IOException {
		
		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		System.out.println("ze.getName(): " + ze);

		if (ze.isDirectory()) { 
			// creates a logicaldoc folder
			String folderPath = FilenameUtils.getFullPathNoEndSeparator(ze.getName());
            dao.createFolders(parent, folderPath);
		} else {
			InputStream stream = zip.getInputStream(ze);
			File docFile = new File(ze.getName());
			String filename = docFile.getName();
			String doctitle = FilenameUtils.getBaseName(filename);
			
			// ensure to have the proper folder to upload the file into
			String folderPath = FilenameUtils.getFullPathNoEndSeparator(ze.getName());
            Menu documentPath = dao.createFolders(parent, folderPath);

			Set<String> tagSet = null;
			if (extractTags) {
				AnalyzerManager analyzer = (AnalyzerManager) Context.getInstance().getBean(AnalyzerManager.class);

				// also extract tags and save on document
				Parser parser = ParserFactory.getParser(filename, null);
				parser.extractText(stream, null, null);

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
				// 	public Document create(InputStream content, String filename, Menu folder, User user, Locale locale, String title,
				// 			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
				// 			String versionDesc, Set<String> tags, Long templateId, Map<String, String> extendedAttributes, boolean immediateIndexing) throws Exception {
				System.out.println("start creation");
				
				// Reopen the stream (the parser has closed it)
				stream = zip.getInputStream(ze);
				docManager.create(stream, filename, documentPath, user, locale, doctitle, null, "", "", "", "", "", tagSet, templateId, null, immediateIndexing);
			} catch (Exception e) {
				logger.error("InMemoryZipImport addEntry failed", e);
			} finally {
				stream.close();
			}
		}
	}

}