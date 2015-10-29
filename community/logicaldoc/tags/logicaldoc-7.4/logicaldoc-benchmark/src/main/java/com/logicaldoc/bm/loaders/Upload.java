package com.logicaldoc.bm.loaders;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.bm.AbstractLoader;
import com.logicaldoc.bm.RandomFile;
import com.logicaldoc.bm.ServerProxy;
import com.logicaldoc.bm.SourceFile;
import com.logicaldoc.bm.cache.EhCacheAdapter;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.StringUtil;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.WSFolder;

/**
 * Loader thread that puts documents to the remote repository.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 6.5
 */
public class Upload extends AbstractLoader {

	private static Log log = LogFactory.getLog(Upload.class);

	private static EhCacheAdapter<String, Long> pathCache;

	private long rootFolder = 4;

	private static RandomFile randomFile = new RandomFile();

	private int[] folderProfiles;

	protected long depth;

	private static List<String> tags = new ArrayList<String>();

	private int tagSize = 4;

	private int tagsNumber = 4;

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
		tagSize = config.getInt("Upload.tagsize");
		tagsNumber = config.getInt("Upload.tags");

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
		synchronized (tags) {
			if (tags.isEmpty()) {
				prepareTags();
				log.info("Prepared " + tags.size() + " tags");
			}
		}
		// Get a random folder
		List<String> folderPath = chooseFolderPath();

		// Make sure the folder exists
		log.info("Create the folders");
		Long folderID = makeFolders(serverProxy.sid, serverProxy, rootFolder, folderPath);
		// Long folderID = makeFoldersFromPath(serverProxy.sid, serverProxy,
		// rootFolder, folderPath);

		SourceFile sourceFile = randomFile.getSourceFile();
		String title = formatter.format(loaderCount);

		Long docId = createDocument(serverProxy.sid, serverProxy, folderID, title, sourceFile);
		if (docId == null) {
			throw new Exception("Error creating document: " + sourceFile.getFile().getName());
		}

		return null;
	}

	private Long createDocument(String ticket, ServerProxy serverProxy, long folderId, String title, SourceFile sfile) {

		String fileName = sfile.getFile().getName();

		WSDocument doc = new WSDocument();
		doc.setFolderId(folderId);
		doc.setTitle(title);
		doc.setFileName(fileName);
		doc.setLanguage(session.getLanguage());

		/*
		 * Add the tags
		 */
		if (doc.getTags() == null || doc.getTags().length < tagsNumber) {
			List<String> tgs = new ArrayList<String>();
			for (int i = 0; i < doc.getTags().length; i++)
				tgs.add(doc.getTags()[i]);
			while (tgs.size() < tagsNumber) {
				String tag = chooseTag();
				if (!tgs.contains(tag))
					tgs.add(tag);
			}
			doc.setTags(tgs.toArray(new String[0]));
		}

		try {
			if (sfile.getContent() != null)
				doc = serverProxy.documentClient.create(ticket, doc, new DataHandler(new ByteArrayDataSource(sfile
						.getContent().getBytes(), "application/octet-stream")));
			else
				doc = serverProxy.documentClient.create(ticket, doc, sfile.getFile());

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
			// System.out.printf("putting in cache: %s, %d %n", currentKey,
			// currentParentFolderID);
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
		String currentKey = "";
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

	protected String chooseTag() {
		int randomIndex = random.nextInt(tags.size());
		return tags.get(randomIndex);
	}

	private void prepareTags() throws IOException {
		tags.clear();

		String buf = StringUtil.writeToString(this.getClass().getResourceAsStream("/tags.txt"), "UTF-8");
		StringTokenizer st = new StringTokenizer(buf, " \\\t\n\r\f\"'.;,()[]:/", false);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (StringUtils.isNotEmpty(token) && token.length() > tagSize)
				tags.add(token);
		}
	}
}