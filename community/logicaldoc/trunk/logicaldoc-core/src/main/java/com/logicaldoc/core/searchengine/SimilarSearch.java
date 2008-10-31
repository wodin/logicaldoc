package com.logicaldoc.core.searchengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Term;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.TermDAO;
import com.logicaldoc.core.searchengine.util.ResultComparator;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;

/**
 * Class for finding similar documents. Created on 21.03.2004
 * 
 * @author Michael Scholz
 */
public class SimilarSearch {

	public SimilarSearch() {
	}

	/**
	 * This method finds documents, which are similar to a reference document.
	 * All documents are valued by dice-coefficient. dice-coefficient = 2*scalar
	 * product (doc1,doc2) / (absolute value(doc1) + absoulute value(doc2))
	 * 
	 * @param docId - ID of the reference document.
	 * @param minScore - Minimum score value (between 0 and 1)
	 * @return Collection of similar documents sorted by score value.
	 */
	public Collection<Result> findSimilarDocuments(long docId, double minScore, long userId) {
		TermDAO termsDao = (TermDAO) Context.getInstance().getBean(TermDAO.class);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Collection<Term> basicTerms = termsDao.findByDocId(docId);

		// select all documents having a keyword a the basic document
		Collection<Term> terms = termsDao.findByStem(docId, 1000);
		Collection<Result> result = new ArrayList<Result>();

		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Set<Long> cache = new HashSet<Long>();
		Iterator<Term> iter = terms.iterator();
		while (iter.hasNext()) {
			// calculate the score for ranking
			Term term = (Term) iter.next();
			Document doc = docDao.findById(term.getDocId());
			Menu folder = doc.getFolder();
			if (!cache.contains(term.getDocId()) && mdao.isReadEnable(folder.getId(), userId)) {
				Collection<Term> docTerms = termsDao.findByDocId(term.getDocId());
				float score = calculateScore(basicTerms, docTerms);

				if (score >= minScore) {
					Result res = new ResultImpl();
					res.createScore(score);
					res.setIcon(doc.getIcon());
					res.setDocId(doc.getId());
					res.setTitle(doc.getTitle());
					res.setSourceDate(doc.getSourceDate());
					res.setDate(doc.getDate());
					if (!result.contains(res))
						result.add(res);
				}
			}
			if (!cache.contains(term.getDocId()))
				cache.add(term.getDocId());
		}

		Collections.sort((List<Result>) result, new ResultComparator());
		return result;
	}

	private float calculateScore(Collection<Term> refTerms, Collection<Term> terms) {
		float score = 0.0f;
		float abs1 = 0.0f;
		float abs2 = 0.0f;
		Hashtable<String, Double> table = convert(terms);
		Iterator<Term> iter = refTerms.iterator();

		while (iter.hasNext()) {
			Term term = iter.next();
			abs1 += term.getValue() * term.getValue();

			if (table.containsKey(term.getStem())) {
				Double value = (Double) table.get(term.getStem());
				abs2 += value.doubleValue() * value.doubleValue();
				score += value.doubleValue() * term.getValue();
			}
		}

		return (2 * score) / (abs1 + abs2);
	}

	private Hashtable<String, Double> convert(Collection<Term> coll) {
		Hashtable<String, Double> table = new Hashtable<String, Double>(coll.size());
		Iterator<Term> iter = coll.iterator();

		while (iter.hasNext()) {
			Term term = (Term) iter.next();
			table.put(term.getStem(), new Double(term.getValue()));
		}

		return table;
	}
}