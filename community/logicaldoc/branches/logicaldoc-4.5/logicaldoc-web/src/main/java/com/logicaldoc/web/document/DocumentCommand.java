package com.logicaldoc.web.document;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

/**
 * This bean represents a single command in the documents toolbar
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.5
 */
public class DocumentCommand {
	private String title;

	private String icon;

	private String confirmation = "X";

	private ValueBinding renderedBinding;

	private MethodBinding action;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public MethodBinding getAction() {
		return action;
	}

	public void setAction(MethodBinding action) {
		this.action = action;
	}

	public void setRenderedBinding(ValueBinding renderedBinding) {
		this.renderedBinding = renderedBinding;
	}

	public Boolean getRendered() {
		if (renderedBinding != null)
			return (Boolean) renderedBinding.getValue(FacesContext.getCurrentInstance());
		else
			return true;
	}

	public String action() {
		if (action != null)
			return (String) action.invoke(FacesContext.getCurrentInstance(), new Object[] {});
		else
			return "";
	}

	public String getConfirmation() {
		return confirmation;
	}

	public void setConfirmation(String confirmation) {
		this.confirmation = confirmation;
	}
}
