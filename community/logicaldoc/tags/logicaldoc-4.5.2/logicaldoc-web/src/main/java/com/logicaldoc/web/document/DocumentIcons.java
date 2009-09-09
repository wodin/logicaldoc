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
 * A bean that models the document icons.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class DocumentIcons {
	protected static Log log = LogFactory.getLog(DocumentIcons.class);

	private List<DocumentCommand> commands = new ArrayList<DocumentCommand>();

	public void init() {
		commands = new ArrayList<DocumentCommand>();

		// Acquire the 'DocumentIcons' extensions of the core plugin
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getSortedExtensions("logicaldoc-core", "DocumentIcons", null);

		DocumentCommand command;
		for (Extension ext : exts) {		
			command = new DocumentCommand();
			commands.add(command);
			command.setTitle(Messages.getMessage(ext.getParameter("title").valueAsString()));
			if (StringUtils.isNotEmpty(ext.getParameter("confirm").valueAsString()))
				command.setConfirmation(Messages.getMessage(ext.getParameter("confirm").valueAsString()));
			if (StringUtils.isNotEmpty(ext.getParameter("link").valueAsString())){
				command.setLinkBinding(FacesUtil.createValueBinding(ext.getParameter("link").valueAsString()));
			}
			if (StringUtils.isNotEmpty(ext.getParameter("target").valueAsString()))
				command.setTarget(ext.getParameter("target").valueAsString());
			command.setIcon(ext.getParameter("icon").valueAsString());
			if (StringUtils.isNotEmpty(ext.getParameter("action").valueAsString()))
				command.setActionBinding(FacesUtil.createActionMethodBinding(ext.getParameter("action").valueAsString()));
			if (StringUtils.isNotEmpty(ext.getParameter("rendered").valueAsString()))
				command.setRenderedBinding(FacesUtil.createValueBinding(ext.getParameter("rendered").valueAsString()));
		}
	}

	public List<DocumentCommand> getCommands() {
		if (commands.isEmpty())
			init();
		return commands;
	}
}