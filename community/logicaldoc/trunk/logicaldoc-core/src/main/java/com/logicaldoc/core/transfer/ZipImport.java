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
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.text.analyzer.AnalyzerManager;
import com.logicaldoc.core.text.parser.Parser;
import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.TagUtil;
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

	protected Locale locale;

	protected Long templateId = null;

	protected User user;

	protected static Log logger = LogFactory.getLog(ZipImport.class);

	protected boolean extractTags = false;

	protected boolean immediateIndexing = false;

	protected int tagsNumber = 3;

	protected String tags;

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

	public void process(File zipsource, Locale locale, Menu parent, long userId, Long templateId) {
		this.locale = locale;
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

	public void process(String zipsource, Locale locale, Menu parent, long userId, Long templateId) {
		File srcfile = new File(zipsource);
		process(srcfile, locale, parent, userId, templateId);
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
			Set<String> tagSet = null;
			if (extractTags) {
				AnalyzerManager analyzer = (AnalyzerManager) Context.getInstance().getBean(AnalyzerManager.class);

				// also extract tags and save on document
				Parser parser = ParserFactory.getParser(file, locale);
				parser.parse(file, locale, null);
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
				docManager.create(file, file.getName(), parent, user, locale, "", null, "", "", "", "", "", tagSet,
						templateId, null, immediateIndexing);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
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
}