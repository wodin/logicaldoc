package com.logicaldoc.core.searchengine;

import java.util.List;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.util.Context;

/**
 * Search specialization for the Tag search.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class TagSearch extends Search {

	protected TagSearch() {
	}

	@Override
	public void internalSearch() throws Exception {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		List<Document> docs = docDao.findByUserIdAndTag(options.getUserId(), options.getExpression(),
				options.getMaxHits());
		for (Document doc : docs) {
			Hit result = new HitImpl();
			result.setDocId(doc.getId());
			result.setTitle(doc.getTitle());
			result.setCustomId(doc.getCustomId());
			result.setCreation(doc.getCreation());
			result.setDate(doc.getDate());
			result.setDocRef(doc.getDocRef());
			result.setFolderId(doc.getFolder().getId());
			result.setSize(doc.getFileSize());
			result.setSource(doc.getSource());
			result.setSourceDate(doc.getSourceDate());
			result.setType(doc.getType());
			hits.add(result);
		}

		moreHitsPresent = (hits.size() >= options.getMaxHits());
	}
}