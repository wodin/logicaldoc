package com.logicaldoc.gui.frontend.client.search;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIResult;
import com.logicaldoc.gui.common.client.beans.GUIResultHit;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.services.SearchService;
import com.logicaldoc.gui.frontend.client.services.SearchServiceAsync;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Collector for all searches
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Search {
	private static Search instance;

	private ListGridRecord[] lastResult = new ListGridRecord[0];

	private GUISearchOptions options = new GUISearchOptions();

	private Set<SearchObserver> observers = new HashSet<SearchObserver>();

	private SearchServiceAsync service = (SearchServiceAsync) GWT.create(SearchService.class);

	private long time;

	private Search() {
	}

	public static Search get() {
		if (instance == null)
			instance = new Search();
		return instance;
	}

	public void addObserver(SearchObserver observer) {
		if (!observers.contains(observer))
			observers.add(observer);
	}

	public GUISearchOptions getOptions() {
		return options;
	}

	public void setOptions(GUISearchOptions options) {
		this.options = options;
	}

	public void search() {
		service.search(Session.get().getSid(), options, new AsyncCallback<GUIResult>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIResult result) {
				time = result.getTime();
				lastResult = new ListGridRecord[result.getHits().length];
				for (int i = 0; i < result.getHits().length; i++) {
					GUIResultHit hit = result.getHits()[i];
					ListGridRecord record = new ListGridRecord();
					lastResult[i] = record;
					record.setAttribute("title", hit.getTitle());
					record.setAttribute("id", hit.getId());
					record.setAttribute("docRef", hit.getDocRef());
					record.setAttribute("score", hit.getScore());
					record.setAttribute("summary", hit.getSummary());
					record.setAttribute("creation", hit.getCreation());
					record.setAttribute("date", hit.getDate());
					record.setAttribute("customId", hit.getCustomId());
					record.setAttribute("folderId", hit.getFolderId());
					record.setAttribute("size", hit.getSize());
					record.setAttribute("icon", hit.getType());
				}

				for (SearchObserver observer : observers) {
					observer.onSearchArrived();
				}
			}

		});
	}

	public ListGridRecord[] getLastResult() {
		return lastResult;
	}

	public long getTime() {
		return time;
	}

	public boolean isEmpty() {
		return (getLastResult() == null || getLastResult().length == 0);
	}
}