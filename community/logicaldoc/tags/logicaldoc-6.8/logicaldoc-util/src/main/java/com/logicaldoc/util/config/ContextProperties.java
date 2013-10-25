package com.logicaldoc.util.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A configuration utility used to retrieve and alter context properties
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class ContextProperties extends OrderedProperties {

	private static final long serialVersionUID = 1L;

	/** this points to an ordinary file */
	private String docPath;

	protected static Logger log = LoggerFactory.getLogger(ContextProperties.class);

	public ContextProperties() throws IOException {
		try {
			load(ContextProperties.class.getClassLoader().getResource("/context.properties"));
		} catch (Throwable t) {
			load(ContextProperties.class.getClassLoader().getResource("context.properties"));
		}
	}

	public ContextProperties(String docname) throws IOException {
		docPath = docname;
		try {
			load(new FileInputStream(docPath));
		} catch (IOException e) {
			log.error("Unable to read from " + docPath, e);
			throw e;
		}
	}

	public ContextProperties(URL docname) throws IOException {
		load(docname);
	}

	/**
	 * Loads the file from the given URL
	 */
	private void load(URL docname) throws IOException {
		try {
			docPath = URLDecoder.decode(docname.getPath(), "UTF-8");
		} catch (IOException e) {
			log.error("Unable to read from " + docPath, e);
			throw e;
		}
		try {
			load(new FileInputStream(docPath));
		} catch (IOException e) {
			log.error("Unable to read from " + docPath, e);
			throw e;
		}
	}

	public ContextProperties(File doc) throws IOException {
		try {
			docPath = doc.getPath();
			load(new FileInputStream(docPath));
		} catch (IOException e) {
			log.error("Unable to read from " + docPath, e);
			throw e;
		}
	}

	/**
	 * Creates new XMLBean from an input stream; XMLBean is read-only!!!
	 */
	public ContextProperties(InputStream is) throws IOException {
		docPath = null;
		try {
			load(is);
		} catch (IOException e) {
			log.error("Unable to read from stream");
			throw e;
		}
	}

	/**
	 * This method saves the properties-file connected by ContextProperties.<br>
	 * <b>NOTE:</b> only call this on an ContextProperties _NOT_ created from an
	 * InputStream!
	 * 
	 * @throws IOException
	 */
	public void write() throws IOException {
		// it might be that we do not have an ordinary file,
		// so we can't write to it
		if (docPath == null)
			throw new IOException("Path not given");

		// Backup the file first
		File src = new File(docPath);
		File backup = new File(src.getParentFile(), src.getName() + ".back");
		FileUtils.copyFile(src, backup);
		log.debug("Backup saved in " + backup.getPath());

		store(new FileOutputStream(docPath), "");
		try {
			store(new FileOutputStream(docPath), "");
			log.info("Saved file " + docPath);
		} catch (IOException ex) {
			if (log.isWarnEnabled()) {
				log.warn(ex.getMessage());
			}
			throw ex;
		}
	}

	public int getInt(String property) {
		return Integer.parseInt(getProperty(property, "0"));
	}

	public int getInt(String property, int defaultValue) {
		return Integer.parseInt(getProperty(property, Integer.toString(defaultValue)));
	}
	
	/**
	 * Gets the property value replacing all variable references
	 */
	public String getPropertyWithSubstitutions(String property) {
		return StrSubstitutor.replaceSystemProperties(getProperty(property));
	}
}