package com.logicaldoc.core.transfer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.util.Context;

/**
 * Exports a folder hierarchy and all documents in it as a zip file.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @author Matteo Caruso - Logical Objects
 */
public class ZipExport {

	protected static Log log = LogFactory.getLog(ZipExport.class);

	private ZipOutputStream zos;

	private long userId;

	private boolean allLevel;

	private long startFolderId;

	public ZipExport() {
		zos = null;
		userId = -1;
		allLevel = false;
		startFolderId = Folder.ROOTID;
	}

	/**
	 * Exports the specified folder content
	 * 
	 * @param folderId Identifier of the folder
	 * @param userId Current user
	 * @return The Stream of the zip archive
	 */
	public ByteArrayOutputStream process(long folderId, long userId) {
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder folder = folderDao.findById(folderId);
		this.userId = userId;
		this.startFolderId = folder.getId();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		zos = new ZipOutputStream(bos);
		zos.setEncoding("Cp850");
		try {
			appendChildren(folder, 0);
		} finally {
			try {
				zos.flush();
				zos.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}

		}
		return bos;
	}

	/**
	 * Exports a selection of documents
	 * 
	 * @param docIds Identifiers of the documents
	 * @return The Stream of the zip archive
	 */
	public void process(long[] docIds, OutputStream out) {
		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		zos = new ZipOutputStream(out);
		zos.setEncoding("Cp850");
		try {
			for (long id : docIds) {
				Document doc = dao.findById(id);
				addDocument("", doc);
			}
		} finally {
			try {
				zos.flush();
				zos.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * If allLevel set true all children of a specified folder will be export.
	 * Otherwise only the first level will be export.
	 * 
	 * @param b
	 */
	public void setAllLevel(boolean b) {
		allLevel = b;
	}

	/**
	 * Adds all children of the specified folder up to the given level
	 * 
	 * @param folder
	 * @param level
	 */
	protected void appendChildren(Folder folder, int level) {
		if (!allLevel && (level > 1)) {
			return;
		} else {
			addFolderDocuments(folder);
			FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			Collection<Folder> children = folderDao.findByUserId(userId, folder.getId());
			Iterator<Folder> iter = children.iterator();

			while (iter.hasNext()) {
				appendChildren(iter.next(), level + 1);
			}
		}
	}

	/**
	 * Adds all folder's documents
	 * 
	 * @param folder
	 */
	protected void addFolderDocuments(Folder folder) {
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Collection<Document> docs = ddao.findByFolder(folder.getId(), null);

		for (Document document : docs) {
			addDocument(getZipEntryPath(folder), document);
		}
	}

	/**
	 * Adds a single document into the archive in the specified path.
	 */
	private void addDocument(String path, Document document) {
		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		File documentFile = manager.getDocumentFile(document);
		InputStream is = null;
		BufferedInputStream bis = null;
		try {
			is = new FileInputStream(documentFile);
			bis = new BufferedInputStream(is);

			ZipEntry entry = new ZipEntry(document.getFileName());
			zos.putNextEntry(entry);

			// Transfer bytes from the file to the ZIP file
			int len;
			while ((len = bis.read()) != -1) {
				zos.write(len);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Computes the correct entry path inside the zip archive
	 * 
	 * @param folder The folder of the document to be inserted
	 * @return The full path
	 */
	private String getZipEntryPath(Folder folder) {
		if (folder.getId() == Folder.ROOTID)
			return "";
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		List<Folder> folders = folderDao.findParents(folder.getId());
		folders.add(folder);
		Collections.reverse(folders);

		List<String> folderNames = new ArrayList<String>();
		for (int i = 0; i < folders.size(); i++) {
			Folder f = folders.get(i);
			if (f.getId() == startFolderId)
				break;
			folderNames.add(f.getName());
		}
		Collections.reverse(folderNames);

		StringBuffer path = new StringBuffer("");
		for (String name : folderNames) {
			path.append(name);
			path.append("/");
		}

		return path.toString();
	}
}
