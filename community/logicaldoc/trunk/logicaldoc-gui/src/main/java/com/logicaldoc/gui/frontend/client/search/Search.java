package com.logicaldoc.gui.frontend.client.search;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIResult;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
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

	private boolean hasMore = false;

	private String suggestion;

	private long estimatedHits;

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
		for (SearchObserver observer : observers) {
			observer.onOptionsChanged(options);
		}
	}

	public void search() {
		ContactingServer.get().show();

		service.search(Session.get().getSid(), options, new AsyncCallback<GUIResult>() {

			@Override
			public void onFailure(Throwable caught) {
				ContactingServer.get().hide();
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIResult result) {
				try {
					suggestion = result.getSuggestion();
					time = result.getTime();
					estimatedHits = result.getEstimatedHits();
					lastResult = new ListGridRecord[result.getHits().length];
					hasMore = result.isHasMore();
					for (int i = 0; i < result.getHits().length; i++) {
						GUIDocument hit = result.getHits()[i];
						ListGridRecord record = new ListGridRecord();
						lastResult[i] = record;
						record.setAttribute("id", hit.getId());
						record.setAttribute("title", hit.getTitle());
						record.setAttribute("size", hit.getFileSize());
						record.setAttribute("icon", hit.getIcon());
						record.setAttribute("version", hit.getVersion());
						record.setAttribute("lastModified", hit.getLastModified());
						record.setAttribute("published", hit.getDate());
						record.setAttribute("publisher", hit.getPublisher());
						record.setAttribute("creator", hit.getCreator());
						record.setAttribute("created", hit.getCreation());
						record.setAttribute("sourceDate", hit.getSourceDate());
						record.setAttribute("sourceAuthor", hit.getSourceAuthor());
						record.setAttribute("customId", hit.getCustomId());
						record.setAttribute("type", hit.getType());
						record.setAttribute("immutable", hit.getImmutable() == 1 ? "stop" : "blank");
						record.setAttribute("signed", hit.getSigned() == 1 ? "rosette" : "blank");
						record.setAttribute("filename", hit.getFileName());
						record.setAttribute("fileVersion", hit.getFileVersion());
						record.setAttribute("fileVersion", hit.getFileVersion());
						record.setAttribute("comment", hit.getComment());
						record.setAttribute("workflowStatus", hit.getWorkflowStatus());
						record.setAttribute("startPublishing", hit.getStartPublishing());
						record.setAttribute("stopPublishing", hit.getStopPublishing());
						record.setAttribute("publishedStatus", hit.getPublished() == 1 ? "yes" : "no");
						record.setAttribute("score", hit.getScore());
						record.setAttribute("summary", hit.getSummary());
						record.setAttribute("lockUserId", hit.getLockUserId());
						record.setAttribute("folderId", hit.getFolder().getId());
						record.setAttribute("folder", hit.getFolder().getName());
						record.setAttribute("docRef", hit.getDocRef());
						record.setAttribute("rating", "rating" + hit.getRating());
						record.setAttribute("template", hit.getTemplate());
						
						
						if (hit.getIndexed() == Constants.INDEX_INDEXED)
							record.setAttribute("indexed", "indexed");
						else if (hit.getIndexed() == Constants.INDEX_SKIP)
							record.setAttribute("indexed", "unindexable");
						else
							record.setAttribute("indexed", "blank");

						if (hit.getStatus() == Constants.DOC_LOCKED)
							record.setAttribute("locked", "stop");
						else if (hit.getStatus() == Constants.DOC_CHECKED_OUT)
							record.setAttribute("locked", "page_edit");
						else
							record.setAttribute("locked", "blank");

						String[] extNames = Session.get().getInfo().getConfig("search.extattr").split(",");
						for (String name : extNames) {
							record.setAttribute("ext_" + name, hit.getValue(name));
						}
					}

					for (SearchObserver observer : observers) {
						observer.onSearchArrived();
					}
				} finally {
					ContactingServer.get().hide();
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

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public long getEstimatedHits() {
		return estimatedHits;
	}

	public void setEstimatedResults(long estimatedResults) {
		this.estimatedHits = estimatedResults;
	}
}