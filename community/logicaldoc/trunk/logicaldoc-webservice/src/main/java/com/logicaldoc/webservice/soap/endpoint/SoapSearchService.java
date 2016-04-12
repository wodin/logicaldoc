package com.logicaldoc.webservice.soap.endpoint;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.TagCloud;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.searchengine.Hit;
import com.logicaldoc.core.searchengine.Search;
import com.logicaldoc.core.searchengine.SearchOptions;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.webservice.model.WSDocument;
import com.logicaldoc.webservice.model.WSFolder;
import com.logicaldoc.webservice.model.WSSearchOptions;
import com.logicaldoc.webservice.model.WSSearchResult;
import com.logicaldoc.webservice.model.WSTagCloud;
import com.logicaldoc.webservice.services.AbstractService;
import com.logicaldoc.webservice.services.SearchService;

/**
 * Search Web Service Implementation
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class SoapSearchService extends AbstractService implements SearchService {

	protected static Logger log = LoggerFactory.getLogger(SoapSearchService.class);

	@Override
	public WSSearchResult find(String sid, WSSearchOptions opt) throws Exception {
		User user = validateSession(sid);

		SearchOptions options = opt.toSearchOptions();
		options.setUserId(user.getId());

		WSSearchResult searchResult = new WSSearchResult();

		Search lastSearch = Search.get(options);
		lastSearch.search();
		List<Hit> hitsList = lastSearch.getHits();

		List<WSDocument> docs = new ArrayList<WSDocument>();
		for (Hit hit : hitsList) {
			WSDocument doc = WSDocument.fromDocument(hit);
			doc.setScore(hit.getScore());
			doc.setSummary(hit.getSummary());
			docs.add(doc);
		}

		searchResult.setTotalHits(docs.size());
		searchResult.setHits(docs.toArray(new WSDocument[0]));
		searchResult.setEstimatedHitsNumber(lastSearch.getEstimatedHitsNumber());
		searchResult.setTime(lastSearch.getExecTime());
		searchResult.setMoreHits(lastSearch.isMoreHitsPresent() ? 1 : 0);

		log.info("User: " + user.getUserName() + " Query: " + options.getExpression());
		log.info("Results number: " + docs.size());

		return searchResult;
	}

	@Override
	public WSDocument[] findByFilename(String sid, String filename) throws Exception {
		User user = validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		List<Document> docs = docDao.findByFileNameAndParentFolderId(null, filename, null, user.getTenantId(), null);
		WSDocument[] wsDocs = new WSDocument[docs.size()];
		for (int i = 0; i < docs.size(); i++) {
			try {
				checkReadEnable(user, docs.get(i).getFolder().getId());
				checkPublished(user, docs.get(i));
				checkArchived(docs.get(i));
			} catch (Exception e) {
				continue;
			}
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
			try {
				checkPublished(user, docs.get(i));
				checkArchived(docs.get(i));
			} catch (Exception e) {
				continue;
			}
			docDao.initialize(docs.get(i));
			wsDocs[i] = WSDocument.fromDocument(docs.get(i));
		}

		return wsDocs;
	}

	@Override
	public WSFolder[] findFolders(String sid, String name) throws Exception {
		User user = validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		List<Folder> folders = folderDao.find(name, user.getTenantId());
		WSFolder[] wsFolders = new WSFolder[folders.size()];
		for (int i = 0; i < folders.size(); i++) {
			try {
				checkReadEnable(user, folders.get(i).getId());
			} catch (Exception e) {
				continue;
			}
			folderDao.initialize(folders.get(i));
			wsFolders[i] = WSFolder.fromFolder(folders.get(i));
		}

		return wsFolders;
	}

	@Override
	public WSTagCloud[] getTagCloud(String sid) throws Exception {
		validateSession(sid);

		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		List<TagCloud> list = dao.getTagCloud(sid);

		WSTagCloud[] tagClouds = new WSTagCloud[list.size()];
		int i = 0;
		for (TagCloud tag : list) {
			tagClouds[i] = WSTagCloud.fromTagCloud(tag);
			i++;
		}

		return tagClouds;
	}

	@Override
	public String[] getTags(String sid) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		List<String> tags = docDao.findAllTags(null, user.getTenantId());
		String[] wsTags = new String[tags.size()];
		for (int i = 0; i < tags.size(); i++) {
			wsTags[i] = tags.get(i);
		}
		return wsTags;
	}
}