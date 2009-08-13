package com.logicaldoc.web.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EventObject;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.component.inputfile.InputFile;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.logicaldoc.web.SessionManagement;

/**
 * <p>
 * The InputFileBean class is the backing bean for the inputfile showcase
 * demonstration. It is used to store the state of the uploaded file.
 * </p>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class InputFileBean implements Renderable {

	protected static Log log = LogFactory.getLog(InputFileBean.class);

	private int percent = -1;

	private String language;
	
	private String encoding;

	private File file = null;

	private String description;

	private PersistentFacesState state;

	private String fileName = "";

	private String contentType = "";

	private boolean extractTags = false;

	private boolean immediateIndexing = false;

	private String tags = "";

	private InputFile inputFile = null;

	private boolean ready = false;

	private Long template = null;

	public InputFileBean() {
		state = PersistentFacesState.getInstance();
	}

	/**
	 * Get the PersistentFacesState.
	 * 
	 * @return state the PersistantFacesState
	 */
	public PersistentFacesState getState() {
		return state;
	}

	/**
	 * Handles rendering exceptions for the progress bar.
	 * 
	 * @param renderingException the exception that occured
	 */
	public void renderingException(RenderingException renderingException) {
		renderingException.printStackTrace();
	}

	public void setPercent(int percent) {
		if (percent < this.percent)
			return;
		this.percent = percent;
		if (percent == 100)
			ready = true;
	}

	public int getPercent() {
		return percent;
	}

	public void setFile(File file) {
		try {
			// If another file was uploaded, first delete the old file
			if ((this.file != null) && this.file.exists()) {
				FileUtils.forceDelete(this.file);
			}
		} catch (IOException e) {
			log.error("Unable to delete temp file " + this.file);
		}
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	private void setReady(boolean ready) {
		this.ready = ready;
		if (ready)
			this.percent = 100;
	}

	public void action(ActionEvent event) {
		InputFile inputFile = (InputFile) event.getSource();
		if (inputFile.getFile() != null && inputFile.getFile().length() != 0) {

			// First of all try to escape the file name with entities
			fileName = StringEscapeUtils.escapeHtml(inputFile.getFilename());

			// Then produce a UTF-8 compliant string
			fileName = StringEscapeUtils.unescapeHtml(fileName);

			setPercent(inputFile.getFileInfo().getPercent());

			if (inputFile.getStatus() == InputFile.SAVED) {
				setFile(inputFile.getFile());
			}

			if (inputFile.getFileInfo().getException() != null) {
				inputFile.getFileInfo().getException().printStackTrace();
			}

			contentType = inputFile.getFileInfo().getContentType();
		} else {
			setReady(false);
		}
	}

	public void progress(EventObject event) {
		InputFile file = (InputFile) event.getSource();
		setPercent(file.getFileInfo().getPercent());
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public boolean isReady() {
		return ready;
	}

	public void deleteUploadDir() {

		if ((file != null) && file.exists()) {

			try {
				log.debug("file = " + file);
				FileUtils.forceDelete(file.getParentFile());
				file = null;
			} catch (IOException e) {
				log.error("Unable to delete temp file " + file.getPath());
			}
		}

		percent = 0;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Reads the uploaded file and stores it's content into a string bufferO.
	 * 
	 * @throws IOException
	 */
	public StringBuffer getContent() throws IOException {
		byte[] buffer = new byte[1024];
		int read = 0;
		StringBuffer content = new StringBuffer();
		InputStream in = new FileInputStream(getFile());

		while ((read = in.read(buffer, 0, 1024)) >= 0) {
			content.append(new String(buffer, 0, read));
		}
		return content;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void reset() {
		deleteUploadDir();
		this.description = null;
		this.language = SessionManagement.getLanguage();
		this.fileName = "";
		setPercent(-1);
		setReady(false);
		setImmediateIndexing(false);
		setExtractTags(false);
		setTags(null);
		template = null;
	}

	public boolean isExtractTags() {
		return extractTags;
	}

	public void setExtractTags(boolean extractTags) {
		this.extractTags = extractTags;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public InputFile getInputFile() {
		return inputFile;
	}

	public void setInputFile(InputFile inputFile) {
		this.inputFile = inputFile;
	}

	public boolean isImmediateIndexing() {
		return immediateIndexing;
	}

	public void setImmediateIndexing(boolean immediateIndexing) {
		this.immediateIndexing = immediateIndexing;
	}

	public Long getTemplate() {
		return template;
	}

	public void setTemplate(Long template) {
		this.template = template;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}