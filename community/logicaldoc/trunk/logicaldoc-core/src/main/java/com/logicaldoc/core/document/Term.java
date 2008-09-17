package com.logicaldoc.core.document;

/**
 * @author Michael Scholz
 * @author Marco Meschieri
 */
public class Term {

	private double value = 0.0;

	private int wordCount = 0;

	private String originWord = "";

	private TermID id = new TermID();

	public Term() {
	}

	public TermID getId() {
		return id;
	}

	public void setId(TermID id) {
		this.id = id;
	}

	public String getStem() {
		return getId().getStem();
	}

	public double getValue() {
		return value;
	}

	public void setStem(String string) {
		getId().setStem(string);
	}

	public void setValue(double d) {
		value = d;
	}

	public int getMenuId() {
		return getId().getMenuId();
	}

	public void setMenuId(int i) {
		getId().setMenuId(i);
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordcount) {
		this.wordCount = wordcount;
	}

	public String getOriginWord() {
		return originWord;
	}

	public void setOriginWord(String originWord) {
		this.originWord = originWord;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Term))
			return false;

		Term other = (Term) obj;
		return other.getId().equals(this.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public String toString() {
		return id.toString();
	}
}