package com.logicaldoc.core.script;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.History;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * Utility methods to handle documents from within Velocity
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.3
 */
public class DocTool {

	public String downloadUrl(long docId) {
		ContextProperties config = Context.get().getProperties();
		String url = config.getProperty("server.url");
		if (!url.endsWith("/"))
			url += "/";
		url += "download?docId=" + docId;
		return url;
	}

	public String displayUrl(long tenantId, long docId) {
		ContextProperties config = Context.get().getProperties();
		String url = config.getProperty("server.url");
		if (!url.endsWith("/"))
			url += "/";
		url += "frontend.jsp?tenantId=" + tenantId + "&docId=" + docId;
		return url;
	}

	public String downloadUrl(Document doc) {
		return downloadUrl(doc.getId());
	}

	public String downloadUrl(History history) {
		return downloadUrl(history.getDocId());
	}

	public String displayUrl(Document doc) {
		return displayUrl(doc.getTenantId(), doc.getId());
	}

	public String displayUtl(History history) {
		return displayUrl(history.getTenantId(), history.getDocId());
	}
}