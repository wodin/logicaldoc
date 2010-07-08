package com.logicaldoc.webservice.search;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.TagCloud;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.FolderDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.dao.GenericDAO;
import com.logicaldoc.core.searchengine.Hit;
import com.logicaldoc.core.searchengine.HitImpl;
import com.logicaldoc.core.searchengine.Search;
import com.logicaldoc.core.searchengine.SearchOptions;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.util.Context;
import com.logicaldoc.webservice.AbstractService;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.WSFolder;

/**
 * Search Web Service Implementation
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class SearchServiceImpl extends AbstractService implements SearchService {

	public static Log log = LogFactory.getLog(SearchServiceImpl.class);

	@Override
	public WSSearchResult find(String sid, SearchOptions options) throws Exception {
		User user = validateSession(sid);

		WSSearchResult searchResult = new WSSearchResult();

		Search lastSearch = Search.get(options);
		lastSearch.search();
		List<Hit> hitsList = lastSearch.getHits();

		searchResult.setTotalHits(hitsList.size());
		searchResult.setHits(hitsList.toArray(new HitImpl[0]));
		searchResult.setEstimatedHitsNumber(lastSearch.getEstimatedHitsNumber());
		searchResult.setTime(lastSearch.getExecTime());
		searchResult.setMoreHits(lastSearch.isMoreHitsPresent() ? 1 : 0);

		log.info("User: " + user.getUserName() + " Query: " + options.getExpression());
		log.info("Results number: " + hitsList.size());

		return searchResult;
	}

	@Override
	public WSDocument[] findByFilename(String sid, String filename) throws Exception {
		User user = validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		List<Document> docs = docDao.findByFileNameAndParentFolderId(null, filename, null, null);
		WSDocument[] wsDocs = new WSDocument[docs.size()];
		for (int i = 0; i < docs.size(); i++) {
			checkReadEnable(user, docs.get(i).getFolder().getId());
			docDao.initialize(docs.get(i));
			wsDocs[i] = WSDocument.fromDocument(docs.get(i));
		}

		return wsDocs;
	}

	@Override
	public WSDocument[] findByTag(String sid, String tag) throws Exception {
		User user = validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		List<Document> docs = docDao.findByUserIdAndTag(user.getId(), tag, null);
		WSDocument[] wsDocs = new WSDocument[docs.size()];
		for (int i = 0; i < docs.size(); i++) {
			docDao.initialize(docs.get(i));
			wsDocs[i] = WSDocument.fromDocument(docs.get(i));
		}

		return wsDocs;
	}

	@Override
	public WSFolder[] findFolders(String sid, String name) throws Exception {
		User user = validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		List<Menu> folders = folderDao.find(name);
		WSFolder[] wsFolders = new WSFolder[folders.size()];
		for (int i = 0; i < folders.size(); i++) {
			checkReadEnable(user, folders.get(i).getId());
			folderDao.initialize(folders.get(i));
			wsFolders[i] = WSFolder.fromFolder(folders.get(i));
		}

		return wsFolders;
	}

	@Override
	public TagCloud[] getTagCloud(String sid) throws Exception {
		GenericDAO genericDao = (GenericDAO) Context.getInstance().getBean(GenericDAO.class);
		Generic generic = genericDao.findByAlternateKey("tagcloud", "-");
		if (generic == null)
			return null;

		TagCloud[] tagClouds = new TagCloud[0];
		genericDao.initialize(generic);
		int i = 0;
		for (String tag : generic.getAttributeNames()) {
			TagCloud tc = new TagCloud(tag);
			StringTokenizer st = new StringTokenizer(generic.getValue(tag).toString(), "|", false);
			tc.setCount(Integer.parseInt(st.nextToken()));
			tc.setScale(Integer.parseInt(st.nextToken()));
			tagClouds[i] = tc;
			i++;
		}

		return tagClouds;
	}

	@Override
	public String[] getTags(String sid) throws Exception {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		List<Object> tags = docDao.findAllTags(null);
		String[] wsTags = new String[tags.size()];
		for (int i = 0; i < tags.size(); i++) {
			wsTags[i] = (String) tags.get(i);
		}
		return wsTags;
	}
}