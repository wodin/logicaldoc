package com.logicaldoc.benchmark;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.FolderClient;
import com.logicaldoc.webservice.folder.WSFolder;

/**
 * Loads documents by using the web services.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.4
 */
public class WsLoader extends AbstractLoader {

	private static Log log = LogFactory.getLog(WsLoader.class);

	private AuthClient auth;

	private DocumentClient documentClient;

	private FolderClient folderClient;

	/**
	 * Initializes resources and connects to the WebService
	 */
	protected void init() {
		super.init();

		log.info("Connect to the server");
		try {
			auth = new AuthClient(url + "/services/Auth");
			documentClient = new DocumentClient(url + "/services/Document");
			folderClient = new FolderClient(url + "/services/Folder");
			log.info("Connection established");

			sid = auth.login(username, password);
			log.info("Created SID: " + sid);
		} catch (Throwable e) {
			log.error("Unable to initialize WebServices connection", e);
		}
	}

	@Override
	protected Long createFolder(String path) {
		WSFolder folder = null;
		try {
			if (sid == null)
				return null;
			folder = folderClient.createPath(sid, rootFolder, path);
			if (folder != null)
				log.debug("Created path " + path);
		} catch (Throwable ex) {
			log.error(ex.getMessage(), ex);
		}
		if (folder == null)
			return null;
		else
			return folder.getId();
	}

	@Override
	protected Long createDocument(long folderId, String title, File file) {
		String fileName = title + "." + FilenameUtils.getExtension(file.getName());

		WSDocument doc = new WSDocument();
		doc.setFolderId(folderId);
		doc.setTitle(title);
		doc.setFileName(fileName);
		doc.setLanguage("en");
		try {
			doc = documentClient.create(sid, doc, file);
			if (doc != null)
				log.debug("Created document " + fileName);
		} catch (Throwable ex) {
			log.error(ex.getMessage(), ex);
		}
		if (doc == null)
			return null;
		else
			return doc.getId();
	}

}