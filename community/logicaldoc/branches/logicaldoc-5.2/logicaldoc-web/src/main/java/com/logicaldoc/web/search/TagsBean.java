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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.searchengine.Hit;
import com.logicaldoc.core.searchengine.Search;
import com.logicaldoc.core.searchengine.SearchOptions;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.components.SortableList;
import com.logicaldoc.web.document.DocumentRecord;

/**
 * Handles documents access by tags
 * 
 * @author Marco Meschieri - Logical Objects
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class TagsBean extends SortableList {

	protected static Log log = LogFactory.getLog(TagsBean.class);

	private Collection<Letter> letters = new ArrayList<Letter>();

	private List<Tag> tags = new ArrayList<Tag>();

	private List<DocumentRecord> documents = new ArrayList<DocumentRecord>();

	private String selectedWord;

	private DocumentHandler dh = new DocumentHandler("xxx");

	// record the last operation requested: letter or tag
	private String reqop;

	private String manualchar;

	public TagsBean() {
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

	public Collection<Tag> getTags() {
		return tags;
	}

	public void reset() {
		tags.clear();
	}

	public int getTagsCount() {
		return tags.size();
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
	 * @param tag The word of where we want to show documents
	 */
	public void select(String tag) {
		selectedWord = tag;
		Tag kword = new Tag();
		kword.setWord(tag);
		kword.select();
	}

	/**
	 * Handles the selection of this letter
	 */
	public String showWordsbychar() {
		if (SessionManagement.isValid()) {
			String lett = new String(manualchar).toLowerCase();
			selectWordsByFirstLetter(lett);

			return null;
		} else {
			return "login";
		}
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
				String lett = new String(new char[] { letter }).toLowerCase();
				selectWordsByFirstLetter(lett);

				return null;
			} else {
				return "login";
			}
		}
	}

	/**
	 * Representation of a tag
	 */
	public class Tag {
		private String word;

		private int count;

		public Tag() {
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
		 * Handles the selection of this tag
		 */
		public String select() {
			try {
				selectedWord = word;

				reqop = "tag";
				documents.clear();
				tags.clear();

				SearchOptions opt = new SearchOptions(1);
				opt.setUserId(SessionManagement.getUserId());
				opt.setExpression(word);

				Search lastSearch = Search.get(opt);
				try {
					lastSearch.search();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}

				List<Hit> result = lastSearch.getHits();

				for (Hit res : result) {
					DocumentRecord record = new DocumentRecord(res.getDocId(), null, null);
					if (!documents.contains(record)) {
						documents.add(record);
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

			return null;
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
	 * Sorts the list of Tag data.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void sort(final String column, final boolean ascending) {

		Comparator comparator = new Comparator() {
			public int compare(Object o1, Object o2) {

				Tag c1 = (Tag) o1;
				Tag c2 = (Tag) o2;
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

		Collections.sort(tags, comparator);
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
					if (column.equals("title")) {
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

	public String getManualchar() {
		return manualchar;
	}

	public void setManualchar(String manualchar) {
		this.manualchar = manualchar;
	}

	public void selectWordsByFirstLetter(String lett) {
		try {
			DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			Collection<Object> coll = ddao.findAllTags(lett);
			Iterator<Object> iter = coll.iterator();
			Hashtable<String, Integer> table = new Hashtable<String, Integer>(coll.size());

			while (iter.hasNext()) {
				String tag = (String) iter.next();
				int count = 1;

				if (table.containsKey(tag)) {
					Integer i = (Integer) table.get(tag);
					table.remove(tag);
					count = i.intValue();
					count++;
				}

				table.put(tag, new Integer(count));
			}

			reqop = "letter";
			tags.clear();
			documents.clear();

			Enumeration<String> enum1 = table.keys();

			while (enum1.hasMoreElements()) {
				Tag tag = new Tag();
				String key = enum1.nextElement();
				Integer value = table.get(key);
				tag.setWord(key);
				tag.setCount(value.intValue());
				tags.add(tag);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}