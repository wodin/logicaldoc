package com.logicaldoc.web.util;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.logicaldoc.core.security.User;

/**
 * Faces utilities methods
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.5.0
 */
public class FacesUtil {
	public static MethodBinding createActionListenerMethodBinding(String actionListenerString) {
		Class[] args = { ActionEvent.class };
		MethodBinding methodBinding = null;

		String str = actionListenerString;
		if (!str.startsWith("#{"))
			str = "#{" + str;
		if (!str.endsWith("}"))
			str = str + "}";
		methodBinding = FacesContext.getCurrentInstance().getApplication().createMethodBinding(str, args);
		return methodBinding;
	}

	public static MethodBinding createActionMethodBinding(String action) {
		Class[] args = {};
		MethodBinding methodBinding = null;

		String str = action;
		if (!str.startsWith("#{"))
			str = "#{" + str;
		if (!str.endsWith("}"))
			str = str + "}";
		methodBinding = FacesContext.getCurrentInstance().getApplication().createMethodBinding(str, args);

		return methodBinding;
	}

	public static ValueBinding createValueBinding(String valueExpression) {
		ValueBinding valueBinding = null;

		String str = valueExpression;
		if (!str.startsWith("#{"))
			str = "#{" + str;
		if (!str.endsWith("}"))
			str = str + "}";
		valueBinding = FacesContext.getCurrentInstance().getApplication().createValueBinding(str);
		return valueBinding;
	}
	
	/**
	 * Get an external bean from FacesContext
	 * 
	 * @param beanName to get
	 * @param facesContext used
	 * @param logs in case of error. It is not required
	 * @return the needed bean
	 */
	public static Object accessBeanFromFacesContext(final String beanName, final FacesContext facesContext, Log... logs) {
		final ELContext elContext = facesContext.getELContext();
		final ELResolver elResolver = elContext.getELResolver();
		final Object returnObject = elResolver.getValue(elContext, null, beanName);
		if (returnObject == null && logs!=null) {
			for (Log log : logs) {
				log.error("Bean with name " + beanName
						+ " was not found. Check the faces-config.xml file if the given bean name is ok.");
			}
		}
		return returnObject;
	}

	/**
	 * Iterates through DOM to find Complete id for parameter ending with the id
	 * 
	 * @param id to get
	 * @param facesContext where search must occur
	 * @return String needed
	 */
	public static String findParameterEndingWithId(final String id, final FacesContext facesContext) {
		ExternalContext ec = facesContext.getExternalContext();
		Iterator<String> it = ec.getRequestParameterNames();
		String returnString = null;
		while (it.hasNext()) {
			String parName = (String) it.next();
			if (parName.endsWith(id)) {
				returnString = parName;
				break;
			}
		}
		return returnString;
	}

	/**
	 * Get component ending with and Id
	 * 
	 * @param id to get
	 * @param facesContext where search must occur
	 * @return UIComponent needed
	 */
	public static UIComponent findComponentEndingWithId(final String id, final FacesContext facesContext) {
		String parameter = findParameterEndingWithId(id, facesContext);
		UIComponent component = null;
		if (StringUtils.isNotEmpty(parameter)) {
			component = (UIComponent) facesContext.getViewRoot().findComponent(parameter);
		}
		return component;
	}

	/**
	 * Clear all messages from FacesContext
	 */
	public static void clearAllMessages() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			Iterator<FacesMessage> iter = facesContext.getMessages();
			while (iter.hasNext()) {
				iter.remove();
			}
		}
	}

	/**
	 * Check if there are messages in FacesContext
	 * 
	 * @return
	 */
	public static boolean isMessagesShown() {
		boolean messagesShown = false;
		Iterator<FacesMessage> iterator = FacesContext.getCurrentInstance().getMessages();
		if (iterator.hasNext()) {
			messagesShown = true;
		}
		return messagesShown;
	}

	public static final void forceRefresh(UIInput control) {
		if(control==null)
			return;
		control.setSubmittedValue(null);
		control.setValue(null);
		control.setLocalValueSet(false);
	}
	
	public static Object getCurrentRequestAttribute(String key){
		return FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(key);
	}
}