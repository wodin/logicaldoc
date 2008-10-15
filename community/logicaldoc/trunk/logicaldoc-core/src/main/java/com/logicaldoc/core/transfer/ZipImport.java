package com.logicaldoc.core.transfer;

import java.io.File;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.FileBean;
import com.logicaldoc.core.ZipBean;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.text.AnalyzeText;
import com.logicaldoc.core.text.parser.Parser;
import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;

/**
 * Created on 16.12.2004
 * 
 * @author micha
 * @author Sebastian Stein
 */
public class ZipImport {

	private String username;

	private String language;

	protected static Log logger = LogFactory.getLog(ZipImport.class);

	private boolean extractKeywords = true;

	public ZipImport() {
		username = "";
		language = "";
		extractKeywords = true;
	}

	public boolean isExtractKeywords() {
		return extractKeywords;
	}

	public void setExtractKeywords(boolean extractKeywords) {
		this.extractKeywords = extractKeywords;
	}

	public void process(File zipsource, String language, Menu parent, String user) {

		this.username = user;
		this.language = language;

		SettingsConfig settings = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
		String userpath = settings.getValue("userdir");

		if (!userpath.endsWith(File.pathSeparator)) {
			userpath += File.pathSeparator;
		}

		userpath += username + File.pathSeparator + "unzip";

		if (FileBean.exists(userpath)) {
			FileBean.deleteDir(userpath);
		}

		FileBean.createDir(userpath);
		ZipBean.unzip(zipsource.getPath(), userpath);

		File file = new File(userpath);
		File[] files = file.listFiles();

		for (int i = 0; i < files.length; i++) {
			addEntry(files[i], parent);
		}

		FileBean.deleteDir(userpath);
	}

	public void process(String zipsource, String language, Menu parent, String user) {
		File srcfile = new File(zipsource);
		process(srcfile, language, parent, user);
	}

	/**
	 * Stores a file in the repository of logicaldoc and inserts some
	 * information in the database of logicaldoc (menu, document, version,
	 * history, searchdocument).
	 * 
	 * @param file
	 * @param parent
	 * @param language Two characters language of the file to add
	 */
	protected void addEntry(File file, Menu parent) {
		try {
			MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			String menuName = file.getName();
			if (file.isDirectory()) { // creates a logicaldoc folder
				Menu menu = dao.createFolder(parent, menuName);

				File[] files = file.listFiles();

				for (int i = 0; i < files.length; i++) {
					addEntry(files[i], menu);
				}
			} else {
				// creates a document
				DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
				Document document = docManager.create(file, parent, username, language);

				if (extractKeywords) {
					// also extract keywords and save on document
					DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
					Locale locale = new Locale(language);
					Parser parser = ParserFactory.getParser(file, locale);
					parser.parse(file);
					String words = parser.getKeywords();
					if (StringUtils.isEmpty(words)) {
						AnalyzeText analyzer = new AnalyzeText();
						words = analyzer.getTerms(5, parser.getContent(), document.getLanguage());
					}
					Set<String> keywords = ddao.toKeywords(words);
					document.setKeywords(keywords);
					ddao.store(document);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}