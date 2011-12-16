package com.logicaldoc.core.searchengine;

/**
 * A BreakIterator-like API for iterating over subwords in text, according to
 * WordDelimiterFilter rules.
 */
final class WordDelimiterIterator {

	/** Indicates the end of iteration */
	public static final int DONE = -1;

	public static final byte[] DEFAULT_WORD_DELIM_TABLE;

	char text[];

	int length;

	/** start position of text, excluding leading delimiters */
	int startBounds;

	/** end position of text, excluding trailing delimiters */
	int endBounds;

	/** Beginning of subword */
	int current;

	/** End of subword */
	int end;

	/* does this string end with a possessive such as 's */
	private boolean hasFinalPossessive = false;

	/**
	 * If false, causes case changes to be ignored (subwords will only be
	 * generated given SUBWORD_DELIM tokens). (Defaults to true)
	 */
	final boolean splitOnCaseChange;

	/**
	 * If false, causes numeric changes to be ignored (subwords will only be
	 * generated given SUBWORD_DELIM tokens). (Defaults to true)
	 */
	final boolean splitOnNumerics;

	/**
	 * If true, causes trailing "'s" to be removed for each subword. (Defaults
	 * to true)
	 * <p/>
	 * "O'Neil's" => "O", "Neil"
	 */
	final boolean stemEnglishPossessive;

	private final byte[] charTypeTable;

	/** if true, need to skip over a possessive found in the last call to next() */
	private boolean skipPossessive = false;

	// Should there be a WORD_DELIM category for chars that only separate
	// words (no catenation of subwords will be
	// done if separated by these chars?) "," would be an obvious candidate...
	static {
		byte[] tab = new byte[256];
		for (int i = 0; i < 256; i++) {
			byte code = 0;
			if (Character.isLowerCase(i)) {
				code |= WordDelimiterFilter.LOWER;
			} else if (Character.isUpperCase(i)) {
				code |= WordDelimiterFilter.UPPER;
			} else if (Character.isDigit(i)) {
				code |= WordDelimiterFilter.DIGIT;
			}
			if (code == 0) {
				code = WordDelimiterFilter.SUBWORD_DELIM;
			}
			tab[i] = code;
		}
		DEFAULT_WORD_DELIM_TABLE = tab;
	}

	/**
	 * Create a new WordDelimiterIterator operating with the supplied rules.
	 * 
	 * @param charTypeTable table containing character types
	 * @param splitOnCaseChange if true, causes "PowerShot" to be two tokens;
	 *        ("Power-Shot" remains two parts regards)
	 * @param splitOnNumerics if true, causes "j2se" to be three tokens; "j" "2"
	 *        "se"
	 * @param stemEnglishPossessive if true, causes trailing "'s" to be removed
	 *        for each subword: "O'Neil's" => "O", "Neil"
	 */
	WordDelimiterIterator(byte[] charTypeTable, boolean splitOnCaseChange, boolean splitOnNumerics,
			boolean stemEnglishPossessive) {
		this.charTypeTable = charTypeTable;
		this.splitOnCaseChange = splitOnCaseChange;
		this.splitOnNumerics = splitOnNumerics;
		this.stemEnglishPossessive = stemEnglishPossessive;
	}

	/**
	 * Advance to the next subword in the string.
	 * 
	 * @return index of the next subword, or {@link #DONE} if all subwords have
	 *         been returned
	 */
	int next() {
		current = end;
		if (current == DONE) {
			return DONE;
		}

		if (skipPossessive) {
			current += 2;
			skipPossessive = false;
		}

		int lastType = 0;

		while (current < endBounds && (WordDelimiterFilter.isSubwordDelim(lastType = charType(text[current])))) {
			current++;
		}

		if (current >= endBounds) {
			return end = DONE;
		}

		for (end = current + 1; end < endBounds; end++) {
			int type = charType(text[end]);
			if (isBreak(lastType, type)) {
				break;
			}
			lastType = type;
		}

		if (end < endBounds - 1 && endsWithPossessive(end + 2)) {
			skipPossessive = true;
		}

		return end;
	}

	/**
	 * Return the type of the current subword. This currently uses the type of
	 * the first character in the subword.
	 * 
	 * @return type of the current word
	 */
	int type() {
		if (end == DONE) {
			return 0;
		}

		int type = charType(text[current]);
		switch (type) {
		// return ALPHA word type for both lower and upper
		case WordDelimiterFilter.LOWER:
		case WordDelimiterFilter.UPPER:
			return WordDelimiterFilter.ALPHA;
		default:
			return type;
		}
	}

	/**
	 * Reset the text to a new value, and reset all state
	 * 
	 * @param text New text
	 * @param length length of the text
	 */
	void setText(char text[], int length) {
		this.text = text;
		this.length = this.endBounds = length;
		current = startBounds = end = 0;
		skipPossessive = hasFinalPossessive = false;
		setBounds();
	}

	// ================================================= Helper Methods
	// ================================================

