package com.logicaldoc.util;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.util.config.ContextProperties;

/**
 * Utility class for tags handling
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class TagUtil {

	public static final int MAX_FIELD_LENGTH = 4000;

	/**
	 * Detects words in the passed string and creates a list of tags.
	 * <p>
	 * <b>Atention:</b> Recognized tags are words of min. MIN_CHARS chars and
	 * max MAX_CHARS characters.
	 * 
	 * @param words
	 * @return
	 */
	public static Set<String> extractTags(String words) {
		Set<String> coll = new HashSet<String>();
		try {
			ContextProperties conf = new ContextProperties();

			BreakIterator boundary = BreakIterator.getWordInstance();
			boundary.setText(words);

			StringTokenizer st = new StringTokenizer(words, ",", false);
			while (st.hasMoreTokens()) {
				String word = st.nextToken();
				if (StringUtils.isNotEmpty(word)) {
					word = word.trim();
					if (word.length() >= conf.getInt("tag.minsize")) {
						if (word.length() > conf.getInt("tag.maxsize"))
							coll.add(word.substring(0, conf.getInt("tag.maxsize")));
						else
							coll.add(word);
					}
				}
			}

		} catch (IOException e) {
		}
		return coll;
	}

	/**
	 * Normalizes the passed string extracting all tags and producing anither
	 * string containing all tags separated by a comma.
	 * <p>
	 * <b>Atention:</b> The returned string is not more long than
	 * MAX_FIELD_LENGTH characters.
	 * 
	 * @param words The string to be normalized
	 * @return a string in the form "tag1,tag2,tag3"
	 */
	public static String normalizeTags(String words) {
		// Extract tags ad compose the normalized string
		Set<String> tags = extractTags(words);
		StringBuffer sb = new StringBuffer();
		for (String tag : tags) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append(tag);
		}
		String str = sb.toString();

		// Trim the string if too long
		if (str.length() > MAX_FIELD_LENGTH)
			str = str.substring(0, MAX_FIELD_LENGTH);
		return str;
	}
}