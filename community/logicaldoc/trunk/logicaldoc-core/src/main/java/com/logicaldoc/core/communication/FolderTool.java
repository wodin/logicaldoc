package com.logicaldoc.core.communication;

import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderHistory;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * Utility methods to handle folders from within Velocity
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.3
 */
public class FolderTool {

	public String display(long tenatId, long folderId) {
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		String url = config.getProperty("server.url");
		if (!url.endsWith("/"))
			url += "/";
		url += "?tenantId="+tenatId+"&folderId=" + folderId;
		return url;
	}
	
	public String display(Folder folder) {
		return display(folder.getTenantId(), folder.getId());
	}
	
	public String display(FolderHistory history) {
		return display(history.getTenantId(), history.getFolderId());
	}
}
