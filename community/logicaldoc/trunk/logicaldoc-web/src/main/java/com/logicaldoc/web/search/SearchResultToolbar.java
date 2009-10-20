package com.logicaldoc.web.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.util.PluginRegistry;
import com.logicaldoc.web.document.DocumentCommand;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * A bean that models the document toolbar.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.5
 */
public class SearchResultToolbar {
	protected static Log log = LogFactory.getLog(SearchResultToolbar.class);

	private List<DocumentCommand> commands = new ArrayList<DocumentCommand>();

	public void init() {
		commands = new ArrayList<DocumentCommand>();

		// Acquire the 'SearchResultToolbar' extensions of the core plugin
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getExtensions("logicaldoc-core", "SearchResultToolbar");
		
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
			DocumentCommand command = new DocumentCommand();
			commands.add(command);
			command.setTitle(Messages.getMessage(ext.getParameter("title").valueAsString()));
			if (StringUtils.isNotEmpty(ext.getParameter("confirm").valueAsString()))
				command.setConfirmation(Messages.getMessage(ext.getParameter("confirm").valueAsString()));
			if (StringUtils.isNotEmpty(ext.getParameter("link").valueAsString())) {
				command.setLinkBinding(FacesUtil.createValueBinding(ext.getParameter("link").valueAsString()));
			}
			if (StringUtils.isNotEmpty(ext.getParameter("target").valueAsString()))
				command.setTarget(ext.getParameter("target").valueAsString());
			command.setIcon(ext.getParameter("icon").valueAsString());
			command.setActionBinding(FacesUtil.createActionMethodBinding(ext.getParameter("action").valueAsString()));
			command.setRenderedBinding(FacesUtil.createValueBinding(ext.getParameter("rendered").valueAsString()));
		}
	}

	public List<DocumentCommand> getCommands() {
		if (commands.isEmpty())
			init();
		return commands;
	}
}