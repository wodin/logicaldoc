package com.logicaldoc.util;

/**
 * Useful class used to filter snippets from lucene, excluding those characters
 * that invalidate HTML page
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class SnippetStripper {

	// Lucene font hilight start tag
	private static final String LUCENE_HILIGHT_START = "&lt;font style='background-color:#FFFF00'&gt;";

	// Lucene font hilight end tag
	private static final String LUCENE_HILIGHT_STOP = "&lt;/font&gt;";

	// Basic latin characters pattern
	private static final String UNICODE_BASIC_LATIN = "\\u0021-\\u007E";

	// Latin-1 characters pattern
	private static final String UNICODE_LATIN_1 = "\\u00A1-\\u00FF";

	// Latin Extended-A characters pattern
	private static final String UNICODE_LATIN_EXTENDED_A = "\\u0100-\\u017F";

	/**
	 * Strips all characters from the input string that may invalidate XML.
	 * Particularly useful for search result summaries
	 * 
	 * @param snippet
	 * @return
	 */
	public static String strip(String snippet) {
		String summary = snippet;
		// Escape all tag delimiters to avoid bad markup in the results page
		summary = summary.replaceAll("<", "&lt;");
		summary = summary.replaceAll(">", "&gt;");

		// But preserve Lucene hilights
		summary = summary.replaceAll(LUCENE_HILIGHT_START, "<font style='background-color:#FFFF00'>");
		summary = summary.replaceAll(LUCENE_HILIGHT_STOP, "</font>");
		String outString = summary;

		// maintain all characters compatible with explorer
		outString = outString.replaceAll("[^" + UNICODE_BASIC_LATIN + UNICODE_LATIN_1 + UNICODE_LATIN_EXTENDED_A + "]",
				" ");

		return outString;
	}
}