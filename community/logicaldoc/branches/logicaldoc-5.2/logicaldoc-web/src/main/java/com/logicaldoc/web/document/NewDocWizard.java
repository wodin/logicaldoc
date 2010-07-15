package com.logicaldoc.web.document;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.text.analyzer.AnalyzerManager;
import com.logicaldoc.core.text.parser.Parser;
import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.upload.InputFileBean;

/**
 * Wizard that handled the creation of a new document.
 * 
 * @author Marco Meschieri - Logical Objects
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
	 * language, tags, autor, etc. to fill the document form so that the user
	 * can still edit this data before finally storing the document in
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

				if (file == null)
					return null;

				String documentLanguage = inputFile.getLanguage();

				// Get folder that called AddDocAction
				Menu folder = documentNavigation.getSelectedDir().getMenu();

				// Makes new Menu
				Menu menu = new Menu();
				menu.setParentId(folder.getId());

				// Gets file to upload name
				String filename = inputFile.getFileName();
				if ("".equals(filename.trim())) {
					filename = inputFile.getFile().getName();
				}

				String title = "";
				String author = "";
				String tags = "";

				// source field got from web.xml.
				// This field is required for Lucene to work properly
				String source = (String) facesContext.getExternalContext().getApplicationMap().get("store");

				// fills needed fields
				int tmpInt = filename.lastIndexOf(".");
				if (tmpInt != -1) {
					title = filename.substring(0, tmpInt);
				} else {
					title = filename;
				}

				docForm.setTitle(title);
				docForm.setSource(source);

				if (author != null) {
					docForm.setSourceAuthor(author);
				}

				docForm.setSourceDate(new Date());
				docForm.setLanguage(documentLanguage);

				if (inputFile.isExtractTags()) {
					// Parses the file where it is already stored
					Locale locale = new Locale(inputFile.getLanguage());
					Parser parser = ParserFactory.getParser(file, inputFile.getFileName(), locale, null);
					String content = parser.getContent();
					tags = parser.getTags();
					if (StringUtils.isNotEmpty(tags)) {
						docForm.setTags(tags);
					} else {
						AnalyzerManager analyzer = (AnalyzerManager) Context.getInstance().getBean(
								AnalyzerManager.class);
						docForm.setTags(analyzer.getTermsAsString(3, content.toString(), LocaleUtil
								.toLocale(documentLanguage)));
					}
				}
				docForm.setImmediateIndexing(inputFile.isImmediateIndexing());
				docForm.setFilename(filename);
			} catch (Exception e) {
				String message = Messages.getMessage("errors.action.savedoc");
				log.error(message, e);
				Messages.addMessage(FacesMessage.SEVERITY_ERROR, message, message);
				showUpload = true;
			} finally {

			}
			return null;
		} else {
			return "login";
		}
	}

	public String abort() {
		documentNavigation.showDocuments();

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

		// Check if all the mandatory preferences are correctly defined by the
		// user
		if (documentForm.isValid("insert")) {
			// Check if the save document operation has no errors.
			String result = documentForm.save();
			// If there are errors,the user remains on the upload document page.
			if ("customIdDuplicated".equals(result))
				return null;
			documentNavigation.selectDirectory(documentNavigation.getSelectedDir());
			return abort();
		}
		return null;
	}

	public void setDocumentNavigation(DocumentNavigation documentNavigation) {
		this.documentNavigation = documentNavigation;
	}
}