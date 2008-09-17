package com.logicaldoc.core.searchengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.logicaldoc.core.document.Term;
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
	public Collection findSimilarDocuments(int menuId, double minScore, String username) {
		TermDAO termsDao = (TermDAO) Context.getInstance().getBean(TermDAO.class);
		Collection<Term> basicTerms = termsDao.findByMenuId(menuId);

		// select all documents having a keyword a the basic document
		Collection<Term> terms = termsDao.findByStem(menuId, 1000);
		Collection<Result> result = new ArrayList<Result>();

		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Set<Integer> cache=new HashSet<Integer>();
		Iterator<Term> iter = terms.iterator();
		while (iter.hasNext()) {
			// calculate the score for ranking
			Term term = (Term) iter.next();
			if (!cache.contains(term.getMenuId()) && mdao.isReadEnable(term.getMenuId(), username)) {
				Collection<Term> docTerms = termsDao.findByMenuId(term.getMenuId());
				float score = calculateScore(basicTerms, docTerms);

				if (score >= minScore) {
					Result res = new ResultImpl();
					Menu menu = mdao.findByPrimaryKey(term.getMenuId());
					res.createScore(score);
					res.setIcon(menu.getMenuIcon());
					res.setMenuId(menu.getMenuId());
					res.setName(menu.getMenuText());
					res.setPath(menu.getMenuPath());
					if (!result.contains(res))
						result.add(res);
				}
			}
			if(!cache.contains(term.getMenuId()))
				cache.add(term.getMenuId());
		}

		Collections.sort((List<Result>) result, new ResultComparator());
		return result;
	}

	private float calculateScore(Collection refTerms, Collection terms) {
		float score = 0.0f;
		float abs1 = 0.0f;
		float abs2 = 0.0f;
		Hashtable table = convert(terms);
		Iterator iter = refTerms.iterator();

		while (iter.hasNext()) {
			Term term = (Term) iter.next();
			abs1 += term.getValue() * term.getValue();

			if (table.containsKey(term.getStem())) {
				Double value = (Double) table.get(term.getStem());
				abs2 += value.doubleValue() * value.doubleValue();
				score += value.doubleValue() * term.getValue();
			}
		}

		return (2 * score) / (abs1 + abs2);
	}

	private Hashtable convert(Collection coll) {
		Hashtable<String, Double> table = new Hashtable<String, Double>(coll.size());
		Iterator iter = coll.iterator();

		while (iter.hasNext()) {
			Term term = (Term) iter.next();
			table.put(term.getStem(), new Double(term.getValue()));
		}

		return table;
	}
}