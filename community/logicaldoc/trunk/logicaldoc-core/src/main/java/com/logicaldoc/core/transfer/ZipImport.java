package com.logicaldoc.core.transfer;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.text.analyzer.AnalyzerManager;
import com.logicaldoc.core.text.parser.Parser;
import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;
import com.logicaldoc.util.io.ZipUtil;

/**
 * This is an import utilities that imports documents stored in a zip archive.
 * All folders in the zip will be replicated.
 * 
 * @author Sebastian Stein
 * @author Matteo Caruso - Logical Objects
 */
public class ZipImport {

	private String language = "";

	private Long templateId = null;

	private User user;

	protected static Log logger = LogFactory.getLog(ZipImport.class);

	private boolean extractKeywords = false;

	private boolean immediateIndexing = false;

	private int keywordsNumber = 3;

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

	/**
	 * The number of auto-extracted keywords (if extractKeywords=true)
	 */
	public int getKeywordsNumber() {
		return keywordsNumber;
	}

	public void setKeywordsNumber(int keywordsNumber) {
		this.keywordsNumber = keywordsNumber;
	}

	public boolean isExtractKeywords() {
		return extractKeywords;
	}

	public void setExtractKeywords(boolean extractKeywords) {
		this.extractKeywords = extractKeywords;
	}

	public void process(File zipsource, String language, Menu parent, long userId, Long templateId) {
		this.language = language;
		this.templateId = templateId;

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		this.user = userDao.findById(userId);

		SettingsConfig settings = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
		String userpath = settings.getValue("userdir");

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
		ZipUtil.unzip(zipsource.getPath(), userpath);
		File[] files = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			addEntry(files[i], parent);
		}

		try {
			FileUtils.deleteDirectory(dir);
		} catch (IOException e) {
		}
	}

	public void process(String zipsource, String language, Menu parent, long userId, Long templateId) {
		File srcfile = new File(zipsource);
		process(srcfile, language, parent, userId, templateId);
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
		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		String menuName = file.getName();
		if (file.isDirectory()) { // creates a logicaldoc folder
			Menu menu = dao.createFolder(parent, menuName);

			File[] files = file.listFiles();

			for (int i = 0; i < files.length; i++) {
				addEntry(files[i], menu);
			}
		} else {
			Set<String> keywords = null;
			if (extractKeywords) {
				AnalyzerManager analyzer = (AnalyzerManager) Context.getInstance().getBean(AnalyzerManager.class);

				// also extract keywords and save on document
				DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				Locale locale = new Locale(language);
				Parser parser = ParserFactory.getParser(file, locale);
				parser.parse(file);
				String words = parser.getKeywords();
				if (StringUtils.isEmpty(words)) {
					try {
						words = analyzer.getTermsAsString(keywordsNumber, parser.getContent(), language);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
				keywords = ddao.toKeywords(words);
			}

			// creates a document
			DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			try {
				docManager.create(file, parent, user, language, "", null, "", "", "", "", "", keywords, templateId, null,
						immediateIndexing);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}