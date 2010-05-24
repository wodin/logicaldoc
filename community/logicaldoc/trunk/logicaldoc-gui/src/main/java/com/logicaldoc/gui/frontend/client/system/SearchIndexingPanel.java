package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUISearchEngine;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.SearchEngineService;
import com.logicaldoc.gui.frontend.client.services.SearchEngineServiceAsync;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * Panel showing the search and indexing infos.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SearchIndexingPanel extends VLayout {
	private TabSet tabSet = new TabSet();

	private Layout searchEngineTabPanel;

	private Layout parsersInfoTabPanel;

	private Layout indexingQueueTabPanel;

	private GUISearchEngine searchEngine;

	public SearchIndexingPanel(GUISearchEngine searchEngine) {
		setWidth100();
		
		this.searchEngine = searchEngine;

		Tab searchEngineTab = new Tab(I18N.getMessage("searchengine"));
		searchEngineTabPanel = new HLayout();
		searchEngineTabPanel.setWidth100();
		searchEngineTabPanel.setHeight100();

		// Languages
		StaticTextItem languages = new StaticTextItem();
		languages.setName("languages");
		languages.setTitle(I18N.getMessage("languages"));
		languages.setValue(this.searchEngine.getLanguages());

		// Entries
		StaticTextItem entries = new StaticTextItem();
		entries.setName("entries");
		entries.setTitle(I18N.getMessage("entries"));
		entries.setValue("" + this.searchEngine.getEntries());

		searchEngineTab.setPane(searchEngineTabPanel);

		Tab parsersInfoTab = new Tab(I18N.getMessage("parsersinfo"));
		parsersInfoTabPanel = new HLayout();
		parsersInfoTabPanel.setWidth100();
		parsersInfoTabPanel.setHeight100();
		parsersInfoTab.setPane(parsersInfoTabPanel);

		Tab indexingQueueTab = new Tab(I18N.getMessage("indexingqueue"));
		indexingQueueTabPanel = new HLayout();
		indexingQueueTabPanel.setWidth100();
		indexingQueueTabPanel.setHeight100();
		indexingQueueTab.setPane(indexingQueueTabPanel);

		tabSet.setTabs(searchEngineTab, parsersInfoTab, indexingQueueTab);

		addMember(tabSet);
	}

	private void retrieveValues(GUISearchEngine searchEngine) {
		this.searchEngine = searchEngine;
	}
}
