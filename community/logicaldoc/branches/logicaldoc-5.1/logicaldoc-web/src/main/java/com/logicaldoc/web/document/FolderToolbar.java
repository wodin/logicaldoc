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
 * A bean that models the folder toolbar.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.0
 */
public class FolderToolbar {
	protected static Log log = LogFactory.getLog(FolderToolbar.class);

	private List<DocumentCommand> commands = new ArrayList<DocumentCommand>();

	public void init() {
		commands = new ArrayList<DocumentCommand>();

		// Acquire the 'FolderToolbar' extensions of the core plugin
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getSortedExtensions("logicaldoc-core", "FolderToolbar", null);

		DocumentCommand command = null;
		
		exts = registry.getSortedExtensions("logicaldoc-core", "FolderMenu", null);
		for (Extension ext : exts) {
			if("menuFolderMove".equals(ext.getId()))
				continue;
			
			command = new DocumentCommand();
			commands.add(command);
			command.setTitle(Messages.getMessage(ext.getParameter("title").valueAsString()));
			if (StringUtils.isNotEmpty(ext.getParameter("confirm").valueAsString()))
				command.setConfirmation(Messages.getMessage(ext.getParameter("confirm").valueAsString()));
			if (StringUtils.isNotEmpty(ext.getParameter("link").valueAsString())) {
				command.setLinkBinding(FacesUtil.createValueBinding(ext.getParameter("link").valueAsString()));	
			}
			if (StringUtils.isNotEmpty(ext.getParameter("target").valueAsString()))
				command.setTarget(ext.getParameter("target").valueAsString());
			
					//ext.getParameter("icon").valueAsString());
			String action=ext.getParameter("action").valueAsString();
			if (StringUtils.isNotEmpty(action)){
				action=action.replaceAll("item.userObject", "documentNavigation");
				command.setActionBinding(FacesUtil
						.createActionMethodBinding(action));
			    command.setRenderedBinding(FacesUtil.createValueBinding(ext.getParameter("rendered").valueAsString()));
			}
			String icon="folder_search.png";
			if("menuFolderSearch".equals(ext.getId()))
			  icon="folder_search.png";
			else if("menuFolderHistory".equals(ext.getId()))
				  icon="folder_history.png";
			else if("menuFolderAdd".equals(ext.getId()))
				  icon="folder_add.png";
			else if("menuFolderDelete".equals(ext.getId()))
				  icon="folder_delete.png";
			else if("menuFolderEdit".equals(ext.getId()))
				  icon="folder_edit.png";
			else if("menuFolderExport".equals(ext.getId()))
				  icon="folder_export.png";
			else if("menuFolderRights".equals(ext.getId()))
				  icon="folder_lock.png";
			else if("menuFolderAudit".equals(ext.getId()))
				  icon="folder_audit.png";
			else if("menuFolderRSS".equals(ext.getId()))
				  icon="folder_rss.png";
			else if("menuFolderWorkflow".equals(ext.getId()))
				  icon="folder_workflow.png";
			command.setIcon(icon);
		}
	}

	public List<DocumentCommand> getCommands() {
		if (commands.isEmpty())
			init();
		return commands;
	}
}