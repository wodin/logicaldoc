package com.logicaldoc.gui.frontend.mock;

import java.util.Date;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUIResult;
import com.logicaldoc.gui.common.client.beans.GUIResultHit;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.frontend.client.services.SearchService;

/**
 * Implementation of the SecurityService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockSearchServiceImpl extends RemoteServiceServlet implements SearchService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUIResult search(String sid, GUISearchOptions options) {
		GUIResult result = new GUIResult();
		result.setTime(132356);
		result.setHasMore(true);
		result.setHits(new GUIResultHit[options.getMaxHits()]);

		for (int i = 0; i < options.getMaxHits(); i++) {
			GUIResultHit hit = new GUIResultHit();
			result.getHits()[i] = hit;
			hit.setId(i + 1000);
			hit.setFolderId(new Long(i + 1000));
			hit.setDate(new Date());
			hit.setCreation(new Date());
			hit.setTitle("Document " + hit.getId());
			hit.setCustomId("custom " + hit.getId());
			hit.setDocType("doc");
			hit
					.setSummary("<font style='background-color: rgb(255, 255, 0);'>LogicalDOC</font> Enterprise Edition is the best choice among all document  management solutions. Its&nbsp;...&nbsp; in your environment in a non-invasive way. Thanks to <font style='background-color: rgb(255, 255, 0);'>LogicalDOC</font> Enterprise Edition you are: Autonomous Free Secure The <font style='background-color: rgb(255, 255, 0);'>LogicalDOC</font>  interface is so  intuitive that you do not need  training.  You can view it as an external  disk and work through drag and  drop as you're used to. <font style='background-color: rgb(255, 255, 0);'>LogicalDOC</font> automatically");
			hit.setScore(73);
		}
		return result;
	}
}