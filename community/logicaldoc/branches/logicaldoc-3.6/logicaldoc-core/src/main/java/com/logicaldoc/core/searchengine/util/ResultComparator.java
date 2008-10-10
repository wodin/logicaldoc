package com.logicaldoc.core.searchengine.util;

import java.util.Comparator;

import com.logicaldoc.core.searchengine.Result;

/**
 * Useful comparator for results
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 */
public class ResultComparator implements Comparator<Result> {

	public ResultComparator() {
	}

	public int compare(Result sr1, Result sr2) {
		Integer d1 = sr1.getScore();
		Integer d2 = sr2.getScore();
		return -1 * d1.compareTo(d2);
	}
}