	/**
	 * Determines whether the transition from lastType to type indicates a break
	 * 
	 * @param lastType Last subword type
	 * @param type Current subword type
	 * @return {@code true} if the transition indicates a break, {@code false}
	 *         otherwise
	 */
	private boolean isBreak(int lastType, int type) {
		if ((type & lastType) != 0) {
			return false;
		}

		if (!splitOnCaseChange && WordDelimiterFilter.isAlpha(lastType) && WordDelimiterFilter.isAlpha(type)) {
			// ALPHA->ALPHA: always ignore if case isn't considered.
			return false;
		} else if (WordDelimiterFilter.isUpper(lastType) && WordDelimiterFilter.isAlpha(type)) {
			// UPPER->letter: Don't split
			return false;
		} else if (!splitOnNumerics
				&& ((WordDelimiterFilter.isAlpha(lastType) && WordDelimiterFilter.isDigit(type)) || (WordDelimiterFilter
						.isDigit(lastType) && WordDelimiterFilter.isAlpha(type)))) {
			// ALPHA->NUMERIC, NUMERIC->ALPHA :Don't split
			return false;
		}

		return true;
	}

	/**
	 * Determines if the current word contains only one subword. Note, it could
	 * be potentially surrounded by delimiters
	 * 
	 * @return {@code true} if the current word contains only one subword,
	 *         {@code false} otherwise
	 */
	boolean isSingleWord() {
		if (hasFinalPossessive) {
			return current == startBounds && end == endBounds - 2;
		} else {
			return current == startBounds && end == endBounds;
		}
	}

	/**
	 * Set the internal word bounds (remove leading and trailing delimiters).
	 * Note, if a possessive is found, don't remove it yet, simply note it.
	 */
	private void setBounds() {
		while (startBounds < length && (WordDelimiterFilter.isSubwordDelim(charType(text[startBounds])))) {
			startBounds++;
		}

		while (endBounds > startBounds && (WordDelimiterFilter.isSubwordDelim(charType(text[endBounds - 1])))) {
			endBounds--;
		}
		if (endsWithPossessive(endBounds)) {
			hasFinalPossessive = true;
		}
		current = startBounds;
	}

	/**
	 * Determines if the text at the given position indicates an English
	 * possessive which should be removed
	 * 
	 * @param pos Position in the text to check if it indicates an English
	 *        possessive
	 * @return {@code true} if the text at the position indicates an English
	 *         posessive, {@code false} otherwise
	 */
	private boolean endsWithPossessive(int pos) {
		return (stemEnglishPossessive && pos > 2 && text[pos - 2] == '\''
				&& (text[pos - 1] == 's' || text[pos - 1] == 'S')
				&& WordDelimiterFilter.isAlpha(charType(text[pos - 3])) && (pos == endBounds || WordDelimiterFilter
				.isSubwordDelim(charType(text[pos]))));
	}

	/**
	 * Determines the type of the given character
	 * 
	 * @param ch Character whose type is to be determined
	 * @return Type of the character
	 */
	private int charType(int ch) {
		if (ch < charTypeTable.length) {
			return charTypeTable[ch];
		}
		switch (Character.getType(ch)) {
		case Character.UPPERCASE_LETTER:
			return WordDelimiterFilter.UPPER;
		case Character.LOWERCASE_LETTER:
			return WordDelimiterFilter.LOWER;

		case Character.TITLECASE_LETTER:
		case Character.MODIFIER_LETTER:
		case Character.OTHER_LETTER:
		case Character.NON_SPACING_MARK:
		case Character.ENCLOSING_MARK: // depends what it encloses?
		case Character.COMBINING_SPACING_MARK:
			return WordDelimiterFilter.ALPHA;

		case Character.DECIMAL_DIGIT_NUMBER:
		case Character.LETTER_NUMBER:
		case Character.OTHER_NUMBER:
			return WordDelimiterFilter.DIGIT;

			// case Character.SPACE_SEPARATOR:
			// case Character.LINE_SEPARATOR:
			// case Character.PARAGRAPH_SEPARATOR:
			// case Character.CONTROL:
			// case Character.FORMAT:
			// case Character.PRIVATE_USE:

		case Character.SURROGATE: // prevent splitting
			return WordDelimiterFilter.ALPHA | WordDelimiterFilter.DIGIT;

			// case Character.DASH_PUNCTUATION:
			// case Character.START_PUNCTUATION:
			// case Character.END_PUNCTUATION:
			// case Character.CONNECTOR_PUNCTUATION:
			// case Character.OTHER_PUNCTUATION:
			// case Character.MATH_SYMBOL:
			// case Character.CURRENCY_SYMBOL:
			// case Character.MODIFIER_SYMBOL:
			// case Character.OTHER_SYMBOL:
			// case Character.INITIAL_QUOTE_PUNCTUATION:
			// case Character.FINAL_QUOTE_PUNCTUATION:

		default:
			return WordDelimiterFilter.SUBWORD_DELIM;
		}
	}
}
