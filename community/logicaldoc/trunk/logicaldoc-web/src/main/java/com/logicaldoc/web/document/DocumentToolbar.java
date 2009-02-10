package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.util.PluginRegistry;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * A bean that models the document toolbar.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class DocumentToolbar {
	protected static Log log = LogFactory.getLog(DocumentToolbar.class);

	private List<DocumentCommand> commands = new ArrayList<DocumentCommand>();

	public void init() {
		commands = new ArrayList<DocumentCommand>();

		// Initialize default commands
		DocumentCommand command = new DocumentCommand();
		commands.add(command);
		command.setTitle(Messages.getMessage("msg.jsp.createdoc"));
		command.setIcon("actions_upload.gif");
		command.setAction(FacesUtil.createActionMethodBinding("newDocWizard.start"));
		command.setRenderedBinding(FacesUtil.createValueBinding("documentNavigation.selectedDir.writeEnabled"));

		command = new DocumentCommand();
		commands.add(command);
		command.setTitle(Messages.getMessage("msg.jsp.uploadfolder"));
		command.setIcon("actions_uploadzip.gif");
		command.setAction(FacesUtil.createActionMethodBinding("documentsRecordsManager.startZipUpload"));
		command.setRenderedBinding(FacesUtil.createValueBinding("documentNavigation.selectedDir.bulkImportEnabled"));

		command = new DocumentCommand();
		commands.add(command);
		command.setTitle(Messages.getMessage("clipboard.copy"));
		command.setIcon("actions_copy.gif");
		command.setAction(FacesUtil.createActionMethodBinding("documentsRecordsManager.copyToClipboard"));

		command = new DocumentCommand();
		commands.add(command);
		command.setTitle(Messages.getMessage("clipboard.move"));
		command.setIcon("actions_paste.gif");
		command.setAction(FacesUtil.createActionMethodBinding("documentsRecordsManager.move"));
		command
				.setRenderedBinding(FacesUtil
						.createValueBinding("documentsRecordsManager.clipboardSize>0 and documentNavigation.selectedDir.writeEnabled"));

		command = new DocumentCommand();
		commands.add(command);
		command.setTitle(Messages.getMessage("document.immutable"));
		command.setIcon("actions_immutability.gif");
		command.setAction(FacesUtil.createActionMethodBinding("documentsRecordsManager.requestImmutabilityComment"));
		command.setRenderedBinding(FacesUtil
				.createValueBinding("documentNavigation.selectedDir.manageImmutabilityEnabled"));

		command = new DocumentCommand();
		commands.add(command);
		command.setTitle(Messages.getMessage("delete"));
		command.setIcon("actions_delete.gif");
		command.setAction(FacesUtil.createActionMethodBinding("documentsRecordsManager.deleteSelected"));
		command.setRenderedBinding(FacesUtil.createValueBinding("documentNavigation.selectedDir.writeEnabled"));
		command.setConfirmation(Messages.getMessage("msg.question.deletedoc"));

		// Acquire the 'DocumentToolbar' extensions of the core plugin
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getExtensions("logicaldoc-core", "DocumentToolbar");

		// Sort the extensions according to ascending position
		List<Extension> sortedExts = new ArrayList<Extension>();
		for (Extension extension : exts) {
			sortedExts.add(extension);
		}
		Collections.sort(sortedExts, new Comparator<Extension>() {
			public int compare(Extension e1, Extension e2) {
				int position1 = Integer.parseInt(e1.getParameter("position").valueAsString());
				int position2 = Integer.parseInt(e2.getParameter("position").valueAsString());
				if (position1 < position2)
					return -1;
				else if (position1 > position2)
					return 1;
				else
					return 0;
			}
		});

		for (Extension ext : sortedExts) {
			command = new DocumentCommand();
			commands.add(command);
			command.setTitle(Messages.getMessage(ext.getParameter("title").valueAsString()));
			if (StringUtils.isNotEmpty(ext.getParameter("confirmation").valueAsString()))
				command.setConfirmation(Messages.getMessage(ext.getParameter("confirmation").valueAsString()));
			command.setIcon(ext.getParameter("icon").valueAsString());
			command.setAction(FacesUtil.createActionMethodBinding(ext.getParameter("action").valueAsString()));
			command.setRenderedBinding(FacesUtil.createValueBinding(ext.getParameter("rendered").valueAsString()));
		}
	}

	public static class DocumentCommand {
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

	public List<DocumentCommand> getCommands() {
		if (commands.isEmpty())
			init();
		return commands;
	}
}