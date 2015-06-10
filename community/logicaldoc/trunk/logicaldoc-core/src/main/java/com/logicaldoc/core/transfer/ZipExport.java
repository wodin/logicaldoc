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
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentEvent;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.pdf.PdfConverterManager;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderEvent;
import com.logicaldoc.core.security.FolderHistory;
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
		startFolderId = Folder.DEFAULTWORKSPACEID;
	}

	/**
	 * Exports the specified folder content
	 * 
	 * @param transaction Transaction with all informations about the export
	 * @param pdfConversion True if the pdf conversion has to be used instead of
	 *        the original files
	 * @return The Stream of the zip archive
	 */
	public ByteArrayOutputStream process(FolderHistory transaction, boolean pdfConversion) {
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder folder = folderDao.findById(transaction.getFolderId());
		this.userId = transaction.getUserId();
		this.startFolderId = folder.getId();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		zos = new ZipArchiveOutputStream(bos);
		zos.setMethod(ZipEntry.DEFLATED);
		zos.setEncoding("UTF-8");
		zos.setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy.ALWAYS);
		zos.setUseLanguageEncodingFlag(true);

		try {
			appendChildren(folder, 0, pdfConversion);
		} finally {
			try {
				zos.flush();
				zos.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}

		}

		/*
		 * Record the export event
		 */
		transaction.setEvent(FolderEvent.EXPORTED.toString());
		folderDao.saveFolderHistory(folder, transaction);

		return bos;
	}

	/**
	 * Exports a selection of documents
	 * 
	 * @param docIds Identifiers of the documents
	 * @return The Stream of the zip archive
	 * @param pdfConversion True if the pdf conversion has to be used instead of
	 *        the original files
	 */
	public ByteArrayOutputStream process(long[] docIds, boolean pdfConversion, History transaction) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		process(docIds, bos, pdfConversion, transaction);
		return bos;
	}

	/**
	 * Exports a selection of documents
	 * 
	 * @param docIds Identifiers of the documents
	 * @param out The stream that will receive the zip
	 * @param pdfConversion True if the pdf conversion has to be used instead of
	 *        the original files
	 */
	public void process(long[] docIds, OutputStream out, boolean pdfConversion, History transaction) {
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		zos = new ZipArchiveOutputStream(out);
		zos.setEncoding("UTF-8");
		zos.setMethod(ZipEntry.DEFLATED);
		zos.setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy.ALWAYS);
		zos.setUseLanguageEncodingFlag(true);

		try {
			for (long id : docIds) {
				Document doc = ddao.findById(id);
				boolean convertToPdf = pdfConversion;
				if (doc.getDocRef() != null) {
					// This is an alias, retrieve the real document
					doc = ddao.findById(doc.getDocRef());
					if ("pdf".equals(doc.getDocRefType()))
						convertToPdf = true;
				}
				addDocument("", doc, convertToPdf);

				if (transaction != null) {
					try {
						History t = (History) transaction.clone();
						transaction.setEvent(DocumentEvent.DOWNLOADED.toString());
						ddao.saveDocumentHistory(doc, t);
					} catch (CloneNotSupportedException e) {
					}
				}
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
	 */
	protected void appendChildren(Folder folder, int level, boolean pdfConversion) {
		if (!allLevel && (level > 1)) {
			return;
		} else {
			addFolderDocuments(folder, pdfConversion);
			FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			Collection<Folder> children = folderDao.findByUserId(userId, folder.getId());
			Iterator<Folder> iter = children.iterator();

			while (iter.hasNext()) {
				appendChildren(iter.next(), level + 1, pdfConversion);
			}
		}
	}

	/**
	 * Adds all folder's documents
	 */
	protected void addFolderDocuments(Folder folder, boolean pdfConversion) {
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Collection<Document> docs = ddao.findByFolder(folder.getId(), null);

		for (Document document : docs) {
			Document doc = document;
			boolean convertToPdf = pdfConversion;
			if (doc.getDocRef() != null) {
				// This is an alias, retrieve the real document
				doc = ddao.findById(doc.getDocRef());
				if ("pdf".equals(doc.getDocRefType()))
					convertToPdf = true;
			}

			addDocument(getZipEntryPath(folder), doc, convertToPdf);
		}
	}

	/**
	 * Adds a single document into the archive in the specified path.
	 */
	private void addDocument(String path, Document document, boolean pdfConversion) {
		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);
		String resource = storer.getResourceName(document, null, null);

		if (pdfConversion && !"pdf".equals(FilenameUtils.getExtension(document.getFileName().toLowerCase()))) {
			PdfConverterManager manager = (PdfConverterManager) Context.getInstance()
					.getBean(PdfConverterManager.class);
			try {
				manager.createPdf(document);
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
				return;
			}
			resource = storer.getResourceName(document, null, PdfConverterManager.SUFFIX);
		}

		InputStream is = null;
		BufferedInputStream bis = null;
		try {
			is = storer.getStream(document.getId(), resource);
			bis = new BufferedInputStream(is);

			String fileName = document.getFileName();
			if (pdfConversion)
				fileName = FilenameUtils.getBaseName(fileName) + ".pdf";

			ZipEntry entry = new ZipEntry(path + fileName);
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
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

		long rootId = folderDao.findRoot(folder.getTenantId()).getId();
		if (folder.getId() == rootId)
			return "";

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
