package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		command.setIcon("actions_upload.png");
		command.setActionBinding(FacesUtil.createActionMethodBinding("newDocWizard.start"));
		command.setRenderedBinding(FacesUtil.createValueBinding("documentNavigation.selectedDir.writeEnabled"));

		command = new DocumentCommand();
		commands.add(command);
		command.setTitle(Messages.getMessage("msg.jsp.uploadfolder"));
		command.setIcon("actions_uploadzip.png");
		command.setActionBinding(FacesUtil.createActionMethodBinding("documentsRecordsManager.startZipUpload"));
		command.setRenderedBinding(FacesUtil.createValueBinding("documentNavigation.selectedDir.bulkImportEnabled"));

		command = new DocumentCommand();
		commands.add(command);
		command.setTitle(Messages.getMessage("clipboard.cut"));
		command.setIcon("actions_cut.png");
		command.setActionBinding(FacesUtil.createActionMethodBinding("documentsRecordsManager.cutToClipboard"));
		command.setRenderedBinding(FacesUtil.createValueBinding("documentsRecordsManager.count>0"));

		command = new DocumentCommand();
		commands.add(command);
		command.setTitle(Messages.getMessage("clipboard.copy"));
		command.setIcon("actions_copy.png");
		command.setActionBinding(FacesUtil.createActionMethodBinding("documentsRecordsManager.copyToClipboard"));
		command.setRenderedBinding(FacesUtil.createValueBinding("documentsRecordsManager.count>0"));

		command = new DocumentCommand();
		commands.add(command);
		command.setTitle(Messages.getMessage("clipboard.move"));
		command.setIcon("actions_paste.png");
		command.setActionBinding(FacesUtil.createActionMethodBinding("documentsRecordsManager.move"));
		command
				.setRenderedBinding(FacesUtil
						.createValueBinding("documentsRecordsManager.clipboardSize>0 and documentNavigation.selectedDir.writeEnabled and (documentsRecordsManager.guiRequest=='cut' or documentsRecordsManager.guiRequest=='copy')"));

		command = new DocumentCommand();
		commands.add(command);
		command.setTitle(Messages.getMessage("document.immutable.make"));
		command.setIcon("actions_immutability.png");
		command.setActionBinding(FacesUtil
				.createActionMethodBinding("documentsRecordsManager.requestImmutabilityComment"));
		command.setRenderedBinding(FacesUtil
				.createValueBinding("documentNavigation.selectedDir.manageImmutabilityEnabled"));

		command = new DocumentCommand();
		commands.add(command);
		command.setTitle(Messages.getMessage("delete"));
		command.setIcon("actions_delete.png");
		command.setActionBinding(FacesUtil.createActionMethodBinding("documentsRecordsManager.deleteSelected"));
		command.setRenderedBinding(FacesUtil.createValueBinding("documentNavigation.selectedDir.writeEnabled"));
		command.setConfirmation(Messages.getMessage("msg.question.deletedoc"));

		// Acquire the 'DocumentToolbar' extensions of the core plugin
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getSortedExtensions("logicaldoc-core", "DocumentToolbar", null);

		for (Extension ext : exts) {
			command = new DocumentCommand();
			commands.add(command);
			command.setTitle(Messages.getMessage(ext.getParameter("title").valueAsString()));
			if (StringUtils.isNotEmpty(ext.getParameter("confirm").valueAsString()))
				command.setConfirmation(Messages.getMessage(ext.getParameter("confirm").valueAsString()));
			command.setIcon(ext.getParameter("icon").valueAsString());
			if (StringUtils.isNotEmpty(ext.getParameter("link").valueAsString())) {
				command.setLinkBinding(FacesUtil.createValueBinding(ext.getParameter("link").valueAsString()));
			}
			if (StringUtils.isNotEmpty(ext.getParameter("target").valueAsString()))
				command.setTarget(ext.getParameter("target").valueAsString());
			if (StringUtils.isNotEmpty(ext.getParameter("action").valueAsString()))
				command.setActionBinding(FacesUtil
						.createActionMethodBinding(ext.getParameter("action").valueAsString()));
			command.setRenderedBinding(FacesUtil.createValueBinding(ext.getParameter("rendered").valueAsString()));
		}
	}

	public List<DocumentCommand> getCommands() {
		if (commands.isEmpty())
			init();
		return commands;
	}
}