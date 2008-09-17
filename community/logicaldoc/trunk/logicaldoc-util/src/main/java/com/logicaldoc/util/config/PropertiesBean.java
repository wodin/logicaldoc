package com.logicaldoc.util.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A configuration utility used to retrieve and alter context properties
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class PropertiesBean {
	private Properties props=new Properties();

	/** this points to an ordinary file */
	private String docPath;

	protected static Log log = LogFactory.getLog(PropertiesBean.class);

	public PropertiesBean() throws IOException{
		this(PropertiesBean.class.getClassLoader().getResource("context.properties"));
	}
	
	public PropertiesBean(String docname) throws IOException {
		docPath = docname;
		try {
			props.load(new FileInputStream(docPath));
		} catch (IOException e) {
			log.error("Unable to read from " + docPath,e);
			throw e;
		}
	}

	public PropertiesBean(URL docname) throws IOException {
		try {
			docPath = URLDecoder.decode(docname.getPath(),"UTF-8");
		} catch (IOException e) {
			log.error("Unable to read from " + docPath,e);
			throw e;
		}
		try {
			props.load(new FileInputStream(docPath));
		} catch (IOException e) {
			log.error("Unable to read from " + docPath,e);
			throw e;
		}
	}

	public PropertiesBean(File doc) throws IOException {
		try {
			docPath = doc.getPath();
			props.load(new FileInputStream(docPath));
		} catch (IOException e) {
			log.error("Unable to read from " + docPath,e);
			throw e;
		}
	}
	
	/**
	 * Creates new XMLBean from an input stream; XMLBean is read-only!!!
	 */
	public PropertiesBean(InputStream is) throws IOException {
		docPath = null;
		try {
			props.load(is);
		} catch (IOException e) {
			log.error("Unable to read from stream");
			throw e;
		}
	}

	public String getProperty(String key) {
		return props.getProperty(key);
	}

	public void setProperty(String key, String value) {
		props.setProperty(key, value);
	}

	public void removeProperty(String key) {
		props.remove(key);
	}

	/**
	 * This method saves the properties-file connected by PropertiesBean.<br>
	 * <b>NOTE:</b> only call this on an PropertiesBean _NOT_ created from an
	 * InputStream!
	 * 
	 * @throws IOException
	 */
	public void write() throws IOException {
		// it might be that we do not have an ordinary file,
		// so we can't write to it
		if (docPath == null)
			throw new IOException("Path not given");

		props.store(new FileOutputStream(docPath), "");
		try {
			props.store(new FileOutputStream(docPath), "");
		} catch (IOException ex) {
			if (log.isWarnEnabled()) {
				log.warn(ex.getMessage());
			}
			throw ex;
		}
	}
}