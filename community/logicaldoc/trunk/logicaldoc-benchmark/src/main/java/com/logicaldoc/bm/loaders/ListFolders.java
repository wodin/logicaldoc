package com.logicaldoc.bm.loaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.bm.AbstractLoader;
import com.logicaldoc.bm.AbstractServerProxy;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.webservice.model.WSFolder;

/**
 * A loader thread that retrieves the folders beneath each directory from the
 * root. This is an expensive process but should reach a stable execution time
 * once the folders in the profile have all been created.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @author Alessandro Gasparini - LogicalDOC
 * @since 6.5
 */
public class ListFolders extends AbstractLoader {
	
	private static Logger log = LoggerFactory.getLogger(ListFolders.class);

	private String messageRecord;

	private int totalFolders;

	private long rootFolder = 4;

	public ListFolders() {
		super(ListFolders.class.getName().substring(ListFolders.class.getName().lastIndexOf('.') + 1));
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		rootFolder = Long.parseLong(config.getProperty("ListFolders.rootFolder"));
	}

	/**
	 * Go to a directory and get a listing of the folders beneath it.
	 */
	@Override
	protected String doLoading(AbstractServerProxy serverProxy) throws Exception {
		totalFolders = 0;
		listFoldersRecursive(serverProxy, rootFolder);

		// Done
		String msg = String.format("Found %s folders", Long.toString(totalFolders));
		this.messageRecord = msg;
		return msg;
	}

	@Override
	public String getSummary() {
		return super.getSummary() + messageRecord;
	}

	/**
	 * Recursive method to list all folders in the hierarchy.
	 */
	private void listFoldersRecursive(AbstractServerProxy serverProxy, long parentFolder) {

		WSFolder[] folders = new WSFolder[0];
		try {
			folders = serverProxy.listChildren(serverProxy.sid, parentFolder);
		} catch (Exception e) {
			log.warn("listFoldersRecursive(): ", e);
		}

		if (folders != null) {
			totalFolders += folders.length;
			for (WSFolder info : folders) {
				listFoldersRecursive(serverProxy, info.getId());
			}
		}
	}
}
