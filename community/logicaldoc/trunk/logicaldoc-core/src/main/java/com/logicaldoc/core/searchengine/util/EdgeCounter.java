package com.logicaldoc.core.searchengine.util;

import java.util.Collection;
import java.util.Iterator;

public class EdgeCounter {

	public static int count(Collection<TermEntry> keywords) {
		Iterator<TermEntry> iter = keywords.iterator();
		int count = 0;

		while (iter.hasNext()) {
			try {
				TermEntry entry = (TermEntry) iter.next();
				count += entry.getDocuments().size();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return count;
	}
}