package com.logicaldoc.util.config;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.logicaldoc.util.config.XMLBean;

/**
 * Configurator class for spring's application context setup.
 * 
 * @author Marco Meschieri
 * @version $Id: ContextConfigurator.java,v 1.1 2007/06/29 06:28:25 marco Exp $
 * @since 3.0
 */
public class ContextConfigurator {

	protected XMLBean xml;

	public ContextConfigurator(String resource) {
		xml = new XMLBean(getClass().getClassLoader().getResource(resource));
	}
	
	public ContextConfigurator() {
		xml = new XMLBean(getClass().getClassLoader().getResource("context.xml"));
	}

	public void setProperty(String id, String propertyName, String value) {
		Element element = xml.getChild("bean", "id", id);
		List poperties = element.getChildren("property");
		for (Iterator iter = poperties.iterator(); iter.hasNext();) {
			Element property = (Element) iter.next();
			if (propertyName.equals(property.getAttribute("name").getValue())) {
				property.getChild("value").setText(value);
				break;
			}
		}
	}

	public String getProperty(String id, String propertyName) {
		Element element = getPropertyElement(id, propertyName);
		if (element != null)
			return element.getChild("value").getText();
		return null;
	}

	/**
	 * Retrieves the prop value of the specified property, that is one inside
	 * the <props> tag:
	 * <p>
	 * 
	 * <property><props><prop key="key_name">key_value</prop></props></property>
	 * 
	 * @param id The bean id
	 * @param propertyName The property name
	 * @param key The pop key
	 * @return The prop value
	 */
	public String getPropertyProp(String id, String propertyName, String key) {
		Element element = getPropElement(id, propertyName, key);

		if (element != null)
			return element.getText();
		return null;
	}

	/**
	 * Retrieves the prop element of the specified property, that is one inside
	 * the <props> tag:
	 * <p>
	 * 
	 * <property><props><prop key="key_name">key_value</prop></props></property>
	 * 
	 * @param id The bean id
	 * @param propertyName The property name
	 * @param key The pop key
	 * @return The prop element
	 */
	protected Element getPropElement(String id, String propertyName, String key) {
		Element element = getPropertyElement(id, propertyName);
		Element props = element.getChild("props");
		for (Iterator iter = props.getChildren().iterator(); iter.hasNext();) {
			Element prop = (Element) iter.next();
			if (key.equals(prop.getAttributeValue("key")))
				return prop;
		}
		return null;
	}

	/**
	 * Sets the prop value of the specified property, that is one inside the
	 * <props> tag:
	 * <p>
	 * 
	 * <property><props><prop key="key_name">key_value</prop></props></property>
	 * 
	 * @param id The bean id
	 * @param propertyName The property name
	 * @param key The pop key
	 * @return The prop element
	 */
	public void setPropValue(String id, String propertyName, String key, String value) {
		Element prop = getPropElement(id, propertyName, key);
		if (prop != null)
			prop.setText(value);
	}

	protected Element getPropertyElement(String id, String propertyName) {
		Element element = xml.getChild("bean", "id", id);
		if (element == null)
			return null;
		List poperties = element.getChildren("property");
		for (Iterator iter = poperties.iterator(); iter.hasNext();) {
			Element property = (Element) iter.next();
			if (propertyName.equals(property.getAttribute("name").getValue())) {
				return property;
			}
		}
		return null;
	}

	public void setDialect(String dbms) {
		DBMSConfigurator conf = new DBMSConfigurator();
		setPropValue("SessionFactory", "hibernateProperties", "hibernate.dialect", conf.getAttribute(dbms, "dialect"));
	}

	public String getDialect() {
		return getPropertyProp("SessionFactory", "hibernateProperties", "hibernate.dialect");
	}

	public boolean write() {
		return xml.writeXMLDoc();
	}

	/**
	 * Enlists a new trigger in the scheduler
	 * 
	 * @param triggerId name of the trigger's bean
	 */
	public void addTrigger(String triggerId) {
		Element element = getPropertyElement("Scheduler", "triggers");
		Element list = element.getChild("list");
		List refs = list.getChildren("ref");
		for (Iterator iterator = refs.iterator(); iterator.hasNext();) {
			Element ref = (Element) iterator.next();
			if (triggerId.equals(ref.getAttribute("bean").getValue()))
				return;
		}

		Element ref = new Element("ref", list.getNamespace());
		ref.setAttribute("bean", triggerId);
		list.addContent(ref);

		xml.writeXMLDoc();
	}
}