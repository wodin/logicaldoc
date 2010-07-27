package com.logicaldoc.util.config;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * Configurator for the web.xml file
 * 
 * @author Marco Meschieri - Logical Objects
 * @author Sebastian Wenzky
 * @since 3.0
 */
public class WebConfigurator extends XMLBean {

	public static enum INIT_PARAM {
		PARAM_OVERWRITE, PARAM_APPEND, PARAM_STOP
	}

	public WebConfigurator(String path) {
		super(path);
	}

	/**
	 * Check for existing element within a XML-document
	 * 
	 * @param elements List of Elements which have as child a
	 * 
	 *        <pre>
	 * name
	 * </pre>
	 * 
	 *        tag
	 * @param match_text The text for looking up whether exists
	 * @param name The tag that should be right there for checking this value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Element elementLookUp(List elements, String match_text, String name) {
		for (Iterator iterator = elements.iterator(); iterator.hasNext();) {
			Element elem = (Element) iterator.next();
			Element elementName = elem.getChild(match_text, elem.getNamespace());
			if (elementName != null && elementName.getText().trim().equals(name)) {
				// The element already exists
				return elem;
			}
		}

		return null;
	}

	/**
	 * Adding a contextparam to the web.xml
	 * 
	 * @param param_name the param
	 * @param param_value the value
	 * @param param_description
	 * @param append * @param append if the param exist, should the new value
	 *        appended? possible values are represented in
	 *        {@link WebConfigurator.INIT_PARAM}
	 */
	public void addContextParam(String param_name, String param_value, String param_description, INIT_PARAM append) {
		List contextParams = getRootElement().getChildren("context-param", getRootElement().getNamespace());
		Element contextParam = this.elementLookUp(contextParams, "param-name", param_name);

		if (contextParam != null && append.equals(INIT_PARAM.PARAM_STOP))
			return;

		if (contextParam == null) {
			// Retrieve the last <servlet> element
			Element lastContextParam = (Element) contextParams.get(contextParams.size() - 1);

			List children = getRootElement().getChildren();

			// Find the index of the element to add the new element after.
			int index = children.indexOf(contextParams);

			// Prepare the new mapping
			contextParam = new Element("context-param", getRootElement().getNamespace());
			Element paramName = new Element("param-name", getRootElement().getNamespace());
			paramName.setText(param_name);
			Element paramValue = new Element("param-value", getRootElement().getNamespace());
			paramValue.setText(param_value);
			contextParam.addContent("\n ");
			contextParam.addContent(paramName);
			contextParam.addContent("\n ");
			contextParam.addContent(paramValue);
			contextParam.addContent("\n ");

			// Add the new element to the next index along.
			// This does cover the case where indexOf returned -1.
			children.add(index + 1, contextParam);
			writeXMLDoc();

			return;
		}

		if (contextParam != null && append.equals(INIT_PARAM.PARAM_APPEND)) {

			Element paramValue = (Element) contextParam.getChildren().get(1);
			paramValue.setText(paramValue.getText() + "," + param_value);
			writeXMLDoc();
			return;
		}

		if (contextParam != null && append.equals(INIT_PARAM.PARAM_OVERWRITE)) {
			Element paramValue = (Element) contextParam.getChildren().get(1);
			paramValue.setText(param_value);
			writeXMLDoc();
			return;
		}
	}

	/**
	 * Adds a init parameter to the servlet
	 * 
	 * @param clazz The classname
	 * @param name Name of the Parameter
	 * @param value Value of the Parameter
	 * @param description Description
	 * @param append if the param exist, should the new value appended? possible
	 *        values are represented in {@link WebConfigurator.INIT_PARAM}
	 */
	public void addInitParam(String servletName, String param_name, String param_value, String param_description,
			INIT_PARAM append) {
		List servlets = getRootElement().getChildren("servlet", getRootElement().getNamespace());
		Element servlet = this.elementLookUp(servlets, "servlet-name", servletName);

		if (servlet == null)
			throw new IllegalStateException("The servlet " + servletName
					+ " has not been found. Have you already written the servlet?");

		Element initParam = this.elementLookUp(servlet.getChildren(), "param-name", param_name);

		if (initParam != null && append.equals(INIT_PARAM.PARAM_STOP))
			return;

		if (initParam != null && append.equals(INIT_PARAM.PARAM_APPEND)) {
			Element paramValue = ((Element) initParam.getParent()).getChild("param-value");
			paramValue.setText(paramValue.getText() + "," + param_value);
			writeXMLDoc();
			return;
		}

		if (initParam != null && append.equals(INIT_PARAM.PARAM_OVERWRITE)) {
			Element paramValue = ((Element) initParam.getParent()).getChild("param-value");
			paramValue.setText(param_value);
			writeXMLDoc();
			return;
		}

		Element paramElement = new Element("init-param", getRootElement().getNamespace());

		// the name
		Element param = new Element("param-name", getRootElement().getNamespace());
		param.setText(param_name);
		// paramElement.addContent("\n ");
		paramElement.getChildren().add(param);

		param = new Element("param-value", getRootElement().getNamespace());
		param.setText(param_value);
		// paramElement.addContent("\n ");
		paramElement.getChildren().add(param);

		if (param_description != null && param_description.equals("") != true) {
			param = new Element("description", getRootElement().getNamespace());
			param.setText(param_description);
			// paramElement.addContent("\n ");
			paramElement.getChildren().add(param);
		}

		Element loadOnStartUpElem = (Element) servlet.getChildren().get(servlet.getChildren().size() - 1);

		// sorting the elements in that way, that element "load-on-startup"
		// always stays at the tail
		if (loadOnStartUpElem.getName().equals("load-on-startup")) {
			servlet.getChildren().remove(loadOnStartUpElem);
			servlet.addContent(paramElement);
			servlet.addContent(loadOnStartUpElem);
		} else
			servlet.getChildren().add(paramElement);

	}

