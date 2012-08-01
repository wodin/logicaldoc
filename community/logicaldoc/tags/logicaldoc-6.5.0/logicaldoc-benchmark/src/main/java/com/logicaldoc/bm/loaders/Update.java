package com.logicaldoc.bm.loaders;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.bm.AbstractLoader;
import com.logicaldoc.bm.ServerProxy;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.StringUtil;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.WSFolder;

/**
 * Loader thread that updates documents already stored in the database.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class Update extends AbstractLoader {

	private static Log log = LogFactory.getLog(Update.class);

	private static List<Long> folders = new ArrayList<Long>();

	private static List<String> tags = new ArrayList<String>();

	private long rootFolder = 4;

	private int depth = 5;

	private int tagSize = 4;

	private int tagsNumber = 4;

	public Update() {
		super(Update.class.getName().substring(Update.class.getName().lastIndexOf('.') + 1));

		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		rootFolder = Long.parseLong(config.getProperty("Update.rootFolder"));
		depth = config.getInt("Update.depth");
		tagSize = config.getInt("Update.tagsize");
		tagsNumber = config.getInt("Update.tags");
	}

	@Override
	protected String doLoading(ServerProxy serverProxy) throws Exception {
		synchronized (folders) {
			if (folders.isEmpty()) {
				prepareFolders(serverProxy, rootFolder, 1);
				log.info("Retrieved " + folders.size() + " folders");

				prepareTags();
				log.info("Prepared " + tags.size() + " tags");
			}
		}

		try {
			// Get a random folder
			long folderId = chooseFolder();

			// List all the documents
			WSDocument[] docs = serverProxy.documentClient.list(serverProxy.sid, folderId);
			if (docs != null)
				for (WSDocument doc : docs) {
					updateDocument(serverProxy, doc);
					statCount++;
				}
		} finally {
			// To compensate the internal increments
			statCount--;
		}
		return null;
	}

	private void updateDocument(ServerProxy serverProxy, WSDocument doc) throws Exception {
		/*
		 * Edit the title
		 */
		String prefix = doc.getTitle();
		if (prefix.contains("updated on")) {
			prefix = prefix.substring(0, prefix.indexOf("updated on"));
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		doc.setTitle(prefix + " updated on " + df.format(new Date()));

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

		doc.setComment("Updated by Loader");

		/*
		 * Request the update
		 */
		serverProxy.documentClient.update(serverProxy.sid, doc);
	}

	protected long chooseFolder() {
		int randomIndex = random.nextInt(folders.size());
		return folders.get(randomIndex);
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

	private void prepareFolders(ServerProxy serverProxy, long parent, int level) throws Exception {
		WSFolder[] ret = serverProxy.folderClient.listChildren(serverProxy.sid, parent);
		if (ret != null)
			for (WSFolder wsFolder : ret) {
				folders.add(wsFolder.getId());
				if (level < depth)
					prepareFolders(serverProxy, wsFolder.getId(), level + 1);
			}
	}
}