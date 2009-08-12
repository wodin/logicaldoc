package com.logicaldoc.core.transfer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
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
		startFolderId = Menu.MENUID_DOCUMENTS;
	}

	/**
	 * Exports the specified folder content
	 * 
	 * @param folderId Identifier of the folder
	 * @param userId Current user
	 * @return The Stream of the zip archive
	 */
	public ByteArrayOutputStream process(long folderId, long userId) {
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu folder = menuDao.findById(folderId);
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
	 * If allLevel set true all children of a specified menu will be export.
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
	protected void appendChildren(Menu folder, int level) {
		if (!allLevel && (level > 1)) {
			return;
		} else {
			addFolderDocuments(folder);
			MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			Collection<Menu> children = menuDao.findByUserId(userId, folder.getId(), Menu.MENUTYPE_DIRECTORY);
			Iterator<Menu> iter = children.iterator();

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
	protected void addFolderDocuments(Menu folder) {
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		Collection<Document> docs = ddao.findByFolder(folder.getId());

		for (Document document : docs) {
			File documentFile = manager.getDocumentFile(document);
			InputStream is = null;
			BufferedInputStream bis = null;
			try {
				is = new FileInputStream(documentFile);
				bis = new BufferedInputStream(is);

				ZipEntry entry = new ZipEntry(getZipEntryPath(folder) + document.getFileName());
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
	}

	/**
	 * Computes the correct entry path inside the zip archive
	 * 
	 * @param folder The folder of the document to be inserted
	 * @return The full path
	 */
	private String getZipEntryPath(Menu folder) {
		if (folder.getId() == Menu.MENUID_DOCUMENTS)
			return "";
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		List<Menu> menus = menuDao.findParents(folder.getId());
		menus.add(folder);
		Collections.reverse(menus);

		List<String> folderNames = new ArrayList<String>();
		for (int i = 0; i < menus.size(); i++) {
			Menu menu = menus.get(i);
			if (menu.getId() == startFolderId)
				break;
			folderNames.add(menu.getText());
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
