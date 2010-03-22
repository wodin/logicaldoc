package com.logicaldoc.webservice;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.java.plugin.registry.Extension;

import com.logicaldoc.util.PluginRegistry;


/**
 * Copy from logicaldoc-web Messages
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class WebServiceMessages extends AbstractMap<String, String> {
	/**
	 * The list of bundles in which keys will be searched
	 */
	private static List<String> bundles = new ArrayList<String>();

	public WebServiceMessages() {
	}

	public static String getMessage(String key, Locale locale) {
		if (bundles.isEmpty()) {
			// Acquire the 'ResourceBundle' extensions of the core plugin
			PluginRegistry registry = PluginRegistry.getInstance();
			Collection<Extension> exts = registry.getSortedExtensions("logicaldoc-core", "ResourceBundle", null);

			for (Extension ext : exts) {
				bundles.add(ext.getParameter("bundle").valueAsString());
			}
		}
		
		// Iterate over bundles in reverse order
		for (int i = bundles.size() - 1; i >= 0; i--) {
			String path = bundles.get(i);

			try {
				ResourceBundle bundle = ResourceBundle.getBundle(path, locale);
				return bundle.getString(key);
			} catch (Exception e) {
				// Continue
			}
		}
		
		return key;
	}

	public static String getMessage(String key) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Locale locale = (Locale) facesContext.getExternalContext().getSessionMap().get("locale");

		if (locale == null) {
			locale = facesContext.getApplication().getDefaultLocale();
		}

		return getMessage(key, locale);
	}

	public static String getMessage(String key, String val0) {
		String msg = getMessage(key);
		return MessageFormat.format(msg, new Object[] { val0 });
	}

	public static String getMessage(String key, Object[] values) {
		String msg = getMessage(key);
		return MessageFormat.format(msg, values);
	}

	public static String getMessage(String key, Locale locale, Object[] values) {
		String msg = getMessage(key, locale);
		return MessageFormat.format(msg, values);
	}

	public static void addLocalizedWarn(String message) {
		addWarn(WebServiceMessages.getMessage(message));
	}

	public static void addWarn(String message) {
		addMessage(FacesMessage.SEVERITY_WARN, null, message, message);
	}

	public static void addLocalizedError(String message) {
		addError(WebServiceMessages.getMessage(message));
	}

	public static void addLocalizedError(String message, String id) {
		addError(WebServiceMessages.getMessage(message), id);
	}

	public static void addError(String message) {
		addMessage(FacesMessage.SEVERITY_ERROR, null, message, message);
	}

	public static void addError(String message, String id) {
		addMessage(FacesMessage.SEVERITY_ERROR, id, message, message);
	}

	public static void addLocalizedInfo(String message) {
		addInfo(WebServiceMessages.getMessage(message));
	}

	public static void addLocalizedInfo(String message, Object[] parameters) {
		addInfo(WebServiceMessages.getMessage(message, parameters));
	}
	
	public static void addInfo(String message) {
		addMessage(FacesMessage.SEVERITY_INFO, null, message, message);
	}

	public static void addMessage(FacesMessage.Severity severity, String summary, String detail) {
		addMessage(severity, null, summary, detail);
	}

	/**
	 * Adds a message in the jsf queue
	 * 
	 * @param severity The severity level
	 * @param clientId The componentID (null can be accepted)
	 * @param summary The summary part(bundle key)
	 * @param detail The detail part(bundle key)
	 */
	public static void addMessage(FacesMessage.Severity severity, String clientId, String summary, String detail) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(severity, summary, detail);
		facesContext.addMessage(clientId, message);
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return null;
	}

	@Override
	public String get(Object key) {
		return WebServiceMessages.getMessage(key.toString());
	}
}
