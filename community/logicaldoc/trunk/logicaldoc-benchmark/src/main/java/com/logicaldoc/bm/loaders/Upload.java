package com.logicaldoc.bm.loaders;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.bm.AbstractLoader;
import com.logicaldoc.bm.RandomFile;
import com.logicaldoc.bm.ServerProxy;
import com.logicaldoc.bm.cache.EhCacheAdapter;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.WSFolder;
import com.sun.mail.iap.ByteArray;

/**
 * Loader thread that puts documents to the remote repository.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 6.5
 */
public class Upload extends AbstractLoader {

	private static Log log = LogFactory.getLog(Upload.class);

	private String basePath;

	private static EhCacheAdapter<String, Long> pathCache;

	private long rootFolder = 4;

	private static RandomFile randomFile = new RandomFile();

	private int[] folderProfiles;

	protected long depth;

	static {
		System.setProperty(CacheManager.ENABLE_SHUTDOWN_HOOK_PROPERTY, "TRUE");
		URL url = Upload.class.getResource("/loader-cache.xml");
		CacheManager cacheManager = CacheManager.create(url);
		Cache cache = cacheManager.getCache("PathCache");

		pathCache = new EhCacheAdapter<String, Long>();
		pathCache.setCache(cache);
	}

	public Upload() {
		super(Upload.class.getName().substring(Upload.class.getName().lastIndexOf('.') + 1));

		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		rootFolder = Long.parseLong(config.getProperty("Upload.rootFolder"));
		randomFile.setSourceDir(config.getProperty("Upload.sourcedir"));
		depth = config.getInt("Upload.depth");

		StringTokenizer tokenizer = new StringTokenizer(config.getProperty("Upload.folderprofile"), ",", false);
		ArrayList<Integer> folderProfilesList = new ArrayList<Integer>(5);
		while (tokenizer.hasMoreTokens()) {
			String folderProfileStr = tokenizer.nextToken().trim();
			Integer folderProfile = Integer.valueOf(folderProfileStr);
			folderProfilesList.add(folderProfile);
		}
		folderProfiles = new int[folderProfilesList.size()];
		for (int i = 0; i < folderProfiles.length; i++) {
			folderProfiles[i] = folderProfilesList.get(i);
		}
		if (folderProfiles.length == 0 || folderProfiles[0] != 1) {
			throw new RuntimeException("'Upload.folderprofile' must always start with '1', "
					+ "which represents the root of the hierarchy, and have at least one other value.  "
					+ "E.g. '1, 3'");
		}

		log.error("folderProfilesStr.length(): " + folderProfilesList.size());
	}

	@Override
	protected String doLoading(ServerProxy serverProxy) throws Exception {

		// Get a random folder
		List<String> folderPath = chooseFolderPath();

		// Make sure the folder exists
//		Long folderID = makeFolders(serverProxy.sid, serverProxy, rootFolder, folderPath);
		Long folderID = makeFoldersFromPath(serverProxy.sid, serverProxy, rootFolder, folderPath);	
		
		Object[] buf = randomFile.getFile();
		File file = (File) buf[0];
		String title = formatter.format(loaderCount);

		Long docId = createDocument(serverProxy.sid, serverProxy, folderID, title, file, (ByteArray) buf[1]);
		if (docId == null) {
			throw new Exception("Error creating document: " + file.getName());
		}

		return null;
	}

	private Long createDocument(String ticket, ServerProxy serverProxy, long folderId, String title, File file,
			ByteArray content) {

		String fileName = file.getName();

		WSDocument doc = new WSDocument();
		doc.setFolderId(folderId);
		doc.setTitle(title);
		doc.setFileName(fileName);
		doc.setLanguage(session.getLanguage());
		try {
			if (content != null)
				doc = serverProxy.documentClient.create(ticket, doc,
						new DataHandler(new ByteArrayDataSource(content.getBytes(), "application/octet-stream")));
			else
				doc = serverProxy.documentClient.create(ticket, doc, file);

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

	protected List<String> chooseFolderPath() {
		// We work through these until we get the required depth.
		// The root node is ignored as it acts as the search root
		List<String> path = new ArrayList<String>((int) depth);
		for (int i = 1; i < depth; i++) {
			int folderProfile = folderProfiles[i];
			int randomFolderId = random.nextInt(folderProfile);
			String name = String.format("folder-%05d", randomFolderId);
			path.add(name);
		}
		return path;
	}

	/**
	 * Creates or find the folders based on caching.
	 */
	protected Long makeFolders(String ticket, ServerProxy serverProxy, Long rootFolder, List<String> folderPath)
			throws Exception {
		// Iterate down the path, checking the cache and populating it as
		// necessary
		Long currentParentFolderID = rootFolder;
		String currentKey = "";

		for (String aFolderPath : folderPath) {
			currentKey += ("/" + aFolderPath);
			// Is this there?
			Long folderID = pathCache.get(currentKey);
			if (folderID != null) {
				// Found it
				currentParentFolderID = folderID;
				// Step into the next level
				continue;
			}

			// It is not there, so create it
			try {
				WSFolder newFolder = new WSFolder();
				newFolder.setName(aFolderPath);
				newFolder.setParentId(currentParentFolderID);
				WSFolder folder = serverProxy.folderClient.create(ticket, newFolder);

				currentParentFolderID = folder.getId();
			} catch (Exception e) {
				currentParentFolderID = pathCache.get(currentKey);
			}

			// Cache the new node
			pathCache.put(currentKey, currentParentFolderID);
		}
		// Done
		return currentParentFolderID;
	}

	/**
	 * Creates or find the folders based on caching.
	 */
	protected Long makeFoldersFromPath(String ticket, ServerProxy serverProxy, Long rootFolder, List<String> folderPath)
			throws Exception {
		// Iterate down the path, checking the cache and populating it as
		// necessary
		String currentKey = getBasePath(serverProxy, rootFolder);
		for (String aFolderPath : folderPath) {
			currentKey += ("/" + aFolderPath);
		}
		// System.out.println("currentKey: " +currentKey);

		Long folderID = pathCache.get(currentKey);

		// It is not there, so create it
		if (folderID == null) {
			WSFolder folder = serverProxy.folderClient.createPath(ticket, rootFolder, currentKey);

			folderID = folder.getId();
			// Cache the new node
			pathCache.put(currentKey, folderID);
			// System.out.println("created path: " +currentKey);
		}

		return folderID;
	}

	private String getBasePath(ServerProxy serverProxy, Long rootFolder) {
		if (basePath == null) {
			try {
				String pathString = "";
				WSFolder[] folders = serverProxy.folderClient.getPath(serverProxy.sid, rootFolder);
				for (int i = 0; i < folders.length; i++) {
					pathString += "/" + folders[i].getName();
				}
				basePath = pathString;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return basePath;
	}
}