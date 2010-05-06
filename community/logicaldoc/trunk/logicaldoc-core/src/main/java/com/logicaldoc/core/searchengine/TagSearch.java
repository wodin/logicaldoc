package com.logicaldoc.core.searchengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
		Set<Long> docIds = docDao.findDocIdByUserIdAndTag(options.getUserId(), options.getExpression());

		List<Long> ids = new ArrayList<Long>(docIds);
		Collections.sort(ids);
		for (Long id : ids) {
			if (hits.size() == options.getMaxHits()) {
				// The maximum number of hits was reached for a quick query
				moreHitsPresent = true;
				break;
			}

			Hit result = new HitImpl();
			result.setDocId(id);
			hits.add(result);
		}
	}
}