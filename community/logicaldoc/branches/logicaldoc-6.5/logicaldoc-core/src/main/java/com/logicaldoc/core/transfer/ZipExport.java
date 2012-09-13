package com.logicaldoc.core.transfer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream.UnicodeExtraFieldPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.util.Context;

/**
 * Exports a folder hierarchy and all documents in it as a zip file. Can also be
 * used to export a selection of documents
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @author Matteo Caruso - Logical Objects
 */
public class ZipExport {

	protected static Logger log = LoggerFactory.getLogger(ZipExport.class);

	private ZipArchiveOutputStream zos;

	private long userId;

	private boolean allLevel;

	private long startFolderId;

	public ZipExport() {
		zos = null;
		userId = -1;
		allLevel = false;
		startFolderId = Folder.DEFAULTWORKSPACE;
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
		zos = new ZipArchiveOutputStream(bos);
		zos.setMethod(ZipEntry.DEFLATED);
		zos.setEncoding("UTF-8");
		zos.setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy.ALWAYS);
		zos.setUseLanguageEncodingFlag(true);

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
	public ByteArrayOutputStream process(long[] docIds) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		process(docIds, bos);
		return bos;
	}

	/**
	 * Exports a selection of documents
	 * 
	 * @param docIds Identifiers of the documents
	 * @param out The stream that will receive the zip
	 */
	public void process(long[] docIds, OutputStream out) {
		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		zos = new ZipArchiveOutputStream(out);
		zos.setEncoding("UTF-8");
		zos.setMethod(ZipEntry.DEFLATED);
		zos.setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy.ALWAYS);
		zos.setUseLanguageEncodingFlag(true);

		try {
			for (long id : docIds) {
				Document doc = dao.findById(id);
				if (doc.getDocRef() != null)
					doc = dao.findById(doc.getDocRef());
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
			Document doc = document;
			if (doc.getDocRef() != null)
				doc = ddao.findById(doc.getDocRef());
			addDocument(getZipEntryPath(folder), doc);
		}
	}

	/**
	 * Adds a single document into the archive in the specified path.
	 */
	private void addDocument(String path, Document document) {
		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);
		InputStream is = null;
		BufferedInputStream bis = null;
		String resource = storer.getResourceName(document, null, null);
		try {
			is = storer.getStream(document.getId(), resource);
			bis = new BufferedInputStream(is);

			ZipEntry entry = new ZipEntry(path + document.getFileName());
			entry.setMethod(ZipEntry.DEFLATED);
			zos.putArchiveEntry(new ZipArchiveEntry(entry));

			// Transfer bytes from the file to the ZIP file
			int len;
			while ((len = bis.read()) != -1) {
				zos.write(len);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			try {
				zos.closeArchiveEntry();
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
