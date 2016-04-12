package com.logicaldoc.webservice.rest.endpoint;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webservice.model.WSSearchOptions;
import com.logicaldoc.webservice.model.WSSearchResult;
import com.logicaldoc.webservice.soap.endpoint.SoapSearchService;

@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class RestSearchService extends SoapSearchService {

	protected static Logger log = LoggerFactory.getLogger(RestSearchService.class);

	@POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public WSSearchResult find(List<Attachment> atts) throws Exception {
		
		log.debug("find({})", atts);

		String sid = null;
		WSSearchOptions opt = null;

		for (Attachment att : atts) {
			if ("sid".equals(att.getContentDisposition().getParameter("name"))) {
				sid = att.getObject(String.class);
			} else if ("opt".equals(att.getContentDisposition().getParameter("name"))) {
				log.debug("find({})", att.getContentType());
				log.debug("find({})", att.getContentDisposition());
				opt = att.getObject(WSSearchOptions.class);
				log.debug("find({})", opt);
			}
		}		
		
		return super.find(sid, opt);
	}
}
