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
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CheckIndex.Status;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.update.SolrIndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.parser.Parser;
import com.logicaldoc.core.parser.ParserFactory;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.io.FileUtil;

/**
 * Standard implementation that implements a local search engine
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class StandardSearchEngine implements SearchEngine {

	protected static Logger log = LoggerFactory.getLogger(StandardSearchEngine.class);

	private static ContextProperties config;

	protected DocumentDAO documentDao;

	protected EmbeddedSolrServer server;

	protected StandardSearchEngine() {
	}

	public void setConfig(ContextProperties config) {
		StandardSearchEngine.config = config;
	}

	public void setDocumentDao(DocumentDAO documentDao) {
		this.documentDao = documentDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.logicaldoc.core.searchengine.SearchEngine#addHit(com.logicaldoc.core
	 * .document.Document, java.lang.String)
	 */
	@Override
	public synchronized void addHit(Document document, String content) throws Exception {
		SolrInputDocument doc = new SolrInputDocument();

		doc.addField(Fields.ID.getName(), Long.toString(document.getId()));
		doc.addField(Fields.LANGUAGE.getName(), document.getLanguage());
		doc.addField(Fields.TITLE.getName(), document.getTitle());
		doc.addField(Fields.SIZE.getName(), document.getFileSize());
		doc.addField(Fields.DATE.getName(), document.getDate());
		doc.addField(Fields.SOURCE_DATE.getName(), document.getSourceDate());
		doc.addField(Fields.SOURCE_ID.getName(), document.getSourceId());
		doc.addField(Fields.RECIPIENT.getName(), document.getRecipient());
		doc.addField(Fields.CREATION.getName(), document.getCreation());
		doc.addField(Fields.CUSTOM_ID.getName(), document.getCustomId());
		doc.addField(Fields.SOURCE.getName(), document.getSource());
		doc.addField(Fields.COMMENT.getName(), document.getComment());
		doc.addField(Fields.TAGS.getName(), document.getTagsString());
		doc.addField(Fields.COVERAGE.getName(), document.getCoverage());
		doc.addField(Fields.SOURCE_AUTHOR.getName(), document.getSourceAuthor());
		doc.addField(Fields.SOURCE_TYPE.getName(), document.getSourceType());
		doc.addField(Fields.DOC_REF.getName(), document.getDocRef());

		int maxText = -1;
		if (StringUtils.isNotEmpty(config.getProperty("index.maxtext"))) {
			try {
				maxText = config.getInt("index.maxtext");
			} catch (Exception e) {
			}
		}

		if (maxText > 0 && content.length() > maxText)
			doc.addField(Fields.CONTENT.getName(), StringUtils.substring(content, 0, maxText));
		else
			doc.addField(Fields.CONTENT.getName(), content);

		if (document.getFolder() != null) {
			doc.addField(Fields.FOLDER_ID.getName(), document.getFolder().getId());
			doc.addField(Fields.FOLDER_NAME.getName(), document.getFolder().getName());
		}

		if (document.getTemplateId() != null) {
			doc.addField(Fields.TEMPLATE_ID.getName(), document.getTemplateId());

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.logicaldoc.core.searchengine.SearchEngine#addHit(com.logicaldoc.core
	 * .document.Document, java.io.InputStream)
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.logicaldoc.core.searchengine.SearchEngine#optimize()
	 */
	@Override
	public synchronized void optimize() {
		log.warn("Started optimization of the index");
		try {
			server.optimize(true, true);
		} catch (Exception e) {
			log.error("Error during optimization: " + e.getMessage(), e);
		}
		log.warn("Finished optimization of the index");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.logicaldoc.core.searchengine.SearchEngine#check()
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.logicaldoc.core.searchengine.SearchEngine#deleteHit(long)
	 */
	@Override
	public synchronized void deleteHit(long id) {
		try {
			server.deleteById(Long.toString(id));
			server.commit();
		} catch (Throwable e) {
			log.debug("Unable to delete hit " + id, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.logicaldoc.core.searchengine.SearchEngine#deleteHits(java.util.Collection
	 * )
	 */
	@Override
	public synchronized void deleteHits(Collection<Long> ids) {
		try {
			List<String> list = new ArrayList<String>();
			for (Long i : ids) {
				list.add(Long.toString(i));
			}
			server.deleteById(list);
			server.commit();
		} catch (Throwable e) {
			log.debug("Unable to delete some hits " + ids, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.logicaldoc.core.searchengine.SearchEngine#getHit(long)
	 */
	@Override
	public Hit getHit(long id) {
		SolrQuery query = new SolrQuery();
		query.setQuery("id:" + id);
		query.setFields("*");
		try {
			QueryResponse rsp = server.query(query);
			SolrDocumentList docs = rsp.getResults();
			if (docs.size() < 1)
				return null;

			SolrDocument doc = docs.get(0);
			Hit hit = Hits.toHit(doc);
			hit.setContent((String) doc.getFieldValue(Fields.CONTENT.getName()));
			return hit;
		} catch (SolrServerException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.logicaldoc.core.searchengine.SearchEngine#search(java.lang.String,
	 * java.lang.String[], java.lang.String, java.lang.Integer)
	 */
	@Override
	public Hits search(String expression, String[] filters, String expressionLanguage, Integer rows) {
		WordDelimiterAnalyzer.lang.set(expressionLanguage);
		Hits hits = null;
		SolrQuery query = prepareSearchQuery(expression, filters, expressionLanguage, rows);

		try {
			log.info("Execute search: " + expression);
			QueryResponse rsp = server.query(query);
			hits = new Hits(rsp);
		} catch (SolrServerException e) {
			log.error(e.getMessage(), e);
		}
		return hits;
	}

	/**
	 * Prepares the query for a search.
	 */
	protected SolrQuery prepareSearchQuery(String expression, String[] filters, String expressionLanguage, Integer rows) {
		SolrQuery query = new SolrQuery();
		query.setQuery(expression);
		if (rows != null)
			query.setRows(rows);
		if (filters != null)
			query.addFilterQuery(filters);
		query.set("exprLang", expressionLanguage);
		return query;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.logicaldoc.core.searchengine.SearchEngine#close()
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.logicaldoc.core.searchengine.SearchEngine#unlock()
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.logicaldoc.core.searchengine.SearchEngine#isLocked()
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.logicaldoc.core.searchengine.SearchEngine#getCount()
	 */
	@Override
	public long getCount() {
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		try {
			QueryResponse rsp = server.query(query);
			SolrDocumentList docs = rsp.getResults();
			return docs.getNumFound();
		} catch (SolrServerException e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.logicaldoc.core.searchengine.SearchEngine#dropIndexes()
	 */
	@Override
	public void dropIndexes() {
		try {
			close();
			FileUtils.deleteDirectory(getIndexDataFolder());
			FileUtils.deleteDirectory(getSpellcheckerDataFolder());
			init();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
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
		File indexdir = new File(config.getPropertyWithSubstitutions("index.dir"));
		indexdir = new File(indexdir, "data");
		return new File(indexdir, "index");
	}

	static File getSpellcheckerDataFolder() throws IOException {
		File indexdir = new File(config.getPropertyWithSubstitutions("index.dir"));
		indexdir = new File(indexdir, "data");
		return new File(indexdir, "spellchecker");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.logicaldoc.core.searchengine.SearchEngine#init()
	 */
	@Override
	public void init() {
		try {
			File home = new File(config.getPropertyWithSubstitutions("index.dir"));
			File solr_xml = new File(home, "solr.xml");

			if (!home.exists()) {
				home.mkdirs();
				home.mkdir();
			}
			if (!solr_xml.exists()) {
				FileUtil.copyResource("/index/solr.xml", solr_xml);
			}

			File conf = new File(config.getPropertyWithSubstitutions("index.dir"));
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

			CoreContainer container = new CoreContainer(home.getPath(), solr_xml);
			server = new EmbeddedSolrServer(container, "");
			unlock();
		} catch (Exception e) {
			log.error("Unable to initialize the Full-text search engine", e);
		}
	}

	@Override
	public Object getServer() {
		return server;
	}
}