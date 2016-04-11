package com.logicaldoc.core.searchengine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CheckIndex.Status;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
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
import com.logicaldoc.util.StringUtil;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.io.FileUtil;

/**
 * Standard implementation that implements a local search engine
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class StandardSearchEngine implements SearchEngine {

	public static Version VERSION = Version.LUCENE_4_9;

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
		documentDao.initialize(document);
		Document doc = document;

		if (document.getDocRef() != null) {
			// This is an alias
			Document referencedDoc = documentDao.findById(document.getDocRef());
			documentDao.initialize(referencedDoc);
			doc = (Document) referencedDoc.clone();
			doc.setId(document.getId());
			doc.setTenantId(document.getTenantId());
			doc.setDocRef(document.getDocRef());
			doc.setDocRefType(document.getDocRefType());
			doc.setFolder(document.getFolder());
		}

		SolrInputDocument hit = new SolrInputDocument();

		hit.addField(Fields.ID.getName(), Long.toString(doc.getId()));
		hit.addField(Fields.TENANT_ID.getName(), Long.toString(doc.getTenantId()));
		hit.addField(Fields.LANGUAGE.getName(), doc.getLanguage());
		hit.addField(Fields.TITLE.getName(), doc.getTitle());
		hit.addField(Fields.SIZE.getName(), doc.getFileSize());
		hit.addField(Fields.DATE.getName(), doc.getDate());
		hit.addField(Fields.SOURCE_DATE.getName(), doc.getSourceDate());
		hit.addField(Fields.SOURCE_ID.getName(), doc.getSourceId());
		hit.addField(Fields.RECIPIENT.getName(), doc.getRecipient());
		hit.addField(Fields.CREATION.getName(), doc.getCreation());
		hit.addField(Fields.CUSTOM_ID.getName(), doc.getCustomId());
		hit.addField(Fields.SOURCE.getName(), doc.getSource());
		hit.addField(Fields.COMMENT.getName(), doc.getComment());
		hit.addField(Fields.TAGS.getName(), doc.getTagsString());
		hit.addField(Fields.COVERAGE.getName(), doc.getCoverage());
		hit.addField(Fields.SOURCE_AUTHOR.getName(), doc.getSourceAuthor());
		hit.addField(Fields.SOURCE_TYPE.getName(), doc.getSourceType());
		hit.addField(Fields.DOC_REF.getName(), doc.getDocRef());

		int maxText = -1;
		if (StringUtils.isNotEmpty(config.getProperty("index.maxtext"))) {
			try {
				maxText = config.getInt("index.maxtext");
			} catch (Exception e) {
			}
		}

		String utf8Content = StringUtil.removeNonUtf8Chars(content);
		if (maxText > 0 && utf8Content.length() > maxText)
			hit.addField(Fields.CONTENT.getName(), StringUtils.substring(utf8Content, 0, maxText));
		else
			hit.addField(Fields.CONTENT.getName(), utf8Content);

		if (doc.getFolder() != null) {
			hit.addField(Fields.FOLDER_ID.getName(), doc.getFolder().getId());
			hit.addField(Fields.FOLDER_NAME.getName(), doc.getFolder().getName());
		}

		if (doc.getTemplateId() != null) {
			hit.addField(Fields.TEMPLATE_ID.getName(), doc.getTemplateId());

			for (String attribute : doc.getAttributeNames()) {
				ExtendedAttribute ext = doc.getExtendedAttribute(attribute);
				// Skip all non-string attributes
				if ((ext.getType() == ExtendedAttribute.TYPE_STRING || ext.getType() == ExtendedAttribute.TYPE_USER)
						&& StringUtils.isNotEmpty(ext.getStringValue())) {

					// Prefix all extended attributes with 'ext_' in order to
					// avoid collisions with standard fields
					hit.addField("ext_" + attribute, ext.getStringValue());
				}
			}
		}

		try {
			MultilanguageAnalyzer.lang.set(doc.getLanguage());
			server.add(hit);
			server.commit();
		} finally {
			MultilanguageAnalyzer.lang.remove();
		}
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
		if (doc.getDocRef() != null)
			doc = documentDao.findById(doc.getDocRef());

		Locale locale = doc.getLocale();
		if (locale == null)
			locale = Locale.ENGLISH;
		Parser parser = ParserFactory.getParser(content, doc.getFileName(), locale, null, doc.getTenantId());
		if (parser == null)
			return;

		String contentString = parser.getContent();

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
		for (Long id : ids)
			deleteHit(id);
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
		} catch (Throwable e) {
			log.error(e.getMessage());
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
		try {
			// This configures the analyzer to use to to parse the expression of
			// the
			// content field
			MultilanguageAnalyzer.lang.set(expressionLanguage);
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
		} finally {
			MultilanguageAnalyzer.lang.remove();
		}
	}

	/**
	 * Prepares the query for a search.
	 */
	protected SolrQuery prepareSearchQuery(String expression, String[] filters, String expressionLanguage, Integer rows) {
		SolrQuery query = new SolrQuery();
		query.setQuery(expression);
		query.setSort(SortClause.desc("score"));
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
		try {
			server.commit();
			unlock();
			server.getCoreContainer().shutdown();
			server.shutdown();
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
			if (SolrIndexWriter.isLocked(directory))
				SolrIndexWriter.unlock(directory);

			try {
				directory.deleteFile("write.lock");
			} catch (Throwable t) {
				log.warn("Unable to delete the index lock, this may be normal");
			}
		} catch (Throwable e) {
			log.warn("unlock " + e.getMessage());
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
			if (SolrIndexWriter.isLocked(directory))
				result = true;
		} catch (Exception e) {
			log.warn("isLocked " + e.getMessage(), e);
		}
		return result;
	}

	/**
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

	/**
	 * @see com.logicaldoc.core.searchengine.SearchEngine#dropIndex()
	 */
	@Override
	public void dropIndex() {
		try {
			server.deleteByQuery("*:*");
			server.optimize();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

	static Directory getIndexDataDirectory() throws IOException {
		return new NIOFSDirectory(getIndexDataFolder());
	}

	static File getIndexDataFolder() throws IOException {
		File indexdir = new File(config.getPropertyWithSubstitutions("index.dir"));
		indexdir = new File(indexdir, "logicaldoc");
		indexdir = new File(indexdir, "data");
		return new File(indexdir, "index");
	}

	/**
	 * @see com.logicaldoc.core.searchengine.SearchEngine#init()
	 */
	@Override
	public void init() {
		try {
			File indexHome = new File(config.getPropertyWithSubstitutions("index.dir"));
			File solr_xml = new File(indexHome, "solr.xml");

			if (!indexHome.exists()) {
				indexHome.mkdirs();
				indexHome.mkdir();
			}
			if (!solr_xml.exists()) {
				FileUtil.copyResource("/index/solr.xml", solr_xml);
			}

			File ldoc = new File(config.getPropertyWithSubstitutions("index.dir"));
			ldoc = new File(ldoc, "logicaldoc");
			if (!ldoc.exists()) {
				ldoc.mkdirs();
				ldoc.mkdir();
			}
			File core_prop = new File(ldoc, "core.properties");
			if (!core_prop.exists())
				FileUtil.copyResource("/index/logicaldoc/core.properties", core_prop);

			File conf = new File(ldoc, "conf");
			if (!conf.exists()) {
				conf.mkdirs();
				conf.mkdir();
			}
			File solrconfig_xml = new File(conf, "solrconfig.xml");
			if (!solrconfig_xml.exists()) {
				FileUtil.copyResource("/index/logicaldoc/conf/solrconfig.xml", solrconfig_xml);
			}
			File schema_xml = new File(conf, "schema.xml");
			if (!schema_xml.exists()) {
				FileUtil.copyResource("/index/logicaldoc/conf/schema.xml", schema_xml);
			}
			File synonyms_txt = new File(conf, "synonyms.txt");
			if (!synonyms_txt.exists()) {
				FileUtil.copyResource("/index/logicaldoc/conf/synonyms.txt", synonyms_txt);
			}
			File protwords_txt = new File(conf, "protwords.txt");
			if (!protwords_txt.exists()) {
				FileUtil.copyResource("/index/logicaldoc/conf/protwords.txt", protwords_txt);
			}

			CoreContainer container = new CoreContainer(indexHome.getPath());
			server = new EmbeddedSolrServer(container, "logicaldoc");
			container.load();

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