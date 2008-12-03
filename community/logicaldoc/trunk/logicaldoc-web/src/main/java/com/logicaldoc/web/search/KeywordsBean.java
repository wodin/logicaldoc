package com.logicaldoc.web.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.components.SortableList;
import com.logicaldoc.web.document.DocumentRecord;

/**
 * Handles documents access by keywords
 * 
 * @author Marco Meschieri - Logical Objects
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class KeywordsBean extends SortableList {

	protected static Log log = LogFactory.getLog(KeywordsBean.class);

	private Collection<Letter> letters = new ArrayList<Letter>();

	private List<Keyword> keywords = new ArrayList<Keyword>();

	private List<DocumentRecord> documents = new ArrayList<DocumentRecord>();

	private String selectedWord;

	private DocumentHandler dh = new DocumentHandler("xxx");

	// record the last operation requested: letter or keyword
	private String reqop;

	public KeywordsBean() {
		// We don't sort by default
		super("xxx");
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

					reqop = "letter";
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

					reqop = "keyword";
					documents.clear();
					keywords.clear();

					for (Long id : docIds) {
						DocumentRecord record = new DocumentRecord(id, null, null, null);

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

	public String getReqop() {
		return reqop;
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	/**
	 * Sorts the list of Keyword data.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void sort(final String column, final boolean ascending) {

		Comparator comparator = new Comparator() {
			public int compare(Object o1, Object o2) {

				Keyword c1 = (Keyword) o1;
				Keyword c2 = (Keyword) o2;
				if (column == null) {
					return 0;
				}
				if (column.equals("tagName")) {
					return ascending ? c1.getWord().compareTo(c2.getWord()) : c2.getWord().compareTo(c1.getWord());
				} else if (column.equals("documentsCount")) {
					return ascending ? new Integer(c1.getCount()).compareTo(c2.getCount()) : new Integer(c2.getCount())
							.compareTo(c1.getCount());
				} else
					return 0;
			}
		};

		Collections.sort(keywords, comparator);
	}

	public class DocumentHandler extends SortableList {

		protected DocumentHandler(String defaultSortColumn) {
			// We don't sort by default
			super("xxx");
		}

		@Override
		protected boolean isDefaultAscending(String sortColumn) {
			return true;
		}

		@Override
		protected void sort(final String column, final boolean ascending) {

			log.debug("invoked DocumentsRecordsManager.sort()");
			log.debug("sort on column: " + column);
			log.debug("sort ascending: " + ascending);

			Comparator comparator = new Comparator() {
				public int compare(Object o1, Object o2) {

					DocumentRecord c1 = (DocumentRecord) o1;
					DocumentRecord c2 = (DocumentRecord) o2;
					if (column == null) {
						return 0;
					}
					if (column.equals("displayDescription")) {
						return ascending ? c1.getDisplayTitle().compareTo(c2.getDisplayTitle()) : c2.getDisplayTitle()
								.compareTo(c1.getDisplayTitle());
					} else if (column.equals("date")) {
						Date d1 = c1.getLastModified() != null ? c1.getLastModified() : new Date(0);
						Date d2 = c2.getLastModified() != null ? c2.getLastModified() : new Date(0);
						return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
					} else if (column.equals("size")) {
						Long s1 = new Long(c1.getDocument().getFileSize());
						Long s2 = new Long(c2.getDocument().getFileSize());
						return ascending ? s1.compareTo(s2) : s2.compareTo(s1);
					} else
						return 0;
				}
			};

			Collections.sort(documents, comparator);
		}

	}

	public DocumentHandler getDh() {
		return dh;
	}
}