	/**
	 * Adds a init parameter to the servlet
	 * 
	 * @param clazz The classname
	 * @param name Name of the Parameter
	 * @param value Value of the Parameter
	 * @param description Description
	 */
	@SuppressWarnings("unchecked")
	public void addInitParam(String servletName, String param_name, String param_value, String param_description) {
		this.addInitParam(servletName, param_name, param_value, param_description, INIT_PARAM.PARAM_STOP);
	}

	/**
	 * Adds a new servlet mapping to the deployment descriptor. If the mapping
	 * already exists no modifications are committed.
	 * 
	 * @param name The servlet name
	 * @param clazz The servlet class fully qualified name
	 */
	public void addServlet(String name, String clazz) {
		this.addServlet(name, clazz, -1);
	}

	/**
	 * Adds a new servlet mapping to the deployment descriptor. If the mapping
	 * already exists no modifications are committed.
	 * 
	 * @param name The servlet name
	 * @param clazz The servlet class fully qualified name
	 * @param load_on startup
	 */
	@SuppressWarnings("unchecked")
	public void addServlet(String name, String clazz, int load_on_startup) {
		Element servlet = null;
		Element servletClass = null;

		// Search for the specified servlet
		List servlets = getRootElement().getChildren("servlet", getRootElement().getNamespace());
		servlet = this.elementLookUp(servlets, "servlet-name", name);
		if (servlet != null) {
			// The servlet already exists, so update it
			servletClass = servlet.getParentElement().getChild("servlet-class");
			servletClass.setText(clazz);
		} else {

			// Retrieve the last <servlet> element
			Element lastServlet = (Element) servlets.get(servlets.size() - 1);

			List children = getRootElement().getChildren();

			// Find the index of the element to add the new element after.
			int index = children.indexOf(lastServlet);

			// Prepare the new mapping
			servlet = new Element("servlet", getRootElement().getNamespace());
			Element servletNameElement = new Element("servlet-name", getRootElement().getNamespace());
			servletNameElement.setText(name);
			servletClass = new Element("servlet-class", getRootElement().getNamespace());
			servletClass.setText(clazz);
			servlet.addContent("\n ");
			servlet.addContent(servletNameElement);
			servlet.addContent("\n ");
			servlet.addContent(servletClass);
			servlet.addContent("\n ");

			// Add the new element to the next index along.
			// This does cover the case where indexOf returned -1.
			children.add(index + 1, servlet);
		}

		writeXMLDoc();
	}

	/**
	 * Adds a new servlet mapping to the deployment descriptor. If the mapping
	 * already exists no modifications are committed.
	 * 
	 * @param servlet The name of the servlet
	 * @param pattern The mapping pattern
	 */
	@SuppressWarnings("unchecked")
	public void addServletMapping(String servlet, String pattern) {
		// Search for the specified mapping
		List mappings = getRootElement().getChildren("servlet-mapping", getRootElement().getNamespace());
		for (Iterator iterator = mappings.iterator(); iterator.hasNext();) {
			Element elem = (Element) iterator.next();
			Element servletName = elem.getChild("servlet-name", elem.getNamespace());
			Element urlPattern = elem.getChild("url-pattern", elem.getNamespace());

			if (servletName.getText().trim().equals(servlet) && urlPattern.getText().trim().equals(pattern)) {
				// The mapping already exists
				return;
			}
		}

		// Retrieve the last <servlet-mapping> element
		Element lastMapping = (Element) mappings.get(mappings.size() - 1);

		List children = getRootElement().getChildren();
		// Find the index of the element to add the new element after.
		int index = children.indexOf(lastMapping);

		// Prepare the new mapping
		Element servletMapping = new Element("servlet-mapping", getRootElement().getNamespace());
		Element servletName = new Element("servlet-name", getRootElement().getNamespace());
		servletName.setText(servlet);
		Element servletPattern = new Element("url-pattern", getRootElement().getNamespace());
		servletPattern.setText(pattern);
		servletMapping.addContent("\n ");
		servletMapping.addContent(servletName);
		servletMapping.addContent("\n ");
		servletMapping.addContent(servletPattern);
		servletMapping.addContent("\n ");

		// Add the new element to the next index along.
		// This does cover the case where indexOf returned -1.
		children.add(index + 1, servletMapping);
		writeXMLDoc();
	}

	public void setDisplayName(String displayName) {
		// Retrieve the <display-name> element
		Element element = getRootElement().getChild("display-name", getRootElement().getNamespace());
		element.setText(displayName);
		writeXMLDoc();
	}

	public String getDisplayName() {
		// Retrieve the <display-name> element
		Element element = getRootElement().getChild("display-name", getRootElement().getNamespace());
		return element.getText();
	}

	public void setDescription(String description) {
		// Retrieve the <display-name> element
		Element element = getRootElement().getChild("description", getRootElement().getNamespace());
		element.setText(description);
		writeXMLDoc();
	}
}