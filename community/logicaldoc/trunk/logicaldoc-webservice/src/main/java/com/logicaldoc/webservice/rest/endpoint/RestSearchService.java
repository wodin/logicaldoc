package com.logicaldoc.webservice.rest.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webservice.model.WSSearchOptions;
import com.logicaldoc.webservice.model.WSSearchResult;
import com.logicaldoc.webservice.rest.SearchService;
import com.logicaldoc.webservice.soap.endpoint.SoapSearchService;

@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class RestSearchService extends SoapSearchService implements SearchService {

	protected static Logger log = LoggerFactory.getLogger(RestSearchService.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.logicaldoc.webservice.rest.endpoint.SearchService#find(java.util.
	 * List)
	 */
	@POST
	@Path("/find")
	public WSSearchResult find(WSSearchOptions opt) throws Exception {
		log.debug("find()");
		String sid = validateSession();
		return super.find(sid, opt);
	}
}
