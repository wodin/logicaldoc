package com.logicaldoc.webservice.rest.endpoint;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webservice.model.WSFolder;
import com.logicaldoc.webservice.rest.FolderService;
import com.logicaldoc.webservice.soap.endpoint.SoapFolderService;

@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class RestFolderService extends SoapFolderService implements FolderService {

	private static Logger log = LoggerFactory.getLogger(RestFolderService.class);

	/* (non-Javadoc)
	 * @see com.logicaldoc.webservice.rest.endpoint.FolderService#create(java.util.List)
	 */
	@POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// The "folder" parameter comes in the POST request body (encoded as XML or
	// JSON).
	public WSFolder create(List<Attachment> atts) throws Exception {

		log.debug("create({})", atts);

		String sid = null;
		WSFolder folder = null;

		for (Attachment att : atts) {
			if ("sid".equals(att.getContentDisposition().getParameter("name"))) {
				sid = att.getObject(String.class);
			} else if ("folder".equals(att.getContentDisposition().getParameter("name"))) {
				log.debug("create({})", att.getContentType());
				log.debug("create({})", att.getContentDisposition());
				folder = att.getObject(WSFolder.class);
				log.debug("create({})", folder);
			}
		}

		return super.create(sid, folder);
	}

	/* (non-Javadoc)
	 * @see com.logicaldoc.webservice.rest.endpoint.FolderService#createSimpleForm(java.lang.String, java.lang.String)
	 */
	@POST
	@Path("/createSimple")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	// The "folderPath" parameter comes in the POST request body.
	public WSFolder createSimpleForm(@FormParam("sid") String sid, @FormParam("folderPath") String folderPath)
			throws Exception {
		log.debug("createSimpleForm()");
		WSFolder root = super.getRootFolder(sid);
		return super.createPath(sid, root.getId(), folderPath);
	}

	/* (non-Javadoc)
	 * @see com.logicaldoc.webservice.rest.endpoint.FolderService#createSimpleJSON(java.lang.String)
	 */
	@POST
	@Path("/createSimple")
	@Consumes({ MediaType.APPLICATION_JSON })
	// The "folderPath" parameter comes in the POST request body.
	public WSFolder createSimpleJSON(String jsonstr) throws Exception {
		log.debug("createSimpleJSON()");

		ObjectMapper mapper = new ObjectMapper();
		TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
		HashMap<String, String> hm = mapper.readValue(jsonstr, typeRef);

		String sid = hm.get("sid");
		String folderPath = hm.get("folderPath");

		WSFolder root = super.getRootFolder(sid);
		return super.createPath(sid, root.getId(), folderPath);
	}

	/* (non-Javadoc)
	 * @see com.logicaldoc.webservice.rest.endpoint.FolderService#createPath(java.lang.String, long, java.lang.String)
	 * The parameters come in the POST request body.
	 */
	@POST
	@Path("/createPath")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public WSFolder createPath(@FormParam("sid") String sid, @FormParam("parentId") long parentId,
			@FormParam("path") String path) throws Exception {

		return super.createPath(sid, parentId, path);
	}

	/* (non-Javadoc)
	 * @see com.logicaldoc.webservice.rest.endpoint.FolderService#createFolder(java.lang.String, long, java.lang.String)
	 */
	@POST
	@Path("/createFolder")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Override
	// The parameters come in the POST request body.
	public long createFolder(@FormParam("sid") String sid, @FormParam("parentId") long parentId,
			@FormParam("name") String name) throws Exception {
		return super.createFolder(sid, parentId, name);
	}

	/* (non-Javadoc)
	 * @see com.logicaldoc.webservice.rest.endpoint.FolderService#getFolder(java.lang.String, long)
	 */
	@GET
	@Path("/getFolder")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public WSFolder getFolder(@QueryParam("sid") String sid, @QueryParam("folderId") long folderId) throws Exception {
		return super.getFolder(sid, folderId);
	}

	/* (non-Javadoc)
	 * @see com.logicaldoc.webservice.rest.endpoint.FolderService#delete(java.lang.String, long)
	 */
	@DELETE
	@Path("/delete")
	public void delete(@QueryParam("sid") String sid, @QueryParam("folderId") long folderId) throws Exception {
		super.delete(sid, folderId);
	}

	/* (non-Javadoc)
	 * @see com.logicaldoc.webservice.rest.endpoint.FolderService#listChildren(java.lang.String, long)
	 */
	@GET
	@Path("/listChildren")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public WSFolder[] listChildren(@QueryParam("sid") String sid, @QueryParam("folderId") long folderId)
			throws Exception {
		log.debug("listChildren()");
		log.debug("sid: {}", sid);
		log.debug("folderId: {}", folderId);

		WSFolder[] fchildren = super.listChildren(sid, folderId);
		log.debug("fchildren: {}", (Object) fchildren);
		log.debug("fchildren.length: {}", fchildren.length);

		return fchildren;
	}

	/* (non-Javadoc)
	 * @see com.logicaldoc.webservice.rest.endpoint.FolderService#getPath(java.lang.String, long)
	 */
	@GET
	@Path("/getPath")
	public WSFolder[] getPath(@QueryParam("sid") String sid, @QueryParam("folderId") long folderId) throws Exception {
		return super.getPath(sid, folderId);
	}

	/* (non-Javadoc)
	 * @see com.logicaldoc.webservice.rest.endpoint.FolderService#getPathString(java.lang.String, long)
	 */
	@GET
	@Path("/getPathString")
	public String getPathString(@QueryParam("sid") String sid, @QueryParam("folderId") long folderId) throws Exception {
		WSFolder[] sss = this.getPath(sid, folderId);
		String pathString = "";
		for (WSFolder wsFolder : sss) {
			pathString += "/" + wsFolder.getName();
		}
		return pathString;
	}

}
