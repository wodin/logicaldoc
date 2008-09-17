package com.logicaldoc.core.text;

import java.util.Collection;
import java.util.Iterator;

import com.logicaldoc.core.document.Term;
import com.logicaldoc.core.document.dao.TermDAO;
import com.logicaldoc.core.text.analyze.Analyzer;
import com.logicaldoc.core.text.analyze.AnalyzerFactory;
import com.logicaldoc.core.text.analyze.Entry;
import com.logicaldoc.util.Context;

/**
 * Class for analysing texts like extracting keywords from a given text. 
 * Created on 24.03.2004
 * 
 * @author Michael Scholz
 */
public class AnalyzeText {

	public AnalyzeText() {
	}

	/**
	 * This method selects 20 keywords of a given text in a specified language
	 * and stores these keywords in a database.
	 * 
	 * @param menuId MenuId of the document the text is from.
	 * @param text Text of a document.
	 * @param language Identified language of the text.
	 * @throws Exception
	 */
	public void storeTerms(int menuId, String text, String language) throws Exception {
		TermDAO termDao = (TermDAO) Context.getInstance().getBean(TermDAO.class);
		Analyzer analyzer = AnalyzerFactory.getAnalyzer(language);
		analyzer.analyze(text);

		long words = analyzer.getWordCount();
		Collection terms = analyzer.getTopWords(20);
		Iterator iter = terms.iterator();

		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			Term term = new Term();
			term.setMenuId(menuId);
			term.setStem(entry.getWord());
			term.setValue(entry.getNumber() * 1000 / words);
			term.setWordCount(entry.getNumber());
			term.setOriginWord(entry.getOriginWord());
			termDao.store(term);
		}
	}

	/**
	 * This method extracts a specified number of keywords and appends them to a
	 * String
	 * 
	 * @param count Number of keywords.
	 * @param text Given text of a document.
	 * @param language Identified language of the text.
	 * @return String of keywords like "Information, Retrieval, DMS, CMS"
	 * @throws Exception
	 */
	public String getTerms(int count, String text, String language) throws Exception {
		StringBuffer result = new StringBuffer();
		Analyzer analyzer = AnalyzerFactory.getAnalyzer(language);
		analyzer.analyze(text);

		Collection terms = analyzer.getTopWords(count);
		Iterator iter = terms.iterator();
		int temp = 0;

		while (iter.hasNext() && (temp < count)) {
			Entry entry = (Entry) iter.next();

			if (temp > 0) {
				result.append(", ");
			}

			result.append(entry.getOriginWord());
			temp++;
		}

		return result.toString();
	}
}
