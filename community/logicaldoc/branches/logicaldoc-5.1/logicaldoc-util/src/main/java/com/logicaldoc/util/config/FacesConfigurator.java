package com.logicaldoc.util.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.logicaldoc.util.config.XMLBean;

/**
 * Configurator for the faces-config.xml file
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class FacesConfigurator extends XMLBean {

	public FacesConfigurator() {
		super(System.getProperty("logicaldoc.app.rootdir") + "/WEB-INF/faces-config.xml");
	}

	@SuppressWarnings("unchecked")
	public void addBundle(String bundlePath) {
		// Retrieve the <application> element
		Element application = getRootElement().getChild("application", getRootElement().getNamespace());

		// Check if the bundle was already added
		List bundles = application.getChildren("message-bundle", application.getNamespace());
		for (Iterator iterator = bundles.iterator(); iterator.hasNext();) {
			Element element = (Element) iterator.next();
			if (bundlePath.equals(element.getText()))
				return;
		}

		// The bundle is new and must be appended
		Element bundle = new Element("message-bundle", application.getNamespace());
		bundle.setText(bundlePath);
		application.addContent(bundle);
		writeXMLDoc();
	}

	@SuppressWarnings("unchecked")
	public List<String> getBundles() {
		List<String> bundles = new ArrayList<String>();

		// Retrieve the <application> element
		Element application = getRootElement().getChild("application", getRootElement().getNamespace());

		// Iterate over bundles
		List elements = application.getChildren("message-bundle", application.getNamespace());
		for (Iterator iterator = elements.iterator(); iterator.hasNext();) {
			Element element = (Element) iterator.next();
			bundles.add(element.getText());
		}

		return bundles;
	}
	
	
	public void addLanguageToFacesConfig(String iso639_2) {

		// Retrieve the <application> element
		Element application = getRootElement().getChild("application", getRootElement().getNamespace());

		Element localeConfig = application.getChild("locale-config", application.getNamespace());
		
//		List zxxx = application.getChildren();
//		Element locales = null;
//		for (Iterator iter = zxxx.iterator(); iter.hasNext();) {
//			Element element = (Element) iter.next();
//			if (element.getName().equals("locale-config")) {
//				locales = element;
//				break;
//			}
//		}

		// The bundle is new and must be appended
		Element ptBundle = new Element("supported-locale", application.getNamespace());
		ptBundle.setText(iso639_2);
		localeConfig.addContent(ptBundle);
		writeXMLDoc();
	}
}