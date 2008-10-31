package com.logicaldoc.web.document;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.context.effects.JavascriptContext;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.text.analyzer.AnalyzerManager;
import com.logicaldoc.core.text.parser.Parser;
import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.upload.InputFileBean;

/**
 * Wizard that handled the creation of a new document.
 * 
 * @author Marco Meschieri - Logical Objects
 * @version $Id: NewDocWizard.java,v 1.9 2006/09/04 15:48:35 marco Exp $
 * @since 3.0
 */
public class NewDocWizard {
	protected static Log log = LogFactory.getLog(NewDocWizard.class);

	private boolean showUpload = true;

	private DocumentNavigation documentNavigation;

	public boolean isShowUpload() {
		return showUpload;
	}

	/**
	 * Starts the upload process
	 */
	@SuppressWarnings("deprecation")
	public String start() {
		documentNavigation.setSelectedPanel(new PageContentBean("uploadDocument"));

		// Remove the uploaded file, if one was uploaded
		Application application = FacesContext.getCurrentInstance().getApplication();
		InputFileBean inputFile = ((InputFileBean) application.createValueBinding("#{inputFile}").getValue(
				FacesContext.getCurrentInstance()));
		inputFile.deleteUploadDir();
		inputFile.reset();
		showUpload = true;
		return null;
	}

	/**
	 * Acquires the uploaded file and shows the edit form. Gets the file
	 * uploaded through the HTML form and extracts all necessary data like
	 * language, keywords, autor, etc. to fill the document form so that the
	 * user can still edit this data before finally storing the document in
	 * logicaldoc.
	 */
	public String next() {
		showUpload = false;

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application application = FacesContext.getCurrentInstance().getApplication();
		InputFileBean inputFile = ((InputFileBean) application.createValueBinding("#{inputFile}").getValue(
				FacesContext.getCurrentInstance()));
		DocumentEditForm docForm = ((DocumentEditForm) application.createValueBinding("#{documentForm}").getValue(
				FacesContext.getCurrentInstance()));
		docForm.reset();

		if (SessionManagement.isValid()) {
			try {
				File file = inputFile.getFile();
				String documentLanguage = inputFile.getLanguage();

				// Get folder that called AddDocAction
				Menu folder = documentNavigation.getSelectedDir().getMenu();

				// Makes path
				String path = new StringBuilder(folder.getPath()).append("/").append(folder.getId()).toString();

				// Makes new Menu
				Menu menu = new Menu();
				menu.setParentId(folder.getId());
				menu.setPath(path);

				// Gets file to upload name
				String filename = file.getName();

				// Stores the document in the repository
				SettingsConfig settings = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);

				// Parses the file where it is already stored
				Locale locale = new Locale(inputFile.getLanguage());
				Parser parser = ParserFactory.getParser(file, locale);
				String content = null;
				String name = "";
				String author = "";
				String keywords = "";

				// and gets some fields
				if (parser != null) {
					content = parser.getContent();
					name = parser.getTitle();
					author = parser.getAuthor();
					if (inputFile.isExtractKeywords())
						keywords = parser.getKeywords();
				}

				if (content == null) {
					content = "";
				}

				// source field got from web.xml.
				// This field is required for Lucene to work properly
				String source = (String) facesContext.getExternalContext().getApplicationMap().get("store");

				if (source == null) {
					source = settings.getValue("defaultSource");
				}

				// fills needed fields
				if ((name != null) && (name.length() > 0)) {
					docForm.setTitle(name);
				} else {
					int tmpInt = filename.lastIndexOf(".");

					if (tmpInt != -1) {
						docForm.setTitle(filename.substring(0, tmpInt));
					} else {
						docForm.setTitle(filename);
					}
				}
				docForm.setSource(source);

				if (author != null) {
					docForm.setSourceAuthor(author);
				}

				docForm.setSourceDate(new Date());
				docForm.setLanguage(documentLanguage);

				if (inputFile.isExtractKeywords()) {
					if ((keywords != null) && !keywords.trim().equals("")) {
						docForm.setKeywords(keywords);
					} else {
						AnalyzerManager analyzer = (AnalyzerManager) Context.getInstance().getBean(AnalyzerManager.class);
						docForm.setKeywords(analyzer.getTerms(5, content.toString(), documentLanguage));
					}
				}

				docForm.setFilename(filename);
			} catch (Exception e) {
				String message = Messages.getMessage("errors.action.savedoc");
				log.error(message, e);
				Messages.addMessage(FacesMessage.SEVERITY_ERROR, message, message);
				showUpload = true;
			} finally {

			}

			// sometimes IE7 freezes the page and in these cases a refresh
			// solves the problem
			JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "window.location.reload(false);");

			return null;
		} else {
			return "login";
		}
	}

	public String abort() {
		documentNavigation.setSelectedPanel(new PageContentBean("documents"));

		// Remove the uploaded file, if one was uploaded
		Application application = FacesContext.getCurrentInstance().getApplication();
		InputFileBean inputFile = ((InputFileBean) application.createValueBinding("#{inputFile}").getValue(
				FacesContext.getCurrentInstance()));
		inputFile.deleteUploadDir();
		return null;
	}

	public String save() {
		Application application = FacesContext.getCurrentInstance().getApplication();
		DocumentEditForm documentForm = ((DocumentEditForm) application.createValueBinding("#{documentForm}").getValue(
				FacesContext.getCurrentInstance()));

		documentForm.save();
		documentNavigation.selectDirectory(documentNavigation.getSelectedDir());
		return abort();
	}

	public void setDocumentNavigation(DocumentNavigation documentNavigation) {
		this.documentNavigation = documentNavigation;
	}
}