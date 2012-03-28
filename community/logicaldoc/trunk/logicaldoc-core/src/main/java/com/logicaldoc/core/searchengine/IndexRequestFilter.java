package com.logicaldoc.core.searchengine;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.servlet.SolrDispatchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.util.Context;

/**
 * This filter looks at the incoming URL maps them to handlers defined in
 * solrconfig.xml
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class IndexRequestFilter extends SolrDispatchFilter {
	final Logger log = LoggerFactory.getLogger(IndexRequestFilter.class);

	public IndexRequestFilter() {
		super();
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.pathPrefix = "/index";
	}

	@Override
	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		// Retrieve the cores from the SearchEngine
		try {
			Field field = EmbeddedSolrServer.class.getDeclaredField("coreContainer");
			field.setAccessible(true);
			SearchEngine engine = (SearchEngine) Context.getInstance().getBean(SearchEngine.class);
			cores = (CoreContainer) field.get(engine.server);
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}

		if (cores == null) {
			abortErrorMessage = "Search engine not already available";
		}

		// Setup the expression language
		String lang = request.getParameter("exprLang");
		if (lang != null)
			WordDelimiterAnalyzer.lang.set(lang);

		log.debug("Use expressionLanguage="+lang);
		
		// Call standard logic that will prepare the SolrRequest
		super.doFilter(request, response, chain);

	}
}