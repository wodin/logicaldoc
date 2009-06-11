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

	private MethodBinding actionBinding;

	private ValueBinding linkBinding;

	private String target;

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

	public MethodBinding getActionBinding() {
		return actionBinding;
	}

	public void setActionBinding(MethodBinding actionBinding) {
		this.actionBinding = actionBinding;
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
		if (actionBinding != null)
			return (String) actionBinding.invoke(FacesContext.getCurrentInstance(), new Object[] {});
		else
			return "";
	}

	public String getConfirmation() {
		return confirmation;
	}

	public void setConfirmation(String confirmation) {
		this.confirmation = confirmation;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getLink() {
		if (linkBinding != null)
			return (String) linkBinding.getValue(FacesContext.getCurrentInstance());
		else
			return "X";
	}

	public void setLinkBinding(ValueBinding linkBinding) {
		this.linkBinding = linkBinding;
	}
}
