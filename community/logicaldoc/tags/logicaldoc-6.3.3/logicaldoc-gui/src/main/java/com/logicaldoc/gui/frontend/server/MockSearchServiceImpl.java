package com.logicaldoc.gui.frontend.server;

import java.util.Date;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUIHit;
import com.logicaldoc.gui.common.client.beans.GUIResult;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.frontend.client.services.SearchService;

/**
 * Implementation of the SearchService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockSearchServiceImpl extends RemoteServiceServlet implements SearchService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUIResult search(String sid, GUISearchOptions options) {
		GUIResult result = new GUIResult();
		result.setTime(132);

		result.setHasMore(true);
		result.setHits(new GUIHit[options.getMaxHits()]);

		for (int i = 0; i < options.getMaxHits(); i++) {
			GUIHit hit = new GUIHit();
			result.getHits()[i] = hit;
			hit.setId(i + 1000);
			hit.setFolderId(new Long(i + 1000));
			hit.setDate(new Date());
			hit.setCreation(new Date());
			hit.setTitle("Document " + hit.getId());
			hit.setCustomId("custom " + hit.getId());
			hit.setType("doc");
			hit.setIcon("word");
			hit.setSummary("<font style='background-color: rgb(255, 255, 0);'>LogicalDOC</font> Enterprise Edition is the best choice among all document  management solutions. Its&nbsp;...&nbsp; in your environment in a non-invasive way. Thanks to <font style='background-color: rgb(255, 255, 0);'>LogicalDOC</font> Enterprise Edition you are: Autonomous Free Secure The <font style='background-color: rgb(255, 255, 0);'>LogicalDOC</font>  interface is so  intuitive that you do not need  training.  You can view it as an external  disk and work through drag and  drop as you're used to. <font style='background-color: rgb(255, 255, 0);'>LogicalDOC</font> automatically");
			hit.setScore(73);
			hit.setSize(123562);
			hit.setComment("comment");
		}

		if (options.getMaxHits() <= 40)
			result.setHasMore(true);
		else
			result.setHasMore(false);
		return result;
	}

	@Override
	public boolean save(String sid, GUISearchOptions options) {
		return true;
	}

	@Override
	public void delete(String sid, String[] names) {

	}

	@Override
	public GUISearchOptions load(String sid, String name) {
		GUISearchOptions options = new GUISearchOptions();
		options.setName(name);
		options.setExpression("saved search");
		return options;
	}

	@Override
	public GUISearchOptions getSimilarityOptions(String sid, long docId, String locale) {
		GUISearchOptions opt = new GUISearchOptions();
		opt.setExpression("pippo pluto");
		return opt;
	}
}