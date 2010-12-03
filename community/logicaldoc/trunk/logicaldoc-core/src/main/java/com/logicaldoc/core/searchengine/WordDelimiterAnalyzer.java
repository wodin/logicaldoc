package com.logicaldoc.core.searchengine;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Fieldable;

/**
 * This analyzer is a wrapper to be used to allow searches in subwords tokens,
 * by using the <code>WordDelimiterFilter</code>.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WordDelimiterAnalyzer extends Analyzer {

	private Analyzer wrapped;

	public WordDelimiterAnalyzer(Analyzer wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream ts = wrapped.tokenStream(fieldName, reader);
		ts = new WordDelimiterFilter(ts, 1, 1, 1, 1, 1, 1, 1, 1, 1, null);
		return ts;
	}

	public void close() {
		wrapped.close();
	}

	public boolean equals(Object obj) {
		return wrapped.equals(obj);
	}

	public int getOffsetGap(Fieldable field) {
		return wrapped.getOffsetGap(field);
	}

	public int getPositionIncrementGap(String fieldName) {
		return wrapped.getPositionIncrementGap(fieldName);
	}

	public int hashCode() {
		return wrapped.hashCode();
	}

	public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
		TokenStream ts = wrapped.tokenStream(fieldName, reader);
		ts = new WordDelimiterFilter(ts, 1, 1, 1, 1, 1, 1, 1, 1, 1, null);
		return ts;
	}

	public String toString() {
		return wrapped.toString();
	}
}
