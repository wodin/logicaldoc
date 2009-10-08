package com.logicaldoc.util.config;

import org.jdom.Element;

import com.logicaldoc.util.config.XMLBean;

/**
 * @author Michael Scholz
 */
public class DBMSConfigurator {

	private XMLBean xml;

	/** Creates a new instance of DBMSConfigurator */
	public DBMSConfigurator() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		xml = new XMLBean(loader.getResource("dbms.xml"));
	}

	public String getAttribute(String name, String attr) {
		Element element = xml.getChild("db", "name", name);
		return xml.getAttributeValue(element, attr);
	}
}
