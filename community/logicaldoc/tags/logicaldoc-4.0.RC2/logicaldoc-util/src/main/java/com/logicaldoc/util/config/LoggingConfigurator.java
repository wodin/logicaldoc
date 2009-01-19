package com.logicaldoc.util.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.text.StrSubstitutor;
import org.jdom.Element;

/**
 * Utility class for manipulating log4j.xml file
 * 
 * @author Michael Scholz, Marco Meschieri
 */
public class LoggingConfigurator {

	private XMLBean xml;

	public LoggingConfigurator() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		xml = new XMLBean(loader.getResource("log4j.xml"));
	}

	/**
	 * This method selects all file appenders.
	 */
	public Collection getLoggingFiles() {
		Collection<String> result = new ArrayList<String>();
		List list = xml.getAllChild("appender");
		Iterator iter = list.iterator();

		while (iter.hasNext()) {
			Element elem = (Element) iter.next();
			List childs = elem.getChildren("param");
			Iterator children = childs.iterator();

			while (children.hasNext()) {
				Element child = (Element) children.next();

				if (child.getAttributeValue("name").equals("File")) {
					result.add(elem.getAttributeValue("name"));
				}
			}
		}

		return result;
	}

	/**
	 * This method selects all file appenders suitable for web visualization
	 */
	public Collection<String> getWebLoggingFiles() {
		Collection<String> result = new ArrayList<String>();
		// Filter appenders not ending by '_WEB'
		for (Iterator iter = getLoggingFiles().iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			if (element.endsWith("_WEB"))
				result.add(element);
		}
		return result;
	}

	/**
	 * This method select all file appenders and filepath.
	 */
	@SuppressWarnings("unchecked")
	public Collection getFiles() {
		Collection result = new ArrayList();
		List list = xml.getAllChild("appender");
		Iterator iter = list.iterator();

		while (iter.hasNext()) {
			Element elem = (Element) iter.next();
			List childs = elem.getChildren("param");
			Iterator children = childs.iterator();

			while (children.hasNext()) {
				Element child = (Element) children.next();

				if (child.getAttributeValue("name").equals("File")) {
					String appender = elem.getAttributeValue("name");
					String file = getFile(appender);
					LoggerProperty logger = new LoggerProperty();
					logger.setAppender(appender.toLowerCase());
					logger.setFile(file);
					result.add(logger);
				}
			}
		}

		return result;
	}

	/**
	 * same as getFile(appender, true)
	 */
	public String getFile(String appender) {
		return getFile(appender, true);
	}

	/**
	 * This method selects a filepath of an appender.
	 * 
	 * @param appender The appender name
	 * @param replaceVariables If true all variables(${var}) in the file path
	 *        will be substituted
	 * @return The log file path
	 */
	public String getFile(String appender, boolean replaceVariables) {
		String result = "";
		Element elem = xml.getChild("appender", "name", appender);
		List childs = elem.getChildren("param");
		Iterator children = childs.iterator();

		while (children.hasNext()) {
			Element child = (Element) children.next();

			if (child.getAttributeValue("name").equals("File")) {
				result = child.getAttributeValue("value");
			}
		}

		if (replaceVariables) {
			result = StrSubstitutor.replaceSystemProperties(result);
		}

		return result;
	}

	/**
	 * This method sets a file of an appender.
	 */
	public void setFile(String appender, String file) {
		Element elem = xml.getChild("appender", "name", appender);
		List childs = elem.getChildren("param");
		Iterator children = childs.iterator();

		while (children.hasNext()) {
			Element child = (Element) children.next();

			if (child.getAttributeValue("name").equals("File")) {
				child.setAttribute("value", file);
			}
		}
	}

	public void addTextAppender(String name) {
		// Check appender existence
		if (xml.getChild("appender", "name", name) != null)
			return;

		// Get the DMS appender and use it as a model
		Element model = xml.getChild("appender", "name", "DMS");

		// Clone the model and add to the root
		Element newAppender = (Element) model.clone();
		List appenders = xml.getRootElement().getChildren("appender");
		xml.getRootElement().addContent(0, newAppender);

		// Setup the appender name
		newAppender.setAttribute("name", name);

		// Now setup the file name
		Iterator params = newAppender.getChildren("param").iterator();
		while (params.hasNext()) {
			Element child = (Element) params.next();
			if (child.getAttributeValue("name").equals("File")) {
				String logfile = name.trim().toLowerCase() + ".log";
				child.setAttribute("value", child.getAttributeValue("value").replaceAll("dms.log", logfile));
				break;
			}
		}
	}

	public void addHtmlAppender(String name) {
		// Check appender existence
		if (xml.getChild("appender", "name", name) != null)
			return;

		// Get the DMS_WEB appender and use it as a model
		Element model = xml.getChild("appender", "name", "DMS_WEB");

		// Clone the model and add to the root
		Element newAppender = (Element) model.clone();
		List appenders = xml.getRootElement().getChildren("appender");
		xml.getRootElement().addContent(0, newAppender);

		// Setup the appender name
		newAppender.setAttribute("name", name);

		// Now setup the file name
		Iterator params = newAppender.getChildren("param").iterator();
		while (params.hasNext()) {
			Element child = (Element) params.next();
			if (child.getAttributeValue("name").equals("File")) {
				String logfile = name.trim().toLowerCase() + ".log.html";
				child.setAttribute("value", child.getAttributeValue("value").replaceAll("dms.log.html", logfile));
				break;
			}
		}
	}

	public void addCategory(Class clazz, String[] appenders) {
		addCategory(clazz.getCanonicalName(), appenders);
	}

	public void addCategory(String name, String[] appenders) {
		// Check category existence
		if (xml.getChild("category", "name", name) != null)
			return;

		Element category = new Element("category");
		category.setAttribute("name", name);
		category.setAttribute("additivity", "false");
		Element priority = new Element("priority");
		priority.setAttribute("value", "info");
		category.addContent(priority);

		for (int i = 0; i < appenders.length; i++) {
			Element appender = new Element("appender-ref");
			appender.setAttribute("ref", appenders[i]);
			category.addContent(appender);
		}

		//Place the new category just before the root category
		Element rootCategory=xml.getChild("root");
		xml.getRootElement().addContent(xml.getRootElement().indexOf(rootCategory)-1, category);
	}

	public boolean write() {
		return xml.writeXMLDoc();
	}
}
