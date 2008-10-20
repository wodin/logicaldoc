package com.logicaldoc.web.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.document.DocumentRecord;

/**
 * Handles documents access by keywords
 * 
 * @author Marco Meschieri - Logical Objects
 * @version $Id: KeywordsBean.java,v 1.2 2007/08/22 14:12:20 marco Exp $
 * @since ###release###
 */
public class KeywordsBean {
	protected static Log log = LogFactory.getLog(KeywordsBean.class);

	private Collection<Letter> letters = new ArrayList<Letter>();

	private Collection<Keyword> keywords = new ArrayList<Keyword>();

	private Collection<DocumentRecord> documents = new ArrayList<DocumentRecord>();

	private String selectedWord;

	public KeywordsBean() {
		String str = "abcdefghijklmnopqrstuvwxyz";

		for (int i = 0; i < str.length(); i++) {
			letters.add(new Letter(str.charAt(i)));
		}
	}

	public Collection<Letter> getLetters() {
		return letters;
	}

	public Collection<Keyword> getKeywords() {
		return keywords;
	}

	public void reset() {
		keywords.clear();
	}

	public int getKeywordsCount() {
		return keywords.size();
	}

	public int getDocumentsCount() {
		return documents.size();
	}

	public Collection<DocumentRecord> getDocuments() {
		return documents;
	}

	public String getSelectedWord() {
		return selectedWord;
	}

	/**
	 * Set selectedWord and show documents marked with that word.
	 * 
	 * @param keyword The word of where we want to show documents
	 */
	public void select(String keyword) {
		selectedWord = keyword;
		Keyword kword = new Keyword();
		kword.setWord(keyword);
		kword.select();
	}

	/**
	 * Representation of a letter
	 */
	public class Letter {
		char letter;

		public Letter(char letter) {
			super();
			this.letter = letter;
		}

		public String getLetter() {
			return new String(new char[] { letter }).toUpperCase();
		}

		/**
		 * Handles the selection of this letter
		 */
		public String select() {
			if (SessionManagement.isValid()) {
				try {
					String lett = new String(new char[] { letter }).toLowerCase();

					long userId = SessionManagement.getUserId();
					DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
					Collection<String> coll = ddao.findKeywords(lett, userId);
					Iterator<String> iter = coll.iterator();
					Hashtable<String, Integer> table = new Hashtable<String, Integer>(coll.size());

					while (iter.hasNext()) {
						String keyword = iter.next();
						int count = 1;

						if (table.containsKey(keyword)) {
							Integer i = (Integer) table.get(keyword);
							table.remove(keyword);
							count = i.intValue();
							count++;
						}

						table.put(keyword, new Integer(count));
					}

					keywords.clear();
					documents.clear();

					Enumeration<String> enum1 = table.keys();

					while (enum1.hasMoreElements()) {
						Keyword keyword = new Keyword();
						String key = enum1.nextElement();
						Integer value = table.get(key);
						keyword.setWord(key);
						keyword.setCount(value.intValue());
						keywords.add(keyword);
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}

				return null;
			} else {
				return "login";
			}
		}
	}

	/**
	 * Representation of a keyword
	 */
	public class Keyword {
		private String word;

		private int count;

		public Keyword() {
			word = "";
			count = 0;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		/**
		 * Handles the selection of this keyword
		 */
		public String select() {
			if (SessionManagement.isValid()) {
				try {
					selectedWord = word;

					long userId = SessionManagement.getUserId();
					DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
					Set<Long> docIds = docDao.findDocIdByUserIdAndKeyword(userId, word);
					documents.clear();
					keywords.clear();

					for (Long id : docIds) {
						DocumentRecord record;

						record = new DocumentRecord(id, null, null, null);

						if (!documents.contains(record)) {
							documents.add(record);
						}
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}

				return null;
			} else {
				return "login";
			}
		}
	}
}
