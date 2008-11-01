package com.logicaldoc.core.text.analyzer;

import java.util.Collection;
import java.util.Iterator;

import com.logicaldoc.core.document.Term;
import com.logicaldoc.core.document.dao.TermDAO;

/**
 * Class for analysing texts like extracting keywords from a given text. Created
 * on 24.03.2004
 * 
 * @author Michael Scholz
 */
public class AnalyzerManager {

	private TermDAO termDAO;

	public AnalyzerManager() {
	}

	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}

	/**
	 * This method selects 20 keywords of a given text in a specified language
	 * and stores these keywords in a database.
	 * 
	 * @param docId The document id the text is from.
	 * @param text Text of a document.
	 * @param language Identified language of the text.
	 * @throws Exception
	 */
	public void storeTerms(long docId, String text, String language) throws Exception {
		Analyzer analyzer = AnalyzerFactory.getAnalyzer(language);
		analyzer.analyze(text);

		long words = analyzer.getWordCount();
		Collection<Entry> terms = analyzer.getTopWords(20);
		Iterator<Entry> iter = terms.iterator();

		while (iter.hasNext()) {
			Entry entry = iter.next();
			Term term = new Term();
			term.setDocId(docId);
			term.setStem(entry.getWord());
			term.setValue(entry.getNumber() * 1000 / words);
			term.setWordCount(entry.getNumber());
			term.setOriginWord(entry.getOriginWord());
			termDAO.store(term);
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
	public String getTermsAsString(int count, String text, String language) throws Exception {
		StringBuffer result = new StringBuffer();
		Analyzer analyzer = AnalyzerFactory.getAnalyzer(language);
		analyzer.analyze(text);

		Collection<Entry> terms = analyzer.getTopWords(count);
		Iterator<Entry> iter = terms.iterator();
		int temp = 0;

		while (iter.hasNext() && (temp < count)) {
			Entry entry = iter.next();

			if (temp > 0) {
				result.append(", ");
			}

			result.append(entry.getOriginWord());
			temp++;
		}

		return result.toString();
	}
}