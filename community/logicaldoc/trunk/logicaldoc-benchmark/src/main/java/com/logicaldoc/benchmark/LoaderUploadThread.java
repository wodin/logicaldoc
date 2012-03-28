package com.logicaldoc.benchmark;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.WSFolder;

public class LoaderUploadThread extends AbstractLoaderThread {

	private static Log log = LogFactory.getLog(LoaderUploadThread.class);
	
	public LoaderUploadThread(LoaderSession session, String loaderName, long testTotal) {
		super(session, loaderName, testTotal);
	}

	@Override
	protected String doLoading(LoaderServerProxy serverProxy, long rootFolder) throws Exception {
		
//	       // Get a random folder
//        List<String> folderPath = super.chooseFolderPath();
//        
//        // Make sure the folder exists
//        NodeRef folderNodeRef = makeFolders(serverProxy.ticket, serverProxy, workingRootNodeRef, folderPath);
//        
//        // Upload it
//        FileInfo[] fileInfos = serverProxy.loaderRemote.uploadContent(
//                serverProxy.ticket,
//                folderNodeRef,
//                filenames,
//                bytes);
//        
//        // Done
//        String msg = String.format("Uploaded %d files to folder: %s", fileInfos.length, folderPath.toString());
//        return msg;
        
		// Get a random folder
        Long folder = getRandomFolder(serverProxy.ticket, serverProxy, rootFolder);
		if (folder == null) {
			throw new Exception("Error getting folder");
		}

		
		File file = super.getFile();
		String title = formatter.format(testCount);

		Long docId = createDocument(serverProxy.ticket, serverProxy, folder, title, file);
		if (docId == null) {
			throw new Exception("Error creating document: " + file.getName());
		}        
		
        // Done
//        String msg = String.format("Uploaded %d files to folder: %s", fileInfos.length, folderPath.toString());
//        return msg;
		 return "Done";
	}

	/**
	 * Gets a random path and retrieves the respective folder ID. If the folder
	 * doesn't exists it is created and the ID cached.
	 */
	private Long getRandomFolder(String ticket, LoaderServerProxy serverProxy, long rootFolder) {
						
		int index = random.nextInt(session.getPaths().size());
		String path = session.getPaths().get(index);

		Long folder = session.getFolderIds(index);
		if (folder == null) {
			try {
				folder = createFolder(ticket, serverProxy, rootFolder, path);
				//folderIds[index] = folder;
				session.setFolderIds(index, folder);
			} catch (Throwable ex) {
				log.error(ex.getMessage(), ex);
				folder = null;
			}
		}
		return folder;
	}		
	

	private Long createFolder(String ticket, LoaderServerProxy serverProxy, long rootFolder, String path) {
		WSFolder folder = null;
		try {							
			if (ticket == null)
				return null;
			//folder = folderClient.createPath(sid, rootFolder, path);
			folder = serverProxy.folderClient.createPath(ticket, rootFolder, path);
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
	
	
	private Long createDocument(String ticket, LoaderServerProxy serverProxy, long folderId, String title, File file) {
		
		String fileName = file.getName();

		WSDocument doc = new WSDocument();
		doc.setFolderId(folderId);
		doc.setTitle(title);
		doc.setFileName(fileName);
		doc.setLanguage(session.getLanguage());
		try {
			//doc = documentClient.create(sid, doc, file);			
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

}
