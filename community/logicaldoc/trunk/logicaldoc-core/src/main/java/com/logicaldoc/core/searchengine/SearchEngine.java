package com.logicaldoc.core.searchengine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CheckIndex.Status;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.update.SolrIndexWriter;

import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.parser.Parser;
import com.logicaldoc.core.parser.ParserFactory;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.io.FileUtil;

/**
 * Facade on the search engine.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class SearchEngine {

	protected static final String FIELD_TEMPLATE_ID = "templateId";

	protected static final String FIELD_TAGS = "tags";

	protected static final String FIELD_CONTENT = "content";

	protected static final String FIELD_FOLDER_ID = "folderId";

	protected static final String FIELD_FOLDER_NAME = "folderName";

	protected static final String FIELD_CREATION = "creation";

	protected static final String FIELD_DATE = "date";

	protected static final String FIELD_SOURCE_DATE = "sourceDate";

	protected static final String FIELD_COVERAGE = "coverage";

	protected static final String FIELD_SOURCE_AUTHOR = "sourceAuthor";

	protected static final String FIELD_SOURCE = "source";

	protected static final String FIELD_SIZE = "size";

	protected static final String FIELD_TITLE = "title";

	protected static final String FIELD_ID = "id";

	protected static final String FIELD_CUSTOM_ID = "customId";

	protected static final String FIELD_DOCREF = "docRef";

	protected static final String FIELD_COMMENT = "comment";

	protected static final String FIELD_LANGUAGE = "language";

	protected static final String FIELD_TYPE = "type";

	protected static Log log = LogFactory.getLog(SearchEngine.class);

	private static ContextProperties config;

	private DocumentDAO documentDao;

	protected EmbeddedSolrServer server;

	private SearchEngine() {
	}

	public void setConfig(ContextProperties config) {
		SearchEngine.config = config;
	}

	/**
	 * Adds a new Hit into the index
	 * 
	 * @param document
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public synchronized void addHit(Document document, String content) throws Exception {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(FIELD_ID, Long.toString(document.getId()));
		doc.addField(FIELD_LANGUAGE, document.getLanguage());
		doc.addField(FIELD_TITLE, document.getTitle());
		doc.addField(FIELD_SIZE, document.getFileSize());
		doc.addField(FIELD_DATE, document.getDate());
		doc.addField(FIELD_SOURCE_DATE, document.getSourceDate());
		doc.addField(FIELD_CREATION, document.getCreation());
		doc.addField(FIELD_CUSTOM_ID, document.getCustomId());
		doc.addField(FIELD_SOURCE, document.getSource());
		doc.addField(FIELD_COMMENT, document.getComment());
		doc.addField(FIELD_TAGS, document.getTagsString());
		doc.addField(FIELD_COVERAGE, document.getCoverage());
		doc.addField(FIELD_SOURCE_AUTHOR, document.getSourceAuthor());
		doc.addField(FIELD_DOCREF, document.getDocRef());
		doc.addField(FIELD_TYPE, FilenameUtils.getExtension(document.getFileName()));
		doc.addField(FIELD_CONTENT, content);

		if (document.getFolder() != null) {
			doc.addField(FIELD_FOLDER_ID, document.getFolder().getId());
			doc.addField(FIELD_FOLDER_NAME, document.getFolder().getName());
		}

		if (document.getTemplateId() != null) {
			doc.addField(FIELD_TEMPLATE_ID, document.getTemplateId());

			for (String attribute : document.getAttributeNames()) {
				ExtendedAttribute ext = document.getExtendedAttribute(attribute);
				// Skip all non-string attributes
				if (ext.getType() == ExtendedAttribute.TYPE_STRING && StringUtils.isNotEmpty(ext.getStringValue())) {
					// Prefix all extended attributes with 'ext_' in order to
					// avoid collisions with standard fields
					doc.addField("ext_" + attribute, ext.getStringValue());
				}
			}
		}

		server.add(doc);
		server.commit();
	}

	/**
	 * Adds a new hit to the index
	 * 
	 * @param content Stream of the document's file
	 * @param document The document that we want to add
	 * @throws Exception
	 */
	public synchronized void addHit(Document document, InputStream content) throws Exception {
		Document doc = document;
		Locale locale = doc.getLocale();
		if (locale == null)
			locale = Locale.ENGLISH;
		Parser parser = ParserFactory.getParser(content, doc.getFileName(), locale, null);
		if (parser == null) {
			return;
		}

		String contentString = parser.getContent();

		if (doc.getDocRef() != null) {
			// This is a shortcut
			doc = documentDao.findById(doc.getDocRef());
			documentDao.initialize(doc);
			doc = (com.logicaldoc.core.document.Document) doc.clone();
			doc.setId(document.getId());
			doc.setDocRef(document.getDocRef());
		}

		if (log.isInfoEnabled()) {
			log.info("addHit " + doc.getId() + " " + doc.getTitle() + " " + doc.getFileVersion() + " "
					+ doc.getPublisher() + " " + doc.getStatus() + " " + doc.getSource() + " " + doc.getSourceAuthor());
		}

		addHit(doc, contentString);
	}

	/**
	 * Launch the index optimization and triggers the build of spellcheck
	 * dictionary
	 */
	public synchronized void optimize() {
		log.warn("Started optimization of the index");
		try {
			server.optimize(true, true);
		} catch (Exception e) {
			log.error("Error during optimization: " + e.getMessage(), e);
		}
		log.warn("Finished optimization of the index");
	}

	/**
	 * Launch the check on the Index
	 */
	public String check() {
		log.warn("Checking index");

		ByteArrayOutputStream baos = null;
		String statMsg = "";
		try {
			CheckIndex ci = new CheckIndex(getIndexDataDirectory());

			// Prepare the output buffer
			baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);

			// Retrieve the status collecting all informations in a string
			Status status = null;
			ci.setInfoStream(ps);
			try {
				status = ci.checkIndex();
			} catch (Exception e) {
				ps.println("ERROR: caught exception, giving up.\n\n");
				log.error(e.getMessage());
			}

			// Elaborate the status showing needed informations
			if (status != null) {
				if (status.clean) {
					statMsg = "OK\n";
				} else if (status.toolOutOfDate) {
					statMsg = "ERROR: Can't check - tool out-of-date\n";
				} else {
					statMsg = "BAD: ";
					if (status.cantOpenSegments) {
						statMsg += "cantOpenSegments ";
					}
					if (status.missingSegments) {
						statMsg += "missingSegments ";
					}
					if (status.missingSegmentVersion) {
						statMsg += "missingSegVersion ";
					}
					if (status.numBadSegments > 0) {
						statMsg += "numBadSegments=" + status.numBadSegments + " ";
					}
					if (status.totLoseDocCount > 0) {
						statMsg += "lostDocCount=" + status.totLoseDocCount + " ";
					}
				}

				String content = "";
				try {
					content = baos.toString("UTF-8");
				} catch (UnsupportedEncodingException e) {
				}

				statMsg += "\n" + content;
			}
		} catch (Throwable t) {
			log.error(t.getMessage());
		} finally {
			if (baos != null)
				try {
					baos.close();
				} catch (IOException e) {

				}
		}
		log.warn("Finished checking index");
		return statMsg;
	}

	/**
	 * Deletes an hit in the index of the search engine.
	 * 
	 * @param id - id of the hit
	 */
	public synchronized void deleteHit(long id) {
		try {
			server.deleteById(Long.toString(id));
			server.commit();
		} catch (Exception e) {
			log.warn("Unable to delete hit " + id, e);
		}
	}

	/**
	 * Removed all passed hits from the index
	 * 
	 * @param ids Collection of hit identifiers
	 */
	public synchronized void deleteHits(Collection<Long> ids) {
		try {
			List<String> list = new ArrayList<String>();
			for (Long i : ids) {
				list.add(Long.toString(i));
			}
			server.deleteById(list);
			server.commit();
		} catch (Exception e) {
			log.warn("Unable to delete some hits " + ids, e);
		}
	}

	public Hit getHit(long id) {
		SolrQuery query = new SolrQuery();
		query.setQuery("id:" + id);
		try {
			QueryResponse rsp = server.query(query);
			SolrDocumentList docs = rsp.getResults();
			if (docs.size() < 1)
				return null;

			SolrDocument doc = docs.get(0);
			Hit hit = Hits.toHit(doc);
			hit.setContent((String) doc.getFieldValue(FIELD_CONTENT));
			return hit;
		} catch (SolrServerException e) {
			log.error(e);
		}
		return null;
	}

	/**
	 * Search for hits
	 */
	public Hits search(String expression, String[] filters, String expressionLanguage, Integer rows) {
		WordDelimiterAnalyzer.lang.set(expressionLanguage);
		Hits hits = null;
		SolrQuery query = new SolrQuery();
		query.setFields("*");
		query.setQuery(expression);
		query.setIncludeScore(true);
		query.setSortField("score", ORDER.desc);
		query.setHighlight(true);
		query.addHighlightField("content");
		query.setHighlightSnippets(4);
		query.setParam("hl.mergeContiguous", true);
		query.setTermsMaxCount(1000);

		if (rows != null)
			query.setRows(rows);

		if (filters != null)
			query.addFilterQuery(filters);

		try {
			log.info("Execute search: " + expression);
			QueryResponse rsp = server.query(query);
			hits = new Hits(rsp);
		} catch (SolrServerException e) {
			log.error(e);
		}
		return hits;
	}

	/**
	 * Close all indexing operations, shuts down the engine.
	 */
	public synchronized void close() {
		log.warn("Closing the indexer");
		Field field;
		try {
			field = EmbeddedSolrServer.class.getDeclaredField("coreContainer");
			field.setAccessible(true);
			CoreContainer container = (CoreContainer) field.get(server);
			container.shutdown();
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}
	}

	/**
	 * This method can unlock a locked index.
	 */
	public synchronized void unlock() {
		try {
			Directory directory = getIndexDataDirectory();
			if (SolrIndexWriter.isLocked(directory)) {
				SolrIndexWriter.unlock(directory);
			}

			directory = getSpellcheckerDataDirectory();
			if (SolrIndexWriter.isLocked(directory)) {
				SolrIndexWriter.unlock(directory);
			}
		} catch (Exception e) {
			log.warn("unlock " + e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Check if at least one index is locked
	 * 
	 * @return true if one or more indexes are locked
	 */
	public boolean isLocked() {
		boolean result = false;

		try {
			Directory directory = getIndexDataDirectory();
			if (SolrIndexWriter.isLocked(directory)) {
				result = true;
			}
		} catch (Exception e) {
			log.warn("isLocked " + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * Returns the number of indexed documents in all indexes. Used for
	 * statistical output.
	 */
	public long getCount() {
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		try {
			QueryResponse rsp = server.query(query);
			SolrDocumentList docs = rsp.getResults();
			return docs.getNumFound();
		} catch (SolrServerException e) {
			log.error(e);
		}
		return 0;
	}

	/**
	 * Drops the fulltext index
	 */
	public void dropIndexes() {
		try {
			close();
			FileUtils.deleteDirectory(getIndexDataFolder());
			FileUtils.deleteDirectory(getSpellcheckerDataFolder());
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static Directory getIndexDataDirectory() throws IOException {
		return new NIOFSDirectory(getIndexDataFolder());
	}

	static Directory getSpellcheckerDataDirectory() throws IOException {
		File indexdir = getSpellcheckerDataFolder();
		return new NIOFSDirectory(indexdir);
	}

	static File getIndexDataFolder() throws IOException {
		File indexdir = new File(config.getPropertyWithSubstitutions("conf.indexdir"));
		indexdir = new File(indexdir, "data");
		return new File(indexdir, "index");
	}

	static File getSpellcheckerDataFolder() throws IOException {
		File indexdir = new File(config.getPropertyWithSubstitutions("conf.indexdir"));
		indexdir = new File(indexdir, "data");
		return new File(indexdir, "spellchecker");
	}

	/**
	 * To be called on the context startup, this method creates all indexes and
	 * unlock the existing ones
	 */
	public void init() {
		try {
			File home = new File(config.getPropertyWithSubstitutions("conf.indexdir"));
			File solr_xml = new File(home, "solr.xml");

			if (!home.exists()) {
				home.mkdirs();
				home.mkdir();
			}
			if (!solr_xml.exists()) {
				FileUtil.copyResource("/index/solr.xml", solr_xml);
			}

			File conf = new File(config.getPropertyWithSubstitutions("conf.indexdir"));
			conf = new File(conf, "conf");
			if (!conf.exists()) {
				conf.mkdirs();
				conf.mkdir();
			}
			File solrconfig_xml = new File(conf, "solrconfig.xml");
			if (!solrconfig_xml.exists()) {
				FileUtil.copyResource("/index/conf/solrconfig.xml", solrconfig_xml);
			}
			File schema_xml = new File(conf, "schema.xml");
			if (!schema_xml.exists()) {
				FileUtil.copyResource("/index/conf/schema.xml", schema_xml);
			}
			File synonyms_txt = new File(conf, "synonyms.txt");
			if (!synonyms_txt.exists()) {
				FileUtil.copyResource("/index/conf/synonyms.txt", synonyms_txt);
			}
			File protwords_txt = new File(conf, "protwords.txt");
			if (!protwords_txt.exists()) {
				FileUtil.copyResource("/index/conf/protwords.txt", protwords_txt);
			}

			CoreContainer container = new CoreContainer();
			container.load(home.getPath(), solr_xml);
			server = new EmbeddedSolrServer(container, "");
			unlock();
		} catch (Exception e) {
			log.error("Unable to initialize the Full-text search engine", e);
		}
	}

	public void setDocumentDao(DocumentDAO documentDao) {
		this.documentDao = documentDao;
	}
}